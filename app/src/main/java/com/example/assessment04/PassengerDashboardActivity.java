package com.example.assessment04;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.assessment04.data.CurrentUser;
import com.example.assessment04.data.Swap;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PassengerDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private CardView swapCardView;
    private Button buttonAccept;
    private Button buttonDecline;
    private TextView content;

    private String email; // Passed from login
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passenger_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.insertRoutes(); // Ensure routes are inserted

        email = getIntent().getStringExtra("email");
        CurrentUser.email = email;

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("QuickBook");

        // Setup DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        swapCardView = findViewById(R.id.swapCardView);
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonDecline = findViewById(R.id.buttonDecline);
        content = findViewById(R.id.textView2);

        Swap swap = dbHelper.getSwapRequests(email);

        if (swap != null) {
            swapCardView.setVisibility(View.VISIBLE);
            content.setText(
                    String.format(
                            getResources().getText(R.string.customer_asking_for_a_seat_change).toString(),
                            (swap.requesterEmail != null)? swap.requesterEmail: "Passenger"
                    )
            );
        }

        buttonAccept.setOnClickListener(view -> {
            dbHelper.swapSeats(swap, CurrentUser.email);
            swapCardView.setVisibility(View.GONE);
            Toast.makeText(this, "Swap successful!", Toast.LENGTH_SHORT).show();
        });

        buttonDecline.setOnClickListener(view -> {
            dbHelper.declineSwap(swap);
            swapCardView.setVisibility(View.GONE);
            Toast.makeText(this, "Swap declined!", Toast.LENGTH_SHORT).show();
        });

        updateHeaderEmail(email);

        // Setup Navigation Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle Navigation Item Selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(PassengerDashboardActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_profile:
                        Intent profileIntent = new Intent(PassengerDashboardActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("email", email); // Pass email for profile retrieval
                        startActivity(profileIntent);
                        break;

                    case R.id.nav_settings:
                        Toast.makeText(PassengerDashboardActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent loginIntent = new Intent(PassengerDashboardActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        break;

                    default:
                        Toast.makeText(PassengerDashboardActivity.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Handle the Open Map button click
        ExtendedFloatingActionButton openMapButton = findViewById(R.id.open_map_button);
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the Map Activity
                Intent mapIntent = new Intent(PassengerDashboardActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });
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