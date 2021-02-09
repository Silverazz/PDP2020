package com.example.pdpproject.Test;

import com.example.pdpproject.algorithms.GenerateScore;
import com.example.pdpproject.apiManager.APIManagerForTests;
import com.example.pdpproject.models.Album;
import com.example.pdpproject.models.Playlist;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.User;
import com.example.pdpproject.models.albums.AlbumSpotify;
import com.example.pdpproject.models.playlists.PlaylistSpotify;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.models.users.UserSpotify;
import com.example.pdpproject.repo.Singleton;

import java.util.ArrayList;

import static com.example.pdpproject.Test.ServiceTest.createAlbum;
import static com.example.pdpproject.Test.ServiceTest.testsAlgos;

public class Test2 {
    public static void main(String[] args) {

        Singleton singleton = Singleton.getInstance();
        GenerateScore score = new GenerateScore(new APIManagerForTests());




        for(int i = 0; i<8; i++) {
            User whiteSheep = new User("User"+i, "popular", new UserSpotify());

            ArrayList<Track> tracks = new ArrayList<>();
            for(int z = 0; z < 4; z++) {
                for (int j = 0; j < 3; j++) {
                    Track basic = new Track("id = " + i + " " + j, "popular" + j, 10, new TrackSpotify());
                    basic.addArtistId("Someone" + z);
                    tracks.add(basic);
                    whiteSheep.addArtist("Someone" + z);

                    ArrayList<Album> albums = new ArrayList<>();
                    albums.add(createAlbum("Someone" + z,new AlbumSpotify(),new TrackSpotify()));
                    singleton.addAlbums(albums);
                }
            }
            Playlist play = new Playlist(i+"","pop",new PlaylistSpotify());
            play.setTracksIds(tracks);
            singleton.addTracks(tracks);

            ArrayList<Playlist> listPlaylist = new ArrayList<Playlist>();
            listPlaylist.add(play);
            whiteSheep.setPlaylists(listPlaylist);

            singleton.addUser(whiteSheep);
        }

        User blackSheep = new User("777", "Freddy", new UserSpotify());

        ArrayList<Track> tracks = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                Track basic = new Track("id = " + j, "Feel Good Inc" + j, 10, new TrackSpotify());
                basic.addArtistId("Gorillaz" + i);
                tracks.add(basic);
                blackSheep.addArtist("Gorillaz" + i);

                ArrayList<Album> albums = new ArrayList<>();
                albums.add(createAlbum("Gorillaz" + i,new AlbumSpotify(),new TrackSpotify()));
                singleton.addAlbums(albums);
            }
        }

        Playlist play = new Playlist("insane","hell yea",new PlaylistSpotify());
        play.setTracksIds(tracks);
        ArrayList<Playlist> listPlaylist = new ArrayList<Playlist>();
        listPlaylist.add(play);
        blackSheep.setPlaylists(listPlaylist);
        singleton.addTracks(tracks);


        singleton.addUser(blackSheep);

        System.out.println("8 users listen Someone music's and another listen Gorillaz music's");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);

    }
}
