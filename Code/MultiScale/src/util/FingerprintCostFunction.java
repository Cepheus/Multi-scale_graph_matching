//
//  MolecCostFunction.java
//  GraphMatching
//
//  Created by Miquel Ferrer Sumsi on 21/05/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//

package util;

//import java.text.DecimalFormat;
//import java.util.LinkedList;
//import java.util.Locale;


import algorithms.Constants;
//import algorithms.MainStar;

/**
 * @author romain
 * 
 */
public class FingerprintCostFunction implements ICostFunction{

	/**
	 * the constant costs
	 */
	private double nodeCosts;
	
	private double edgeCosts;

	private double alpha;


	public FingerprintCostFunction(double nodeCosts, double edgeCosts, double alpha) {
            this.nodeCosts = nodeCosts;
            this.edgeCosts = edgeCosts;
            this.alpha = alpha;
	}

	/**
	 * @return costs of a distortion between
	 * @param start
	 *            and
	 * @param end
	 */
	public double getCosts(GraphComponent start, GraphComponent end) {
		/**
		 * node handling
		 */
		if (start.isNode() || end.isNode()) {
			double freqStart=0;
			double freqEnd=0;
			
			// start is not empty
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				String freqStartString = (String) start.getTable().get("x");
				freqStart = Double.parseDouble(freqStartString);
			} else {
				// insertion
				return this.alpha * this.nodeCosts;
			}
			// end is not empty
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				String freqEndString = (String) end.getTable().get("y");
				freqEnd = Double.parseDouble(freqEndString);
			
			} else {
				// deletion
				return this.alpha *this.nodeCosts;
			}
			
		
			//substitution
			return this.alpha * Math.abs(freqStart-freqEnd);
		}
		/**
		 * edge handling
		 */
		else {
			if (start.getComponentId().equals(Constants.EPS_ID)) {
				return (1-this.alpha) * this.edgeCosts;
			}
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				return (1-this.alpha) * this.edgeCosts;
			}
			
			String freqStartString = (String) start.getTable().get("angle");
			double freqStart = Double.parseDouble(freqStartString);
			String freqEndString = (String) end.getTable().get("angle");
			double freqEnd = Double.parseDouble(freqEndString);
			double res = Math.abs(freqStart-freqEnd);
			return (1-this.alpha) * res;
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
		return nodeCosts;
	}

}
