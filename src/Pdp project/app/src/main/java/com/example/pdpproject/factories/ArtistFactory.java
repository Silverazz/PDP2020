package com.example.pdpproject.factories;

import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.modelInterface.ArtistModelItf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArtistFactory {

    public static ArrayList<Artist> getArtists(ArrayList<JSONObject> objects, ArtistModelItf artistModel) {
        ArrayList<Artist> artists = new ArrayList<>();
        for(JSONObject artist : objects){
            try {
                String id = artist.getString("id");
                String name = artist.getString("name");
                Artist new_artist = new Artist(id,name, artistModel);
                artists.add(new_artist);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return artists;
    }

    public static ArrayList<Artist> getArtistsFromTracks(ArrayList<JSONObject> objectsTracks, ArtistModelItf artistModel) {
        ArrayList<Artist> artistArr = new ArrayList<>();
        for(JSONObject track : objectsTracks){
            try {
                JSONArray artists  =  track.getJSONArray("artists");

                for(int i = 0; i<artists.length(); i++) {
                    JSONObject artist = artists.getJSONObject(i);
                    String id = artist.getString("id");
                    String name = artist.getString("name");
                    artistArr.add(new Artist(id,name,artistModel));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return artistArr;
    }

    public static Artist getArtist(JSONObject data, ArtistModelItf artistModel){
        try {
            String id = data.getString("id");
            String name = data.getString("name");
            Artist artist = new Artist(id, name, artistModel);
            return artist;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
