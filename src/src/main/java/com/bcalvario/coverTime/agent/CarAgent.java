package com.bcalvario.coverTime.agent;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CarAgent {
    private static int nextId = 0;
    private final int id;
    private Point location;
    private Point destination;
    private List<Point> currentPath;
    private final Set<Point> visitedCities;
    private Integer previousCity;
    private Color color;
    private boolean isCrashed;
    private int crashCooldown;

    public CarAgent(Point startLocation) {
        this.id = nextId++;
        this.location = new Point(startLocation);
        this.destination = new Point(startLocation);
        this.currentPath = new LinkedList<>();
        this.visitedCities = new HashSet<>();
        this.previousCity = null;
        this.color = Color.YELLOW; // Default color
        this.isCrashed = false;
        this.crashCooldown = 0;
    }

    public void updatePosition(GridCell[][] grid) {
        // If crashed, decrement cooldown and do not move.
        if (isCrashed) {
            crashCooldown--;
            if (crashCooldown <= 0) {
                isCrashed = false;
                this.color = Color.YELLOW; // Return to normal color
            }
            return;
        }

        if (currentPath != null && !currentPath.isEmpty()) {
            // Determine movement speed based on cell type
            int moves = 1;
            CellType currentCellType = grid[location.x][location.y].getType();
            if (currentCellType == CellType.FREEWAY) {
                moves = 3; // Move 3 steps on a freeway for faster travel
            }

            // Move the agent along the path
            for (int i = 0; i < moves && !currentPath.isEmpty(); i++) {
                this.location = currentPath.remove(0);
            }
        }
    }

    public void setPath(List<Point> path, Integer previousCity) {
        this.currentPath = new LinkedList<>(path);
        if (path != null && !path.isEmpty()) {
            this.destination = path.get(path.size() - 1);
        }
        this.previousCity = previousCity;
    }

    public void crash() {
        this.isCrashed = true;
        this.crashCooldown = 50; // Immobilized for 50 ticks
        this.color = Color.RED; // Change color to indicate a crash
    }

    public boolean isAtDestination() {
        return location.equals(destination);
    }

    public void visitCity(Point cityLocation) {
        visitedCities.add(cityLocation);
    }

    public Set<Point> hasVisited() {
        return visitedCities;
    }

    public Point getLocation() {
        return location;
    }

    public Integer getPrevCity() {
        return previousCity;
    }

    public Color getColor() {
        return color;
    }

    public boolean isCrashed() {
        return isCrashed;
    }
}