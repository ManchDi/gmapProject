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
            listAllEntriesOfTable("history");
            //listAllTables();
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
       /* for (Station station : stationList) {
            station.printStation();
        }*/
        return stationList;
    }
    @SuppressLint("Range")
    public List<Charger> getChargers(String ch_id) {

        List<Charger> chargerList = new ArrayList<>();

        // Query to fetch stations within the square area
        String query = "SELECT * FROM chargers " +
                "WHERE st_id = '"+ch_id+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // Iterate through the cursor and add chargers to the list
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Charger charger = new Charger();
                charger.setId(cursor.getInt(cursor.getColumnIndex("st_id")));
                charger.setStationId(cursor.getInt(cursor.getColumnIndex("st_id")));
                charger.setChargerType(cursor.getString(cursor.getColumnIndex("charger_type")));
                charger.setPrice(cursor.getString(cursor.getColumnIndex("price")));
                charger.setConnectionType(cursor.getString(cursor.getColumnIndex("connection_type")));
                charger.setWattage(cursor.getString(cursor.getColumnIndex("wattage")));
                chargerList.add(charger);
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();
        for (Charger charger : chargerList) {
            charger.print();
        }
        return chargerList;
    }
    public void addHistoryDatum(int chId, int us_id) {
        ArrayList<String> stationList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlAdd = "INSERT INTO history (user_id, station_id) VALUES (?, ?)";
        Object[] bindArgs = {us_id,chId};
        db.execSQL(sqlAdd, bindArgs);
        String sqlDelete = "DELETE FROM history WHERE user_id = ? AND history_id NOT IN (SELECT history_id FROM history WHERE user_id = ? ORDER BY history_id DESC LIMIT 5)";
        String[] deleteArgs = {String.valueOf(us_id), String.valueOf(us_id)};
        db.execSQL(sqlDelete, deleteArgs);
        //listAllEntriesOfTable("history");
        db.close();
    }
}