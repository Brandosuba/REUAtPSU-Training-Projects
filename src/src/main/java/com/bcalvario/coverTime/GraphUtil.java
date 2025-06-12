package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleGraph;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/*
Brandon Calvario
 */
public final class GraphUtil {
    private GraphUtil() {}
    public static List<Integer> cList(int n) {
        int complete = n * (n - 1) / 2;
        Set<Integer> set = new HashSet<>(Arrays.asList(n - 1,
                (int) Math.round(1.5 * n),
                2 * n,
                3 * n
        ));
        if (complete < 4 * n) set.add(complete);
        return set.stream().sorted().collect(Collectors.toList());
    }
    public static boolean isConnected(Graph<Integer, DefaultEdge> g, int n) {
        Set<Integer> seen = new HashSet<>();
        Deque<Integer> q = new ArrayDeque<>();
        seen.add(0); q.add(0);
        while (!q.isEmpty()) {
            int v = q.removeFirst();
            for (DefaultEdge e : g.edgesOf(v)) {
                int u = Graphs.getOppositeVertex(g, e, v);
                if (seen.add(u)) q.addLast(u);
            }
        }
        return seen.size() == n;
    }
    public static Graph<Integer, DefaultEdge> connectedRandomMultigraph(int n, int c, Random rng) {
        Graph<Integer, DefaultEdge> g = new Pseudograph<>(DefaultEdge.class);
        IntStream.range(0, n).forEach(g::addVertex);
        List<Integer> verts = IntStream.range(0, n).boxed()
                .collect(Collectors.toList());
        Collections.shuffle(verts, rng);
        for (int i = 1; i < n; i++) {
            int u = verts.get(i);
            int v = verts.get(rng.nextInt(i));
            g.addEdge(u, v);
        }
        while (g.edgeSet().size() < c) {
            int u = rng.nextInt(n);
            int v = rng.nextInt(n);
            g.addEdge(u, v);
        }
        return g;
    }
    public static Graph<Integer, DefaultEdge> connectedRandomSimpleGraph(int n, int c, Random rng) {
        Graph<Integer, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
        IntStream.range(0, n).forEach(g::addVertex);

        List<Integer> verts = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        Collections.shuffle(verts, rng);
        for (int i = 1; i < n; i++) {
            int u = verts.get(i);
            int v = verts.get(rng.nextInt(i));
            g.addEdge(u, v);
        }

        int maxEdges = n * (n - 1) / 2;
        if (c > maxEdges) {
            c = maxEdges;
        }
        while (g.edgeSet().size() < c) {
            int u = rng.nextInt(n);
            int v = rng.nextInt(n);
            if (u != v && !g.containsEdge(u, v)) {
                g.addEdge(u, v);
            }
        }
        return g;
    }
}