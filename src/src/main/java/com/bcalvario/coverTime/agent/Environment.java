package com.bcalvario.coverTime.agent;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        generateRoads();
        spawnAgents(agentCount);
    }

    public void initialize() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new GridCell(CellType.FOREST);
            }
        }
    }

    /**
     * Places cities on the grid and sets their cell type to CITY.
     */
    private void generateCityLocations() {
        int numCities = network.vertexSet().size();
        double idealSeparation = Math.sqrt((double)(width * height) / numCities);
        int minDistance = (int)(idealSeparation * 0.8);

        for (int cityId : network.vertexSet()) {
            Point p;
            boolean tooClose;
            int tries = 0;
            do {
                p = new Point(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
                tooClose = false;
                for (Point existingPoint : cityLocations.values()) {
                    if (p.distance(existingPoint) < minDistance) {
                        tooClose = true;
                        break;
                    }
                }
                tries++;
                if (tries > 2000) {
                    minDistance *= 0.95;
                    tries = 0;
                }
            } while (tooClose);
            cityLocations.put(cityId, p);
            locationToCityId.put(p, cityId);
            // This line is what makes a cell a city.
            grid[p.x][p.y].setType(CellType.CITY);
        }
    }

    /**
     * Generates the road network using a pathfinder.
     */
    private void generateRoads() {
        for (DefaultEdge edge : network.edgeSet()) {
            Point p1 = cityLocations.get(network.getEdgeSource(edge));
            Point p2 = cityLocations.get(network.getEdgeTarget(edge));
            List<Point> roadPath = Pathfinder.findPathForRoads(p1, p2, grid);
            for (Point roadPoint : roadPath) {
                if (grid[roadPoint.x][roadPoint.y].getType() == CellType.FOREST) {
                    grid[roadPoint.x][roadPoint.y].setType(CellType.ROAD);
                }
            }
        }
    }

    public void step() {
        for (CarAgent agent : agents) {
            if (agent.isAtDestination()) {
                handleArrival(agent);
            }
            agent.updatePosition();
        }
    }


    private void handleArrival(CarAgent agent) {
        Point currentLocation = agent.getLocation();
        if (locationToCityId.containsKey(currentLocation)) {
            int cityId = locationToCityId.get(currentLocation);
            agent.visitCity(currentLocation);

            List<Integer> neighborCities = Graphs.neighborListOf(network, cityId);
            if(neighborCities.isEmpty()) return;

            if (neighborCities.size() > 1 && agent.getPrevCity() != null) {
                neighborCities.remove(agent.getPrevCity());
            }

            int destinationCityId = neighborCities.get(random.nextInt(neighborCities.size()));
            Point newDestination = cityLocations.get(destinationCityId);

            // Give the agent a path to follow
            List<Point> path = Pathfinder.findPath(currentLocation, newDestination, grid);
            agent.setPath(path, cityId);
        }
    }

    /*
 checks if the agents have visited every city.
  */
    public boolean areAllVisited() {
        Set<Point> visited = new HashSet<>();
        for (CarAgent agent : agents) {
            visited.addAll(agent.hasVisited());
        }
        return visited.size() == cityLocations.size();
    }

    /*
    Creates the agents and places them at random starting cities.
     */
    public void spawnAgents(int agentCount) {
        List<Integer> cityIds = new ArrayList<>(network.vertexSet());
        for (int i = 0; i < agentCount; i++) {
            int startCity = cityIds.get(i % cityIds.size());
            Point startLocation = cityLocations.get(startCity);
            agents.add(new CarAgent(startLocation));
        }
    }
    private int chooseNextCity(int currentCity,Integer previousCity) {
        List<Integer> neighbor = Graphs.neighborListOf(network, currentCity);
        if(neighbor.size() > 1 && previousCity != null) {
            neighbor.remove(previousCity);
        }
        return neighbor.get(random.nextInt(neighbor.size()));

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
