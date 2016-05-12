#need this import
from ast import literal_eval;


import  networkx as nx;
import random;
import math;


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
            if random.uniform(0,1)>0.5:
                w = math.pow( math.pow(pos[i][0]-pos[j][0],2)+math.pow(pos[i][1]-pos[j][1],2),0.5);
                G.add_edge(i,j,weight = w);
                G.add_edge(j,i,weight = w);

    return G,pos;


#copy these next 2 plus the import
def graphToString(G):
    out = "";

    undirG = G.to_undirected();
    for v in undirG.nodes():
        out+="v:"+str(v)+"\n";
    for e in undirG.edges():
        attr = G.get_edge_data(e[0],e[1]);
        out+= "e:"+str(e)+"\t"+str(attr)+"\n";
    return out;

def stringToGraph(S):
    out = nx.Graph();
    out = G.to_undirected();
    for line in S.split("\n"):
        if line[:2] == "v:":
            out.add_node(literal_eval(line[2:].strip("\n")));
        if line[:2] == "e:":
            print(line);
            trimmed = line[2:].strip("\n");
            split = trimmed.split("\t");
            tuple = literal_eval(split[0]);
            dict = literal_eval(split[1]);
            out.add_edge(tuple[0],tuple[1],dict);

    return out;



G, pos = randomMetricGraph(10);
print(G.edges());
print(graphToString(G));
G2 = stringToGraph(graphToString(G));

print(len(G.edges()));
print(len(G2.edges()));
print(G.edges());
print(G2.edges());

print(len(G.nodes()));
print(len(G2.nodes()));
print(G.nodes());
print(G2.nodes());


print("assert no missing original in new:");
for e in G.edges():
    x = e[0];
    y = e[1];
    if not e in G2.edges():
        print("FAIL");

print("assert no edges in new that didn't exist before:");
for e in G2.edges():
    if not e in G.edges():
        print("FAIL");