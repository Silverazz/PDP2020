package com.example.pdpproject.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdpproject.R;
import com.example.pdpproject.apiManager.APIManagerItf;

public class Player extends AppCompatActivity{

    protected TextView trackTitle;
    protected ImageView coverImage;
    protected SeekBar seekBar;
    protected ImageButton toggleShuffleBtn;
    protected ImageButton skipPrevBtn;
    protected ImageButton playPauseBtn;
    protected ImageButton skipNextBtn;
    protected ImageButton toggleRepeatBtn;
    protected APIManagerItf apiManagerItf;

    private static final String TAG = Player.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        trackTitle =  findViewById(R.id.current_track_label);
        coverImage = findViewById(R.id.cover_image);
        seekBar =  findViewById(R.id.seek_bar);
        toggleShuffleBtn =  findViewById(R.id.toggle_shuffle_button);
        skipPrevBtn =  findViewById(R.id.skip_prev_button);
        playPauseBtn =  findViewById(R.id.play_pause_button);
        skipNextBtn =  findViewById(R.id.skip_next_button);
        toggleRepeatBtn =  findViewById(R.id.toggle_repeat_button);
        setViews(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        setViews(true);
    }

    protected void setViews(boolean val){
        coverImage.setImageResource(R.drawable.widget_placeholder);
        trackTitle.setVisibility(View.VISIBLE);
        seekBar.setEnabled(val);
        toggleShuffleBtn.setEnabled(val);
        skipPrevBtn.setEnabled(val);
        playPauseBtn.setEnabled(val);
        skipNextBtn.setEnabled(val);
        toggleRepeatBtn.setEnabled(val);
    }


    public void onListUserClicked(View view){
        Intent intent = new Intent(this, ListUsersActivity.class);
        startActivity(intent);
    }

    public void onSettingClicked(View view){
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        setViews(false);
    }


    protected void logError(Throwable throwable) {
        Toast.makeText(this, R.string.err_generic_toast, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "", throwable);
    }

    protected void logMessage(String msg) {
        logMessage(msg, Toast.LENGTH_SHORT);
    }

    private void logMessage(String msg, int duration) {
        Toast.makeText(this, msg, duration).show();
        Log.d(TAG, msg);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).create().show();
    }
    /**
     * Handle errors by displaying a toast and logging.
     *
     * @param exception
     *            the exception that occured while contacting Deezer services.
     */
    protected void handleError(final Exception exception) {
        String message = exception.getMessage();
        if (TextUtils.isEmpty(message)) {
            message = exception.getClass().getName();
        }

        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        ((TextView) toast.getView().findViewById(android.R.id.message)).setTextColor(Color.RED);
        toast.show();

        Log.e("BaseActivity", "Exception occured " + exception.getClass().getName(), exception);
    }
}
