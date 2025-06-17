package com.bcalvario.coverTime.agent;

import com.bcalvario.coverTime.Constants;
import com.bcalvario.coverTime.GraphUtil;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.SimpleGraph;

import javax.swing.*;
import java.util.Random;

public class AgentMain {
    public static void main(String[] args) {
        final int CITY_COUNT = 10;
        final int ROAD_COUNT = 15;
        final int NUM_AGENTS = 5;
        final int GRID_WIDTH = 50;
        final int GRID_HEIGHT = 50;

        Graph<Integer, DefaultEdge> cityNetwork = createCityNetwork(CITY_COUNT, ROAD_COUNT);
        Environment environment = new Environment(GRID_WIDTH, GRID_HEIGHT, NUM_AGENTS, cityNetwork);
        EnvironmentPanel environmentPanel = new EnvironmentPanel(environment);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("City Network Simulation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(environmentPanel);
            frame.pack(); // Sizes the window to fit the preferred size of the panel
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        });

        Timer timer = new Timer(100, e -> {
            if (!environment.areAllVisited()) {
                environment.step();
                environmentPanel.repaint();
            } else {
                ((Timer) e.getSource()).stop();
                System.out.println("All cities have been visited.");
                JOptionPane.showMessageDialog(environmentPanel, "All cities have been visited!", "Simulation Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        timer.start();
    }
//The Physical Representation (Environment.java)
//
//The rectangular appearance comes from how the abstract graph is drawn onto the grid.
//the generateCityLocations method takes the abstract cities and gives them physical (x, y) coordinates. Crucially,
//
//it picks these coordinates randomly within the bounds of the rectangular GridCell[][] grid.

    public static Graph<Integer, DefaultEdge> createCityNetwork(int cityCount, int roadCount) {
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Random rand = new Random();
//Create Nodes: A loop adds the specified number of cities (e.g., 10) to the graph.
// These are just logical entities like City 0, City 1, etc.
        for (int i = 0; i < cityCount; i++) {
            graph.addVertex(i);
        }
       //to ensure it's possible to travel between all cities, the code first creates a simple "spanning tree"
        // by connecting each city to the next one in a line (0-1, 1-2, 2-3, ...).
        // This guarantees there's at least one path from any city to any other.
        for (int i = 0; i < cityCount - 1; i++) {
            graph.addEdge(i, i + 1);
        }
        int maxEdges = cityCount * (cityCount - 1) / 2;
        int currentRoadCount = graph.edgeSet().size();
        int targetRoadCount = Math.min(roadCount, maxEdges);

        //Add Randomness: To make the network more interesting than a simple line,
        // a while loop adds the remaining "roads" by picking two random cities and connecting them,
        // as long as a road doesn't already exist there.
        while (currentRoadCount < targetRoadCount) {
            int u = rand.nextInt(cityCount);
            int v = rand.nextInt(cityCount);
            if (u != v && !graph.containsEdge(u, v)) {
                graph.addEdge(u, v);
                currentRoadCount++;
            }
        }
        return graph;
    }
}
