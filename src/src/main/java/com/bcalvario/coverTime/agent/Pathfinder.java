package com.bcalvario.coverTime.agent;

import java.awt.Point;
import java.util.*;

/**
 * A utility class that uses the A* algorithm to find paths on the grid.
 * Now includes separate methods for agents (who can't go through forests)
 * and for road generation (which prefers existing roads).
 */
public class Pathfinder {

    // Inner class to represent a node in the A* search
    private static class Node implements Comparable<Node> {
        Point point;
        Node parent;
        double gCost; // Cost from start
        double hCost; // Heuristic cost to end
        double fCost; // gCost + hCost

        Node(Point point, Node parent, double gCost, double hCost) {
            this.point = point;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }
    }

    /**
     * Finds the shortest path for an AGENT. Agents see forests as obstacles.
     */
    public static List<Point> findPath(Point start, Point end, GridCell[][] grid) {
        return findPath(start, end, grid, (p) -> grid[p.x][p.y].getType() == CellType.FOREST ? Double.POSITIVE_INFINITY : 1.0);
    }

    /**
     * Finds the best path for generating ROADS. Roads prefer to follow existing road cells.
     */
    public static List<Point> findPathForRoads(Point start, Point end, GridCell[][] grid) {
        return findPath(start, end, grid, (p) -> {
            CellType type = grid[p.x][p.y].getType();
            if (type == CellType.ROAD || type == CellType.CITY) {
                return 0.1; // Very low cost to encourage using existing roads
            }
            return 1.0; // Normal cost for traveling through forest
        });
    }

    /**
     * The core A* algorithm, now using a functional interface for cost calculation.
     */
    private static List<Point> findPath(Point start, Point end, GridCell[][] grid, CostFunction costFunction) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Point, Double> gCostMap = new HashMap<>();
        Map<Point, Node> allNodes = new HashMap<>();

        double startGCost = 0;
        double startHCost = start.distance(end);
        Node startNode = new Node(start, null, startGCost, startHCost);

        openSet.add(startNode);
        gCostMap.put(start, startGCost);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode.point.equals(end)) {
                return reconstructPath(currentNode);
            }

            for (Point neighborPoint : getNeighbors(currentNode.point, grid.length, grid[0].length)) {
                double cost = costFunction.getCost(neighborPoint);
                if (Double.isInfinite(cost)) continue;

                double tentativeGCost = currentNode.gCost + cost;

                if (tentativeGCost < gCostMap.getOrDefault(neighborPoint, Double.MAX_VALUE)) {
                    double hCost = neighborPoint.distance(end);
                    Node neighborNode = new Node(neighborPoint, currentNode, tentativeGCost, hCost);
                    gCostMap.put(neighborPoint, tentativeGCost);
                    openSet.add(neighborNode);
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    private static List<Point> reconstructPath(Node endNode) {
        List<Point> path = new ArrayList<>();
        Node current = endNode;
        while (current != null) {
            path.add(current.point);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static List<Point> getNeighbors(Point p, int width, int height) {
        List<Point> neighbors = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                int newX = p.x + dx;
                int newY = p.y + dy;
                if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                    neighbors.add(new Point(newX, newY));
                }
            }
        }
        return neighbors;
    }

    // Functional interface for calculating movement cost
    @FunctionalInterface
    interface CostFunction {
        double getCost(Point p);
    }
}
