//
//  CoilCostFunction.java
//  GraphMatching
//
//  Created by Miquel Ferrer Sumsi on 25/06/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//  Prova de CVS

/** 
 * 
 */
package util;

import java.text.DecimalFormat;
import java.util.Locale;

import algorithms.Constants;


/**
 * @author mferrer
 * 
 */
public class CoilCostFunction implements ICostFunction {

	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;

	private double edgeSubstitutionCosts = 0.0;

	private double alpha;

	
	public CoilCostFunction(double nodeCosts, double edgeCosts, double alpha){
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
			double xEnd;
			double yEnd;
			// start is not empty
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
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
				return (1-this.alpha)*this.edgeCosts;
			}
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				return (1-this.alpha)*this.edgeCosts;
			}
			return (1-this.alpha)*this.edgeSubstitutionCosts;
		}
	}

	/**
	 * @return the cost of an edge operation
	 */
	public double getEdgeCosts() {
		return (1-this.alpha)*edgeCosts;
	}
	
	/**
	 * @return the cost of a node operation
	 */
	public double getNodeCosts() {
		return this.alpha * nodeCosts;
	}

}
