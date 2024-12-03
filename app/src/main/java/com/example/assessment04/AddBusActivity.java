package com.example.assessment04;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.assessment04.routedata.Route;
import com.example.assessment04.routedata.TurnManager;

import java.time.LocalTime;
import java.util.List;

public class AddBusActivity extends AppCompatActivity {

    private EditText busNameInput, busNumberInput;
    private Spinner driverSpinner, routeSpinner;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        // Adjust layout to fit system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        busNameInput = findViewById(R.id.bus_name_input);
        busNumberInput = findViewById(R.id.bus_number_input);
        driverSpinner = findViewById(R.id.driver_spinner);
        routeSpinner = findViewById(R.id.route_spinner);
        submitButton = findViewById(R.id.submit_button);

        // Populate the Driver Spinner dynamically
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<String> drivers = dbHelper.getAvailableDrivers();
        if (drivers.isEmpty()) {
            drivers.add("No drivers available"); // Fallback message
        }
        drivers.add(0, "Select Driver"); // Add default option at the top

        ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drivers);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverAdapter);

        // Populate the Route Spinner (Using predefined entries in strings.xml)
        Route[] routes = Route.values();
        ArrayAdapter<Route> routeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routes);
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(routeAdapter);

        // Handle Submit Button Click
        submitButton.setOnClickListener(view -> {
            String busName = busNameInput.getText().toString().trim();
            String busNumber = busNumberInput.getText().toString().trim();
            String selectedDriver = driverSpinner.getSelectedItem().toString();

            // Validate Inputs
            if (busName.isEmpty()) {
                Toast.makeText(AddBusActivity.this, "Please enter the bus name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (busNumber.isEmpty()) {
                Toast.makeText(AddBusActivity.this, "Please enter the bus number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDriver.equals("Select Driver")) {
                Toast.makeText(AddBusActivity.this, "Please select a driver", Toast.LENGTH_SHORT).show();
                return;
            }

            int stationCode;

            try {
                stationCode = ((Route) routeSpinner.getSelectedItem()).getRouteCode();
            } catch (ClassCastException | NullPointerException e1) {
                Toast.makeText(AddBusActivity.this, "Please select a route", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save Bus Data
            saveBusData(busNumber, busName, stationCode, selectedDriver);

            // Confirmation Message
            Toast.makeText(AddBusActivity.this, "Bus added successfully!", Toast.LENGTH_SHORT).show();

            // Navigate back to dashboard
            finish();
        });

        // After saving bus details
        String busNumber = busNumberInput.getText().toString().trim();
        String busName = busNameInput.getText().toString().trim();
        Route selectedRoute = (Route) routeSpinner.getSelectedItem();

        // Fetch existing buses for the route
        List<String> buses = dbHelper.getBusesForRoute(selectedRoute);

        // Add the new bus
        buses.add(busNumber);

        // Assign turns
        TurnManager turnManager = new TurnManager();
        LocalTime startTime = LocalTime.of(6, 0); // Example: Start schedule at 6:00 AM
        turnManager.assignTurns(selectedRoute, buses, startTime);

        // Print or save the schedule for debugging or display purposes
        turnManager.printSchedule(selectedRoute);
    }

    // Save Bus Data to SQLite
    private void saveBusData(String busNumber, String busName, int routeCode, String driver) {
        DatabaseHelper dbHelper = new DatabaseHelper(AddBusActivity.this);
        boolean insertSuccess = dbHelper.insertBuses(busNumber, busName, routeCode, driver);
        if (insertSuccess) {
            Toast.makeText(AddBusActivity.this, "Bus details saved to SQLite", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddBusActivity.this, "Failed to save bus details to SQLite", Toast.LENGTH_SHORT).show();
        }
    }
}