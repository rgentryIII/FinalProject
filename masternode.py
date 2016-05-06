#!/usr/local/bin/python
import xmlrpclib, sys, networkx as nx, threading
from SimpleXMLRPCServer import SimpleXMLRPCServer
from .servernode importservernode
from .clientnode importclientnode


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


# Current min tour
min = 999999
graph = nx.Graph()

def updateMinTour(minTour):
   min = minTour
   # Update min on each slave node
   

# Gives node a subgraph to explore
def getGraph():
    return graph



#makes a Queue of segments starting from the first node and extending n edges away in each direction -> will return 2n+1 length segs
#the reason we specify the middle and work out in both directions is that if you specify (0,1,2) and (0,3,4) as start
#points, you will end up examining (0,1,2...,4,3) and (0,3,4....2,1) which are isomorphic. However, if you say (4,3,0,1,2)
#and get rid of (2,1,0,3,4) then without loss of generality all subproblems will be non-overlapping.
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




def main():
   # List of slave nodes tuples(ip,port)
   nodes = [(1,8000)]
   # Create clients for every slavenode
   for i in nodes:
      list[i] = clientnode(,i[0],i[1])
      
    


def start():
    main()
