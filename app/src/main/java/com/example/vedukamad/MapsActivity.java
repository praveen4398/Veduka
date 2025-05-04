package com.example.vedukamad;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText searchBox;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Wire up views
        searchBox    = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);

        // Initialize the MapFragment
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // When “Search” is tapped, geocode and move the camera
        searchButton.setOnClickListener(v -> {
            String place = searchBox.getText().toString().trim();
            if (place.isEmpty()) {
                Toast.makeText(this, "Please enter a city or town", Toast.LENGTH_SHORT).show();
            } else {
                geocodeAndMark(place);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // When the user taps on the map, clear & drop a marker, then return coords
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));

            Intent result = new Intent();
            result.putExtra("latitude", latLng.latitude);
            result.putExtra("longitude", latLng.longitude);
            setResult(RESULT_OK, result);
            finish();
        });
    }

    /**
     * Geocode the user’s input, place a marker, and center the map.
     */
    private void geocodeAndMark(String placeName) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> results = geocoder.getFromLocationName(placeName, 1);
            if (results == null || results.isEmpty()) {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Address addr = results.get(0);
            LatLng latLng = new LatLng(addr.getLatitude(), addr.getLongitude());

            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(placeName));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error finding location", Toast.LENGTH_SHORT).show();
        }
    }
}
