package com.example.pdpproject.repo;

import com.example.pdpproject.Config;
import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Singleton {

    private static Singleton ourInstance;
    private String mainAccessToken;
    private String refreshToken;
    private String appPlaylistId;
    private User mainUser;
    private Config config;

    //key : id
    private HashMap<String, User> userHashMap;
    private HashMap<String, Track> trackHashMap;
    private HashMap<String, Artist> artistHashMap;
    private HashMap<String, Album> albumHashMap;


    public static synchronized Singleton getInstance() {
        if (ourInstance==null)
            ourInstance=new Singleton();
        return ourInstance;
    }

    private Singleton() {
        userHashMap = new HashMap<>();
        trackHashMap = new HashMap<>();
        artistHashMap = new HashMap<>();
        albumHashMap = new HashMap<>();
        config = new Config();
    }

    public boolean clearAll(){
        userHashMap.clear();
        trackHashMap.clear();
        artistHashMap.clear();
        albumHashMap.clear();
        return true;
    }

    public String getMainAccessToken() {
        return mainAccessToken;
    }

    public void setMainAccessToken(String mainAccessToken) {
        this.mainAccessToken = mainAccessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAppPlaylistId() {
        return appPlaylistId;
    }

    public void setAppPlaylistId(String appPlaylistId) {
        this.appPlaylistId = appPlaylistId;
    }
    public final HashMap<String, Track> getTrackHashMap() { return trackHashMap; }

    private void removeUserfromMap(String SpotID){
        userHashMap.remove(SpotID);
    }

    public Config getConfig() {
        return config;
    }


/**Data management**/
    /**User**/
    public User getUserById(String userId){
        if(mainUser.getId() == userId){
            return mainUser;
        }
        return userHashMap.get(userId);
    }
    public User getMainUser(){
        return mainUser;
    }

    public void addUser(User user){
        if(mainUser==null){
            mainUser=user;
        }else {
            String userId = user.getId();
            if(!userHashMap.containsValue(user) && !userHashMap.containsKey(userId)){
                userHashMap.put(userId,user);
            }
        }

    }

    public void deleteUser(User user){
        removeUserfromMap(user.getId());
    }
    public final ArrayList<User> getAllUser(){
        ArrayList<User> users = new ArrayList<>();
        if(mainUser != null)
            users.add(mainUser);
        for(Map.Entry user : userHashMap.entrySet()){
            users.add((User) user.getValue());
        }
        return users;
    }
    public final ArrayList<User> getOtherUser(){
        ArrayList<User> users = new ArrayList<>();
        for(Map.Entry user : userHashMap.entrySet()){
            users.add((User) user.getValue());
        }
        return users;
    }


    /**Artist**/
    public Artist getArtistById(String artistId){
        return artistHashMap.get(artistId);
    }
    public void addArtist(Artist artist){
        String artistId = artist.getId();
        if(!artistHashMap.containsKey(artistId)){
            artistHashMap.put(artistId, artist);
        }
    }
    public ArrayList<Artist> getArtists(){
        ArrayList<Artist> artists= new ArrayList<>();
        for(Map.Entry map : artistHashMap.entrySet()){
            artists.add((Artist) map.getValue());
        }
        return  artists;
    }

    public final ArrayList<Artist> getArtistsByTrackId(String trackId){
        ArrayList<Artist> artists = new ArrayList<>();
        Track track = trackHashMap.get(trackId);
        for(String artistId : track.getArtistsIds()){
            artists.add(artistHashMap.get(artistId));
        }
        return artists;
    }

    /**Playlist**/


    /**Track**/
    public Track getTrackById(String trackId){
        return trackHashMap.get(trackId);
    }
    public boolean hasTrack(String trackId){
        return trackHashMap.containsKey(trackId);
    }
    public void addTracks(ArrayList<Track> tracks){
        for(Track track : tracks){
            String trackId = track.getId();
            if(!trackHashMap.containsKey(trackId)){
                trackHashMap.put(trackId,track);
            }
        }
    }
    public ArrayList<Track> getTracksByPlayList(Playlist playlist){
        ArrayList<Track> tracks = new ArrayList<>();
        for(String trackId: playlist.getTracksIds()){
            tracks.add(trackHashMap.get(trackId));
        }
        return tracks;
    }

    public ArrayList<Track> getTracksByAlbumId(String albumId){
        ArrayList<Track> tracks = new ArrayList<>();
        Album album = albumHashMap.get(albumId);
        for(String trackId: album.getTracksIds()){
            tracks.add(trackHashMap.get(trackId));
        }
        return tracks;
    }
    /**Album**/
    public Album getAlbumById(String albumId){
        return albumHashMap.get(albumId);
    }
    public Album getAlbumByTrackId(String trackId){
        Track track = trackHashMap.get(trackId);
        return albumHashMap.get(track.getAlbumId());
    }
    public HashMap<String, Album> getAlbumHashMap(){return albumHashMap;}

    public void addAlbums(ArrayList<Album> albums){
        for(Album album : albums){
            if(!albumHashMap.containsKey(album.getId())){
                albumHashMap.put(album.getId(),album);
            }
        }
    }






}
