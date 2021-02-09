package com.example.pdpproject.factories;

import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.modelInterface.TrackModelItf;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrackFactory {

    public static ArrayList<Track> getTracksFromArtist(ArrayList<JSONObject> data, TrackModelItf trackModel) {
//
//        ArrayList<Track> tracks = new ArrayList<>();
//        for (JSONObject track : data) {
//            try {
//                String id = track.getString("id");
//                String name = track.getString("name");
//                String albumID = track.getString("albumID");
//                Track new_track = new Track(id, name, albumID, trackModel);
//                tracks.add(new_track);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return null;
    }

    /**
     * Create Track Model with :
     * track id
     * track name
     * album id
     *
     * @param data
     * @param trackModel
     * @return ArrayList<Track>
     */
    public  static ArrayList<Track> getTracks(ArrayList<JSONObject> data, TrackModelItf trackModel) {
        ArrayList<Track> tracks = new ArrayList<>();
        for(JSONObject track : data){
            try {
                String id = track.getString("id");
                String name = track.getString("name");
                String albumID = track.getString("albumID");
                Integer rank = track.getInt("rank");
                Track new_track = new Track(id,name,albumID,rank, trackModel);
                tracks.add(new_track);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tracks;
    }

}
