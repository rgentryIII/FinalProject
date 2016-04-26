/*
 *class for computing the subsets of size k of a given set. Note: I did not
 * write the important method combinations(int,int[]). Credit goes to afsantos
 * who contributes this to a stackoverflow.com thread:
 * http://stackoverflow.com/questions/4504974/how-to-iteratively-generate-k-elements-subsets-from-a-set-of-size-n-in-java
 * 
 * I have modified his implementation by converting each subset into an integer
 * representation- this tremendously reduces memory use at the cost of 
 * compression time and limitation of what values can be in the subsets.
 * 
 * Alex Ellison
 */
package tsp;

import java.util.ArrayList;
import java.util.Arrays;

public class Subsets {

    public static int[] combinations(int k, int[] set) {
        // binomial(N, K)
        int c = (int) binomial(set.length, k);
        // where all sets are stored
        int[] out = new int[c];
        // the k indexes (from set) where the red squares are
        // see image above
        int[] ind = k < 0 ? null : new int[k];
        // initialize red squares
        for (int i = 0; i < k; ++i) {
            ind[i] = i;
        }
        // for every combination
        for (int i = 0; i < c; ++i) {
            // get its elements (red square indexes)
            int[] precompressed = new int[k];
            for (int j = 0; j < k; ++j) {
                precompressed[j] = set[ind[j]];
            }
            // update red squares, starting by the last
            int x = ind.length - 1;
            boolean loop;
            do {
                loop = false;
                // move to next
                ind[x] = ind[x] + 1;
                // if crossing boundaries, move previous
                if (ind[x] > set.length - (k - x)) {
                    --x;
                    loop = x >= 0;
                } else {
                    // update every following square
                    for (int x1 = x + 1; x1 < ind.length; ++x1) {
                        ind[x1] = ind[x1 - 1] + 1;
                    }
                }
            } while (loop);
            out[i] = encode(precompressed);
        }
        return out;
    }

    private static long binomial(int n, int k) {
        //returns n choose k
        if (k < 0 || k > n) {
            return 0;
        }
        if (k > n - k) {    // take advantage of symmetry
            k = n - k;
        }
        long c = 1;
        for (int i = 1; i < k + 1; ++i) {
            c = c * (n - (k - i));
            c = c / i;
        }
        return c;
    }

    public static int encode(int[] arr) {
        /*
         the limitation to this method is how high a bit represenation we can
         get away with. For int we can't store higher than a value of 31 in a 
         set since 2^31 is the highest positive bit (assuming two's comp.)
         but a long should get away with 63 for the same reason (in practice
         seems to die with any value>62
         */
        int out = 0;
        for (int i : arr) {
            out += 1 << i;
        }
        return out;
    }

    public static int[] decode(long n) {
        //no way to tell how many numbers are stored in a number readily, so
        //we use a flexible list then convert to array
        ArrayList<Integer> list = new ArrayList<Integer>();
        int idx = 0;
        while (n > 0) {
            if (n % 2 == 1) {
                list.add(idx);
            }
            idx++;
            n /= 2;
        }
        int[] out = new int[list.size()];
        idx = 0;
        for (int i : list) {
            out[idx] = i;
            idx++;
        }
        return out;
    }
}