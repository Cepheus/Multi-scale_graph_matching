/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author mferrer
 */

import util.GraphComponent;
import algorithms.Constants;

public class MutagenCostFunction implements ICostFunction {

	private double[][] stringMatrix;

	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private double alpha;

	public MutagenCostFunction(double nodeCosts, double edgeCosts, double alpha) {
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
				chemSym2 = (String) end.getTable().get("chem");
				double l2 = chemSym2.length();
				return this.alpha * l2 * this.nodeCosts;
			}
			// end is not empty
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				chemSym2 = (String) end.getTable().get("chem");
			} else {
				// deletion
				chemSym1 = (String) start.getTable().get("chem");
				double l1 = chemSym1.length();
				return this.alpha * l1 * this.nodeCosts;
			}
			return this.alpha * this.getStringDistance(chemSym1, chemSym2);
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
			
//			if (start.getComponentId().equals(end.getComponentId())){
//				//String freqEStartString = (String)start.getTable().get("LINKS");
//				//String freqEEndString = (String)end.getTable().get("LINKS");
//				String freqEStartString = (String)start.getTable().get("valence");
//				String freqEEndString = (String)end.getTable().get("valence");
//				double freqEStart = 1;
//				if(freqEStartString != null){
//				//if(!freqEStartString.equals("null")){
//					freqEStart = Double.parseDouble(freqEStartString);
//				}
//				double freqEEnd = 1;
//				if(freqEEndString != null){
//				//if(!freqEEndString.equals("null")){
//					freqEEnd = Double.parseDouble(freqEEndString);
//				}
//				//return 0;
//				return (Math.abs(freqEStart-freqEEnd)*((1-this.alpha) * this.edgeCosts));
//			}
			
			return 0.;
		}
	}

	private double getStringDistance(String s1, String s2) {
		int n = s1.length();
		int m = s2.length();
		if (m > n) {
			String s = s1;
			s1 = s2;
			s2 = s;
			n = s1.length();
			m = s2.length();
		}
		s2 += s2;
		m *= 2.;
		this.stringMatrix = new double[n + 1][m + 1];
		this.stringMatrix[0][0] = 0;
		for (int i = 1; i <= n; i++) {
			this.stringMatrix[i][0] = this.stringMatrix[i - 1][0]
					+ this.nodeCosts;
		}
		for (int j = 1; j <= m; j++) {
			this.stringMatrix[0][j] = this.stringMatrix[0][j - 1];
		}

		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				double subst = 0.;
				if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
					subst = 0.;
				} else {
					subst = this.nodeCosts;
				}
				double m1 = this.stringMatrix[i - 1][j - 1] + subst;
				double m2 = this.stringMatrix[i - 1][j] + this.nodeCosts;
				double m3 = this.stringMatrix[i][j - 1] + this.nodeCosts;
				this.stringMatrix[i][j] = Math.min(m1, Math.min(m2, m3));
			}
		}
		double min = Double.POSITIVE_INFINITY;
		for (int j = 0; j <= m; j++) {
			double current = this.stringMatrix[n][j];
			if (current < min) {
				min = current;
			}
		}
		return min;
	}

	/**
	 * @return the cost of an edge operation
	 */
	public double getEdgeCosts() {
		return (1-this.alpha) * edgeCosts;
	}

	/**
	 * @return the cost of a node operation
	 */
	public double getNodeCosts() {
		return this.alpha * nodeCosts;
	}

}

