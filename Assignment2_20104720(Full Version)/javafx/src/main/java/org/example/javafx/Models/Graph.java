package org.example.javafx.Models;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

public class Graph implements Initializable {

    public static Graph graph;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graph = this;
    }

    // ----------------- NEIGHBOR UTILITY -----------------

    public Set<Station> getNeighbours(Station station) {
        return station.getNeighbours().keySet();
    }

    // ----------------- BFS (Shortest by Stops) -----------------

    public Route findShortestPathBFS(Station start, Station end) {
        return findShortestPathBFSWithAvoid(start, end, new HashSet<>());
    }

    public Route findShortestPathBFSWithAvoid(Station start, Station end, Set<Station> toAvoid) {
        Map<Station, Station> previous = new HashMap<>();
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                int stops = 0;
                for (Station s = end; s != null; s = previous.get(s)) {
                    path.add(0, s);
                    stops++;
                }
                return new Route(path, stops - 1);
            }

            for (Station neighbor : getNeighbours(current)) {
                if (!visited.contains(neighbor) && !toAvoid.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    previous.put(neighbor, current);
                }
            }
        }

        return null;
    }

    public Route findShortestPathBFSWithWaypoints(List<Station> waypoints, Set<Station> toAvoid) {
        if (waypoints.size() < 2) return null;

        List<Station> fullPath = new ArrayList<>();
        int totalStops = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Station from = waypoints.get(i);
            Station to = waypoints.get(i + 1);
            Route segment = findShortestPathBFSWithAvoid(from, to, toAvoid);

            if (segment == null) return null;

            if (i > 0) segment.getPath().remove(0); // prevent duplicate node
            fullPath.addAll(segment.getPath());
            totalStops += segment.getStops();
        }

        return new Route(fullPath, totalStops);
    }

    // ----------------- DFS (All Possible Routes) -----------------

    public List<Route> findAllRoutesDFS(Station start, Station end) {
        List<Route> allRoutes = new ArrayList<>();
        Set<Station> visited = new HashSet<>();
        List<Station> currentPath = new ArrayList<>();
        dfsRecursive(start, end, visited, currentPath, allRoutes);
        return allRoutes;
    }

    private void dfsRecursive(Station current, Station end,
                              Set<Station> visited,
                              List<Station> currentPath,
                              List<Route> allRoutes) {

        visited.add(current);
        currentPath.add(current);

        if (current.equals(end)) {
            allRoutes.add(new Route(new ArrayList<>(currentPath), currentPath.size() - 1));
        } else {
            for (Station neighbor : getNeighbours(current)) {
                if (!visited.contains(neighbor)) {
                    dfsRecursive(neighbor, end, visited, currentPath, allRoutes);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    public Route findDFSWithWaypoints(List<Station> waypoints, Set<Station> toAvoid) {
        if (waypoints.size() < 2) return null;

        List<Station> fullPath = new ArrayList<>();
        int totalStops = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Station from = waypoints.get(i);
            Station to = waypoints.get(i + 1);

            List<Route> routes = findAllRoutesDFSWithAvoid(from, to, toAvoid);
            if (routes.isEmpty()) return null;

            // Pick the shortest by stop count
            Route shortest = routes.stream()
                    .min(Comparator.comparingInt(Route::getStops))
                    .orElse(null);

            if (shortest == null) return null;

            // Avoid duplication of the connection point
            if (i > 0) shortest.getPath().remove(0);

            fullPath.addAll(shortest.getPath());
            totalStops += shortest.getStops();
        }

        return new Route(fullPath, totalStops);
    }

    public List<Route> findAllRoutesDFSWithAvoid(Station start, Station end, Set<Station> toAvoid) {
        List<Route> allRoutes = new ArrayList<>();
        Set<Station> visited = new HashSet<>();
        List<Station> currentPath = new ArrayList<>();
        dfsAvoidRecursive(start, end, visited, currentPath, allRoutes, toAvoid);
        return allRoutes;
    }

    private void dfsAvoidRecursive(Station current, Station end,
                                   Set<Station> visited,
                                   List<Station> currentPath,
                                   List<Route> allRoutes,
                                   Set<Station> toAvoid) {

        if (toAvoid.contains(current)) return;

        visited.add(current);
        currentPath.add(current);

        if (current.equals(end)) {
            allRoutes.add(new Route(new ArrayList<>(currentPath), currentPath.size() - 1));
        } else {
            for (Station neighbor : getNeighbours(current)) {
                if (!visited.contains(neighbor)) {
                    dfsAvoidRecursive(neighbor, end, visited, currentPath, allRoutes, toAvoid);
                }
            }
        }

        visited.remove(current);
        currentPath.remove(currentPath.size() - 1);
    }

    // ----------------- DFS: First Valid Route Only -----------------

    public Route findSingleRouteDFS(Station start, Station end) {
        Set<Station> visited = new HashSet<>();
        List<Station> path = new ArrayList<>();
        boolean found = dfsHelper(start, end, visited, path);
        return found ? new Route(path, path.size() - 1) : null;
    }

    private boolean dfsHelper(Station current, Station end, Set<Station> visited, List<Station> path) {
        visited.add(current);
        path.add(current);

        if (current.equals(end)) return true;

        for (Station neighbor : getNeighbours(current)) {
            if (!visited.contains(neighbor)) {
                if (dfsHelper(neighbor, end, visited, path)) return true;
            }
        }

        path.remove(path.size() - 1);
        visited.remove(current);
        return false;
    }

    // ----------------- DIJKSTRA (Shortest by Distance) -----------------

    public Route findShortestPathDijkstra(Set<Station> allStations, Station start, Station end) {
        return findShortestPathDijkstraWithAvoid(allStations, start, end, new HashSet<>());
    }

    public Route findShortestPathDijkstraWithAvoid(Set<Station> allStations, Station start, Station end, Set<Station> toAvoid) {
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Station station : allStations) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                double totalDistance = distances.get(end);
                for (Station s = end; s != null; s = previous.get(s)) {
                    path.add(0, s);
                }
                return new Route(path, totalDistance);
            }

            for (Station neighbor : getNeighbours(current)) {
                if (toAvoid.contains(neighbor)) continue;

                Link link = current.getLinkTo(neighbor);
                double totalDistance = distances.get(current) + link.getDistance();

                if (totalDistance < distances.get(neighbor)) {
                    distances.put(neighbor, totalDistance);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return null;
    }

    public Route findShortestPathDijkstraWithWaypoints(Set<Station> allStations, List<Station> waypoints, Set<Station> toAvoid) {
        if (waypoints.size() < 2) return null;

        List<Station> fullPath = new ArrayList<>();
        double totalDistance = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Station from = waypoints.get(i);
            Station to = waypoints.get(i + 1);
            Route segment = findShortestPathDijkstraWithAvoid(allStations, from, to, toAvoid);

            if (segment == null) return null;

            if (i > 0) segment.getPath().remove(0); // prevent duplicate
            fullPath.addAll(segment.getPath());
            totalDistance += segment.getDistance();
        }

        return new Route(fullPath, totalDistance);
    }

    // ----------------- DIJKSTRA + PENALTY -----------------

    public Route findShortestPathDijkstraWithPenalty(Set<Station> allStations, Station start, Station end, double penalty, Set<Station> toAvoid) {
        Map<Station, Double> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        Map<Station, Line> usedLine = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

        for (Station station : allStations) {
            distances.put(station, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(start);
        usedLine.put(start, null);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            if (current.equals(end)) {
                List<Station> path = new ArrayList<>();
                double totalDistance = distances.get(end);
                for (Station s = end; s != null; s = previous.get(s)) {
                    path.add(0, s);
                }
                return new Route(path, totalDistance);
            }

            for (Station neighbor : getNeighbours(current)) {
                if (toAvoid.contains(neighbor)) continue;

                Link link = current.getLinkTo(neighbor);
                double baseDistance = link.getDistance();
                Line currentLine = link.getLine();
                double linePenalty = 0;

                if (usedLine.get(current) != null && !usedLine.get(current).equals(currentLine)) {
                    linePenalty = penalty;
                }

                double totalDistance = distances.get(current) + baseDistance + linePenalty;

                if (totalDistance < distances.get(neighbor)) {
                    distances.put(neighbor, totalDistance);
                    previous.put(neighbor, current);
                    usedLine.put(neighbor, currentLine);
                    queue.add(neighbor);
                }
            }
        }

        return null;
    }

    public Route findShortestPathWithWaypoints(Set<Station> allStations, List<Station> waypoints, double penalty, Set<Station> toAvoid) {
        if (waypoints.size() < 2) return null;

        List<Station> fullPath = new ArrayList<>();
        double totalDistance = 0;

        for (int i = 0; i < waypoints.size() - 1; i++) {
            Station from = waypoints.get(i);
            Station to = waypoints.get(i + 1);
            Route segment = findShortestPathDijkstraWithPenalty(allStations, from, to, penalty, toAvoid);

            if (segment == null) return null;

            if (i > 0) segment.getPath().remove(0);
            fullPath.addAll(segment.getPath());
            totalDistance += segment.getDistance();
        }

        return new Route(fullPath, totalDistance);
    }
}