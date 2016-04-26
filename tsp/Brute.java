//(c) Alex Ellison, 2014
package tsp;

/**
 *
 * class for finding the optimal solution to a traveling salesman problem. It
 * does so by brute force and is just a verification mechanism for the duration
 * of development.
 */
public class Brute {

    Graph G;
    Order min;
    double minW = Double.POSITIVE_INFINITY;

    public Brute(Graph G) {
        this.G = G;
    }

    //computes the length of the cycle from first index through the others and 
    //then back to the first from the final
    private double pathWeight(int[] indices) {
        int n = indices.length;
        double out = 0;
        for (int i = 0; i < n; i++) {
            out += G.M[indices[i]][indices[(i + 1) % n]];
        }
        return out;
    }

    public Order minTour() {
        min = null;
        minW = Double.POSITIVE_INFINITY;

        int[] ints = new int[G.M.length];
        for (int i = 0; i < G.M.length; i++) {
            ints[i] = i;
        }

        opt(ints, new int[0]);
        return min;
    }

    //opt runs through all possible paths and whenever it finds a new min tour
    //it sets the instance variable min to that new value. We systematically
    //run through all possibilities by maintaining an array of vertices
    //unvisited (in) and those visited (out)
    private void opt(int[] in, int[] out) {
        if (in.length == 0) {
            double w = pathWeight(out);
            if (w < minW) {
                minW = w;
                min = new Order(out, w);
            }
        } else {
            /*
             * running through and copying in, except for one value each time
             * and adding that to a clone of out - this produces a recursive
             * tree at which the leaves have in= {} and out is an ordering
             * on which a pathlength is then computed and compared to the min.
             * All ugly lines below are simply indexing
             */
            for (int i = 0; i < in.length; i++) {
                int[] newIn = new int[in.length - 1];
                for (int e = 0; e < i; e++) {
                    newIn[e] = in[e];
                }
                for (int e = i + 1; e < in.length; e++) {
                    newIn[e - 1] = in[e];
                }

                int[] newOut = new int[out.length + 1];
                for (int e = 0; e < out.length; e++) {
                    newOut[e] = out[e];
                }
                newOut[newOut.length - 1] = in[i];
                opt(newIn, newOut);
            }
        }
    }
}