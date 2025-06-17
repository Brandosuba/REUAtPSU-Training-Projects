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

    public static Graph<Integer, DefaultEdge> createCityNetwork(int cityCount, int roadCount) {
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Random rand = new Random();
        for (int i = 0; i < cityCount; i++) {
            graph.addVertex(i);
        }
        for (int i = 0; i < cityCount - 1; i++) {
            graph.addEdge(i, i + 1);
        }
        int maxEdges = cityCount * (cityCount - 1) / 2;
        int currentRoadCount = graph.edgeSet().size();
        int targetRoadCount = Math.min(roadCount, maxEdges);
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
