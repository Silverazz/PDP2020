package com.example.pdpproject.activities;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.example.pdpproject.repo.Singleton;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SpotifyLogin  {

    private String SPOTIFY_CLIENT_ID = "6ce146dc122643279ba873f48606e931";
    private String SPOTIFY_CLIENT_SECRET = "b9b0990e95664e88a2d337490df6a04d";
    private String SPOTIFY_REDIRECT_URI = "equify://callback";
    final static int SPOTIFY_AUTH_TOKEN_REQUEST_CODE = 0x10;
    final static int SPOTIFY_AUTH_CODE_REQUEST_CODE = 0x11;


    private String code;
    private Singleton singleton = Singleton.getInstance();
    SpotifyAppRemote mSpotifyAppRemote;

    public void connection(Application application, Activity activity){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);

        SpotifyAppRemote.connect(
                application, new ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                        .setRedirectUri(SPOTIFY_REDIRECT_URI)
                        .showAuthView(false)
                        .build(),
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        getMainToken(activity);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
                        logError(throwable,activity);
                    }
                }
        );
    }


    private void getMainToken(Activity activity) {
        final AuthorizationRequest request = getSpotifyAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(activity, SPOTIFY_AUTH_TOKEN_REQUEST_CODE, request);
        activity.overridePendingTransition(0,0);
    }

    public void queryRequestCode(Activity activity) {
        final AuthorizationRequest request = getSpotifyAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(activity, SPOTIFY_AUTH_CODE_REQUEST_CODE, request);
        activity.overridePendingTransition(0,0);

    }

    private AuthorizationRequest getSpotifyAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(SPOTIFY_CLIENT_ID, type, SPOTIFY_REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-follow-read", "playlist-modify-public"})
                .setCampaign("your-campaign-token")
                .build();

    }

    private boolean setRefreshToken() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        final String BaseURL = "https://accounts.spotify.com/api/token";
        MediaType FORM = MediaType.parse("multipart/form-data");
        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", SPOTIFY_REDIRECT_URI)
                .add("client_id", SPOTIFY_CLIENT_ID)
                .add("client_secret", SPOTIFY_CLIENT_SECRET)
                .build();
        final Request request2 = new Request.Builder()
                .url(BaseURL)
                .post(formBody)
                .build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Call mCall = mOkHttpClient.newCall(request2);

        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject res = new JSONObject(response.body().string());
                    String refreshToken = res.getString("refresh_token");
                    singleton.setRefreshToken(refreshToken);
                    semaphore.release();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        semaphore.acquire();
        return true;
    }

    public void setAccessToken(int resultCode, Intent data ){
        AuthorizationResponse response = checkError(resultCode,data);
        String mAccessToken = response.getAccessToken();
        singleton.setMainAccessToken(mAccessToken);

    }

    public boolean setCode(int resultCode, Intent data){
        AuthorizationResponse response = checkError(resultCode,data);
        this.code = response.getCode();
        try {
            return setRefreshToken();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }

    private AuthorizationResponse checkError(int resultCode, Intent data  ){
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (response.getError() != null && response.getError().isEmpty()) {
            throw new RuntimeException(response.getError());
        }

        return response;
    }

    private void logError(Throwable throwable,Activity activity) {
        Toast.makeText(activity, "Can't connect to Spotify.", Toast.LENGTH_SHORT).show();
        Log.e("remote", "", throwable);
    }

    private void logMessage(String msg,Activity activity) {
        logMessage(msg, Toast.LENGTH_SHORT, activity);
    }

    private void logMessage(String msg, int duration, Activity activity) {
        Toast.makeText(activity, msg, duration).show();
    }

}
