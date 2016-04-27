#branch and bound for tsp- Alex Rick n Joel
import networkx as nx;
import random;


infinity = 9999999999;

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

def pathWeight(G, path):
    out = 0;
    l = len(path);
    for i in range(0, l-1):
        out+= G.get_edge_data(path[i],path[i+1])['weight'];
    return out;

def tourWeight(G, path):
    out = 0;
    l = len(path);
    for i in range(0, l):
        out+= G.get_edge_data(path[i],path[(i+1)%l])['weight'];
    return out;

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


def minTour(G):
    stack = [];
    n = G.number_of_nodes();
    stack.append([0]);
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


G = randomGraph(10);
tour = minTour(G);
print(tour);
