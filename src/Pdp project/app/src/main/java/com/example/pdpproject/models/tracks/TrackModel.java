package com.example.pdpproject.models.tracks;

import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Model;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.modelInterface.TrackModelItf;

import java.util.ArrayList;

public abstract class TrackModel extends Model implements Comparable{
    private String albumId;
    private ArrayList<String> artistsIds;
    private int rank;

    public TrackModel(String id, String name, Integer rank){
        super(id,name);
        this.rank =rank;
        artistsIds = new ArrayList<>();
    }

    public TrackModel(String id, String name, String albumId, Integer rank){
        super(id,name);
        this.albumId = albumId;
        this.rank =rank;
        artistsIds = new ArrayList<>();

    }


    public String getAlbumId() {
        return albumId;

    }

    public final ArrayList<String> getArtistsIds() {
        return artistsIds;
    }

    public void setArtists(ArrayList<Artist> artists) {
        for(Artist artist :artists){
            if(!this.artistsIds.contains(artist.getId())){
                this.artistsIds.add(artist.getId());
            }
        }
    }

    public void  addArtistId(String artistId){
        if(!this.artistsIds.contains(artistId)){
            this.artistsIds.add(artistId);
        }
    }

    public int getRank() {
        return rank;
    }


    @Override
    public int compareTo(Object track) {
        int comparage = ((Track)track).getRank();
        return comparage -this.rank;
    }

    public abstract TrackModelItf getMyModelItf();
}
