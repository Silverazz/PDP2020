package com.example.pdpproject.apiManager;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import org.json.JSONException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class refreshTokenAsynTest {
    /**
     * token and refresh token must manually set
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void exportModels() throws ExecutionException, InterruptedException, JSONException {
        String token ="BQD-h1yX9Xbe-IcVOu1czDV15i-5C5kYcpHRyho5i_kRvf3TaJ7-bDYrobO2fB7t5Vsl5yrpJNxpCzM61kI0wPVySuWFTuzuvMQm1DJR-v99bfLq-d4kT-KBtp7yDnFCcDzB2vklAwwdRB7gn60wrEa7ZYKmDkZXZYpGLzyVss44Jq6UeSVk1qizbOIbOQ";
        String refreshToken = "AQDfLoBRiQfkRP4KYYpQGiGr7EJdZu6L_FDKiWzH6Plfi_buIol7ie9l59Ck3BxJptnVhW6_qjJiUPKFlOp1jTaBzAjYBdjLt1taAxjf6LUUMpeogrjMXScdwjdnsTEri3o";
        Singleton.getInstance().setRefreshToken(refreshToken);

        APIManagerSpotifyAsyn apiManagerSpotifyAsyn  =new APIManagerSpotifyAsyn();
        apiManagerSpotifyAsyn.setRequestToken(token);
        apiManagerSpotifyAsyn.exportModels();
        LogMsg.logSpotify();

        // make main access token expire
        Singleton.getInstance().setMainAccessToken("123");

        //test get top tracks
        ArrayList<Artist> artists = Singleton.getInstance().getArtists();
        for(int i = 0; i< 10 ;i++){
            Artist artist =artists.get(i);
            LogMsg.logAppdata("artist name : " +artist.getName());
            ArrayList<Track> topTrack = apiManagerSpotifyAsyn.exportArtistTopTracks(artist.getId(),false);
            LogMsg.logAppdata("top tracks " + topTrack.toString());
            if(i==0) Singleton.getInstance().setMainAccessToken("123");
            Set<String> albums = apiManagerSpotifyAsyn.fetchAlbumsIdsFromArtist(artist.getId(),false);
            LogMsg.logAppdata("albums : " + albums.toString());
        }
        Singleton.getInstance().setMainAccessToken("123");
        ArrayList<Track> tracks = apiManagerSpotifyAsyn.exportTracksFromAlbum("2ODvWsOgouMbaA5xf0RkJe",false);
        Collections.sort(tracks);
        for(Track track : tracks){
            LogMsg.logAppdata("Tracks from album : " + track.getName());
        }
        Singleton.getInstance().setMainAccessToken("123");
        apiManagerSpotifyAsyn.addTracksToAppPlaylist(tracks,false);

        ArrayList<Artist> relatedArtists = apiManagerSpotifyAsyn.exportRelatedArtists("1Xyo4u8uXC1ZmMpatF05PJ",false);
        for (Artist artist :relatedArtists){
            LogMsg.logAppdata("related artist : " +artist.getName());
        }

        assertEquals(tracks.size(),18);
    }
}