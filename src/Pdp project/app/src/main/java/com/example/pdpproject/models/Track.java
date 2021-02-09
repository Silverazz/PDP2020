package com.example.pdpproject.models;

import com.example.pdpproject.models.modelInterface.TrackModelItf;
import com.example.pdpproject.models.tracks.TrackModel;


import java.util.ArrayList;

public class Track extends TrackModel implements TrackModelItf {
    private TrackModelItf trackItf;

    public Track(String id, String name,Integer rank, TrackModelItf trackItf) {
        super(id, name,rank);
        this.trackItf = trackItf;
    }

    public Track(String id, String name, String albumId, Integer rank, TrackModelItf trackItf) {
        super(id, name, albumId,rank);
        this.trackItf = trackItf;
    }


    @Override
    public ArrayList<Double> getScores() {
        return trackItf.getScores();
    }

    @Override
    public TrackModelItf getMyModelItf() {
        return trackItf;
    }
}
