package com.example.pdpproject.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pdpproject.LogMsg;
import com.example.pdpproject.R;
import com.example.pdpproject.service.ReadWriteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class AdminPanelActivity extends AppCompatActivity {
    private File adminLog;
    private File fileCSV;
    private ListView listPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        File f = this.getDir("Log", Context.MODE_PRIVATE);
        this.adminLog = new File(f.getAbsolutePath(),"AdminLog.txt");
        this.fileCSV = new File(f.getAbsolutePath(),"AdminLog.csv");
        JSONObject ans = ReadWriteHelper.readInFileASCII(this.adminLog);

        String algorithme = "";
        String nbSkip = "";
        JSONArray tracks ;
        ArrayList<String> arrayTracks = new ArrayList<>();
        String topArtist  = "";
        try {
            algorithme = ans.getString("Algorithme");
            nbSkip = ans.getString("Nb Skip");
            tracks = ans.getJSONArray("Tracks");
            for(int i=0; i<tracks.length(); i++){
                arrayTracks.add(tracks.get(i).toString());
            }
            topArtist = ans.getString("TopArtist");
            Log.d("appdata"," top artist = " + topArtist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView artistName = findViewById(R.id.artistName);
        artistName.setText(topArtist);
        TextView algo = findViewById(R.id.algorithme);
        algo.setText(algorithme);
        TextView skip = findViewById(R.id.nbSkip);
        skip.setText(nbSkip);
        listPeople = findViewById(R.id.listView);

        listPeople.setAdapter(new MyListAdapter(this,R.layout.list_track,arrayTracks));


        ImageView download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportCSV();
            }
        });


    }

    private class MyListAdapter extends ArrayAdapter<String> {
        private int layout;
        private ArrayList<String> users;
        private MyListAdapter(Context context, int resource, ArrayList<String> objects) {
            super(context,resource,objects);
            layout = resource;
            users = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = LayoutInflater.from(getContext());

            View rowView = inflater.inflate(layout,parent,false);

            TextView title = (TextView) rowView.findViewById(R.id.list_track_text);

            title.setText(users.get(position));
            return rowView;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportCSV(){
        File file = this.getExternalFilesDir(DIRECTORY_DOWNLOADS);
        String namefile = "EquifyLog.csv";
        File dst = new File(file.getAbsolutePath(),namefile);
        dst.setWritable(true);
        int i = 0;
        //Si le fichier existe déjà on écrase le précédent
        while(dst.exists()){
            dst.delete();
            namefile = "EquifyLog(" + i + ").csv";
            i++;
            dst = new File(file.getAbsolutePath(),namefile);
        }

        try {

            Files.copy(this.fileCSV.toPath(), dst.toPath());

            //Permet d'afficher le fichier dans le répertoire Downloads
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Downloads.TITLE,namefile);
                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, namefile);
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                contentValues.put(MediaStore.Downloads.SIZE, dst.length());
                ContentResolver database = getContentResolver();
                database.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            }
            else{
                LogMsg.logAppdata("Cannot download logs");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
