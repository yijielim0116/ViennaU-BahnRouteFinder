package org.example.javafx.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a station on the Vienna U-Bahn network.
 * Each station has a name, coordinates (x, y), neighbouring stations with distances,
 * lines it belongs to, and route-related metadata used in pathfinding algorithms.
 */
public class Station {

    // Station name (e.g., "Stephansplatz")
    private String stationName;

    // Coordinates on the map (used for graphical plotting and distance calculation)
    private double x;
    private double y;

    // Neighbouring stations and their distances (edge weight)
    private Map<Station, Link> neighbours = new HashMap<>();

    // Lines that pass through this station (e.g., U1, U2)
    private List<Line> lines;

    // Distance values to neighbouring stations (optional if not using the Map)
    private List<Double> distances;

    // Used for Dijkstra's algorithm – distance from starting station
    private double distanceFromStart = Double.MAX_VALUE;

    // Used for path reconstruction in Dijkstra's algorithm
    private Station previousStation;

    /**
     * Constructor to initialize station with name and coordinates.
     */
    public Station(String stationName, double x, double y) {
        this.stationName = stationName;
        this.x = x;
        this.y = y;
        this.neighbours = new HashMap<>();
        this.lines = new ArrayList<>();
        this.distances = new ArrayList<>();
    }

    // -------------------- GETTERS & SETTERS --------------------

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Map<Station, Link> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Map<Station, Link> neighbours) {
        this.neighbours = neighbours;
    }

    public Link getLinkTo(Station neighbor) {
        return neighbours.get(neighbor);
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Double> getDistances() {
        return distances;
    }

    public void setDistances(List<Double> distances) {
        this.distances = distances;
    }

    public double getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(double distanceFromStart) {
        this.distanceFromStart = distanceFromStart;
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public void setPreviousStation(Station previousStation) {
        this.previousStation = previousStation;
    }

    // -------------------- UTILITY METHODS --------------------

    /**
     * Adds a neighbour to this station and calculates the distance between them.
     * This is useful for graph edge creation.
     */
    public void addNeighbour(Station neighbour, Line line) {
        double distance = this.calculateDistanceTo(neighbour);
        Link link = new Link(this, neighbour, line, distance);
        this.neighbours.put(neighbour, link);
    }

    /**
     * Calculates Euclidean distance between this station and another.
     * Used as edge weight in pathfinding algorithms.
     */
    public double calculateDistanceTo(Station other) {
        return Math.sqrt(Math.pow(this.x - other.getX(), 2) + Math.pow(this.y - other.getY(), 2));
    }

    /**
     * Adds a U-Bahn line that passes through this station.
     */
    public void addLine(Line line) {
        this.lines.add(line);
    }

    /**
     * Returns a readable summary of the station, including neighbour station names.
     */
    @Override
    public String toString() {
        String neighborNames = neighbours.keySet().stream()
                .map(Station::getStationName)
                .collect(Collectors.joining(", "));

        return "Station: " + "Name: " + stationName +
                ", xCoordinate: " + x +
                ", yCoordinate: " + y +
                ", Neighbouring stations: " + neighborNames;
    }

    public void resetPathfindingData() {
        this.distanceFromStart = Double.MAX_VALUE;
        this.previousStation = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Station station = (Station) obj;
        return stationName.equals(station.stationName);
    }

    @Override
    public int hashCode() {
        return stationName.hashCode();
    }
}
