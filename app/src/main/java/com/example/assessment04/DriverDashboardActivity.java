package com.example.assessment04;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assessment04.data.Cancel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DriverDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView busNameText, routeDetailsText, passengerCountText;
    private Button startTripButton, endTripButton;

    private String email; // Driver's email

    private RecyclerView recyclerViewCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = getIntent().getStringExtra("email");

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setTitle("QuickBook");

        updateHeaderEmail(email);

        // Initialize Views
        busNameText = findViewById(R.id.bus_name);
        routeDetailsText = findViewById(R.id.route_details);
        passengerCountText = findViewById(R.id.passenger_count);
        startTripButton = findViewById(R.id.start_trip_button);
        endTripButton = findViewById(R.id.end_trip_button);
        recyclerViewCancel = findViewById(R.id.recyclerViewCancel);

        // Load Assigned Bus Info
        loadAssignedBusDetails();

        // Trip Action Buttons
        startTripButton.setOnClickListener(v -> {
            Toast.makeText(this, "Trip Started", Toast.LENGTH_SHORT).show();
            startTripButton.setEnabled(false);
            endTripButton.setEnabled(true);
        });

        endTripButton.setOnClickListener(v -> {
            Toast.makeText(this, "Trip Ended", Toast.LENGTH_SHORT).show();
            startTripButton.setEnabled(true);
            endTripButton.setEnabled(false);
        });


        // Handle Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(DriverDashboardActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_profile:
                        Intent profileIntent = new Intent(DriverDashboardActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("email", email); // Pass email for profile retrieval
                        startActivity(profileIntent);
                        break;

                    case R.id.nav_settings:
                        Toast.makeText(DriverDashboardActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent loginIntent = new Intent(DriverDashboardActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        break;

                    default:
                        Toast.makeText(DriverDashboardActivity.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        DatabaseHelper helper = new DatabaseHelper(this);
        List<Cancel> cancels = helper.getCancelRequests(helper.getAssignedBusForDriver(email));
        CancelAdapter adapter = new CancelAdapter(this, cancels);
        recyclerViewCancel.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCancel.setAdapter(adapter);
    }

    private void loadAssignedBusDetails() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Bus assignedBus = dbHelper.getAssignedBusForDriver(email);

        if (assignedBus != null) {
            busNameText.setText("Bus Name: " + assignedBus.getBusName());
            routeDetailsText.setText("Route: " + assignedBus.getRoute());
            passengerCountText.setText("Passengers: " + dbHelper.getPassengerCount(assignedBus.getBusNo()));
        } else {
            busNameText.setText("Bus Name: None");
            routeDetailsText.setText("Route: None");
            passengerCountText.setText("Passengers: 0");
        }
    }

    private void updateHeaderEmail(String email) {
        // Access header view
        View headerView = navigationView.getHeaderView(0);
        TextView headerEmail = headerView.findViewById(R.id.header_email);

        // Update email
        headerEmail.setText(email);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}