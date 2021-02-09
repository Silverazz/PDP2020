package com.example.pdpproject.models.albums;

import com.example.pdpproject.models.Model;
import com.example.pdpproject.models.modelInterface.AlbumModelItf;

import java.util.ArrayList;

public abstract class AlbumModel extends Model {
    private ArrayList<String> tracksIds;
    private String imgUrl;

    public AlbumModel(String id, String name, String imgUrl ) {
        super(id, name);
        tracksIds = new ArrayList<>();
        this.imgUrl =imgUrl;
    }

    public ArrayList<String> getTracksIds() {
        return tracksIds;
    }

    public void setTracksIds(ArrayList<String> tracksIds) {
        this.tracksIds = tracksIds;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public abstract AlbumModelItf getMyModelItf();
}
