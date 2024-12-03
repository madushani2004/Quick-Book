package com.example.assessment04;

import static com.example.assessment04.R.id.empty_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class OwnerDashboardActivity extends AppCompatActivity {

    private final int REQUEST_CODE_ADD_BUS = 590;

    private FloatingActionButton addBusButton;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private String email = null;

    private BusAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_owner_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseHelper dbHelper = new DatabaseHelper(this);


        // Update email in header


        emptyView = findViewById(empty_view);

        TextView totalBusesTextView = findViewById(R.id.total_buses);
        TextView totalRoutesTextView = findViewById(R.id.total_routes);

        int totalBuses = dbHelper.getTotalBuses();
        int totalRoutes = dbHelper.getTotalRoutes();

        totalBusesTextView.setText(String.valueOf(totalBuses));
        totalRoutesTextView.setText(String.valueOf(totalRoutes));

        email = getIntent().getStringExtra("email");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("QuickBook");

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView headerUsername = headerView.findViewById(R.id.header_username);
        TextView headerEmail = headerView.findViewById(R.id.header_email);

        // Set user details
        String username = dbHelper.getUserName(email); // Fetch username using a helper method
        headerUsername.setText(username != null ? username : "User Name");
        headerEmail.setText(email);


        // Enable the hamburger icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Handle Navigation Item Clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            final int id = item.getItemId();

            switch (id) {
                case R.id.nav_home:
                    Toast.makeText(OwnerDashboardActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.nav_profile:
                    Intent intent = new Intent(OwnerDashboardActivity.this, ProfileActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    break;

                case R.id.nav_settings:
                    //startActivity(new Intent(OwnerDashboardActivity.this, SettingsActivity.class));
                    Toast.makeText(OwnerDashboardActivity.this, "Settings selected", Toast.LENGTH_SHORT).show();

                    break;

                case R.id.nav_logout:
                    new AlertDialog.Builder(OwnerDashboardActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(OwnerDashboardActivity.this, LoginActivity.class));
                                finish();
                            })
                            .setNegativeButton("No", null)
                            .show();
                    break;

                default:
                    Toast.makeText(OwnerDashboardActivity.this, "Invalid selection", Toast.LENGTH_SHORT).show();
                    break;
            }

            // Close the drawer after selection
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        addBusButton = findViewById(R.id.fab_add_bus);

        addBusButton.setOnClickListener(view -> {
            Intent intent = new Intent(OwnerDashboardActivity.this, AddBusActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_BUS);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        recyclerView = findViewById(R.id.registered_buses_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Bus> busList = dbHelper.getAllBuses(); // Fetch data from SQLite

        if (busList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        adapter = new BusAdapter(busList, this);
        adapter.setOnDeleteListener(() -> {
            totalBusesTextView.setText(String.valueOf(dbHelper.getTotalBuses()));
            totalRoutesTextView.setText(String.valueOf(dbHelper.getTotalRoutes()));
        });

        recyclerView.setAdapter(adapter);

        updateHeaderEmail(email);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TextView totalBusesTextView = findViewById(R.id.total_buses);
        TextView totalRoutesTextView = findViewById(R.id.total_routes);

        if (requestCode == BusAdapter.REQUEST_FOR_LIST_UPDATE || requestCode == REQUEST_CODE_ADD_BUS) {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            List<Bus> busList = dbHelper.getAllBuses(); // Fetch data from SQLite

            if (busList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

            adapter = new BusAdapter(busList, this);
            adapter.setOnDeleteListener(() -> {
                totalBusesTextView.setText(String.valueOf(dbHelper.getTotalBuses()));
                totalRoutesTextView.setText(String.valueOf(dbHelper.getTotalRoutes()));
            });
            recyclerView.setAdapter(adapter);

            totalBusesTextView.setText(String.valueOf(dbHelper.getTotalBuses()));
            totalRoutesTextView.setText(String.valueOf(dbHelper.getTotalRoutes()));

            adapter.notifyDataSetChanged();

            /*int position = data.getIntExtra("position", -1);

            if (position != -1) {
                myList.set(position, updatedItem); // Update the list
                myAdapter.notifyDataSetChanged(); // Notify adapter
            }*/
        }
    }
}
