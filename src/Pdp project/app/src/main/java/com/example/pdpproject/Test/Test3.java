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

public class Test3 {
    public static void main(String[] args) {

        Singleton singleton = Singleton.getInstance();
        GenerateScore score = new GenerateScore(new APIManagerForTests());

        ArrayList<Album> albums = new ArrayList<>();

        for(int i = 0; i < 50; i++){
            albums.add(createAlbum("Not Franck"+i,new AlbumSpotify(),new TrackSpotify()));
        }

        User bigUser = new User("Big user" , "Big user", new UserSpotify());
        ArrayList<Track> tracks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Track music = new Track("Music" + i, "Sample" + i, 10, new TrackSpotify());
            music.addArtistId("Franck" + i);
            tracks.add(music);
            bigUser.addArtist("Franck" + i);
            albums.add(createAlbum("Franck" + i,new AlbumSpotify(),new TrackSpotify()));
        }
        singleton.addAlbums(albums);
        Playlist playlist = new Playlist("Number 1","Big playlist",new PlaylistSpotify());
        playlist.setTracksIds(tracks);
        singleton.addTracks(tracks);

        ArrayList<Playlist> listPlaylist = new ArrayList<>();
        listPlaylist.add(playlist);
        bigUser.setPlaylists(listPlaylist);

        singleton.addUser(bigUser);

        for(int i = 0; i<10; i++){
            User user = new User("User" + i, "User " + i, new UserSpotify());

            ArrayList<Track> tracks2 = new ArrayList<>();
            for(int j = 0; j < 4; j++) {
                Track music = new Track("Music of little user " + i, "Sample" + i, 10, new TrackSpotify());
                music.addArtistId("Not Franck" + i);
                tracks2.add(music);
                user.addArtist("Not Franck" + i);
            }

            Playlist playlist2 = new Playlist("Number 1","Big playlist",new PlaylistSpotify());
            playlist2.setTracksIds(tracks2);
            singleton.addTracks(tracks2);

            ArrayList<Playlist> listPlaylist2 = new ArrayList<>();
            listPlaylist2.add(playlist);
            user.setPlaylists(listPlaylist2);

            singleton.addUser(user);
        }

        System.out.println("One user with 50 musics and 10 users with 1 music");
        if(args.length > 0 && args[0].equals("v"))
            testsAlgos(score,50,true);
        else
            testsAlgos(score,50,false);
    }
}
