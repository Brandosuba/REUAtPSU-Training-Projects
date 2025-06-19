import random
import numpy as np
"""
Brandon Calvario
6/18/2025
"""

def generate_graph(nodes, directed):
    """
    Generates an adjacency matrix for a graph.
    - nodes: number of nodes in the graph
    - directed: boolean, True for directed, False for undirected
    """
    matrix = np.zeros((nodes, nodes), dtype=int)
    if directed:
        for i in range(nodes):
            for j in range(nodes):
                if i != j:
                    matrix[i, j] = random.choice([0, 1])
    else:  # Undirected
        for i in range(nodes):
            for j in range(i, nodes):
                if i == j:
                    matrix[i, j] = 0
                else:
                    val = random.choice([0, 1])
                    matrix[i, j] = val
                    matrix[j, i] = val
    return matrix

def create_dataset(num_samples, nodes):
    """
    Creates a dataset of graphs.
    - num_samples: total number of graphs to generate
    - nodes: number of nodes in each graph
    """
    data = []
    for i in range(num_samples // 2):
        # Directed graph, label 0
        adj_matrix_dir = generate_graph(nodes, True)
        input_vector_dir = adj_matrix_dir.reshape(nodes * nodes, 1)
        data.append((input_vector_dir, 0))

        # Undirected graph, label 1
        adj_matrix_undir = generate_graph(nodes, False)
        input_vector_undir = adj_matrix_undir.reshape(nodes * nodes, 1)
        data.append((input_vector_undir, 1))

    random.shuffle(data)
    return data