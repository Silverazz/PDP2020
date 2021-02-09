package com.example.pdpproject.service;


import android.os.AsyncTask;

import com.example.pdpproject.models.Logs;
import com.example.pdpproject.models.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ReadWriteHelper {

    private static void writeLogsInFileJSON(Logs log){

        JSONObject json = new JSONObject();

        ArrayList<Track> tracks = log.getAllTracks();
        Iterator<Track> iterator = tracks.iterator();

        JSONArray jsonArray = new JSONArray();
        while (iterator.hasNext()){
            String trackName = iterator.next().getName();
            jsonArray.put(trackName);
        }

        try {
            PrintWriter writer = new PrintWriter(log.getFileJSON());
            json.put("Algorithme",log.getAlgorithme());
            json.put("Nb Skip",log.getNbSkip());
            json.put("Tracks",jsonArray);
            json.put("TopArtist",log.getTopArtist());
            writer.println(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeLogsInFileCSV(Logs log){

        try {
            PrintWriter writer = new PrintWriter(log.getFileCSV());
            String ans = "Algorithme," + log.getAlgorithme();
            writer.println(ans);
            ans = "Nb skip," + log.getNbSkip();
            writer.println(ans);
            ans = "Id,Name";
            writer.println(ans);


            ArrayList<Track> tracks = log.getAllTracks();
            Iterator<Track> iterator = tracks.iterator();
            int id = 0;

            while(iterator.hasNext()){
                id++;
                ans = id + "," + iterator.next().getName();
                writer.println(ans);
            }
            writer.println("Top Artist" + "," + log.getTopArtist());
            writer.close();

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
    }

    public static class AsyncTaskLogs extends AsyncTask<Logs, Void, Integer> {

        protected Integer doInBackground(Logs... logs){

            for(Logs log : logs){
                writeLogsInFileCSV(log);
                writeLogsInFileJSON(log);
            }

            return new Integer(0);
        }

    }

    public static JSONObject readInFileASCII(File file){
        String ans = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Scanner scanner = new Scanner(reader);
            while(scanner.hasNext())
                ans += scanner.nextLine();
            scanner.close();
            ans = ans.replaceAll("[\t ]", "");
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(ans);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
