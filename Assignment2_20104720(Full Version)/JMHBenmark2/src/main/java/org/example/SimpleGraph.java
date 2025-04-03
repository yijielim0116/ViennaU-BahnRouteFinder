package org.example;

import java.util.*;

public class SimpleGraph {
    private final Map<SimpleStation, List<SimpleStation>> adjacency = new HashMap<>();

    public void addConnection(SimpleStation a, SimpleStation b) {
        adjacency.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        adjacency.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    public SimpleRoute bfs(SimpleStation start, SimpleStation end) {
        Set<SimpleStation> visited = new HashSet<>();
        Queue<List<SimpleStation>> queue = new LinkedList<>();
        queue.add(List.of(start));

        while (!queue.isEmpty()) {
            List<SimpleStation> path = queue.poll();
            SimpleStation current = path.get(path.size() - 1);

            if (current.equals(end)) {
                return new SimpleRoute(path, path.size() - 1, path.size() * 1.5);
            }

            if (visited.add(current)) {
                for (SimpleStation neighbor : adjacency.getOrDefault(current, List.of())) {
                    List<SimpleStation> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return null;
    }
}