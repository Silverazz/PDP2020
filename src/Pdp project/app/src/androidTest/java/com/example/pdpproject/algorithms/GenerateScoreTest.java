package com.example.pdpproject.algorithms;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.apiManager.APIManagerDeezer;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GenerateScoreTest {

    @Test
    public void generatePlaylist() {

        String token = "frYCE7wdTziNbfPQ5LnSCS4xBg2rJv3pLzWxubM0XXyUeeOt8N";
        Singleton singleton = Singleton.getInstance();
        singleton.setMainAccessToken(token);
        APIManagerDeezer apiManagerDeezer = new APIManagerDeezer();
        apiManagerDeezer.setRequestToken(token);
        apiManagerDeezer.exportModels();
        GenerateScore generateScore = new GenerateScore(apiManagerDeezer);

        ArrayList<Track> tracks = generateScore.generatePlaylist(new AdditiveUtilitarianStrategy(),50);
        LogMsg.logAppdata("tracks size " + tracks.size());
        for(Track track : tracks){
            LogMsg.logAppdata("Track : " + track.getName());
        }
        assertNotEquals(0,tracks.size());

    }
}