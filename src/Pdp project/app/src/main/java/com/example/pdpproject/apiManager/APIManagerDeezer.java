package com.example.pdpproject.apiManager;

import com.example.pdpproject.Config;
import com.example.pdpproject.apiManager.requests.DeezerRequest;
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
import com.example.pdpproject.models.albums.AlbumDeezer;
import com.example.pdpproject.models.artists.ArtistDeezer;
import com.example.pdpproject.models.playlists.PlaylistDeezer;
import com.example.pdpproject.models.tracks.TrackDeezer;
import com.example.pdpproject.models.users.UserDeezer;
import com.example.pdpproject.repo.Singleton;
import com.example.pdpproject.service.ServiceDeezer;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class APIManagerDeezer implements APIManagerItf {

    private String UserDeezerId;
    private final int limitFollowedArtist = 20;


    private com.example.pdpproject.models.User user;
    private ArrayList<Playlist> playlists;
    private Set<String> artistTobeCreate;
    private HashMap<String, JSONObject> albumTobeCreate;
    private CopyOnWriteArrayList<String> genresTobeAdd;
    //Singleton
    private Singleton singleton = Singleton.getInstance();
    private DeezerRequest deezerRequest;

    public APIManagerDeezer() {
        artistTobeCreate = new HashSet<>();
        albumTobeCreate = new HashMap<>();
        genresTobeAdd = new CopyOnWriteArrayList<>();
        deezerRequest = new DeezerRequest();
    }

    @Override
    public void exportModels() {
        try {
            user = exportUserModel();
            addFollowedArtist();

            boolean hasAppPlaylist = false;
            Playlist appPlaylist =null;
            playlists = exportPlaylistModels();
            for(Playlist playlist : playlists){
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
            for (Artist artist : artists) {
                singleton.addArtist(artist);
                //updata user artist map
                user.addArtist(artist.getId());
            }

            ArrayList<Album> albums = exportAlbumsFromToBeCreate();
            //update user genreMap
            ArrayList<String> genres = new ArrayList<>(genresTobeAdd);
            user.addGenres(genres, 1);
            singleton.addAlbums(albums);


            // setup appPlaylist for main user
            if(singleton.getMainUser()==null){
                String playlistId = "";
                if(hasAppPlaylist){
                    // clean previous appPlaylist
                    playlistId = appPlaylist.getId();
                    singleton.setAppPlaylistId(playlistId);
                    if(!clearPlaylist(false))
                        throw new RuntimeException("failed to clear playlist");
                }else{
                    // create appPlaylist
                    playlistId = createPlaylist(Config.appPlaylistName,false);
                    if(playlistId==null) throw new RuntimeException("failed to create playlist");
                    singleton.setAppPlaylistId(playlistId);
                }

            }
            playlists.remove(appPlaylist);
            user.setPlaylists(playlists);
            singleton.addUser(user);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get JSON response from API
     * then create user model by factory
     * @return User
     * @throws InterruptedException
     */
    private User exportUserModel() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        deezerRequest.requestUserProfile(new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {

                        JSONObject data = result.getJSONObject("data");
                        user = UserFactory.getUser(data, new UserDeezer());
                        UserDeezerId = user.getId();
                        semaphore.release();
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
        deezerRequest.requestFollowedArtist(limitFollowedArtist, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {

                        JSONObject data = result.getJSONObject("data");
                        ArrayList<String> followedArtists = ServiceDeezer.followedArtistFromMe(data);
                        for (String id:followedArtists) {
                                artistTobeCreate.add(id);
                                user.addFollowedArtist(id);
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private ArrayList<Playlist> exportPlaylistModels() throws InterruptedException {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        deezerRequest.requestPlaylistsByUser(UserDeezerId, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {

                        JSONObject data = result.getJSONObject("data");
                        ArrayList<JSONObject> playlistJson = ServiceDeezer.listPlaylistOfUser(data);
                        ArrayList<Playlist> ps = PlaylistFactory.getPlaylists(playlistJson, new PlaylistDeezer());
                        playlists.addAll(ps);
                        semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        semaphore.acquire();
        return playlists;
    }

    private ArrayList<Track> exportTrackModels(String playListId, boolean mode) throws InterruptedException {

        ArrayList<Track> alltracks = new ArrayList<>();
        Semaphore semaphore = new Semaphore(0);


        deezerRequest.requestTracksByPlaylist(playListId, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    ArrayList<JSONObject> trackJson = ServiceDeezer.listTracksOfPlaylist(data);
                    ArrayList<Track> ts = TrackFactory.getTracks(trackJson, new TrackDeezer());
                    alltracks.addAll(ts);
                    if(!mode){

                        semaphore.release();
                    }else{
                        //Set up artistsIds for Track and send to be created artists
                        HashMap<String, String> trackArtistsMap = ServiceDeezer.artistOfTracks(data);
                        for (Track track : ts) {
                            String artistId = trackArtistsMap.get(track.getId());
                            track.addArtistId(artistId);
                            artistTobeCreate.add(artistId);
                        }

                        //send to be create albums
                        HashMap<String, JSONObject> albumMap = ServiceDeezer.albumOfTracks(data);
                        for (Map.Entry map : albumMap.entrySet()) {
                            if (!albumTobeCreate.containsKey(map.getKey())) {
                                albumTobeCreate.put((String) map.getKey(), (JSONObject) map.getValue());
                            }
                        }

                        semaphore.release();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.acquire();
        return alltracks;
    }


    /**
     * Create an array of artist from artist to be create map
     * update artist genres
     *
     * @return ArrayList<Artist>
     */
    private ArrayList<Artist> exportArtistsFromToBeCreate() throws InterruptedException {
        ArrayList<Artist> artists = new ArrayList<>();

        Semaphore semaphore = new Semaphore(0);
        Iterator<String> itr = artistTobeCreate.iterator();
        while (itr.hasNext()) {
            //get artists json from api
            deezerRequest.requestArtistById(itr.next(), new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject artistJson = result.getJSONObject("data");
                        Artist artist = ArtistFactory.getArtist(artistJson,new ArtistDeezer());
                        if(artist!=null)
                            artists.add(artist);
                        semaphore.release();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        semaphore.acquire(artistTobeCreate.size());
        return artists;
    }

    @Override
    public ArrayList<Track> exportArtistTopTracks(String artistId, boolean hasRefreshed) {

        ArrayList<Track> topTracks = new ArrayList<>();
        Semaphore semaphore = new Semaphore(0);
        try {
            deezerRequest.requestTopTracksFromArtist(artistId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject data = result.getJSONObject("data");
                        ArrayList<JSONObject> topTracksJSON= ServiceDeezer.topTracksFromArtist(data);
                        ArrayList<Track> ts = TrackFactory.getTracks(topTracksJSON, new TrackDeezer());
                        topTracks.addAll(ts);
                        semaphore.release();
                    } catch (JSONException e) {
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

    @Override
    public Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed) {
        Set<String> albums = new HashSet<>();
        Semaphore semaphore = new Semaphore(0);
        try {
            deezerRequest.requestAlbumsFromArtist(artistId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {

                            JSONObject data = result.getJSONObject("data");
                            Set<String> ids = ServiceDeezer.albumsFromArtist(data);
                            albums.addAll(ids);
                            semaphore.release();
                        } catch (JSONException ex) {
                        ex.printStackTrace();
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
    private ArrayList<Album> exportAlbumsFromToBeCreate() {
        ArrayList<Album> albums = new ArrayList<>();
        for (Map.Entry map : albumTobeCreate.entrySet()) {
            JSONObject albumJson = (JSONObject) map.getValue();
            Album album = AlbumFactory.getAlbum(albumJson,new AlbumDeezer());
            albums.add(album);
        }
        Semaphore semaphore = new Semaphore(0);
        // send genres to genremap
        for(Album album :albums){
            deezerRequest.requestAlbumGenres(album.getId(), new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject data = result.getJSONObject("data");
                        ArrayList<String> genres = ServiceDeezer.genresFromAlbum(data);
                        genresTobeAdd.addAll(genres);
                        semaphore.release();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        try {
            semaphore.acquire(albumTobeCreate.size());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return albums;
    }

    @Override
    public ArrayList<Track> exportTracksFromAlbum(String albumId, boolean hasRefreshed) {

        ArrayList<Track> tracks = new ArrayList<>();
        Semaphore semaphore = new Semaphore(0);
        try {
            //Get id of tracks from album first
            deezerRequest.requestTracksFromAlbum(albumId, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject data = result.getJSONObject("data");
                        ArrayList<JSONObject> trackJSONList = ServiceDeezer.listOfTracks(data,albumId);
                        ArrayList<Track> alltracks = TrackFactory.getTracks(trackJSONList, new TrackDeezer());
                        tracks.addAll(alltracks);
                        semaphore.release();


                    } catch (JSONException e) {
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



    private boolean clearPlaylist(boolean hasRefreshed) {
        final boolean[] res = {false};
        Semaphore semaphore = new Semaphore(0);
        String playlistId = singleton.getAppPlaylistId();
        try {
            ArrayList<Track> tracks = exportTrackModels(playlistId, false);
            if(tracks.size()==0){
                res[0] =true;
                semaphore.release();
            }else {
                String songs = "";
                for (Track track : tracks) {
                    songs+=track.getId()+",";
                }
                songs = songs.substring(0,songs.length()-1);
                deezerRequest.requestDeleteTracks(playlistId, songs, new ExportCallbackO() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONObject data = result.getJSONObject("data");
                            res[0]=data.getBoolean("res");
                            semaphore.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];
    }
    @Override
    public String createPlaylist(String name, boolean hasRefreshed) {
        final String[] id = new String[1];
        Semaphore semaphore = new Semaphore(0);
        try {
            deezerRequest.requestCreatePlaylist( name, new ExportCallbackO() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                            JSONObject data = result.getJSONObject("data");
                            id[0] = data.getString("id");
                            semaphore.release();
                    } catch (JSONException  e) {
                        e.printStackTrace();
                    }
                }
            });
            semaphore.acquire();
        } catch ( InterruptedException e) {
            e.printStackTrace();
        }
        return id[0];
    }

    @Override
    public boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed) {
        final boolean[] res = new boolean[1];
        Semaphore semaphore = new Semaphore(0);
        try {
            if (clearPlaylist(false)) {

                String songs = null;
                for (Track track : tracks) {
                    songs+=track.getId()+",";
                }
                songs =songs.substring(0,songs.length()-1);

                String appPlaylistId = singleton.getAppPlaylistId();
                deezerRequest.requestAddTracksToAppPlaylist(appPlaylistId, songs, new ExportCallbackO() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            JSONObject data = result.getJSONObject("data");
                            res[0] = data.getBoolean("res");
                            semaphore.release();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                semaphore.acquire();
            }else throw new RuntimeException("failed to clear playlist");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res[0];
    }

    @Override
    public ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed) {
        Semaphore semaphore = new Semaphore(0);
        ArrayList<Artist> relatedArtists = new ArrayList<>();
        deezerRequest.requestRelatedArtist(artistId, new ExportCallbackO() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    ArrayList<JSONObject> relatedArtistsJson = ServiceDeezer.relatedArtists(data);
                    ArrayList<Artist> artists = ArtistFactory.getArtists(relatedArtistsJson,new ArtistDeezer());
                    relatedArtists.addAll(artists);
                    semaphore.release();
                } catch (JSONException e) {
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

    @Override
    public void setRequestToken(String token) {
        deezerRequest.setToken(token);
    }

}
