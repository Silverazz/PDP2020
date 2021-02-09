package com.example.pdpproject.apiManager.requests;

import android.app.Activity;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.activities.DeezerLogin;
import com.example.pdpproject.apiManager.APIManagerDeezer;
import com.example.pdpproject.apiManager.ExportCallbackO;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class APIManagerDeezerTest {

    @Test
    public void exportModels() throws InterruptedException {
        String token = "frYCE7wdTziNbfPQ5LnSCS4xBg2rJv3pLzWxubM0XXyUeeOt8N";
        Singleton singleton = Singleton.getInstance();
        singleton.setMainAccessToken(token);
        APIManagerDeezer apiManagerDeezer = new APIManagerDeezer();
        apiManagerDeezer.setRequestToken(token);
        apiManagerDeezer.exportModels();
        LogMsg.logDeezer();

        DeezerRequest deezerRequest = new DeezerRequest();
        deezerRequest.setToken(token);
        String playlist = "7342008944";
        String albumId = "101516602";
        //test get top tracks
        ArrayList<Artist> artists = Singleton.getInstance().getArtists();
        for(int i = 0; i< artists.size() ;i++){
            Artist artist =artists.get(i);
            LogMsg.logAppdata("artist name : " +artist.getName());
            ArrayList<Track>topTrack = apiManagerDeezer.exportArtistTopTracks(artist.getId(),false);
            LogMsg.logAppdata("top tracks " + topTrack.toString());
            Set<String> albums = apiManagerDeezer.fetchAlbumsIdsFromArtist(artist.getId(),false);
            LogMsg.logAppdata("albums : " + albums.toString());
        }



        ArrayList<Track> tracks = apiManagerDeezer.exportTracksFromAlbum(albumId,false);
        Collections.sort(tracks);
        for(Track track : tracks){
            LogMsg.logAppdata("Tracks from album : " + track.getName());
        }

        ArrayList<Artist> relatedArtists = apiManagerDeezer.exportRelatedArtists("4050205",false);
        for (Artist artist :relatedArtists){
            LogMsg.logAppdata("related artist : " +artist.getName());
        }


        assertTrue(true);

    }
}