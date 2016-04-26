//(c) Alex Ellison
package tsp;

/**
 * modified version of the dynamic programming algorithm in that it's straight
 * up memoized recursion with the idea I can drop table rows when memory fills
 */
import java.util.*;

public class Memo extends Dynamic {

    int tableCap = 11631998;

    public Memo(Graph G) {
        super(G);
    }

    public Memo(Graph G, int cap) {
        super(G);
        tableCap = cap;
    }

    public double min() {

        double best = Double.POSITIVE_INFINITY;
        int[] S = new int[N - 1];

        for (int i = 0; i < S.length; i++) {
            S[i] = i + 1;
        }
        for (int i : S) {
            double temp = min(S, i) + g.M[0][i];
            if (temp < best) {
                best = temp;
            }
        }
        return best;
    }

    public double min(int[] S, int i) {
        //takes a subset of indices that includes i and never zero. returns
        //minimum path length from zero thru S ending at i
        manageMem();

        int row = S.length - 1;
        //base case:
        if (row == 0) {
            return g.M[0][i];
        }
        //attempt to retrieve from table

        int encoding = Subsets.encode(S);
        Key key = new Key(encoding, i);
        if (table.containsKey(row, key)) {
            return table.get(row, key).dist;
        }
        //table look up failed
        double best = Double.POSITIVE_INFINITY;
        int bestJ = -1;
        for (int j : S) {
            if (j != i) {
                double temp = min(excludeValue(S, i), j) + g.M[i][j];
                if (temp < best) {
                    best = temp;
                    bestJ = j;
                }
            }
        }
        table.add(row, key, new Entry(best, bestJ));
        return best;
    }

    public void manageMem() {
        //in the event that the memory starts overfilling, we jetison the
        //largest row in the table to save space - the beauty of memoized 
        //recursion instead of dynamic programming is that it is flexible
        //enough to recover lost progress (that said it is slower even if 
        //no entries are deleted (factor of 1.5~2.0)
        if (table.size > tableCap) {
            int max = 0;
            Row row = null;
            int i = 0;
            int imax = 0;
            for (Row r : table.rows()) {
                if (r.size() > max) {
                    max = r.size();
                    row = r;
                    imax = i;
                }
                i++;
            }
            table.clear(imax);
        }
    }

    public boolean contains(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }
}
