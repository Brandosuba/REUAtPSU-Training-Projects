import random
import numpy as np
"""
Brandon Calvario
6/18/2025
"""
def sigmoid(z):
    #The sigmoid function.
    return 1.0 / (1.0 + np.exp(-z))


def sigmoid_prime(z):
    #Derivative of the sigmoid function.
    return sigmoid(z) * (1 - sigmoid(z))


def vectorize_result(j):
    """Return a 2-dimensional unit vector with a 1.0 in the jth
    position and zeroes elsewhere. This is used to convert a label (0 for
    directed, 1 for undirected) into a corresponding desired output from
    the neural network."""
    e = np.zeros((2, 1))
    e[j] = 1.0
    return e

#Main Network class
class Network(object):

    def __init__(self, sizes):
        """
        The list ``sizes`` contains the number of neurons in the respective
        layers of the network. For example, if the list was [16, 15, 2],
        it would be a three-layer network, with the first layer containing
        16 neurons, the second layer 15 neurons, and the third layer 2
        neurons. The biases and weights for the network are initialized
        randomly, using a Gaussian distribution with mean 0, and variance 1.
        """
        self.num_layers = len(sizes)
        self.sizes = sizes
        self.biases = [np.random.randn(y, 1) for y in sizes[1:]]
        self.weights = [np.random.randn(y, x)
                        for x, y in zip(sizes[:-1], sizes[1:])]

    def feedforward(self, a):
        """Return the output of the network if ``a`` is input."""
        for b, w in zip(self.biases, self.weights):
            a = sigmoid(np.dot(w, a) + b)
        return a

    def SGD(self, training_data, epochs, mini_batch_size, eta,
            test_data=None):
        """
        Train the neural network using mini-batch stochastic gradient descent.
        The ``training_data`` is a list of tuples ``(x, y)`` representing
        the training inputs and the desired outputs.
        If ``test_data`` is provided then the
        network will be evaluated against the test data after each
        epoch, and partial progress printed out. This is useful for
        tracking progress, but slows things down substantially.
        Returns a list containing the cost at each epoch.
        """
        training_data = list(training_data)
        n = len(training_data)

        # This list will store the cost (MSE) for each epoch
        cost_history = []

        if test_data:
            test_data = list(test_data)
            n_test = len(test_data)

        for j in range(epochs):
            random.shuffle(training_data)
            mini_batches = [
                training_data[k:k + mini_batch_size]
                for k in range(0, n, mini_batch_size)]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)

            # After each epoch, calculate and store the total cost (MSE)
            # over the entire training dataset.
            epoch_cost = self.total_cost(training_data)
            cost_history.append(epoch_cost)

            if test_data:
                print(f"Epoch {j}: Cost = {epoch_cost:.4f}, Accuracy: {self.evaluate(test_data)} / {n_test}")
            else:
                print(f"Epoch {j} complete. Cost = {epoch_cost:.4f}")

        return cost_history

    def update_mini_batch(self, mini_batch, eta):
        """
        Update the network's weights and biases by applying gradient
        descent using backpropagation to a single mini batch.
        The ``mini_batch`` is a list of tuples ``(x, y)``, and ``eta``
        is the learning rate.
        """
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        for x, y in mini_batch:
            delta_nabla_b, delta_nabla_w = self.backprop(x, y)
            nabla_b = [nb + dnb for nb, dnb in zip(nabla_b, delta_nabla_b)]
            nabla_w = [nw + dnw for nw, dnw in zip(nabla_w, delta_nabla_w)]
        self.weights = [w - (eta / len(mini_batch)) * nw
                        for w, nw in zip(self.weights, nabla_w)]
        self.biases = [b - (eta / len(mini_batch)) * nb
                       for b, nb in zip(self.biases, nabla_b)]

    def backprop(self, x, y):
        """
        Return a tuple ``(nabla_b, nabla_w)`` representing the
        gradient for the cost function C_x.  ``nabla_b`` and
        ``nabla_w`` are layer-by-layer lists of numpy arrays, similar
        to ``self.biases`` and ``self.weights``.
        """
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        # feedforward
        activation = x
        activations = [x]  # list to store all the activations, layer by layer
        zs = []  # list to store all the z vectors, layer by layer
        for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)
        # backward pass
        delta = self.cost_derivative(activations[-1], y) * \
                sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].transpose())
        for l in range(2, self.num_layers):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l + 1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l - 1].transpose())
        return (nabla_b, nabla_w)

    def evaluate(self, test_data):
        """
        Return the number of test inputs for which the neural
        network outputs the correct result. The neural network's
        output is assumed to be the index of whichever neuron
        in the final layer has the highest activation.
        """
        test_results = [(np.argmax(self.feedforward(x)), y)
                        for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)

    def total_cost(self, data):
        #Return the total cost (Mean Squared Error) for the data set ``data``.
        cost = 0.0
        n = len(data)
        for x, y in data:
            a = self.feedforward(x)
            cost += 0.5 * np.linalg.norm(a - y) ** 2
        return cost / n

    def cost_derivative(self, output_activations, y):
        """
        Return the vector of partial derivatives \partial C_x / \partial a
        for the output activations.
        """
        return (output_activations - y)
