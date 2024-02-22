package com.capstone.gmapproject;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.*;

public class DbConnector extends SQLiteOpenHelper {

    private SQLiteDatabase chargeChartsDB;

    private final Context myContext;

    private static final String DATABASE_NAME = "charge_charts_db.db";

    public static final String DATABASE_PATH = "app/src/main/assets/";

    public static final int DATABASE_VERSION = 1;

    public DbConnector(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public void createDatabase() throws IOException {
        boolean exist = checkDatabase();
        if(exist) Log.v("DB Exists", "db exists");
        exist = checkDatabase();
        if(!(exist)) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {
        boolean check = false;
        try {
            String path = DATABASE_PATH + DATABASE_NAME;
            File dbFile = new File(path);
            check = dbFile.exists();
        } catch(SQLiteException e) {

        }
        return check;
    }

    private void copyDataBase() throws IOException {
        InputStream input = myContext.getAssets().open(DATABASE_NAME);
        String outputFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream output = new FileOutputStream(outputFileName);
        byte[] buffer = new byte[2024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    public void deleteDB() {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists()) {
            file.delete();
            System.out.println("Deleted database file.");
        }
    }

    public synchronized void closeDataBase() throws SQLException {
        if(chargeChartsDB != null) {
            chargeChartsDB.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the chargerLocations table
        //db.execSQL("CREATE TABLE IF NOT EXISTS chargerLocations (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL);");

        // Insert the initial charger
        //db.execSQL("INSERT INTO chargerLocations (latitude, longitude) VALUES (45, -120);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
        if (newVersion > oldVersion) {
            Log.v("Database Upgrade", "Database version higher than old.");
            deleteDB();
        }
    }
}