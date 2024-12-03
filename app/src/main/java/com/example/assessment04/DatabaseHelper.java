package com.example.assessment04;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.assessment04.routedata.Route;
import com.example.assessment04.routedata.Station;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BusBooking.db";
    private static final int DATABASE_VERSION = 1;

    // User Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ROLE = "role";

    // Bus Table
    private static final String TABLE_BUSES = "buses";
    private static final String COLUMN_BUS_NAME = "name";
    //private static final String COLUMN_ROUTE_CODE = "route_code";
    private static final String COLUMN_BUSNO = "bus_no";
    private static final String COLUMN_DRIVER_NAME = "driver_name";

    // Profile Table
    public static final String TABLE_PROFILE = "profile";
    public static final String COLUMN_PROFILE_ID = "id";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_EMAIL_PROFILE = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_IMAGE_URI = "image_uri";

    // Define the Routes Table
    private static final String TABLE_ROUTES = "routes";
    private static final String COLUMN_ROUTE_CODE = "route_code";
    private static final String COLUMN_FROM_STATION = "from_station";
    private static final String COLUMN_TO_STATION = "to_station";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_ROLE + " TEXT NOT NULL)";
        db.execSQL(createUsersTable);

        // Create Buses Table
        String createBusesTable = "CREATE TABLE " + TABLE_BUSES + " (" +
                COLUMN_BUSNO + " TEXT PRIMARY KEY, " +
                COLUMN_BUS_NAME + " TEXT NOT NULL, " +
                COLUMN_ROUTE_CODE + " INTEGER NOT NULL, " +
                COLUMN_DRIVER_NAME + " TEXT NOT NULL)";
        db.execSQL(createBusesTable);

        // Create Profile Table with Email Association
        String createProfileTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + " (" +
                COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL_PROFILE + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT NOT NULL, " +
                COLUMN_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_IMAGE_URI + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + "))";
        db.execSQL(createProfileTable);

        // Create Routes Table
        String createRoutesTable = "CREATE TABLE " + TABLE_ROUTES + " (" +
                COLUMN_ROUTE_CODE + " INTEGER PRIMARY KEY, " +
                COLUMN_FROM_STATION + " TEXT NOT NULL, " +
                COLUMN_TO_STATION + " TEXT NOT NULL)";
        db.execSQL(createRoutesTable);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        onCreate(db);
    }

    // Insert User
    public boolean insertUser(String email, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ROLE, role);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Insert Bus
    public boolean insertBuses(String busNumber, String busName, int routeCode, String driverName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUSNO, busNumber);
        values.put(COLUMN_BUS_NAME, busName);
        values.put(COLUMN_ROUTE_CODE, routeCode);
        values.put(COLUMN_DRIVER_NAME, driverName);

        long result = db.insert(TABLE_BUSES, null, values);
        db.close();
        return result != -1;
    }

    public void insertRoutes() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Insert predefined routes
        values.put(COLUMN_ROUTE_CODE, Route.ColomboToKandy.getRouteCode());
        values.put(COLUMN_FROM_STATION, Route.ColomboToKandy.getFrom().getName());
        values.put(COLUMN_TO_STATION, Route.ColomboToKandy.getTo().getName());
        db.insert(TABLE_ROUTES, null, values);

        values.clear();

        values.put(COLUMN_ROUTE_CODE, Route.ColomboToGalle.getRouteCode());
        values.put(COLUMN_FROM_STATION, Route.ColomboToGalle.getFrom().getName());
        values.put(COLUMN_TO_STATION, Route.ColomboToGalle.getTo().getName());
        db.insert(TABLE_ROUTES, null, values);

        values.clear();

        values.put(COLUMN_ROUTE_CODE, Route.ColomboToKadawatha.getRouteCode());
        values.put(COLUMN_FROM_STATION, Route.ColomboToKadawatha.getFrom().getName());
        values.put(COLUMN_TO_STATION, Route.ColomboToKadawatha.getTo().getName());
        db.insert(TABLE_ROUTES, null, values);

        values.clear();

        values.put(COLUMN_ROUTE_CODE, Route.ColomboToKurunagala.getRouteCode());
        values.put(COLUMN_FROM_STATION, Route.ColomboToKurunagala.getFrom().getName());
        values.put(COLUMN_TO_STATION, Route.ColomboToKurunagala.getTo().getName());
        db.insert(TABLE_ROUTES, null, values);

        values.clear();

        values.put(COLUMN_ROUTE_CODE, Route.ColomboToJaffna.getRouteCode());
        values.put(COLUMN_FROM_STATION, Route.ColomboToJaffna.getFrom().getName());
        values.put(COLUMN_TO_STATION, Route.ColomboToJaffna.getTo().getName());
        db.insert(TABLE_ROUTES, null, values);

        // Insert other routes similarly
        db.close();
    }

    // Get User Role
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return role;
    }

    // Get All Buses
    @SuppressLint("Range")
    public List<Bus> getAllBuses() {
        List<Bus> busList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUSES, null);

        if (cursor.moveToFirst()) {
            do {
                String busNo = cursor.getString(cursor.getColumnIndex(COLUMN_BUSNO));
                String busName = cursor.getString(cursor.getColumnIndex(COLUMN_BUS_NAME));
                int routeCode = cursor.getInt(cursor.getColumnIndex(COLUMN_ROUTE_CODE));
                Route route = Route.routeFrom(routeCode);
                String driverName = cursor.getString(cursor.getColumnIndex(COLUMN_DRIVER_NAME));

                busList.add(new Bus(busNo, busName, route, driverName));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return busList;
    }

    // Save Profile
    public boolean saveProfile(String fullName, String email, String phone, String address, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the profile exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_EMAIL_PROFILE + "=?", new String[]{email});

        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL_PROFILE, email);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_IMAGE_URI, imageUri);

        long result;
        if (cursor.getCount() > 0) {
            // Profile exists, update it
            result = db.update(TABLE_PROFILE, values, COLUMN_EMAIL_PROFILE + "=?", new String[]{email});
        } else {
            // Profile does not exist, insert it
            result = db.insert(TABLE_PROFILE, null, values);
        }

        cursor.close();
        db.close();
        return result != -1;
    }




    // Get Profile
    public Cursor getProfileFor(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_PROFILE + " WHERE " + COLUMN_EMAIL_PROFILE + "=? LIMIT 1", new String[]{email});
    }

    public int deleteBus(String busNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.delete(TABLE_BUSES, COLUMN_BUSNO + "=?", new String[]{busNo});
    }

    @Nullable
    public Bus getBusById(String busNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUSES + " WHERE " + COLUMN_BUSNO + "=?", new String[]{busNo});

        if (cursor != null && cursor.moveToFirst()) {
            int busNumberIndex = cursor.getColumnIndex(COLUMN_BUSNO);
            int busNameIndex = cursor.getColumnIndex(COLUMN_BUS_NAME);
            int routeIndex = cursor.getColumnIndex(COLUMN_ROUTE_CODE);
            int driverNameIndex = cursor.getColumnIndex(COLUMN_DRIVER_NAME);

            if (busNumberIndex >= 0 && busNameIndex >= 0 && routeIndex >= 0 && driverNameIndex >= 0) {
                String busNumber = cursor.getString(busNumberIndex);
                String busName = cursor.getString(busNameIndex);
                int routeCode = cursor.getInt(routeIndex);
                Route route = Route.routeFrom(routeCode);
                String driverName = cursor.getString(driverNameIndex);

                cursor.close();
                return new Bus(busNumber, busName, route, driverName);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }


    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT full_name FROM profile WHERE email = ?", new String[]{email});

        String username = null;
        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }

        cursor.close();
        return username;
    }



    public boolean updateBus(String busNo, String busName, int route, String driverName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUS_NAME, busName);
        values.put(COLUMN_ROUTE_CODE, route);
        values.put(COLUMN_DRIVER_NAME, driverName);

        int rowsAffected = db.update(TABLE_BUSES, values, COLUMN_BUSNO + "=?", new String[]{busNo});
        db.close();
        return rowsAffected > 0; // Return true if the update was successful
    }

    public int getTotalBuses() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BUSES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getTotalRoutes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(DISTINCT " + COLUMN_ROUTE_CODE + ") FROM " + TABLE_BUSES, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    // Get all available drivers not already assigned to a bus
    public List<String> getAvailableDrivers() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> availableDrivers = new ArrayList<>();

        // Query to fetch drivers not already assigned to a bus
        String query = "SELECT " + COLUMN_EMAIL + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_ROLE + " = 'Bus Driver' " +
                " AND " + COLUMN_EMAIL + " NOT IN (SELECT " + COLUMN_DRIVER_NAME + " FROM " + TABLE_BUSES + ")";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(0);
                availableDrivers.add(email); // Add driver email to the list
                System.out.println("Driver found: " + email); // Debug log
            } while (cursor.moveToNext());
        } else {
            System.out.println("No available drivers found."); // Debug log
        }

        cursor.close();
        db.close();
        return availableDrivers;
    }


    // Add a method in DatabaseHelper to fetch assigned bus for the driver
    public Bus getAssignedBusForDriver(String driverEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BUSES + " WHERE " + COLUMN_DRIVER_NAME + "=?", new String[]{driverEmail});

        if (cursor != null && cursor.moveToFirst()) {
            int busNoIndex = cursor.getColumnIndex(COLUMN_BUSNO);
            int busNameIndex = cursor.getColumnIndex(COLUMN_BUS_NAME);
            int routeCodeIndex = cursor.getColumnIndex(COLUMN_ROUTE_CODE);
            int driverNameIndex = cursor.getColumnIndex(COLUMN_DRIVER_NAME);

            // Ensure the indices are valid
            if (busNoIndex != -1 && busNameIndex != -1 && routeCodeIndex != -1 && driverNameIndex != -1) {
                String busNo = cursor.getString(busNoIndex);
                String busName = cursor.getString(busNameIndex);
                int routeCode = cursor.getInt(routeCodeIndex);
                Route route = Route.routeFrom(routeCode);
                String driverName = cursor.getString(driverNameIndex);
                cursor.close();
                return new Bus(busNo, busName, route, driverName);
            } else {
                Log.e("DB Error", "Column index is -1. Check column names.");
            }
        }

        return null;
    }

    public List<String> getBusesForRoute(Route route) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> buses = new ArrayList<>();

        String query = "SELECT " + COLUMN_BUSNO + " FROM " + TABLE_BUSES + " WHERE " + COLUMN_ROUTE_CODE + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(route.getRouteCode())});

        if (cursor.moveToFirst()) {
            do {
                buses.add(cursor.getString(0)); // Fetch bus number
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return buses;
    }

    public List<Bus> getBusesForRouteAndStation(Station station, Route route) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Bus> buses = new ArrayList<>();

        // Query to retrieve buses for the given station and route
        String query = "SELECT b.* FROM buses b " +
                "JOIN routes r ON b.route_code = r.route_code " +
                "WHERE r.from_station = ? AND r.route_code = ?";
        Cursor cursor = db.rawQuery(query, new String[]{station.getName(), String.valueOf(route.getRouteCode())});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get column indices
                int busNoIndex = cursor.getColumnIndex(COLUMN_BUSNO);
                int busNameIndex = cursor.getColumnIndex(COLUMN_BUS_NAME);
                int routeCodeIndex = cursor.getColumnIndex(COLUMN_ROUTE_CODE);
                int driverNameIndex = cursor.getColumnIndex(COLUMN_DRIVER_NAME);

                // Ensure all column indices are valid
                if (busNoIndex != -1 && busNameIndex != -1 && routeCodeIndex != -1 && driverNameIndex != -1) {
                    // Retrieve values from the cursor
                    String busNo = cursor.getString(busNoIndex);
                    String busName = cursor.getString(busNameIndex);
                    int routeCode = cursor.getInt(routeCodeIndex);
                    String driverName = cursor.getString(driverNameIndex);

                    // Convert route_code to Route enum
                    Route routeObj = Route.routeFrom(routeCode);
                    buses.add(new Bus(busNo, busName, routeObj, driverName));
                } else {
                    // Log an error if any column index is invalid
                    Log.e("DB Error", "One or more column indices are -1. Check column names.");
                }
            } while (cursor.moveToNext());
        }

        // Close the cursor and database
        if (cursor != null) cursor.close();
        db.close();

        return buses;
    }




    public List<Bus> getBusesForStation(Station station) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Bus> buses = new ArrayList<>();

        String query = "SELECT " + COLUMN_BUSNO + ", " + COLUMN_BUS_NAME + ", " +
                COLUMN_ROUTE_CODE + ", " + COLUMN_DRIVER_NAME +
                " FROM " + TABLE_BUSES +
                " WHERE " + COLUMN_ROUTE_CODE +
                " IN (SELECT route_code FROM routes WHERE from_station = ?)";

        Cursor cursor = db.rawQuery(query, new String[]{station.getName()});

        if (cursor.moveToFirst()) {
            do {
                int busNoIndex = cursor.getColumnIndex(COLUMN_BUSNO);
                int busNameIndex = cursor.getColumnIndex(COLUMN_BUS_NAME);
                int routeCodeIndex = cursor.getColumnIndex(COLUMN_ROUTE_CODE);
                int driverNameIndex = cursor.getColumnIndex(COLUMN_DRIVER_NAME);

                if (busNoIndex != -1 && busNameIndex != -1 && routeCodeIndex != -1 && driverNameIndex != -1) {
                    String busNo = cursor.getString(busNoIndex);
                    String busName = cursor.getString(busNameIndex);
                    int routeCode = cursor.getInt(routeCodeIndex);
                    String driverName = cursor.getString(driverNameIndex);

                    Route route = Route.routeFrom(routeCode); // Convert route_code to Route enum
                    buses.add(new Bus(busNo, busName, route, driverName));
                } else {
                    Log.e("DB Error", "One or more column indices are -1. Check column names.");
                }
            } while (cursor.moveToNext());
        } else {
            Log.w("DB Warning", "No buses found for station: " + station.getName());
        }

        cursor.close();
        db.close();
        return buses;
    }



    public List<Route> getAllRoutes() {
        List<Route> routeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ROUTES, null);

        // Get column indices
        int routeCodeIndex = cursor.getColumnIndex(COLUMN_ROUTE_CODE);
        int fromStationIndex = cursor.getColumnIndex(COLUMN_FROM_STATION);
        int toStationIndex = cursor.getColumnIndex(COLUMN_TO_STATION);

        // Log the column indices to check if they are correct
        Log.d("Database", "routeCodeIndex: " + routeCodeIndex);
        Log.d("Database", "fromStationIndex: " + fromStationIndex);
        Log.d("Database", "toStationIndex: " + toStationIndex);

        // Check if the columns are valid
        if (routeCodeIndex == -1 || fromStationIndex == -1 || toStationIndex == -1) {
            Log.e("Database Error", "Column not found. Please check column names.");
        }

        if (cursor.moveToFirst()) {
            do {
                // Get values from the cursor
                int routeCode = cursor.getInt(routeCodeIndex);
                String fromStation = cursor.getString(fromStationIndex);
                String toStation = cursor.getString(toStationIndex);

                // Get the Route enum values using valueOf() instead of new
                Route route = Route.valueOf(fromStation + "To" + toStation); // Construct the enum constant name dynamically

                // Add the route to the list
                routeList.add(route);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return routeList;
    }



}



