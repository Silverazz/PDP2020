package com.example.pdpproject.apiManager;

import com.example.pdpproject.Config;
import com.example.pdpproject.apiManager.requests.SpotifyRequestAsyn;
import com.example.pdpproject.factories.AlbumFactory;
import com.example.pdpproject.factories.ArtistFactory;
import com.example.pdpproject.factories.PlaylistFactory;
import com.example.pdpproject.factories.TrackFactory;
import com.example.pdpproject.factories.UserFactory;
import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.User;
import com.example.pdpproject.models.albums.AlbumSpotify;
import com.example.pdpproject.models.artists.ArtistSpotify;
import com.example.pdpproject.models.playlists.PlaylistSpotify;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.models.users.UserSpotify;
import com.example.pdpproject.repo.Singleton;
import com.example.pdpproject.service.ServiceSpotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;


public class APIManagerSpotifyAsyn implements APIManagerItf {

    private String UserSpotId;
    private final int limitFollowedArtist = 20;


    private User user;
    private ArrayList<Playlist> playlists;
    private Set<String> artistTobeCreate;
    private HashMap<String, JSONObject> albumTobeCreate;
    private CopyOnWriteArrayList<String> genresTobeAdd;
    //Singleton
    private Singleton singleton = Singleton.getInstance();
    private SpotifyRequestAsyn spotifyRequestAsyn;


    public APIManagerSpotifyAsyn() {
        artistTobeCreate = new HashSet<>();
        albumTobeCreate = new HashMap<>();
        genresTobeAdd = new CopyOnWriteArrayList<>();
        spotifyRequestAsyn = new SpotifyRequestAsyn();
    }

    @Override
    public void setRequestToken(String token) {
        spotifyRequestAsyn.setToken(token);
    }

    @Override
    public void exportModels() {
        try {
            user = exportUserModel();
            addFollowedArtist();
            boolean hasAppPlaylist = false;
            Playlist appPlaylist =null;
            playlists = exportPlaylistModels();

            for (Playlist playlist : playlists) {
                if(!playlist.getName().equals(Config.appPlaylistName)){
                    ArrayList<Track> tracks = exportTrackModels(playlist.getId(),true);
                    playlist.setTracksIds(tracks);
                    singleton.addTracks(tracks);
                }else {
                    ArrayList<Track> tracks = exportTrackModels(playlist.getId(),false);
                    playlist.setTracksIds(tracks);
                    hasAppPlaylist=true;
                    appPlaylist =playlist;
                }
            }
            ArrayList<Artist> artists = exportArtistsFromToBeCreate();
            //update user genreMap
            ArrayList<String> genres =new ArrayList<>(genresTobeAdd);
            user.addGenres(genres, 1);
            for (Artist artist : artists) {
                singleton.addArtist(artist);
                //updata user artist map
                user.addArtist(artist.getId());
            }
            ArrayList<Album> albums = exportAlbumsFromToBeCreate();
            singleton.addAlbums(albums);

            // setup appPlaylist for main user
            if(singleton.getMainUser()==null){
                String playlistId = "";
                if(hasAppPlaylist){
                    // clean previous appPlaylist
                    playlistId = appPlaylist.getId();
                    if(!clearPlaylist(playlistId,false))
                        throw new RuntimeException("failed to clear playlist");
                }else{
                    // create appPlaylist
                    playlistId = createPlaylist(Config.appPlaylistName,false);
                    if(playlistId==null) throw new RuntimeException("failed to create playlist");
                }
                singleton.setAppPlaylistId(playlistId);
            }
            playlists.remove(appPlaylist);
            user.setPlaylists(playlists);
            singleton.addUser(user);


        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * Finally, create User by Factory
     *
     * @return User
     * @throws InterruptedException
     */
    private User exportUserModel() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        spotifyRequestAsyn.requestUserProfile(new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int status_code = result.getInt("status_code");
                    if (status_code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        JSONObject userJson = ServiceSpotify.infoOfUser(data);
                        user = UserFactory.getUser(userJson, new UserSpotify());
                        UserSpotId = user.getId();
                        semaphore.release();
                    } else throw new JSONException("Get response failed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        semaphore.acquire();
        return user;
    }


    /**
     * Add followedArtists to user
     * and update Set artistTobeCreate
     */
    private void addFollowedArtist() {
        spotifyRequestAsyn.requestFollowedArtist(limitFollowedArtist, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int status_code = result.getInt("status_code");
                    if (status_code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        JSONArray followedArtists = ServiceSpotify.followedArtistFromMe(data);
                        for (int i = 0; i < followedArtists.length(); i++) {
                            try {
                                String followedArtistId = followedArtists.getJSONObject(i).getString("id");
                                artistTobeCreate.add(followedArtistId);
                                user.addFollowedArtist(followedArtistId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else throw new JSONException("Get response failed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * Finally, create Array of Playlist by Factory
     *
     * @return ArrayList<Playlist>
     * @throws InterruptedException
     */
    private ArrayList<Playlist> exportPlaylistModels() throws InterruptedException {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        spotifyRequestAsyn.requestPlaylistsByUser(UserSpotId, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int status_code = result.getInt("status_code");
                    if (status_code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        ArrayList<JSONObject> playlistJson = ServiceSpotify.listPlaylistOfUser(data);
                        ArrayList<Playlist> ps = PlaylistFactory.getPlaylists(playlistJson, new PlaylistSpotify());
                        playlists.addAll(ps);
                        semaphore.release();
                    } else throw new JSONException("Get response failed");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        semaphore.acquire();
        return playlists;
    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * We get an array of Track JSONObject
     * Finally, create Array of Track by Factory
     *
     * @param playListId
     * @param mode       true : update artists,albums and features. false : only tracks
     * @return ArrayList<Track>
     */
    private ArrayList<Track> exportTrackModels(String playListId, boolean mode) throws InterruptedException {

        ArrayList<Track> alltracks = new ArrayList<>();
        final boolean[] hasNext = {true};
        final int[] offset = {0};

        Semaphore semaphore = new Semaphore(1);

        while (hasNext[0]) {
            semaphore.acquire();
            spotifyRequestAsyn.requestTracksByPlaylist(playListId, offset[0], new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200) {
                            JSONObject data = result.getJSONObject("data");
                            ArrayList<JSONObject> trackJson = ServiceSpotify.listTracksOfPlaylist(data);
                            ArrayList<Track> ts = TrackFactory.getTracks(trackJson, new TrackSpotify());

                            if (!mode) {
                                alltracks.addAll(ts);
                                if (ts.size() >= 100) {
                                    hasNext[0] = true;
                                    offset[0] += 100;
                                } else {
                                    hasNext[0] = false;
                                }
                                semaphore.release();
                                return;
                            }

                            //Set up artistsIds for Track and send to be created artists
                            HashMap<String, ArrayList<String>> trackArtistsMap = ServiceSpotify.listArtistsOfTracks(data);
                            if (trackArtistsMap.size() != ts.size())
                                throw new ArrayStoreException();

                            for (Track track : ts) {
                                ArrayList<String> artists = trackArtistsMap.get(track.getId());
                                assert artists != null;
                                for (String artistId : artists) {
                                    track.addArtistId(artistId);
                                    artistTobeCreate.add(artistId);
                                }
                            }

                            //send to be create albums
                            HashMap<String, JSONObject> albumMap = ServiceSpotify.listAlbumsOfTracks(data);
                            for (Map.Entry map : albumMap.entrySet()) {
                                if (!albumTobeCreate.containsKey(map.getKey())) {
                                    albumTobeCreate.put((String) map.getKey(), (JSONObject) map.getValue());
                                }
                            }

                            //Set track features
                            HashMap<String, Track> idTrack = new HashMap<>();
                            String ids = "";
                            for (Track track : ts) {
                                if (!singleton.hasTrack(track.getId())) {
                                    idTrack.put(track.getId(), track);
                                    ids += track.getId() + ",";

                                }
                            }
                            if (ids.length() > 0) {
                                ids = ids.substring(0, ids.length() - 1);

                                spotifyRequestAsyn.requestTracksFeatures(ids, new ExportCallbackO() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        try {
                                            JSONObject data = result.getJSONObject("data");
                                            JSONArray audio_features = data.getJSONArray("audio_features");
                                            for (int i = 0; i < audio_features.length(); i++) {
                                                JSONObject featuresJson = audio_features.getJSONObject(i);
                                                double acousticness = featuresJson.getDouble("acousticness");
                                                double danceability = featuresJson.getDouble("danceability");
                                                double energy = featuresJson.getDouble("energy");
                                                double speechiness = featuresJson.getDouble("speechiness");
                                                double valence = featuresJson.getDouble("valence");
                                                String id = featuresJson.getString("id");
                                                Track track = idTrack.get(id);
                                                ((TrackSpotify) track.getMyModelItf()).setAcousticness(acousticness);
                                                ((TrackSpotify) track.getMyModelItf()).setDanceability(danceability);
                                                ((TrackSpotify) track.getMyModelItf()).setEnergy(energy);
                                                ((TrackSpotify) track.getMyModelItf()).setSpeechiness(speechiness);
                                                ((TrackSpotify) track.getMyModelItf()).setValence(valence);
                                            }
                                            alltracks.addAll(ts);
                                            if (ts.size() >= 100) {
                                                hasNext[0] = true;
                                                offset[0] += 100;
                                            } else {
                                                hasNext[0] = false;
                                            }
                                            semaphore.release();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                });
                            } else {
                                hasNext[0] = false;
                                semaphore.release();
                            }

                        } else throw new JSONException("Get response failed");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        semaphore.acquire();
        return alltracks;
    }


    /**
     * Create an array of artist from artist to be create map
     * update artist genres
     *
     * @return ArrayList<Artist>
     */
    private ArrayList<Artist> exportArtistsFromToBeCreate() throws JSONException, InterruptedException {
        ArrayList<Artist> artists = new ArrayList<>();

        Semaphore semaphore = new Semaphore(0);

        for (int i = 0; i < artistTobeCreate.size(); i = i + 50) {
            // create ids for request
            StringBuilder ids = new StringBuilder();
            int size = Math.min(i + 50, artistTobeCreate.size());
            for (int j = i; j < size; j++) {
                ids.append((artistTobeCreate.toArray())[j]).append(",");
            }
            ids = new StringBuilder(ids.substring(0, ids.length() - 1));
            //get artists json from api
            spotifyRequestAsyn.requestArtistsByIds(ids.toString(), new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200) {
                            JSONObject data = result.getJSONObject("data");
                            JSONArray artistsJSON = data.getJSONArray("artists");
                            for (int k = 0; k < artistsJSON.length(); k++) {
                                JSONObject artistJSON = artistsJSON.getJSONObject(k);
                                Artist artist = ArtistFactory.getArtist(artistJSON,new ArtistSpotify());
                                //artist genre init
                                JSONArray genres = artistJSON.getJSONArray("genres");
                                for (int i = 0; i < genres.length(); i++) {
                                    String genre = (String) genres.get(i);
                                    genresTobeAdd.add(genre);
                                }
                                artists.add(artist);
                                semaphore.release();
                            }
                        } else throw new JSONException("Get response failed");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        semaphore.acquire(artistTobeCreate.size());
        return artists;
    }

    /**
     * Use by algorithm to get top tracks from artists
     * get artist from singleton and check it's top tracks
     * if it has previously set then return
     * else send request to API to get it
     * Use main access token which could be refreshed in case of expiration.
     *
     * @param artistId
     * @param hasRefreshed false : updateMainAccessToken, true : already update
     * @return
     */
    @Override
    public ArrayList<Track> exportArtistTopTracks(String artistId, boolean hasRefreshed) {

        ArrayList<Track> topTracks = new ArrayList<>();
        Semaphore semaphore = new Semaphore(0);
        try {
            spotifyRequestAsyn.requestTopTracksFromArtist(artistId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200) {
                            JSONObject data = result.getJSONObject("data");
                            ArrayList<JSONObject> tracksJson = ServiceSpotify.topTracksFromArtist(data);
                            ArrayList<Track> ts = TrackFactory.getTracks(tracksJson, new TrackSpotify());
                            topTracks.addAll(ts);
                            semaphore.release();
                        } else if (status_code == 401 && !hasRefreshed) {
                            updateMainAccessToken();
                            topTracks.addAll(exportArtistTopTracks(artistId, true));
                            semaphore.release();
                        } else throw new JSONException("Get response failed");
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return topTracks;

    }

    /**
     * Get id of albums from an artist
     *
     * @param artistId
     * @param hasRefreshed false : updateMainAccessToken, true : already update
     * @return
     */
    @Override
    public Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed) {
        Set<String> albums = new HashSet<>();
        Semaphore semaphore = new Semaphore(0);
        try {
            spotifyRequestAsyn.requestAlbumsFromArtist(artistId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200) {
                            JSONObject data = result.getJSONObject("data");
                            Set<String> ids = ServiceSpotify.albumsFromArtist(data);
                            albums.addAll(ids);
                            semaphore.release();
                        } else if (status_code == 401 && !hasRefreshed) {
                            updateMainAccessToken();
                            albums.addAll(fetchAlbumsIdsFromArtist(artistId, true));
                            semaphore.release();
                        } else throw new JSONException("Get response failed");

                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return albums;

    }

    /**
     * Create an array of album from album to be create map
     *
     * @return ArrayList<Album>
     * @throws JSONException
     */
    private ArrayList<Album> exportAlbumsFromToBeCreate() throws JSONException {
        ArrayList<Album> albums = new ArrayList<>();
        for (Map.Entry map : albumTobeCreate.entrySet()) {
            JSONObject albumJson = (JSONObject) map.getValue();
            Album album = AlbumFactory.getAlbum(albumJson,new AlbumSpotify());
            albums.add(album);
        }
        return albums;
    }

    /**
     * Export track from an album
     *
     * @param albumId
     * @param hasRefreshed false : updateMainAccessToken, true : already update
     * @return ArrayList<Track>
     */
    @Override
    public ArrayList<Track> exportTracksFromAlbum(String albumId, boolean hasRefreshed) {
        ArrayList<Track> tracks = new ArrayList<>();

        Semaphore semaphore = new Semaphore(0);
        try {
            //Get id of tracks from album first
            spotifyRequestAsyn.requestTracksFromAlbum(albumId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200) {
                            JSONObject data = result.getJSONObject("data");
                            Set<String> idSet = ServiceSpotify.tracksIdFromAlbum(data);
                            String ids = idSet.toString();
                            ids = ids.substring(1, ids.length() - 1);
                            ids = ids.replaceAll("\\s", "");
                            //Then get several tracks from track api
                            spotifyRequestAsyn.requestTracks(ids, new ExportCallbackO() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        JSONObject data = result.getJSONObject("data");
                                        ArrayList<JSONObject> trackJSONList = ServiceSpotify.listOfTracks(data);
                                        ArrayList<Track> alltracks = TrackFactory.getTracks(trackJSONList, new TrackSpotify());
                                        tracks.addAll(alltracks);
                                        semaphore.release();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else if (status_code == 401 && !hasRefreshed) {
                            updateMainAccessToken();
                            tracks.addAll(exportTracksFromAlbum(albumId, true));
                            semaphore.release();

                        } else{

                            throw new JSONException("Get response failed on : " + albumId);
                        }
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return tracks;
    }

    private boolean clearPlaylist(String playlistId, boolean hasRefreshed) {
        final boolean[] res = {false};
        Semaphore semaphore = new Semaphore(0);

        try {
            ArrayList<Track> tracks = exportTrackModels(playlistId, false);
            JSONArray dataArr = new JSONArray();
            for (Track track : tracks) {
                JSONObject uri = new JSONObject();
                uri.put("uri", "spotify:track:" + track.getId());
                dataArr.put(uri);
            }
            JSONObject data = new JSONObject();
            data.put("tracks", dataArr);
            spotifyRequestAsyn.requestDeleteTracks(playlistId, data.toString(), new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200 || status_code == 201) {
                            res[0] = true;
                            semaphore.release();
                        } else if (status_code == 401 && !hasRefreshed) {
                            updateMainAccessToken();
                            res[0] = clearPlaylist(playlistId, true);
                            semaphore.release();
                        } else throw new JSONException("Get response failed");
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            semaphore.acquire();
            return res[0];
        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public String createPlaylist(String name, boolean hasRefreshed) {
        final String[] id = new String[1];
        JSONObject param = new JSONObject();
        Semaphore semaphore = new Semaphore(0);
        try {
            param.put("name", name);
            param.put("public", true);
            spotifyRequestAsyn.requestCreatePlaylist(UserSpotId, param.toString(), new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        int status_code = result.getInt("status_code");
                        if (status_code == 200 || status_code == 201) {
                            JSONObject data = result.getJSONObject("data");
                            id[0] = data.getString("id");
                            semaphore.release();
                        } else if (status_code == 401 && !hasRefreshed) {
                            updateMainAccessToken();
                            id[0] = createPlaylist(name, true);
                            semaphore.release();
                        } else throw new JSONException("Get response failed");
                    } catch (JSONException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            semaphore.acquire();
            return id[0];
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed) {
        final boolean[] res = new boolean[1];
        Semaphore semaphore = new Semaphore(0);
        try {
            if (clearPlaylist(singleton.getAppPlaylistId(), false)) {
                JSONObject param = new JSONObject();
                JSONArray trackArr = new JSONArray();
                for (Track track : tracks) {
                    String data = "spotify:track:" + track.getId();
                    trackArr.put(data);
                }

                param.put("uris", trackArr);
                String appPlaylistId = singleton.getAppPlaylistId();
                spotifyRequestAsyn.requestAddTracksToAppPlaylist(appPlaylistId, param.toString(), new ExportCallbackO() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            int status_code = result.getInt("status_code");
                            if (status_code==200 || status_code == 201){
                                res[0] = true;
                                semaphore.release();
                            } else throw new JSONException("Get response failed");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                semaphore.acquire();
                return res[0];
            }else throw new RuntimeException("failed to clear playlist");
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed) {
        Semaphore semaphore = new Semaphore(0);
        ArrayList<Artist> relatedArtists = new ArrayList<>();
        spotifyRequestAsyn.requestRelatedArtist(artistId, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    int status_code = result.getInt("status_code");
                    if (status_code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        ArrayList<JSONObject> relatedArtistsJson = ServiceSpotify.relatedArtists(data);
                        ArrayList<Artist> artists = ArtistFactory.getArtists(relatedArtistsJson,new ArtistSpotify());
                        relatedArtists.addAll(artists);
                        semaphore.release();
                    }else if (status_code == 401 && !hasRefreshed) {
                        updateMainAccessToken();
                        relatedArtists.addAll(exportRelatedArtists(artistId, true));
                        semaphore.release();
                    }
                    else throw new JSONException("Get response failed");

                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return relatedArtists;
    }


    private void updateMainAccessToken() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        spotifyRequestAsyn.getNewMainAccessToken(new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {

                try {
                    int status_code = result.getInt("status_code");
                    if (status_code == 200) {
                        JSONObject data = result.getJSONObject("data");
                        String newToken = data.getString("access_token");
                        singleton.setMainAccessToken(newToken);
                        semaphore.release();
                    } else throw new JSONException("Get response failed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        semaphore.acquire(1);
    }


}
