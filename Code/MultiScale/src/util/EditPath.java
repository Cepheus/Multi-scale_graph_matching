package util;

import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;

import algorithms.Constants;
import algorithms.LAPJonkerVolgenantAssignment;
import algorithms.Munkres;
import algorithms.MunkresRec;


/**
 * @author Romain Raveaux
 * 
 */
public class EditPath implements Comparable {



	/**
	 * the total costs of this path
	 */
	private double totalCosts;

	static public int NoHeuristic=0;
	static public int NoAssigmentHeuristic=1;
	static public int MunkresAssigmentHeuristic=2;
	static public int LAPAssigmentHeuristic=3;
	//public static int BestFirstAssigmentHeuristic = 4;


	private int noOfDistortedNodes;

	private String editPathId;

	private double HeuristicCosts;
	private boolean isheuristiccomputed;


	public double getHeuristicCosts() {
		return HeuristicCosts;
	}

	public int getNoOfDistortedNodes()
	{
		return noOfDistortedNodes;
	}

	public void setID(String editPathId)
	{
		this.editPathId = editPathId;
	}

	public String getID()
	{
		return editPathId;
	}

	public void setNoOfDistortedNodes(int noOfDistortedNodes)
	{
		this.noOfDistortedNodes = noOfDistortedNodes;
	}

	public void setHeuristicCosts(double heuristicCosts) {
		HeuristicCosts = heuristicCosts;
	}


	public double ComputeHeuristicCosts(int heuristicmethod) {

		if(this.isheuristiccomputed==false){
			if(heuristicmethod==NoAssigmentHeuristic) this.ComputeHeuristicCosts(); // estimating the cost of the edit path from the current node till the leaf nodes
			if(heuristicmethod==MunkresAssigmentHeuristic) ComputeHeuristicCostsAssignmentMunkres();//this.ComputeHeuristicCostsAssignment();
			if(heuristicmethod==LAPAssigmentHeuristic) this.ComputeHeuristicCostsAssignmentLAP();
			//if(heuristicmethod==BestFirstAssigmentHeuristic) this.ComputeHeuristicCostsAssignmentBestFirst();
			if(heuristicmethod==NoHeuristic) HeuristicCosts=0;
			isheuristiccomputed=true;
		}
		return HeuristicCosts;

	}

	private void ComputeHeuristicCostsAssignmentBestFirst() {
		// TODO Auto-generated method stub
		HeuristicCosts=0.0;
		int n1,n2;
		int e1,e2;
		n1 = this.unUsedNodes1.size();
		n2 = this.unUsedNodes2.size();

		e1 = this.unUsedEdges1.size();
		e2 = this.unUsedEdges2.size();
		//System.out.println("********************************************");

		double leastexpensivenodesub = this.ComputeMinSubstituionBestMatch(unUsedNodes1,unUsedNodes2);
		HeuristicCosts = leastexpensivenodesub;

		double leastexpensiveedgesub = this.ComputeMinSubstituionBestMatch(unUsedEdges1,unUsedEdges2);
		HeuristicCosts += leastexpensiveedgesub;

		int nbdeletions=Math.max(0, n1-n2);
		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
		int nbinsertion=Math.max(0,n2-n1);
		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);

		nbdeletions=Math.max(0, e1-e2);
		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
		nbinsertion=Math.max(0,e2-e1);
		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);

	}

	public double ComputeHeuristicCosts() {

		HeuristicCosts=0.0;
		int n1,n2;
		int e1,e2;
		n1 = this.unUsedNodes1.size();
		n2 = this.unUsedNodes2.size();

		e1 = this.unUsedEdges1.size();
		e2 = this.unUsedEdges2.size();
		//System.out.println("********************************************");

		int nbdeletions=Math.max(0, n1-n2);
		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
		int nbinsertion=Math.max(0,n2-n1);
		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);

		nbdeletions=Math.max(0, e1-e2);
		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
		nbinsertion=Math.max(0,e2-e1);
		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);


		return HeuristicCosts;
	}


	public double ComputeHeuristicCostsAssignment() {
		HeuristicCosts=0.0;

		//double leastexpensivenodesub = ComputeMinSubstituion(unUsedNodes1,unUsedNodes2,true);
		//HeuristicCosts = leastexpensivenodesub;

		//double leastexpensiveedgesub = ComputeMinSubstituion(unUsedEdges1,unUsedEdges2,false);
		//HeuristicCosts += leastexpensiveedgesub;
		int n1,n2;
		int e1,e2;
		n1 = this.unUsedNodes1.size();
		n2 = this.unUsedNodes2.size();

		e1 = this.unUsedEdges1.size();
		e2 = this.unUsedEdges2.size();
		//System.out.println("********************************************");

		int nbdeletions=Math.max(0, n1-n2);
		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
		int nbinsertion=Math.max(0,n2-n1);
		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);

		nbdeletions=Math.max(0, e1-e2);
		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
		nbinsertion=Math.max(0,e2-e1);
		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);

		HeuristicCosts += UpdateNodeCost(unUsedNodes1,unUsedNodes2,StarredNode,true);
		HeuristicCosts += UpdateNodeCost(unUsedEdges1,unUsedEdges2,StarredEdge,false);



		//		int n1,n2;
		//		int e1,e2;
		//		n1 = this.unUsedNodes1.size();
		//		n2 = this.unUsedNodes2.size();
		//
		//		e1 = this.unUsedEdges1.size();
		//		e2 = this.unUsedEdges2.size();
		//		//System.out.println("********************************************");
		//
		//		int nbdeletions=Math.max(0, n1-n2);
		//		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
		//		int nbinsertion=Math.max(0,n2-n1);
		//		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);
		//
		//		nbdeletions=Math.max(0, e1-e2);
		//		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
		//		nbinsertion=Math.max(0,e2-e1);
		//		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);

		//System.out.println("totalCosts="+totalCosts+"\tHeuristicCosts="+HeuristicCosts+"\tTOTAL="+(totalCosts+HeuristicCosts)+"\tGlobalNodeCost="+this.GlobalNodeCost+"\tGlobalEdgeCost="+this.GlobalEdgeCost);

		return HeuristicCosts;
	}

	private double ComputeNbDeletion(LinkedList unUsedNodes12, int nbdeletions) {
		// TODO Auto-generated method stub
		GraphComponent node2 = Constants.EPS_COMPONENT;//new GraphComponent(Constants.EPS_ID) ;
		double res=0.0;

		for(int i=0;i<nbdeletions;i++){
			GraphComponent node1 = (GraphComponent) unUsedNodes12.get(i);
			res+=Constants.costFunction.getCosts(node1, node2);
		}
		return res;
	}

	private double ComputeNbInsertion(LinkedList unUsedNodes12, int nbdeletions) {
		// TODO Auto-generated method stub
		GraphComponent node2 = Constants.EPS_COMPONENT;//new GraphComponent(Constants.EPS_ID) ;
		double res=0.0;

		for(int i=0;i<nbdeletions;i++){
			GraphComponent node1 = (GraphComponent) unUsedNodes12.get(i);
			res+=Constants.costFunction.getCosts(node2,node1 );
		}
		return res;
	}


	private double ComputeMinSubstituion(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22, boolean isnode) {
		// TODO Auto-generated method stub
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();

		GraphComponent epsn = Constants.EPS_COMPONENT;//new GraphComponent(Constants.EPS_ID) ;
		//epsn.setNode(isnode);

		GraphComponent node1;
		GraphComponent node2;

		int n = Math.max(n1, n2);




		double res=0;

		if(n != 0  ){



			double epscost=0;
			int nbdeletions=Math.max(0, n1-n2);
			epscost += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			epscost += ComputeNbInsertion(unUsedNodes22,nbinsertion);

			double[][] matrix = new double[n][n];
			for (int i = 0; i < n; i++){

				if(i<n1){
					node1 = (GraphComponent)unUsedNodes12.get(i);	

				}else{
					node1 = epsn;
				}

				for (int j = 0; j < n; j++){
					matrix[i][j]= 0;
					if(j<n2){
						node2 = (GraphComponent)unUsedNodes22.get(j);

					}else{
						node2 = epsn;
					}

					//matrix[i][j]=Constants.costFunction.getCosts(node1, node2);

					if(node2.getId().equals(epsn.getId()) == true ||
							node1.getId().equals(epsn.getId()) == true){
						matrix[i][j]= 0;
					}else{
						//matrix[i][j]=Constants.costFunction.getCosts(node1, node2)/(double)(n);

						matrix[i][j]=Constants.costFunction.getCosts(node1, node2)/Math.max(this.source.size(), this.target.size());
					}



				}

			}

			double substitutioncost = munkresRec.getCosts(matrix);
			//substitutioncost /= Math.max(this.source.size(), this.target.size());
			res = substitutioncost+epscost;
		}
		return res;


		//double epscost = res-substitutioncost;
		//double sol = epscost + (substitutioncost/Math.max(this.source.size(), this.target.size()));
		//res = sol;



		//substitutioncost /= Math.max(this.source.size(), this.target.size());
		//if(epscost != 0 ) substitutioncost=0;
		//res = substitutioncost;


		/*double epscost=0;
			int nbdeletions=Math.max(0, n1-n2);
			epscost += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			epscost += ComputeNbInsertion(unUsedNodes22,nbinsertion);*/


		/*LAPJonkerVolgenantAssignment LAP; 
			LAP = new LAPJonkerVolgenantAssignment();
			int[] rowsol,colsol;
			double[] u,v;
			u=new double[n];
			v=new double[n];
			rowsol = new int[n];
			colsol = new int[n];
			double cost = LAP.lap(n, matrix, rowsol, colsol, u, v);
			if(cost != res ){
				System.out.println("resss");
			}*/



	}

	private double ComputeMinSubstituionNode(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();

		GraphComponent epsn = new GraphComponent(Constants.EPS_ID) ;
		//epsn.setNode(true);
		GraphComponent node1;
		GraphComponent node2;

		int n = Math.max(n1, n2);
		int nmin = Math.min(n1, n2);
		int nmax = Math.max(this.source.size(), this.target.size());
		double res=0;
		if(n != 0 ){
			double norm=0;
			double[][] matrix = new double[n][n];
			for (int i = 0; i < n; i++){

				if(i<n1){
					node1 = (GraphComponent)unUsedNodes12.get(i);			
				}else{
					node1 = epsn;
				}

				for (int j = 0; j < n; j++){

					if(j<n2){
						node2 = (GraphComponent)unUsedNodes22.get(j);
					}else{
						node2 = epsn;
					}

					if(node2.getId() == epsn.getId() || node1.getId() == epsn.getId() ){
						matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
					}else{
						matrix[i][j]=Constants.costFunction.getCosts(node1, node2);

					}


				}

			}

			res = munkresRec.getCosts(matrix);
			//if(norm >= 1) res-=0.5;
			//if(norm == 0) res =0;
			//res /=(double)(nmax);	
		}

		/*if(n != 0 ){
			int nbdeletions=Math.max(0, n1-n2);
			res += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			res += ComputeNbInsertion(unUsedNodes22,nbinsertion);
		}*/
		//double res = munkresRec.getSubstituionsCost(source, target);



		/*double nmin =Math.min(n1, n2); 
		if (nmin != 0) {
			res/=nmin;
			//double res = munkresRec.getSubstituionsCost(source, target);
			int nbdeletions=Math.max(0, n1-n2);
			res += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			res += ComputeNbInsertion(unUsedNodes22,nbinsertion);
		}else{
			res=0;
		}*/


		return res;
	}

	private double ComputeMinSubstituionEdge(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();

		GraphComponent epsn = new GraphComponent(Constants.EPS_ID) ;
		//epsn.setNode(false);
		GraphComponent node1;
		GraphComponent node2;


		int n = Math.max(n1, n2);
		int nmin = Math.min(n1, n2);
		double res=0;
		if(n != 0 ){

			double[][] matrix = new double[n][n];
			for (int i = 0; i < n; i++){

				if(i<n1){
					node1 = (GraphComponent)unUsedNodes12.get(i);			
				}else{
					node1 = epsn;
				}

				for (int j = 0; j < n; j++){

					if(j<n2){
						node2 = (GraphComponent)unUsedNodes22.get(j);
					}else{
						node2 = epsn;
					}

					if(node2.getId() == epsn.getId() || node1.getId() == epsn.getId() ){
						matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
					}else{
						matrix[i][j]=Constants.costFunction.getCosts(node1, node2);

					}


				}

			}

			res = munkresRec.getCosts(matrix);
			//if(norm >= 1) res-=0.5;
			//if(norm == 0) res =0;
			//res /=(double)(nmax);	
		}

		/*if(n != 0 ){
			int nbdeletions=Math.max(0, n1-n2);
			res += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			res += ComputeNbInsertion(unUsedNodes22,nbinsertion);
		}*/
		//double res = munkresRec.getSubstituionsCost(source, target);



		/*double nmin =Math.min(n1, n2); 
		if (nmin != 0) {
			res/=nmin;
			//double res = munkresRec.getSubstituionsCost(source, target);
			int nbdeletions=Math.max(0, n1-n2);
			res += ComputeNbDeletion(unUsedNodes12,nbdeletions);
			int nbinsertion=Math.max(0,n2-n1);
			res += ComputeNbInsertion(unUsedNodes22,nbinsertion);
		}else{
			res=0;
		}*/


		return res;
	}

	private double ComputeMinSubstituionBestMatch(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub

		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();

		GraphComponent epsn = new GraphComponent(Constants.EPS_ID) ;
		GraphComponent node1;
		GraphComponent node2;
		HashMap<String,Integer> already = new  HashMap<String,Integer>();
		int nmax = Math.max(n1, n2);
		int nmin = Math.min(n1, n2);

		LinkedList smallist;
		LinkedList biglist;

		if(n1>n2){
			smallist = unUsedNodes22;
			biglist = unUsedNodes12;				 
		}else{
			smallist = unUsedNodes12;
			biglist = unUsedNodes22;
		}



		double res=0;
		for (int i = 0; i < nmin; i++){
			node1 = (GraphComponent)smallist.get(i);			
			double minvalue=Double.MAX_VALUE;
			int minindex=-1;

			for (int j = 0; j < nmax; j++){
				node2 = (GraphComponent)biglist.get(j);
				if (already.containsKey(node2.getId())==false){

					double cout=Constants.costFunction.getCosts(node1, node2);
					if( (cout<minvalue)  ){
						minvalue=cout;
						minindex=j;

					}
				}

			}
			//if(minindex>-1){
			GraphComponent curbest = (GraphComponent)biglist.get(minindex);
			already.put(curbest.getId(),1);
			res += minvalue;
			//}

		}



		//int nbdeletions=Math.max(0, n1-n2);
		//res += ComputeNbDeletion(unUsedNodes12,nbdeletions);
		//int nbinsertion=Math.max(0,n2-n1);
		//res += ComputeNbInsertion(unUsedNodes22,nbinsertion);

		return res;
	}

	private double ComputeMinSubstituionLAP(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub
		LAPJonkerVolgenantAssignment LAP; 
		LAP = new LAPJonkerVolgenantAssignment();
		int n = Math.max(unUsedNodes12.size(), unUsedNodes22.size());
		double[][] matrix = new double[n][n];
		int[] rowsol,colsol;
		double[] u,v;
		u=new double[n];
		v=new double[n];
		rowsol = new int[n];
		colsol = new int[n];

		for (int i = 0; i < n; i++){
			GraphComponent node1 = new GraphComponent(Constants.EPS_ID) ;
			if(i<unUsedNodes12.size()){
				node1 = (GraphComponent)unUsedNodes12.get(i);

			}else{
				node1 = Constants.EPS_COMPONENT;
			}

			for (int j = 0; j < n; j++){
				GraphComponent node2 = new GraphComponent(Constants.EPS_ID) ;
				if(j<unUsedNodes22.size()){
					node2 = (GraphComponent)unUsedNodes22.get(j);

				}else{
					node2 = Constants.EPS_COMPONENT;
				}
				
				double minval=Constants.costFunction.getCosts(node1, node2);
				GraphComponent epsgc = Constants.EPS_COMPONENT;
				if(!node1.equals(Constants.EPS_COMPONENT) && !node2.equals(Constants.EPS_COMPONENT)){
					double valdel = Constants.costFunction.getCosts(node1,epsgc);
					//minval = Math.min(minval, valdel);
					double valins = Constants.costFunction.getCosts(epsgc,node2);
					minval = Math.min(minval, valdel+valins);
				}
				
				//matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
				matrix[i][j]=minval;
//				if(node1.getId() == Constants.EPS_ID || 
//						node2.getId() == Constants.EPS_ID	 ){
//					matrix[i][j]=0;
//				}else{
//					matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
//				}
			}

		}
		double cost = LAP.lap(n, matrix, rowsol, colsol, u, v);
		return cost;
		
//		MunkresRec munkresRec;
//		munkresRec = new MunkresRec();
//		double cost=munkresRec.getCosts(matrix);
//		return cost;
			
		//return munkresRec.getCosts(matrix);
	}
	
	
	private double ComputeMinSubstituionMunkres(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub
		LAPJonkerVolgenantAssignment LAP; 
		LAP = new LAPJonkerVolgenantAssignment();
		int n = Math.max(unUsedNodes12.size(), unUsedNodes22.size());
		double[][] matrix = new double[n][n];
		int[] rowsol,colsol;
		double[] u,v;
		u=new double[n];
		v=new double[n];
		rowsol = new int[n];
		colsol = new int[n];

		for (int i = 0; i < n; i++){
			GraphComponent node1 = new GraphComponent(Constants.EPS_ID) ;
			if(i<unUsedNodes12.size()){
				node1 = (GraphComponent)unUsedNodes12.get(i);

			}else{
				node1 = Constants.EPS_COMPONENT;
			}

			for (int j = 0; j < n; j++){
				GraphComponent node2 = new GraphComponent(Constants.EPS_ID) ;
				if(j<unUsedNodes22.size()){
					node2 = (GraphComponent)unUsedNodes22.get(j);

				}else{
					node2 = Constants.EPS_COMPONENT;
				}
				
				double minval=Constants.costFunction.getCosts(node1, node2);
				GraphComponent epsgc = Constants.EPS_COMPONENT;
//				if(!node1.equals(Constants.EPS_COMPONENT) && !node2.equals(Constants.EPS_COMPONENT)){
//					double valdelins = Constants.costFunction.getCosts(node1,epsgc);
//					minval = Math.min(minval, valdelins);
//					valdelins = Constants.costFunction.getCosts(epsgc,node2);
//					minval = Math.min(minval, valdelins);
//				}
				
				if(!node1.equals(Constants.EPS_COMPONENT) && !node2.equals(Constants.EPS_COMPONENT)){
					double valdel = Constants.costFunction.getCosts(node1,epsgc);
					//minval = Math.min(minval, valdel);
					double valins = Constants.costFunction.getCosts(epsgc,node2);
					minval = Math.min(minval, valdel+valins);
				}
				
				//matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
				matrix[i][j]=minval;
//				if(node1.getId() == Constants.EPS_ID || 
//						node2.getId() == Constants.EPS_ID	 ){
//					matrix[i][j]=0;
//				}else{
//					matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
//				}
			}

		}
		//double cost = LAP.lap(n, matrix, rowsol, colsol, u, v);
		//return cost;
		
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		double cost=munkresRec.getCosts(matrix);
		return cost;
			
		//return munkresRec.getCosts(matrix);
	}

	/**
	 * the unused nodes of both graphs
	 */
	private LinkedList unUsedNodes1, unUsedNodes2;

	/**
	 * the unused edges of both grpahs
	 */
	private LinkedList unUsedEdges1, unUsedEdges2;

	/**
	 * saves all distortions key = source graph-component value = target
	 * graph-component
	 */
	private Hashtable distortions;

	private Hashtable EdgeInverted;
	public Hashtable getEdgeInverted() {
		return EdgeInverted;
	}

	public void setEdgeInverted(Hashtable edgeInverted) {
		EdgeInverted = edgeInverted;
	}

	private Graph source, target;

	private double GlobalNodeCost;

	private double GlobalEdgeCost;

	private HashMap<GraphComponent, Integer> NodeToIndiceG1;

	private HashMap<GraphComponent, Integer> EdgeToIndiceG1;

	private HashMap<GraphComponent, Integer> NodeToIndiceG2;

	private HashMap<GraphComponent, Integer> EdgeToIndiceG2;

	private int[][] StarredNode;

	private int[][] StarredEdge;

	private double[][] nodecostmatrix;

	private double[][] edgecostmatrix;

	private HashMap<GraphComponent, Double> BestMatchNodeG2;

	private HashMap<GraphComponent, Double> BestMatchEdgeG2;

	private HashMap<GraphComponent, Double> BestMatchNodeG1;

	private HashMap<GraphComponent, Double> BestMatchEdgeG1;

	private HashMap<GraphComponent, GraphComponent> BestMatchNodeG2G1;

	private HashMap<GraphComponent, GraphComponent> BestMatchEdgeG2G1;

	private HashMap<GraphComponent, GraphComponent> BestMatchNodeG1G2;

	private HashMap<GraphComponent, GraphComponent> BestMatchEdgeG1G2;

	private double BestCostInsertionNode;

	private double BestCostInsertionEdge;

	private double BestCostDeletionNode;

	private double BestCostDeletionEdge;

	/**
	 * constructs a new editpath between
	 * 
	 * @param source
	 *            and
	 * @param target
	 */
	public EditPath(Graph source, Graph target) {
		this.init();
		this.source = source;
		this.target = target;
		this.unUsedNodes1.addAll(source);
		this.unUsedNodes2.addAll(target);
		this.unUsedEdges1.addAll(source.getEdges());
		this.unUsedEdges2.addAll(target.getEdges());


	}

	/**
	 * constructs a new editpath between
	 * 
	 * @param source
	 *            and
	 * @param target
	 */
	public EditPath(Graph source, Graph target, int heuristicmethod) {
		this.init();
		this.source = source;
		this.target = target;
		this.unUsedNodes1.addAll(source);
		this.unUsedNodes2.addAll(target);
		this.unUsedEdges1.addAll(source.getEdges());
		this.unUsedEdges2.addAll(target.getEdges());

//		if(heuristicmethod==this.MunkresAssigmentHeuristic){
//			nodecostmatrix = ComputeCostMatrix(unUsedNodes1,unUsedNodes2,true);
//			edgecostmatrix = ComputeCostMatrix(unUsedEdges1,unUsedEdges2,false);
//			StarredNode = ComputeStarredMatrix(nodecostmatrix, true);
//			StarredEdge = ComputeStarredMatrix(edgecostmatrix,false);
//
//
////			NodeToIndiceG1  = IndexComponent(unUsedNodes1);
////
////			EdgeToIndiceG1 = IndexComponent(unUsedEdges1);
////
////			NodeToIndiceG2  = IndexComponent(unUsedNodes2);
////
////			EdgeToIndiceG2 = IndexComponent(unUsedEdges2);
//			
//
//			BestMatchNodeG2 = ComputeBestMatch(StarredNode,unUsedNodes2,true);
//			BestMatchEdgeG2 = ComputeBestMatch(StarredEdge,unUsedEdges2,false);
//
//			BestMatchNodeG1 = ComputeBestMatchG1(StarredNode,unUsedNodes1,true);
//			BestMatchEdgeG1 = ComputeBestMatchG1(StarredEdge,unUsedEdges1,false);
//			
//				
//
////			BestMatchNodeG2G1 = ComputeBestMatchGC(StarredNode,unUsedNodes2,true);
////			BestMatchEdgeG2G1 = ComputeBestMatchGC(StarredEdge,unUsedEdges2,false);
////
////			BestMatchNodeG1G2 = ComputeBestMatchGCG1(StarredNode,unUsedNodes1,true);
////			BestMatchEdgeG1G2 = ComputeBestMatchGCG1(StarredEdge,unUsedEdges1,false);
//		}

	}



	private HashMap<GraphComponent, GraphComponent> ComputeBestMatchGC(
			int[][] starredNode2, LinkedList unUsedNodes22, boolean b) {
		// TODO Auto-generated method stub
		int k = starredNode2.length;
		HashMap<GraphComponent,GraphComponent> BestMatchNodeG2a = new HashMap<GraphComponent,GraphComponent>();
		for(int i=0;i<unUsedNodes22.size();i++){
			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
			for(int j=0;j<k;j++){

				int r = starredNode2[j][0];
				int c = starredNode2[j][1];
				if(c == i){
					GraphComponent gc1;

					if(b == true ){
						if(r>= this.source.size() ) {
							gc1=Constants.EPS_COMPONENT;
						}else{
							gc1 = (GraphComponent) this.source.get(r);
						}
					}else{
						if(r>= this.source.getEdges().size() ) {
							gc1=Constants.EPS_COMPONENT;
						}else{
							gc1 = (GraphComponent) this.source.getEdges().get(r);
						}
					}
					BestMatchNodeG2a.put(gc, gc1);
					j=k;
				}

			}

		}
		return BestMatchNodeG2a;
	}

	private HashMap<GraphComponent, GraphComponent> ComputeBestMatchGCG1(
			int[][] starredNode2, LinkedList unUsedNodes22, boolean b) {
		// TODO Auto-generated method stub
		int k = starredNode2.length;
		HashMap<GraphComponent,GraphComponent> BestMatchNodeG2a = new HashMap<GraphComponent,GraphComponent>();
		for(int i=0;i<unUsedNodes22.size();i++){
			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
			for(int j=0;j<k;j++){

				int r = starredNode2[j][0];
				int c = starredNode2[j][1];
				if(r == i){
					GraphComponent gc1;

					if(b == true ){
						if(c>= this.target.size() ) {
							gc1=Constants.EPS_COMPONENT;
						}else{
							gc1 = (GraphComponent) this.target.get(c);
						}


					}else{
						if(c>= this.target.getEdges().size() ) {
							gc1=Constants.EPS_COMPONENT;
						}else{
							gc1 = (GraphComponent) this.target.getEdges().get(c);
						}

					}
					BestMatchNodeG2a.put(gc, gc1);
					j=k;
				}

			}

		}
		return BestMatchNodeG2a;
	}

	private HashMap<GraphComponent, Double> ComputeBestMatch(
			int[][] starredNode2, LinkedList unUsedNodes22, boolean b) {
		// TODO Auto-generated method stub
		int k = starredNode2.length;
		HashMap<GraphComponent,Double> BestMatchNodeG2a = new HashMap<GraphComponent,Double>();
		if(b == true) BestCostDeletionNode=Double.MAX_VALUE; 
		if(b == false) BestCostDeletionEdge=Double.MAX_VALUE; 
	
		

//		for(int i=0;i<unUsedNodes22.size();i++){
//			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
//			for(int j=0;j<k;j++){
//
//				int r = starredNode2[j][0];
//				int c = starredNode2[j][1];
//				if(c == i){
//					double Costs;
//					if(b == true ){
//						Costs=this.nodecostmatrix[r][c];
//					}else{
//						Costs=this.edgecostmatrix[r][c];
//					}
//					BestMatchNodeG2a.put(gc, Costs);
//					j=k;
//				}
//
//			}
//
//		}
		
		
		for(int i=0;i<unUsedNodes22.size();i++){
			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
			double minvalue = Double.MAX_VALUE;
			for(int j=0;j<k;j++){

					double val=0;
					if(b==true) val = nodecostmatrix[j][i];
					if(b==false) val = edgecostmatrix[j][i];
					if(val<minvalue) {
						minvalue = val;
					}
				
				}
				BestMatchNodeG2a.put(gc, minvalue);
		}
		
		boolean flag=true;
		for(int j=0;j<k;j++){

			int r = starredNode2[j][0];
			int c = starredNode2[j][1];
			if(c  >= unUsedNodes22.size()){
				double Costs;
				if(b == true ){
					Costs=this.nodecostmatrix[r][c];
					if(BestCostDeletionNode > Costs){
						BestCostDeletionNode = Costs;
						flag=false;
					}
					
				}else{
					Costs=this.edgecostmatrix[r][c];
					if(BestCostDeletionEdge > Costs){
						BestCostDeletionEdge = Costs;
						flag=false;
					}
				}
				
			}

		}
		
		if(flag == true ){
			if(b == true ){
				//BestCostDeletionNode=0;
			}else{
				//BestCostDeletionEdge=0;
			}
		}
		return BestMatchNodeG2a;
	}

	private HashMap<GraphComponent, Double> ComputeBestMatchG1(
			int[][] starredNode2, LinkedList unUsedNodes22, boolean b) {
		// TODO Auto-generated method stub
		int k = starredNode2.length;
		HashMap<GraphComponent,Double> BestMatchNodeG2a = new HashMap<GraphComponent,Double>();
		if(b==true )BestCostInsertionNode=Double.MAX_VALUE; 
		if(b==false )BestCostInsertionEdge=Double.MAX_VALUE; 
	
		
//		for(int i=0;i<unUsedNodes22.size();i++){
//			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
//			for(int j=0;j<k;j++){
//
//				int r = starredNode2[j][0];
//				int c = starredNode2[j][1];
//				if(r == i){
//					double Costs;
//					if(b == true ){
//						Costs=this.nodecostmatrix[r][c];
//					}else{
//						Costs=this.edgecostmatrix[r][c];
//					}
//					BestMatchNodeG2a.put(gc, Costs);
//					j=k;
//				}
//
//			}
//
//		}
		
		
		for(int i=0;i<unUsedNodes22.size();i++){
			GraphComponent gc = (GraphComponent) unUsedNodes22.get(i);
			double minvalue = Double.MAX_VALUE;
			for(int j=0;j<k;j++){

					double val=0;
					if(b==true) val = nodecostmatrix[i][j];
					if(b==false) val = edgecostmatrix[i][j];
					if(val<minvalue) {
						minvalue = val;
					}
				
				}
				BestMatchNodeG2a.put(gc, minvalue);
		}
		
		boolean flag=true;
		for(int j=0;j<k;j++){

			int r = starredNode2[j][0];
			int c = starredNode2[j][1];
			if(r  >= unUsedNodes22.size()){
				double Costs;
				if(b == true ){
					Costs=this.nodecostmatrix[r][c];
					if(BestCostInsertionNode > Costs){
						BestCostInsertionNode = Costs;
						flag=false;
					}
					
					
				}else{
					Costs=this.edgecostmatrix[r][c];
					if(BestCostInsertionEdge > Costs){
						BestCostInsertionEdge = Costs;
						flag=false;
					}
				}
				
			}

		}
		
		if(flag == true ){
			if(b == true ){
				//BestCostInsertionNode=0;
			}else{
				//BestCostInsertionEdge=0;
			}
		}
		
		return BestMatchNodeG2a;
	}

	private double UpdateNodeCost(LinkedList unUsedNodes12, LinkedList unUsedNodes22, int[][] starredNode, boolean isnode) {
		// TODO Auto-generated method stub

		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();
		GraphComponent epsgc = Constants.EPS_COMPONENT;
		//epsgc.setNode(isnode);
		int n = Math.max(n1, n2);
		if(n==0) return 0;
		double[][] costmat = new double[n][n];

		for(int i=0;i<n;i++){
			GraphComponent gc1;
			if(i<n1){
				gc1 = (GraphComponent) unUsedNodes12.get(i);
			}else{
				gc1 = epsgc;
			}

			for(int j=0;j<n;j++){
				GraphComponent gc2;
				if(j<n2){
					gc2 = (GraphComponent) unUsedNodes22.get(j);
				}else{
					gc2 = epsgc;
				}

				double val1=Float.MAX_VALUE,val2=Float.MAX_VALUE; 
				val1=val2=0;
				if(gc1.equals(Constants.EPS_COMPONENT)){
					if(isnode==true ){
						val1= this.BestCostInsertionNode;
					}else{
						val1= this.BestCostInsertionEdge;
					}
					//val1=Constants.costFunction.getCosts(gc1,gc2);	
					//val1=999;
					//val1=Math.min(val1,  Constants.costFunction.getCosts(gc1,gc2)	);
					val1=0;
				}

				if(gc2.equals(Constants.EPS_COMPONENT)){
					if(isnode==true ){
						val2= this.BestCostDeletionNode;
					}else{
						val2= this.BestCostDeletionEdge;
					}
				    //val2=Math.min(val2,  Constants.costFunction.getCosts(gc1,gc2)	);
//					val2=999;
					//val2= Constants.costFunction.getCosts(gc1,gc2);	
					val2=0;
				}



				if(isnode==true ){
					if(BestMatchNodeG2.containsKey(gc2)){
						val2 = this.BestMatchNodeG2.get(gc2);

					}

					if(BestMatchNodeG1.containsKey(gc1)){
						val1 = this.BestMatchNodeG1.get(gc1);

					}
				}

				if(isnode==false ){
					if(BestMatchEdgeG2.containsKey(gc2)){
						val2 = this.BestMatchEdgeG2.get(gc2);

					}

					if(BestMatchEdgeG1.containsKey(gc1)){
						val1 = this.BestMatchEdgeG1.get(gc1);

					}
				}

				double minval = Math.min(val1, val2);
				if(!gc1.equals(Constants.EPS_COMPONENT) && !gc2.equals(Constants.EPS_COMPONENT)){
					double valdelins = Constants.costFunction.getCosts(gc1,epsgc);
					minval = Math.min(minval, valdelins);
					valdelins = Constants.costFunction.getCosts(epsgc,gc2);
					minval = Math.min(minval, valdelins);
				}
				
				costmat[i][j]=minval;

			}//fin for j	
		}//fin for i

//		double minvalue,summin;
//		
//		minvalue=summin=0;
//		minvalue=Double.MAX_VALUE;
//		for(int i=0;i<n;i++){
//			minvalue=Double.MAX_VALUE;
//			for(int j=0;j<n;j++){
//				if(costmat[j][i]<minvalue){
//					minvalue = costmat[j][i];
//				}
//			}
//			summin += minvalue;
//		}
//		
//		
//		return summin;
//		
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		double cost =munkresRec.getCosts(costmat);

		return cost;
//		
		//		if(isnode == true){
		//			res = this.GlobalNodeCost;
		//			Enumeration mykeys = distortions.keys();
		//			while(mykeys.hasMoreElements()){
		//				GraphComponent gckey = (GraphComponent)mykeys.nextElement();
		//				GraphComponent gcvalue = (GraphComponent) distortions.get(gckey);
		//				
		//				if(gckey.equals(Constants.EPS_COMPONENT)){
		//					if(gcvalue.isNode()){
		//						//res -= Constants.costFunction.getCosts(gckey,gcvalue);	
		//					}
		//				}
		//				
		//				if(gcvalue.equals(Constants.EPS_COMPONENT)){
		//					if(gckey.isNode()){
		//						//res -= Constants.costFunction.getCosts(gckey,gcvalue);	
		//					}
		//				}
		//					
		//				if(gckey.isNode() && !gckey.equals(Constants.EPS_COMPONENT) && !gcvalue.equals(Constants.EPS_COMPONENT)){
		//					double val1=0,val2=0; 
		//					if(BestMatchNodeG2.containsKey(gcvalue)){
		//						val2 = this.BestMatchNodeG2.get(gcvalue);
		//						
		//					}
		//					
		//					if(BestMatchNodeG1.containsKey(gckey)){
		//						val1 = this.BestMatchNodeG1.get(gckey);
		//						
		//					}
		//					
		//					res -= Math.max(val1, val2);
		//				}
		//			}
		//		}else{
		//			res = this.GlobalEdgeCost;
		//			Enumeration mykeys = distortions.keys();
		//			while(mykeys.hasMoreElements()){
		//				GraphComponent gckey = (GraphComponent)mykeys.nextElement();
		//				GraphComponent gcvalue = (GraphComponent) distortions.get(gckey);
		//				
		//				if(gckey.equals(Constants.EPS_COMPONENT)){
		//					if(!gcvalue.isNode()){
		//						//res -= Constants.costFunction.getCosts(gckey,gcvalue);	
		//					}
		//				}
		//				
		//				if(gcvalue.equals(Constants.EPS_COMPONENT)){
		//					if(!gckey.isNode()){
		//						//res -= Constants.costFunction.getCosts(gckey,gcvalue);	
		//					}
		//				}
		//					
		//				if(!gckey.isNode() && !gckey.equals(Constants.EPS_COMPONENT) && !gcvalue.equals(Constants.EPS_COMPONENT)){
		//					double val1=0,val2=0; 
		//					if(BestMatchEdgeG2.containsKey(gcvalue)){
		//						val2 = this.BestMatchEdgeG2.get(gcvalue);
		//						
		//					}
		//					
		//					if(BestMatchEdgeG1.containsKey(gckey)){
		//						val1 = this.BestMatchEdgeG1.get(gckey);
		//						
		//					}
		//					
		//					res -= Math.max(val1, val2);
		//				}
		//			}
		//		}
		//		
		//		
		//		
		//		
		//	
		//		return Math.max(0,res);

		//		HashMap<GraphComponent,Integer> already = new HashMap<GraphComponent,Integer>();
		//	
		//		double totalcost=0;
		//		int nbdeletion=0;
		//		if(unUsedNodes22.size()<unUsedNodes12.size()){
		//			for(int i=0;i<unUsedNodes22.size();i++){
		//				GraphComponent gc =(GraphComponent) unUsedNodes22.get(i);
		//				if(isnode ==true){
		//					double cost = this.BestMatchNodeG2.get(gc);
		//					GraphComponent matched = this.BestMatchNodeG2G1.get(gc);
		//					already.put(matched,1);
		//					if(this.distortions.containsKey(matched)==false) totalcost+= cost;
		//				}else{
		//					double cost = this.BestMatchEdgeG2.get(gc);
		//					GraphComponent matched = this.BestMatchEdgeG2G1.get(gc);
		//					already.put(matched,1);
		//					if(this.distortions.containsKey(matched)==false)  totalcost+= cost;
		//				}
		//			}
		//			
		//			for(int i=0;i<unUsedNodes12.size();i++){
		//				GraphComponent gc =(GraphComponent) unUsedNodes12.get(i);
		//				if(already.containsKey(gc)==false ){
		//					if(isnode ==true){
		//						double cost = this.BestMatchNodeG1.get(gc);
		//						totalcost+= cost;
		//					}else{
		//						double cost = this.BestMatchEdgeG1.get(gc);
		//						totalcost+= cost;
		//					}
		//				}
		//			}
		//			
		//		}else{
		//			for(int i=0;i<unUsedNodes12.size();i++){
		//				GraphComponent gc =(GraphComponent) unUsedNodes12.get(i);
		//				if(isnode ==true){
		//					double cost = this.BestMatchNodeG1.get(gc);
		//					GraphComponent matched = this.BestMatchNodeG1G2.get(gc);
		//					already.put(matched,1);
		//					if(this.distortions.containsValue(matched)==false)totalcost+= cost;
		//				}else{
		//					double cost = this.BestMatchEdgeG1.get(gc);
		//					GraphComponent matched = this.BestMatchEdgeG1G2.get(gc);
		//					already.put(matched,1);
		//					if(this.distortions.containsValue(matched)==false)totalcost+= cost;
		//				}
		//			}
		//			
		//			for(int i=0;i<unUsedNodes22.size();i++){
		//				GraphComponent gc =(GraphComponent) unUsedNodes22.get(i);
		//				if(already.containsKey(gc)==false ){
		//					if(isnode ==true){
		//						double cost = this.BestMatchNodeG2.get(gc);
		//						totalcost+= cost;
		//					}else{
		//						double cost = this.BestMatchEdgeG2.get(gc);
		//						totalcost+= cost;
		//					}
		//				}
		//			}
		//		}
		//		
		//		
		//		if(totalcost+HeuristicCosts + this.totalCosts >7.779)
		//			try {
		//				System.in.read();
		//			} catch (IOException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			} 
		//		return totalcost;

		//		LinkedList maxlist;
		//		HashMap<GraphComponent, Integer>  IndexElement ;
		//		double[][] costmatrix;
		//
		//
		//		int tempindex = 0;
		//		int r;
		//		int c;
		//
		//		double totCosts = 0.0;
		//
		//		LinkedList originallist;
		//		boolean isg1=true;
		//
		//
		//		//		
		//		//		//boolean g1list=true;
		//		if(unUsedNodes12.size()<unUsedNodes22.size()){
		//			isg1=true;
		//			maxlist = unUsedNodes12;
		//			//g1list = true;
		//			if(isnode == true){
		//				IndexElement = NodeToIndiceG1;
		//				costmatrix = this.nodecostmatrix;
		//				originallist = source;
		//			}else{
		//				IndexElement = EdgeToIndiceG1;
		//				costmatrix = this.edgecostmatrix;
		//				originallist = source.getEdges();
		//			}	
		//		}else{
		//			maxlist = unUsedNodes22;
		//			isg1 = false;
		//			//g1list = false;
		//			if(isnode == true){
		//				IndexElement = NodeToIndiceG2;
		//				costmatrix = this.nodecostmatrix;
		//				originallist = target;
		//			}else{
		//				IndexElement = EdgeToIndiceG2;
		//				costmatrix = this.edgecostmatrix;
		//				originallist = target.getEdges();
		//			}	
		//		}
		//
		//		//		
		//		//
		//		//		for(int i=0;i<maxlist.size();i++){
		//		//
		//		//			GraphComponent gc = (GraphComponent) maxlist.get(i);
		//		//			tempindex = IndexElement.get(gc);
		//		//
		//		//			r = starredNode[tempindex][0];
		//		//			c = starredNode[tempindex][1];
		//		//			totCosts += costmatrix[r][c];
		//		//
		//		//		}
		//
		//		int k = starredNode.length;
		//		if(isnode == true){
		//			totCosts = this.GlobalNodeCost;	
		//		}else{
		//			totCosts = this.GlobalEdgeCost;
		//		}
		//		totCosts=0.0;
		//		LinkedList<GraphComponent> matched = new LinkedList<GraphComponent>();
		//
		//		for(int i=0;i<maxlist.size();i++){
		//			GraphComponent gc = (GraphComponent) maxlist.get(i);
		//			int index = IndexElement.get(gc);
		//			int indexmachted=-1;
		//			for (int j = 0; j < k; j++) {
		//				r = starredNode[j][0];
		//				c = starredNode[j][1];
		//				if(isg1 == true){
		//					if(index ==r){
		//						indexmachted =c;
		//					}
		//				}else{
		//					if(index ==c){
		//						indexmachted=r;	
		//					}
		//				}
		//			}
		//			if(isg1 == true){
		//				totCosts += costmatrix[index][indexmachted];	
		//				//GraphComponent gctmp = (GraphComponent) this.target.get(indexmachted);
		//				//matched.add(gctmp);
		//			}else{
		//				totCosts += costmatrix[indexmachted][index];
		//				//GraphComponent gctmp = (GraphComponent) this.source.get(indexmachted);
		//				//matched.add(gctmp);
		//			}
		//		}
		//
		//
		//
		//		//		for (int i = 0; i < k; i++) {
		//		//			r = starredNode[i][0];
		//		//			c = starredNode[i][1];
		//		//			
		//		//			if(isg1 == true){
		//		//				if(r >= originallist.size()){
		//		//					//totCosts += costmatrix[r][c];	
		//		//				}else{
		//		//					GraphComponent gc = (GraphComponent) originallist.get(r);
		//		//					if(GraphComponentIntheList(gc,maxlist)==true){
		//		//						totCosts += costmatrix[r][c];	
		//		//					}
		//		//				}
		//		//			}else{
		//		//				if(c >= originallist.size()){
		//		//					//totCosts += costmatrix[r][c];	
		//		//				}else{
		//		//					GraphComponent gc = (GraphComponent) originallist.get(c);
		//		//					if(GraphComponentIntheList(gc,maxlist)==true){
		//		//						totCosts += costmatrix[r][c];	
		//		//					}
		//		//				}
		//		//			}
		//
		//
		//
		//
		//
		//
		//		return totCosts;
	}

	private boolean GraphComponentIntheList(GraphComponent gc,
			LinkedList unUsedNodes12) {
		// TODO Auto-generated method stub
		for(int i=0;i<unUsedNodes12.size();i++){
			GraphComponent gcur = (GraphComponent) unUsedNodes12.get(i);
			if(gc == gcur){
				return true;
			}
		}
		return false;
	}

	private HashMap<GraphComponent, Integer> IndexComponent(
			LinkedList unUsedNodes12) {
		// TODO Auto-generated method stub
		HashMap<GraphComponent, Integer> NodeToIndiceG1 = new HashMap<GraphComponent,Integer>();
		for(int i=0;i<unUsedNodes12.size();i++){
			NodeToIndiceG1.put((GraphComponent) unUsedNodes12.get(i), i);	
		}
		return NodeToIndiceG1;
	}

	private int[][] ComputeStarredMatrix(double[][] nodecostmatric,boolean isnode) {
		// TODO Auto-generated method stub
		MunkresRec munkresRec;
		munkresRec = new MunkresRec();
		double cost =munkresRec.getCosts(nodecostmatric);
		if(isnode == true ) this.GlobalNodeCost = cost;
		if(isnode == false ) this.GlobalEdgeCost = cost;

		return munkresRec.getStarredIndices();
	}

	private double[][] ComputeCostMatrix(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22,boolean isnode) {
		// TODO Auto-generated method stub


		int n1 = unUsedNodes12.size();
		int n2 = unUsedNodes22.size();

		GraphComponent epsn = Constants.EPS_COMPONENT;//new GraphComponent(Constants.EPS_ID) ;
	//	epsn.setNode(isnode);

		GraphComponent node1;
		GraphComponent node2;

		int n = Math.max(n1, n2);
		double[][] matrix = new double[n][n];
		for (int i = 0; i < n; i++){

			if(i<n1){
				node1 = (GraphComponent)unUsedNodes12.get(i);	

			}else{
				node1 = epsn;
			}

			for (int j = 0; j < n; j++){

				if(j<n2){
					node2 = (GraphComponent)unUsedNodes22.get(j);

				}else{
					node2 = epsn;
				}

				matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
				//				if( node1.getId().equals(epsn.getId())==true || 
				//						node2.getId().equals(epsn.getId())==true){
				//					matrix[i][j]=0;
				//				}else{
				//					matrix[i][j]=Constants.costFunction.getCosts(node1, node2);
				//				}
			}

		}


		return matrix;
	}

	/**
	 * constructs a new editpath from a string str
	 *  @param String Format: (HeuristicCosts<>totalCosts<>getId1<>getId2<>getEdgesId1<>
	 *  getEdgesId2<>getEdgesMode1<>getEdgesMode2<>unUsedNodes1<>
	 *  unUsedNodes2<>unUsedEdges1<>unUsedEdges2<>Distortions<>invertedEdges)

	 */

	public EditPath(String str) { 
		init(); // initializing the edit path
		this.source = new Graph();
		this.target = new Graph();

		String[] parts = str.split("<>"); // splitting the string into 14 parts
		//14 parts
		/* 0:HeuristicCost
		 * 1:totalCost
		 * 2:getId1
		 * 3:getId2
		 * 4:getEdgesId1
		 * 5:getEdgesId2
		 * 6:getEdgesMode1
		 * 7:getEdgesMode2
		 * 8:unUsedNodes1
		 * 9:unUsedNodes2
		 * 10:unUsedEdges1
		 * 11:unUsedEdges2
		 * 12:Distortions
		 * 13:invertedEdges
		 * */
		/*		System.out.println("Length: "+parts.length);
	     for(int i=0; i<parts.length ; i++)
		 {
			 System.out.println("parts("+i+") = "+parts[i]);
		 }
		 */
		//parsing both heuristic and total costs
		HeuristicCosts = Double.parseDouble(parts[0]);
		totalCosts=Double.parseDouble(parts[1]);

		//parsing graph1's ID
		if(parts[2].length()!=0)
			this.source.setId(parts[2]);
		//parsing graph2's ID
		if(parts[3].length()!=0)
			this.target.setId(parts[3]);
		//parsing edgesID of graph1
		if(parts[4].length()!=0)
			this.source.setEdgeId(parts[4]);
		//parsing edgesID of graph2
		if(parts[5].length()!=0)
			this.target.setEdgeId(parts[5]);
		//parsing setEdgeMode of graph1
		if(parts[6].length()!=0)
			this.source.setEdgeMode(parts[6]);
		//parsing setEdgeMode of graph2
		if(parts[7].length()!=0)
			this.target.setEdgeMode(parts[7]);

		//Parsing unUsedNodes1
		//System.out.println("---------------unUsedNodes1");
		if(parts[8].length()!=0)
		{
			nodeListParser(this,source,unUsedNodes1,parts[8],parts[6]);

		}

		//Parsing unUsedNodes2
		//System.out.println("-------------unUsedNodes2");
		if(parts[9].length()!=0)
		{
			nodeListParser(this,target,unUsedNodes2,parts[9],parts[7]);
		}


		//Parsing unUsedEdges1
		//System.out.println("------unUsedEdges1");
		if(parts[10].length()!=0)
		{
			edgeListParser(this,source,unUsedEdges1,parts[10],parts[6]);

		}

		//Parsing unUsedEdges2
		//System.out.println("-------unUsedEdges2**");
		if(parts[11].length()!=0)
		{
			edgeListParser(this,target,unUsedEdges2,parts[11],parts[7]);

		}

		/*Parsing Distortions
		 * 	Distortions:  <distortion1/distortion2/distortion3/.........etc>
		 * distortion(i): distortionType%1st part>>2nd part
		 * distortionType: Node or Edge
		 */

		//System.out.println("----------Distortions**");
		if(parts[12].length()!=0)
		{
			String[] getDistortions = parts[12].split("/");
			for(int i=0 ; i<getDistortions.length ; i++)
			{
				String[] DistortionType = getDistortions[i].split("%");

				// if the distortion is node to node
				if(DistortionType[0].equals("Node"))
				{
					//		System.out.println("Node Distortion ............");
					String[] nodes12 = DistortionType[1].split(">>");

					//testing whether sourceNode, targetNode or non of them is  eps
					if(nodes12[0].equals("eps_id"))
					{
						GraphComponent eps = new GraphComponent(Constants.EPS_ID);
						Node nd2 = new Node(nodes12[1],parts[7],true,target);
						//target.add(nd2);
						//      	System.out.println("Node Distortion (eps,"+nd2.getId()+")");
						distortions.put(eps, nd2);
					}
					else if(nodes12[1].equals("eps_id"))
					{
						Node nd1 = new Node(nodes12[0],parts[6],true,source);
						//source.add(nd1);
						GraphComponent eps = new GraphComponent(Constants.EPS_ID);
						//    	System.out.println("Node Distortion ("+nd1.getId()+",eps)");
						distortions.put(nd1, eps);
					}
					else
					{
						//System.out.println("neither nd1 nor nd2 is eps*************");
						Node nd1 = new Node(nodes12[0],parts[6],true,source);
						//source.add(nd1);
						Node nd2 = new Node(nodes12[1],parts[7],true,target);
						//target.add(nd2);
						//	System.out.println("Node Distortion ("+nd1.getId()+","+nd2.getId()+")");
						distortions.put(nd1, nd2);

					}

					///////////////////////////////////////////		
				}

				// if the distortion is edge to edge
				else if(DistortionType[0].equals("Edge"))
				{
					//System.out.println("Edge Distortion ............");
					String[] edges12 = DistortionType[1].split(">>");

					//testing whether sourceNode, targetNode or non of them is eps 
					if(edges12[0].equals("eps_id"))
					{
						GraphComponent eps = new GraphComponent(Constants.EPS_ID);
						Edge edg2 = new Edge(edges12[1],parts[7],target);
						target.getEdges().add(edg2);
						//	System.out.println("Edge Distortion (eps,"+edg2.getId()+")");
						distortions.put(eps, edg2);
					}
					else if(edges12[1].equals("eps_id"))
					{
						Edge edg1 = new Edge(edges12[0],parts[6],source);
						source.getEdges().add(edg1);
						GraphComponent eps = new GraphComponent(Constants.EPS_ID);
						//System.out.println("Edge Distortion ("+edg1.getId()+",eps)");
						distortions.put(edg1, eps);
					}
					else
					{

						Edge edg1 = new Edge(edges12[0],parts[6],source);
						source.getEdges().add(edg1);
						Edge edg2 = new Edge(edges12[1],parts[7],target);
						target.getEdges().add(edg2);
						//	System.out.println("Edge Distortion ("+edg1.getId()+","+edg2.getId()+")");
						distortions.put(edg1, edg2);	
					}

				}

			}
		}


		//Parsing invertedEdges

		/*
		 *invertedEdges Format: <invertedEdge1/invertedEdge2/ ...... >
		 *invertedEdges(i): edge=true or edge=false
		 */
		if(parts.length==14)
		{
			if(parts[13].length()!=0)
			{

				if(parts[13].toString().equals("\t")!=true)
				{
					String[] getInvertedEdges = parts[13].split("/");
					for(int i=0; i<getInvertedEdges.length ; i++)
					{
						String[] invertedEdge = getInvertedEdges[i].split("=");
						Edge edg = new Edge(invertedEdge[0],parts[6],source);
						boolean isInverted =Boolean.parseBoolean(invertedEdge[1]) ;
						EdgeInverted.put((GraphComponent)edg, isInverted);

					}	

				}



			}

		}


		/*	System.out.println("**************Source*************************");

		Iterator nodeIterator = source.iterator();
		while (nodeIterator.hasNext()) {
			Node node = (Node) nodeIterator.next();
			System.out.println("**********************");
			System.out.println("Node:"+node.getId());
			for(int i=0; i<node.getEdges().size(); i++)
			{
			  System.out.println("Edge"+node.getEdges().get(i));

			}
			System.out.println("**********************");

		}

		System.out.println("**************Target*************************");

		Iterator nodeIterator2 = target.iterator();
		while (nodeIterator2.hasNext()) {
			Node node = (Node) nodeIterator2.next();
			System.out.println("**********************");
			System.out.println("Node:"+node.getId());
			for(int i=0; i<node.getEdges().size(); i++)
			{
			  System.out.println("Edge"+node.getEdges().get(i));

			}
			System.out.println("**********************");

		}
		System.out.println("**********************");
		System.out.println("Edges in Graph source");

		Iterator nodeIterator3 = source.getEdges().iterator();
		while (nodeIterator3.hasNext()) {
			Edge edge = (Edge) nodeIterator3.next();
			System.out.println("**Edge: "+edge.getId());
			System.out.println("Start node: "+edge.getStartNode().getId());
			System.out.println("End node: "+edge.getEndNode().getId());

		}

		System.out.println("**********************");
		System.out.println("Edges in Graph target");

		Iterator nodeIterator4 = target.getEdges().iterator();
		while (nodeIterator4.hasNext()) {
			Edge edge = (Edge) nodeIterator4.next();
			System.out.println("**Edge: "+edge.getId());
			System.out.println("Start node: "+edge.getStartNode().getId());
			System.out.println("End node: "+edge.getEndNode().getId());
		}
		 */
	}

	/**
	 * parsing the edge list
	 *
	 */
	private void edgeListParser(EditPath editPath, Graph graph,
			LinkedList unUsedEdges, String edgeListString, String edgeMode) {
		// TODO Auto-generated method stub

		String[] getUnUsedEdges = edgeListString.split("/");
		for(int i=0; i<getUnUsedEdges.length ; i++)
		{
			Edge edg = new Edge(getUnUsedEdges[i],edgeMode, graph);
			unUsedEdges.add(edg);
			graph.getEdges().add(edg);
		}

	}

	/**
	 * parsing the node list
	 *
	 */
	private void nodeListParser(EditPath editPath, Graph graph, LinkedList unUsedNodes,
			String nodeListString, String edgeMode) {

		String[] getUnUsedNodes = nodeListString.split("/");
		for(int i=0; i<getUnUsedNodes.length ; i++)
		{

			Node nd = new Node(getUnUsedNodes[i],edgeMode,false,graph); // the node has not existed before
			unUsedNodes.add((GraphComponent)nd);
			graph.add(nd);

		}


		// TODO Auto-generated method stub

	}

	/**
	 * copy constructor for an existing edit path
	 * 
	 * @param e
	 */
	public EditPath(EditPath e) {
		this.init();
		this.source = e.getSource();
		this.target = e.getTarget();
		this.unUsedNodes1.addAll(e.getUnUsedNodes1());
		this.unUsedNodes2.addAll(e.getUnUsedNodes2());
		this.unUsedEdges1.addAll(e.getUnUsedEdges1());
		this.unUsedEdges2.addAll(e.getUnUsedEdges2());
		this.totalCosts = e.getTotalCosts();
		this.distortions.putAll(e.getDistortions());
		this.EdgeInverted.putAll(e.getEdgeInverted());
		this.noOfDistortedNodes=e.getNoOfDistortedNodes();
		//		this.NodeToIndiceG1.putAll(e.NodeToIndiceG1);
		//		this.NodeToIndiceG2.putAll(e.NodeToIndiceG2);
		//		this.EdgeToIndiceG1.putAll(e.EdgeToIndiceG1);
		//		this.EdgeToIndiceG2.putAll(e.EdgeToIndiceG2);
		//		this.nodecostmatrix = e.nodecostmatrix.clone();
		//		this.edgecostmatrix = e.edgecostmatrix.clone();
		//		this.StarredNode = e.StarredNode.clone();
		//		this.StarredEdge = e.StarredEdge.clone();
		this.GlobalNodeCost = e.GlobalNodeCost;
		this.GlobalEdgeCost = e.GlobalEdgeCost;

		this.BestMatchNodeG2.putAll(e.BestMatchNodeG2);
		this.BestMatchEdgeG2.putAll(e.BestMatchEdgeG2);

		this.BestMatchNodeG1.putAll(e.BestMatchNodeG1);
		this.BestMatchEdgeG1.putAll(e.BestMatchEdgeG1);


		//		BestMatchNodeG2G1.putAll(e.BestMatchNodeG2G1);
		//		BestMatchEdgeG2G1.putAll(e.BestMatchEdgeG2G1);
		//		
		//		BestMatchNodeG1G2.putAll(e.BestMatchNodeG1G2);
		//		BestMatchEdgeG1G2.putAll(e.BestMatchEdgeG1G2);
		
		
		this.BestCostDeletionNode = e.BestCostDeletionNode;
		this.BestCostDeletionEdge = e.BestCostDeletionEdge;
		

		this.BestCostInsertionNode = e.BestCostInsertionNode;
		this.BestCostInsertionEdge = e.BestCostInsertionEdge;

	}

	/**
	 * helper method for both constructors
	 */
	private void init() {
		this.unUsedEdges1 = new LinkedList();
		this.unUsedEdges2 = new LinkedList();
		this.unUsedNodes1 = new LinkedList();
		this.unUsedNodes2 = new LinkedList();
		this.distortions = new Hashtable();
		EdgeInverted = new Hashtable();
		this.totalCosts = 0.0;
		this.noOfDistortedNodes=0;
		HeuristicCosts=0.0;
		this.isheuristiccomputed=false;

		NodeToIndiceG1 = new HashMap<GraphComponent,Integer>();
		NodeToIndiceG2 = new HashMap<GraphComponent,Integer>();
		EdgeToIndiceG1 = new HashMap<GraphComponent,Integer>();
		EdgeToIndiceG2 = new HashMap<GraphComponent,Integer>();

		this.nodecostmatrix = new double[1][1];
		this.edgecostmatrix = new double[1][1];
		this.StarredNode = new int[1][1];
		this.StarredEdge = new int[1][1];

		BestMatchNodeG2 = new HashMap<GraphComponent,Double>();
		BestMatchEdgeG2 = new HashMap<GraphComponent,Double>();

		BestMatchNodeG1 = new HashMap<GraphComponent,Double>();
		BestMatchEdgeG1 = new HashMap<GraphComponent,Double>();

		BestMatchNodeG2G1 = new HashMap<GraphComponent,GraphComponent>();
		BestMatchEdgeG2G1 =  new HashMap<GraphComponent,GraphComponent>();

		BestMatchNodeG1G2 =  new HashMap<GraphComponent,GraphComponent>();
		BestMatchEdgeG1G2 =  new HashMap<GraphComponent,GraphComponent>();



	}

	/**
	 * adds a new distortion to this path
	 * 
	 * @param sComp
	 * @param tComp
	 */
	public void addDistortion(GraphComponent sComp, GraphComponent tComp) {
		isheuristiccomputed=false;
		this.totalCosts += Constants.costFunction.getCosts(sComp, tComp);
		if (sComp.getComponentId().equals(Constants.EPS_ID)) {
			GraphComponent eps = new GraphComponent(Constants.EPS_ID);
			//System.out.println("****DISTORTION : ("+eps.getComponentId()+" , "+tComp.getComponentId()+")");
			this.distortions.put(eps, tComp);
		} else {
			//System.out.println("****DISTORTION : ("+sComp.getComponentId()+" , "+tComp.getComponentId()+")");
			this.distortions.put(sComp, tComp);
		}
		
		if(sComp.isNode()==true){
			this.noOfDistortedNodes++;	
		}
		
//		if(tComp.isNode()==true){
//			this.noOfDistortedNodes++;	
//		}

		if (sComp.isNode() || tComp.isNode()) {
			
			this.unUsedNodes1.remove(sComp);
			// edgehandling (only once and only if comp=node!)
			//System.out.println("handling edges between "+sComp);
			Constants.edgeHandler.handleEdges(this, sComp, tComp);
		} else {
			this.unUsedEdges1.remove(sComp);
		}
		if (tComp.isNode()) {
			this.unUsedNodes2.remove(tComp);
		} else {
			this.unUsedEdges2.remove(tComp);
		}

		if (   sComp.getComponentId().equals(Constants.EPS_ID) == false &&  tComp.getComponentId().equals(Constants.EPS_ID) == false && 
				sComp.isNode()==false && tComp.isNode()==false && ((Edge)sComp) .isDirected()  ==true) {
			EdgeInverted.put(sComp, ((Edge)sComp).isInverted());
		}
	}

	/**
	 * @return true if this path is complete
	 */
	public boolean isComplete() {
		int remaining = this.unUsedNodes1.size();
		remaining += this.unUsedNodes2.size();
		//remaining += this.unUsedEdges1.size();
		//remaining += this.unUsedEdges2.size();
		return (remaining == 0);
	}

	/**
	 * @return true if this path has not used all start nodes yet
	 */
	public boolean hasStartNodes() {
		return (this.unUsedNodes1.size() > 0);
	}

	/**
	 * @return the total costs of this path
	 */
	public double getTotalCosts() {
		return totalCosts;
	}

	/**
	 * getters for the unused graphcomponents of this path
	 */
	public LinkedList getUnUsedNodes1() {
		return unUsedNodes1;
	}

	public LinkedList getUnUsedNodes2() {
		return unUsedNodes2;
	}

	public LinkedList getUnUsedEdges1() {
		return unUsedEdges1;
	}

	public LinkedList getUnUsedEdges2() {
		return unUsedEdges2;
	}

	/**
	 * @return the next node of source graph
	 */
	public Node getNext() {
		return (Node) this.unUsedNodes1.getFirst();
	}

	public Node getNextG2() {
		return (Node) this.unUsedNodes2.getFirst();
	}

	/**
	 * completes the actual path by inserting the remaining nodes of target
	 */
	public void complete() {
		LinkedList tempList = new LinkedList();
		tempList.addAll(this.unUsedNodes2);
		Iterator iter = tempList.iterator();
		while (iter.hasNext()) {
			Node w = (Node) iter.next();
			this.addDistortion(Constants.EPS_COMPONENT, w);
		}
		
		
		
//		tempList = new LinkedList();
//		tempList.addAll(getUnUsedEdges2());
//		iter = tempList.iterator();
//		while (iter.hasNext()) {
//			Edge w = (Edge) iter.next();
//			addDistortion(Constants.EPS_COMPONENT, w.getStartNode());
//			addDistortion(Constants.EPS_COMPONENT, w.getEndNode());
//			addDistortion(Constants.EPS_COMPONENT, w);
//		}
//		
//		tempList = new LinkedList();
//		tempList.addAll(getUnUsedEdges1());
//		iter = tempList.iterator();
//		while (iter.hasNext()) {
//			Edge w = (Edge) iter.next();
//			addDistortion(Constants.EPS_COMPONENT, w.getStartNode());
//			addDistortion(Constants.EPS_COMPONENT, w.getEndNode());
//			addDistortion(Constants.EPS_COMPONENT, w);
//		}
	
	}

	/**
	 * @return the mappings
	 */
	public Hashtable getDistortions() {
		return distortions;
	}

	/**
	 * @return the origin of
	 * @param mapped
	 */
	public GraphComponent getStart(GraphComponent mapped) {
		Enumeration enumeration = this.distortions.keys();
		while (enumeration.hasMoreElements()) {
			GraphComponent key = (GraphComponent) enumeration.nextElement();
			GraphComponent value = (GraphComponent) this.distortions.get(key);
			if (value.equals(mapped)) {
				return key;
			}
		}
		return null;
	}


	/**
	 * helper method for debuging: prints the path in the std. output
	 */
	public void printMe() {
		System.out.println("source-label: "+this.source.getId());
		System.out.println("target-label: "+this.target.getId());
		Enumeration enumeration = this.distortions.keys();
		while (enumeration.hasMoreElements()) {
			GraphComponent key = (GraphComponent) enumeration.nextElement();
			GraphComponent value = (GraphComponent) this.distortions.get(key);

			if(key.isNode() == false &&
					key.getId().equals(Constants.EPS_ID) ==false ){
				if(value.isNode() == false &&
						value.getId().equals(Constants.EPS_ID) ==false ){
					Edge e=(Edge) key;
					if( e.isDirected()==true){
						boolean isinverted = (Boolean) this.EdgeInverted.get(e);
						e.setInverted(isinverted);
						System.out.print( " \t inverted="+e.isInverted());
					}
				}
			}



			System.out.print(key.getComponentId() + "\t --> \t"+ value.getComponentId()+"\t\t"+Constants.costFunction.getCosts(key, value));


			if(key.isNode() == false &&
					key.getId().equals(Constants.EPS_ID) ==false ){
				if(value.isNode() == false &&
						value.getId().equals(Constants.EPS_ID) ==false ){
					Edge e=(Edge) key;
					if( e.isDirected()==true){
						boolean isinverted = (Boolean) this.EdgeInverted.get(e);
						e.setInverted(isinverted);
						System.out.print( " \t inverted="+e.isInverted());
					}
				}
			}


			System.out.println();


		}

	}

	/**
	 * asserts that all components are used
	 * 
	 * @return true if all components are used
	 */
	public boolean allUsed() {
		int remaining = this.unUsedNodes1.size();
		remaining += this.unUsedNodes2.size();
		remaining += this.unUsedEdges1.size();
		remaining += this.unUsedEdges2.size();
		if (remaining != 0){
			System.out.println(((Edge) this.unUsedEdges2.getFirst()).getComponentId());
		}
		return (remaining == 0);
	}

	public int getNumberOfNodeOps(){
		int space = 0;
		Enumeration enumeration = this.distortions.keys();
		while (enumeration.hasMoreElements()) {
			GraphComponent key = (GraphComponent) enumeration.nextElement();
			GraphComponent value = (GraphComponent) this.distortions.get(key);
			if (key.isNode() || value.isNode()){
				space++;
			}
		}
		return space;
	}

	public Graph getSource() {
		return source;
	}

	public Graph getTarget() {
		return target;
	}

	@Override
	public int compareTo(Object other) {
		// TODO Auto-generated method stub
		double nombre1 = ((EditPath) other).getTotalCosts(); 
		double nombre2 = this.getTotalCosts(); 
		if (nombre1 > nombre2)  return -1; 
		else if(nombre1 == nombre2) return 0; 
		else return 1; 

	}

	@Override
	/**
	 * @return the editPath as a string 
	 * EditPath To String converter
	 */
	public String toString() {		  

		/* Format:
		 *  < HeuristicCosts<>totalCosts<>getGraphId1<>getGraphId2<>getEdgesId1<>getEdgesId2<>getEdgesMode1<>getEdgesMode2<>
		 *  unUsedNodes1<>unUsedNodes2<>unUsedEdges1<>unUsedEdges2<>Distortions<>invertedEdges
		 */
		String str=""+HeuristicCosts+"<>"+totalCosts+"<>";
		str=str+source.getId()+"<>"+target.getId()+"<>";
		str=str+source.getEdgeId()+"<>"+target.getEdgeId()+"<>";
		str = str+ source.getEdgeMode()+"<>"+target.getEdgeMode();

		str=str+"<>";
		// Nodes1 Format:  <node1.toString()/node2.toString()/node3.toString()/.........etc>
		for(int i=0; i<unUsedNodes1.size();i++)
		{
			Node node1 = (Node) unUsedNodes1.get(i);
			str =str + node1.toString();
			if(i!=unUsedNodes1.size()-1)
			{
				str=str+"/";
			}
		}

		str=str+"<>";
		// Nodes2 Format:  <node1.toString()/node2.toString()/node3.toString()/.........etc>
		for(int i=0; i<unUsedNodes2.size();i++)
		{
			Node node2 = (Node) unUsedNodes2.get(i);
			str =str + node2.toString();
			if(i!=unUsedNodes2.size()-1)
			{
				str=str+"/";
			} 
		}

		str=str+"<>";
		// Edges1 Format:  <edge1.toString()/edge2.toString()/edge3.toString()/.........etc>
		for(int i=0; i<unUsedEdges1.size();i++)
		{
			Edge edge1 = (Edge) unUsedEdges1.get(i);
			str=str+edge1.toString();
			if(i!=unUsedEdges1.size()-1)
			{
				str=str+"/";
			}
		}


		str=str+"<>";
		// Edges2 Format:  <edge1.toString()/edge2.toString()/edge3.toString()/.........etc>
		for(int i=0; i<unUsedEdges2.size();i++)
		{
			Edge edge2 = (Edge) unUsedEdges2.get(i);
			str=str+edge2.toString();
			if(i!=unUsedEdges2.size()-1)
			{
				str=str+"/";
			}
		}

		str=str+"<>";
		// Distortions:  <distortion1/distortion2/distortion3/.........etc>

		// System.out.println("--Distortions");
		Enumeration enumeration = this.distortions.keys();

		int counter=0; 
		while (enumeration.hasMoreElements())
		{
			if(counter!=0)
			{
				str=str+"/";
			}	
			counter=1;
			GraphComponent key = (GraphComponent) enumeration.nextElement();
			GraphComponent value = (GraphComponent) this.distortions.get(key);
			//System.out.println("--key ="+key.getComponentId()+", value = "+value.getComponentId());

			// edge  --- edge

			if(key.isNode() == false  && value.isNode()==false){

				str=str +"Edge%";
				if(key.getComponentId().equals("eps_id"))
				{
					str=str+"eps_id"+">>";
				}
				else{
					Edge keyEdge = (Edge)key;
					str=str+keyEdge.toString()+">>";
					//System.out.println("-- ketEdge :"+keyEdge.toString());
				}
				if(value.getComponentId().equals("eps_id"))
				{
					str=str+"eps_id";

				}
				else{
					Edge valueEdge =(Edge)value;
					str=str+valueEdge.toString();

				}


			}
			// node 
			else{
				str= str+"Node%";
				if(key.getComponentId().equals("eps_id"))
				{
					str=str+"eps_id"+">>";
				}
				else{
					Node keyNode = (Node)key;
					str=str+keyNode.toString()+">>";
				}
				if(value.getComponentId().equals("eps_id"))
				{
					str=str+"eps_id";
				}
				else{
					Node valueNode =(Node)value;
					str=str+valueNode.toString();
				}
			}
		}

		str=str+"<>";
		// invertedEdges Format: <invertedEdge1/invertedEdge2/ ...... >		
		Enumeration enumeration2 = this.EdgeInverted.keys();
		int counter1=0; 
		while (enumeration.hasMoreElements())
		{
			if(counter1!=0)
			{
				str=str+"/";
			}
			counter=1;
			Edge key = (Edge) enumeration.nextElement();
			String value = this.EdgeInverted.get(key).toString();
			str=str+key.toString()+"="+value;
		}
		return str;
	}

	public double ComputeHeuristicCostsAssignmentLAP() {
		// TODO Auto-generated method stub
		HeuristicCosts=0.0;
		int n1,n2;
		int e1,e2;
		n1 = this.unUsedNodes1.size();
		n2 = this.unUsedNodes2.size();

		e1 = this.unUsedEdges1.size();
		e2 = this.unUsedEdges2.size();
		//System.out.println("********************************************");

		double leastexpensivenodesub = ComputeMinSubstituionLAP(unUsedNodes1,unUsedNodes2);
		HeuristicCosts = leastexpensivenodesub;

		double leastexpensiveedgesub = ComputeMinSubstituionLAP(unUsedEdges1,unUsedEdges2);
		HeuristicCosts += leastexpensiveedgesub;

//		int nbdeletions=Math.max(0, n1-n2);
//		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
//		int nbinsertion=Math.max(0,n2-n1);
//		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);
//
//		nbdeletions=Math.max(0, e1-e2);
//		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
//		nbinsertion=Math.max(0,e2-e1);
//		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);


		return HeuristicCosts;

	}
	
	
	public double ComputeHeuristicCostsAssignmentMunkres() {
		// TODO Auto-generated method stub
		HeuristicCosts=0.0;
		int n1,n2;
		int e1,e2;
		n1 = this.unUsedNodes1.size();
		n2 = this.unUsedNodes2.size();

		e1 = this.unUsedEdges1.size();
		e2 = this.unUsedEdges2.size();
		//System.out.println("********************************************");

		double leastexpensivenodesub = ComputeMinSubstituionMunkres(unUsedNodes1,unUsedNodes2);
		HeuristicCosts = leastexpensivenodesub;

		double leastexpensiveedgesub = ComputeMinSubstituionMunkres(unUsedEdges1,unUsedEdges2);
		HeuristicCosts += leastexpensiveedgesub;

//		int nbdeletions=Math.max(0, n1-n2);
//		HeuristicCosts += ComputeNbDeletion(unUsedNodes1,nbdeletions);
//		int nbinsertion=Math.max(0,n2-n1);
//		HeuristicCosts += ComputeNbInsertion(unUsedNodes2,nbinsertion);
//
//		nbdeletions=Math.max(0, e1-e2);
//		HeuristicCosts += ComputeNbDeletion(unUsedEdges1,nbdeletions);
//		nbinsertion=Math.max(0,e2-e1);
//		HeuristicCosts += ComputeNbInsertion(unUsedEdges2,nbinsertion);


		return HeuristicCosts;

	}


}
