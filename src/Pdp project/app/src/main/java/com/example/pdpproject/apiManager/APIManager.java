package com.example.pdpproject.apiManager;

import com.example.pdpproject.models.Artist;
import com.example.pdpproject.models.Track;

import java.util.ArrayList;
import java.util.Set;

public class APIManager implements APIManagerItf {
    private APIManagerItf apiManagerItf;
    public APIManager(APIManagerItf apiManagerItf) {
        this.apiManagerItf = apiManagerItf;
    }

    @Override
    public void exportModels() {
        apiManagerItf.exportModels();
    }

    @Override
    public ArrayList<Track> exportArtistTopTracks(String artistId,boolean hasRefreshed) {
        return apiManagerItf.exportArtistTopTracks(artistId,hasRefreshed);
    }

    @Override
    public Set<String> fetchAlbumsIdsFromArtist(String artistId, boolean hasRefreshed) {
        return apiManagerItf.fetchAlbumsIdsFromArtist(artistId,hasRefreshed);
    }

    @Override
    public ArrayList<Track> exportTracksFromAlbum(String albumId,boolean hasRefreshed) {
        return apiManagerItf.exportTracksFromAlbum(albumId,hasRefreshed);
    }

    @Override
    public String createPlaylist(String name, boolean hasRefreshed) {
        return apiManagerItf.createPlaylist(name,hasRefreshed);
    }

    @Override
    public boolean addTracksToAppPlaylist(ArrayList<Track> tracks, boolean hasRefreshed) {
        return apiManagerItf.addTracksToAppPlaylist(tracks,hasRefreshed);
    }

    @Override
    public ArrayList<Artist> exportRelatedArtists(String artistId, boolean hasRefreshed) {
        return apiManagerItf.exportRelatedArtists(artistId,false);
    }

    @Override
    public void setRequestToken(String token) {
        apiManagerItf.setRequestToken(token);
    }

}
