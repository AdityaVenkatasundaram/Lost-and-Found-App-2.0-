package com.example.lostandfoundapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "API Key: " + getString(R.string.google_maps_key));

        dbHelper = new DataBaseHelper(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "SupportMapFragment is null");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d(TAG, "Map is ready");

        // Retrieve all items from the database
        Cursor cursor = dbHelper.getAllItems();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_LOCATION));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DataBaseHelper.COLUMN_DESCRIPTION));
                Log.d(TAG, "Item: " + description + ", Location: " + location);

                // Split the location string into latitude and longitude
                String[] latLng = location.split(", ");
                if (latLng.length == 2) {
                    try {
                        double latitude = Double.parseDouble(latLng[0]);
                        double longitude = Double.parseDouble(latLng[1]);

                        // Add a marker for each item
                        LatLng itemLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(itemLocation).title(description));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid location format: " + location, e);
                    }
                } else {
                    Log.e(TAG, "Location string does not contain valid latitude and longitude: " + location);
                }
            }
            cursor.close();
        } else {
            Log.e(TAG, "Cursor is null or empty");
        }

        // Move the camera to the first marker if available
        if (mMap.getCameraPosition().zoom == 0) {
            LatLng defaultLocation = new LatLng(-34, 151); // Sydney
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));
        }
    }
}
