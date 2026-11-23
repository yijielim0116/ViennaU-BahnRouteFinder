# Vienna U-Bahn Route Finder

A JavaFX app for finding routes between stations on the Vienna U-Bahn, using depth-first search, breadth-first search and Dijkstra's algorithm, with the route drawn on the network map in the line colours.

Data Structures and Algorithms 2 · CA Exercise 2 (team exercise) · South East Technological University (SETU)

## Features

- **Multiple routes (DFS):** lists different route permutations between two stations.
- **Fewest stops (BFS):** the route that passes the fewest stations.
- **Shortest distance (Dijkstra):** the route with the shortest travel distance.
- **Fewest line changes (Dijkstra with a penalty):** shortest distance plus a user-set cost for every change of line.
- **Waypoints and avoided stations:** choose stations the route must pass through or must avoid.
- **Map:** the chosen route is drawn on the U-Bahn map in each line's colour.

The network (5 lines, 98 stations) is loaded from `vienna_subway.csv` into a graph of stations and links.

## Project structure

| Path | Contents |
|---|---|
| `javafx/src/main/java/.../Models/` | `Graph` (all the search algorithms), `GraphBuilder`, `Station`, `Link`, `Line`, `Route` |
| `javafx/src/main/java/.../Controller/RouteFinderController.java` | User interface logic and route drawing |
| `javafx/src/main/resources/CSV/vienna_subway.csv` | Station connections, lines and colours |
| `javafx/src/main/resources/Image/` | U-Bahn network maps |
| `javafx/src/main/test/` | JUnit tests |
| `JMHBenmark2/` | JMH benchmarks for BFS, DFS and Dijkstra |
| `CA Exercise 2 2025.pdf` | The assignment brief |

## Running it

From `Assignment2_20104720(Full Version)/javafx`:

```bash
./mvnw javafx:run
```

## Tools

Java · JavaFX · FXML · JUnit 5 · JMH · Maven
