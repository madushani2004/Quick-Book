package com.example.assessment04.routedata;

import androidx.annotation.NonNull;

public enum Route {
    ColomboToKandy(Station.ColomboFort, Station.Kandy),
    ColomboToGalle(Station.ColomboFort, Station.Galle),
    ColomboToKadawatha(Station.ColomboFort, Station.Kadawatha),
    ColomboToKurunagala(Station.ColomboFort, Station.Kurunagala),
    ColomboToJaffna(Station.ColomboFort, Station.Jaffna);
    // Always add new Routes here;

    private final Station from;
    private final Station to;
    private final int routeCode;

    Route(Station from, Station station) {
        this.from = from;
        this.to = station;
        routeCode = ordinal();
    }

    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    @NonNull
    @Override
    public String toString() {
        return from.getName() + " - " + to.getName();
    }

    public int getRouteCode() {
        return routeCode;
    }

    public static Route routeFrom(int routeCode) {
        return Route.values()[routeCode];
    }
}
