//(c) Alex Ellison 2014
package tsp;

/**
 * small class for storing an order of indices that outlines a path through a
 * graph. This path could be a cycle but not necessarily- there is a method for
 * computing the weights of either scenario.
 *
 * This class is used in the Dynamic programming solution and the branch and
 * bound technique.
 */
public class Order implements Comparable<Order> {

    int[] indices = null;
    double weight = -1;

    public Order(int[] arr) {
        //constructor used in the branch and bound algorithm 
        indices = arr;
    }

    public Order(int[] arr, double w) {
        indices = arr;
        weight = w;
    }

    public double stWeight(Graph G) {
        //returns the weight through G along the path, excluding the link
        //from last back to first index (from s to t)
        double out = 0;
        
        for (int i = 0; i < indices.length - 1; i++) {
            out += G.M[indices[i]][indices[i + 1]];
        }
        return out;
    }
    
    public double cycleWeight(Graph G) {
        double out = 0;
        for (int i = 0; i < indices.length; i++) {
            out += G.M[indices[i]][indices[(i + 1) % indices.length]];
        }
        return out;
    }

    public void print() {
        System.out.println("Ordering: weight = " + weight + " indices:");
        for (int i : indices) {
            System.out.print(i + " ");
        }
        System.out.println();
    }

    public int compareTo(Order other) {
        return Double.compare(weight, other.weight);
    }
}