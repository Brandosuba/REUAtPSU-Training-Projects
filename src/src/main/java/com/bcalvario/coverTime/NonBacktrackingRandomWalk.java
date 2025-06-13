package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Random;

/**
 * Implements a non-backtracking random walk strategy.
 * At each step, the walker moves to a randomly chosen neighbor, but it is forbidden
 * from immediately returning to the node it just came from. This introduces a
 * one-step memory to the walk, making it a more efficient exploration strategy
 * compared to a simple random walk, as it avoids wasting steps.
 *
 * @author Brandon Calvario
 */
public class NonBacktrackingRandomWalk implements WalkStrategy {
    @Override
    public String getName() {
        return "Non-Backtracking";
    }

    @Override
    public int coverTime(Graph<Integer, DefaultEdge> graph, Random rand) {
        int n = graph.vertexSet().size();
        // If the graph is trivial, the cover time is 0.
        if (n <= 1) {
            return 0;
        }

        // Start at a random node.
        int current = rand.nextInt(n);
        // 'previous' stores the last visited node to prevent backtracking. Initialized to -1.
        int previous = -1;
        boolean[] seen = new boolean[n];
        seen[current] = true;
        int visited = 1, steps = 0;

        while (visited < n) {
            // Get the list of neighbors of the current node.
            List<Integer> neighbors = Graphs.neighborListOf(graph, current);
            // If there's more than one neighbor and we're not at the starting step,
            // remove the previous node from the list of possible next steps.
            if (neighbors.size() > 1 && previous != -1) {
                neighbors.remove(Integer.valueOf(previous));
            }

            // Update the previous node before moving to the next.
            previous = current;
            // Choose the next node randomly from the (potentially modified) neighbor list.
            current = neighbors.get(rand.nextInt(neighbors.size()));

            // If the new node is unvisited, mark it as seen.
            if (!seen[current]) {
                seen[current] = true;
                visited++;
            }
            steps++;
        }
        return steps;
    }
}

