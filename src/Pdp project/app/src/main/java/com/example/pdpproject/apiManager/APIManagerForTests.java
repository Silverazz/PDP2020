package com.example.pdpproject.apiManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.repo.Singleton;

public class APIManagerForTests implements APIManagerItf {

    private Singleton singleton = Singleton.getInstance();

    public APIManagerForTests(){    }

    public void exportModels() {
        return;
    }

    public ArrayList<Track> exportArtistTopTracks(String artistId, boolean hasRefreshed) {
        ArrayList<Track> tracks = new ArrayList<>();

        for(Track track : singleton.getTrackHashMap().values()) {
            if (track.getArtistsIds().contains(artistId)) {
                tracks.add(track);
            }
        }
        return tracks;
    }

    public Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed) {
    	Set<String> s = new HashSet<>();
    	for(Map.Entry<String, Album> entry : singleton.getAlbumHashMap().entrySet()) {
    		for(Track t : exportTracksFromAlbum(entry.getKey(), hasRefreshed)) {
    			if(t.getArtistsIds().contains(artistId)) {
    				s.add(entry.getValue().getId());
    				break;
    			}
    		}
    	}
        return s;
    }

    public ArrayList<Track> exportTracksFromAlbum(String albumId, boolean hasRefreshed) {
        ArrayList<Track> tracks = new ArrayList<>();
        Album alb = singleton.getAlbumHashMap().get(albumId);
        for(String track : alb.getTracksIds()){
            Track tr = new Track(track,track,albumId, 10, new TrackSpotify());
            String artistID = tr.getName().substring(13);
            while (artistID.substring(0,1).equals(" ")){
                artistID = artistID.substring(1);
            }
            tr.addArtistId(artistID);
            tracks.add(tr);
        }
        return tracks;
    }

    @Override
    public String createPlaylist(String name, boolean hasRefreshed) {
        return null;
    }

    public boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed) {
        singleton.addTracks(tracks);
        return true;
    }

    public ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed) {
        ArrayList<Artist> artists = new ArrayList<>();
        return artists;
    }


    public void setRequestToken(String token) {
        return;
    }

}
