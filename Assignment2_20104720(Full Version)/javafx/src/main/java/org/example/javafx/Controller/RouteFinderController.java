package org.example.javafx.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.javafx.Models.*;

import java.net.URL;
import java.util.*;

public class RouteFinderController implements Initializable {

    // UI Components
    @FXML
    public MenuButton startStation;
    @FXML
    public MenuButton endStation;
    @FXML
    public MenuButton avoidStation;
    @FXML
    public MenuButton waypointStation;
    @FXML
    public ListView<String> routeOutput;
    @FXML
    public ListView<String> dfsAllListView;
    @FXML
    public ListView<String> avoidView;
    @FXML
    public ListView<String> waypointView;
    @FXML
    public ImageView mapImage;

    // Internal Data
    private final Graph graph = new Graph();
    public final Set<Station> avoidStations = new HashSet<>();
    private final List<Station> waypoints = new ArrayList<>();
    public final Map<String, Station> stationMap = new HashMap<>();
    public Station selectedStartStation;
    public Station selectedEndStation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load static subway map image
        mapImage.setImage(new Image(Objects.requireNonNull(
                getClass().getResource("/Image/U-Bahn-Netz_Wien_2000.png")).toExternalForm()));
    }

    // Load station data from CSV and populate MenuButtons
    @FXML
    public void populateMap(ActionEvent event) {
        try {
            GraphBuilder builder = new GraphBuilder();
            Set<Station> stations = builder.loadGraphFromCSV("/CSV/vienna_subway.csv");

            stationMap.clear();
            for (Station s : stations) {
                stationMap.put(s.getStationName(), s);
            }

            populateMenuButtons(new ArrayList<>(stations));
            routeOutput.setItems(FXCollections.observableArrayList("✅ Map populated with stations."));
        } catch (Exception e) {
            e.printStackTrace();
            routeOutput.setItems(FXCollections.observableArrayList("❌ Failed to load map."));
        }
    }

    private void populateMenuButtons(List<Station> stations) {
        // Reset all menu options
        startStation.getItems().clear();
        endStation.getItems().clear();
        avoidStation.getItems().clear();
        waypointStation.getItems().clear();

        stations.sort(Comparator.comparing(Station::getStationName));

        for (Station station : stations) {
            String name = station.getStationName();

            MenuItem startItem = new MenuItem(name);
            startItem.setOnAction(e -> {
                selectedStartStation = station;
                startStation.setText("Start: " + name);
            });

            MenuItem endItem = new MenuItem(name);
            endItem.setOnAction(e -> {
                selectedEndStation = station;
                endStation.setText("End: " + name);
            });

            MenuItem avoidItem = new MenuItem(name);
            avoidItem.setOnAction(e -> avoidStation.setText(name));

            MenuItem waypointItem = new MenuItem(name);
            waypointItem.setOnAction(e -> waypointStation.setText(name));

            startStation.getItems().add(startItem);
            endStation.getItems().add(endItem);
            avoidStation.getItems().add(avoidItem);
            waypointStation.getItems().add(waypointItem);
        }
    }

    // Avoid Station Handlers
    @FXML
    public void addAvoid(ActionEvent event) {
        String name = avoidStation.getText();
        Station station = stationMap.get(name);
        if (station != null && avoidStations.add(station)) {
            avoidView.getItems().add(name);
        }
    }

    @FXML
    public void removeAvoid(ActionEvent event) {
        String selected = avoidView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            avoidStations.remove(stationMap.get(selected));
            avoidView.getItems().remove(selected);
        }
    }

    // Waypoint Handlers
    @FXML
    public void addWaypoint(ActionEvent event) {
        String name = waypointStation.getText();
        Station station = stationMap.get(name);
        if (station != null && !waypoints.contains(station)) {
            waypoints.add(station);
            waypointView.getItems().add(name);
        }
    }

    @FXML
    public void removeWaypoint(ActionEvent event) {
        String selected = waypointView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            waypoints.remove(stationMap.get(selected));
            waypointView.getItems().remove(selected);
        }
    }

    // Search Algorithms
    @FXML
    public void bfsSearch(ActionEvent event) {
        if (!checkStations()) return;

        List<Station> fullPath = new ArrayList<>();
        fullPath.add(selectedStartStation);
        fullPath.addAll(waypoints);
        fullPath.add(selectedEndStation);

        double totalStops = 0;
        List<Station> combinedPath = new ArrayList<>();

        for (int i = 0; i < fullPath.size() - 1; i++) {
            Station from = fullPath.get(i);
            Station to = fullPath.get(i + 1);
            Route segment = graph.findShortestPathBFSWithAvoid(from, to, avoidStations);

            if (segment == null) {
                outputMessage("❌ No BFS route through waypoints.");
                return;
            }

            if (i > 0) segment.getPath().remove(0); // avoid duplicate
            combinedPath.addAll(segment.getPath());
            totalStops += segment.getStops();
        }

        Route fullRoute = new Route(combinedPath, (int) totalStops);
        displayRoute("BFS + Waypoints + Avoid", fullRoute);
    }

    @FXML
    public void dijkstraSearch(ActionEvent event) {
        if (!checkStations()) return;

        List<Station> fullPath = new ArrayList<>();
        fullPath.add(selectedStartStation);
        fullPath.addAll(waypoints);
        fullPath.add(selectedEndStation);

        double totalDistance = 0;
        List<Station> combinedPath = new ArrayList<>();

        for (int i = 0; i < fullPath.size() - 1; i++) {
            Station from = fullPath.get(i);
            Station to = fullPath.get(i + 1);
            Route segment = graph.findShortestPathDijkstraWithAvoid(new HashSet<>(stationMap.values()), from, to, avoidStations);

            if (segment == null) {
                outputMessage("❌ No Dijkstra route through waypoints.");
                return;
            }

            if (i > 0) segment.getPath().remove(0);
            combinedPath.addAll(segment.getPath());
            totalDistance += segment.getDistance();
        }

        Route fullRoute = new Route(combinedPath, totalDistance);
        displayRoute("Dijkstra + Waypoints + Avoid", fullRoute);
    }

    @FXML
    public void dijkstraWithPenaltySearch(ActionEvent event) {
        if (!checkStations()) return;
        double penalty = 10.0;

        List<Station> fullPath = new ArrayList<>();
        fullPath.add(selectedStartStation);
        fullPath.addAll(waypoints);
        fullPath.add(selectedEndStation);

        double totalDistance = 0;
        List<Station> combinedPath = new ArrayList<>();

        for (int i = 0; i < fullPath.size() - 1; i++) {
            Station from = fullPath.get(i);
            Station to = fullPath.get(i + 1);
            Route segment = graph.findShortestPathDijkstraWithPenalty(new HashSet<>(stationMap.values()), from, to, penalty, avoidStations);

            if (segment == null) {
                outputMessage("❌ No Dijkstra + Penalty route through waypoints.");
                return;
            }

            if (i > 0) segment.getPath().remove(0);
            combinedPath.addAll(segment.getPath());
            totalDistance += segment.getDistance();
        }

        Route fullRoute = new Route(combinedPath, totalDistance);
        displayRoute("Dijkstra + Penalty + Waypoints + Avoid", fullRoute);
    }

    @FXML
    public void dfsSearch(ActionEvent event) {
        if (!checkStations()) return;

        List<Station> fullPath = new ArrayList<>();
        fullPath.add(selectedStartStation);
        fullPath.addAll(waypoints);
        fullPath.add(selectedEndStation);

        List<Route> allSegments = new ArrayList<>();
        for (int i = 0; i < fullPath.size() - 1; i++) {
            Station from = fullPath.get(i);
            Station to = fullPath.get(i + 1);
            List<Route> routes = graph.findAllRoutesDFSWithAvoid(from, to, avoidStations);

            if (routes.isEmpty()) {
                outputMessage("❌ No DFS route through waypoints.");
                return;
            }

            Route shortest = routes.stream().min(Comparator.comparingInt(Route::getStops)).orElse(null);
            allSegments.add(shortest);
        }

        List<Station> finalPath = new ArrayList<>();
        int totalStops = 0;

        for (int i = 0; i < allSegments.size(); i++) {
            Route r = allSegments.get(i);
            if (i > 0) r.getPath().remove(0);
            finalPath.addAll(r.getPath());
            totalStops += r.getStops();
        }

        Route fullRoute = new Route(finalPath, totalStops);
        displayRoute("DFS (Shortest) + Waypoints + Avoid", fullRoute);
    }

    @FXML
    public void dfsAllSearch(ActionEvent event) {
        if (!checkStations()) return;

        List<Route> allRoutes = graph.findAllRoutesDFS(selectedStartStation, selectedEndStation);
        if (allRoutes.isEmpty()) {
            dfsAllListView.setItems(FXCollections.observableArrayList("❌ No DFS routes found."));
            return;
        }

        List<String> output = new ArrayList<>();
        int count = 1;
        for (Route r : allRoutes) {
            output.add("Path " + count++ + " (" + r.getStops() + " stops):");
            for (Station s : r.getPath()) {
                output.add(" → " + s.getStationName());
            }
            output.add(""); // spacing
        }

        dfsAllListView.setItems(FXCollections.observableArrayList(output));
    }

    @FXML
    public void waypointSearch(ActionEvent event) {
        if (waypoints.size() < 2) {
            outputMessage("⚠️ Add at least 2 waypoints.");
            return;
        }

        double penalty = 10.0;
        Route route = graph.findShortestPathWithWaypoints(
                new HashSet<>(stationMap.values()), waypoints, penalty, avoidStations);
        displayRoute("Waypoints + Avoid + Penalty", route);
    }

    // Utility: Display route to routeOutput
    public void displayRoute(String title, Route route) {
        List<String> output = new ArrayList<>();
        if (route == null) {
            output.add("❌ No route found.");
        } else {
            output.add("🚇 " + title);
            output.add("Stops: " + route.getStops());
            if (route.getDistance() > 0) {
                output.add("Distance: " + String.format("%.2f", route.getDistance()));
            }
            output.add("Path:");
            for (Station station : route.getPath()) {
                output.add(" → " + station.getStationName());
            }
        }
        routeOutput.setItems(FXCollections.observableArrayList(output));
    }

    // Utility: Check if both start and end are selected
    private boolean checkStations() {
        if (selectedStartStation == null || selectedEndStation == null) {
            outputMessage("⚠️ Please select both Start and End stations.");
            return false;
        }
        return true;
    }

    public void outputMessage(String message) {
        routeOutput.setItems(FXCollections.observableArrayList(message));
    }

    @FXML
    public void clearMap(ActionEvent event) {
        // Reset all visual and internal selections
        routeOutput.getItems().clear();
        dfsAllListView.getItems().clear();
        avoidView.getItems().clear();
        waypointView.getItems().clear();

        startStation.setText("Starting Station");
        endStation.setText("End Station");
        avoidStation.setText("Select Station");
        waypointStation.setText("Select Station");

        avoidStations.clear();
        waypoints.clear();
        selectedStartStation = null;
        selectedEndStation = null;
    }

    @FXML
    public void closeApplication(ActionEvent event) {
        Platform.exit();
    }
}