package com.example.pdpproject.models;

import com.example.pdpproject.models.artists.ArtistModel;
import com.example.pdpproject.models.modelInterface.ArtistModelItf;

import org.json.JSONObject;

public class Artist extends ArtistModel implements ArtistModelItf {
    ArtistModelItf artistItf;

    public Artist(String id, String name, ArtistModelItf
            artistItf){
        super(id,name);
        this.artistItf = artistItf;
    }

    @Override
    public ArtistModelItf getMyModelItf() {
        return artistItf;
    }
}
