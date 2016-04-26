//(c) Alex Ellison 2014
package tsp;

import java.util.Arrays;

/**
 * branch and bound solution to metric traveling salesman problem based on the
 * algorithm laid out by the authors of the following page
 * http://www.academic.marist.edu/~jzbv/algorithms/Branch%20and%20Bound.htm
 *
 */
import java.util.*;

public class BB {

    final int N;
    Graph G;
    double upperBound;

    public BB(Graph G) {
        this.G = G;
        N = G.M.length;
    }

    public Order minTour() {
        long time = TSP.now();
        Order out= minTour(0);
        time = TSP.now()-time;
        System.out.println("non-parallel BB time: "+time+" for N = "+N);
        System.out.println("min dist: "+out.weight);
        return out;
    }

    public Order minTour(int seed) {
        //this can be implemented with a stack, queue or priority queue- but
        //the most memory efficient is by far the stack, and memory is our 
        //biggest limiting variable at this point
        //Seed is the single vertex we start with as our partial solution
        if (seed >= N) {
            System.out.println("seed out of bounds");
            return null;
        }

        Stack<Order> S = new Stack<>();
        //upperBound = G.MSTorder.weight;
        upperBound = G.MSTorder.weight;
        Order out = null;
        Order init = new Order(new int[]{seed}, upperBound);
        S.add(init);

        while (S.size() > 0) {
            Order dq = S.pop();
            //pruning 
            if (dq.weight > upperBound) {
                continue;
            }
            //if node we're considering is a tour, we consider it as a
            //possible new best
            if (dq.indices.length == N) {
                upperBound = dq.weight;
                out = dq;
            } else {
                //adding children to stack
                Order[] progeny = progeny(dq);
                for (Order p : progeny) {
                    S.add(p);
                }
            }
        }

        return out;
    }

    public Order[] progeny(Order O) {
        //we find all the possible paths one longer than O by considering all
        //indices in G but not in O and appending them to the end of O
        int n = N - O.indices.length;
        Order[] out = new Order[n];
        int idx = 0;
        for (int i = 0; i < N; i++) {
            if (!contains(O.indices, i)) {
                out[idx] = new Order(append(O.indices, i));
                out[idx].weight = lowerBound4(out[idx]);
                idx++;
            }
        }
        Arrays.sort(out);
        return out;
    }

    public double lowerBound(Order O) {
        //computes the lower bound of tsp weight of O through G- sum of specific
        //path and min possible over the remaining values (including the last
        //entry in the specified path)
        if (O.indices.length == N) {
            return O.cycleWeight(G);
        }

        double out = O.stWeight(G);

        for (int i = 0; i < N; i++) {
            if (!contains(O.indices, i) || i == O.indices[O.indices.length - 1]) {
                //add min edge weight incident with vertex i to out
                double min = Double.POSITIVE_INFINITY;
                for (int e = 0; e < N; e++) {
                    if (i != e) {
                        if (G.M[i][e] < min) {
                            min = G.M[i][e];
                        }
                    }
                }
                out += min;
            }
        }
        return out;
    }

    public double lowerBound2(Order O) {
        //this is slightly more sophisticated lower bound function- rather 
        // than just looking at the single least expensive edge per unvisited
        // vertex it averages the smallest two, which is a better bound since
        // each vertex must be entered and left by two ways
        if (O.indices.length == N) {
            return O.cycleWeight(G);
        }

        double out = O.stWeight(G);
        double sum = 0;
        for (int i = 0; i < N; i++) {
            if (!contains(O.indices, i) || i == O.indices[O.indices.length - 1]) {
                //add min edge weight incident with vertex i to out
                double min1 = Double.POSITIVE_INFINITY;
                double min2 = min1;
                for (int e = 0; e < N; e++) {
                    if (i != e) {
                        if (min1 > min2) {
                            if (G.M[i][e] < min1) {
                                min1 = G.M[i][e];
                            }
                        } else {
                            if (G.M[i][e] < min2) {
                                min2 = G.M[i][e];
                            }
                        }
                    }
                }
                sum += min1 + min2;
            }
        }
        sum /= 2;
        out += sum;
        return out;
    }

    public double lowerBound3(Order O) {
        //this the same as bound2, but with a pre-computation of contained
        //indices. I expected this to be faster but that may not be the case
        //for small arrays or even in general, data insufficient so far
        if (O.indices.length == N) {
            return O.cycleWeight(G);
        }

        double out = O.stWeight(G);
        double sum = 0;

        boolean[] contained = contained(O.indices, N);
        
        for (int i = 0; i < N; i++) {
            if (!contained[i] || i == O.indices[O.indices.length - 1]) {
                //add min edge weight incident with vertex i to out
                double min1 = Double.POSITIVE_INFINITY;
                double min2 = min1;
                for (int e = 0; e < N; e++) {
                    if (i != e ) {
                        if (min1 > min2) {
                            if (G.M[i][e] < min1) {
                                min1 = G.M[i][e];
                            }
                        } else {
                            if (G.M[i][e] < min2) {
                                min2 = G.M[i][e];
                            }
                        }
                    }
                }
                sum += min1 + min2;
            }
        }
        sum /= 2;
        out += sum;
        return out;
    }

     public double lowerBound4(Order O) {
        //this the same as bound 3 except for vertices not in path we only
         //look at min weights to vertices not in path (except 0 and l-1)
         //CURRENTLY YIELDS IMPERFECT ANSWERS
        if (O.indices.length == N) {
            return O.cycleWeight(G);
        }

        double out = O.stWeight(G);
        double sum = 0;

        boolean[] contained = contained(O.indices, N);
        boolean[] containedP = Arrays.copyOf(contained, contained.length);
        containedP[O.indices[O.indices.length-1]]=false;
        containedP[0]=false;
        for (int i = 0; i < N; i++) {
            if (!contained[i] || i == O.indices[O.indices.length - 1]) {
                //add min edge weight incident with vertex i to out
                double min1 = Double.POSITIVE_INFINITY;
                double min2 = min1;
                for (int e = 0; e < N; e++) {
                    if (i != e && !containedP[e]) {
                        if (min1 > min2) {
                            if (G.M[i][e] < min1) {
                                min1 = G.M[i][e];
                            }
                        } else {
                            if (G.M[i][e] < min2) {
                                min2 = G.M[i][e];
                            }
                        }
                    }
                }
                sum += min1 + min2;
            }
        }
        sum /= 2;
        out += sum;
        return out;
    }

    
    public int[] append(int[] arr, int n) {
        //appends n to the end of input array
        int[] out = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            out[i] = arr[i];
        }
        out[arr.length] = n;
        return out;
    }

    public boolean contains(int[] arr, int n) {
        //returns true iff n is an element of arr
        for (int i : arr) {
            if (i == n) {
                return true;
            }
        }
        return false;
    }

    public boolean[] contained(int[] indices, int n) {
        boolean[] out = new boolean[n];
        for (int i = 0; i < n; i++) {
            out[i] = false;
        }
        for (int i : indices) {
            out[i] = true;
        }
        return out;
    }
}
