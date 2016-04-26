//(c) Alex Ellison 2014
package tsp;

/**
 *
 *wrapper class that enables me to make an array of treemaps, which are generic
 * 
 * Furthermore, this lets me compartmentalize my dynamic programming solution
 * into something that resembles the conventional table in that each row gets
 * its own object (here rows are segregated by size of subset). Particularly
 * for a tree implementation in which access speed depends on size, this is 
 * useful.
 * 
 * Additionally, I can get a sense of which rows take up more memory, and 
 * in the event of maxing out memory we can drop rows and piece the solution
 * together later (which will involve small recomputation if there are rows
 * dropped).
 * 
 * April 2015: Went back and figured out how to make the HashMap implementation
 * work, boosts dynamic programming by about a factor of 3 in speed for n=~20
 */
import java.util.*;

public class Row extends TreeMap<Key,Entry> implements Comparable<Row>{
    
    public Row() {
        super();
    }
    
    
    
    public int compareTo(Row other)
    {
        int mySize=size();
        int otherSize=other.size();
      
        if (mySize > otherSize) {
            return 1;
        }
        if (mySize == otherSize) {
            return 0;
        }
        return -1;
    }
}
