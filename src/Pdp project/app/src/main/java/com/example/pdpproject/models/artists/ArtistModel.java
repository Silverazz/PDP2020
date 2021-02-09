package com.example.pdpproject.models.artists;

import com.example.pdpproject.models.Model;
import com.example.pdpproject.models.modelInterface.ArtistModelItf;

public abstract class ArtistModel extends Model {

    public ArtistModel(String id, String name) {
        super(id, name);
    }
    protected abstract ArtistModelItf getMyModelItf();
}
