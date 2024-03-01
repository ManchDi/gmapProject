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
import java.util.LinkedList;
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
        history=new LinkedList<>();
        if(!history.isEmpty()){
            for(int i = 1; i <= history.size(); i++){

                TextView entry = new TextView(this);
                if(i==1){entry = (TextView)findViewById(R.id.h1);}
                if(i==2){entry = (TextView)findViewById(R.id.h2);}
                if(i==3){entry = (TextView)findViewById(R.id.h3);}
                if(i==4){entry = (TextView)findViewById(R.id.h4);}
                if(i==5){entry = (TextView)findViewById(R.id.h5);}
                String text = "Entry " + i +
                        "\nAddress: " + history.peek().address +
                        "\nType: " + history.peek().type +
                        "\nCharger: " + history.peek().charger +
                        "\nCost: \nTime: ";
                entry.setText(text);
                history.add(history.remove());
            }
        }

        //cursor = chargeChartsDB.rawQuery("SELECT username FROM user_cred WHERE user_id = " + ID);
        //username_box = (TextView) findViewById(R.id.username_box);
        //username_box.setText(cursor.getString(0));


    }

    private void addEntry(HistoryEntry entry){
        history.add(entry);
        if(history.size() > 5){
            history.remove();
        }
    }
//Params: ID: station ID
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
