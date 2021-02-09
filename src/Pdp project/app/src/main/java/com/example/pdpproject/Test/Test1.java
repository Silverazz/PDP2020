package com.example.pdpproject.Test;


import com.example.pdpproject.algorithms.GenerateScore;
import com.example.pdpproject.apiManager.APIManagerForTests;
import com.example.pdpproject.models.*;
import com.example.pdpproject.models.albums.AlbumSpotify;
import com.example.pdpproject.models.playlists.PlaylistSpotify;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.models.users.UserSpotify;
import com.example.pdpproject.repo.Singleton;

import java.util.ArrayList;

import static com.example.pdpproject.Test.ServiceTest.createAlbum;
import static com.example.pdpproject.Test.ServiceTest.testsAlgos;

public class Test1 {


    public static void main(String[] args) {


        Singleton singleton = Singleton.getInstance();
        GenerateScore score = new GenerateScore(new APIManagerForTests());



        User rocker = new User("User1","rocker",new UserSpotify());

        ArrayList<Track> tracksRock = new ArrayList<>();
        for(int j = 0; j<2; j++) {
            for (int i = 0; i < 10; i++) {
                Track rock = new Track("rock" + i, "Let's rock " + i, 10 - i, new TrackSpotify());
                rock.addArtistId("User'1 artist" + j);
                tracksRock.add(rock);
                rocker.addArtist("User'1 artist" + j);
                Track rock2 = new Track("rock" + i, "Hell's bells " + i, 10 - i, new TrackSpotify());
                rock2.addArtistId("User's 1 artist Bis" + j);
                tracksRock.add(rock2);
                rocker.addArtist("User's 1 artist Bis" + j);

                ArrayList<Album> albums = new ArrayList<>();
                albums.add(createAlbum("User'1 artist" + j,new AlbumSpotify(),new TrackSpotify()));
                albums.add(createAlbum("User's 1 artist Bis" + j,new AlbumSpotify(),new TrackSpotify()));
                singleton.addAlbums(albums);

            }
        }

        Playlist playRock = new Playlist("1","rock",new PlaylistSpotify());
        playRock.setTracksIds(tracksRock);
        singleton.addTracks(tracksRock);

        ArrayList<Playlist> listPlaylist = new ArrayList<Playlist>();
        listPlaylist.add(playRock);
        rocker.setPlaylists(listPlaylist);

        singleton.addUser(rocker);


        User classic = new User("User2","classic",new UserSpotify());

        ArrayList<Track> tracksClassic = new ArrayList<>();
        for(int j = 0; j<2; j++) {
            for (int i = 0; i < 10; i++) {
                Track classicTrack = new Track("classic" + i, "4 saisons" + i, 10 - i, new TrackSpotify());
                classicTrack.addArtistId("User's 2 artist" + j);
                tracksClassic.add(classicTrack);
                classic.addArtist("User's 2 artist" + j);
                Track classicTrack2 = new Track("classic" + i, "idk " + i, 10 - i, new TrackSpotify());
                classicTrack2.addArtistId("User's 2 artist Bis" + j);
                tracksClassic.add(classicTrack2);
                classic.addArtist("User's 2 artist Bis" + j);

                ArrayList<Album> albums = new ArrayList<>();
                albums.add(createAlbum("User's 2 artist" + j, new AlbumSpotify(), new TrackSpotify()));
                albums.add(createAlbum("User's 2 artist Bis" + j, new AlbumSpotify(), new TrackSpotify()));
                singleton.addAlbums(albums);

            }
        }

        Playlist playClassic = new Playlist("2","classic",new PlaylistSpotify());
        playClassic.setTracksIds(tracksClassic);
        singleton.addTracks(tracksClassic);

        ArrayList<Playlist> listPlaylist2 = new ArrayList<Playlist>();
        listPlaylist2.add(playClassic);
        classic.setPlaylists(listPlaylist2);

        singleton.addUser(classic);

        System.out.println("Playlist for 2 users who listen each one a different artist");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);


        User rappeur = new User("User3","rap",new UserSpotify());


        ArrayList<Track> tracksRap = new ArrayList<>();
        for(int j = 0; j<7; j++) {
            for (int i = 0; i < 3; i++) {
                Track rap = new Track("rap" + i, "Mockingbird" + i, 10, new TrackSpotify());
                rap.addArtistId("User's 3 artist" + j);
                tracksRap.add(rap);
                rappeur.addArtist("User's 3 artist" + j);
                Track rap2 = new Track("rapfr" + i, "Suicide social" + i, 10, new TrackSpotify());
                rap2.addArtistId("User's 3 artist Bis" + j);
                tracksRap.add(rap2);
                rappeur.addArtist("User's 3 artist Bis" + j);

                ArrayList<Album> albumsRap = new ArrayList<>();
                albumsRap.add(createAlbum("User's 3 artist" + j,new AlbumSpotify(),new TrackSpotify()));
                albumsRap.add(createAlbum("User's 3 artist Bis" + j,new AlbumSpotify(),new TrackSpotify()));
                singleton.addAlbums(albumsRap);
            }
        }

        Playlist playRap = new Playlist("3","rap",new PlaylistSpotify());
        playRap.setTracksIds(tracksRap);
        singleton.addTracks(tracksRap);

        ArrayList<Playlist> listPlaylist3 = new ArrayList<Playlist>();
        listPlaylist3.add(playRap);
        rocker.setPlaylists(listPlaylist3);

        singleton.addUser(rappeur);

        System.out.println("Add user which listen rap");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);

        User rocker2 = new User("User1","rocker",new UserSpotify());

        ArrayList<Track> tracksRock2 = new ArrayList<>();

        Track rock = new Track("rocky", "Let's rock ", 10 , new TrackSpotify());
        rock.addArtistId("User'1 artist0");
        tracksRock2.add(rock);
        rocker.addArtist("User'1 artist0");

        for(int j = 0; j<2; j++) {
            for (int i = 0; i < 4; i++) {

                Track rock2 = new Track("rock" + i, "Hell's bells " + i, 10 - i, new TrackSpotify());
                rock2.addArtistId("User's 4 artist" + j);
                tracksRock2.add(rock2);
                rocker.addArtist("User's 4 artist" + j);

                ArrayList<Album> albums = new ArrayList<>();
                albums.add(createAlbum("User's 4 artist" + j,new AlbumSpotify(),new TrackSpotify()));
                singleton.addAlbums(albums);

            }
        }

        Playlist playRock2 = new Playlist("1","rock",new PlaylistSpotify());
        playRock2.setTracksIds(tracksRock2);
        singleton.addTracks(tracksRock2);

        ArrayList<Playlist> listPlaylist4 = new ArrayList<Playlist>();
        listPlaylist4.add(playRock2);
        rocker2.setPlaylists(listPlaylist4);

        singleton.addUser(rocker);

        System.out.println("Add user with User's artist1 in playlist");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);


/*
        User allInclusive = new User("User4","SeveralMusic", new UserSpotify());

        ArrayList<Album> albumsMixte = new ArrayList<>();
        albumsMixte.add(createAlbum("Dunno",new AlbumSpotify(),new TrackSpotify()));
        albumsMixte.add(createAlbum("Van Halen",new AlbumSpotify(),new TrackSpotify()));
        albumsMixte.add(createAlbum("Dragonforce",new AlbumSpotify(),new TrackSpotify()));
        albumsMixte.add(createAlbum("Pizza",new AlbumSpotify(),new TrackSpotify()));
        singleton.addAlbums(albumsMixte);

        Track rock = new Track("rock","Jump",20, new TrackSpotify());
        Track samba = new Track("samba","Dunno",20, new TrackSpotify());
        Track metal = new Track("metal","Cry Thunder",20, new TrackSpotify());
        Track classico = new Track("classic","4 saisons",20, new TrackSpotify());

        rock.addArtistId("Van Halen");
        allInclusive.addArtist("Van Halen");
        samba.addArtistId("Dunno");
        allInclusive.addArtist("Dunno");
        metal.addArtistId("Dragonforce");
        allInclusive.addArtist("Dragonforce");
        classico.addArtistId("Pizza");
        allInclusive.addArtist("Pizza");

        Playlist play = new Playlist("4","mixte", new PlaylistSpotify());
        ArrayList<Track> tracks = new ArrayList<Track>();
        tracks.add(rock);
        tracks.add(samba);
        tracks.add(metal);
        tracks.add(classico);
        play.setTracksIds(tracks);
        singleton.addTracks(tracks);

        ArrayList<Playlist> listPlaylistAllInclusive = new ArrayList<Playlist>();
        listPlaylistAllInclusive.add(play);
        allInclusive.setPlaylists(listPlaylistAllInclusive);

        singleton.addUser(allInclusive);

        System.out.println("Add user with many different tastes of music");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);
*/
    }

}