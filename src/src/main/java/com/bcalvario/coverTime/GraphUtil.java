package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility class for graph-related operations.
 * provides the algorithms (rules) to procedurally
 * generate graphs with specific properties (N nodes, C edges, connectivity).
 *
 * @author Brandon Calvario
 */
public final class GraphUtil {
    private GraphUtil() {
    }

    public static List<Integer> cList(int n) {
        int complete = n * (n - 1) / 2;
        Set<Integer> set = new HashSet<>(Arrays.asList(n - 1, (int) Math.round(1.5 * n), 2 * n, 3 * n));
        if (complete < 4 * n) {
            set.add(complete);
        }
        return set.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Checks if a graph is connected using a Breadth-First Search (BFS) algorithm.
     * As discussed in the presentation, a graph must be connected for the cover time
     * to be finite. This method validates that prerequisite.
     */
    public static boolean isConnected(Graph<Integer, DefaultEdge> g, int n) {
        Set<Integer> seen = new HashSet<>();
        Deque<Integer> q = new ArrayDeque<>();
        // Start the BFS from an arbitrary node (e.g., node 0).
        seen.add(0);
        q.add(0);
        while (!q.isEmpty()) {
            int v = q.removeFirst();
            // Visit all neighbors of the current node.
            for (DefaultEdge e : g.edgesOf(v)) {
                int u = Graphs.getOppositeVertex(g, e, v);
                if (seen.add(u)) { // If 'u' has not been seen before
                    q.addLast(u); // Add it to the queue to visit later.
                }
            }
        }
        // If the number of nodes visited equals the total number of nodes, the graph is connected.
        return seen.size() == n;
    }

    /**
     * Generates a connected random multigraph (pseudograph)
     * allows for multiple edges between the same two nodes and self-loops.
     * The algorithm first creates a spanning tree to guarantee connectivity, then adds
     * the remaining edges randomly.
     *
     * @param n   Number of nodes (vertices).
     * @param c   Number of connections (edges).
     * @param rng Random number generator.
     * @return A connected random multigraph.
     */
    public static Graph<Integer, DefaultEdge> connectedRandomMultigraph(int n, int c, Random rng) {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        IntStream.range(0, n).forEach(g::addVertex);

        // create a spanning tree to ensure connectivity (N-1 edges)
        // This is a common algorithm for generating connected random graphs.
        List<Integer> verts = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(verts, rng); // Randomize vertex order to ensure randomness.
        for (int i = 1; i < n; i++) {
            int u = verts.get(i);
            int v = verts.get(rng.nextInt(i)); // Connect to a random, previously-added vertex.
            g.addEdge(u, v);
        }

        // Add the remaining edges randomly
        while (g.edgeSet().size() < c) {
            int u = rng.nextInt(n);
            int v = rng.nextInt(n);
            g.addEdge(u, v); // Self-loops (u==v) are allowed in a Pseudograph.
        }
        return g;
    }

    /**
     * Generates a connected random simple graph.
     * A simple graph has no self-loops or multiple edges between the same two nodes.
     * The process is similar to multigraph generation but with added constraints.
     *
     * @param n   Number of nodes.
     * @param c   Number of connections (edges).
     * @param rng Random number generator.
     * @return A connected random simple graph.
     */
    public static Graph<Integer, DefaultEdge> connectedRandomSimpleGraph(int n, int c, Random rng) {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        IntStream.range(0, n).forEach(g::addVertex);

        // Create a spanning tree
        List<Integer> verts = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        Collections.shuffle(verts, rng);
        for (int i = 1; i < n; i++) {
            int u = verts.get(i);
            int v = verts.get(rng.nextInt(i));
            g.addEdge(u, v);
        }
        // Cap the number of edges at the maximum possible for a simple graph.
        int maxEdges = n * (n - 1) / 2;
        if (c > maxEdges) {
            c = maxEdges;
        }
        // Add remaining edges randomly, avoiding self-loops and duplicates ---
        while (g.edgeSet().size() < c) {
            int u = rng.nextInt(n);
            int v = rng.nextInt(n);
            // The additional constraints for a simple graph:
            // 1. No self-loop (u != v).
            // 2. No existing edge (!g.containsEdge(u, v)).
            if (u != v && !g.containsEdge(u, v)) {
                g.addEdge(u, v);
            }
        }
        return g;
    }
}
