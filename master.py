#!/usr/local/bin/python                                                                            
import xmlrpclib, sys,networkx as nx, threading, time, random, math, itertools
from SimpleXMLRPCServer import SimpleXMLRPCServer
from servernode import servernode
import Queue



# Current min tour                                                                                 
minTour = 999999
# Minimum path
minPath = None
# Graph and Queue
queue = None
G = None

l = [None]


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

    Q = Queue.Queue();
    for x in out.keys():
        L = [];
        for element in x:
            L.append(element);
        Q.put(L);
    return Q;

# This function first updates the minTour and subgraph, then sends out the minTour                 
# to all other slavenodes.                                                                         
def updateMinTour(newMinTour,path):
   global minTour
   global minPath                                                                                   
   global l                                                                                       
   minTour = newMinTour
   minPath = path

   # Update min on each slave node (Probably want to exclude the node that called this)            
   for i in l:                                                                                    
      l[i].updateMin(minTour) 
                                                                    
   return "Minimum is: {}".format(minTour)

# Need to figure out how to turn graph into a string so we can send it
def getWholeGraph():
    str1 = ''.join(nx.generate_gml(G))
    return str1
    
    
    

# Gives node a subgraph to explore                                                                 
def getGraph():   
   global queue
   print "In getGraph"
   return queue.get()

def main():
   global G
   global queue
   global minPath
   G, pos = randomMetricGraph(10)
   queue = makeQueue(G,1)

# List of slave nodes tuples(ip,port)                                                          
   nodes = [("172.31.13.227",8200)]

# Create and start server                                                                        
   master_ip = "172.31.13.224"
   master_port = 8100
   master = servernode(master_ip,master_port)
   master.register_function(updateMinTour)
   master.register_function(getGraph)
   master.register_function(getWholeGraph)
   master.start()
   

#Create clients for every slavenode                                                           
   global l
   for i in nodes:             
      j = 0
      url = "http://{}:{}".format(i[0], i[1])
      l[j] = xmlrpclib.ServerProxy(url)
      j+= 1
   try:
       while not queue.empty():
           time.sleep(1)
       print (minPath)
       master.stop_server()
   except KeyboardInterrupt:
      master.stop_server()
def start():
    main()

if __name__ == "__main__": main()
