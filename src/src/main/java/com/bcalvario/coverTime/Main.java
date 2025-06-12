package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.SwingUtilities;
import java.util.Random;
/*
Brandon Calvario
 */
public final class Main {

    public static void main(String[] args) {
        Random rng     = new Random();
        CoverTime ct   = new CoverTime();

        System.out.printf("%-4s %-4s %-6s %-8s%n", "N", "C","Runs", "avgCt");

        for (int n : Constants.N_VALUES) {
            for (int c : GraphUtil.cList(n)) {

                long total = 0;
                for (int run = 0; run < Constants.RUNS_PER_CONFIG; run++) {

                    Graph<Integer, DefaultEdge> g =
                            GraphUtil.connectedRandomMultigraph(n, c, rng);
                    total+= ct.coverTime(g);

                    if (run == 0 && Constants.SHOW_GRAPHS && n <= Constants.PREVIEW_MAX) {
                        String title = "N=" + n + "  C=" + c;
                        SwingUtilities.invokeLater(() ->
                                GraphPreview.show(g, title));
                    }
                }
                double avg = total / (double) Constants.RUNS_PER_CONFIG;
                System.out.printf("%-4d %-4d %-6d %-8.2f%n",
                        n, c, Constants.RUNS_PER_CONFIG, avg);
            }
        }
    }
}