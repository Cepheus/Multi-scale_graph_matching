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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import algorithms.Constants;
import algorithms.MunkresRec;

/**
 * @author romain
 * 
 */
public class LetterCostFunction implements ICostFunction{

	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private MunkresRec munkresRec;

	private double alpha;

	
	public LetterCostFunction(double nodeCosts, double edgeCosts, double alpha){
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
			if (!start.getComponentId().equals(Constants.EPS_ID) && start.isNode()) {
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
			
			double distance = Math.sqrt(Math.pow((xEnd - xStart), 2.)
					+ Math.pow((yEnd - yStart), 2.));
			DecimalFormat decFormat = (DecimalFormat) DecimalFormat
					.getInstance(Locale.ENGLISH);
			decFormat.applyPattern("0.00000");
			String distanceString = decFormat.format(distance);
			distance = Double.parseDouble(distanceString);
			return this.alpha * distance;
			
			
			
		}
		/**
		 * edge handling
		 */ 
		else {
			if (start.getComponentId().equals(Constants.EPS_ID)) {
				return this.edgeCosts;
			}
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				return this.edgeCosts;
			}
			return 0;

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
		return edgeCosts;
	}
	
	/**
	 * @return the cost of a node operation
	 */
	public double getNodeCosts() {
		return this.alpha * nodeCosts;
	}
}
