package com.bcalvario.coverTime;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Random;

/**
 * defines the contract for any random walk algorithm
 */
public interface WalkStrategy {
    int coverTime(Graph<Integer, DefaultEdge> graph, Random rand);
    String getName();
}
