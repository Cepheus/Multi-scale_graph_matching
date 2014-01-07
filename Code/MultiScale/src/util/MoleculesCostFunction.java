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
 * @author kriesen
 * 
 */
public class MoleculesCostFunction implements ICostFunction{

	/**
	 * the constant costs
	 */
	private double nodeCosts;
	
	private double edgeCosts;

	private double alpha;


	public MoleculesCostFunction(double nodeCosts, double edgeCosts, double alpha) {
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
			String chemSym1;
			String chemSym2;
			// start is not empty
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				chemSym1 = (String) start.getTable().get("chem");
			} else {
				// insertion
				return this.alpha * this.nodeCosts;
			}
			// end is not empty
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				chemSym2 = (String) end.getTable().get("chem");
			} else {
				// deletion
				return this.alpha *this.nodeCosts;
			}
			if (chemSym1.equals(chemSym2)) {
				return 0;
			} else {
				return this.alpha * 2 * this.nodeCosts;
			}
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
			return 0.;
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
