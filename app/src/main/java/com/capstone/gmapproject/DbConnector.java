package com.capstone.gmapproject;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DbConnector extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chargers.db";
    private static final int DATABASE_VERSION = 1;

    public DbConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the chargerLocations table
        db.execSQL("CREATE TABLE IF NOT EXISTS chargerLocations (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL);");

        // Insert the initial charger
        db.execSQL("INSERT INTO chargerLocations (latitude, longitude) VALUES (45, -120);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
}