//(c) Alex Ellison 2014
package tsp;

import java.util.*;

public class TSP {

    static public long maxMem = -1;
    static PANE p;
    static Window W;

    public static void main(String[] args) {

        Graph G = new Graph(gen(3));
        p = new PANE(G);
        W = new Window(p);
        G.computeBB();
        p.repaint();

    }

    public static double[][] gen(int n) {
        double[][] out = new double[2 * (2 << n - 1)][2];
        System.out.println(out.length);
        int index = 0;
        double x0 = 0.5;
        double y0 = 0.5;
        for (int level = 0; level < (n-1); level++) {
            for (int i = 0; i < 2 << level; i++) {
                out[index] = new double[]{i, level};
                out[index + 1] = new double[]{-i, level};
                index += 2;
            }
        }
        out = Matrices.mult(0.1, out);
        for(int i = 0;i<out.length;i++){
            out[i][0]+=x0;
            out[i][1]+=y0;
        }
        return out;
    }

    public static double freeMemFraction() {
        double free = (double) Runtime.getRuntime().freeMemory();
        double max = (double) Runtime.getRuntime().maxMemory();
        return free / max;
    }

    public static double memUsedFraction() {
        long free = Runtime.getRuntime().freeMemory();
        return (double) (maxMem - free) / ((double) maxMem);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static Graph random(int N) {
        //creates random complete metric graph in unit square with N vertices
        double[][] XY = new double[N][2];
        for (int i = 0; i < N; i++) {
            XY[i][0] = Math.random();
            XY[i][1] = Math.random();
        }

        return new Graph(XY);
    }
}
