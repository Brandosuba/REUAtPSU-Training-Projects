"""
generateGraph.py
----------------
Generates and visualizes adjacency matrices for graphs.

- Includes functions for creating "perfect" and "noisy" datasets.
- Includes a function to plot the "average" of a set of graphs as a heatmap.
"""
import random
import numpy as np
import matplotlib.pyplot as plt


def generate_graph(nodes: int, directed: bool) -> np.ndarray:
    """Generates a single, perfect adjacency matrix."""
    matrix = np.zeros((nodes, nodes), dtype=int)

    if directed:
        while True:
            for i in range(nodes):
                for j in range(nodes):
                    if i != j:
                        matrix[i, j] = random.choice([0, 1])
            if not np.array_equal(matrix, matrix.T):
                break
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

"""
Generates a single 'noisy' or 'imperfect' adjacency matrix to create a
more realistic and challenging classification problem.
"""
def generate_noisy_graph(nodes: int, directed: bool) -> np.ndarray:
    if directed:
        matrix = generate_graph(nodes, directed=True)
        for i in range(nodes):
            for j in range(nodes):
                if matrix[i, j] == 1 and random.random() < 0.4:
                    matrix[j, i] = 1
        return matrix
    else:
        matrix = generate_graph(nodes, directed=False)
        for _ in range(2):
            r, c = random.randint(0, nodes - 1), random.randint(0, nodes - 1)
            if r != c:
                matrix[r, c] = 1 - matrix[r, c]
        return matrix


def create_dataset(nodes: int, per_class: int, noisy: bool = False):
    data = []
    generator = generate_noisy_graph if noisy else generate_graph

    for _ in range(per_class):
        data.append((generator(nodes, True).reshape(nodes ** 2, 1), 0))
        data.append((generator(nodes, False).reshape(nodes ** 2, 1), 1))

    random.shuffle(data)
    return data

"""
Calculates and plots the average of a set of graph matrices as a heatmap.
This shows the probability of a connection at each position.
"""
def plot_average_graph(graphs, nodes, title):
    if not graphs:
        print(f"No graphs to average for '{title}'")
        return

    avg_matrix = sum(graphs).reshape(nodes, nodes) / len(graphs)

    plt.figure(figsize=(6, 6))
    plt.imshow(avg_matrix, cmap='viridis', interpolation='nearest')
    plt.colorbar(label='Connection Probability')
    plt.title(title)
    plt.xlabel("Node")
    plt.ylabel("Node")
    plt.show()