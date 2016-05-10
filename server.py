#!/usr/local/bin/python                                                                               

import xmlrpclib, sys, threading
from SimpleXMLRPCServer import SimpleXMLRPCServer
from SimpleXMLRPCServer import SimpleXMLRPCRequestHandler

# Acts as a server so other nodes (the master) can call methods on it                                
class servernode(threading.Thread):
    def __init__(self,ip,port):
        super(servernode,self).__init__()
        self.ip = ip
        self.port = port
        self.server = SimpleXMLRPCServer((ip, port))
        self.server.register_introspection_functions()

    def register_function(self, function):
        self.server.register_function(function)

    def run(self):
        self.server.serve_forever()

    def stop_server(self):
        self.server.shutdown()
        self.server.server_close()


    




            
