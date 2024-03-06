package com.capstone.gmapproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Queue;

public class ProfileActivity extends AppCompatActivity {

    private Queue<HistoryEntry> history;
    private List<HistoryEntry> historyNew;
    private DbConnector dbConnector;
    private Cursor cursor;

    private SQLiteDatabase chargeChartsDB;
    private String username;
    private int userID;

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

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                MainActivity.setLoggedIn(false);
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        //set the username text field to the users username
        TextView txtUsername = (TextView) findViewById(R.id.txt_username);
        txtUsername.setText(MainActivity.getUsername());

        userID = MainActivity.getUserID();
        Log.d("dbHelper","beforeHistory");
        historyNew=dbConnector.getHistory(userID);
        Log.d("dbHelper","gotHistory");
        RecyclerView recyclerView = findViewById(R.id.recycler_view_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HistoryViewer adapter = new HistoryViewer(historyNew, history -> {
            // Item click
            Log.d("dbConnector", "tapped history");
        });
        recyclerView.setAdapter(adapter);
        /*

        //build an array list to store the users history, pulls one type of information for each entry at a time
        int[] userHistory = new int[5];
        int totalNum = 0;

        //first query for the station id's then get their names

        String query = "SELECT station_id from history WHERE user_id = ? ORDER BY history_id DESC";

        chargeChartsDB = dbConnector.getReadableDatabase();

        cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userID)});

        if(cursor.moveToFirst())
        {
            userHistory[0] = cursor.getInt(0);
            totalNum++;
            //move results into the userHistory array
            for(int i = 1; i < 5; i++)
            {
                if(cursor.moveToNext())
                {
                    userHistory[i] = cursor.getInt(0);
                    totalNum++;
                }
            }

            if(totalNum > 0)
            {
                query = "SELECT name from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[0])});

                TextView h1Name = (TextView) findViewById(R.id.h1Name);
                cursor.moveToFirst();
                h1Name.setText(cursor.getString(0));

                query = "SELECT address from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[0])});

                TextView h1Address = (TextView) findViewById(R.id.h1Address);
                cursor.moveToFirst();
                h1Address.setText(cursor.getString(0));

                Button h1Button = (Button) findViewById(R.id.h1Button);
                h1Button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        String address = "http://maps.google.co.in/maps?q=" + h1Address.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                        startActivity(i);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("user_id", userID);
                        contentValues.put("station_id", userHistory[0]);
                        long result = chargeChartsDB.insert("history", null, contentValues);
                    }
                });

                h1Button.setVisibility(View.VISIBLE);
            }


            if(totalNum > 1)
            {
                query = "SELECT name from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[1])});

                TextView h2Name = (TextView) findViewById(R.id.h2Name);
                cursor.moveToFirst();
                h2Name.setText(cursor.getString(0));

                query = "SELECT address from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[1])});

                TextView h2Address = (TextView) findViewById(R.id.h2Address);
                cursor.moveToFirst();
                h2Address.setText(cursor.getString(0));

                Button h2Button = (Button) findViewById(R.id.h2Button);
                h2Button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("user_id", userID);
                        contentValues.put("station_id", userHistory[1]);
                        long result = chargeChartsDB.insert("history", null, contentValues);

                        String address = "http://maps.google.co.in/maps?q=" + h2Address.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                        startActivity(i);
                    }
                });

                h2Button.setVisibility(View.VISIBLE);
            }

            if(totalNum > 2)
            {
                query = "SELECT name from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[2])});

                TextView h3Name = (TextView) findViewById(R.id.h3Name);
                cursor.moveToFirst();
                h3Name.setText(cursor.getString(0));

                query = "SELECT address from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[2])});

                TextView h3Address = (TextView) findViewById(R.id.h3Address);
                cursor.moveToFirst();
                h3Address.setText(cursor.getString(0));

                Button h3Button = (Button) findViewById(R.id.h3Button);
                h3Button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("user_id", userID);
                        contentValues.put("station_id", userHistory[2]);
                        long result = chargeChartsDB.insert("history", null, contentValues);

                        String address = "http://maps.google.co.in/maps?q=" + h3Address.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                        startActivity(i);
                    }
                });

                h3Button.setVisibility(View.VISIBLE);
            }

            if(totalNum > 3)
            {
                query = "SELECT name from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[3])});

                TextView h4Name = (TextView) findViewById(R.id.h4Name);
                cursor.moveToFirst();
                h4Name.setText(cursor.getString(0));

                query = "SELECT address from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[3])});

                TextView h4Address = (TextView) findViewById(R.id.h4Address);
                cursor.moveToFirst();
                h4Address.setText(cursor.getString(0));

                Button h4Button = (Button) findViewById(R.id.h4Button);
                h4Button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("user_id", userID);
                        contentValues.put("station_id", userHistory[3]);
                        long result = chargeChartsDB.insert("history", null, contentValues);

                        String address = "http://maps.google.co.in/maps?q=" + h4Address.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                        startActivity(i);
                    }
                });

                h4Button.setVisibility(View.VISIBLE);
            }
            if(totalNum > 4)
            {
                query = "SELECT name from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[4])});

                TextView h5Name = (TextView) findViewById(R.id.h5Name);
                cursor.moveToFirst();
                h5Name.setText(cursor.getString(0));

                query = "SELECT address from stations WHERE st_id = ?";

                cursor = chargeChartsDB.rawQuery(query, new String[]{String.valueOf(userHistory[4])});

                TextView h5Address = (TextView) findViewById(R.id.h5Address);
                cursor.moveToFirst();
                h5Address.setText(cursor.getString(0));

                Button h5Button = (Button) findViewById(R.id.h5Button);
                h5Button.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("user_id", userID);
                        contentValues.put("station_id", userHistory[4]);
                        long result = chargeChartsDB.insert("history", null, contentValues);

                        String address = "http://maps.google.co.in/maps?q=" + h5Address.getText().toString();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                        startActivity(i);
                    }
                });

                h5Button.setVisibility(View.VISIBLE);
            }
        }*/
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
