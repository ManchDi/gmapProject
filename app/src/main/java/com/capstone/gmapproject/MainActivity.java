package com.capstone.gmapproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DbConnector dbConnector;
    private SQLiteDatabase db;
    private static int userID;
    private static boolean loggedIn;
    private boolean fullScreen = true;
    private static String username;
    private int defualtRadius = 6, currentStationID = 0;
    private TextView radiusView;
    List<Marker> markerList;
    List<Charger> chargerList;
    ArrayList<Bitmap> icons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radiusView = (TextView) findViewById(R.id.text_view_id);
        // radiusView.setText(defualtRadius);
        dbConnector = new DbConnector(this);
        markerList = new ArrayList<>();
        chargerList = new ArrayList<>();
        try {
            dbConnector.checkDatabaseExistence();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Check which screen to go to, if there is a userID stored from when someone logged in, go to map, otherwise to go login
        if (!loggedIn) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {
            initializeIcons();
            mapLayout();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request location permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

    }

    public void initializeIcons() {
        icons = new ArrayList<>(7);
        int width = 75;
        int height = 75;
        for (int i = 0; i < 7; i++) {
            Bitmap originalBitmap;
            try {
                if (i == 0) {
                    InputStream is = getAssets().open("Logo-Tesla.bmp"); //0
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 1) {
                    InputStream is = getAssets().open("Logo-Shell.bmp");//1
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 2) {
                    InputStream is = getAssets().open("Logo-Flo.bmp");//2
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 3) {
                    InputStream is = getAssets().open("Logo-Evgo.bmp");//3
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 4) {
                    InputStream is = getAssets().open("Logo-EVCS.bmp");//4
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 5) {
                    InputStream is = getAssets().open("Logo-EvConnect.bmp");//5
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                } else if (i == 6) {
                    InputStream is = getAssets().open("Logo-ElectrifyAmerica.bmp");//6
                    originalBitmap = BitmapFactory.decodeStream(is);
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
                    icons.add(i, resizedBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //function to switch layout to the map after login is authenticated
    public void mapLayout() {
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        //Button for switching between Main and Profile
        ImageButton profileButton = (ImageButton) findViewById(R.id.prof);
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        // Check location permission

        zoomToCurrentLocation();
        gMap.setOnMarkerClickListener(marker -> {
            // Show the white window
            if (marker.getTag() != null) {
                Integer clickCount = (Integer) marker.getTag();
                showWhiteWindow(clickCount);
                currentStationID = clickCount;
            } else {
                showWhiteWindow(0);
            }


            return false; // Return false to allow default marker behavior
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);

        }

    }
    public void refresh(android.view.View view) {
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
                            removeMarkers();
                            placeStationsOnMap(dbConnector.getStations(lng,lat,defualtRadius));
                            // Move the camera to the user's current location and zoom in
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    public void removeMarkers(){
        for (Marker marker : markerList) {
            marker.remove(); // Remove the marker from the map
        }
    }
    public void placeStationsOnMap(@NonNull List<Station> stations) {
        for (Station station : stations) {
            LatLng currentLatLng =new LatLng(station.getLatitude(), station.getLongitude());
            // Add the marker to the Google Map
            Marker marker = gMap.addMarker(new MarkerOptions().position(currentLatLng).title(station.getName()));
            marker.setTag(station.getId());
            // Edit  icon based on charger
            switch(station.getChargerType()) {
                case "unknown" :
                    Log.d("dpHelper","unknown ch type");
                    break;
                case "Tesla" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(0)));
                    break;
                case "Electrify America" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(1)));break;
                case "Shell Sky EV Technology" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(2)));break;
                case "FLO" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(3)));break;
                case "EV Connect" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(4)));break;
                case "EVCS" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(5)));break;
                case "Evgo" :
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(icons.get(6)));break;
                default :
                    break;
            }
            markerList.add(marker);

        }
    }
   public void increaseRadius(android.view.View view){
        if (defualtRadius<200){
            defualtRadius+=5;
            TextView textView = (TextView)findViewById(R.id.text_view_id);
            textView.setText(String.valueOf(defualtRadius));
        } else{
            showToast("max radius");
        }
    }
   public void decreaseRadius(android.view.View view){
       if (defualtRadius>5){
           defualtRadius-=5;
           TextView textView = (TextView)findViewById(R.id.text_view_id);
           textView.setText(String.valueOf(defualtRadius));
       } else{
           showToast("min radius");

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
                            double lat = location.getLatitude(), lng=location.getLongitude();
                            LatLng currentLatLng = new LatLng(lat, lng);

                            // Move the camera to the user's current location and zoom in
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                            placeStationsOnMap(dbConnector.getStations(lng,lat, defualtRadius));
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
    public void navigate(View view){
        dbConnector.addHistoryDatum(currentStationID,getUserID());
        String address = "http://maps.google.co.in/maps?q=" + dbConnector.getStationName(currentStationID).get(1);
        Log.d("dbHelper","add: "+address);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(i);
    }
    //Invokes after marker click
    private void showWhiteWindow(Integer id) {

            db = dbConnector.getReadableDatabase();
            String newID = id.toString();
            chargerList = dbConnector.getChargers(newID);

        //set the charger type in the new view
        getStationType(id);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_chargers);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ListViewer adapter = new ListViewer(chargerList, charger -> {
                // Item click
                Log.d("dbConnector", "opened charger");
            });
            recyclerView.setAdapter(adapter);

        //creating reference to the map, pulling params, changing the height
        if(fullScreen) {
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

        //Changing position of radius counter.
        TextView radiusTextView = findViewById(R.id.text_view_id);
        RelativeLayout.LayoutParams textViewParams = (RelativeLayout.LayoutParams) radiusTextView.getLayoutParams();

        // Update the Radius position to be under the button
        textViewParams.addRule(RelativeLayout.BELOW, R.id.zoom_button);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        textViewParams.addRule(RelativeLayout.ABOVE, 0);
        textViewParams.setMargins(0, 16, 0, 16); // Adjust top and bottom margins as needed

        // Applying updated parameters to the Radius
        radiusTextView.setLayoutParams(textViewParams);
        //setting list visible
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.setVisibility(View.VISIBLE);
        }
        //Toast.makeText(MainActivity.this, "id: "+id, Toast.LENGTH_SHORT).show();
    }

    private void getStationType(int stationID)
    {
        db = dbConnector.getReadableDatabase();

        String chargerType = "";
        String typeQuery = "SELECT charger_type FROM stations WHERE st_id = ?";

        Cursor cursor = db.rawQuery(typeQuery, new String[]{String.valueOf(stationID)});
        cursor.moveToNext();
        chargerType += cursor.getString(0);

        TextView type = (TextView) findViewById(R.id.stationType);
        type.setText(chargerType);

        db.close();
    }
    public void closeWindow(View view){
        //creating reference to the map, pulling params, changing the height
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mapFragment.getView().setLayoutParams(params);

        Button zoomButton = findViewById(R.id.zoom_button);
        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams) zoomButton.getLayoutParams();

        // Updating the button's position to stay on the new map size
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        buttonParams.setMargins(0, 16, 0, 16); // Adjust top and bottom margins as needed

        // Applying updated parameters to the button
        zoomButton.setLayoutParams(buttonParams);

        //Changing position of radius counter.
        TextView radiusTextView = findViewById(R.id.text_view_id);
        RelativeLayout.LayoutParams textViewParams = (RelativeLayout.LayoutParams) radiusTextView.getLayoutParams();

        // Update the TextView's position to be under the button
        textViewParams.addRule(RelativeLayout.ABOVE, R.id.zoom_button);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        textViewParams.addRule(RelativeLayout.BELOW, 0);
        textViewParams.setMargins(0, 16, 0, 16); // Adjust top and bottom margins as needed

        // Applying updated parameters to the TextView
        radiusTextView.setLayoutParams(textViewParams);
        //setting list visible
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.setVisibility(View.INVISIBLE);
        fullScreen=true;
    }
    public static void setUserID(int userID)
    {
        MainActivity.userID = userID;
    }

    public static void setLoggedIn(boolean status)
    {
        loggedIn = status;
    }

    public static void setUsername(String user)
    {
        username = user;
    }

    public static int getUserID()
    {
        return userID;
    }

    public static String getUsername()
    {
        return username;
    }

}