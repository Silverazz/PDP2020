package com.example.pdpproject.apiManager;

import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;

import java.util.ArrayList;
import java.util.Set;


public interface APIManagerItf {
    void exportModels();

    ArrayList<Track> exportArtistTopTracks(String artistId, boolean hasRefreshed);

    Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed);

    ArrayList<Track> exportTracksFromAlbum(String albumId, boolean hasRefreshed);
    String createPlaylist(String name, boolean hasRefreshed);
    boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed);

    ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed);
    void setRequestToken(String token);
}
