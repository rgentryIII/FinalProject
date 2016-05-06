#!/usr/local/bin/python

import xmlrpclib, sys, networkx as nx, threading
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
    



















    

    

