package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Random;

/**
 * Main class for the random walker simulation.
 * This version is modified to test and compare two specific graph instances:
 * a Simple Graph and a Multigraph of the same size and connection count.
 *
 * @author Brandon Calvario
 */
public final class Main {

    public static void main(String[] args) {
        // --- Define the parameters for the two specific graphs to be tested ---
        final int NODE_COUNT = 15;
        final int EDGE_COUNT = 25;

        Random rng = new Random();
        List<WalkStrategy> strategies = List.of(
                new SimpleRandomWalk(),
                new NonBacktrackingRandomWalk()
        );

        // --- Print Header for Statistical Results ---
        System.out.println("Comparing two specific graphs: N=" + NODE_COUNT + ", C=" + EDGE_COUNT);
        System.out.printf("%-14s %-28s %-12s%n", "Graph Type", "Strategy", "AvgCoverTime");
        System.out.println("----------------------------------------------------------");

        // --- Test 1: Simple Graph ---
        Graph<Integer, DefaultEdge> simpleGraph = GraphUtil.connectedRandomSimpleGraph(NODE_COUNT, EDGE_COUNT, rng);
        for (WalkStrategy strategy : strategies) {
            long totalCoverTime = 0;
            for (int run = 0; run < Constants.RUNS_PER_CONFIG; run++) {
                // To be statistically fair, we can regenerate the graph for each run
                Graph<Integer, DefaultEdge> g = GraphUtil.connectedRandomSimpleGraph(NODE_COUNT, EDGE_COUNT, rng);
                totalCoverTime += strategy.coverTime(g, rng);
            }
            double avgCoverTime = totalCoverTime / (double) Constants.RUNS_PER_CONFIG;
            System.out.printf("%-14s %-28s %-12.2f%n",
                    "Simple Graph", strategy.getName(), avgCoverTime);
        }

        // --- Test 2: Multigraph ---
        Graph<Integer, DefaultEdge> multiGraph = GraphUtil.connectedRandomMultigraph(NODE_COUNT, EDGE_COUNT, rng);
        for (WalkStrategy strategy : strategies) {
            long totalCoverTime = 0;
            for (int run = 0; run < Constants.RUNS_PER_CONFIG; run++) {
                // Regenerate for each run
                Graph<Integer, DefaultEdge> g = GraphUtil.connectedRandomMultigraph(NODE_COUNT, EDGE_COUNT, rng);
                totalCoverTime += strategy.coverTime(g, rng);
            }
            double avgCoverTime = totalCoverTime / (double) Constants.RUNS_PER_CONFIG;
            System.out.printf("%-14s %-28s %-12.2f%n",
                    "Multigraph", strategy.getName(), avgCoverTime);
        }

        System.out.println("----------------------------------------------------------");

        // --- Side-by-Side Visualization ---
        // We will visualize the first instance of each graph type that we generated.
        if (Constants.SHOW_GRAPHS) {
            // We'll use the more efficient NonBacktracking walker for the animation
            WalkStrategy visualStrategy = new NonBacktrackingRandomWalk();

            String title1 = String.format("Simple Graph (N=%d, C=%d)", NODE_COUNT, EDGE_COUNT);
            String title2 = String.format("Multigraph (N=%d, C=%d)", NODE_COUNT, EDGE_COUNT);

            // Call the side-by-side animation method from GraphPreview
            GraphPreview.animateTwoWalksSideBySide(simpleGraph, visualStrategy, title1, multiGraph, visualStrategy, title2);
        }
    }
}