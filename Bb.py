#branch and bound for tsp- Alex Rick n Joel
import networkx as nx;
import random;
import matplotlib.pyplot as plt
import math;
infinity = 9999999999;

#creates a graph of n nodes with totally random edge weights
def randomGraph(n):
    G = nx.Graph();
    for i in range(0,n):
       G.add_node(i);
    for i in range(0,n):
        for j in range(i+1,n):
            w = random.uniform(0.1,1);
            G.add_edge(i,j,weight = w);
            G.add_edge(j,i,weight = w);
    return G;

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

#returns the weight through the indices specified by path through G
def pathWeight(G, path):
    out = 0;
    l = len(path);
    for i in range(0, l-1):
        out+= G.get_edge_data(path[i],path[i+1])['weight'];
    return out;

#returns the weight through the indices of a tour (n length cycle) specified by path through G
def tourWeight(G, path):
    out = 0;
    l = len(path);
    for i in range(0, l):
        out+= G.get_edge_data(path[i],path[(i+1)%l])['weight'];
    return out;

#computes the lower bound a tour starting with path could have through G. Basically path lenght + the
#lowest two edge weights per node
def lowerBound(G, path):
    l = len(path);
    n = G.number_of_nodes();
    if l == n:
        return tourWeight(G,path);
    out = pathWeight(G,path);
    sum = 0;
    for i in range(0,n):
        *head, tail = path;
        if not i in path or i == tail:
            min1 = infinity;
            min2 = infinity;
            for j in range(0,n):
                if j == i:
                    continue;
                if min1 > min2:
                    if G.get_edge_data(i,j)['weight'] < min1:
                        min1 = G.get_edge_data(i,j)['weight'];
                else:
                    if G.get_edge_data(i,j)['weight'] < min2:
                        min2 = G.get_edge_data(i,j)['weight'];
            sum+=min1+min2;
    out+=sum/2;
    return out;

#the main algorithm
def minTour(G):
    stack = [];
    n = G.number_of_nodes();
    stack.append([0]);
    #really lazy bound - each edge has max weight of one in current graph generators
    bound = n;
    out = None;
    while len(stack)>0:
        path = stack.pop();
        lower = lowerBound(G,path);
        if lower < bound:
            if len(path) == n:
                print("complete tour found");
                print(lower);
                bound = lower;
                #if this beat the bound and is a full tour, this is our best complete solution yet
                out = path;
            else:
                #add all child paths to stack
                for i in range(0,n):
                    if not i in path:
                        child = path+[i];
                        stack.append(child);

    return out;

G,pos = randomMetricGraph(15);
tour = minTour(G);
print(tour);

#create a list of edges in the tour so we can color them for output
tourEdges = [];
for i in range(0,G.number_of_nodes()):
    tourEdges.append((tour[i],tour[((i+1)%G.number_of_nodes())]));
    tourEdges.append((tour[((i+1)%G.number_of_nodes())],tour[i]));

colors = [];
for e in G.edges():
    if e in tourEdges:
        colors.append('r');
    else:
        colors.append('w');


nx.draw(G, pos=  pos,edge_color=colors, with_labels=True);

plt.show();