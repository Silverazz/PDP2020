package com.example.pdpproject.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ServiceDeezer {

    public static ArrayList<String> followedArtistFromMe(JSONObject data){
        ArrayList<String> artists = new ArrayList<>();
        try {
            JSONArray arr = data.getJSONArray("data");
            for(int i =0; i<arr.length();i++){
                String id = arr.getJSONObject(i).getString("id");
                artists.add(id);
            }
            return  artists;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * https://api.deezer.com/user/me/playlists?access_token={token}
     * @param object
     * @return
     */
    public static ArrayList<JSONObject> listPlaylistOfUser(JSONObject object){

        JSONArray obj = null;
        try {
            obj = object.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<JSONObject> allPlaylists = new ArrayList<>();

        for(int i=0; i<obj.length(); i++){
            JSONObject playlist;
            String name, id, isPublic;

            try {
                JSONObject item= obj.getJSONObject(i);
                name = item.getString("title");
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
     * https://api.deezer.com/playlist/{id}/tracks
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
            JSONArray items = obj.getJSONArray("data");

            for(int i = 0; i<items.length(); i++){

                String id, name, albumID;
                JSONObject track =  items.getJSONObject(i);

                id = track.getString("id");
                name = track.getString("title_short");
                Integer rank = track.getInt("rank");
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
     * https://api.deezer.com/playlist/{id}/tracks
     * @param obj
     * @return HashMap
     */
    public static HashMap<String, String> artistOfTracks(JSONObject obj){
        HashMap<String,String> trackArtistsMap = new HashMap<>();

        try {
            JSONArray items = obj.getJSONArray("data");
            for(int i = 0; i<items.length(); i++) {

                String trackId;
                JSONObject track = items.getJSONObject(i);
                trackId = track.getString("id");
                JSONObject artistJSON = track.getJSONObject("artist");
                String artistId = artistJSON.getString("id");
                trackArtistsMap.put(trackId, artistId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trackArtistsMap;
    }

    /**
     * https://api.deezer.com/playlist/{id}/tracks
     * @param obj
     * @return HashMap
     */
    public static HashMap<String, JSONObject> albumOfTracks(JSONObject obj){
        HashMap<String,JSONObject> trackAlbumsMap = new HashMap<>();

        try {
            JSONArray items = obj.getJSONArray("data");
            for(int i = 0; i<items.length(); i++) {

                JSONObject track = items.getJSONObject(i);
                JSONObject albumJSON = track.getJSONObject("album");
                String albumId = albumJSON.getString("id");
                String albumName = albumJSON.getString("title");
                String albumImgUrl = albumJSON.getString("cover_big");
                JSONObject album = new JSONObject();
                album.put("id",albumId);
                album.put("name",albumName);
                album.put("imgUrl",albumImgUrl);
                trackAlbumsMap.put(albumId, album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trackAlbumsMap;
    }

    /**
     * https://api.deezer.com/artist/{id}/top
     * @param obj
     * @return
     */
    public static ArrayList<JSONObject> topTracksFromArtist(JSONObject obj){
       return listTracksOfPlaylist(obj);
    }

    /**
     * https://api.deezer.com/artist/{id}/albums
     * @param object
     * @return
     */
    public static Set<String> albumsFromArtist(JSONObject object){
        Set<String> albums = new HashSet<>();
        try {
            JSONArray items = object.getJSONArray("data");
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
            JSONArray items = object.getJSONArray("data");
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
     * Object From https://api.deezer.com/album/{id}/tracks
     * @param object
     * @return
     */
    public static ArrayList<JSONObject> listOfTracks(JSONObject object,String albumID){
        ArrayList<JSONObject> allTracks = new ArrayList<>();

        try {
            JSONArray items = object.getJSONArray("data");

            for(int i = 0; i<items.length(); i++){

                String id, name;
                JSONObject track =  items.getJSONObject(i);

                id = track.getString("id");
                name = track.getString("title");
                Integer rank = track.getInt("rank");

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

    public static ArrayList<String> genresFromAlbum(JSONObject object){
        ArrayList<String> genreArr = new ArrayList<>();
        try {
            JSONArray items = object.getJSONObject("genres").getJSONArray("data");

            for(int i = 0; i<items.length(); i++){
                JSONObject genre =  items.getJSONObject(i);
                String name = genre.getString("name");;
                genreArr.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genreArr;
    }


}
