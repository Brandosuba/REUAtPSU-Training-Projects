import networkx as netx
import numpy as np
import matplotlib.pyplot as plt
import random
from collections import deque

def randomwalk(net):
    if not netx.is_connected(net):
        return False
        
    N = net.number_of_nodes()
    visited = set()
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)
    steps = 0
    
    while len(visited) < N:
        neighbors = list(net.neighbors(curr_node))
        curr_node = random.choice(neighbors)
        visited.add(curr_node)
        steps += 1
        
    return steps

def explorewalk(net):
    if not netx.is_connected(net):
        return False
        
    N = net.number_of_nodes()
    visited = set()
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)
    steps = 0
    
    while len(visited) < N:
        neighbors = list(net.neighbors(curr_node))
        unvisited = [n for n in neighbors if n not in visited]
        curr_node = random.choice(unvisited) if unvisited else random.choice(neighbors)
        visited.add(curr_node)
        steps += 1
    return steps

def randomwalk_trace(net):
    if not netx.is_connected(net):
        return False

    N = net.number_of_nodes()
    visited = set()
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)

    yield curr_node, visited.copy()  # Initial state

    while len(visited) < N:
        neighbors = list(net.neighbors(curr_node))
        curr_node = random.choice(neighbors)
        visited.add(curr_node)
        yield curr_node, visited.copy()
        
def explorewalk_trace(net):
    if not netx.is_connected(net):
        return False
        
    N = net.number_of_nodes()
    visited = set()
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)
    
    yield curr_node, visited.copy()
    
    while len(visited) < N:
        neighbors = list(net.neighbors(curr_node))
        unvisited = [n for n in neighbors if n not in visited]
        curr_node = random.choice(unvisited) if unvisited else random.choice(neighbors)
        visited.add(curr_node)
        yield curr_node, visited.copy()
    
def dfswalk(net):
    if not netx.is_connected(net):
        return False
    
    N = net.number_of_nodes()
    visited = set()
    stack = []
    
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)
    stack.append(curr_node)
    steps = 0
    
    while len(visited) < N:
        curr_node = stack[-1]
        neighbors = list(net.neighbors(curr_node))

        unvisited = [n for n in neighbors if n not in visited]
        
        if unvisited:
            next_node = random.choice(unvisited)
            visited.add(next_node)
            stack.append(next_node)
        else:
   
            stack.pop()
        steps += 1
        
    return steps

def dfswalk_trace(net):
    if not netx.is_connected(net):
        return False
    
    N = net.number_of_nodes()
    visited = set()
    stack = []
    
    curr_node = random.choice(list(net.nodes()))
    visited.add(curr_node)
    stack.append(curr_node)
    
    yield curr_node, visited.copy()
    
    while len(visited) < N:
        curr_node = stack[-1]
        neighbors = list(net.neighbors(curr_node))
        unvisited = [n for n in neighbors if n not in visited]
        
        if unvisited:
            next_node = random.choice(unvisited)
            visited.add(next_node)
            stack.append(next_node)
            yield next_node, visited.copy()
        else:
            
            stack.pop()
            yield stack[-1], visited.copy()
   

