package com.example.pdpproject.factories;

import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.modelInterface.PlayListModelItf;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaylistFactory {

    public static ArrayList<Playlist> getPlaylists(ArrayList<JSONObject> Playlists, PlayListModelItf playlistModel) {
        ArrayList<Playlist> playlists = new ArrayList<>();
        for(JSONObject playlist : Playlists){
            try {
                String id = playlist.getString("id");
                if(playlist.getString("isPublic").equals("true")){
                    String name = playlist.getString("name");
                    Playlist new_playlist = new Playlist(id,name, playlistModel);
                    playlists.add(new_playlist);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return playlists;
    }
}