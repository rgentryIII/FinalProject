//(c) Alex Ellison 2014
package tsp;

import java.util.*;
import java.math.*;

/**
 * multi-threaded solver of branch and bound. For n vertices there are n-1
 * solvers running, one for each starting partial solution {0,i} where i!=0.
 */
public class ParallelBB {

    boolean[] done;
    final int N;
    Order out = null;
    Graph G;
    BB2[] solvers;
    private double upperBound;
    private double min = Double.POSITIVE_INFINITY;
    Order[] subSolutions;
    double pruned = 0;
    double[] factorials;
    long start;
    public ParallelBB(Graph G) {
        N = G.M.length;
        start = TSP.now();
        factorials = new double[N];
        factorials[0] = 1;
        for (int i = 1; i < N; i++) {
            factorials[i] = i * factorials[i - 1];
        }
        this.G = G;
        solvers = new BB2[N - 1];
        done = new boolean[N - 1];
        subSolutions = new Order[N - 1];
        this.setBound(G.MSTorder.weight);
    }

    public Order minTour() {
        long time = TSP.now();
        //creating a thread for n-1 subproblems
        for (int i = 1; i < N; i++) {
            solvers[i - 1] = new BB2(G, i, this);
        }
        while (!done()) {
            //rather than wasting the main thread's power we can wait and 
            //check if we're done at regular intervals
            try {
                 Thread.sleep(10);
                 
            } catch (Exception X) {
            }
        }
        //finding best solution found by the various threads
        double min = Double.POSITIVE_INFINITY;
        Order minOrder = null;
        for (Order o : subSolutions) {
            if (o != null) {
                if (o.weight < min) {
                    min = o.weight;
                    minOrder = o;
                }
            }
        }
        System.out.println("Parallel Solution found in " + (TSP.now() - time)
                + " ms. N = " + G.M.length + "\n");
         System.out.println("pruned fraction ~ "+pruned / (double) (N * factorials[N - 1]));
         System.out.println("min dist: "+minOrder.weight);
        return minOrder;
    }

    public Order minTour(long timeCapMillis) {
        long time = TSP.now();
        //creating a thread for n-1 subproblems
        for (int i = 1; i < N; i++) {
            solvers[i - 1] = new BB2(G, i, this);
        }

        while (TSP.now() - time < timeCapMillis && !done()) {

        }
        
        for (BB2 b : solvers) {
            b.kill();
        }

        //finding best solution found by the various threads
        double min = Double.POSITIVE_INFINITY;
        Order minOrder = null;
        for (Order o : subSolutions) {
            if (o != null) {
                if (o.weight < min) {
                    min = o.weight;
                    minOrder = o;
                }
            }
        }

        System.out.println("exact solution found: " + done());
         System.out.println(pruned / (double) (N * factorials[N - 1]));
        return minOrder;
    }

    public boolean done() {
        //the process is only done when all threads are done
        for (boolean b : done) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public void setBound(double bound) {
        System.out.println("new min: "+bound);
        this.upperBound = bound;
    }

    public double bound() {
        return upperBound;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double min() {
        return min;
    }
}

class BB2 extends BB implements Runnable {

    /*
     this works exactly the same as its superclass, the only differences are
     1. when it finishes it "checks in with" the parent that manages the
     various threads (flips a boolean that it's done and stores its best find).
     2. it shares an upperbound field with all other threads that they all are
     pushing down together
     */
    Thread T;
    final int N;
    int seed;
    ParallelBB parent;
    long startTime;
    private boolean kill = false;
    
    public BB2(Graph G, int seed, ParallelBB parent) {
        super(G);
        T = new Thread(this);
        this.seed = seed;
        N = G.M.length;
        this.parent = parent;
        startTime = System.currentTimeMillis();
        T.start();
    }

    public void run() {
        Order O;
        O = minTour(seed);
        parent.done[seed - 1] = true;
        parent.subSolutions[seed - 1] = O;
        long dt = System.currentTimeMillis() - startTime;
        long sec = dt / 1000;
        long min = sec / 60;
          System.out.println("thread " + seed + " done after " + sec + " sec = " + min + " min");
           System.out.println("nodes pruned (including all unvisited children): " + parent.pruned);
    }

    public Order minTour(int seed) {
        //this can be implemented with a stack, queue or priority queue- but
        //the most memory efficient is by far the stack, and memory is our 
        //biggest limiting variable at this point

        if (seed >= N) {
            System.out.println("seed out of bounds");
            return null;
        }

        Stack<Order> S = new Stack<>();

        Order out = null;
        Order init = new Order(new int[]{0, seed}, parent.bound());
        S.add(init);

        while (S.size() > 0 && !kill) {
            Order dq = S.pop();
            if (dq.weight > parent.bound()) {
                    parent.pruned+=parent.factorials[N-dq.indices.length+1];
                continue;
            }
            /*
            if(Math.random()<0.00001){
                double fact = N *parent.factorials[N-1];
                double d = parent.pruned/fact;
                System.out.println(d);
            }*/
            //if we get here, guaranteed not worse than bound
            if (dq.indices.length == N) {
                parent.setBound(dq.weight);
                TSP.p.G.BBOrder=dq;
                TSP.p.repaint();
                out = dq;
            } else {
                Order[] progeny = progeny(dq);
                for (Order p : progeny) {
                    S.add(p);
                }
            }
        }
        return out;
    }

    public void kill(){
        kill = true;
    }
}
