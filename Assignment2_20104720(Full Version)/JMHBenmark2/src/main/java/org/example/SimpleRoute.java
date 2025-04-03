package org.example;

public class SimpleRoute {
    private final java.util.List<SimpleStation> path;
    private final int stops;
    private final double distance;

    public SimpleRoute(java.util.List<SimpleStation> path, int stops, double distance) {
        this.path = path;
        this.stops = stops;
        this.distance = distance;
    }

    public java.util.List<SimpleStation> getPath() {
        return path;
    }

    public int getStops() {
        return stops;
    }

    public double getDistance() {
        return distance;
    }
}