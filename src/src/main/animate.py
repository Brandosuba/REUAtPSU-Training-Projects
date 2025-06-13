import networkx as netx
import numpy as np
import matplotlib.pyplot as plt
from walkers import randomwalk_trace, explorewalk_trace, dfswalk_trace
import matplotlib.animation as animation


n = 40
c = 60

randnet = netx.gnm_random_graph(n, c)
while not netx.is_connected(randnet):
    randnet = netx.gnm_random_graph(n, c)
    
k = (2 * c) // n
smallnet = netx.watts_strogatz_graph(n, k, 0.1)
while not netx.is_connected(smallnet):
    smallnet = netx.watts_strogatz_graph(n, k, 0.1)

m = c // n
scalenet = netx.barabasi_albert_graph(n, m)
while not netx.is_connected(scalenet):
    scalenet = netx.barabasi_albert_graph(n, m)

#everything after this is chatgpt :P

# --- Positions ---
pos_rand = netx.spring_layout(randnet, seed=42)
pos_small = netx.spring_layout(smallnet, seed=42)
pos_scale = netx.spring_layout(scalenet, seed=42)

# --- Generate traces ---
trace_rand = list(explorewalk_trace(randnet))
trace_small = list(explorewalk_trace(smallnet))
trace_scale = list(explorewalk_trace(scalenet))

trace_rand_dfs = list(dfswalk_trace(randnet))
trace_small_dfs = list(dfswalk_trace(smallnet))
trace_scale_dfs = list(dfswalk_trace(scalenet))

frames = max(len(trace_rand), len(trace_small), len(trace_scale),
             len(trace_rand_dfs), len(trace_small_dfs), len(trace_scale_dfs))

# --- Setup Plot ---
fig, axes = plt.subplots(2, 3, figsize=(18, 8))

titles = ["Random Graph", "Small World", "Scale-Free"]
graphs = [randnet, smallnet, scalenet]
positions = [pos_rand, pos_small, pos_scale]

# Existing walks (explorewalk)
for ax, G, pos, title in zip(axes[0], graphs, positions, titles):
    netx.draw_networkx_edges(G, pos, ax=ax, alpha=0.3)
    netx.draw_networkx_labels(G, pos, ax=ax, font_size=8)
    ax.set_title(f"{title} - Explore Walk")
    
# DFS walks
for ax, G, pos, title in zip(axes[1], graphs, positions, titles):
    netx.draw_networkx_edges(G, pos, ax=ax, alpha=0.3)
    netx.draw_networkx_labels(G, pos, ax=ax, font_size=8)
    ax.set_title(f"{title} - DFS Walk")

walkers = []
visited_nodes = [[] for _ in range(6)]  # 3 explore + 3 dfs

# Create scatter plot objects for walkers
for ax in axes.flatten():
    walkers.append(ax.scatter([], [], s=200, c='red'))

# --- Animation Functions ---
def init():
    for walker in walkers:
        walker.set_offsets(np.empty((0, 2)))
    return walkers

def update(frame):
    # Explore walks
    for i, trace in enumerate([trace_rand, trace_small, trace_scale]):
        ax = axes[0, i]
        pos = positions[i]
        if frame < len(trace):
            node, visited = trace[frame]
        else:
            node, visited = trace[-1]

        # Clear previous visited highlights
        for v in visited_nodes[i]:
            v.remove()
        visited_nodes[i].clear()

        # Draw visited nodes
        for v in visited:
            visited_nodes[i].append(ax.scatter(*pos[v], s=100, c='skyblue', alpha=0.7))

        walkers[i].set_offsets([pos[node]])

    # DFS walks
    for i, trace in enumerate([trace_rand_dfs, trace_small_dfs, trace_scale_dfs], start=3):
        ax = axes[1, i-3]
        pos = positions[i-3]
        if frame < len(trace):
            node, visited = trace[frame]
        else:
            node, visited = trace[-1]

        for v in visited_nodes[i]:
            v.remove()
        visited_nodes[i].clear()

        for v in visited:
            visited_nodes[i].append(ax.scatter(*pos[v], s=100, c='skyblue', alpha=0.7))

        walkers[i].set_offsets([pos[node]])

    return walkers + sum(visited_nodes, [])

# --- Run Animation ---
ani = animation.FuncAnimation(
    fig, update, frames=frames, init_func=init, interval=25, repeat=False
)

plt.show()

poob = input("poob: ")