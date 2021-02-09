package com.example.pdpproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.pdpproject.R;
import com.example.pdpproject.models.Logs;
import com.example.pdpproject.models.Track;
import com.example.pdpproject.models.tracks.TrackSpotify;
import com.example.pdpproject.service.ReadWriteHelper;


import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LoginPageActivity extends AppCompatActivity{

    private boolean isLogged=false;
    private SpotifyLogin spotifyLogin = new SpotifyLogin();
    private DeezerLogin deezerLogin = new DeezerLogin();
    private Class platform;

//    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //Create log file
        File f = this.getDir("Log", Context.MODE_PRIVATE);

        File adminLog = new File(f.getAbsolutePath(),"AdminLog.txt");
        File logCSV = new File(f.getAbsolutePath(),"AdminLog.csv");
        Logs log = new Logs(logCSV,adminLog,"no algo");
        log.addTrack(new Track("666","yo","65665",10,new TrackSpotify()));
        new ReadWriteHelper.AsyncTaskLogs().execute(log);

        CardView spotifyLoginBtn = findViewById(R.id.spotify_login);
        CardView deezerLoginBtn = findViewById(R.id.deezer_login);
        CardView logsBtn = findViewById(R.id.logs);

        spotifyLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSpotifyConnect();
            }
        });

        deezerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeezerConnect();
            }
        });

        logsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogsButtonClick();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLogged){
            startActivity(new Intent(this,platform));
        }
    }

    public void onSpotifyConnect() {
        platform =SpotifyPlayer.class;
        spotifyLogin.connection(getApplication(),this);

    }

    public void onDeezerConnect() {
        platform =DeezerPlayer.class;
        deezerLogin.initConnector(this);
        deezerLogin.mainConnection();
    }

    public void onLogsButtonClick() {
        Intent intent = new Intent(LoginPageActivity.this, AdminPanelActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SpotifyLogin.SPOTIFY_AUTH_TOKEN_REQUEST_CODE){
            spotifyLogin.setAccessToken(resultCode,data);
            spotifyLogin.queryRequestCode(this);
        }
        if(requestCode == SpotifyLogin.SPOTIFY_AUTH_CODE_REQUEST_CODE){
            if(spotifyLogin.setCode(resultCode,data)){
                isLogged =true;
                gotoHomePage(SpotifyPlayer.class);
            };

        }
    }

    public void gotoHomePage(Class platform) {

        Intent intent = new Intent(this, platform);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }


}
