"""
Main.py
---------------------------------
An improved experiment runner for the graph classification project.

Key Features:
- Fulfills all original project criteria.
- Automatically tests multiple network architectures and hyperparameters.
- Reports best AND worst performing configurations for comparison.
- Can use "perfect" or "noisy" datasets.
- Evaluates performance using accuracy and a confusion matrix.
- Plots learning curves for the best and worst performing networks.
- Visualizes the "average" directed and undirected graphs as heatmaps.
"""

import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

# Make sure you have the updated network.py from the student experiment
# Or ensure the original vectorize_result takes num_classes
import network as ntwrk
import generateGraph as generateG

def plot_curve(values, title, ylabel):
    """Utility function to plot learning curves."""
    plt.figure(figsize=(8, 5))
    plt.plot(range(len(values)), values, marker='.', linestyle='-')
    plt.xlabel("Epoch")
    plt.ylabel(ylabel)
    plt.title(title)
    plt.grid(True)
    plt.show()


def get_predictions(net, data):
    """Get network predictions for a given dataset."""
    return [np.argmax(net.feedforward(x)) for x, y in data]

def calculate_confusion_matrix(predictions, labels):
    """Calculates and prints a confusion matrix."""
    matrix = defaultdict(int)
    for pred, actual in zip(predictions, labels):
        matrix[(pred, actual)] += 1

    print("\nConfusion Matrix for Best Network (Prediction, Actual):")
    print("-------------------------------------------------------")
    # Label 0: Directed, Label 1: Undirected
    print(f"Predicted Directed,   Actual Directed:   {matrix[(0, 0)]}")
    print(f"Predicted Undirected, Actual Directed:   {matrix[(1, 0)]}")
    print(f"Predicted Directed,   Actual Undirected: {matrix[(0, 1)]}")
    print(f"Predicted Undirected, Actual Undirected: {matrix[(1, 1)]}")
    print("-------------------------------------------------------")


def run_hyperparameter_search(num_nodes, use_noisy_data=False):
    """
    Trains and evaluates multiple network configurations to find the best one.
    This directly addresses the criteria to "play with the network parameters".
    """
    print(f"--- Starting Search for {num_nodes}x{num_nodes} graphs (Noisy Data: {use_noisy_data}) ---")

    # --- 1. Create a stable dataset for the search ---
    n_inputs = num_nodes ** 2
    train_set = generateG.create_dataset(num_nodes, per_class=500, noisy=use_noisy_data)
    test_set = generateG.create_dataset(num_nodes, per_class=200, noisy=use_noisy_data)

    train_data = [(x, ntwrk.vectorize_result(y, 2)) for x, y in train_set]
    test_data = [(x, y) for x, y in test_set]
    test_labels = [y for x, y in test_data]

    # --- 2. Define search space ---
    hidden_layer_configs = [[10], [30], [15, 5]]
    learning_rates = [0.1, 0.5, 1.0]
    epochs = 15

    best_accuracy = 0
    worst_accuracy = 1.0
    best_config, worst_config = {}, {}
    best_history, worst_history, best_net = None, None, None

    # --- 3. Run the search loop ---
    for hidden_layers in hidden_layer_configs:
        for eta in learning_rates:
            print(f"\nTesting Config: Hidden={hidden_layers}, LR={eta}")

            net = ntwrk.Network([n_inputs] + hidden_layers + [2])

            cost_hist, acc_hist = net.SGD(
                train_data, epochs, mini_batch_size=10, eta=eta, test_data=test_data
            )

            final_accuracy = acc_hist[-1]
            current_config = {'hidden': hidden_layers, 'eta': eta, 'epochs': epochs}

            if final_accuracy > best_accuracy:
                best_accuracy = final_accuracy
                best_config = current_config
                best_history = (cost_hist, acc_hist)
                best_net = net

            if final_accuracy < worst_accuracy:
                worst_accuracy = final_accuracy
                worst_config = current_config
                worst_history = (cost_hist, acc_hist)


    # --- 4. Report the results ---
    print("\n=======================================================")
    print(f"Search Complete for {num_nodes}x{num_nodes} graphs:")
    print("\n--- Best Configuration ---")
    print(f"  - Architecture: {[n_inputs] + best_config['hidden'] + [2]}")
    print(f"  - Learning Rate: {best_config['eta']}")
    print(f"  - Final Accuracy: {best_accuracy:.3f}")

    print("\n--- Worst Configuration ---")
    print(f"  - Architecture: {[n_inputs] + worst_config['hidden'] + [2]}")
    print(f"  - Learning Rate: {worst_config['eta']}")
    print(f"  - Final Accuracy: {worst_accuracy:.3f}")
    print("=======================================================")

    # Analyze the best network with a confusion matrix
    predictions = get_predictions(best_net, test_data)
    calculate_confusion_matrix(predictions, test_labels)

    # Plot the learning curves for the best network
    plot_curve(best_history[0], f"Best MSE ({num_nodes}x{num_nodes})", "MSE")
    plot_curve(best_history[1], f"Best Accuracy ({num_nodes}x{num_nodes})", "Accuracy")

    # Plot the learning curves for the worst network
    if worst_history:
        plot_curve(worst_history[0], f"Worst MSE ({num_nodes}x{num_nodes})", "MSE")
        plot_curve(worst_history[1], f"Worst Accuracy ({num_nodes}x{num_nodes})", "Accuracy")

    # --- 5. Visualize the average graphs ---
    directed_graphs = [x for x, y in test_set if y == 0]
    undirected_graphs = [x for x, y in test_set if y == 1]

    # NEW WAY (what you are replacing it with)
    generateG.plot_average_graph(directed_graphs, num_nodes, f"Average Directed Graph ({num_nodes}x{num_nodes})")
    generateG.plot_average_graph(undirected_graphs, num_nodes, f"Average Undirected Graph ({num_nodes}x{num_nodes})")

if __name__ == "__main__":
    # Experiment 1: Small graphs, as per criteria
    run_hyperparameter_search(num_nodes=4, use_noisy_data=False)

    # Experiment 2: Larger graphs, as per "if you have time" criteria
    run_hyperparameter_search(num_nodes=10, use_noisy_data=False)

    # Experiment 3: More challenging noisy data (optional)
    # run_hyperparameter_search(num_nodes=10, use_noisy_data=True)