/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Alex
 */
public class Dynamic2 extends Dynamic {

    double lowerBound;
    BB bb;
    int culls = 0;

    public Dynamic2(Graph G) {
        super(G);
        lowerBound = G.BestMSTAweight();
        bb = new BB(G);
    }

    @Override
    public double minPathLength() {
        int n = g.M.length;
        //vertices minus zero
        int[] V = new int[n - 1];
        for (int i = 0; i < V.length; i++) {
            V[i] = i + 1;
        }
        //filling first "table" row (all pairs that include 0)
        for (int i : V) {
            Key k = new Key(Subsets.encode(new int[]{i}), i);
            table.add(0, k, new Entry(g.M[0][i], i));
        }

        //building up the rows in order of size
        for (int i = 2; i <= V.length; i++) {
            System.out.println("!");
            int[] subsets = Subsets.combinations(i, V);

            //for each subset S of the specified size, we now want to find the
            //optimal pathlength from 0, thru S ending at k in S
            for (int encoding : subsets) {
                int[] S = Subsets.decode(encoding);
                //scanning through terminus vertices, filling table entry for
                //each pairing of our fixed (in scope of loop) S and variable k
                for (int k : S) {
                    Key putkey = new Key(encoding, k);
                    int excludeK = Subsets.encode(excludeValue(S, k));
                    double min = Double.POSITIVE_INFINITY;
                    //index corresponding to min
                    int mindex = -1;
                    for (int m : S) {
                        if (m == k) {
                            //don't want to consider this case
                            continue;
                        }
                        Key fetch = new Key(excludeK, m);
                        if (!table.containsKey(i - 2, fetch)) {
                            break;
                        }
                        double d = table.get(i - 2, fetch).dist + g.M[m][k];
                        if (d < min) {
                            min = d;
                            mindex = m;
                        }
                    }
                    culls++;
                    if (min < lowerBound) {
                        Order o = traceOrder(i - 1, new Key2(-1, k));
                        double bound = bb.lowerBound2(o);
                        System.out.println(lowerBound+" "+bound);
                        if (lowerBound > bound) {

                            //   if (min < bound) { //this if is implied from two above
                            table.add(i - 1, putkey, new Entry(min, mindex));
                            culls--;
                            // }
                        }
                    }
                }
            }
        }
        System.out.println("culled: " + culls);
        double min = Double.POSITIVE_INFINITY;
        int encodeV = Subsets.encode(V);
        for (int i : V) {
            int row = table.N - 1;
            if (!table.containsKey(row, new Key(encodeV, i))) {
                continue;
            }
            double d = table.get(row, new Key(encodeV, i)).dist + g.M[i][0];
            if (d < min) {
                min = d;
                finalIndex = i;
            }
        }
        System.out.println(lowerBound);
        System.out.println("MIN: " + min);
        return min;
    }

    Order traceOrder(int row, Key2 k) {
        int[] order = new int[row];
        row--;
        while (row >= 0) {

            int index = table.get(row, k).m;
            order[row] = index;
            k = new Key2(-1, index);
            row--;
        }
        return new Order(order);
    }

    public int[] excludeValue(int[] arr, int value) {
        //return an array equivalent to arr with value removed
        int idx = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            System.out.println("specified value not in array: " + value + ", " + Arrays.toString(arr));
            return arr;
        } else {
            int[] out = new int[arr.length - 1];
            for (int i = 0; i < idx; i++) {
                out[i] = arr[i];
            }
            for (int i = idx + 1; i < arr.length; i++) {
                out[i - 1] = arr[i];
            }
            return out;
        }
    }

}

class Key2 extends Key {

    public Key2(int S, int v) {
        super(S, v);
    }

    public int compareTo(Key2 other) {
        if (v > other.v) {
            return 1;
        } else if (v < other.v) {
            return -1;
        }
        return 0;
    }

    public int compareTo(Key other) {
        if (v > other.v) {
            return 1;
        } else if (v < other.v) {
            return -1;
        }
        return 0;
    }

}
