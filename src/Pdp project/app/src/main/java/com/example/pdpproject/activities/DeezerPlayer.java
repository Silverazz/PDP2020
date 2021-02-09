package com.example.pdpproject.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.player.PlayerWrapper;
import com.deezer.sdk.player.PlaylistPlayer;
import com.deezer.sdk.player.event.BufferState;
import com.deezer.sdk.player.event.OnBufferErrorListener;
import com.deezer.sdk.player.event.OnBufferProgressListener;
import com.deezer.sdk.player.event.OnBufferStateChangeListener;
import com.deezer.sdk.player.event.OnPlayerErrorListener;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.event.PlayerWrapperListener;
import com.deezer.sdk.player.exception.NotAllowedToPlayThatSongException;
import com.deezer.sdk.player.exception.StreamLimitationException;
import com.deezer.sdk.player.exception.TooManyPlayersExceptions;
import com.deezer.sdk.player.networkcheck.WifiAndMobileNetworkStateChecker;
import com.example.pdpproject.LogMsg;
import com.example.pdpproject.R;
import com.example.pdpproject.algorithms.AdditiveUtilitarianStrategy;
import com.example.pdpproject.algorithms.GenerateScore;
import com.example.pdpproject.apiManager.APIManager;
import com.example.pdpproject.apiManager.APIManagerDeezer;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.repo.Singleton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Semaphore;

public class DeezerPlayer extends Player
        implements
        PlayerInterface,
        PlayerWrapperListener,
        OnPlayerProgressListener {

    private boolean isMainUserSaved=false;
    private DeezerLogin deezerLogin;
    private Singleton singleton = Singleton.getInstance();
    private int prev_nb_user=0;

    private PlaylistPlayer mPlaylistPlayer;
    private PlayerHandler mPlayerHandler = new PlayerHandler();
    private PlayerWrapper mPlayer;
    private DeezerConnect mDeezerConnect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deezerLogin = new DeezerLogin();
        deezerLogin.initConnector(this);
        setViews(false);
        mDeezerConnect = new DeezerConnect(this,DeezerLogin.SAMPLE_APP_ID);
        new SessionStore().restore(mDeezerConnect,this);
        LogMsg.logAppdata("new user " + mDeezerConnect.getCurrentUser().getName());
        isMainUserSaved =false;

        createPlayer();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                apiManagerItf = new APIManager(new APIManagerDeezer());
                apiManagerItf.setRequestToken(singleton.getMainAccessToken());
                apiManagerItf.exportModels();
                setPrev_nb_user(prev_nb_user+1);
                LogMsg.logAppdata("finish export models");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleShuffleBtn.setVisibility(View.INVISIBLE);
        if(isMainUserSaved){
            mPlayer.pause();
            long playlistis= Long.parseLong(singleton.getAppPlaylistId());
            int userSize = singleton.getAllUser().size();
            if(prev_nb_user>userSize && userSize>1){
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        updatePlaylist();
                        setPrev_nb_user(prev_nb_user-1);
                        mPlaylistPlayer.playPlaylist(playlistis);
                    }
                });

            }else {
                mPlaylistPlayer.playPlaylist(playlistis);
            }

        }

    }

    public synchronized void setPrev_nb_user(int prev_nb_user) {
        this.prev_nb_user = prev_nb_user;
    }

    /**
     * Creates the PlaylistPlayer
     */
    private void createPlayer() {
        try {
            mPlaylistPlayer = new PlaylistPlayer(getApplication(), mDeezerConnect,
                    new WifiAndMobileNetworkStateChecker());
            mPlaylistPlayer.addPlayerListener(this);
            mPlaylistPlayer.addOnPlayerProgressListener(this);
            setAttachedPlayer(mPlaylistPlayer);
        }
        catch (TooManyPlayersExceptions e) {
            handleError(e);
        }
        catch (DeezerError e) {
            handleError(e);
        }


    }

    private void setAttachedPlayer(final PlayerWrapper player) {
        mPlayer = player;
        player.addOnBufferErrorListener(mPlayerHandler);
        player.addOnBufferStateChangeListener(mPlayerHandler);
        player.addOnBufferProgressListener(mPlayerHandler);

        player.addOnPlayerErrorListener(mPlayerHandler);
        player.addOnPlayerStateChangeListener(mPlayerHandler);
        player.addOnPlayerProgressListener(mPlayerHandler);

        if (mPlayer.isAllowedToSeek()) {
            seekBar.setEnabled(true);
        }
    }

    @Override
    public void onToggleShuffleButtonClicked(View view) {
        //shuffle is not available on deezer sdk
    }

    @Override
    public void onSkipPreviousButtonClicked(View view) {
        mPlaylistPlayer.skipToPreviousTrack();
    }

    @Override
    public void onPlayPauseButtonClicked(View view) {
        if ((mPlayer.getPlayerState() == PlayerState.PLAYING)) {
            mPlayer.pause();
        } else {
            mPlayer.play();
        }
    }

    @Override
    public void onSkipNextButtonClicked(View view) {
        mPlaylistPlayer.skipToNextTrack();
    }

    @Override
    public void onToggleRepeatButtonClicked(View view) {
        PlayerWrapper.RepeatMode current = mPlayer.getRepeatMode();
        PlayerWrapper.RepeatMode next;

        switch (current) {
            case NONE:
                next = PlayerWrapper.RepeatMode.ONE;
                toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_one);
                toggleRepeatBtn.setColorFilter(Color.rgb(0,255,0));
                break;
            case ONE:
                next = PlayerWrapper.RepeatMode.ALL;
                toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_all);
                toggleRepeatBtn.setColorFilter(Color.rgb(0,255,0));
                break;
            case ALL:
            default:
                next = PlayerWrapper.RepeatMode.NONE;
                toggleRepeatBtn.setImageResource(R.drawable.mediaservice_repeat_off);
                toggleRepeatBtn.setColorFilter(Color.rgb(255,255,255));
                break;
        }
        mPlayer.setRepeatMode(next);
    }

    @Override
    public void onAddUserClicked(View view) {
        new SessionStore().clear(this);
        Semaphore semaphore = new Semaphore(0);
        deezerLogin.subConnection(semaphore);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    // save other user's info
                    apiManagerItf.setRequestToken(deezerLogin.getToken());
                    apiManagerItf.exportModels();
                    updatePlaylist();
                    long playlistID= Long.parseLong(singleton.getAppPlaylistId());
                    mPlaylistPlayer.playPlaylist(playlistID);
                    setPrev_nb_user(prev_nb_user+1);
                    isMainUserSaved=true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void updatePlaylist() {
        LogMsg.logAppdata("playlist updating");
        GenerateScore generateScore = new GenerateScore(apiManagerItf);
        int playlistSize = singleton.getConfig().getPlaylistSize();
        ArrayList<Track> tracks = generateScore.generatePlaylist(new AdditiveUtilitarianStrategy(),playlistSize);
        Collections.sort(tracks);
        apiManagerItf.addTracksToAppPlaylist(tracks,false);
    }


    // player wrapper
    @Override
    public void onPlayerProgress(long l) {

    }

    @Override
    public void onAllTracksEnded() {

    }

    @Override
    public void onPlayTrack(PlayableEntity playableEntity) {

    }

    @Override
    public void onTrackEnded(PlayableEntity playableEntity) {

    }

    @Override
    public void onRequestException(Exception e, Object o) {

    }


    @Override
    public void onBackPressed() {

    }

    protected void setViews(boolean val,boolean setCover) {
        if(setCover)coverImage.setImageResource(R.drawable.widget_placeholder);
        trackTitle.setVisibility(View.VISIBLE);
        seekBar.setEnabled(val);
        toggleShuffleBtn.setEnabled(val);
        skipPrevBtn.setEnabled(val);
        playPauseBtn.setEnabled(val);
        skipNextBtn.setEnabled(val);
        toggleRepeatBtn.setEnabled(val);
    }

    @Override
    protected void onDestroy() {
        doDestroyPlayer();
        super.onDestroy();
    }

    /**
     * Will destroy player.
     */
    private void doDestroyPlayer() {

        if (mPlayer == null) {
            // No player, ignore
            return;
        }

        if (mPlayer.getPlayerState() == PlayerState.RELEASED) {
            // already released, ignore
            return;
        }

        // first, stop the player if it is not
        if (mPlayer.getPlayerState() != PlayerState.STOPPED) {
            mPlayer.stop();
        }

        // then release it
        mPlayer.release();
    }

    /**
     * Handler for messages sent by the player and buffer
     */
    private class PlayerHandler
            implements
            OnPlayerProgressListener,
            OnBufferProgressListener,
            OnPlayerStateChangeListener,
            OnPlayerErrorListener,
            OnBufferStateChangeListener,
            OnBufferErrorListener {

        @Override
        public void onBufferError(final Exception ex, final double percent) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    handleError(ex);
                }
            });
        }

        @Override
        public void onBufferStateChange(final BufferState state, final double percent) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showBufferProgress((int) Math.round(percent));
                }
            });
        }

        /**
         * displays the current progression of the buffer
         *
         * @param position
         */
        private void showBufferProgress(final int position) {
            synchronized (this) {
                if (mPlayer != null) {
                    if (position > 0) {
                        showTrackDuration(mPlayer.getTrackDuration());
                    }
                    long progress = (position * mPlayer.getTrackDuration()) / 100;
                    seekBar.setSecondaryProgress((int) progress / 1000);
                }
            }
        }
        /**
         *
         * @param trackLength
         */
        public void showTrackDuration(final long trackLength) {
            seekBar.setMax((int) trackLength / 1000);
        }

        @Override
        public void onPlayerError(final Exception ex, final long timePosition) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    handleError(ex);
                    if (ex instanceof NotAllowedToPlayThatSongException) {
                        mPlayer.skipToNextTrack();
                    } else if (ex instanceof StreamLimitationException) {
                        // Do nothing ,
                    } else {
                        finish();
                    }
                }
            });
        }

        @Override
        public void onPlayerStateChange(final PlayerState state, final long timePosition) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showPlayerState(state);
                    showPlayerProgress(timePosition);
                }
            });
        }

        /**
         * Displays the current state of the player
         *
         * @param state
         *            the current player's state
         */
        public void showPlayerState(final PlayerState state) {
            seekBar.setEnabled(true);
            playPauseBtn.setEnabled(false);

            switch (state) {
                case STARTED:
                case INITIALIZING:
                case PAUSED:
                case PLAYBACK_COMPLETED:
                    setViews(true,false);
                    playPauseBtn.setEnabled(true);
                    playPauseBtn.setImageResource(R.drawable.btn_play);
                    String title = mPlaylistPlayer.getCurrentTrack().getTitle()+" - " +mPlaylistPlayer.getCurrentTrack().getArtist().getName();
                    trackTitle.setText(title);
                    new DownloadImageTask(coverImage).execute(mPlaylistPlayer.getCurrentTrack().getAlbum().getBigImageUrl());
                    break;
                case READY:
                    setViews(true,false);
                    playPauseBtn.setEnabled(true);
                    playPauseBtn.setImageResource(R.drawable.btn_play);
                    showPlayerProgress(0);
                    title = mPlaylistPlayer.getCurrentTrack().getTitle()+" - " +mPlaylistPlayer.getCurrentTrack().getArtist().getName();
                    trackTitle.setText(title);
                    new DownloadImageTask(coverImage).execute(mPlaylistPlayer.getCurrentTrack().getAlbum().getBigImageUrl());
                    break;
                case PLAYING:
                    setViews(true,false);
                    playPauseBtn.setEnabled(true);
                    playPauseBtn.setImageResource(R.drawable.btn_pause);
                    title = mPlaylistPlayer.getCurrentTrack().getTitle()+ " - " +mPlaylistPlayer.getCurrentTrack().getArtist().getName();
                    trackTitle.setText(title);
//                    new DownloadImageTask(coverImage).execute(mPlaylistPlayer.getCurrentTrack().getAlbum().getBigImageUrl());
                    break;
                case WAITING_FOR_DATA:
                    playPauseBtn.setEnabled(false);
                    break;

                case STOPPED:
                    seekBar.setEnabled(false);
                    showPlayerProgress(0);
                    showBufferProgress(0);
                    playPauseBtn.setImageResource(R.drawable.btn_play);
                    break;
                default:
                    break;
            }
        }


        /**
         * Displays the current progression of the playback
         *
         * @param timePosition
         *            the current player's state
         */
        public void showPlayerProgress(final long timePosition) {
            seekBar.setProgress((int) timePosition / 1000);
            seekBar.setEnabled(true);
        }

        @Override
        public void onBufferProgress(final double percent) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showBufferProgress((int) Math.round(percent));
                }
            });
        }

        @Override
        public void onPlayerProgress(final long timePosition) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showPlayerProgress(timePosition);
                }
            });
        }
    }

    /**
     * convert online image to a bitmap form
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Bitmap resized = Bitmap.createScaledBitmap(result, 600, 600, true);
            bmImage.setImageBitmap(resized);
        }
    }
}
