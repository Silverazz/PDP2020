package com.example.pdpproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.pdpproject.Config;
import com.example.pdpproject.LogMsg;
import com.example.pdpproject.R;
import com.example.pdpproject.repo.Singleton;

public class Setting extends AppCompatActivity {
    EditText playlist_size;
    Config config = Singleton.getInstance().getConfig();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        playlist_size= findViewById(R.id.setting_playlist_size_data);
        playlist_size.setText(""+config.getPlaylistSize());
        playlist_size.setSelection(playlist_size.length());
    }

    public void onSaveClicked(View view){
        Integer playlistSize = Integer.parseInt(playlist_size.getText().toString());
        config.setPlaylistSize(playlistSize);
        LogMsg.logAppdata("size is " + playlistSize);
        onBackPressed();
    }
}
