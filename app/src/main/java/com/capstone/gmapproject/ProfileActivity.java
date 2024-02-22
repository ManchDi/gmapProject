package com.capstone.gmapproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class ProfileActivity extends AppCompatActivity {

    private Queue<HistoryEntry> history;
    private DbConnector dbConnector;
    private Cursor cursor;

    private SQLiteDatabase chargeChartsDB = dbConnector.getReadableDatabase();
    private String username;
    public TextView username_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        cursor = chargeChartsDB.rawQuery("SELECT st_id\n" +
                                            "FROM chargers\n" +
                                            "WHERE ch_id = " + ID, null);
        String address = cursor.getString(0);
        cursor = chargeChartsDB.rawQuery("SELECT charger_type\n" +
                                            "FROM chargers\n" +
                                            "WHERE ch_id = " + ID, null);
        String type = cursor.getString(0);
        cursor = chargeChartsDB.rawQuery("SELECT connection_type\n" +
                                            "FROM chargers\n" +
                                            "WHERE ch_id = " + ID, null);
        String charger = cursor.getString(0);
        HistoryEntry entry = new HistoryEntry(address, type, charger);
        this.addEntry(entry);
    }
}
