package com.bcalvario.coverTime.agent;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Environment {
//    private final GridCell[][] grid;
//    private final List<CarAgent> agents;
//    private final Map<Integer, Point> cityLocations;

    public Environment(int width, int height, Graph<Integer, DefaultEdge>graph, int numAgents) {
//        this.grid = new GridCell[width][height];
//        for(int x = 0; x < width; x++) {
//            for(int y = 0; y < height; y++) {
//                this.grid[x][y] = new GridCell(CellType.FOREST);
//            }
//        }
//        this.agents = new ArrayList<>();
//        List<Point> startingCities = new ArrayList<>(cityLocations.values());
//        for(int x = 0; x < numAgents; x++) {
//            Point startPos = startingCities.get(x % startingCities.size());
//            this.agents.add(new CarAgent(startPos));
//        }
//        this.cityLocations = generateCityLocations(width,height,graph.vertexSet().size());
//        cityLocations.forEach((cityId,point)->{ grid[point.x][point.y] = new GridCell(CellType.CITY)})
    }
    private Map<Integer,Point> generateCityLocations(int width, int height, int numCities){
        return Map.of();
    }
//    public boolean allVisited(){
//        Set<Point> visited = new HashSet<>();
//
//    }
    public void step(){
    }
}
