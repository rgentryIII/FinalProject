/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tsp;

/**
 *
 * @author Alex
 */
public class temp extends BB{
    public temp(Graph G){
        super(G);
    }
    
    public double lowerBound2(Order o){
        return super.lowerBound3(o);
    }
}
