package com.bcalvario.coverTime.agent;

import java.awt.Point;
import java.util.*;

public class Pathfinder {

    private static class Node implements Comparable<Node> {
        Point point;
        Node parent;
        double gCost;
        double hCost;
        double fCost;

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
     * Finds the shortest path for an AGENT. Agents prefer freeways, then roads, and cannot enter forests.
     */
    public static List<Point> findPathForAgent(Point start, Point end, GridCell[][] grid) {
        return findPath(start, end, grid, p -> {
            CellType type = grid[p.x][p.y].getType();
            switch (type) {
                case FREEWAY: return 0.2; // Very low cost for freeways
                case ROAD:
                case CITY:    return 1.0; // Normal cost for roads and cities
                case FOREST:  return Double.POSITIVE_INFINITY; // Cannot pass through forests
                default:      return 1.0;
            }
        });
    }

    /**
     * Finds the best path for generating ROADS. Roads prefer to follow existing road/freeway cells.
     */
    public static List<Point> findPathForRoads(Point start, Point end, GridCell[][] grid) {
        return findPath(start, end, grid, p -> {
            CellType type = grid[p.x][p.y].getType();
            if (type == CellType.ROAD || type == CellType.FREEWAY || type == CellType.CITY) {
                return 0.1; // Very low cost to encourage using existing infrastructure
            }
            return 1.0; // Normal cost for traveling through forest
        });
    }

    private static List<Point> findPath(Point start, Point end, GridCell[][] grid, CostFunction costFunction) {
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<Point, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, start.distance(end));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode.point.equals(end)) {
                return reconstructPath(currentNode);
            }

            for (Point neighborPoint : getNeighbors(currentNode.point, grid.length, grid[0].length)) {
                double cost = costFunction.getCost(neighborPoint) * currentNode.point.distance(neighborPoint);
                if (Double.isInfinite(cost)) continue;

                double newGCost = currentNode.gCost + cost;

                Node neighborNode = allNodes.get(neighborPoint);
                if (neighborNode == null || newGCost < neighborNode.gCost) {
                    if (neighborNode != null) {
                        openSet.remove(neighborNode);
                    }
                    double hCost = neighborPoint.distance(end);
                    neighborNode = new Node(neighborPoint, currentNode, newGCost, hCost);
                    allNodes.put(neighborPoint, neighborNode);
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

    @FunctionalInterface
    interface CostFunction {
        double getCost(Point p);
    }
}
