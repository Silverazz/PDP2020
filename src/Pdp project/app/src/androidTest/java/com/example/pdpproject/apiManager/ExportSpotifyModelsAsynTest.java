package com.example.pdpproject.apiManager;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class ExportSpotifyModelsAsynTest {

    @Test
    public void exportModels() throws InterruptedException {
        String token = "BQB-tWObcls65M6h6Y3427yNfmcH2i3DVQUhhS2O8Mnp_GVHQp64aBVNGVqFpytADHyvA7J55M8D_li5ajgVTeuEKd3QroWaGaxnL0JIhf1IDUb3DjAbIt9W2bNJJ_g6Kto0uqQ7rZa4rhGqzxjpiJYwEaEwZ_5T";
        APIManagerSpotifyAsyn exportSpotifyModelsAsyn  =new APIManagerSpotifyAsyn();
        exportSpotifyModelsAsyn.setRequestToken(token);
        exportSpotifyModelsAsyn.exportModels();
        LogMsg.logSpotify();

        //test get top tracks
        ArrayList<Artist> artists = Singleton.getInstance().getArtists();
        for(int i = 0; i< 10 ;i++){
            Artist artist =artists.get(i);
            LogMsg.logAppdata("artist name : " +artist.getName());
            ArrayList<Track> topTrack = exportSpotifyModelsAsyn.exportArtistTopTracks(artist.getId(),false);
            LogMsg.logAppdata("top tracks " + topTrack.toString());

            Set<String> albums = exportSpotifyModelsAsyn.fetchAlbumsIdsFromArtist(artist.getId(),false);
            LogMsg.logAppdata("albums : " + albums.toString());
        }

        ArrayList<Track> tracks = exportSpotifyModelsAsyn.exportTracksFromAlbum("2ODvWsOgouMbaA5xf0RkJe",false);
        Collections.sort(tracks);
        for(Track track : tracks){
            LogMsg.logAppdata("Tracks from album : " + track.getName());
        }



        assertTrue(true);
    }
}