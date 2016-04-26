//(c) Alex Ellison 2014
package tsp;

/*
 * dynamic programming solution to metric travelling salesman problem
 */
import java.util.*;

public class Dynamic {

    Graph g;
    /*
     * I wanted to do this with a hashmap, but if I make the map a hash not
     * a tree, then the hashmap thinks it doesn't have keys I can observe it
     * has. However, the tree runs fine so we're sticking with that.
    
     * RECORD: 22 cities with 99.53% memory use of 1.509 gig heap (-Xms1600m)
     computed in 130.6 sec
     */
    public Table table;
    int finalIndex = -1;
    int N;

    public Dynamic(Graph G) {
        g = G;
        N = G.M.length;
        table = new Table(N - 1);
    }

    public Order minTour() {
        long time = System.nanoTime();

        //running minPathLength() builds the table
        double dist = minPathLength();
        System.out.println(table.size);
        //constructing the set that will be in all elements of the table's top
        //row
        int[] V = new int[g.M.length - 1];
        for (int i = 0; i < V.length; i++) {
            V[i] = i + 1;
        }
        int encodeV = Subsets.encode(V);
        //we know the optimal table entry's finalIndex, so key is trivial
        Key key = new Key(encodeV, finalIndex);

        //sequentially considering entries of the table, looking at the
        //minimizing index m stored there and then looking up the entry in the
        //next table row specified by a terminus index = m, and an set encoding
        //of V minus the element we just added to the tour
        int nextIndex = finalIndex;
        //ordering will represent the tour
        ArrayList<Integer> ordering = new ArrayList<Integer>();
        int row = table.N - 1;
        while (V.length > 0) {
            ordering.add(nextIndex);
            V = excludeValue(V, nextIndex);
            nextIndex = table.get(row, key).m;
            row--;
            key = new Key(Subsets.encode(V), nextIndex);
        }
        ordering.add(0);
        //convert arraylist to array
        int[] arr = new int[ordering.size()];
        int idx = 0;
        for (int i : ordering) {
            arr[idx] = i;
            idx++;
        }
        //print out time information
        long dt = System.nanoTime() - time;
        dt /= 1000000;
        System.out.println("dynamic programming solution solution found in " + dt + " ms");
        System.out.println("mem use: " + TSP.memUsedFraction());
        return new Order(arr, dist);
    }

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
                        
                        double d = table.get(i - 2, fetch).dist + g.M[m][k];
                        if (d < min) {
                            min = d;
                            mindex = m;
                        }
                    }
                    table.add(i - 1, putkey, new Entry(min, mindex));
                }
            }
        }

        double min = Double.POSITIVE_INFINITY;
        int encodeV = Subsets.encode(V);
        for (int i : V) {
            int row = table.N - 1;
            double d = table.get(row, new Key(encodeV, i)).dist + g.M[i][0];
            if (d < min) {
                min = d;
                finalIndex = i;
            }
        }
        System.out.println("MIN: "+min);
        return min;
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

class Key implements Comparable<Key> {
    /*
     a key to the dynamic programming table needs to uniquely represent the
     corresponding set and index it ends at. The terminus index is easy,
     the set is represented by an integer encoding determined outside this
     class.
     */

    //encoding of subset
    int S;
    //terminating index
    int v;

    public Key(int S, int v) {
        this.S = S;
        this.v = v;
    }

    public int compareTo(Key other) {
        if (v > other.v) {
            return 1;
        } else if (v < other.v) {
            return -1;
        }
        if (S > other.S) {
            return 1;

        } else if (S < other.S) {
            return -1;
        }
        return 0;
    }
    
    public int compareTo(Key2 other){
         if (v > other.v) {
            return 1;
        } else if (v < other.v) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object other){
        Key k = (Key)other;
        return k.S==S && k.v==v;
    }
    //necessary for hashcode implementation of Row
    @Override
    public int hashCode(){
        return S;
    }
    public void print() {
        System.out.println("KEY: ");
        System.out.println("terminus " + v);
        System.out.println("encoding " + S);
        System.out.println(Arrays.toString(Subsets.decode(S)));
    }

    public void print(String id) {
        System.out.println("KEY: " + id);
        System.out.println("terminus " + v);
        System.out.println("encoding " + S);
        System.out.println(Arrays.toString(Subsets.decode(S)));
    }
}

class Entry {

    //represents a table entry in dynamic programming table
    //distance of path from 0 thru S ending at vertex of index m
    double dist;
    int m;

    public Entry(double dist, int m) {
        this.dist = dist;
        this.m = m;
    }

}
