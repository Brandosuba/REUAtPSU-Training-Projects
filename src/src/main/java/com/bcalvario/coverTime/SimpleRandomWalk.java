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
 * @author Brandon Calvario
 */
public class SimpleRandomWalk implements WalkStrategy {
    @Override
    public String getName() {
        return "Simple";
    }
    @Override
    public int coverTime(Graph<Integer, DefaultEdge> graph, Random rand) {
        int n = graph.vertexSet().size();
        if(n<=1){
            return 0;
        }
        int current = rand.nextInt(n);
        boolean[]seen = new boolean[n];
        seen[current] = true;
        int visited = 1, steps = 0;
        while(visited<n){
            List<Integer> neighbors = Graphs.neighborListOf(graph, current);
            current = neighbors.get(rand.nextInt(neighbors.size()));

            if(!seen[current]){
                seen[current] = true;
                visited++;
            }
            steps++;
        }
        return steps;
    }
}
