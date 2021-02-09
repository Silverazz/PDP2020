package com.example.pdpproject.models.playlists;

import com.example.pdpproject.models.Model;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.modelInterface.PlayListModelItf;

import java.util.ArrayList;

public abstract class PlaylistModel extends Model {
    private ArrayList<String> tracksIds;

    public PlaylistModel(String id, String name) {
        super(id, name);
        tracksIds = new ArrayList<>();
    }

    public ArrayList<String> getTracksIds() {
        return tracksIds;
    }
    public void setTracksIds(ArrayList<Track> tracks) {
        for(Track track :tracks){
            this.tracksIds.add(track.getId());
        }
    }
    public abstract PlayListModelItf getMyModelItf();
}
