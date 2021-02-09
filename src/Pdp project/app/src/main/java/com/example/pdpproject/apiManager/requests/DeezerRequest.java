package com.example.pdpproject.apiManager.requests;


import com.example.pdpproject.apiManager.ExportCallbackO;
import com.example.pdpproject.repo.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeezerRequest {
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Singleton singleton = Singleton.getInstance();
    private String token;

    public void setToken(String token) {
        this.token = token;
    }

    public void requestUserProfile(final ExportCallbackO exportCallbackO) {

        String BaseURL = "https://api.deezer.com/user/me";
        BaseURL += "?access_token=" +token;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestFollowedArtist(int limitFollowedArtist, final ExportCallbackO exportCallbackO) {
        String CURL = "https://api.deezer.com/user/me/artists?limit=" + limitFollowedArtist;
        CURL += "&access_token=" + token;
        final Request request = new Request.Builder()
                .url(CURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestPlaylistsByUser(String userId, final ExportCallbackO exportCallbackO) {
        String BaseURL = "https://api.deezer.com/user/me/playlists";
        BaseURL += "?access_token=" +token;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }


    public void requestArtistById(String artistId, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.deezer.com/artist/" + artistId;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);

    }

    public void requestRelatedArtist(String artistId, final ExportCallbackO exportCallbackO){
        final String BaseURL = "https://api.deezer.com/artist/" +artistId +"/related";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }


    public void requestAlbumsFromArtist(String artistId, ExportCallbackO exportCallbackO) {
        final String CURL = "https://api.deezer.com/artist/" + artistId + "/albums";
        final Request request = new Request.Builder()
                .url(CURL)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestTopTracksFromArtist(String artistId, final ExportCallbackO exportCallbackO) {
        String BaseURL = "https://api.deezer.com/artist/";
        BaseURL += artistId + "/top";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall,exportCallbackO);
    }


    public void requestTracksByPlaylist(String playlistId, final ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.deezer.com/playlist/";
        String new_url = BaseURL + playlistId + "/tracks";
        final Request request = new Request.Builder()
                .url(new_url)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestTracksFromAlbum(String albumId, ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.deezer.com/album/" + albumId+"/tracks";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestAlbumGenres(String albumId,ExportCallbackO exportCallbackO){
        final String BaseURL = "https://api.deezer.com/album/" + albumId;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestTrack(String trackId, ExportCallbackO exportCallbackO) {
        final String BaseURL = "https://api.deezer.com/track/" + trackId;
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();

        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }


    public void requestDeleteTracks(String playlistId, String tracks, final ExportCallbackO exportCallbackO){
        String BaseURL = "https://api.deezer.com/playlist/" + playlistId +"/tracks";
        String mainAccessToken = singleton.getMainAccessToken();
        BaseURL += "?songs="+tracks;
        BaseURL+="&access_token="+mainAccessToken;
        BaseURL+="&request_method=DELETE";

        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall,exportCallbackO);
    }

    public void requestCreatePlaylist(String name, final ExportCallbackO exportCallbackO){
        String BaseURL ="https://api.deezer.com/user/me/playlists";
        String mainAccessToken = singleton.getMainAccessToken();
        BaseURL += "?title="+name;
        BaseURL+="&access_token="+mainAccessToken;
        BaseURL+="&request_method=POST";

        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall, exportCallbackO);
    }

    public void requestDeletePlaylist(String playlistId, final ExportCallbackO exportCallbackO){
        String BaseURL ="https://api.deezer.com/playlist/" + playlistId ;
        String mainAccessToken = singleton.getMainAccessToken();
        BaseURL+="&access_token="+mainAccessToken;
        BaseURL+="&request_method=DELETE";
        final Request request = new Request.Builder()
                .url(BaseURL)
                .build();
        Call mCall = mOkHttpClient.newCall(request);
        getResponse(mCall,exportCallbackO);
    }


    public void requestAddTracksToAppPlaylist(String playlistId,String songs, final ExportCallbackO exportCallbackO){
        String BaseURL ="https://api.deezer.com/playlist/" + playlistId + "/tracks";
        String mainAccessToken = singleton.getMainAccessToken();
        BaseURL += "?songs="+songs;
        BaseURL+="&access_token="+mainAccessToken;
        BaseURL+="&request_method=POST";
        final Request request = new Request.Builder()
                .url(BaseURL)
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
        String error = "{\"error\":{\"type\":\"Exception\",\"message\":\"Quota limit exceeded\",\"code\":4}}";
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
                    if(res.equals(error)){
                        waitResponse(waitCall,exportCallbackO);
                        return;
                    }
                    if (res.equals("true")){
                        obj.put("data", new JSONObject("{\"res\" : true}"));
                    }else{
                        obj.put("data", new JSONObject(res));
                    }
                    exportCallbackO.onSuccess(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void waitResponse(Call mCall, final ExportCallbackO exportCallbackO){
        String error = "{\"error\":{\"type\":\"Exception\",\"message\":\"Quota limit exceeded\",\"code\":4}}";
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
                        if(res.equals(error)){
                            waitResponse(waitCall,exportCallbackO);
                        }
                        else if (res.equals("true")){
                            obj.put("data", new JSONObject("{\"res\" : true}"));
                        }else{
                            obj.put("data", new JSONObject(res));
                        }

                        exportCallbackO.onSuccess(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

    }

}
