#!/usr/local/bin/python                                                                               

import xmlrpclib, sys, threading
from SimpleXMLRPCServer import SimpleXMLRPCServer


# Acts as a server so other nodes (the master) can call methods on it                                
class servernode(threading.Thread):
    def _init_(self,ip,port):
        threading.Thread.__init__(self)
        self.ip = ip
        self.port = port
    def run(self,functions):
        server = SimpleXMLRPCServer(ip,port)
        for i in functions:
            server.register_function(functions[i])



            
