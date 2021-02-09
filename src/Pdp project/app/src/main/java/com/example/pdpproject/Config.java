package com.example.pdpproject;

public class Config {

    public static String appPlaylistName ="EquifyPlaylist";

    private int playlistSize = 20; //Default value

    public Config() {
    }

    public int getPlaylistSize() {
        return playlistSize;
    }

    public void setPlaylistSize(int playlistSize) {
        this.playlistSize = playlistSize;
    }
}
