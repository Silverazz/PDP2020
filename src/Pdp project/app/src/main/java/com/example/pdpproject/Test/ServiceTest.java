package com.example.pdpproject.Test;

import com.example.pdpproject.algorithms.AdditiveUtilitarianStrategy;
import com.example.pdpproject.algorithms.AverageWithoutMisery;
import com.example.pdpproject.algorithms.BordaCount;
import com.example.pdpproject.algorithms.GenerateScore;
import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.artists.ArtistSpotify;
import com.example.pdpproject.models.modelInterface.AlbumModelItf;
import com.example.pdpproject.models.modelInterface.TrackModelItf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ServiceTest {

    public static void displayTracks(ArrayList<Track> tracks, boolean verbose){
        Iterator<Track> iter = tracks.iterator();
        HashMap<String,Integer> countArtist = new HashMap<>();
        int nbTopArtist = 0;
        String topArtist = "";
        while(iter.hasNext()){
            Track tr = (Track) iter.next();
            if(verbose)
                System.out.println("track --> " + tr.getName());

            Iterator<String> artistIter = tr.getArtistsIds().iterator();
            while (artistIter.hasNext()){
                String artist = (String) artistIter.next();
                if(countArtist.containsKey(artist)){
                    Integer val = countArtist.get(artist) + 1;
                    countArtist.remove(artist);
                    countArtist.put(artist,val);
                    if(val > nbTopArtist){
                        nbTopArtist = val;
                        topArtist = artist;
                    }
                }
                else {
                    countArtist.put(artist, 1);
                }
                if(verbose)
                    System.out.println("     artist --> " + artist);
            }
        }
        Set<String> artists = countArtist.keySet();
        for(String artist : artists){
            System.out.println("Nb tracks related to " + artist + " is " + countArtist.get(artist));
        }
        System.out.println("Top artist is " + topArtist + " and has " + (nbTopArtist*100/tracks.size()) + "% tracks in the playlist");
        System.out.println();
    }

    public static void testsAlgos(GenerateScore score, int nbTracks,boolean verbose){
        System.out.println("AdditiveUtilitarianStrategy");
        ArrayList<Track> listOfTracks = score.generatePlaylist(new AdditiveUtilitarianStrategy(),nbTracks);
        displayTracks(listOfTracks,verbose);
        System.out.println("BordaCount");
        listOfTracks = score.generatePlaylist(new BordaCount(),nbTracks);
        displayTracks(listOfTracks,verbose);
        //System.out.println("AverageWithoutMisery");
        //listOfTracks = score.generatePlaylist(new AverageWithoutMisery(),nbTracks);
        //displayTracks(listOfTracks,verbose);
    }

    public static Album createAlbum(String artiste, AlbumModelItf itf, TrackModelItf itf2){
        Album album = new Album(artiste ,artiste + " best OF","none", itf);
        ArrayList<String> track = new ArrayList<>();
        for(int i = 1; i < 99; i++) {
            Track tr = new Track("Single n" + i + " of " + artiste,
                    "Single n" + i +" of " + artiste, album.getName(), 10, itf2);
            ArrayList<Artist> art = new ArrayList<>();
            art.add(new Artist(artiste,artiste,new ArtistSpotify()));
            tr.setArtists(art);

            track.add(tr.getName());
        }
        album.setTracksIds(track);
        return album;
    }
}
