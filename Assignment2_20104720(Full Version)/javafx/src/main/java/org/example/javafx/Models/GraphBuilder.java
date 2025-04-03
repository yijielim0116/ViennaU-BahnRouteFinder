package org.example.javafx.Models;

import org.example.javafx.Models.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class GraphBuilder {

    private final Map<String, Station> stationMap = new HashMap<>();
    private final Map<String, Line> lineMap = new HashMap<>();

    public Set<Station> loadGraphFromCSV(String resourcePath) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(resourcePath))));

        String line;
        boolean firstLine = true;

        while ((line = br.readLine()) != null) {
            if (firstLine) { firstLine = false; continue; }

            String[] parts = line.split(",");
            if (parts.length < 4) continue;

            String startName = parts[0].trim();
            String endName = parts[1].trim();
            String lineNumber = parts[2].trim();
            String color = parts[3].trim();

            Station start = stationMap.computeIfAbsent(startName, name -> new Station(name, 0, 0));
            Station end = stationMap.computeIfAbsent(endName, name -> new Station(name, 0, 0));

            String lineKey = lineNumber + "-" + color;
            Line lineObj = lineMap.computeIfAbsent(lineKey, k -> new Line("U" + lineNumber, color));

            lineObj.addStation(start);
            lineObj.addStation(end);

            start.addNeighbour(end, lineObj);
            end.addNeighbour(start, lineObj);
        }

        br.close();
        return new HashSet<>(stationMap.values());
    }
}