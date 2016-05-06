#!/usr/local/bin/python                                                                               

import xmlrpclib, sys, networkx as nx, threading

# Acts as a client that can call methods on the master node                                         
class clientnode(threading.Thread):
    def _init_(self,ip,port):
        threading.Thread._init_(self)
        self.ip = ip
        self.port = port
        self.master = master
    def run(self):
        url = "http://{}:{}".format(ip, port)
        master = xmlrpclib.ServerProxy(url)
