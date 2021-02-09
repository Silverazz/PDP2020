package com.example.pdpproject.apiManager.requests;

import com.example.pdpproject.apiManager.ExportCallbackO;
import com.example.pdpproject.repo.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyRequestAsyn {
    private String token;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Singleton singleton = Singleton.getInstance();
    private String SPOTIFY_CLIENT_ID = "6ce146dc122643279ba873f48606e931";
    private String SPOTIFY_CLIENT_SECRET = "b9b0990e95664e88a2d337490df6a04d";

    public SpotifyRequestAsyn() {
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get user profile from Spotify API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param exportCallbackO
     */
    public void requestUserProfile(final ExportCallbackO exportCallbackO) {

        final String BaseURL = "https://api.spotify.com/v1/me";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get user's playlists
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param userId
     * @param exportCallbackO
     */
    public void requestPlaylistsByUser(String userId, final ExportCallbackO exportCallbackO) {
        String BaseURL = "https://api.spotify.com/v1/users/";
        BaseURL += userId + "/playlists";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get artist object from Spotify API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param artistId
     * @param exportCallbackO
     */
    public void requestArtistById(String artistId, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/artists/" + artistId;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);

    }

    public void requestRelatedArtist(String artistId, final ExportCallbackO exportCallbackO){
        final String BaseURL = "https://api.spotify.com/v1/artists/" +artistId +"/related-artists";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get artists object from Spotify API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param ids
     * @param exportCallbackO On success JSONObject from API will be pass to Callback function
     */
    public void requestArtistsByIds(String ids, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/artists?ids=" + ids;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);

    }

    /**
     * Get user's followed artists from Spotify API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param limitFollowedArtist
     * @param exportCallbackO
     */
    public void requestFollowedArtist(int limitFollowedArtist, final ExportCallbackO exportCallbackO) {
        final String CURL = "https://api.spotify.com/v1/me/following?type=artist&limit=" + limitFollowedArtist;
        final Request request = new Request.Builder()
                .url(CURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get Albums from an artist from Spotify API
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param artistId
     * @param exportCallbackO
     */
    public void requestAlbumsFromArtist(String artistId, ExportCallbackO exportCallbackO) {
        final String CURL = "https://api.spotify.com/v1/artists/" + artistId + "/albums";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(CURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get top tracks from an artist
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param artistId
     * @param exportCallbackO
     */
    public void requestTopTracksFromArtist(String artistId, final ExportCallbackO exportCallbackO) {
        String BaseURL = "https://api.spotify.com/v1/artists/";
        BaseURL += artistId + "/top-tracks?country=FR";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall,exportCallbackO);
    }

    /**
     * Get tracks from a playlist
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param playlistId
     * @param exportCallbackO
     */
    public void requestTracksByPlaylist(String playlistId, int offset, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/playlists/";

        String new_url = BaseURL + playlistId + "/tracks?" + "fields = items(track(id,name,album(id,name),artists))" + "&limit=100&offset=" + offset;
        final Request request = new Request.Builder()
                .url(new_url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get tracks from an album
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param albumId
     * @param exportCallbackO
     */
    public void requestTracksFromAlbum(String albumId, ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/albums/" + albumId + "/tracks";
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get Tracks JSONObject
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param tracksId
     * @param exportCallbackO
     */
    public void requestTracks(String tracksId, ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/tracks?ids=" + tracksId;
        String mainAccessToken = singleton.getMainAccessToken();
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + mainAccessToken)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get track feature from API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param trackId
     * @param exportCallbackO
     */
    public void requestTrackFeatures(String trackId, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/audio-features/";

        String new_url = BaseURL + trackId;
        final Request request = new Request.Builder()
                .url(new_url)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Get tracks feature from API
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param ids
     * @param exportCallbackO
     */
    public void requestTracksFeatures(String ids, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.spotify.com/v1/audio-features?ids=" + ids;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Http method Delete
     * Delete tracks from a playlist
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param playlistId
     * @param tracks
     * @param exportCallbackO
     */
    public void requestDeleteTracks(String playlistId, String tracks, final ExportCallbackO exportCallbackO){
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
        getResponse(mCall,exportCallbackO);
    }

    /**
     * Create a playlist for user
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param userId
     * @param name
     * @param exportCallbackO
     */
    public void requestCreatePlaylist(String userId, String name, final ExportCallbackO exportCallbackO){
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
        getResponse(mCall, exportCallbackO);
    }

    /**
     * Add Tracks to App Playlist
     * Use main access token which could be refreshed in case of expiration.
     * On success JSONObject that contains status code and response body from API will be pass to Callback function
     * @param playlistId
     * @param param
     * @param exportCallbackO
     */
    public void requestAddTracksToAppPlaylist(String playlistId,String param, final ExportCallbackO exportCallbackO){
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
        getResponse(mCall,exportCallbackO);
    }

    /**
     * Execute request and put response in Callback as parameter
     * @param mCall
     * @param exportCallbackO
     */
    private void getResponse(Call mCall, final ExportCallbackO exportCallbackO) {
        String error = "{\"error\":{\"status\":429,\"message\":\"API rate limit exceeded\"}}";
        Call waitCall = mCall.clone();
        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject();
                    String res = response.body().string();
                    obj.put("status_code", response.code());
                    JSONObject data=  new JSONObject(res);
                    if(data.toString().equals(error)){
                        waitResponse(waitCall,exportCallbackO);
                        return;
                    }else{
                        obj.put("data", data);
                        exportCallbackO.onSuccess(obj);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void waitResponse(Call mCall, final ExportCallbackO exportCallbackO){
        String error = "{\"error\":{\"status\":429,\"message\":\"API rate limit exceeded\"}}";
        Call waitCall = mCall.clone();
        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("status_code", response.code());
                    String res = response.body().string();
                    JSONObject data=  new JSONObject(res);
                    if(data.toString().equals(error)){
                        waitResponse(waitCall,exportCallbackO);
                    }else{
                        obj.put("data", data);
                    }
                    exportCallbackO.onSuccess(obj);
                } catch (JSONException  e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Use refresh token to get a new access token
     * @param exportCallbackO
     */
    public void getNewMainAccessToken(ExportCallbackO exportCallbackO){
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
        getResponse(mCall,exportCallbackO);
    }


}
