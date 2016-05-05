#master node for distributed branch and bound for tsp- Alex Rick n Joel
import networkx as nx;
import random;
import matplotlib.pyplot as plt
import math;
import itertools;
from Lib import queue;
infinity = 9999999999;


#creates graph with nodes at positions pos and returns metric graph over those positions, and the pos
def randomMetricGraph(n):
    G = nx.Graph();
    pos=[];
    for i in range(0,n):
        coord = (random.uniform(0,1),random.uniform(0,1));
        pos.append(coord);
        G.add_node(i, pos = coord);
    for i in range(0,n):
        for j in range(i+1,n):
            w = math.pow( math.pow(pos[i][0]-pos[j][0],2)+math.pow(pos[i][1]-pos[j][1],2),0.5);
            G.add_edge(i,j,weight = w);
            G.add_edge(j,i,weight = w);

    return G,pos;


#makes a Queue of segments starting from node
def makeQueue(G,n):
    N = 2*n+1;
    #generate all permutations of appropriate length
    permutations = itertools.permutations(G.nodes(),N);
    out = {}
    #we keep a list so we can iterate the dictionary (hashmap) while modifying it
    list= [];
    #only take permutations with the same midpoint
    for x in permutations:
        if x[n] == G.nodes()[0]:
            out[x]=x;
            list.append(x);
    #remove reversed copies
    L= len(out);
    for i in range(0,L):
        x = list[i];
        rev = x[::-1];
        if x in out:
            del out[rev];

    Q = queue.Queue();
    for x in out.keys():
        Q.put(x);
    return Q;


G, pos = randomMetricGraph(10);
Q = makeQueue(G,2);

while not Q.empty():
    print(Q.get());