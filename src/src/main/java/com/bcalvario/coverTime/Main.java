package com.bcalvario.coverTime;

import com.mxgraph.analysis.mxGraphProperties;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 *@author Brandon Calvario
 *Main class for the random walker simulation
 * it activates the experiment by iterating through configurations of the following:
 * Network (graph) Size (N)
 * Connection count (C)
 * Walk Strategy simple vs. non-backtracking.
 **/
public final class Main {

    public static void main(String[] args) {
        Random rng     = new Random();
        List<WalkStrategy> strategies = List.of(
                new SimpleRandomWalk(),
                new NonBacktrackingRandomWalk()
        );
        //looping through each specified # of nodes
        System.out.printf("%-4s %-4s %-14s %-28s %-6s %-8s%n", "N", "C", "Graph Type", "Strategy", "Runs", "avgCt");
        System.out.println("--------------------------------------------------------------------------------");

        //looping through each specified # of nodes
        for (int n : Constants.N_VALUES) {
            for (int c : GraphUtil.cList(n)) {
                for (GraphType graphType : GraphType.values()) {
                    // Loop through the list of strategy objects.
                    for (WalkStrategy strategy : strategies) {
                        long totalCoverTime = 0;
                        for (int run = 0; run < Constants.RUNS_PER_CONFIG; run++) {
                            Graph<Integer, DefaultEdge> g;
                            if (graphType == GraphType.SIMPLE) {
                                g = GraphUtil.connectedRandomSimpleGraph(n, c, rng);
                            } else {
                                g = GraphUtil.connectedRandomMultigraph(n, c, rng);
                            }

                            // Show graph if enabled and within size limits, only on the first run.
                            if (run == 0 && Constants.SHOW_GRAPHS && n <= Constants.PREVIEW_MAX) {
                                String title = String.format("Type: %s, N=%d, C=%d", graphType, n, c);
                                GraphPreview.show(g, title);
                            }

                            // Call the coverTime method from the strategy object.
                            totalCoverTime += strategy.coverTime(g, rng);
                        }
                        double avg = totalCoverTime / (double) Constants.RUNS_PER_CONFIG;

                        System.out.printf("%-4d %-4d %-14s %-28s %-6d %-8.2f%n",
                                n, c, graphType.toString(), strategy.getName(), Constants.RUNS_PER_CONFIG, avg);
                    }
                }
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
    }
}