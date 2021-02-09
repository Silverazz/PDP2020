package com.example.pdpproject.factories;

import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.modelInterface.AlbumModelItf;

import org.json.JSONException;
import org.json.JSONObject;

public class AlbumFactory {

    public static Album getAlbum(JSONObject albumJson, AlbumModelItf albumModel){
        String id = null;
        try {
            id = albumJson.getString("id");
            String name = albumJson.getString("name");
            String imgUrl = albumJson.getString("imgUrl");
            return new Album(id, name, imgUrl, albumModel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
