package com.capstone.gmapproject;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.widget.Button;
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


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DbConnector dbConnector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);
        View windowLayout = findViewById(R.id.window_layout);
        dbConnector = new DbConnector(this);
        Cursor cursor = getAllChargerLocations();

        // Display data using Toast
        displayChargerLocations(cursor);

        // Don't forget to close the cursor when done
        if (cursor != null) {
            cursor.close();
        }

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

                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            // Move the camera to the user's current location and zoom in
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        } else {
                            Toast.makeText(MainActivity.this, "Unable to fetch location", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    private Cursor getAllChargerLocations() {
        SQLiteDatabase db = dbConnector.getReadableDatabase();
        return db.query("chargerLocations", null, null, null, null, null, null);
    }
    private void displayChargerLocations(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));

                String message = "Latitude: " + latitude + ", Longitude: " + longitude;

                // Display data using Toast
                showToast(message);
            } while (cursor.moveToNext());
        } else {
            showToast("No charger locations found");
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
}