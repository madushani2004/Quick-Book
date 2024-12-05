package com.example.assessment04.routedata;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public enum Station {
    ColomboFort("Colombo Fort", new LatLng(6.9271, 79.8612)),
    Kandy("Kandy", new LatLng(7.291418, 80.636696)),
    Jaffna("Jaffna", new LatLng(9.6615, 80.0255)),
    Galle("Galle", new LatLng(6.0329, 80.2168)),
    Kurunagala("Kurunagala", new LatLng(7.4818, 80.3609));

    private final String name;
    private final LatLng latLng;

    private Station(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void addMarker(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(latLng).title(name));
    }
}
