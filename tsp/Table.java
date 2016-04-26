//(c) Alex Ellison 2014
package tsp;

/**
 * table class for dynamic programming. 
 */
public class Table {
    private Row[] rows;
    int size = 0;
    final int N;
    public Table(int n){
        rows = new Row[n];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new Row();
        }
        N = n;
    }
    
    public void add(int row,Key k, Entry e){
        rows[row].put(k, e);
        size++;
    }
    
    public Entry get(int row, Key k){
        return rows[row].get(k);
    }
    
    public void clear(int row){
        int rowSize = rows[row].size();
        rows[row].clear();
        size-= rowSize;
    }
    
    public boolean containsKey(int row, Key key){
        return rows[row].containsKey(key);
    }
    
    public Row[] rows(){
        return rows;
    }
}
