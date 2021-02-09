package com.example.pdpproject.Test;

import static com.example.pdpproject.Test.ServiceTest.createAlbum;
import static com.example.pdpproject.Test.ServiceTest.testsAlgos;

import java.util.ArrayList;

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

public class Test4 {
    public static void main(String[] args) {


        Singleton singleton = Singleton.getInstance();
        GenerateScore score = new GenerateScore(new APIManagerForTests());

        User allInclusive = new User("User1", "SeveralMusic", new UserSpotify());

        ArrayList<Album> albumsMixte = new ArrayList<>();
        albumsMixte.add(createAlbum("Dunno", new AlbumSpotify(), new TrackSpotify()));
        albumsMixte.add(createAlbum("Van Halen", new AlbumSpotify(), new TrackSpotify()));
        albumsMixte.add(createAlbum("Dragonforce", new AlbumSpotify(), new TrackSpotify()));
        albumsMixte.add(createAlbum("Pizza", new AlbumSpotify(), new TrackSpotify()));
        singleton.addAlbums(albumsMixte);

        Track rock = new Track("rock", "Jump", 20, new TrackSpotify());
        Track samba = new Track("samba", "Dunno", 20, new TrackSpotify());
        Track metal = new Track("metal", "Cry Thunder", 20, new TrackSpotify());
        Track classico = new Track("classic", "4 saisons", 20, new TrackSpotify());

        rock.addArtistId("Van Halen");
        allInclusive.addArtist("Van Halen");
        samba.addArtistId("Dunno");
        allInclusive.addArtist("Dunno");
        metal.addArtistId("Dragonforce");
        allInclusive.addArtist("Dragonforce");
        classico.addArtistId("Pizza");
        allInclusive.addArtist("Pizza");

        Playlist play = new Playlist("4", "mixte", new PlaylistSpotify());
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

        for (int i = 2; i < 5; i++) {
            User allInclusive2 = new User("user " + i  , "SeveralMusic", new UserSpotify());

            ArrayList<Album> albumsMixte2 = new ArrayList<>();
            albumsMixte2.add(createAlbum("user" + i + "Samba", new AlbumSpotify(), new TrackSpotify()));
            albumsMixte2.add(createAlbum("user" + i + "Rock", new AlbumSpotify(), new TrackSpotify()));
            albumsMixte2.add(createAlbum("user" + i + "Metal", new AlbumSpotify(), new TrackSpotify()));
            albumsMixte2.add(createAlbum("user" + i + "Class", new AlbumSpotify(), new TrackSpotify()));
            singleton.addAlbums(albumsMixte2);

            Track rock2 = new Track("user" + i + "Rock", "user" + i + "Rock", 20, new TrackSpotify());
            Track samba2 = new Track("user" + i + "Samba", "user" + i + "Samba", 20, new TrackSpotify());
            Track metal2 = new Track("user" + i + "Metal", "user" + i + "Metal", 20, new TrackSpotify());
            Track classico2 = new Track("user" + i + "Class", "user" + i + "Class", 20, new TrackSpotify());
            
            rock2.addArtistId("user" + i + "Rock");
            allInclusive2.addArtist("user" + i + "Rock");
            samba2.addArtistId("user" + i + "Samba");
            allInclusive2.addArtist("user" + i + "Samba");
            metal2.addArtistId("user" + i + "Metal");
            allInclusive2.addArtist("user" + i + "Metal");
            classico2.addArtistId("user" + i + "Class");
            allInclusive2.addArtist("user" + i + "Class");

            Playlist play2 = new Playlist("4", "mixte", new PlaylistSpotify());
            ArrayList<Track> tracks2 = new ArrayList<Track>();
            tracks2.add(rock2);
            tracks2.add(samba2);
            tracks2.add(metal2);
            tracks2.add(classico2);
            play2.setTracksIds(tracks2);
            singleton.addTracks(tracks2);

            ArrayList<Playlist> listPlaylistAllInclusive2 = new ArrayList<Playlist>();
            listPlaylistAllInclusive2.add(play2);
            allInclusive2.setPlaylists(listPlaylistAllInclusive2);


            singleton.addUser(allInclusive2);
            
            System.out.println(i +" users which listen many different artists");
            if (args.length > 0 && args[0].equals("v"))
                testsAlgos(score, 50, true);
            else
                testsAlgos(score, 50, false);
        }
        System.out.println("size ="+singleton.getAllUser().get(2).getPlaylists().get(0).getTracksIds().get(3));

    }
}
