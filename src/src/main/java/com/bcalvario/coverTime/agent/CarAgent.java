package com.bcalvario.coverTime.agent;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

// Car driving on road network
// Has a current position & memory of visited cities
public class CarAgent {
    // Declare car agent properties
    private int x;
    private int y;
    private Point location;
    private Point destination;
//    private Color color;
    private Set<Point> visitedCities;
    private Integer previousCity;

    // Constructor for CarAgent object
    public CarAgent(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.visitedCities = new HashSet<>();
    } // CarAgent

    // Update a car agent's position
    public void updatePosition() {
      if(isAtDestination()){
          return;
      }
      int dx = Integer.compare(destination.x, location.x);
      int dy = Integer.compare(destination.y, location.y);
      location.translate(dx, dy);
    }
    public void setDestination(Point destination, Integer currentCityId){
        this.destination= destination;
        this.previousCity = currentCityId;
    }
    public boolean isAtDestination(){
        return location.equals(destination);
    }// updatePosition

//     Add a visited city to a car agent's memory of visited cities
    public void addCity (Point cityLocation) {
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
} // CarAgent