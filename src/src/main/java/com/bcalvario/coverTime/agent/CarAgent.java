package com.bcalvario.coverTime.agent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// Car driving on road network
// Has a current position & memory of visited cities
public class CarAgent {
    // Declare car agent properties
    private static int nextId = 0;
    private final int id;
    private Point location;
    private Point destination;
    private List<Point> currentPath;
    //    private Color color;
    private final Set<Point> visitedCities;
    private Integer previousCity;

    // Constructor for CarAgent object
    public CarAgent(int startX, int startY, Point startLocation, Set<Point> visitedCities, Integer previousCity) {
        this.id = nextId++;
        this.location = new Point(startLocation);
        this.destination = new Point(startLocation);
        this.visitedCities = new HashSet<>();
    }

    public CarAgent(Point startLocation) {
        this.id = nextId++;
        this.location = new Point(startLocation);
        this.destination = new Point(startLocation);
        this.currentPath = new LinkedList<>();
        this.visitedCities = new HashSet<>();
        this.previousCity = null;
    }// CarAgent

    // Update a car agent's position
    public void updatePosition() {
//        if (isAtDestination()) {
//            return;
//        }
//        int dx = Integer.compare(destination.x, location.x);
//        int dy = Integer.compare(destination.y, location.y);
//        location.translate(dx, dy);
        if(!currentPath.isEmpty()) {
            this.location = currentPath.remove(0);
        }
    }

    public void setDestination(Point newLocation, Integer currentCity) {
        this.destination = newLocation;
        this.previousCity = currentCity;
    }
    public void setPath(List<Point> path, Integer previousCities) {
        this.currentPath = new LinkedList<>(path);
        if(!path.isEmpty()) {
            this.destination = path.get(path.size() - 1);
        }
        this.previousCity = previousCities;
    }

    public boolean isAtDestination() {
        return location.equals(destination);
    }// updatePosition

    //     Add a visited city to a car agent's memory of visited cities
    public void visitCity(Point cityLocation) {
        visitedCities.add(cityLocation);
    } // addCity

    // Check if a car agent has visited a city
    public Set<Point> hasVisited() {
        return visitedCities;
    } // hasVisited

    public Point getLocation() {
        return location;
    }

    public Integer getPrevCity() {
        return previousCity;
    }

    public void setPrevCity(int cityId) {
        this.previousCity = cityId;
    }
} // CarAgent