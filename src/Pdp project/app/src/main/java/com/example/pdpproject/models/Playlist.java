package com.example.pdpproject.models;

import com.example.pdpproject.models.modelInterface.PlayListModelItf;
import com.example.pdpproject.models.playlists.PlaylistModel;


public class Playlist extends PlaylistModel implements PlayListModelItf {
    private PlayListModelItf playlistItf;
    public Playlist(String id, String name, PlayListModelItf playlistItf){
        super(id,name);
        this.playlistItf = playlistItf;

    }


    @Override
    public PlayListModelItf getMyModelItf() {
        return playlistItf;
    }
}
