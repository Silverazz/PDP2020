package com.example.pdpproject.activities;

import android.view.View;

public interface PlayerInterface {
   void onToggleShuffleButtonClicked(View view);
   void onSkipPreviousButtonClicked(View view);
   void onPlayPauseButtonClicked(View view);
   void onSkipNextButtonClicked(View view);
   void onToggleRepeatButtonClicked(View view);
   void onAddUserClicked(View view);
   void updatePlaylist();
}
