package com.example.assessment04.routedata;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnManager {

    private static final int TURN_INTERVAL_MINUTES = 60; // Interval between buses
    private static final int REST_TIME_MINUTES = 30;     // Rest time after completing a route
    private final Map<Route, Integer> routeTravelTimes;  // Travel times for each route
    private final Map<Route, List<Turn>> turnSchedules;  // Schedule for each route

    public TurnManager() {
        this.routeTravelTimes = new HashMap<>();
        this.turnSchedules = new HashMap<>();
        initializeRouteTravelTimes();
    }

    // Initialize travel times for each route
    private void initializeRouteTravelTimes() {
        routeTravelTimes.put(Route.ColomboToKandy, 180); // 3 hours
        routeTravelTimes.put(Route.ColomboToGalle, 120); // 2 hours
        routeTravelTimes.put(Route.ColomboToKadawatha, 60); // 1 hour
        routeTravelTimes.put(Route.ColomboToKurunagala, 150); // 2.5 hours
    }

    // Assign turns for a given route
    public void assignTurns(Route route, List<String> busNumbers, LocalTime startTime) {
        int travelTimeMinutes = routeTravelTimes.get(route);
        List<Turn> schedule = new ArrayList<>();

        LocalTime currentTime = startTime;

        for (String busNumber : busNumbers) {
            schedule.add(new Turn(busNumber, currentTime, route));
            currentTime = currentTime.plusMinutes(travelTimeMinutes + REST_TIME_MINUTES);
        }

        turnSchedules.put(route, schedule);
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

        @Override
        public String toString() {
            return "Bus " + busNumber + " departs at " + departureTime + " for route " + route;
        }
    }
}
