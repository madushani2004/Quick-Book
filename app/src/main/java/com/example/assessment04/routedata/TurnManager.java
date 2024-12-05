package com.example.assessment04.routedata;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.assessment04.DatabaseHelper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TurnManager {

    private static final int TURN_INTERVAL_MINUTES = 60; // Interval between buses
    private static final int REST_TIME_MINUTES = 30;     // Rest time after completing a route
    private final Map<Route, Integer> routeTravelTimes;  // Travel times for each route
    private final Map<Route, List<Turn>> turnSchedules;  // Schedule for each route
    private static TurnManager manager = null;

    private TurnManager() {
        this.routeTravelTimes = new HashMap<>();
        this.turnSchedules = new HashMap<>();
        initializeRouteTravelTimes();
    }

    // Initialize travel times for each route
    private void initializeRouteTravelTimes() {
        routeTravelTimes.put(Route.ColomboToKandy, 180); // 3 hours
        routeTravelTimes.put(Route.ColomboToGalle, 120); // 2 hours
        routeTravelTimes.put(Route.ColomboToKurunagala, 150); // 2.5 hours
    }

    // Assign turns for a given route
    public void assignTurns(List<String> busNumbers, LocalTime startTime, DatabaseHelper dbHelper) {
        List<Turn> schedule = new ArrayList<>();
        LocalTime currentTime = startTime;

        for (String busNumber : busNumbers) {
            Route r = dbHelper.getRouteFor(busNumber);
            Turn turn = new Turn(busNumber, currentTime, r);
            Integer travelTimeMinutesObj = routeTravelTimes.get(r);
            int travelTimeMinutes = (travelTimeMinutesObj == null)? 0: travelTimeMinutesObj;

            schedule.add(turn);
            Log.d("TurnManager", "Assigned Turn: " + turn);
            currentTime = currentTime.plusMinutes(travelTimeMinutes + REST_TIME_MINUTES);
        }

        /*turnSchedules.put(route, schedule);
        saveTurnsToDatabase(dbHelper, route);*/

        saveTurnsToDB(dbHelper, schedule);

    }

    private void saveTurnsToDB(DatabaseHelper dbHelper, List<Turn> schedule) {
        dbHelper.clearTurnTable();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (Turn turn: schedule) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TURN_ROUTE_CODE, turn.getRoute().getRouteCode());
            values.put(DatabaseHelper.COLUMN_TURN_BUS_NO, turn.getBusNumber());
            values.put(DatabaseHelper.COLUMN_TURN_DEPARTURE_TIME, turn.getDepartureTime().toString());

            db.insert(DatabaseHelper.TABLE_TURNS, null, values);
        }

        db.close();

        turnSchedules.clear();
        for (Route route: Route.values()) {
            List<Turn> list = schedule.stream().filter(item -> item.getRoute() == route).collect(Collectors.toList());
            turnSchedules.put(route, list);
        }
    }

    public void retrieveTurnsFromDB(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        List<Turn> schedule = helper.retrieveTurns();
        turnSchedules.clear();
        for (Route route: Route.values()) {
            List<Turn> list = schedule.stream().filter(item -> item.getRoute() == route).collect(Collectors.toList());
            turnSchedules.put(route, list);
        }
        helper.close();
    }


    public void saveTurnsToDatabase(DatabaseHelper dbHelper, Route route) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Turn> schedule = turnSchedules.get(route);

        for (Turn turn : schedule) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TURN_ROUTE_CODE, route.getRouteCode());
            values.put(DatabaseHelper.COLUMN_TURN_BUS_NO, turn.getBusNumber());
            values.put(DatabaseHelper.COLUMN_TURN_DEPARTURE_TIME, turn.getDepartureTime().toString());

            db.insert(DatabaseHelper.TABLE_TURNS, null, values);
        }

        db.close();
    }

    // Get the turn schedule for a route
    public List<Turn> getTurnSchedule(Route route) {
        return turnSchedules.getOrDefault(route, new ArrayList<>());
    }

    // Print the schedule for debugging
    public void printSchedule(Route route) {
        List<Turn> schedule = getTurnSchedule(route);
        System.out.println("Turn Schedule for " + route + ":");
        for (Turn turn : schedule) {
            System.out.println(turn);
        }
    }

    // Class representing a bus turn
    public static class Turn {
        private final String busNumber;
        private final LocalTime departureTime;
        private final Route route;

        public Turn(String busNumber, LocalTime departureTime, Route route) {
            this.busNumber = busNumber;
            this.departureTime = departureTime;
            this.route = route;
        }

        public String getBusNumber() {
            return busNumber;
        }

        public LocalTime getDepartureTime() {
            return departureTime;
        }

        public Route getRoute() {
            return route;
        }

        @NonNull
        @Override
        public String toString() {
            return "Bus " + busNumber + " departs at " + departureTime + " for route " + route;
        }
    }

    public static TurnManager instance() {
        if (manager == null) {
            manager = new TurnManager();
        }
        return manager;
    }

}
