/**
 * 
 */
package util;

import java.util.Arrays;
import java.util.LinkedList;

import algorithms.Constants;

/**
 * @author kriesen
 * 
 */
public class ImageCostFunction implements ICostFunction {

	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private double alpha;

	/** the color-histograms (image-DB) */
	private double[] histoX = new double[64];

	private double[] histoY = new double[64];

	public ImageCostFunction(double nodeCosts, double edgeCosts, double alpha) {
		this.nodeCosts = nodeCosts;
		this.edgeCosts = edgeCosts;
		this.alpha = alpha;
	}

	/**
	 * @see util.ICostFunction#getCosts(util.GraphComponent,
	 *      util.GraphComponent)
	 */
	public double getCosts(GraphComponent start, GraphComponent end) {
		/**
		 * node handling
		 */
		
		if (start.isNode() || end.isNode()) {
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				for (int i = 1; i < 65; i++) {
					String histoString = (String) start.getTable().get(
							"histo" + i);
					double d = Double.parseDouble(histoString);
					this.histoX[i - 1] = d;
				}
			} else {
				// insertion
				return this.alpha * this.nodeCosts;
			}
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				for (int i = 1; i < 65; i++) {
					String histoString = (String) end.getTable().get(
							"histo" + i);
					double d = Double.parseDouble(histoString);
					this.histoY[i - 1] = d;
				}
			} else {
				// deletion
				return this.alpha * this.nodeCosts;
			}
			// substitution
			return this.alpha * this.calculateDistance(this.histoX, this.histoY);
		} else {
		
			if (start.getComponentId().equals(Constants.EPS_ID)) {
				return (1. -this.alpha) * this.edgeCosts;
			} 
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				return (1. -this.alpha) * this.edgeCosts;
			}
			return 0.0;
		}
	}

	/**
	 * @see util.ICostFunction#getEdgeCosts()
	 */
	public double getEdgeCosts() {
		return (1. -this.alpha) * edgeCosts;
	}

	/**
	 * @return the cost of a node operation
	 */
	public double getNodeCosts() {
		return this.alpha * nodeCosts;
	}

	/**
	 * calculates the distance between two 64-dim vectors (LeSaux-DB)
	 */
	private double calculateDistance(double[] histX, double[] histY) {
		double toSquare = 0;
		for (int i = 0; i < 64; i++) {
			double temp1 = histX[i];
			double temp2 = histY[i];
			double temp3 = Math.pow((temp2 - temp1), 2.);
			toSquare += temp3;
		}
		Arrays.fill(this.histoX, 0.);
		Arrays.fill(this.histoY, 0.);
		return Math.sqrt(toSquare);
	}

}
