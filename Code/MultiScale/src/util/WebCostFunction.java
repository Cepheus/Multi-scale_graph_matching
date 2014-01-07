//
//  WebCostFunction.java
//  GraphMatching
//
//  Created by Miquel Ferrer Sumsi on 16/01/08.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

package util;

//import java.text.DecimalFormat;
//import java.util.LinkedList;
//import java.util.Locale;

import algorithms.Constants;

//import algorithms.Main;

/**
 * @author kriesen
 * 
 */
public class WebCostFunction implements ICostFunction{

	/**
	 * the constant costs
	 */
	private double nodeIns = 1.0;
	
	private double nodeDel = 1.0;

	private double edgeIns = 1.0; //0.5;
	
	private double edgeDel = 1.0; //0.5;

	private double edgeSub = 1.0;

	private double edgeSubEq = 0.0001;
	
	private double edgeCosts = 0.5;
	
	private double nodeSubEq = 0.0001;
	
	private double nodeSub = 1000.0;

	/**
	 * @return costs of a distortion between 
	 * @param start and @param end
	 */
	public double getCosts(GraphComponent start, GraphComponent end) {
		/**
		 * node handling
		 */ 
		if (start.isNode() || end.isNode()) {
			double freqStart;
			double freqEnd;
			String nodeIdStart, nodeIdEnd, freqStartString, freqEndString;
			// start is not empty
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				nodeIdStart = start.getId();
				nodeIdStart = start.getComponentId();
				freqStartString = (String) start.getTable().get("FREQUENCY");
				freqStart = Double.parseDouble(freqStartString);
			} else {
				// insertion
				return this.nodeIns;
			}
			// end is not empty
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				nodeIdEnd = end.getId();
				nodeIdEnd = end.getComponentId();
				freqEndString = (String) end.getTable().get("FREQUENCY");
				freqEnd = Double.parseDouble(freqEndString);
			} else {
				// deletion
				return this.nodeDel;
			}
			//substitution
			double distance;
			if(nodeIdStart.equals(nodeIdEnd)){
				if (freqStart == freqEnd){
					distance = 0;
				}else{
					distance = Math.abs(freqStart-freqEnd)*this.nodeSubEq;
				}
				//distance=0;
			}else{
				distance = this.nodeSub;
			}
			return distance;
		}
		/**
		 * edge handling
		 */ 
		else {
			if (start.getComponentId().equals(Constants.EPS_ID)) {
				return this.edgeIns;
			}
			if (end.getComponentId().equals(Constants.EPS_ID)) {
				return this.edgeDel;
			}
			if (start.getComponentId().equals(end.getComponentId())){
				//String freqEStartString = (String)start.getTable().get("LINKS");
				//String freqEEndString = (String)end.getTable().get("LINKS");
				String freqEStartString = (String)start.getTable().get("TEXT");
				String freqEEndString = (String)end.getTable().get("TEXT");
				double freqEStart = 1;
				if(freqEStartString != null){
				//if(!freqEStartString.equals("null")){
					freqEStart = Double.parseDouble(freqEStartString);
				}
				double freqEEnd = 1;
				if(freqEEndString != null){
				//if(!freqEEndString.equals("null")){
					freqEEnd = Double.parseDouble(freqEEndString);
				}
				//return 0;
				return (Math.abs(freqEStart-freqEEnd)*this.edgeSubEq);
			}
			return this.edgeSub;
		}
	}

	/**
	 * @return the cost of an edge operation
	 */
	public double getEdgeCosts() {
		return edgeCosts;
	}

	
	// added by Zeina 
	@Override
	public double getNodeCosts() {
		// TODO Auto-generated method stub
		return 0;
	}
}
