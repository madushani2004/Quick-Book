package com.example.assessment04;

import com.example.assessment04.routedata.Route;

public class Bus {

    private String busNo; // Unique identifier for the bus
    private String busName;
    private Route route;
    private String driverName;

    public Bus(String busNo, String busName, Route route, String driverName) {
        this.busNo = busNo;
        this.busName = busName;
        this.route = route;
        this.driverName = driverName;
    }

    public String getBusNo() {
        return busNo;
    }

    public String getBusName() {
        return busName;
    }

    public Route getRoute() {
        return route;
    }

    public String getDriverName() {
        return driverName;
    }
}
