package util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import algorithms.Constants;

public class CoilRAGCostFunction implements ICostFunction {
	
	/**
	 * the constant costs
	 */
	private double nodeCosts;

	private double edgeCosts;
	
	private double alpha;

	/** the vector feature*/
	private Vector<Double> vect1;// = new double[64];
	private Vector<Double> vect2;// = new double[64];

	

	public CoilRAGCostFunction(double nodeCosts, double edgeCosts, double alpha) {
		this.nodeCosts = nodeCosts;
		this.edgeCosts = edgeCosts;
		this.alpha = alpha;
		vect1=new Vector<Double>();
		vect2=new Vector<Double>();
	}

	@Override
	public double getCosts(GraphComponent start, GraphComponent end) {
		// TODO Auto-generated method stub
		vect1.clear();
		vect2.clear();
		
		
		/**
		 * node handling
		 */
		String  tmp;
		double d;
		Enumeration values;
		if (start.isNode() || end.isNode()) {
			if (!start.getComponentId().equals(Constants.EPS_ID)) {
				 values = start.getTable().elements();
				while(values.hasMoreElements()==true){
					tmp =(String)values.nextElement();
					try{
						d = Double.parseDouble(tmp);
					}catch (NumberFormatException e){
						d=0;
					}
					vect1.add(d);
				}
			} else {
				// insertion
				return this.alpha * this.nodeCosts;
			}
			
			
			if (!end.getComponentId().equals(Constants.EPS_ID)) {
				 values = end.getTable().elements();
					while(values.hasMoreElements()==true){
						tmp =(String)values.nextElement();
						try{
							d = Double.parseDouble(tmp);
						}catch (NumberFormatException e){
							d=0;
						}
						vect2.add(d);
					}
			} else {
				// deletion
				return this.alpha * this.nodeCosts;
			}
			
			// substitution
			return this.alpha * this.calculateDistance();
			
		}else {
			/**
			 * edge handling
			 */
			
			if (start.getComponentId().equals(Constants.EPS_ID) || end.getComponentId().equals(Constants.EPS_ID)) {
				return (1 - this.alpha) * this.edgeCosts;
			}
			else {
				String s1 = (String) start.getTable().get("boundary");
				String s2 = (String) end.getTable().get("boundary");
				Integer v1 = Integer.parseInt(s1);
				Integer v2 = Integer.parseInt(s2);
				return (1 - this.alpha) * Math.abs(v1-v2);
			}
		}
		
			
			
	
	}

	private double calculateDistance() {
		// TODO Auto-generated method stub
		double toSquare = 0;
		double temp1=0;
		double temp2=0;
		double temp3=0;
		for (int i = 0; i < vect1.size(); i++) {
			temp1 = vect1.get(i);
			temp2 = vect2.get(i);
			temp3 = Math.pow((temp2 - temp1), 2.);
			toSquare += temp3;
		}
		//Arrays.fill(this.histoX, 0.);
		//Arrays.fill(this.histoY, 0.);
		return Math.sqrt(toSquare);
		
	}

	@Override
	public double getEdgeCosts() {
		return (1. -this.alpha) * edgeCosts;
	}

	@Override
	public double getNodeCosts() {
		// TODO Auto-generated method stub
		return this.alpha * nodeCosts;
	}
	
	

}
