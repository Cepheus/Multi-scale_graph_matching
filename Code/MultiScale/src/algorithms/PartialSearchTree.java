package algorithms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import util.EditPath;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.Node;
import util.UniversalCostFunction;
import util.UniversalEdgeHandler;
import util.UnlabeledCostFunction;
import xml.XMLParser;
import xml.XMLWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;




/**
 * @author Zeina Abu-Aisheh
 * 
 * This program implements the partial search tree of Graph Edit Distance, before running the program, one should specify the level of the search tree, 
 * source and target graphs 
 * 
 */
public class PartialSearchTree {



	private static final int MegaBytes = 1048576;
	ArrayList<EditPath> OPEN; // Tiens � jour la liste des noeud non encore utilis�s. Le score du chemin.
	
	
	private Graph G1; //Graph 1	
	private Graph G2; //Graph 2
	
	private Node CurNode; //Current node
	
	private int G2NbNodes; //Number of nodes of G2
	private int G1NbNodes; //Number of nodes of G2
	public int jobNo; // Level of search tree
	private int saturation;
	public double upperBound;

	private long javaFreeMemory;
	private long javaMaxMemory;
	private long javaTotalMemory;
	private long javaUsedMemory;
	public long NoOfDiscardedEditPaths;
    public boolean foundLocalEditPath;
	boolean debug;
	private int memFilled;
	
    public EditPath pLocalMin;
    public int N;
    public int noOfCallsMinCost;
    public int kEditPaths;


	////////////////////////////////////////////////////////////////////////////////////////////
	public PartialSearchTree(Graph G1, Graph G2,ICostFunction costfunction, IEdgeHandler edgehandler,int jobNo,boolean debug, Context context) throws IOException{
			
		//System.out.println("************************************Partial Search Tree************************************");
		this.jobNo = jobNo;
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		G1NbNodes = G1.size();
		OPEN = new ArrayList<EditPath>();
		foundLocalEditPath = false;
		saturation = 20;
		NoOfDiscardedEditPaths=0;
		memFilled=0;
		noOfCallsMinCost = 0;
		EditPath p = new EditPath(G1,G2); //EditPath between G1 and G2
		pLocalMin = dummyUpperBound(p, context);
		upperBound = pLocalMin.getTotalCosts();
		inti(context); // constructing the 1st level loop(); // contructing other levels of the search treeof the search tree
	
	}
	

	public PartialSearchTree(EditPath editPath ,ICostFunction costfunction, IEdgeHandler edgehandler, boolean debug,Context context , double upperBound) throws IOException{

	//	System.out.println("************************************Partial PATH (Edit Path)************************************");


		javaMaxMemory = Runtime.getRuntime().maxMemory() / MegaBytes;
		////////////////////////////////////////////////////////////////////////////
	
		this.debug = debug;
		this.jobNo = jobNo+1;
		OPEN = new ArrayList<EditPath>();		
		this.OPEN.add(editPath); // adding the 1st editPAth to the list OPEN
		foundLocalEditPath = false;
		saturation = 20;
	//	pLocalMin = null;
		this.upperBound =  upperBound;
		NoOfDiscardedEditPaths=0;
		memFilled =0;
		//pmin = dummyUpperBound(editPath, context);
		//upperBound = pmin.getTotalCosts();
		this.N = 50;
		this.kEditPaths = -1;
		noOfCallsMinCost = 0;
		partialMatcher(N,context); // contructing other levels of the search tree
		if(memFilled==1 && kEditPaths!=-1)
		{
			Collections.sort(OPEN);
			
			removeSomeEditPaths(kEditPaths,context);
				
		}
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	  
	private void removeSomeEditPaths(int kEditPaths2, Context context) {
		// TODO Auto-generated method stub
		int size = OPEN.size(); 
		int delta =size-kEditPaths2;
		if(delta>0)
		{
			for(int i=0; i<delta ; i++){
				OPEN.remove(size-i-1);
			}
				
			
		}
			
		if(debug==true) System.out.println("After DELETING pairs" + OPEN.size());
		
	}
	


	



	private EditPath dummyUpperBound(EditPath p, Context context) {
		// TODO Auto-generated method stub
		
		
		// Substitution case
		//System.out.println("Substitution phase");
		int remainingG1 = p.getUnUsedNodes1().size();
		int remainingG2 = p.getUnUsedNodes2().size();
		
		int loop = 0;
		boolean isDeletion = false;
		boolean isInsertion = false;
		if(remainingG1==remainingG2 || remainingG1 < remainingG2)
		{
			//substitution or insertion
			loop = remainingG1;
			if(remainingG1 < remainingG2)
			{
				isInsertion = true;
			}

		}
		else
		{
			// deletion (remainingG2 < remainingG1)
			loop = remainingG2;
			isDeletion = true;
		}
		
		
		for(int i=0;i<loop;i++)
		{
			context.progress();
     		Node v = p.getNextG2();
			this.CurNode = p.getNext();
			p.addDistortion(this.CurNode, v);
		}
		

		//Deletion case
		if(isDeletion == true)
		{
			//System.out.println("Deletion phase");
			int noOfDeletedNodes = p.getUnUsedNodes1().size()- p.getUnUsedNodes2().size(); 
			for(int i=0;i<noOfDeletedNodes;i++)
			{
				context.progress();
				this.CurNode = p.getNext();
				p.addDistortion( this.CurNode , Constants.EPS_COMPONENT);
		
			}
		}
		
		//Insertion case
		else if(isInsertion == true)
		{
			//System.out.println("Insertion phase");
			int noOfInsertedNodes = p.getUnUsedNodes2().size()- p.getUnUsedNodes1().size(); 
			for(int i=0;i<noOfInsertedNodes;i++)
			{
				context.progress();
				this.CurNode = p.getNextG2();
				p.addDistortion( Constants.EPS_COMPONENT , this.CurNode );	
			}
		}
		
		return p;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////

	private void partialMatcher(int N, Context context) {
		
		int noOfIterations = 0;
		int iter =  0;
		while(true == true){
			
			iter++;
		  //  if(debug==true)	System.out.println("No of iterations -----------------------------"+noOfIterations);
			
			// each N iterations, search for a better upperBound and save it 
/*			if(noOfIterations > N)
			{
				updatePmin(context);
	      		noOfIterations=-1;
			}		
			noOfIterations++;
*/					
			if(OPEN.isEmpty() == true)
			{
				if(debug==true)
				{
					System.out.println("No more candidates, no complete solution could be found");
					System.out.println("Please check your graph data");
				}
			
				return;
			}
			
			//EditPath pmin =findmincostEditPathWithHeuristic(OPEN, context);
			
			EditPath pmin =findmincostEditPathWithHeuristic(context,noOfIterations,N);
			noOfIterations++;
			if(noOfIterations > N)
			{
	      		noOfIterations=0;
			}
			
			if(debug==true)
			{
				System.out.println("plocalmin (totalCost)::: "+pmin.getTotalCosts());
				System.out.println("plocalmin (heuristicCost) ::: "+pmin.getHeuristicCosts());
				
			}
			
			OPEN.remove(pmin);

			if(pmin.isComplete() == true){
				if(debug==true)System.out.println("OPEN ==="+OPEN.size());
				foundLocalEditPath = true;
				this.OPEN.clear(); // clear the OPEN list
				//pmin = pLocalMin;
				upperBound = pmin.getTotalCosts();
				if(debug==true)System.out.println("COMPLETED............");
				return; // return to the loop () function
			}
			else
			{
			
				if(pmin.getUnUsedNodes1().size() > 0)
				{
					this.CurNode=pmin.getNext();
				//	if(debug==true) System.out.println("Current Node="+this.CurNode.getId());
				
					LinkedList<Node>UnUsedNodes2= pmin.getUnUsedNodes2();
					for(int i=0;i<UnUsedNodes2.size();i++)
					{
						 
						// Substitution case .......
						context.progress();
						EditPath newpath = new EditPath(pmin);
						Node w = UnUsedNodes2.get(i);
						addSubstitution(newpath,w);
						if(memFilled ==1)
						{
							return;
						}
				    }
							
					// Deletion case ...
					context.progress();
					EditPath newpath = new EditPath(pmin);
					addDeletion(newpath);
					if(memFilled ==1)
					{
						return;
					}
				}
				else{
					
					//Insertion case ...
					context.progress();
					EditPath newpath = new EditPath(pmin);
					addInsertion(newpath);
					if(memFilled ==1)
					{
						return;
					}
	
				}
											
			}
			//if(debug==true) System.out.println("NO OF ITERATIONS ::::::"+noOfIterations);

		
		}//boucle while

		
	}
 ///////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void updatePmin(Context context) {
		// TODO Auto-generated method stub
		EditPath pLocalMin1 = findDeepestEditPath(context);
		
	
		
  		if(debug==true)
  		{
  			System.out.println("PatrialMatcher ::: COST ::::"+pLocalMin1.getTotalCosts());
      		
  		}
  		
  		if(pLocalMin1 != null)
  		{
  			EditPath tempLocalMin = new EditPath(pLocalMin1);
  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: extracting"+pLocalMin.getTotalCosts()+"("+pLocalMin.getNoOfDistortedNodes()+")");
  			tempLocalMin = dummyUpperBound(tempLocalMin, context);
  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
    	    //if(debug==true)
         	if(tempLocalMin.getTotalCosts() < upperBound)
         	{
         		// delete the previous pmin from the OPEN set.
         	//	OPEN.remove(pmin);
         		
         		// change the current pmin 
         		pLocalMin = new EditPath(tempLocalMin);
	      		upperBound = tempLocalMin.getTotalCosts();
	      		if (debug==true ) System.out.println("PatrialMatcher ::::: UPDATING upper bound to be "+upperBound);
	      		
	      		// add it to the OPEN set ..
	      	//	OPEN.add(pmin);
         	}
      		
  		}
	}


	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditPath findDeepestEditPath(Context context) {
		// TODO Auto-generated method stub

			// TODO Auto-generated method stub
			int i=0;
			int nbpaths = OPEN.size();
			double maxvalue = Double.MIN_VALUE;
			int indexmax = -1;

			for(i=0;i<nbpaths;i++){
				EditPath p = OPEN.get(i);
				if(p.getDistortions().size() >maxvalue && p.getTotalCosts()<upperBound){
					maxvalue = p.getDistortions().size();
					indexmax = i;
				}
			}
			if(indexmax!=-1) 
			{
				//System.out.println("Deepest edit Path has a cost ="+OPEN.get(indexmax)+" and an index: "+indexmax);
				return OPEN.get(indexmax);
			}
			else 
			{
				return null;
			}
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void addSubstitution(EditPath newpath, Node w) {
		// TODO Auto-generated method stub
		newpath.addDistortion(this.CurNode, w);
		newpath.setID(this.CurNode.getId()+"_"+w.getId());
		int succeed = AddEditPath(newpath);
		if(succeed==-1) 
		{
			memFilled= 1;
			if(debug==true) System.out.println("MEMORY SATURATED ..........");
			return;
		}
		else if(succeed==1)
		{
			this.OPEN.add(newpath);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void addDeletion(EditPath newpath) {
		// TODO Auto-generated method stub
		newpath.addDistortion(this.CurNode, Constants.EPS_COMPONENT);
		newpath.setID(CurNode.getId()+"_eps");
		int succeed = AddEditPath(newpath);
		if(succeed==-1) 
		{
			memFilled = 1;
			if (debug == true)System.out.println("MEMORY SATURATED ..........");
			return;
		}
		else if(succeed==1)
		{
			this.OPEN.add(newpath);
		}	
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void addInsertion(EditPath newpath) {
		// TODO Auto-generated method stub

		newpath.complete();	
		
		int succeed = AddEditPath(newpath);
		if(succeed==-1) 
		{
			memFilled = 1;
			if (debug == true)System.out.println("MEMORY SATURATED ..........");
			return;
		}
		else if(succeed==1)
		{
			this.OPEN.add(newpath);
			
			// update pmin
			pLocalMin = new EditPath(newpath);
			upperBound = newpath.getTotalCosts();
		}
				
		if(debug==true) 
		{
			System.out.println("We complete the path by inserting all remaining nodes of G2 CurNode=");
			newpath.printMe();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private int AddEditPath(EditPath newpath) {
		// TODO Auto-generated method stub

		int succeed = 0;
		
		javaUsedMemory =  getJavaUsedMemory();
				
		if( javaUsedMemory  <  javaMaxMemory)
		{
			if(newpath.getTotalCosts()+newpath.getHeuristicCosts() < upperBound)
			{
		
				succeed=1;
			}
			else
			{
			
				NoOfDiscardedEditPaths++;
			}
					
		}
		else
		{
			succeed= -1 ; 
		
		}
		return succeed;
		
	}

////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditPath findmincostEditPath(ArrayList<EditPath> oPEN2, Context context) {
		// TODO Auto-generated method stub
		int i=0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		int indexmin = -1;

		for(i=0;i<nbpaths;i++){
			context.progress();
			EditPath p = OPEN.get(i);
			if(p.getTotalCosts() <minvalue){
				minvalue = p.getTotalCosts();
				indexmin = i;
			}
		}
		return OPEN.get(indexmin);
	}
	

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		private EditPath findmincostEditPathWithHeuristic(Context context, int noOfIterations, int N) {
		// TODO Auto-generated method stub
			int i=0;
			int nbpaths = OPEN.size();
			double minvalue = Double.MAX_VALUE;
			double h=0.0,g=0.0;
			int indexmin = -1;
			noOfCallsMinCost++;
			
			double maxvalue = Double.MIN_VALUE;
			int indexmax = -1;
			
			//System.out.println("OPEN size :: "+nbpaths);
			
			for(i=0;i<nbpaths;i++)
			{
			
				EditPath p = OPEN.get(i);
				g = p.getTotalCosts(); // computing the cost of the edit path from the root till the current node...
				
				
				//double start =System.currentTimeMillis();
				h=p.ComputeHeuristicCosts();
				//h = p.ComputeHeuristicCostsAssignment();
				//	double end =System.currentTimeMillis();
				//System.out.println("OPEN("+i+") :: "+OPEN.get(i).getTotalCosts());
				
				// estimating the cost of the edit path from the current node till the leaf nodes
				
				if((g+h) <minvalue){
					minvalue = g+h;
					indexmin = i;
					//System.out.println("**indexmin :::"+indexmin + "(g+h) : "+(g+h));
				}
				if(noOfIterations == N)
				{
					if(p.getDistortions().size() >maxvalue && p.getTotalCosts()<upperBound)
					{
						maxvalue = p.getDistortions().size();
						indexmax = i;
					}
				}
				
			
			}
			
			//////////////////////////////////////////////////////////////////////////////////////////////
			
			EditPath pMinHeuristic = OPEN.get(indexmin);
			//System.out.println("before :: pMinHeuristic :::"+pMinHeuristic.getTotalCosts());
			if(noOfIterations == N)
			{

				EditPath pLocalDeepestPath = OPEN.get(indexmax);
				
				EditPath dummyLocalDeepestPath = new EditPath(pLocalDeepestPath);
				
				if(debug==true)
				{
					System.out.println("PatrialMatcher ::: COST ::::"+dummyLocalDeepestPath.getTotalCosts());
				
				}
				
				if(pLocalDeepestPath != null)
				{
					//	if (debug==true ) System.out.println("PatrialMatcher ::::: extracting"+pLocalMin.getTotalCosts()+"("+pLocalMin.getNoOfDistortedNodes()+")");
					dummyLocalDeepestPath = dummyUpperBound(dummyLocalDeepestPath, context);
					//	if (debug==true ) System.out.println("PatrialMatcher ::::: Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
					//if(debug==true)
					if(dummyLocalDeepestPath.getTotalCosts() < upperBound)
					{
						// delete the previous pmin from the OPEN set.
						//OPEN.remove(pmin);
				
						// change the current pmin 
						pLocalMin = new EditPath(dummyLocalDeepestPath);
						upperBound = pLocalMin.getTotalCosts();
						if (debug==true ) System.out.println("PatrialMatcher ::::: UPDATING upper bound to be "+upperBound);
				
					}
				
				}
				
				///////////////////////////////////////////////////////////////////////////////////////////////:
				//System.out.println("after :: pMinHeuristic :::"+pMinHeuristic.getTotalCosts());

			}
			return pMinHeuristic;
		}

	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// the initialization function
	private void inti(Context context) throws IOException {
		// TODO Auto-generated method stub
		
		// Node Substitution
		for(int i=0;i<G2NbNodes;i++){
		
		    context.progress();
		// System.out.println("progressing....");  
			EditPath p = new EditPath(G1,G2); //EditPAth between G1 and G2
			Node v = (Node)G2.get(i);
			this.CurNode = p.getNext();
			p.addDistortion(this.CurNode, v);
		//	if(debug==true) System.out.println("Substitution CurNode G1 and ith node of G2= ("+this.CurNode.getId()+"  "+v.getId()+")   Cost ="+p.getTotalCosts());
			OPEN.add(p);

			
		}

		//Node Deletion
		
		EditPath p = new EditPath(G1,G2);
		this.CurNode = p.getNext();
		p.addDistortion(this.CurNode, Constants.EPS_COMPONENT); 
	/*	if(debug==true) 
		{
			System.out.println("Deletion CurNode="+this.CurNode.getId() + " -- Cost = "+p.getTotalCosts());
			System.out.println("DISTORTED NODES ======="+p.getNoOfDistortedNodes());
			
		}
		*/
		OPEN.add(p);
	
	}
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	private long getJavaUsedMemory() {
		// TODO Auto-generated method stub
		javaTotalMemory = Runtime.getRuntime().totalMemory()/MegaBytes;
		javaFreeMemory = Runtime.getRuntime().freeMemory() / MegaBytes;
		long mem =  (javaTotalMemory - javaFreeMemory)+saturation;
		return mem;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("Before executing the PartialSearchTree function");
		MedLetterDataSet(); // gxl graphs 
		System.out.println("After executing the PartialSearchTree function ");	
		
	}

	
	private static void MedLetterDataSet() {
		// TODO Auto-generated method stub

		String sg1;
		String sg2;
		SpeedEvalTest SET = new SpeedEvalTest("PartialSearchTree.txt");
		int treeLevel=5;

		sg1 = "./data/Letter/MED/AP1_0100.gxl";
	    sg2 = "./data/Letter/MED/AP1_0000.gxl";
		
	    Constants.costFunction= new LetterCostFunction(10,10,0.5);

		 // set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler();//UnDirectedEdgeHandler(); 
		
		XMLParser xmlParser = new XMLParser();
		Graph g1 = null;
		Graph g2 = null;
		try {
			g1 = xmlParser.parseGXL(sg1);
			g2= xmlParser.parseGXL(sg2);
		//	SET.StartChrono();
		//	PartialSearchTree GED = new PartialSearchTree(g1,g2,Constants.costFunction,Constants.edgeHandler,treeLevel,true);
	//		GED.saveOPENList();
		//	System.out.println("OPEN SIZE ::::::"+GED.OPEN.size());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SET.CloseStream();
	}
	

}
