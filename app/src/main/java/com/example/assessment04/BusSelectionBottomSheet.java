package com.example.assessment04;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment04.routedata.Route;
import com.example.assessment04.routedata.Station;
import com.example.assessment04.routedata.TurnManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class BusSelectionBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView busRecyclerView;
    private Spinner routeSelector;

    private Station selectedStation;

    public static BusSelectionBottomSheet newInstance(Station station) {
        BusSelectionBottomSheet fragment = new BusSelectionBottomSheet();
        Bundle args = new Bundle();
        args.putString("stationName", station.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String stationName = getArguments().getString("stationName");
            selectedStation = getStationByName(stationName);

            if (selectedStation != null) {
                Log.d("BusSelectionBottomSheet", "Selected station: " + selectedStation.getName());
            } else {
                Log.e("BusSelectionBottomSheet", "Station not found for name: " + stationName);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        busRecyclerView = view.findViewById(R.id.available_buses_recycler);
        routeSelector = view.findViewById(R.id.route_selector);

        // Retrieve selected station
        if (getArguments() != null) {
            String stationName = getArguments().getString("stationName");
            selectedStation = getStationByName(stationName);
        }

        // Setup filters and bus list
        setupRouteSelector();
        loadAvailableBuses();

        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = (Route) parent.getItemAtPosition(position);
                loadAvailableBuses();
                setupAvailableBusesRecycler(selectedStation, selectedRoute);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Route selectedRoute = (Route) routeSelector.getSelectedItem();

        // Fetch buses for the route and station
        List<Bus> buses = (selectedRoute != null)? getAvailableBusesForStationAndRoute(selectedStation, selectedRoute): new ArrayList<>();

        // Initialize RecyclerView with available buses and their turn times
        setupAvailableBusesRecycler(buses);

        return view;
    }

    private List<Bus> getAvailableBusesForStationAndRoute(Station station, Route route) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        return (route != null)? dbHelper.getBusesForRouteAndStation(station, route): new ArrayList<>(); // Fetch buses for the selected station and route
    }

    private void setupRouteSelector() {
        if (selectedStation == null) {
            Log.e("BusSelection", "Station not set for route selector");
            return;
        }

        ArrayAdapter<Route> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                getRoutesFromStation(selectedStation)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSelector.setAdapter(adapter);

        // Reset buses when route changes
        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAvailableBuses(); // Refresh buses
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    private void setupAvailableBusesRecycler(List<Bus> buses) {
        // You need to fetch the turn schedule and pass the turn times
        List<TurnManager.Turn> turns;
        if (buses.isEmpty())
            turns = new ArrayList<>();
        else
            turns = TurnManager.instance().getTurnSchedule(buses.get(0).getRoute()); // Adjust based on actual route

        // Initialize RecyclerView with bus details and turn times
        BusAdapter busAdapter = new BusAdapter(buses, turns, getContext());
        busRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        busRecyclerView.setAdapter(busAdapter);
    }

    private void loadAvailableBuses() {
        if (selectedStation == null) {
            Log.e("BusSelection", "Selected station is null");
            return;
        }

        Route selectedRoute = (Route) routeSelector.getSelectedItem();
        if (selectedRoute == null) {
            Log.e("BusSelection", "Selected route is null");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Bus> buses = dbHelper.getBusesForRouteAndStation(selectedStation, selectedRoute);
        List<TurnManager.Turn> turns = TurnManager.instance().getTurnSchedule(selectedRoute);

        // Debug: Print the size of the buses list
        Log.d("BusSelection", "Station: " + selectedStation.getName() + ", Route: " + selectedRoute);
        Log.d("BusSelection", "Retrieved buses: " + buses.size());

        if (busRecyclerView.getAdapter() != null) {
            ((BusAdapter) busRecyclerView.getAdapter()).notifyItemRangeRemoved(0, busRecyclerView.getAdapter().getItemCount());
        }


        if (buses.isEmpty()) {
            Toast.makeText(getContext(), "No buses available for this route.", Toast.LENGTH_SHORT).show();
        } else {
            BusAdapter busAdapter = new BusAdapter(buses, turns, getContext());
            busRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            busRecyclerView.setAdapter(busAdapter);
        }
    }

    private void setupAvailableBusesRecycler(Station selectedStation, Route selectedRoute) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<Bus> buses = dbHelper.getBusesForRouteAndStation(selectedStation, selectedRoute);

        if (buses.isEmpty()) {
            Toast.makeText(getContext(), "No buses available for this route", Toast.LENGTH_SHORT).show();
        }

        List<TurnManager.Turn> turns =  TurnManager.instance().getTurnSchedule(selectedRoute);

        BusAdapter adapter = new BusAdapter(buses, turns, getContext());
        busRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        busRecyclerView.setAdapter(adapter);
    }

    private void openSeatBookingUI() {
        // Launch seat booking activity with the selected bus details
    }

    private Station getStationByName(String name) {
        for (Station station : Station.values()) {
            if (station.getName().equals(name)) {
                return station;
            }
        }
        return null;
    }

    private List<Route> getRoutesFromStation(Station station) {
        List<Route> routes = new ArrayList<>();
        for (Route route : Route.values()) {
            if (route.getFrom().equals(station)) {
                routes.add(route);
            }
        }
        return routes;
    }

    private void setupAvailableBusesRecycler(List<Bus> buses, List<TurnManager.Turn> turns) {
        // Initialize RecyclerView with bus details and turn times
        BusAdapter busAdapter = new BusAdapter(buses, turns, getContext());
        busRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        busRecyclerView.setAdapter(busAdapter);
    }

}

