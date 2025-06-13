import networkx as netx
import numpy as np
import matplotlib.pyplot as plt
from walkers import randomwalk
from walkers import explorewalk
from walkers import dfswalk

nvals = [10, 20, 30, 40, 50, 60, 70, 80]
cvals = [40, 60, 80, 100, 120, 140, 160, 180]

rand_rw = {}
rand_exp = {}
rand_dfs = {}

smallworld_rw = {}
smallworld_exp = {}
smallworld_dfs = {}

scalefree_rw = {}
scalefree_exp = {}
scalefree_dfs = {}

def plottimes_combined(rand_rw, rand_exp, rand_dfs,
                       smallworld_rw, smallworld_exp, smallworld_dfs,
                       scalefree_rw, scalefree_exp, scalefree_dfs):
    fig, axs = plt.subplots(3, 3, figsize=(15, 8))
    
    def plot_single(ax, results, title_prefix):
        for c in cvals:
            x = []
            y = []
            for n in nvals:
                if (n, c) in results:
                    x.append(n)
                    y.append(results[(n, c)])
            ax.plot(x, y, label=f'C={c}')
        ax.set_xlabel('Nodes (n)')
        ax.set_ylabel('Steps')
        ax.set_title(title_prefix)
        ax.legend()
    
    # Random Graph
    plot_single(axs[0, 0], rand_rw, 'Random Graph - Random Walk')
    plot_single(axs[0, 1], rand_exp, 'Random Graph - Explore Walk')
    plot_single(axs[0, 2], rand_dfs, 'Random Graph - DFS Walk')
    
    # Small World
    plot_single(axs[1, 0], smallworld_rw, 'Small World - Random Walk')
    plot_single(axs[1, 1], smallworld_exp, 'Small World - Explore Walk')
    plot_single(axs[1, 2], smallworld_dfs, 'Small World - DFS Walk')
    
    # Scale Free
    plot_single(axs[2, 0], scalefree_rw, 'Scale Free - Random Walk')
    plot_single(axs[2, 1], scalefree_exp, 'Scale Free - Explore Walk')
    plot_single(axs[2, 2], scalefree_dfs, 'Scale Free - DFS Walk')

    plt.tight_layout()
    plt.show()

for n in nvals:
    for c in cvals:
        if c > n*(n-1)/2:
            continue
        
        randtimes_rw = []
        randtimes_exp = []
        randtimes_dfs = []
        
        smallworldtimes_rw = []
        smallworldtimes_exp = []
        smallworldtimes_dfs = []
        
        scalefreetimes_rw = []
        scalefreetimes_exp = []
        scalefreetimes_dfs = []
        
        for i in range(500):
            net = netx.gnm_random_graph(n, c)
            if netx.is_connected(net):
                randtimes_rw.append(randomwalk(net))
                randtimes_exp.append(explorewalk(net))
                randtimes_dfs.append(dfswalk(net))
            
            k = (2 * c) // n
            try:
                sw = netx.watts_strogatz_graph(n, k, 0.1)
                if netx.is_connected(sw):
                    smallworldtimes_rw.append(randomwalk(sw))
                    smallworldtimes_exp.append(explorewalk(sw))
                    smallworldtimes_dfs.append(dfswalk(sw))
            except:
                pass
            
            m = max(1, c // n)  # BA graph requires m >= 1
            try:
                sf = netx.barabasi_albert_graph(n, m)
                if netx.is_connected(sf):
                    scalefreetimes_rw.append(randomwalk(sf))
                    scalefreetimes_exp.append(explorewalk(sf))
                    scalefreetimes_dfs.append(dfswalk(sf))
            except:
                pass
        
        if randtimes_rw:   
            rand_rw[(n,c)] = np.mean(randtimes_rw)
        if randtimes_exp:
            rand_exp[(n,c)] = np.mean(randtimes_exp)
        if randtimes_dfs:
            rand_dfs[(n,c)] = np.mean(randtimes_dfs)
        
        if smallworldtimes_rw:
            smallworld_rw[(n,c)] = np.mean(smallworldtimes_rw)
        if smallworldtimes_exp:
            smallworld_exp[(n,c)] = np.mean(smallworldtimes_exp)
        if smallworldtimes_dfs:
            smallworld_dfs[(n,c)] = np.mean(smallworldtimes_dfs)
        
        if scalefreetimes_rw:
            scalefree_rw[(n,c)] = np.mean(scalefreetimes_rw)
        if scalefreetimes_exp:
            scalefree_exp[(n,c)] = np.mean(scalefreetimes_exp)
        if scalefreetimes_dfs:
            scalefree_dfs[(n,c)] = np.mean(scalefreetimes_dfs)

plottimes_combined(rand_rw, rand_exp, rand_dfs,
                   smallworld_rw, smallworld_exp, smallworld_dfs,
                   scalefree_rw, scalefree_exp, scalefree_dfs)