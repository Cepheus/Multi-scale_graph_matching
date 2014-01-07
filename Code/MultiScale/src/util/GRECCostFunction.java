//
//  GRECCostFunction.java
//  GraphMatching
//
//  Created by Miquel Ferrer Sumsi on 17/05/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

/**
 * 
 */
package util;

import java.text.DecimalFormat;
import java.util.Locale;

import algorithms.Constants;
import algorithms.MunkresRec;

/**
 * @author kriesen
 * 
 */
public class GRECCostFunction implements ICostFunction{

	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private MunkresRec munkresRec;

	private double alpha;

	
	public GRECCostFunction(double nodeCosts, double edgeCosts, double alpha){
		this.munkresRec = new MunkresRec();
		this.nodeCosts = nodeCosts;
		this.edgeCosts = edgeCosts;
		this.alpha = alpha;
	}


	/**
	 * @return costs of a distortion between 
	 * @param start and @param end
	 */
	public double getCosts(GraphComponent start, GraphComponent end) {
		/**
		 * node handling
		 */ 
		if (start.isNode() || end.isNode()) {
			double xStart;
			double yStart;
			String startType;
			double xEnd;
			double yEnd;
			String endType;
			// start is not empty
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				startType = (String) start.getTable().get("type");
				String xStartString = (String) start.getTable().get("x");
				xStart = Double.parseDouble(xStartString);
				String yStartString = (String) start.getTable().get("y");
				yStart = Double.parseDouble(yStartString);				
			} else {
				// insertion
				return this.alpha * this.nodeCosts;
			}
			// end is not empty
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				endType = (String) end.getTable().get("type");
				String xEndString = (String) end.getTable().get("x");
				xEnd = Double.parseDouble(xEndString);
				String yEndString = (String) end.getTable().get("y");
				yEnd = Double.parseDouble(yEndString);
			} else {
				// deletion
				return this.alpha * this.nodeCosts;
			}
			if (startType.equals(endType)){
				double distance = Math.sqrt(Math.pow((xEnd - xStart), 2.)
						+ Math.pow((yEnd - yStart), 2.));
				DecimalFormat decFormat = (DecimalFormat) DecimalFormat
						.getInstance(Locale.ENGLISH);
				decFormat.applyPattern("0.00000");
				String distanceString = decFormat.format(distance);
				distance = Double.parseDouble(distanceString);
				return this.alpha * distance;
			} else {
				return this.alpha * (2*this.nodeCosts);
			}
			
		}
		/**
		 * edge handling
		 */ 
		else {
			int startFreq;
			int endFreq;
			if (start.getComponentId().equals(Constants.EPS_ID)) {
				endFreq = Integer.parseInt((String) end.getTable().get("frequency"));
				return (1-this.alpha)*endFreq * this.edgeCosts;
			}
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				startFreq = Integer.parseInt((String) start.getTable().get("frequency"));
				return (1-this.alpha)*startFreq * this.edgeCosts;
			}
			startFreq = Integer.parseInt((String) start.getTable().get("frequency"));
			endFreq = Integer.parseInt((String) end.getTable().get("frequency"));
			int n = startFreq + endFreq;
			double[][] matrix = new double[n][n];
			for (int i = 0; i < startFreq; i++){
				String startType = (String) start.getTable().get("type"+i);
				for (int j = 0; j < endFreq; j++){
					String endType = (String) end.getTable().get("type"+j);
					if (startType.equals(endType)){
						matrix[i][j] = 0;
					} else {
						matrix[i][j] = 2*this.edgeCosts;
					}
				}
			}
			for (int i = startFreq; i < startFreq+endFreq; i++){
				for (int j = 0; j < endFreq; j++){
					if (i-startFreq == j){
						matrix[i][j] = this.edgeCosts;
					} else {
						matrix[i][j] = Double.POSITIVE_INFINITY;
					}
				}
			}
			for (int i = 0; i < startFreq; i++){
				for (int j = endFreq; j < startFreq + endFreq; j++){
					if (j-endFreq == i){
						matrix[i][j] = this.edgeCosts;
					} else {
						matrix[i][j] = Double.POSITIVE_INFINITY;
					}
				}
			}
			return (1-this.alpha)*this.munkresRec.getCosts(matrix);
			
		}
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
