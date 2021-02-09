package com.example.pdpproject.models;

import com.example.pdpproject.models.albums.AlbumModel;
import com.example.pdpproject.models.modelInterface.AlbumModelItf;


public class Album extends AlbumModel implements AlbumModelItf {
    private AlbumModelItf albumItf;

    public Album(String id, String name, String imgUrl, AlbumModelItf albumItf){
        super(id,name,imgUrl);
        this.albumItf = albumItf;
    }


    @Override
    public AlbumModelItf getMyModelItf() {
        return albumItf;
    }
}
