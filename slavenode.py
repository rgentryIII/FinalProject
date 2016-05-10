#!/usr/local/bin/python

import xmlrpclib, sys, networkx as nx, threading, math
from SimpleXMLRPCServer import SimpleXMLRPCServer
from servernode import servernode


minTour = 999999
master = None
G = None

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
        head, tail = path[0:pathLen-1], path[pathLen-1:];        
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
    global master
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
                master.updateMinTour(bound,out)
            else:
                #add all child paths to stack
                for i in range(0,n):
                    if not i in path:
                        child = path+[i];
                        stack.append(child);
   
   



def updateMin(newMin):
    global minTour
    minTour = newMin
    print "Minimum is: {}".format(minTour)
    return "Minimum is: {}".format(minTour)


def start():
    main()

def main():
    global G
    slave_ip = "172.31.13.227"
    slave_port = 8200
    slave = servernode(slave_ip,slave_port)
    slave.register_function(updateMin)
    slave.start()
    

    master_ip = "172.31.13.224"
    master_port = 8100
    url = "http://{}:{}".format(master_ip, master_port)
    master = xmlrpclib.ServerProxy(url,verbose =True)
    try:
        strGraph = master.getWholeGraph()
        G = nx.parse_gml(strGraph)
        print "Made it"
        x = master.getGraph()

        while (x != None):
            print "in While"
            minTour(G,x)    
            print "First subgraph complete"
            x = master.getGraph
        slave.stop_server()
    except KeyboardInterrupt:
        print "Blah"
        slave.stop_server()
        
if __name__ == "__main__": 
    main()
    



















    

    

