#!/usr/local/bin/python
import xmlrpclib, sys, networkx as nx, threading
from SimpleXMLRPCServer import SimpleXMLRPCServer
from .servernode importservernode
from .clientnode importclientnode



# Current min tour
min = 999999
graph = nx.Graph()

def updateMinTour(minTour):
   min = minTour
   # Update min on each slave node
   

# Gives node a subgraph to explore
def getGraph():
    return graph

def main():
   # List of slave nodes tuples(ip,port)
   nodes = [(1,8000)]
   # Create clients for every slavenode
   for i in nodes:
      list[i] = clientnode(,i[0],i[1])
      
    


def start():
    main()
