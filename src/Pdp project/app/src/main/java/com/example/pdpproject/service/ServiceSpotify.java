package com.example.pdpproject.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ServiceSpotify {
    /**
     *  https://api.spotify.com/v1/users/{user_id}
     * @param object return from spotify api
     * @return a JSONObject that contains id, display_name
     */
    public static JSONObject infoOfUser(JSONObject object){
        JSONObject userInfosParsed = new JSONObject();

        String ID = null;
        String name = null;
        String platform = null;

        try {
            ID = object.getString("id");
            name = object.getString("display_name");

            userInfosParsed.put("id",ID);
            userInfosParsed.put("name",name);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userInfosParsed;
    }

    /**
     * https://api.spotify.com/v1/playlists/{playlist_id}
     * @param object
     * @return
     */
    public static ArrayList<JSONObject> listPlaylistOfUser(JSONObject object){

        JSONArray obj = null;
        try {
            obj = object.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<JSONObject> allPlaylists = new ArrayList<>();

        for(int i=0; i<obj.length(); i++){
            JSONObject playlist;
            String name, id, isPublic;

            try {
                JSONObject item= obj.getJSONObject(i);
                name = item.getString("name");
                id = item.getString("id");
                isPublic = item.getString("public");


                playlist = new JSONObject();
                playlist.put("name",name);
                playlist.put("id",id);
                playlist.put("isPublic",isPublic);
                allPlaylists.add(playlist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return allPlaylists;
    }

    /**
     * https://api.spotify.com/v1/playlists/{playlist_id}/tracks
     * Get the wanted data below :
     * track id
     * track name
     * album id of track
     * JSONArray of artist object
     * @param obj
     * @return ArrayList<JSONObject> of track object
     */
    public static ArrayList<JSONObject> listTracksOfPlaylist(JSONObject obj){
        ArrayList<JSONObject> allTracks = new ArrayList<>();

        try {
            JSONArray items = obj.getJSONArray("items");

            for(int i = 0; i<items.length(); i++){

                String id, name, albumID;
                JSONObject track =  items.getJSONObject(i).getJSONObject("track");

                id = track.getString("id");
                name = track.getString("name");
                Integer rank = track.getInt("popularity");
                JSONObject album = track.getJSONObject("album");
                albumID = album.getString("id");

                JSONObject newTrack = new JSONObject();
                newTrack.put("id",id);
                newTrack.put("name",name);
                newTrack.put("albumID",albumID);
                newTrack.put("rank",rank);
                allTracks.add(newTrack);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allTracks;
    }

    public static ArrayList<String> genresFromArtist(JSONObject object){
        ArrayList<String> genresArr = new ArrayList<>();
        try {
            JSONArray genres = object.getJSONArray("genres");
            for (int i =0;i<genres.length();i++){
                String g = genres.getString(i);
                genresArr.add(g);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genresArr;
    }

    /**
     * https://api.spotify.com/v1/me/following?type=artist
     * @param object
     * @return
     */
    public static JSONArray followedArtistFromMe(JSONObject object){
        JSONArray followedArtist = new JSONArray();
        try {
            JSONArray artists = object.getJSONObject("artists").getJSONArray("items");
            for(int i =0;i < artists.length();i++){
                JSONObject artist = artists.getJSONObject(i);
                String id = artist.getString("id");
                String name = artist.getString("name");
                JSONArray genres = artist.getJSONArray("genres");
                ArrayList<String> g = new ArrayList<>();
                for(int j =0; j<genres.length();j++){
                    g.add(genres.getString(i));
                }
                JSONObject data = new JSONObject();
                data.put("id",id);
                data.put("name",name);
                data.put("genres",g);
                followedArtist.put(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return followedArtist;
    }

    /**
     * https://api.spotify.com/v1/playlists/{playlist_id}/tracks
     * @param obj
     * @return
     */
    public static HashMap<String, ArrayList<String>> listArtistsOfTracks(JSONObject obj){
        HashMap<String, ArrayList<String>> trackArtistsMap = new HashMap<>();

        try {
            JSONArray items = obj.getJSONArray("items");
            for(int i = 0; i<items.length(); i++) {

                String trackId;
                JSONObject track = items.getJSONObject(i).getJSONObject("track");
                trackId = track.getString("id");
                JSONArray artistJsonArr = track.getJSONArray("artists");
                ArrayList<String> artistMap = new ArrayList<>();

                for (int j = 0; j < artistJsonArr.length(); j++) {
                    String artistId = artistJsonArr.getJSONObject(j).getString("id");
                    artistMap.add(artistId);
                }
                trackArtistsMap.put(trackId, artistMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trackArtistsMap;
    }

    /**
     * https://api.spotify.com/v1/playlists/{playlist_id}/tracks
     * @param obj
     * @return
     */
    public static HashMap<String,JSONObject> listAlbumsOfTracks(JSONObject obj){
        HashMap<String, JSONObject> albumMap = new HashMap<>();
        try {
            JSONArray items = obj.getJSONArray("items");
            for(int i = 0; i<items.length(); i++) {

                JSONObject track = items.getJSONObject(i).getJSONObject("track");
                JSONObject albumJson =track.getJSONObject("album");
                JSONObject album = new JSONObject();
                String id =albumJson.getString("id");
                album.put("id",id);
                album.put("name",albumJson.getString("name"));
                album.put("imgUrl",albumJson.getJSONArray("images").getJSONObject(0).getString("url"));
                albumMap.put(id,album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albumMap;
    }

    public static JSONObject featuresOfTrack(JSONObject object){
        JSONObject features = new JSONObject();
        try {
            double acousticness = object.getDouble("acousticness");
            double danceability = object.getDouble("danceability");
            double energy = object.getDouble("energy");
            double speechiness = object.getDouble("speechiness");
            double valence = object.getDouble("valence");
            features.put("acousticness",acousticness);
            features.put("danceability",danceability);
            features.put("energy",energy);
            features.put("speechiness",speechiness);
            features.put("valence",valence);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return features;
    }

    /**
     * https://api.spotify.com/v1/artists/{id}/top-tracks
     * @param obj
     * @return
     */
    public static ArrayList<JSONObject> topTracksFromArtist(JSONObject obj){
        ArrayList<JSONObject> allTracks = new ArrayList<>();

        try {
            JSONArray items = obj.getJSONArray("tracks");

            for(int i = 0; i<items.length(); i++){

                String id, name, albumID;
                JSONObject track =  items.getJSONObject(i);

                id = track.getString("id");
                name = track.getString("name");
                Integer rank = track.getInt("popularity");
                JSONObject album = track.getJSONObject("album");
                albumID = album.getString("id");

                JSONObject newTrack = new JSONObject();
                newTrack.put("id",id);
                newTrack.put("name",name);
                newTrack.put("albumID",albumID);
                newTrack.put("rank",rank);
                allTracks.add(newTrack);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allTracks;
    }

    /**
     * https://api.spotify.com/v1/artists/{id}/albums
     * @param object
     * @return
     */
    public static Set<String> albumsFromArtist(JSONObject object){
        Set<String> albums = new HashSet<>();
        try {
            JSONArray items = object.getJSONArray("items");
            for(int i = 0 ; i<items.length();i++){
                JSONObject albumJson = items.getJSONObject(i);
                String id = albumJson.getString("id");
                albums.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return albums;

    }

    public static ArrayList<JSONObject> relatedArtists(JSONObject object){
        ArrayList<JSONObject> relatedArtists = new ArrayList<>();
        try {
            JSONArray items = object.getJSONArray("artists");
            for(int i = 0; i<items.length(); i++) {
                String id, name;
                JSONObject artist = items.getJSONObject(i);
                id =artist.getString("id");
                name =artist.getString("name");
                JSONObject newArtist = new JSONObject();
                newArtist.put("id",id);
                newArtist.put("name",name);
                relatedArtists.add(newArtist);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return relatedArtists;
    }

    /**
     * https://api.spotify.com/v1/albums
     * @param object
     * @return
     */
    public static Set<String> tracksIdFromAlbum(JSONObject object){
        Set<String> ids = new HashSet<>();
        try {
            JSONArray items = object.getJSONArray("items");
            for(int i = 0 ; i<items.length();i++){
                String id = items.getJSONObject(i).getString("id");
                ids.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * Object From https://api.spotify.com/v1/tracks
     * @param object
     * @return
     */
    public static ArrayList<JSONObject> listOfTracks(JSONObject object){
        ArrayList<JSONObject> allTracks = new ArrayList<>();

        try {
            JSONArray items = object.getJSONArray("tracks");

            for(int i = 0; i<items.length(); i++){

                String id, name, albumID;
                JSONObject track =  items.getJSONObject(i);

                id = track.getString("id");
                name = track.getString("name");
                Integer rank = track.getInt("popularity");
                JSONObject album = track.getJSONObject("album");
                albumID = album.getString("id");

                JSONObject newTrack = new JSONObject();
                newTrack.put("id",id);
                newTrack.put("name",name);
                newTrack.put("albumID",albumID);
                newTrack.put("rank",rank);
                allTracks.add(newTrack);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allTracks;
    }




}
