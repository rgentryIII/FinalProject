//(c) Alex Ellison 2014
package tsp;

/**
 * matrix representing distances between vertices of a (connected) graph
 */
import java.util.*;

public class Graph {

    //cartesian coordinates of all vertices
    double[][] XY;
    //Matrix M stores all distances
    double[][] M;
    Order dynamicOrder = null;
    Order MSTorder = null;
    Order BBOrder = null;

    int[][] NN;
    
    public Graph(double[][] XY) {
        this.XY = XY;
        int n = XY.length;
        M = new double[n][n];
        NN= new int[n][n-1];

        for (int i = 0; i < n; i++) {
            for (int e = 0; e < n; e++) {
                double x1 = XY[i][0];
                double y1 = XY[i][1];
                double x2 = XY[e][0];
                double y2 = XY[e][1];
                double dx = x1 - x2;
                double dy = y1 - y2;
                if (i == e) {
                    M[i][e] = Double.POSITIVE_INFINITY;
                } else {
                    M[i][e] = Math.sqrt(dx * dx + dy * dy);
                }
            }
        }
        this.computeMSTApprox();
    }

    public double[][] minSpanTree() {

        int n = M.length;
        //complete graph has n(n-1)/2 edges
        Edge[] edges = new Edge[n * (n - 1) / 2];
        //build edge objects
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int e = i + 1; e < n; e++) {
                edges[idx] = new Edge(i, e, M[i][e]);
                idx++;
            }
        }
        Arrays.sort(edges);
        //contains the degree of vertex 0 <= i <n (initially n-1 for complete G) 
        int[] degree = new int[n];
        for (int i = 0; i < n; i++) {
            degree[i] = n - 1;
        }
        //prim's algorithm (M[i][e]=infinity means disconnected):

        //trees always have |V|-1 edges
        Edge[] tree = new Edge[n - 1];
        Set found = new Set();
        found.add(0);
        idx = 0;
        while (found.size() < n) {
            for (Edge e : edges) {
                boolean hasI1 = found.contains(e.i1);
                boolean hasI2 = found.contains(e.i2);
                if (hasI1 && !hasI2) {
                    found.add(e.i2);
                    tree[idx] = e;
                    idx++;
                    break;
                } else if (!hasI1 && hasI2) {
                    found.add(e.i1);
                    tree[idx] = e;
                    idx++;
                    break;
                }
            }
        }
        //construct output matrix - initialize as null Edges
        double[][] out = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int e = 0; e < n; e++) {
                out[i][e] = Double.POSITIVE_INFINITY;
            }
        }
        //set edge weights for all e in tree
        for (Edge e : tree) {
            //matrix needs to be symmetrical
            out[e.i1][e.i2] = e.weight;
            out[e.i2][e.i1] = e.weight;
        }
        return out;
    }

    public Order MSTApprox() {
        //finding min spanning tree
        double[][] tree = minSpanTree();
        //finding adjacency matrix of tree
        tree = orthodoxAdjacency(tree);
        int n = tree.length;
        int[] ordering = new int[n];
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        int idx = 0;

        //running a preorder traversal of the MST, the order of the nodes 
        //is their order in the TSP traversal (we start from zero)
        Set visited = new Set();
        visited.add(0);
        while (!stack.empty() && idx < ordering.length) {
            int pop = stack.pop();
            ordering[idx] = pop;
            for (int i = 0; i < n; i++) {
                if (tree[pop][i] == 1 && !visited.contains(i)) {
                    stack.push(i);
                    visited.add(i);
                }
            }
            idx++;
        }
        //computing tour weight
        double weight = 0;
        for (int i = 0; i < n - 1; i++) {
            weight += M[ordering[i]][ordering[i + 1]];
        }
        weight += M[ordering[n - 1]][ordering[0]];
        return new Order(ordering, weight);
    }

    //computes and sets the min tour based on the dynamic programming algorithm
    public void computeDynamic() {
        Dynamic D = new Dynamic(this);
        this.dynamicOrder = D.minTour();
    }
    
    public void computeDynamic2() {
        Dynamic2 D = new Dynamic2(this);
        this.dynamicOrder = D.minTour();
    }
    

    //computes and sets the min tour using branch and bound
    public void computeBB() {
        BB B = new BB(this);
        this.BBOrder = B.minTour();
    }

    //computes and sets the min tour using seeded branch and bound
    public void computeBB(int seed) {
        BB B = new BB(this);
        this.BBOrder = B.minTour(seed);
    }

    //computes and sets the min tour using parallel processing branch and bound
    public void computeBBParallel() {
        ParallelBB B = new ParallelBB(this);
        this.BBOrder = B.minTour();
    }
    
    //computes and sets the min tour using parallel processing branch and bound
    public void computeBBParallel(long timeCapMillis) {
        ParallelBB B = new ParallelBB(this);
        this.BBOrder = B.minTour(timeCapMillis);
    }

    public void computeMSTApprox() {
        this.MSTorder = MSTApprox();
    }
    
    public double BestMSTAweight(){
        //observation, mst approximation weight depends on which vertex the
        //traversal begins from, so we try them all.
        double[][] backup = Matrices.copy(M);
        int n = M.length;
        
        Order min = MSTApprox();
        for(int i = 1;i<n;i++){
            M= Matrices.diagonalShift(M, i);
            Order msta  = MSTApprox();
            if(msta.weight<min.weight){
                min = msta;
            }
        }
        M = backup;
        return min.weight;
    }

    public double[][] orthodoxAdjacency(double[][] weights) {
        //takes a matrix of weights where disconnected = infinity and
        //any edge has finite weight and returns an equivalent adjacency matrix
        double[][] out = new double[weights.length][weights[0].length];
        for (int i = 0; i < weights.length; i++) {
            for (int e = 0; e < weights[0].length; e++) {
                if (weights[i][e] == Double.POSITIVE_INFINITY) {
                    out[i][e] = 0;
                } else {
                    out[i][e] = 1;
                }
            }
        }
        return out;
    }
}

class Set {

    //simple set class for running prim's algorithm. Highly specific to this
    //program, uses values that are ints corresponding to indices in adjacency
    //matrix of G- which means all i are unique
    //As it is used in this program, the set size is linear to the number of 
    //vertices and as such is small, so there is really no gain in using some
    //ordered struct like a skiplist (yet)
    ArrayList<Integer> ints = new ArrayList<Integer>();

    public boolean contains(int i) {
        return ints.contains(new Integer(i));
    }

    public void add(int i) {
        ints.add(new Integer(i));
    }

    public int size() {
        return ints.size();
    }
}

class Edge implements Comparable<Edge> {

    int i1, i2;
    double weight;

    public Edge(int i1, int i2, double weight) {
        this.i1 = i1;
        this.i2 = i2;
        this.weight = weight;
    }

    public int compareTo(Edge other) {
        return 1 * Double.compare(weight, other.weight);
    }

    public String toString() {
        return "(" + i1 + ", " + i2 + ") weight: " + weight;
    }
}
