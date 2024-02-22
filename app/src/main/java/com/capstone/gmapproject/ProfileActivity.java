package com.capstone.gmapproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class ProfileActivity extends AppCompatActivity {

    private Queue<HistoryEntry> history;
    private DbConnector dbConnector;
    private Cursor cursor;

    private SQLiteDatabase chargeChartsDB;
    private String username;
    public TextView username_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dbConnector = new DbConnector(this);
        //Button for switching between Profile and Main
        Button goBackButton = (Button) findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        username_box = (TextView) findViewById(R.id.username_box);
        username_box.setText(username);


    }

    private void addEntry(HistoryEntry entry){
        history.add(entry);
        if(history.size() > 5){
            history.remove();
        }
    }

    private void getEntry(int ID){
        ArrayList<String> entries =dbConnector.getHistoryDatum(ID);
        if (entries.size() >= 3) {
            String address = entries.get(0);
            String type = entries.get(1);
            String charger = entries.get(2);

            HistoryEntry entry = new HistoryEntry(address, type, charger);
            this.addEntry(entry);
        } else {
            // Handle the case where the returned ArrayList doesn't contain enough elements
            Log.e("dbHelper", "Insufficient data returned from getHistoryDatum");
        }
    }
}
