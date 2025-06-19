import network as ntwrk
import generateGraph as generateG
import numpy as np
import matplotlib.pyplot as plt
"""
Brandon Calvario
6/18/2025
"""

def plot_mse(cost_history, epochs, title):
    """
    Plots the Mean Square Error (MSE) over epochs.
    - cost_history: A list of MSE values, one for each epoch.
    - epochs: The total number of epochs.
    - title: The title for the plot.
    """
    plt.figure()
    plt.plot(range(epochs), cost_history, marker='o', linestyle='-')
    plt.title(title)
    plt.xlabel('Epochs')
    plt.ylabel('Mean Square Error (MSE)')
    plt.grid(True)
    plt.show()


if __name__ == "__main__":
    # param for 4x4
    print("--- Starting experiment with 4x4 graphs ---")
    NUM_NODES_4 = 4
    INPUT_NEURONS_4 = NUM_NODES_4 * NUM_NODES_4
    OUTPUT_NEURONS = 2  # 2 output neurons: one for 'directed', one for 'undirected'

    # Architecture
    # The list defines the layers: [input_layer, hidden_layer_1, ..., output_layer]
    # To add more hidden layers: [INPUT_NEURONS_4, 30, 10, OUTPUT_NEURONS]
    # You can experiment with the number of neurons in the hidden layer (e.g., change 15).
    net_4 = ntwrk.Network([INPUT_NEURONS_4, 80, OUTPUT_NEURONS])

    # Data generation and formatting
    # We'll create a training set and a separate test set.
    training_data_raw_4 = generateG.create_dataset(15, NUM_NODES_4)
    test_data_raw_4 = generateG.create_dataset(50, NUM_NODES_4)
    # The input is the flattened adjacency matrix, and the output is a vectorized result.
    # e.g., directed (0) -> [1, 0]', undirected (1) -> [0, 1]'
    training_inputs_4 = [np.reshape(x, (INPUT_NEURONS_4, 1)) for x, y in training_data_raw_4]
    training_results_4 = [ntwrk.vectorize_result(y) for x, y in training_data_raw_4]
    training_data_4 = list(zip(training_inputs_4, training_results_4))
    test_inputs_4 = [np.reshape(x, (INPUT_NEURONS_4, 1)) for x, y in test_data_raw_4]
    test_data_4 = list(zip(test_inputs_4, [y for x, y in test_data_raw_4]))
    # Training
    # Hyperparameters to experiment with:
    # - epochs: How many times to loop through the entire training dataset.
    # - mini_batch_size: The number of samples to process before updating weights.
    # - eta (learning rate): How big the steps are during gradient descent.
    epochs_4 = 15
    cost_history_4 = net_4.SGD(training_data_4, epochs=epochs_4, mini_batch_size=10, eta=2.5, test_data=test_data_4)
    plot_mse(cost_history_4, epochs_4, 'MSE vs. Epochs (4x4 Graphs)')


    # param for 10x10 graphs

    print("\n--- Starting experiment with 10x10 graphs ---")
    NUM_NODES_10 = 10
    INPUT_NEURONS_10 = NUM_NODES_10 * NUM_NODES_10
    net_10 = ntwrk.Network([INPUT_NEURONS_10, 15, OUTPUT_NEURONS])
    # data generation
    training_data_raw_10 = generateG.create_dataset(35, NUM_NODES_10)
    test_data_raw_10 = generateG.create_dataset(50, NUM_NODES_10)
    training_inputs_10 = [np.reshape(x, (INPUT_NEURONS_10, 1)) for x, y in training_data_raw_10]
    training_results_10 = [ntwrk.vectorize_result(y) for x, y in training_data_raw_10]
    training_data_10 = list(zip(training_inputs_10, training_results_10))
    test_inputs_10 = [np.reshape(x, (INPUT_NEURONS_10, 1)) for x, y in test_data_raw_10]
    test_data_10 = list(zip(test_inputs_10, [y for x, y in test_data_raw_10]))
    epochs_10 = 35
    cost_history_10 = net_10.SGD(training_data_10, epochs=epochs_10, mini_batch_size=10, eta=3.0,
                                 test_data=test_data_10)
    plot_mse(cost_history_10, epochs_10, 'MSE vs. Epochs (10x10 Graphs)')
