package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Random;

/**
 * A simple random walk strategy
 * At each step, the walker chooses a random neighbor to move to
 * this can result in backtracking.
 *
 * @author Brandon Calvario
 */
public class SimpleRandomWalk implements WalkStrategy {
    @Override
    public String getName() {
        return "Simple";
    }

    @Override
    public int coverTime(Graph<Integer, DefaultEdge> graph, Random rand) {
        // Get the number of nodes (vertices) in the graph.
        int n = graph.vertexSet().size();
        // Base case: If the graph is trivial (has 0 or 1 node), no steps are needed.
        if (n <= 1) {
            return 0;
        }
        // The walker starts at a randomly chosen node.
        int current = rand.nextInt(n);
        // A boolean array is used to efficiently track which nodes have been visited.
        // The index corresponds to the node's integer label.
        boolean[] seen = new boolean[n];
        seen[current] = true;
        int visited = 1;
        int steps = 0;
        // The loop continues until all nodes have been visited (visited == n).
        while (visited < n) {
            // Get the list of all neighbors of the current node.
            List<Integer> neighbors = Graphs.neighborListOf(graph, current);
            // Move to a new node by choosing a random neighbor. This is the core
            // of the "simple" random walk strategy.
            current = neighbors.get(rand.nextInt(neighbors.size()));
            // Check if the newly visited node is one we haven't seen before.
            if (!seen[current]) {
                seen[current] = true;
                visited++;
            }
            // Increment the step counter for every move made.
            steps++;
        }
        return steps; // Return the total steps taken to cover the graph.
    }
}

