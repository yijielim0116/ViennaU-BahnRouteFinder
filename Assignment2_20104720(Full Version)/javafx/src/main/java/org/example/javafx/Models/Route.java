package org.example.javafx.Models;

import java.util.List;

/**
 * Represents a route/path between two U-Bahn stations.
 * This class stores the sequence of stations (the path),
 * the number of stops along the route, and optionally the total distance.
 */
public class Route {

    // List of stations that make up the path (in order)
    private List<Station> path;

    // Number of stops on this route (used in BFS or DFS)
    private int stops;

    // Total distance of this route (used in Dijkstra's algorithm)
    private double distance;

    /**
     * Constructor for a route where the number of stops is known (e.g., BFS).
     * Distance defaults to 0.
     */
    public Route(List<Station> path, int stops) {
        this.path = path;
        this.stops = stops;
        this.distance = 0; // default when distance is not provided
    }

    /**
     * Constructor for a route where the total distance is known (e.g., Dijkstra).
     * Number of stops defaults to 0.
     */
    public Route(List<Station> path, double distance) {
        this.path = path;
        this.distance = distance;
        this.stops = 0; // default when stops are not provided
    }

    // ------------------ GETTERS & SETTERS ------------------

    /**
     * Returns the list of stations on the route.
     */
    public List<Station> getPath() {
        return path;
    }

    /**
     * Sets the list of stations on the route.
     */
    public void setPath(List<Station> path) {
        this.path = path;
    }

    /**
     * Returns the number of stops on the route.
     */
    public int getStops() {
        return (path != null) ? Math.max(0, path.size() - 1) : 0;
    }

    /**
     * Sets the number of stops on the route.
     */
    public void setStops(int stops) {
        this.stops = stops;
    }

    /**
     * Returns the total distance of the route.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the total distance of the route.
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
}
