package com.example.pdpproject.apiManager;

import com.example.pdpproject.LogMsg;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExportSpotifyModelsPerformanceTest {

    @Test
    public void exportModels() {
        String token = "BQBkVU6EjuzEsTEuEsWVIQ4UBSitznNto2w1WNjdNcjMpqCBmjAOK9o6zDscqrLiKi6eAPqdfvJgN38LV1l0gKWnDSkattBaSW57UPnLLiBJHr5327Re6sWdmPchhP6TMj0-LZsFA6vuVmajq1UyNnqSVZ0TQPT2";

        long startTimeSyn = System.currentTimeMillis();
        APIManagerSpotifySyn exportSpotifyModelsSyn =new APIManagerSpotifySyn();
        exportSpotifyModelsSyn.setRequestToken(token);
        exportSpotifyModelsSyn.exportModels();
        LogMsg.logSpotify();

        long endTimeSyn = System.currentTimeMillis();
//        boolean clear = Singleton.getInstance().clearAll();

        long startTimeAsyn = System.currentTimeMillis();

        APIManagerSpotifyAsyn exportSpotifyModelsAsyn  =new APIManagerSpotifyAsyn();
        exportSpotifyModelsAsyn.setRequestToken(token);
        exportSpotifyModelsAsyn.exportModels();
        LogMsg.logSpotify();
        long endTimeAsyn = System.currentTimeMillis();

        long syn = endTimeSyn - startTimeSyn;
        long asyn = endTimeAsyn - startTimeAsyn;
        LogMsg.logAppdata("Syn time : " + syn );
        LogMsg.logAppdata("Asyn time : " + asyn );
        assertTrue(asyn<syn);
    }
}