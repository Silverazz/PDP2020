package com.example.pdpproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pdpproject.R;
import com.example.pdpproject.repo.Singleton;

import java.util.ArrayList;
import java.util.ListIterator;

import com.example.pdpproject.models.User;

public class ListUsersActivity extends AppCompatActivity {

    private ArrayList<User> user_list;
    private ListView listPeople;
    private Singleton singleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listusers);

        singleton = Singleton.getInstance();
        user_list =  singleton.getOtherUser();

        ArrayList<String> users_names = new ArrayList<>();

        for(User user : user_list){
            users_names.add(user.getName());
        }

        listPeople = findViewById(R.id.listView);

        listPeople.setAdapter(new MyListAdapter(this,R.layout.list_item,users_names));

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

            TextView title = (TextView) rowView.findViewById(R.id.list_item_text);
            Button button = (Button) rowView.findViewById(R.id.list_item_button);

            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                    suppress(position);
                }
            });
            title.setText(users.get(position));
            return rowView;
        }

        public void suppress(int position){

            overridePendingTransition(0, 0);
            User user_to_delete = user_list.get(position);
            singleton.deleteUser(user_to_delete);
            finish();
//            startActivity(getIntent());
//            overridePendingTransition(0, 0);
        }
    }
}