package com.example.pdpproject.models;

import android.os.Build;

import com.example.pdpproject.LogMsg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.RequiresApi;

public class Logs {
    private ArrayList<Track> tracks;
    private HashMap<String, Integer> artistScore;
    private String algorithme;
    private int nbSkip;
    private String topArtist;
    private File fileCSV;
    private File fileJSON;

    public Logs (File csv, File json, String algo){
        tracks = new ArrayList<>();
        artistScore = new HashMap<>();
        topArtist = null;
        algorithme = algo;
        nbSkip = 0;

        //LogMsg.logAppdata("file is at " + csv.getPath());
        //String[] csvPath = csv.getAbsolutePath().split(".");
        //String[] jsonPath = json.getAbsolutePath().split(".");
        //LogMsg.logAppdata("extension is     :   " + csvPath[0]);
        //if(csvPath[1].equals("csv"))
            fileCSV = csv;
        //if(jsonPath[1].equals("txt"))
            fileJSON = json;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addTrack(Track track){
        tracks.add(track);
        ArrayList<String> artists = track.getArtistsIds();
        Iterator<String> iter = artists.iterator();
        while(iter.hasNext()){
            String a = iter.next();
            if(artistScore.containsKey(a)) {
                Integer value = artistScore.get(a) + 1;
                artistScore.replace(a,artistScore.get(a), value);
            }
            else {
                artistScore.put(a, new Integer(1));
            }
        }
        setTopArtist();
    }

    private void setTopArtist(){
        int scoreMax = 0;
        Set keys = artistScore.keySet();

        Iterator iter = keys.iterator();
        while(iter.hasNext()){
            String a = (String) iter.next();

            if(scoreMax < artistScore.get(a)) {
                scoreMax = artistScore.get(a);
                this.topArtist = a;
            }
        }
    }

    public String getTopArtist() {
        return topArtist;
    }

    public ArrayList<Track> getAllTracks() {
        return tracks;
    }

    public File getFileCSV(){
        return fileCSV;
    }

    public File getFileJSON(){
        return fileJSON;
    }

    public String getAlgorithme() { return algorithme; }

    public void hasSkipped() { nbSkip++; }

    public int getNbSkip() { return nbSkip;  }
}
