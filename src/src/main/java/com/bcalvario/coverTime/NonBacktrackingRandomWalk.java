package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;
import java.util.Random;

/**
 *
 */

public class NonBacktrackingRandomWalk implements WalkStrategy {
    @Override
    public String getName(){
        return "Non-Backtracking";
    }
    @Override
    public int coverTime(Graph<Integer, DefaultEdge> graph, Random rand){
        int n = graph.vertexSet().size();
        if(n <= 1){
            return 0;
        }
        int current = rand.nextInt(n);
        int previous = -1;
        boolean[]seen = new boolean[n];
        seen[current] = true;
        int visited = 1, steps = 0;

        while(visited < n){
            List<Integer> neighbors = Graphs.neighborListOf(graph,current);
            if(neighbors.size() > 1 && previous != -1){
                neighbors.remove(Integer.valueOf(previous));
            }
            previous = current;
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
