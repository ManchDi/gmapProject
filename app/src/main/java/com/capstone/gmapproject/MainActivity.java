package com.capstone.gmapproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.MarkerOptions;
import android.view.MotionEvent;
import android.view.View;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private boolean isWindowFullscreen = false;
    private GoogleMap gMap;
    private float startY;
    private FusedLocationProviderClient fusedLocationClient;
    private SwipeGestureHandler swipeGestureHandler;
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);
        View windowLayout = findViewById(R.id.window_layout);
        swipeGestureHandler = new SwipeGestureHandler(this);
        gestureDetector = new GestureDetector(this, swipeGestureHandler);

        windowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }
    public void expandWindow() {
        // Expand window to fullscreen
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        windowLayout.requestLayout();
    }

    public void restoreWindow() {
        // Restore window to original size
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        windowLayout.requestLayout();
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
            showWhiteWindow();
            return false; // Return false to allow default marker behavior
        });
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
                            gMap.addMarker(new MarkerOptions().position(currentLatLng).title("Eburg"));

                        } else {
                            // Handle the case where location is null
                            LatLng locationDef = new LatLng(46.995, -120.549);
                            gMap.addMarker(new MarkerOptions().position(locationDef).title("Eburg"));
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
                gMap.addMarker(new MarkerOptions().position(locationDef).title("Eburg"));
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDef, 12));
            }
        }
    }
    private void showWhiteWindow() {
        View windowLayout = findViewById(R.id.window_layout);
        windowLayout.setVisibility(View.VISIBLE);
    }
}