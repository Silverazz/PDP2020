package com.example.pdpproject.models.users;

import com.example.pdpproject.models.Model;
import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.modelInterface.UserModelItf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class UserModel extends Model {
    private ArrayList<Playlist> playlists;
    private HashMap<String, Float> genreMap;
    private ArrayList<String> followedArtistsIds;
    private HashMap<String,Integer> artistsIdMap;

    public UserModel(String id, String name) {
        super(id, name);
        playlists = new ArrayList<>();
        genreMap = new HashMap<>();
        followedArtistsIds = new ArrayList<>();
        artistsIdMap = new HashMap<>();
    }

    public final ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = new ArrayList<>(playlists);
    }

    public final ArrayList<String> getFollowedArtistsIds() {
        return followedArtistsIds;
    }

    public void setFollowedArtistsIds(ArrayList<String> followedArtistsIds) {
        this.followedArtistsIds = followedArtistsIds;
    }
    public void addFollowedArtist(String followedArtistId){
        this.followedArtistsIds.add(followedArtistId);
    }

    /**
     * Get the listened Artists Id
     * @return List of Artist Id
     */
    public final ArrayList<String> getArtistsId() {
        ArrayList<String> artistsId = new ArrayList<>();
        for(Map.Entry artist : artistsIdMap.entrySet()){
            for(int i = 0; i < (Integer) artist.getValue(); i++){
                artistsId.add((String) artist.getKey());
            }
        }
        return artistsId;
    }

    public void addArtist(String artistId){
        if(!artistsIdMap.containsKey(artistId)){
            artistsIdMap.put(artistId,1);
        }
        else{
            int currentCount = artistsIdMap.get(artistId);
            artistsIdMap.put(artistId,currentCount+1);
        }

    }

    public final HashMap<String, Float> getGenreMap() {
        return genreMap;
    }
    public final HashMap<String, Integer> getArtistsIdMap() {
        return artistsIdMap;
    }


    public void updateGenreMap(String genre,float coeff){
        if (genreMap.containsKey(genre)){
            float currentCount = genreMap.get(genre);
            genreMap.put(genre,currentCount+coeff);
        }else{
            genreMap.put(genre,coeff);
        }
    }

    public void addGenres(ArrayList<String> genres, float coeff){
        for (String genre : genres){
            updateGenreMap(genre,coeff);
        }
    }

    public abstract UserModelItf getMyModelItf();
}
