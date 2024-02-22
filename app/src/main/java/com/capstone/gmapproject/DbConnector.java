package com.capstone.gmapproject;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DbConnector extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "charge_charts_db.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;



    public DbConnector(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    public void checkDatabaseExistence() throws IOException {
        SQLiteDatabase checkDB = null;
        Log.d("dbHelper", "checkingExistence");
        try {
            String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
            checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
            Log.d("dbHelper", "db exists");
        } catch (SQLException e) {
            // Database does not exist, create it
            Log.d("dbHelper", "db doesnt exist, going to create");

            copyDatabaseFromAssets();
            Log.d("dbHelper", "finished db creation");

        }
        if (checkDB != null) {
            checkDB.close();
        }
    }
    //copy the db from the assets folder
    public void copyDatabaseFromAssets() throws IOException {
        Log.d("dbHelper", "inside copy");
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        Log.d("dbHelper", "finished copy");
    }
    public void listAllEntriesOfTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                StringBuilder rowInfo = new StringBuilder();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String columnName = cursor.getColumnName(i);
                    String columnValue = cursor.getString(i);
                    rowInfo.append(columnName).append(": ").append(columnValue).append(", ");
                }
                Log.d("dbHelper", "Table: " + tableName + ", Entry: " + rowInfo.toString());
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
    }
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM user_info WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") long userId = cursor.getLong(cursor.getColumnIndex("user_id"));
            cursor.close();
            db.close();
            Log.d("dbHelper", "user already exists");

            return userId; // User already exists, return the existing user ID
        }
        ContentValues valuesUserInfo = new ContentValues();
        valuesUserInfo.put("username", username);
        Log.d("dbHelper", "updated user_info with" + valuesUserInfo);

        long userId = db.insert("user_info", null, valuesUserInfo);

        ContentValues valuesUserCred = new ContentValues();
        valuesUserCred.put("user_id", userId);
        valuesUserCred.put("password", password);
        Log.d("dbHelper", "updated user_cred");

        db.insert("user_cred", null, valuesUserCred);

        db.close();
        return userId;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void listAllTables() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                Log.d("dbHelper", "Table name: " + tableName);
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
    }
    @SuppressLint("Range")
    public List<Station> getStations(double lng, double ltd, double radius) {
        List<Station> stationList = new ArrayList<>();
        double earthRadius = 6371; // Earth radius in kilometers

        // Convert radius from kilometers to degrees
        double radiusDegrees = radius / earthRadius;

        // Calculate maximum and minimum longitude and latitude values for the square area
        double maxLng = lng + Math.toDegrees(radiusDegrees / Math.cos(Math.toRadians(ltd)));
        double minLng = lng - Math.toDegrees(radiusDegrees / Math.cos(Math.toRadians(ltd)));
        double maxLtd = ltd + Math.toDegrees(radiusDegrees);
        double minLtd = ltd - Math.toDegrees(radiusDegrees);

        // Query to fetch stations within the square area
        String query = "SELECT * FROM stations " +
                "WHERE latitude BETWEEN ? AND ? " +
                "AND longitude BETWEEN ? AND ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(minLtd), String.valueOf(maxLtd),
                String.valueOf(minLng), String.valueOf(maxLng)});

        // Iterate through the cursor and add stations to the list
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Station station = new Station();
                station.setId(cursor.getInt(cursor.getColumnIndex("st_id")));
                station.setName(cursor.getString(cursor.getColumnIndex("name")));
                station.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                station.setChargerAmount(cursor.getInt(cursor.getColumnIndex("charger_amount")));
                station.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
                station.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
                stationList.add(station);
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        for (Station station : stationList) {
            station.printStation();
        }
        return stationList;
    }
    public ArrayList<String> getHistoryDatum(int chId) {
        ArrayList<String> stationList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery("SELECT st_id FROM chargers WHERE ch_id = ?", new String[]{String.valueOf(chId)});
        if (cursor.moveToFirst()) {
            String address = cursor.getString(0);
            stationList.add(address);
        }
        cursor.close();

        // Query for charger type
        cursor = db.rawQuery("SELECT charger_type FROM chargers WHERE ch_id = ?", new String[]{String.valueOf(chId)});
        if (cursor.moveToFirst()) {
            String type = cursor.getString(0);
            stationList.add(type);
        }
        cursor.close();

        // Query for connection type
        cursor = db.rawQuery("SELECT connection_type FROM chargers WHERE ch_id = ?", new String[]{String.valueOf(chId)});
        if (cursor.moveToFirst()) {
            String charger = cursor.getString(0);
            stationList.add(charger);
        }
        cursor.close();
        return stationList;
    }
}