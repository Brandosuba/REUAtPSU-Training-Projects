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




    public Environment(int width, int height,int agentCount, Graph<Integer, DefaultEdge>network) {
        this.width = width;
        this.height = height;
        this.grid = new GridCell[width][height];
        this.agents = new ArrayList<>();
        this.network = network;
        this.cityLocations = new HashMap<>();
        this.locationToCityId = new HashMap<>();
    }
    public void initialize() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                grid[x][y] = new GridCell(CellType.FOREST);
            }
        }
    }
    private void generateCityLocations(){
        int numCities = network.vertexSet().size();
        int minDistance = (int) ((width*height)/(numCities > 1 ? numCities * 0.5 : 1));

        for(int cityId :network.vertexSet()){
            Point p;
            boolean tooClose;
            do{
                p = new Point(random.nextInt(width - 4) + 2, random.nextInt(height - 4) + 2);
                tooClose = false;
                for(Point existingPoint : cityLocations.values()){
                    if(p.distance(existingPoint) < minDistance){
                        tooClose = true;
                        break;
                    }
                }
            }while(tooClose);
            cityLocations.put(cityId, p);
            locationToCityId.put(p, cityId);
            grid[p.x][p.y].setType(CellType.CITY);
        }
    }
    private void generateRoads(){
        for(DefaultEdge edge : network.edgeSet()){
            Point p1 = cityLocations.get(network.getEdgeSource(edge));
            Point p2 = cityLocations.get(network.getEdgeTarget(edge));

            int x1 = p1.x,
                    y1 = p1.y;
            int x2 = p2.x,
                    y2 = p2.y;
            int dx = Math.abs(x2 - x1), sx = x1 < x2 ? 1 : -1;
            int dy = -Math.abs(y2 - y1), sy = y1 < y2 ? 1 : -1;
            int err = dx + dy,e2;
            while(true){
                if (grid[x1][y1].getType() == CellType.FOREST){
                    grid[x1][y1].setType(CellType.ROAD);
                }
                if(x1 == x2 && y1 == y2) {
                    break;
                }
                e2 = 2*err;
                if(e2 >= dy){
                    err+=dy;
                    x1+=sx;
                }
                if(e2<=dx){
                    err+=dx;
                    y1+=sy;
                }
            }
        }
    }
    public void step(){
        for(CarAgent agent : agents){
            if(agent.isAtDestination()){
                handleArrival(agent);
            }
            agent.updatePosition();
        }
    }
    private void handleArrival(CarAgent agent){
        Point currentLocation = agent.getLocation();
        if(locationToCityId.containsKey(currentLocation)){
            int cityId = locationToCityId.get(currentLocation);
            agent.addCity(currentLocation);
            List<Integer> neighborCity = Graphs.neighborListOf(network,cityId);
            if(neighborCity.isEmpty()){
                return;
            }
            if(neighborCity.size() > 1 && agent.getPrevCity() != null){
                neighborCity.remove(agent.getPrevCity());
            }
            int destinationCity = neighborCity.get(random.nextInt(neighborCity.size()));
            Point newDestination = cityLocations.get(destinationCity);
            agent.setDestination(newDestination, cityId);
        }
    }
    /*
 checks if the agents have visited every city.
  */
    public boolean areAllVisited(){
        Set<Point> visited = new HashSet<>();
        for(CarAgent agent : agents){
            visited.addAll(agent.hasVisited());
        }
        return visited.size() == cityLocations.size();
    }
    /*
    Creates the agents and places them at random starting cities.
     */
    public void spawnAgents(int agentCount){
        List<Integer>cityIds = new ArrayList<>(network.vertexSet());
        for(int i = 0; i < agentCount; i++){
            int startCityId = cityIds.get(cityIds.size()-1);
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
