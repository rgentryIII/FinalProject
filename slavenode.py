#!/usr/local/bin/python

import xmlrpclib, sys, networkx as nx, threading
import math;
from SimpleXMLRPCServer import SimpleXMLRPCServer
from .servernode import servernode
from .clientnode import clientnode



def updateMin(newMin):
    min = newMin

# if a new mintour is found call updateMinTour
def newMinTour(minTour):
    master.updateMinTour(mintour)
    

# if it has explored the entire graph, it asks for a new subgraph from the master
def getGraph():
    G = master.getGraph()
    return G


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

#the main algorithm, takes the graph G and the LIST of nodes specifying the starting point of DFS
def minTour(G, start):
    stack = [];
    n = G.number_of_nodes();
    stack.append(start);
    bound = lowerBound(G,start);
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


def start():
    main()

def main():
    functions = ["updateMin"]
    server_ip = 1
    server_port = 8000
    server = servernode(,server_ip,server_port)
    server.run(,functions)

    client_ip = 2
    client_port = 8100

    
    client = clientnode(,client_ip,client_port)
    client.run()
    



















    

    

