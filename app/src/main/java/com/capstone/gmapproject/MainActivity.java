package com.capstone.gmapproject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.EventListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DbConnector dbConnector;
    private SQLiteDatabase db;
    private static int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        dbConnector = new DbConnector(this);
        /*try {
            dbConnector.copyDatabaseFromAssets();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        //initialize login screen
        loginScene();
    }

    //function to switch layout to the map after login is authenticated
    public void mapLayout()
    {
        setContentView(R.layout.activity_main);
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
         mapFragment.getMapAsync(this);
         View windowLayout = findViewById(R.id.window_layout);
         /*
         Cursor cursor = getAllChargerLocations();

         // Display data using Toast
         displayChargerLocations(cursor);

         // Don't forget to close the cursor when done
         if (cursor != null) {
         cursor.close();
         }
          */
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permission is already granted
            zoomToCurrentLocation();
        }
        gMap.setOnMarkerClickListener(marker -> {
            // Show the white window
            if(marker.getTag()!=null){
                Integer clickCount = (Integer) marker.getTag();
                showWhiteWindow(clickCount);
            } else {
                showWhiteWindow(0);
            }


            return false; // Return false to allow default marker behavior
        });
        gMap.setMyLocationEnabled(true);
    }
    public void zoomInToCurrentLocation(android.view.View view) {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Get last known location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double lat=location.getLatitude() ;
                            double lng=location.getLongitude();
                            LatLng currentLatLng = new LatLng(lat,lng );

                            // Move the camera to the user's current location and zoom in
                            dbConnector.getStations(lng,lat,1);
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void zoomToCurrentLocation() {

        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {

                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Move the camera to the user's current location and zoom in
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
                            gMap.addMarker(new MarkerOptions().position(currentLatLng).title("EburgCur")).setTag(1);
                            LatLng locationDef = new LatLng(46.995, -120.549);
                            gMap.addMarker(new MarkerOptions().position(locationDef).title("Eburg2")).setTag(2);
                        } else {
                            // Handle the case where location is null
                            LatLng locationDef = new LatLng(46.995, -120.549);
                            gMap.addMarker(new MarkerOptions().position(locationDef).title("EburgNoLoc"));
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDef, 12));
                            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    //Fetching permission response
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                zoomToCurrentLocation();
            } else {
                // Location permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();

                //zoom in to Eburg
                LatLng locationDef = new LatLng(46.995, -120.549);
                gMap.addMarker(new MarkerOptions().position(locationDef).title("EburgDenied"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDef, 12));
            }
        }
    }
    //Invokes after marker click
    private void showWhiteWindow(Integer id) {
        //creating reference to the map, pulling params, changing the height
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = 1000;
        mapFragment.getView().setLayoutParams(params);

        Button zoomButton = findViewById(R.id.zoom_button);
        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) zoomButton.getLayoutParams();

        // Updating the button's position to stay on the new map size
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        buttonParams.setMargins(0, 16, 0, 16); // Adjust top and bottom margins as needed

        // Applying updated parameters to the button
        zoomButton.setLayoutParams(buttonParams);

        //setting list visible
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "id: "+id, Toast.LENGTH_SHORT).show();
    }

    //method to check if accurate username and password were given, returns true if so, false if not
    private boolean authenticateLogin()
    {
        db = dbConnector.getReadableDatabase();
        //get the username and password from their respective text fields
        EditText usernameText = (EditText) findViewById(R.id.username_text_input);
        String username = usernameText.getText().toString();

        EditText passwordText = (EditText) findViewById(R.id.password_text_input);
        String password = passwordText.getText().toString();

        //compare username to the usernames in the database to see if a match is present
        String query = "SELECT * FROM user_info WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if(!(cursor.getCount() > 0)) {
            db.close();
            return false;
        }
        cursor.close();

        query = "SELECT user_id FROM user_info WHERE username = ?";
        cursor = db.rawQuery(query, new String[]{username});
        cursor.moveToFirst();
        int tempUserID = cursor.getInt(0);
        cursor.close();

        //check password
        query = "SELECT password FROM user_cred WHERE user_id = ?";
        cursor = db.rawQuery(query, new String[]{String.valueOf(tempUserID)});
       if(!(cursor.getCount() > 0))
       {
           db.close();
           return false;
       }
       cursor.moveToFirst();
       String compare = cursor.getString(0);
       if(!compare.equals(password))
       {
           db.close();
           return false;
       }

       db.close();
        return true;
    }

    //set the static user ID variable to the logged in user's ID, for future use.
    private void setUserID() {
        String query = "SELECT user_id FROM user_info WHERE username = ?";

        db = dbConnector.getReadableDatabase();

        EditText usernameText = (EditText) findViewById(R.id.username_text_input);
        String username = usernameText.getText().toString();

        Cursor cursor = db.rawQuery(query, new String[]{username});
        cursor.moveToFirst();
        userID = cursor.getInt(0);
        cursor.close();
        db.close();

    }

    private void createAccountScene()
    {
        setContentView(R.layout.create_account_screen);
        Button back = (Button) findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //back to login screen
                loginScene();
            }
        });

        Button createAccount = (Button) findViewById(R.id.btnCreate);
        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                db = dbConnector.getReadableDatabase();
                //first, check if that username is currently in use, then if not, create the account
                EditText usernameText = (EditText) findViewById(R.id.createUsername_text_input);
                String username = usernameText.getText().toString();

                EditText passwordText = (EditText) findViewById(R.id.createPassword_text_input);
                String password = passwordText.getText().toString();

                boolean valid = createAccount(username, password);
                if (valid == false) printCreationError();
                else printConfirmCreation();

                db.close();
            }
        });
    }

    private void loginScene()
    {
        setContentView(R.layout.login_screen);

        //create buttons for login/create account and set up listeners
        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //throw to method for authenticating login
                boolean valid = authenticateLogin();
                if(!valid) {
                    printFalseCredentials();
                }
                else{
                    setUserID();
                    mapLayout();
                }
            }
        });

        //transition to account creation page
        Button createAccount = (Button) findViewById(R.id.btnCreate);
        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createAccountScene();
            }
        });
    }

    private boolean createAccount(String username, String password)
    {
        db = dbConnector.getWritableDatabase();

        //query database to see if same username exists
        String query = "SELECT * FROM user_info WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        int error = cursor.getCount();
        cursor.close();
        //if it exists, they cannot create that account
        if(error > 0)
        {
            db.close();
            return false;
        }
        //if not, make the new account
        else
        {

            ContentValues usernameValues = new ContentValues();
            usernameValues.put("username", username);


            long result;

            result = db.insert("user_info", null, usernameValues);
            if(result == -1) printConfirmError();

            ContentValues passwordValues = new ContentValues();
            passwordValues.put("password", password);
            result = db.insert("user_cred", null, passwordValues);
            db.close();

            if(result == -1) return false;
            else return true;
        }

    }

    private void printCreationError()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Invalid Credentials");
        alertDialogBuilder.setMessage("This username already exists, try again.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void printConfirmCreation()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Account Created");
        alertDialogBuilder.setMessage("You have successfully created an account, please go back to the main menu and log in.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void printConfirmError()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Account Not Created");
        alertDialogBuilder.setMessage("There was an error creating your account, please try again.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void printFalseCredentials()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Incorrect Login Credentials");
        alertDialogBuilder.setMessage("Your username or password was incorrect, please try again.");

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }
}