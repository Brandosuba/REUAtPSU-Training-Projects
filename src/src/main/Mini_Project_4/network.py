"""
network.py
----------
This module provides a class-based implementation for a standard, feedforward
neural network.

WHAT IT DOES:
- Implements the Stochastic Gradient Descent (SGD) learning algorithm.
- Uses the backpropagation algorithm to efficiently calculate the gradient of
  the cost function, which tells the network how to adjust its weights and biases.
- Can be used for multi-class classification problems.

Brandon Calvario
6/19/2025
"""

import random
import numpy as np

#Activation
"""
The sigmoid activation function.
It squashes any input value to a number between 0 and 1. This is useful
for turning numbers into probabilities.
"""
def sigmoid(z):
    return 1.0 / (1.0 + np.exp(-z))

"""
The derivative of the sigmoid function.
This is needed during backpropagation to calculate how much a change in a
neuron's weighted input affects its output.
"""
def sigmoid_prime(z):
    s = sigmoid(z)
    return s * (1 - s)
#Data Formatting

def vectorize_result(j: int, num_classes: int) -> np.ndarray:
    e = np.zeros((num_classes, 1))
    e[j] = 1.0
    return e


#Network class
"""
Holds a list containing the number of neurons in each layer.
For example, [16, 10, 2] would create a network with 16 input neurons, one hidden layer of 10 neurons, and an output layer of 2 neurons.
"""
class Network:
    def __init__(self, sizes):
        self.num_layers = len(sizes)
        self.sizes = sizes
        # Biases and weights are initialized randomly from a standard normal distribution.
        # Biases are not needed for the input layer, so we start from the second layer.
        self.biases = [np.random.randn(y, 1) for y in sizes[1:]]
        self.weights = [np.random.randn(y, x) for x, y in zip(sizes[:-1], sizes[1:])]

    """
    Calculates the output of the network for a given input 'a'.
    It passes the input through each layer, applying the sigmoid function
    at each step.
    """
    def feedforward(self, a):
        for b, w in zip(self.biases, self.weights):
            a = sigmoid(np.dot(w, a) + b)
        return a

    def SGD(self, training_data, epochs, mini_batch_size, eta, test_data=None):
        training_data = list(training_data)
        n = len(training_data)

        cost_history, acc_history = [], []
        if test_data:
            test_data = list(test_data)
            n_test = len(test_data)

        for j in range(epochs):
            # Shuffle the training data at the start of each epoch
            random.shuffle(training_data)
            # Partition the data into mini-batches
            mini_batches = [
                training_data[k:k + mini_batch_size]
                for k in range(0, n, mini_batch_size)
            ]
            # Update the network for each mini-batch
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)

            # After each epoch, calculate and record metrics
            epoch_cost = self.total_cost(training_data)
            cost_history.append(epoch_cost)

            if test_data:
                acc = self.evaluate(test_data) / n_test
                acc_history.append(acc)
                print(f"Epoch {j:>3}:  cost={epoch_cost:.4f}  acc={acc:.3f}")
            else:
                print(f"Epoch {j:>3}:  cost={epoch_cost:.4f}")

        return cost_history, acc_history

    """
    Updates the network's weights and biases by applying gradient descent
    using backpropagation to a single mini-batch.
    """
    def update_mini_batch(self, mini_batch, eta):
        # Create zero-filled arrays to store the gradients for biases and weights
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]

        # For each sample in the mini-batch, calculate the gradient and add it to the total
        for x, y in mini_batch:
            delta_nabla_b, delta_nabla_w = self.backprop(x, y)
            nabla_b = [nb + dnb for nb, dnb in zip(nabla_b, delta_nabla_b)]
            nabla_w = [nw + dnw for nw, dnw in zip(nabla_w, delta_nabla_w)]

        # Update the weights and biases based on the average gradient over the mini-batch
        m = len(mini_batch)
        self.weights = [w - (eta / m) * nw for w, nw in zip(self.weights, nabla_w)]
        self.biases = [b - (eta / m) * nb for b, nb in zip(self.biases, nabla_b)]

    """
    The core of the learning algorithm. It calculates the gradient of the
    cost function with respect to the network's weights and biases for a
    single training example (x, y).
    Returns a tuple (nabla_b, nabla_w) representing the gradient for the cost function.
    """
    def backprop(self, x, y):
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]

        # 1. Feedforward: Pass the input through the network, storing activations
        #    and z-vectors (pre-activation values) for each layer.
        activation = x
        activations = [x]
        zs = []
        for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)

        # 2. Backward Pass: Propagate the error backward from the output layer.
        # Calculate the error (delta) for the output layer
        delta = self.cost_derivative(activations[-1], y) * sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].T)

        # Propagate the error to previous layers
        for l in range(2, self.num_layers):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l + 1].T, delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l - 1].T)

        return (nabla_b, nabla_w)

    """
     Measures the network's accuracy on the test data.
     It counts how many inputs are correctly classified. The "correct" output
     is the neuron in the final layer with the highest activation.
     """
    def evaluate(self, test_data):
        test_results = [(np.argmax(self.feedforward(x)), y) for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)
    """
    Calculates the total cost (mean squared error) for a given dataset.
    """
    def total_cost(self, data):
        cost = 0.0
        for x, y in data:
            a = self.feedforward(x)
            cost += 0.5 * np.linalg.norm(a - y)**2
        return cost / len(data)

    """
    Calculates the derivative of the cost function with respect to the output activations.
    For mean squared error, this is simply (a - y).
    """
    def cost_derivative(self, output_activations, y):

        return (output_activations - y)

