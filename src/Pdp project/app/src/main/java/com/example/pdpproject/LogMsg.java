package com.example.pdpproject;

import android.util.Log;

import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.User;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.repo.Singleton;

import java.util.ArrayList;

public class LogMsg {

    public static void  logAppdata(String msg){
        Log.d("appdata",msg);
    }

    public static void logSpotify(){
        Singleton singleton = Singleton.getInstance();
        Log.d("appdata","My Main token : " + singleton.getMainAccessToken());
        Log.d("appdata","My Refresh token : " + singleton.getRefreshToken());

        for (User user : singleton.getAllUser()){
            LogMsg.logAppdata("user artist map :" + user.getArtistsIdMap().toString());
//            LogMsg.logAppdata("FollowedArtists :" + user.getFollowedArtistsIds().toString());
            LogMsg.logAppdata("user genre map :" +user.getGenreMap().toString());
            for(Playlist playlist : user.getPlaylists()){
                Log.d("appdata"," Playlist name : " + playlist.getName());
                Log.d("appdata"," Playlist size : " + playlist.getTracksIds().size());
                for(Track track: singleton.getTracksByPlayList(playlist)){
                    LogMsg.logAppdata("name track  :" + track.getName());
                    LogMsg.logAppdata("track rank  :" + track.getRank());
                    double acousticness = ((TrackSpotify) track.getMyModelItf() ) .getAcousticness();
                    double danceability = ((TrackSpotify) track.getMyModelItf() ) .getDanceability();
                    double energy =  ((TrackSpotify) track.getMyModelItf() ) .getEnergy();
                    double speechiness =  ((TrackSpotify) track.getMyModelItf() ) .getSpeechiness();
                    double valence =  ((TrackSpotify) track.getMyModelItf() ) .getValence();
                    LogMsg.logAppdata("Features" + " acous : " +acousticness +", danceability : "+ danceability
                                        + ", energy : " + energy + ", speechiness : " +speechiness +", valence : "+ valence);
                    for(Artist artist :singleton.getArtistsByTrackId(track.getId()) ){
                        LogMsg.logAppdata(" -name artist : " + artist.getName());
                    }
                }
            }

        }
    }

    public static void logDeezer(){
        Singleton singleton = Singleton.getInstance();
        Log.d("appdata","My Main token : " + singleton.getMainAccessToken());

        for (User user : singleton.getAllUser()){
            LogMsg.logAppdata("user artist map :" + user.getArtistsIdMap().toString());
            LogMsg.logAppdata("FollowedArtists :" + user.getFollowedArtistsIds().toString());
            LogMsg.logAppdata("user genre map :" +user.getGenreMap().toString());
            for(Playlist playlist : user.getPlaylists()){
                Log.d("appdata"," Playlist name : " + playlist.getName());
                Log.d("appdata"," Playlist size : " + playlist.getTracksIds().size());
                for(Track track: singleton.getTracksByPlayList(playlist)){
                    LogMsg.logAppdata("name track  :" + track.getName());
                    LogMsg.logAppdata("track rank  :" + track.getRank());
                    for(Artist artist :singleton.getArtistsByTrackId(track.getId()) ){
                        LogMsg.logAppdata(" -name artist : " + artist.getName());
                    }
                }
            }

        }
    }

}
