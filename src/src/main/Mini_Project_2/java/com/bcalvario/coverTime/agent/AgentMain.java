package com.bcalvario.coverTime.agent;

import com.bcalvario.coverTime.Constants;
import com.bcalvario.coverTime.GraphUtil;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.util.Random;

public class AgentMain {
    public static void main(String[] args) {
        final int NODE_COUNT = 10;
        final int EDGE_COUNT = 15;
        final int NUM_AGENTS = 5;
        final int GRID_WIDTH = 100;
        final int GRID_HEIGHT = 100;

        Graph<Integer, DefaultEdge> graph = GraphUtil.connectedRandomSimpleGraph(NODE_COUNT, EDGE_COUNT, new Random());
        Environment environment = new Environment(GRID_WIDTH,GRID_HEIGHT,graph,NUM_AGENTS);
        EnvironmentPanel panel = new EnvironmentPanel(environment);

        JFrame frame = new JFrame("ABM Environment - Vehicles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Timer timer = new Timer(Constants.ANIMATION_DELAY, event -> {
            environment.step();
            panel.repaint();
            if(environment.allVisited()){
                ((Timer)event.getSource()).stop();
                System.out.println("All Cities have been visited.");
            }
        });
        timer.start();//
    }
}
