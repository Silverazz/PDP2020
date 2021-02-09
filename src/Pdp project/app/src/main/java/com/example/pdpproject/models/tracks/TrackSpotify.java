package com.example.pdpproject.models.tracks;

import com.example.pdpproject.models.modelInterface.TrackModelItf;


import java.util.ArrayList;

public class TrackSpotify implements TrackModelItf {
    double acousticness;
    double danceability;
    double energy;
    double speechiness;
    double valence;

    public TrackSpotify(float acousticness, float danceability, float energy, float speechiness, float valence) {
        this.acousticness = acousticness;
        this.danceability = danceability;
        this.energy = energy;
        this.speechiness = speechiness;
        this.valence = valence;
    }

    public TrackSpotify() {
        acousticness = 0;
        danceability = 0;
        energy = 0;
        speechiness = 0;
        valence = 0;
    }

    @Override
    public ArrayList<Double> getScores() {
        ArrayList scores = new ArrayList();
        scores.add(acousticness);
        scores.add(danceability);
        scores.add(energy);
        scores.add(speechiness);
        scores.add(valence);
        return scores;
    }



    public double getAcousticness() {
        return acousticness;
    }

    public void setAcousticness(double acousticness) {
        this.acousticness = acousticness;
    }

    public double getDanceability() {
        return danceability;
    }

    public void setDanceability(double danceability) {
        this.danceability = danceability;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public double getSpeechiness() {
        return speechiness;
    }

    public void setSpeechiness(double speechiness) {
        this.speechiness = speechiness;
    }

    public double getValence() {
        return valence;
    }

    public void setValence(double valence) {
        this.valence = valence;
    }



}
