package com.example.assessment04;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.assessment04.routedata.Route;
import com.example.assessment04.routedata.Station;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.assessment04.databinding.ActivityMapsBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set default camera position
        LatLng defaultLocation = new LatLng(6.9271, 79.8612); // Example coordinates
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 8));

        // Add markers for all stations
        for (Station station : Station.values()) {
            station.addMarker(mMap);
        }

        // Handle marker clicks to open the Bottom Sheet with buses
        mMap.setOnMarkerClickListener(marker -> {
            String stationName = marker.getTitle();
            Station selectedStation = getStationByName(stationName);
            if (selectedStation != null) {
                openBusBottomSheet(selectedStation);
            }
            return true; // Indicate that the click has been handled
        });
    }

    private Station getStationByName(String name) {
        for (Station station : Station.values()) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    private void openBusBottomSheet(Station station) {
        BusSelectionBottomSheet bottomSheet = BusSelectionBottomSheet.newInstance(station);
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }



}