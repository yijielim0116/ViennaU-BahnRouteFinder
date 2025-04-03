package org.example.javafx.Models;

public class Link {
    private Station from;
    private Station to;
    private Line line;
    private double distance;

    public Link(Station from, Station to, Line line, double distance) {
        this.from = from;
        this.to = to;
        this.line = line;
        this.distance = distance;
    }

    // Getters
    public Station getFrom() {
        return from;
    }

    public Station getTo() {
        return to;
    }

    public Line getLine() {
        return line;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return from.getStationName() + " → " + to.getStationName() +
                " on " + line.getLineName() + " (" + distance + ")";
    }
}
