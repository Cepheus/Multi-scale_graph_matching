/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.text.DecimalFormat;
import java.util.Locale;

import algorithms.Constants;
import algorithms.MunkresRec;
/**
 *
 * @author Nico
 */
public class VectographCostFunction implements ICostFunction{
    	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private MunkresRec munkresRec;

	private double alpha;
        
        public VectographCostFunction (double nodeCosts, double edgeCosts, double alpha) {
            	this.munkresRec = new MunkresRec();
		this.nodeCosts = nodeCosts;
		this.edgeCosts = edgeCosts;
		this.alpha = alpha;
            
        }
        
        public double getCosts(GraphComponent start, GraphComponent end) {
            
            
            return 0.0;
        }
        
        private void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++){
                for (int j = 0; j < matrix.length; j++){
                        System.out.print(matrix[i][j]+" ");
                }
                System.out.println();
        }
		
	}
                
        /**
        * @return the cost of an edge operation
        */
	public double getEdgeCosts() {
		return (1. - this.alpha)*edgeCosts;
	}
	
	/**
	 * @return the cost of a node operation
	 */
	public double getNodeCosts() {
		return this.alpha * nodeCosts;
	}    
}
