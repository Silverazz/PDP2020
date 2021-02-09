package com.example.pdpproject.apiManager.requests;


import com.example.pdpproject.repo.Singleton;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyRequest {
    private String token;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String SPOTIFY_CLIENT_ID = "6ce146dc122643279ba873f48606e931";
    private String SPOTIFY_CLIENT_SECRET = "b9b0990e95664e88a2d337490df6a04d";
    private Singleton singleton = Singleton.getInstance();

    public SpotifyRequest() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get user profile from Spotify API
     *
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestUserProfile() {

        final String BaseURL = "https://api.spotify.com/v1/me";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get user's playlists
     *
     * @param userId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestPlaylistsByUser(String userId) {
        String BaseURL = "https://api.spotify.com/v1/users/";
        BaseURL += userId + "/playlists";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get artist object from Spotify API
     *
     * @param artistId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestArtistById(String artistId) {
        final String BaseURL = "https://api.spotify.com/v1/artists/" + artistId;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);

    }

    public  Future<JSONObject> requestRelatedArtist(String artistId){
        final String BaseURL = "https://api.spotify.com/v1/artists/" +artistId +"/related-artists";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get artists object from Spotify API
     *
     * @param ids
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestArtistsByIds(String ids) {
        final String BaseURL = "https://api.spotify.com/v1/artists?ids=" + ids;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);

    }

    /**
     * Get user's followed artists from Spotify API
     *
     * @param limitFollowedArtist
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestFollowedArtist(int limitFollowedArtist) {
        final String CURL = "https://api.spotify.com/v1/me/following?type=artist&limit=" + limitFollowedArtist;
        final Request request = new Request.Builder()
                .url(CURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     *  Get Albums from an artist from Spotify API
     *  Use main access token which could be refreshed in case of expiration.
     * @param artistId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestAlbumsFromArtist(String artistId){
        final String CURL = "https://api.spotify.com/v1/artists/" + artistId + "/albums";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(CURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get top tracks from an artist
     * Use main access token which could be refreshed in case of expiration.
     * @param artistId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTopTracksFromArtist(String artistId) {
        String BaseURL = "https://api.spotify.com/v1/artists/";
        BaseURL += artistId + "/top-tracks?country=FR";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get tracks from a playlist
     * @param playlistId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTracksByPlaylist(String playlistId,int offset) {
        final String BaseURL = "https://api.spotify.com/v1/playlists/";

        String new_url = BaseURL + playlistId + "/tracks?" +"fields = items(track(id,name,album(id,name),artists))" + "&limit=100&offset=" +offset;
        final Request request = new Request.Builder()
                .url(new_url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get tracks from an album
     * Use main access token which could be refreshed in case of expiration.
     * @param albumId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTracksFromAlbum(String albumId) {
        final String BaseURL = "https://api.spotify.com/v1/albums/" + albumId + "/tracks";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Get tracks JSONObject
     * Use main access token which could be refreshed in case of expiration.
     * @param tracksId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTracks(String tracksId) {
        final String BaseURL = "https://api.spotify.com/v1/tracks?ids=" + tracksId;
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }


    /**
     * Get track feature from API
     * @param trackId
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTrackFeatures(String trackId){
        final String BaseURL = "https://api.spotify.com/v1/audio-features/";

        String new_url = BaseURL + trackId;
        final Request request = new Request.Builder()
                .url(new_url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }
    /**
     * Get tracks feature from API
     * @param ids
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestTracksFeatures(String ids){
        final String BaseURL = "https://api.spotify.com/v1/audio-features?ids=" +ids;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Http method Delete
     * Delete tracks from a playlist
     * Use main access token which could be refreshed in case of expiration.
     * @param playlistId
     * @param tracks
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestDeleteTracks(String playlistId, String tracks){

        final String BaseURL = "https://api.spotify.com/v1/playlists/" + playlistId +"/tracks";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, tracks);
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .delete(body)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Create a playlist for user
     * Use main access token which could be refreshed in case of expiration.
     * @param userId
     * @param name
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestCreatePlaylist(String userId, String name){
        final String BaseURL ="https://api.spotify.com/v1/users/" + userId +"/playlists";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, name);
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .post(body)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Add Tracks to App Playlist
     * @param playlistId
     * @param param
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> requestAddTracksToAppPlaylist(String playlistId,String param){

        final String BaseURL ="https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, param);
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .post(body)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }

    /**
     * Execute request on another thread
     * @param mCall
     * @return JSONObject that contains status code and response body
     */
    private Future<JSONObject> getResponse(Call mCall) {

        return executorService.submit(() -> {
            Response response = mCall.execute();
            JSONObject obj = new JSONObject();
            obj.put("status_code",response.code());
            obj.put("data",new JSONObject(response.body().string()));
            return obj;
        });
    }

    /**
     * Use refresh token to get a new access token
     * @return JSONObject that contains status code and response body
     */
    public Future<JSONObject> getNewMainAccessToken(){
        final String BaseURL = "https://accounts.spotify.com/api/token";
        MediaType FORM = MediaType.parse("multipart/form-data");
        String refreshToken = singleton.getRefreshToken();
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token",refreshToken)
                .add("client_id", SPOTIFY_CLIENT_ID)
                .add("client_secret", SPOTIFY_CLIENT_SECRET)
                .build();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .post(formBody)
                .build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call mCall = mOkHttpClient.newCall(request);
        return getResponse(mCall);
    }


}
