package com.example.assessment04;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment04.routedata.Route;
import com.example.assessment04.routedata.Station;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class BusSelectionBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView busRecyclerView;
    private Spinner routeSelector;
    private TextInputEditText dateSelector;

    private Station selectedStation;
    private Button bookSeatButton;

    public static BusSelectionBottomSheet newInstance(Station station) {
        BusSelectionBottomSheet fragment = new BusSelectionBottomSheet();
        Bundle args = new Bundle();
        args.putString("stationName", station.getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        busRecyclerView = view.findViewById(R.id.available_buses_recycler);
        routeSelector = view.findViewById(R.id.route_selector);
        dateSelector = view.findViewById(R.id.date_selector);
        bookSeatButton = view.findViewById(R.id.book_seat_button);

        // Retrieve selected station
        if (getArguments() != null) {
            String stationName = getArguments().getString("stationName");
            selectedStation = getStationByName(stationName);
        }

        // Setup filters and bus list
        setupRouteSelector();
        setupDateSelector();
        loadAvailableBuses();

        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = (Route) parent.getItemAtPosition(position);
                setupAvailableBusesRecycler(selectedStation, selectedRoute);
                loadAvailableBuses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        bookSeatButton.setOnClickListener(v -> {
            // Open seat booking UI
            openSeatBookingUI();
        });

        return view;
    }

    private void setupRouteSelector() {
        // Populate the route selector with routes departing from the selected station
        ArrayAdapter<Route> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                getRoutesFromStation(selectedStation)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSelector.setAdapter(adapter);

        // Update buses when route changes
        routeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAvailableBuses(); // Refresh buses when route changes
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupDateSelector() {
        dateSelector.setOnClickListener(v -> {
            // Open date picker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year, month, dayOfMonth) -> dateSelector.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
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

        // Debug: Print the size of the buses list
        Log.d("BusSelectionBottomSheet", "Available Buses: " + buses.size());

        if (buses.isEmpty()) {
            Toast.makeText(getContext(), "No buses available for this route.", Toast.LENGTH_SHORT).show();
        } else {
            BusAdapter busAdapter = new BusAdapter(buses, getContext());
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

        BusAdapter adapter = new BusAdapter(buses, getContext());
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

}

