package com.bcalvario.coverTime.agent;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Environment {
    private final GridCell[][] grid;
    private final List<CarAgent> agents;
    private final Map<Integer, Point> cityLocations;
    private final Map<Point, Integer> locationToCityId;
    private final Graph<Integer, DefaultEdge> network;
    private final int width;
    private final int height;
    private final Random random = new Random();

    public Environment(int width, int height, int agentCount, Graph<Integer, DefaultEdge> network) {
        this.width = width;
        this.height = height;
        this.grid = new GridCell[width][height];
        this.agents = new ArrayList<>();
        this.network = network;
        this.cityLocations = new HashMap<>();
        this.locationToCityId = new HashMap<>();
        initialize();
        generateCityLocations();
        generateFreeways(); // Generate freeways first
        generateRoads();    // Then generate regular roads
        spawnAgents(agentCount);
    }

    public void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new GridCell(CellType.FOREST);
            }
        }
    }

    private void generateCityLocations() {
        for (int cityId : network.vertexSet()) {
            Point p = new Point(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
            cityLocations.put(cityId, p);
            locationToCityId.put(p, cityId);
            grid[p.x][p.y].setType(CellType.CITY);
        }
    }

    private void generateFreeways() {
        // Connect the 2 most distant pairs of cities with a freeway
        List<Integer> cities = new ArrayList<>(network.vertexSet());
        if (cities.size() < 4) return; // Need at least 4 cities for 2 pairs

        // Find the most distant pair
        double maxDist = -1;
        int c1 = -1, c2 = -1;
        for (int i = 0; i < cities.size(); i++) {
            for (int j = i + 1; j < cities.size(); j++) {
                Point p1 = cityLocations.get(cities.get(i));
                Point p2 = cityLocations.get(cities.get(j));
                double dist = p1.distance(p2);
                if (dist > maxDist) {
                    maxDist = dist;
                    c1 = cities.get(i);
                    c2 = cities.get(j);
                }
            }
        }

        // Build freeway between the first pair
        buildPath(c1, c2, CellType.FREEWAY);

        // Remove them and find the next most distant pair
        final int finalC1 = c1;
        final int finalC2 = c2;
        List<Integer> remainingCities = cities.stream()
                .filter(c -> c != finalC1 && c != finalC2)
                .collect(Collectors.toList());

        maxDist = -1;
        int c3 = -1, c4 = -1;
        for (int i = 0; i < remainingCities.size(); i++) {
            for (int j = i + 1; j < remainingCities.size(); j++) {
                Point p1 = cityLocations.get(remainingCities.get(i));
                Point p2 = cityLocations.get(remainingCities.get(j));
                double dist = p1.distance(p2);
                if (dist > maxDist) {
                    maxDist = dist;
                    c3 = remainingCities.get(i);
                    c4 = remainingCities.get(j);
                }
            }
        }
        // Build freeway between the second pair
        if(c3 != -1) {
            buildPath(c3, c4, CellType.FREEWAY);
        }
    }

    private void generateRoads() {
        for (DefaultEdge edge : network.edgeSet()) {
            buildPath(network.getEdgeSource(edge), network.getEdgeTarget(edge), CellType.ROAD);
        }
    }

    private void buildPath(int cityId1, int cityId2, CellType pathType) {
        Point p1 = cityLocations.get(cityId1);
        Point p2 = cityLocations.get(cityId2);
        List<Point> path = Pathfinder.findPathForRoads(p1, p2, grid);
        for (Point point : path) {
            // Don't overwrite cities, but allow roads to become freeways.
            if (grid[point.x][point.y].getType() != CellType.CITY) {
                // Only plain forests can become roads, but roads can be upgraded to freeways
                if (pathType == CellType.ROAD && grid[point.x][point.y].getType() == CellType.FOREST) {
                    grid[point.x][point.y].setType(CellType.ROAD);
                } else if (pathType == CellType.FREEWAY) {
                    grid[point.x][point.y].setType(CellType.FREEWAY);
                }
            }
        }
    }

    public void step() {
        for (CarAgent agent : agents) {
            if (agent.isAtDestination()) {
                handleArrival(agent);
            }
            // Pass the grid to the agent for speed detection
            agent.updatePosition(grid);
        }
        detectCollisions();
    }

    private void detectCollisions() {
        Map<Point, List<CarAgent>> agentLocations = new HashMap<>();
        for (CarAgent agent : agents) {
            agentLocations.computeIfAbsent(agent.getLocation(), k -> new ArrayList<>()).add(agent);
        }

        for (List<CarAgent> agentsInCell : agentLocations.values()) {
            if (agentsInCell.size() > 1) {
                // Potential for collision
                for (int i = 0; i < agentsInCell.size(); i++) {
                    for (int j = i + 1; j < agentsInCell.size(); j++) {
                        CarAgent agent1 = agentsInCell.get(i);
                        CarAgent agent2 = agentsInCell.get(j);

                        // If they aren't already crashed, there's a chance they crash
                        if (!agent1.isCrashed() && !agent2.isCrashed()) {
                            if (random.nextDouble() < 0.15) { // 15% chance of a crash
                                agent1.crash();
                                agent2.crash();
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleArrival(CarAgent agent) {
        Point currentLocation = agent.getLocation();
        if (locationToCityId.containsKey(currentLocation)) {
            int cityId = locationToCityId.get(currentLocation);
            agent.visitCity(currentLocation);

            List<Integer> neighborCities = new ArrayList<>(Graphs.neighborListOf(network, cityId));
            if (neighborCities.isEmpty()) return;

            if (neighborCities.size() > 1 && agent.getPrevCity() != null) {
                neighborCities.remove(agent.getPrevCity());
            }
            int destinationCityId = neighborCities.get(random.nextInt(neighborCities.size()));
            Point newDestination = cityLocations.get(destinationCityId);

            List<Point> path = Pathfinder.findPathForAgent(currentLocation, newDestination, grid);
            agent.setPath(path, cityId);
        }
    }

    public boolean areAllVisited() {
        Set<Point> visited = new HashSet<>();
        for (CarAgent agent : agents) {
            visited.addAll(agent.hasVisited());
        }
        return visited.size() == cityLocations.size();
    }

    public void spawnAgents(int agentCount) {
        List<Integer> cityIds = new ArrayList<>(network.vertexSet());
        for (int i = 0; i < agentCount; i++) {
            int startCity = cityIds.get(i % cityIds.size());
            Point startLocation = cityLocations.get(startCity);
            agents.add(new CarAgent(startLocation));
        }
    }

    public GridCell[][] getGrid() {
        return grid;
    }

    public List<CarAgent> getAgents() {
        return agents;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}