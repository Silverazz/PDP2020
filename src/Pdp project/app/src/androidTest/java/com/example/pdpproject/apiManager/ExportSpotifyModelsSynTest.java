package com.example.pdpproject.apiManager;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class ExportSpotifyModelsSynTest {

    @Test
    public void exportModels() throws ExecutionException, InterruptedException {
        String token = "BQDxtQ84NrpT49MVcTITeAHsn7gS-q1ShVx1JAKiH7cO82Gr67So8kfqvWZ20Ho5lnW9syFRP65DCETY1PdDJooJIZFPqgBhT8wM_2GvUPGUJEHEDR0gucF1nRLXDE8UW3rjYgN6tsg0qMxWyXCNriKsUWUcqYt1aUyp1buZv9XKS9xynjTvM7WKdMFYHg";
        APIManagerSpotifySyn exportSpotifyModelsSyn =new APIManagerSpotifySyn();
        exportSpotifyModelsSyn.setRequestToken(token);
        exportSpotifyModelsSyn.exportModels();
        LogMsg.logSpotify();

        //test get top tracks
        ArrayList<Artist> artists = Singleton.getInstance().getArtists();
        for(int i = 0; i< 10 ;i++){
            Artist artist =artists.get(i);
            LogMsg.logAppdata("artist name : " +artist.getName());
            ArrayList<Track> topTrack = exportSpotifyModelsSyn.exportArtistTopTracks(artist.getId(),false);
            LogMsg.logAppdata("top tracks " + topTrack.toString());
            Set<String> albums = exportSpotifyModelsSyn.fetchAlbumsIdsFromArtist(artist.getId(),false);
            LogMsg.logAppdata("albums : " + albums.toString());
        }

        ArrayList<Track> tracks = exportSpotifyModelsSyn.exportTracksFromAlbum("2ODvWsOgouMbaA5xf0RkJe",false);
        Collections.sort(tracks);
        for(Track track : tracks){
            LogMsg.logAppdata("Tracks from album : " + track.getName());
        }

        assertTrue(true);
    }
}