package com.example.pdpproject.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.R;
import com.example.pdpproject.algorithms.AdditiveUtilitarianStrategy;
import com.example.pdpproject.algorithms.GenerateScore;
import com.example.pdpproject.apiManager.APIManager;
import com.example.pdpproject.apiManager.APIManagerSpotifyAsyn;

import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.spotify.sdk.android.sub.AuthorizationClient;
import com.spotify.sdk.android.sub.AuthorizationRequest;
import com.spotify.sdk.android.sub.AuthorizationResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class SpotifyPlayer extends Player implements PlayerInterface {

    private SpotifyAppRemote mSpotifyAppRemote;
    private static String SPOTIFY_CLIENT_ID = "6ce146dc122643279ba873f48606e931";
    private static String SPOTIFY_REDIRECT_URI = "equify://callback";
    private static int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private Singleton singleton = Singleton.getInstance();
    private boolean newConnection;
    private final ErrorCallback mErrorCallback = this::logError;
    private int prev_nb_user=0;

    private TrackProgressBar mTrackProgressBar;
    private Subscription<PlayerState> mPlayerStateSubscription;
    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback =
            new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState playerState) {

                    if (!playerState.playbackOptions.isShuffling) {
                        toggleShuffleBtn.setColorFilter(Color.rgb(255,255,255));
                    } else {
                        toggleShuffleBtn.setColorFilter(Color.rgb(0,255,0));
                    }

                    if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                        toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_all);
                        toggleRepeatBtn.setColorFilter(Color.rgb(0,255,0));
                    } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
                        toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_one);
                        toggleRepeatBtn.setColorFilter(Color.rgb(0,255,0));
                    } else {
                        toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_off);
                        toggleRepeatBtn.setColorFilter(Color.rgb(255,255,255));
                    }
                    if(playerState.track!=null){
                        trackTitle.setText( String.format(
                                Locale.FRANCE, "%s\n%s", playerState.track.name, playerState.track.artist.name));
                        trackTitle.setTag(playerState);
                    }


                    // Update progressbar
                    if (playerState.playbackSpeed > 0) {
                        mTrackProgressBar.unpause();
                    } else {
                        mTrackProgressBar.pause();
                    }

                    // Invalidate play / pause
                    if (playerState.isPaused && playerState.track!=null) {
                        playPauseBtn.setImageResource(R.drawable.btn_play);
                    } else {
                        playPauseBtn.setImageResource(R.drawable.btn_pause);
                    }

                    // Get image from track
                    if(playerState.track != null){
                        mSpotifyAppRemote
                                .getImagesApi()
                                .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                                .setResultCallback(
                                        bitmap -> {
                                            Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                                            coverImage.setImageBitmap(bitmap);


                                        });
                        // Invalidate seekbar length and position
                        seekBar.setMax((int) playerState.track.duration);
                        mTrackProgressBar.setDuration(playerState.track.duration);
                        mTrackProgressBar.update(playerState.playbackPosition);
                        seekBar.setEnabled(true);
                    }



                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("appdata", "I am Spotifyplayer!");
        LogMsg.logAppdata("MAIN TOKEN : " + singleton.getMainAccessToken());
        LogMsg.logAppdata("Refresh TOKEN : " + singleton.getRefreshToken());
        apiManagerItf =new APIManager(new APIManagerSpotifyAsyn());
        apiManagerItf.setRequestToken(singleton.getMainAccessToken());
        apiManagerItf.exportModels();
        prev_nb_user++;
        newConnection =false;

    }

    @Override
    protected void onResume() {
        super.onResume();
            SpotifyAppRemote.connect(
                    getApplication(), new ConnectionParams.Builder(SPOTIFY_CLIENT_ID)
                            .setRedirectUri(SPOTIFY_REDIRECT_URI)
                            .showAuthView(false)
                            .build(),
                    new Connector.ConnectionListener() {
                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            mTrackProgressBar = new TrackProgressBar(seekBar);
                            if(singleton.getAllUser().size()>1){
                                setViews(true);
                                if(newConnection){
                                    String playUri = "spotify:playlist:" + singleton.getAppPlaylistId();
                                    LogMsg.logAppdata("play uri " +playUri );
                                    mSpotifyAppRemote.getPlayerApi().play(playUri);
                                    newConnection =false;
                                }
                                int usersSize = singleton.getAllUser().size();
                                if(prev_nb_user>usersSize){
                                    updatePlaylist();
                                    prev_nb_user=usersSize;
                                }
                                mPlayerStateSubscription =
                                        (Subscription<PlayerState>)
                                                mSpotifyAppRemote
                                                        .getPlayerApi()
                                                        .subscribeToPlayerState()
                                                        .setEventCallback(mPlayerStateEventCallback)
                                                        .setLifecycleCallback(
                                                                new Subscription.LifecycleCallback() {
                                                                    @Override
                                                                    public void onStart() {
                                                                    }

                                                                    @Override
                                                                    public void onStop() {
                                                                    }
                                                                })
                                                        .setErrorCallback(
                                                                throwable -> {
                                                                    logError(throwable);
                                                                });
                            }


                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
                            logError(throwable);
                        }
                    }
            );


    }

    @Override
    public void onToggleShuffleButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .toggleShuffle()
                .setErrorCallback(mErrorCallback);
    }

    @Override
    public void onSkipPreviousButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .skipPrevious()
                .setErrorCallback(mErrorCallback);
        mSpotifyAppRemote.getUserApi().getCapabilities();
    }

    @Override
    public void onPlayPauseButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .getPlayerState()
                .setResultCallback(
                        playerState -> {
                            if (playerState.isPaused) {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .resume()
                                        .setErrorCallback(mErrorCallback);
                            } else {
                                mSpotifyAppRemote
                                        .getPlayerApi()
                                        .pause()
                                        .setErrorCallback(mErrorCallback);
                            }
                        });

    }

    @Override
    public void onSkipNextButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .skipNext()
                .setErrorCallback(mErrorCallback);
    }

    @Override
    public void onToggleRepeatButtonClicked(View view) {
        mSpotifyAppRemote
                .getPlayerApi()
                .toggleRepeat()
                .setErrorCallback(mErrorCallback);
    }

    @Override
    public void onAddUserClicked(View view) {
        AuthorizationClient.clearCookies(this);
        getMainToken();
    }

    /**
     * Get access token of new user from API
     */
    private void getMainToken() {
        final AuthorizationRequest request = getSpotifyAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }
    private AuthorizationRequest getSpotifyAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(SPOTIFY_CLIENT_ID, type, SPOTIFY_REDIRECT_URI)
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-follow-read", "playlist-modify-public"})
                .setCampaign("your-campaign-token")
                .build();
    }


    @Override
    public void updatePlaylist() {
        // wait for algo
        LogMsg.logAppdata("playlist updating");
        GenerateScore generateScore = new GenerateScore(apiManagerItf);
        int playlistSize = singleton.getConfig().getPlaylistSize();
        ArrayList<Track> tracks = generateScore.generatePlaylist(new AdditiveUtilitarianStrategy(),playlistSize);
        Collections.sort(tracks);
        apiManagerItf.addTracksToAppPlaylist(tracks,false);
        newConnection=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (response.getType().toString().equals("empty")) {
            return;
        }
        if (response.getError() != null && response.getError().isEmpty()) {
            throw new RuntimeException(response.getError());
        }
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            String mAccessToken = response.getAccessToken();
            apiManagerItf.setRequestToken(mAccessToken);
            apiManagerItf.exportModels();
            prev_nb_user++;
            overridePendingTransition(0, 0);
            updatePlaylist();
        }
    }


    @Override
    protected void onDestroy() {
        mSpotifyAppRemote
                .getPlayerApi()
                .pause()
                .setErrorCallback(mErrorCallback);
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        LogMsg.logAppdata("destroy");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

    }


    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;

        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mSpotifyAppRemote
                                .getPlayerApi()
                                .seekTo(seekBar.getProgress())
                                .setErrorCallback(mErrorCallback);
                    }
                };

        private final Runnable mSeekRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        int progress = mSeekBar.getProgress();
                        mSeekBar.setProgress(progress + LOOP_DURATION);
                        mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
                    }
                };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }

        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }
}
