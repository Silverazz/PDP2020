package com.example.pdpproject.apiManager;


import com.example.pdpproject.Config;
import com.example.pdpproject.apiManager.requests.SpotifyRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;


public class APIManagerSpotifySyn implements APIManagerItf {

    private String UserSpotId;
    private final int limitFollowedArtist = 20;


    private User user;
    private ArrayList<Playlist> playlists;
    private Set<String> artistTobeCreate;
    private HashMap<String, JSONObject> albumTobeCreate;
    private CopyOnWriteArrayList<String> genresTobeAdd;
    //Singleton
    private Singleton singleton = Singleton.getInstance();
    private SpotifyRequest spotifyRequest;

    public APIManagerSpotifySyn( ) {
        artistTobeCreate = new HashSet<>();
        albumTobeCreate = new HashMap<>();
        genresTobeAdd =new CopyOnWriteArrayList<>();
        this.spotifyRequest = new SpotifyRequest() ;
    }

    @Override
    public void setRequestToken(String token) {
        this.spotifyRequest.setToken(token);
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

            for (Artist artist : artists) {
                singleton.addArtist(artist);
                //update user genreMap
                ArrayList<String> genres = new ArrayList<>(genresTobeAdd);
                user.addGenres(genres,1);
                //update user artist map
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

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * Finally, create User by Factory
     *
     * @return User
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private User exportUserModel() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = spotifyRequest.requestUserProfile().get();
        int status_code = response.getInt("status_code");
        if(status_code==200){
            JSONObject data = response.getJSONObject("data");
            JSONObject userJson = ServiceSpotify.infoOfUser(data);
            user = UserFactory.getUser(userJson, new UserSpotify());
            UserSpotId = user.getId();
            return user;
        }
        throw new JSONException("Get response failed");
    }

    /**
     * Add followedArtists to user
     * and update Set artistTobeCreate
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JSONException
     */
    private void addFollowedArtist() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = spotifyRequest.requestFollowedArtist(limitFollowedArtist).get();
        int status_code = response.getInt("status_code");
        if(status_code==200){
            JSONObject data = response.getJSONObject("data");
            JSONArray followedArtists = ServiceSpotify.followedArtistFromMe(data);
            for(int i = 0; i<followedArtists.length();i++){
                String followedArtistId = followedArtists.getJSONObject(i).getString("id");
                artistTobeCreate.add(followedArtistId);
                user.addFollowedArtist(followedArtistId);
            }
        }else throw new JSONException("Get response failed");


    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * Finally, create Array of Playlist by Factory
     *
     * @return ArrayList<Playlist>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private ArrayList<Playlist> exportPlaylistModels() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = spotifyRequest.requestPlaylistsByUser(UserSpotId).get();
        int status_code = response.getInt("status_code");
        if(status_code ==200){
            JSONObject data = response.getJSONObject("data");
            ArrayList<JSONObject> playlistJson = ServiceSpotify.listPlaylistOfUser(data);
            return PlaylistFactory.getPlaylists(playlistJson, new PlaylistSpotify());
        }
        else throw new JSONException("Get response failed");
    }

    /**
     * Get JSON response from API
     * Then use ServiceSpotify to filter data
     * We get an array of Track JSONObject
     * Finally, create Array of Track by Factory
     *
     * @param playListId
     * @param mode true : update artists,albums and features. false : only tracks
     * @return ArrayList<Track>
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private ArrayList<Track> exportTrackModels(String playListId, boolean mode) throws ExecutionException, InterruptedException, JSONException {

        boolean hasNext = true;
        int offset = 0;
        ArrayList<Track> allTracks = new ArrayList<>();
        while (hasNext){
            JSONObject response = spotifyRequest.requestTracksByPlaylist(playListId, offset).get();
            int status_code = response.getInt("status_code");
            if (status_code==200){
                JSONObject data = response.getJSONObject("data");
                ArrayList<JSONObject> trackJson = ServiceSpotify.listTracksOfPlaylist(data);
                ArrayList<Track> tracks = TrackFactory.getTracks(trackJson, new TrackSpotify());
                if(!mode){
                    allTracks.addAll(tracks);
                    if(tracks.size()>=100){
                        hasNext = true;
                        offset+=100;
                    }else {
                        hasNext =false;
                    }
                    continue;
                }

                //Set up artistsIds for Track and send to be created artists
                HashMap<String, ArrayList<String>> trackArtistsMap = ServiceSpotify.listArtistsOfTracks(data);
                if (trackArtistsMap.size() != tracks.size()) throw new ArrayStoreException();
                for (Track track : tracks) {
                    ArrayList<String> artists = trackArtistsMap.get(track.getId());
                    assert artists != null;
                    for (String artistId : artists) {
                        track.addArtistId(artistId);
                        artistTobeCreate.add(artistId);
                    }
                }

                //send to be create albums
                HashMap<String, JSONObject> albumMap = ServiceSpotify.listAlbumsOfTracks(data);
                for (Map.Entry map : albumMap.entrySet())
                    if (!albumTobeCreate.containsKey(map.getKey()))
                        albumTobeCreate.put((String) map.getKey(), (JSONObject) map.getValue());


                //Set track features
                HashMap<String, Track> idTrack =new HashMap<>();
                String ids = "";
                for(Track track : tracks){
                    if(!singleton.hasTrack(track.getId())){
                        idTrack.put(track.getId(),track);
                        ids += track.getId()+",";

                    }
                }
                ids = ids.substring(0,ids.length()-1);
                JSONObject response1 = spotifyRequest.requestTracksFeatures(ids).get();
                int status_code1 = response1.getInt("status_code");
                if(status_code1==200){
                    JSONArray audio_features = response1.getJSONObject("data").getJSONArray("audio_features");
                    for(int i =0; i<audio_features.length();i++){
                        JSONObject featuresJson = audio_features.getJSONObject(i);
                        double acousticness = featuresJson.getDouble("acousticness");
                        double danceability = featuresJson.getDouble("danceability");
                        double energy = featuresJson.getDouble("energy");
                        double speechiness = featuresJson.getDouble("speechiness");
                        double valence = featuresJson.getDouble("valence");
                        String id = featuresJson.getString("id");
                        Track track = idTrack.get(id);
                        ((TrackSpotify) track.getMyModelItf() ) .setAcousticness(acousticness);
                        ((TrackSpotify) track.getMyModelItf() ) .setDanceability(danceability);
                        ((TrackSpotify) track.getMyModelItf() ) .setEnergy(energy);
                        ((TrackSpotify) track.getMyModelItf() ) .setSpeechiness(speechiness);
                        ((TrackSpotify) track.getMyModelItf() ) .setValence(valence);
                    }
                }
                else throw new JSONException("Get response failed");;



                allTracks.addAll(tracks);
                if(tracks.size()>=100){
                    hasNext = true;
                    offset+=100;
                }else {
                    hasNext =false;
                }
            }else throw new JSONException("Get response failed");;

        }
       return allTracks;
    }



    /**
     * Create an array of artist from artist to be create map
     * update artist genres
     * @return ArrayList<Artist>
     */
    private ArrayList<Artist> exportArtistsFromToBeCreate() throws ExecutionException, InterruptedException, JSONException {
        ArrayList<Artist> artists = new ArrayList<>();

        for(int i =0; i<artistTobeCreate.size();i=i+50) {
            // create ids for request
            String ids = "";
            int size = Math.min(i + 50, artistTobeCreate.size());
            for (int j = i; j < size; j++) {
                ids += (artistTobeCreate.toArray())[j] + ",";
            }
            ids = ids.substring(0, ids.length() - 1);
            JSONObject response = spotifyRequest.requestArtistsByIds(ids).get();
            int status_code = response.getInt("status_code");
            if(status_code==200){
                JSONArray data = null;
                try {
                    data = response.getJSONObject("data").getJSONArray("artists");
                    for (int k = 0; k < data.length(); k++) {
                        JSONObject artistJSON = data.getJSONObject(k);
                        Artist artist = ArtistFactory.getArtist(artistJSON,new ArtistSpotify());
                        //artist genre init
                        JSONArray genres = artistJSON.getJSONArray("genres");
                        for (int p = 0; p < genres.length(); p++) {
                            String genre = (String) genres.get(p);
                            genresTobeAdd.add(genre);
                        }
                        artists.add(artist);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else throw new JSONException("Get response failed");

        }
        return artists;
    }

    /**
     * Use by algorithm to get top tracks from artists
     * get artist from singleton and check it's top tracks
     * if it has previously set then return
     * else send request to API to get it
     * Use main access token which could be refreshed in case of expiration.
     * @param artistId
     * @param hasRefreshed false : updateMainAccessToken, true : already update
     * @return
     * @throws InterruptedException
     */
    @Override
    public final ArrayList<Track> exportArtistTopTracks(String artistId, boolean hasRefreshed) {
        //artist topTrackIds init
        ArrayList<Track> topTracks =null;
        try {
            JSONObject response = spotifyRequest.requestTopTracksFromArtist(artistId).get();
            int status_code = response.getInt("status_code");
            if(status_code==200){
                JSONObject data = response.getJSONObject("data");
                ArrayList<JSONObject> tracksJson = ServiceSpotify.topTracksFromArtist(data);
                topTracks =TrackFactory.getTracks(tracksJson,new TrackSpotify());

            }else if(status_code==401 && !hasRefreshed){
                //use refresh token to update main access token
                updateMainAccessToken();
                return exportArtistTopTracks(artistId,true);
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return topTracks;

    }

    /**
     * Get id of albums from an artist
     * @param artistId
     * @param hasRefreshed false : updateMainAccessToken, true : already update
     * @return
     */
    @Override
    public Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed) {
        try {
            JSONObject response = spotifyRequest.requestAlbumsFromArtist(artistId).get();
            int status_code = response.getInt("status_code");
            if(status_code==200){
                JSONObject data = response.getJSONObject("data");
                return ServiceSpotify.albumsFromArtist(data);
            }else if(status_code==401 && !hasRefreshed){
                updateMainAccessToken();
                return fetchAlbumsIdsFromArtist(artistId,true);
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Create an array of album from album to be create map
     * @return ArrayList<Album>
     * @throws JSONException
     */
    private ArrayList<Album> exportAlbumsFromToBeCreate() throws JSONException {
        ArrayList<Album> albums = new ArrayList<>();
        for (Map.Entry map : albumTobeCreate.entrySet()){
            JSONObject albumJson = (JSONObject) map.getValue();
            Album album = AlbumFactory.getAlbum(albumJson,new AlbumSpotify());
            albums.add(album);
        }
        return albums;
    }

    /**
     * Export track from an album
     * @param albumId
     * @return ArrayList<Track>
     */
    @Override
    public ArrayList<Track> exportTracksFromAlbum(String albumId,boolean hasRefreshed) {
        // Get id of tracks from an album
        try {
            JSONObject response = spotifyRequest.requestTracksFromAlbum(albumId).get();
            int status_code = response.getInt("status_code");
            if(status_code==200){
                JSONObject data = response.getJSONObject("data");
                Set<String> idSet = ServiceSpotify.tracksIdFromAlbum(data);
                String ids = idSet.toString();

                ids = ids.substring(1,ids.length()-1);
                ids = ids.replaceAll("\\s","");
                //Then get several tracks from track api
                JSONObject response1 = spotifyRequest.requestTracks(ids).get();
                JSONObject data1 =response1.getJSONObject("data");
                ArrayList<JSONObject> trackJSONList = ServiceSpotify.listOfTracks(data1);
                return TrackFactory.getTracks(trackJSONList, new TrackSpotify());
            }else if(status_code == 401 && !hasRefreshed){
                updateMainAccessToken();
                return exportTracksFromAlbum(albumId,true);
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean clearPlaylist(String playlistId, boolean hasRefreshed)  {
        try {
        ArrayList<Track> tracks = exportTrackModels(playlistId,false);
        JSONArray dataArr = new JSONArray();
        for(Track track :tracks){
            JSONObject uri = new JSONObject();
            uri.put("uri","spotify:track:"+track.getId());
            dataArr.put(uri);
        }
        JSONObject data = new JSONObject();
        data.put("tracks",dataArr);

            JSONObject response = spotifyRequest.requestDeleteTracks(playlistId,data.toString()).get();
            int status_code = response.getInt("status_code");
            if(status_code==200 || status_code==201){
                return true;
            }else if(status_code==401 && !hasRefreshed){
                updateMainAccessToken();
                return clearPlaylist(playlistId,true);
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public String createPlaylist(String name, boolean hasRefreshed){
        JSONObject param = new JSONObject();

        try {
            param.put("name",name);
            param.put("public",true);
            JSONObject response = spotifyRequest.requestCreatePlaylist(UserSpotId,param.toString()).get();
            int status_code = response.getInt("status_code");
            if(status_code==200 || status_code ==201){
                JSONObject data = response.getJSONObject("data");
                return data.getString("id");
            }else if(status_code==401 && !hasRefreshed){
                updateMainAccessToken();
                return createPlaylist(name,true);
            }
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed){
        try {
            if(clearPlaylist(singleton.getAppPlaylistId(),false)){
                JSONObject param = new JSONObject();
                JSONArray trackArr = new JSONArray();
                for(Track track :tracks){
                    String data = "spotify:track:" +track.getId();
                    trackArr.put(data);
                }
                param.put("uris",trackArr);
                String appPlaylistId = singleton.getAppPlaylistId();
                JSONObject response = spotifyRequest.requestAddTracksToAppPlaylist(appPlaylistId,param.toString()).get();
                int status_code = response.getInt("status_code");
                if(status_code==200 || status_code == 201) return true;
            }else throw new RuntimeException("failed to clear playlist");
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed) {
        ArrayList<Artist> relatedArtists = new ArrayList<>();
        try {
            JSONObject response = spotifyRequest.requestRelatedArtist(artistId).get();
            int status_code = response.getInt("status_code");
            if(status_code==200){
                JSONObject data = response.getJSONObject("data");
                ArrayList<JSONObject> artistsJson = ServiceSpotify.relatedArtists(data);
                ArrayList<Artist> artists = ArtistFactory.getArtists(artistsJson,new ArtistSpotify());
                relatedArtists.addAll(artists);
            }else if(status_code==401 && !hasRefreshed){
                updateMainAccessToken();
                return exportRelatedArtists(artistId,true);
            }else throw new RuntimeException("failed to clear playlist");
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
        }
        return relatedArtists;
    }


    private void updateMainAccessToken() throws ExecutionException, InterruptedException, JSONException {
        JSONObject response = spotifyRequest.getNewMainAccessToken().get();
        int status_code = response.getInt("status_code");
        if(status_code==200){
            JSONObject data = response.getJSONObject("data");
            String newToken = data.getString("access_token");
            singleton.setMainAccessToken(newToken);
        }else throw new JSONException("Get response failed");
    }


}
