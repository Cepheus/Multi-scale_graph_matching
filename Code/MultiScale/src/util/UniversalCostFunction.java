package util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import algorithms.Constants;

/**
 * @author Romain Raveaux
 * 
 */


public class UniversalCostFunction implements ICostFunction {

	/**
	 * the constant costs
	 */
	//private double nodeCosts;

	//private double edgeCosts;

	private double alpha;


	//(double nodeCosts, double edgeCosts, double alpha){
	public UniversalCostFunction(double alpha) {
		this.alpha = alpha;
		//vect1=new Vector<Double>();
		//vect2=new Vector<Double>();
	}

	@Override
	public double getCosts(GraphComponent start, GraphComponent end) {
		// TODO Auto-generated method stub


		/**
		 * node handling
		 */
		String  tmp;
		double d;
		Enumeration values;
		//Si c'est deux noeud
		if (start.isNode() || end.isNode()) {
			//si c'est une substitution
			if (!start.getComponentId().equals(Constants.EPS_ID) &&
					!end.getComponentId().equals(Constants.EPS_ID) 	
					) {
				double cost = CalculateCost(start.getTable(),end.getTable() ,start);
				return this.alpha*cost;
			}//fin substitution

			//suppression
			if (!start.getComponentId().equals(Constants.EPS_ID) &&
					end.getComponentId().equals(Constants.EPS_ID) 	==true
					) {
				double cost =1;// CalculateCost(start.getTable() ,start);
				return this.alpha+( this.alpha*cost);
			}//fin suppresion


			//insertion
			if (start.getComponentId().equals(Constants.EPS_ID) &&
					end.getComponentId().equals(Constants.EPS_ID) 	==false
					) {
				double cost = 1;//CalculateCost(end.getTable(),end );
				return this.alpha+(this.alpha*cost);
			}//fin insertion



		}else{ //fin des deux noeuds


		/**
		 * edges handling
		 */

		//Si c'est deux edges
		//if (start.isNode() == false || end.isNode() == false) {
			//si c'est une substitution
			if (end.getComponentId()!= null && start.getComponentId()!= null &&
					!start.getComponentId().equals(Constants.EPS_ID) &&
					!end.getComponentId().equals(Constants.EPS_ID) 	
					) {
				double cost = CalculateCost(start.getTable(),end.getTable() ,start);

				//Romain ajout pour le sens de l'arc
				if(!start.getComponentId().equals(Constants.EPS_ID) &&
						!end.getComponentId().equals(Constants.EPS_ID)){
					if(      ((Edge)start).isDirected() == true &&   ((Edge)start).isInverted() == true      ){
						cost+= (1. -this.alpha)/2.0;
						return (1. -this.alpha) *cost;
					}

				}
				return (1. -this.alpha) *cost;
			}//fin substitution

			//suppression
			if (end.getComponentId()!= null &&	end.getComponentId().equals(Constants.EPS_ID) 	==true
					) {
				double cost =1;// CalculateCost(start.getTable() ,start);
				//return (1. -this.alpha) +((1. -this.alpha) *cost);
				return (1. -this.alpha) *cost;
			}//fin suppresion


			//insertion
			if (start.getComponentId()!= null && start.getComponentId().equals(Constants.EPS_ID) ==true) {
				double cost = 1;//CalculateCost(end.getTable() ,end);
				//return (1. -this.alpha) +((1. -this.alpha) *cost);
				return (1. -this.alpha) *cost;
			}//fin insertion



		} //fin des deux arcs


		return Double.MIN_VALUE;
	}

	private double CalculateCost(Hashtable table, GraphComponent GC) {
		// TODO Auto-generated method stub
		double cost=0.0; 
		Enumeration values1 = table.elements();
		int i=0;
		int size=table.size();
		String v1;
		Double d1;
		int ecart = 0;
		if(GC.isNode() == true){
			ecart=0;
		}else{
			ecart = 2;
		}
		
		while(values1.hasMoreElements()==true){
			i++;
			v1 =(String)values1.nextElement();
			if(size-i >=ecart){
				try{
					d1 = Double.parseDouble(v1);

					cost+= Math.abs(d1);
				}catch (NumberFormatException e){
					cost++; 
				}
			}
		}
		
		if(table.size() == 0) return cost;
		
		return cost/(double)(table.size()-ecart);
	}

	private double CalculateCost(Hashtable table, Hashtable table2, GraphComponent GC) {
		// TODO Auto-generated method stub
		double cost=0.0; 
		Enumeration values1 = table.elements();
		Enumeration values2 = table2.elements();
		int i=0;
		int size=table.size();
		int ecart = 0;
		if(GC.isNode() == true){
			ecart=0;
		}else{
			ecart = 2;
		}
		if(table.size() != table2.size() ) return Double.MIN_VALUE;

		String v1,v2;
		Double d1,d2;
		while(values1.hasMoreElements()==true){
			i++;
			v1 =(String)values1.nextElement();
			v2 =(String)values2.nextElement();
			if(size-i >=ecart){
				try{
					d1 = Double.parseDouble(v1);
					d2 = Double.parseDouble(v2);
					cost+= Math.abs(d1-d2);
				}catch (NumberFormatException e){
					if(v1.equals(v2) ==false) cost++; 
				}
			}
		}
		
		if(table.size() == 0) return cost;
		
		return cost/(double)(table.size()-ecart);
	}

	@Override
	public double getEdgeCosts() {
		// TODO Auto-generated method stub
		return alpha;
	}

	@Override
	public double getNodeCosts() {
		// TODO Auto-generated method stub
		return (1. -this.alpha) ;
	}

}
