package com.example.assessment04;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assessment04.routedata.Route;

public class EditBusActivity extends AppCompatActivity {

    private EditText busNameInput, busNumberInput;
    private Spinner routeSpinner, driverSpinner;
    private Button saveButton;

    private int busId; // Bus ID passed from intent
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bus);

        final int position = getIntent().getIntExtra("pos", 0);

        dbHelper = new DatabaseHelper(this);

        // Initialize Views
        busNameInput = findViewById(R.id.bus_name_input);
        busNumberInput = findViewById(R.id.bus_number_input);
        routeSpinner = findViewById(R.id.route_spinner);
        driverSpinner = findViewById(R.id.driver_spinner);
        saveButton = findViewById(R.id.save_button);

        // Get Bus ID from Intent as a String
        String busId = getIntent().getStringExtra("busId");
        if (busId == null || busId.isEmpty()) {
            Toast.makeText(this, "Invalid bus selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Log busId for debugging
        Log.d("EditBusActivity", "Received busId: " + busId);

        // Load bus details into fields
        Bus bus = dbHelper.getBusById(busId);
        if (bus != null) {
            busNameInput.setText(bus.getBusName());
            busNumberInput.setText(bus.getBusNo());
            loadBusDetails(bus); // Separate logic into a method
        } else {
            Toast.makeText(this, "Failed to load bus details", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedItem", "New Value");
            resultIntent.putExtra("position", 1); // Send back the position
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        // Save updated details
        saveButton.setOnClickListener(v -> saveBusDetails(busId));
    }



    private void loadBusDetails(Bus bus) {
        // Populate the Route Spinner (Using predefined entries in strings.xml)
        Route[] routes = Route.values();
        ArrayAdapter<Route> routeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, routes);
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(routeAdapter);

        if (bus.getRoute() != null) {
            int routePosition = routeAdapter.getPosition(bus.getRoute());
            routeSpinner.setSelection(routePosition);
        }

        String[] drivers = {"Driver A", "Driver B", "Driver C"}; // Replace with dynamic data if applicable
        ArrayAdapter<String> driverAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, drivers);
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(driverAdapter);

        if (bus.getDriverName() != null) {
            int driverPosition = driverAdapter.getPosition(bus.getDriverName());
            driverSpinner.setSelection(driverPosition);
        }
    }


    private void saveBusDetails(String busId) {
        String updatedName = busNameInput.getText().toString().trim();
        Route updatedRoute = (Route) routeSpinner.getSelectedItem();
        String updatedDriver = driverSpinner.getSelectedItem().toString();

        // Validate inputs
        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedRoute.toString()) || TextUtils.isEmpty(updatedDriver)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update bus details in the database
        boolean success = dbHelper.updateBus(busId, updatedName, updatedRoute.getRouteCode(), updatedDriver);
        if (success) {
            Toast.makeText(this, "Bus updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update bus", Toast.LENGTH_SHORT).show();
        }
    }

}
