package com.example.pdpproject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.pdpproject.LogMsg;
import com.deezer.sdk.model.Permissions;
import com.deezer.sdk.network.connect.DeezerConnect;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.connect.event.DialogListener;
import com.example.pdpproject.repo.Singleton;

import java.util.concurrent.Semaphore;


public class DeezerLogin {

    // The access token would not expire by using offline access
    private static String[] PERMISSIONS = new String[]{
            Permissions.BASIC_ACCESS,
            Permissions.OFFLINE_ACCESS,
            Permissions.DELETE_LIBRARY,
            Permissions.LISTENING_HISTORY,
            Permissions.MANAGE_COMMUNITY,
            Permissions.EMAIL,
            Permissions.MANAGE_LIBRARY
    };



    private DeezerConnect mDeezerConnect = null;
    public static final String SAMPLE_APP_ID = "398164";

    private Activity activity = null;
    private Singleton singleton = Singleton.getInstance();
    private String token;
    private boolean isMainUser;
    private Semaphore semaphore;

    public void setmDeezerConnect(DeezerConnect mDeezerConnect) {
        this.mDeezerConnect = mDeezerConnect;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void initConnector(Activity activity) {
        this.activity = activity;
        mDeezerConnect = new DeezerConnect(activity, SAMPLE_APP_ID);
    }

    public void mainConnection() {
//      mDeezerConnect.logout(activity);
        isMainUser=true;
        mDeezerConnect.authorize(activity, PERMISSIONS, mDeezerDialogListener);
    }
    public void subConnection(Semaphore semaphore){
        this.semaphore = semaphore;
        isMainUser=false;
        mDeezerConnect.authorize(activity, PERMISSIONS, mDeezerDialogListener);
    }


    public String getToken() {
        return token;
    }

    private void startNextActivity(Class platform) {
        Intent intent = new Intent(activity, platform);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    /**
     * A listener for the Deezer Login Dialog
     */
    private DialogListener mDeezerDialogListener = new DialogListener() {

        @Override
        public void onComplete(final Bundle values) {
            token = mDeezerConnect.getAccessToken();
            LogMsg.logAppdata("deezer access token : " + mDeezerConnect.getAccessToken());
            if(isMainUser){
                // store the current authentication info
                SessionStore sessionStore = new SessionStore();
                sessionStore.save(mDeezerConnect, activity);
                singleton.setMainAccessToken(mDeezerConnect.getAccessToken());
                // Launch the Home activity
                startNextActivity(DeezerPlayer.class);
            }else{
                semaphore.release();
            }

        }

        @Override
        public void onException(final Exception exception) {
            Toast.makeText(activity, "Deezer error during login",
                    Toast.LENGTH_LONG).show();
        }


        @Override
        public void onCancel() {
            Toast.makeText(activity, "Login cancelled", Toast.LENGTH_LONG).show();
        }


    };
}
