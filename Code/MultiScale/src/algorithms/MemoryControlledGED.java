package algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.naming.Context;

import util.EditPath;
import util.GRECCostFunction;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.Node;
import util.UniversalEdgeHandler;
import xml.XMLParser;

public class MemoryControlledGED {

	/**
	 * @param args
	 */
	

	private static final int MegaBytes = 1048576;
	
	ArrayList<EditPath> OPEN; // Tiens ï¿½ jour la liste des noeud non encore utilisï¿½s. Le score du chemin.
	private Graph G1; //Graph 1	
	private Graph G2; //Graph 2
	private Node CurNode; //Current node
	
	private int G2NbNodes; //Number of nodes of G2
	private int G1NbNodes; //Number of nodes of G2
	
	public int openCounterSize; // a variable that tracks the maximum size of the set OPEN
	public  int editPathCounter; // a variable that tracks number of edit paths
	
	private EditPath BestEditpath; // Zeina: The best answer
	
	boolean debug;
	
	private double javaFreeMemory;
	private double javaMaxMemory;
	private double javaTotalMemory;
	private double javaUsedMemory;
	
	public long NoOfDiscardedEditPaths;


    public int memFilled;
    public EditPath pmin; // pmin locally 

    public int dataSetNumber;
    public int methodNumber;
    
    public String testGraphClass;
    public String trainingGraphClass;
    
    public double upperBound;
    public double satMemory;
    SpeedEvalTest SPEEDMatcher;
    SpeedEvalTest SPEEDReadWrite;
    SpeedEvalTest Parsing;
    SpeedEvalTest Garbage;
    
    SpeedEvalTest HeuristicCosts;
    SpeedEvalTest HeuristicCostsAssign;

    public int noOfCallsMinCost;
   
    
    public int kEditPaths;
    public int N;
    
  
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	
	public MemoryControlledGED(Graph G1, Graph G2, double satMemory,
			int kEditPaths, int nIterations, ICostFunction costFunction,
			IEdgeHandler edgeHandler, boolean debug) throws IOException, InterruptedException {
		// TODO Auto-generated constructor stub
		
		this.satMemory = satMemory;

		//this.N = (int)Double.MAX_VALUE;
		this.N = nIterations;
		this.kEditPaths = kEditPaths;		
		this.javaMaxMemory = Runtime.getRuntime().maxMemory() / MegaBytes;
		
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		G1NbNodes = G1.size();
		OPEN = new ArrayList<EditPath>(); // empty OPEN list
		pmin = null; // empty pmin 
		
		SPEEDMatcher = new SpeedEvalTest("./speedMatcher.csv");
		SPEEDReadWrite = new SpeedEvalTest("./speedrw.csv");
		Parsing = new SpeedEvalTest("./speedParsing.csv");
		Garbage = new SpeedEvalTest("./speedGC.csv");
		HeuristicCosts = new SpeedEvalTest("./speedHeuristic1.csv");
		HeuristicCostsAssign = new SpeedEvalTest("./speedAssignment.csv");
		
		HeuristicCosts.GetPrintStream().println("TotalCost,HeuristicCost,UpperBound");
		
		
		initialization(); // constructing the 1st level loop(); // contructing other levels of the search treeof the search tree
		NoOfDiscardedEditPaths=0;	
		SPEEDMatcher.GetPrintStream().println("BranchMatcher");
		SPEEDReadWrite.GetPrintStream().println("speedRW");
		loop(); // contructing other levels of the search tree
		
		SPEEDMatcher.CloseStream();
		SPEEDReadWrite.CloseStream();
		memFilled = 0;
	}


	private void initialization() throws FileNotFoundException {
		// TODO Auto-generated method stub
	//	System.out.println("JAVA-MaxMemory::::"+javaMaxMemory);
		
		/* 
		 * 1st step
		 */
		init(); 
		
		EditPath p = new EditPath(G1,G2); //EditPath between G1 and G2
		
		
		/* 
		 * 2nd step
		 */
		// For the moment consider dummyUpperBound(p) as the optimal solution
		pmin = dummyUpperBound(p); 
		upperBound = pmin.getTotalCosts();
		//OPEN.add(pmin); // adding the upper bound to the OPEN list
	
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  

	private void init() {
		// TODO Auto-generated method stub
		
		// Node Substitution
		for(int i=0;i<G2NbNodes;i++){
			EditPath p = new EditPath(G1,G2); //EditPath between G1 and G2
			Node v = (Node)G2.get(i);
			this.CurNode = p.getNext();
			p.addDistortion(this.CurNode, v);
			OPEN.add(p);			
			
		}

		//Node Deletion		
		EditPath p = new EditPath(G1,G2);
		this.CurNode = p.getNext();
		p.addDistortion(this.CurNode, Constants.EPS_COMPONENT); 
		OPEN.add(p);
		
	}




	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	private EditPath dummyUpperBound(EditPath p) {
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
				this.CurNode = p.getNextG2();
				p.addDistortion( Constants.EPS_COMPONENT , this.CurNode );	
			}
		}
		
		return p;
	}

	

	
	 /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  
	private void loop() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		File fEditPaths = new File("./data/fEditPaths.txt");
		double start = 0.0, end = 0.0;
		//******************************************************************************
		// Delete the file if it already exists and create a new file ...
		if(fEditPaths.delete()){
			if(debug == true)	System.out.println(fEditPaths.getName() + " is deleted!");
		}else{
			if(debug == true) System.out.println("Delete operation is failed.");
		}
		fEditPaths.createNewFile();
		
		//*****************************************************************************
		//int i=0;
		while(true)
		{


			if(debug == true) System.out.println("BEFORE partialMatcher:::upper bound :::"+upperBound);
			memFilled = 0;
			
			/* 
			 * 3rd step
			 */
			start =System.currentTimeMillis();
			partialMatcher(this.N);
			end =System.currentTimeMillis();
			
			
			if(debug == true) System.out.println("AFTER partialMatcher:::upper bound :::"+upperBound);
			SPEEDMatcher.GetPrintStream().println((end-start));
			
			/* 
			 * 4th step
			 */
			int isFinished  = MFGED_Finished(fEditPaths);
			
			if(debug==true) System.out.println("FINISHED :::"+isFinished);
			if ( isFinished ==1)
			{
				if (debug == true) System.out.println("COMPLETED Task...............");
				start =System.currentTimeMillis();
				System.gc();
				end =System.currentTimeMillis();
				Garbage.GetPrintStream().println((end-start));
				return;

			}
			else
			{
				 // Memory saturation or the fEditPaths is not empty yet(i.E. the global sol is not found yet)
				//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
				
				// Memory Saturation
		      	if( memFilled ==1)
				{
		      		/* 
					 * 5th step
					 */
		      		changeUpperBound();
					
		      		/* 
					 * 6th step
					 */
		      		StoreOpen(fEditPaths, kEditPaths);
		      		
		      		/* 
					 * 7th step
					 */
					CleanOPEN();
					
				}
		      	
		       // System.out.println("Finished ??? " + MFGED_Finished(fEditPaths));
	        	
		      	// Keep reading the file fEditPaths until there is no editPath inside....
		      	while( MFGED_Finished(fEditPaths)!=1)
				{
	      			if(debug==true) System.out.println("MFGED not finished ... continue :-)");
					 start =System.currentTimeMillis();	
				     readWrite(fEditPaths); 
				     end =System.currentTimeMillis();
					 SPEEDReadWrite.GetPrintStream().println((end-start));	
				}
 
				//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
			}
			
	} // end while
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void changeUpperBound()
	{		
	  		// TODO Auto-generated method stub
			EditPath pLocalMin = findDeepestEditPath();
			
		
			
	  		if(debug==true)
	  		{
	  			System.out.println("PatrialMatcher ::: COST ::::"+pLocalMin.getTotalCosts());
	      		
	  		}
	  		
	  		if(pLocalMin != null)
	  		{
	  			EditPath tempLocalMin = new EditPath(pLocalMin);
	  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: extracting"+pLocalMin.getTotalCosts()+"("+pLocalMin.getNoOfDistortedNodes()+")");
	  			tempLocalMin = dummyUpperBound(tempLocalMin);
	  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
	    	    //if(debug==true)
	         	if(tempLocalMin.getTotalCosts() < upperBound)
	         	{
	         		// delete the previous pmin from the OPEN set.
	         	//	OPEN.remove(pmin);
	         		
	         		// change the current pmin 
	         		pmin = new EditPath(tempLocalMin);
		      		upperBound = tempLocalMin.getTotalCosts();
		      		if (debug==true ) System.out.println("PatrialMatcher ::::: UPDATING upper bound to be "+upperBound);

	         	}
	      		
	  		}
	  		
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void CleanOPEN() {
		// TODO Auto-generated method stub
		if (debug == true) System.out.println("::::::Before GC ::" +getJavaUsedMemory());
		OPEN = null;
		double start =System.currentTimeMillis();
		System.gc();
		double end =System.currentTimeMillis();
		Garbage.GetPrintStream().println((end-start));
		if (debug == true) System.out.println("::::::After GC ::" +getJavaUsedMemory());
		OPEN=  new ArrayList<EditPath>(); 
	
	}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditPath findmincostEditPath() {
		// TODO Auto-generated method stub
		int i=0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		int indexmin = -1;

		for(i=0;i<nbpaths;i++){
			EditPath p = OPEN.get(i);
			if(p.getTotalCosts() <minvalue){
				minvalue = p.getTotalCosts();
				indexmin = i;
			}
		}
		return OPEN.get(indexmin);
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditPath findDeepestEditPath() {
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
	

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void readWrite(File fEditPaths) {

		EditPath p= null;
		//int t=0;
		try {

		  //Construct the new file that will later be renamed to the original filename.
		  File tempFile = new File(fEditPaths.getAbsolutePath() + ".tmp");
		  tempFile.createNewFile();
		  
		  if(debug == true)System.out.println(" temp file :::" +fEditPaths.getAbsolutePath() + ".tmp");
		 // BufferedReader br = new BufferedReader(new FileReader(fEditPaths), 1024*80);
		  BufferedReader br = new BufferedReader(new FileReader(fEditPaths));
		  
		  String line = null;
		  double start;
		  double end;
		  //Read from the original file and write to the new
		  //unless content matches data to be removed.
		  
		  int i =0;
		  while ((line = br.readLine()) != null) 
		  {
			  // System.out.println("Line ("+i+")"+line);				 
			   i++;		  
			   StringTokenizer itr = new StringTokenizer(line);
			   double editPathCost =Double.parseDouble(itr.nextToken());
			   double editHeuristicPathCost =Double.parseDouble(itr.nextToken());
			   double cost = editPathCost+editHeuristicPathCost;
			   if(debug==true) System.out.println("EditPathCost::::::"+cost);
			   
			   if (cost < upperBound) 
			   {
			    	start =System.currentTimeMillis();
			    	EditPath pi = new EditPath(itr.nextToken());
			    	end =System.currentTimeMillis();
					Parsing.GetPrintStream().println((end-start));
					
					//////////////////////////////////////////
					// Remove the previous pmin as it will never help further
				/*	if(OPEN.size()>0)
					{
				    	if(p.equals(pmin)==false)
						{
							OPEN.remove(p);
						}
						
					}
				*/	
					////////////////////////////////////////////:
					
					//Adding pi and pmin to the OPEN set before calling partialMatcher
			    	OPEN.add(pi);
			    	//OPEN.add(pmin);
			    	//p = pmin;
			    	
			    	if(debug == true) System.out.println("OPEN "+OPEN.size());
			    	if(debug == true)
			    	{
			    		System.out.println("Before PartialMatcher:::"+OPEN.size());
			    		System.out.println("BEFORE partialMatcher:::upper bound :::"+upperBound);
			    	}
			    	memFilled = 0;
			    	
			    	// Calling PartialMatcher
			    	///////////////////////////////////////////////////
			    	start =System.currentTimeMillis();
			    	partialMatcher(this.N);
			    	end =System.currentTimeMillis();
			    	SPEEDMatcher.GetPrintStream().println((end-start));
			    	//////////////////////////////////////////////////
			    	
			    	if(debug==true) System.out.println("AFTER partialMatcher:::upper bound :::"+upperBound);
			    	javaUsedMemory =  getJavaUsedMemory();
			     	
			    	if(OPEN.size()==0)
			    	{
			     		CleanOPEN();
			    	}
			    	
			   
			    	//if memory saturation happens
			      	if( memFilled ==1)
					{
			      		changeUpperBound();
			      		StoreOpen(tempFile, kEditPaths);
			      		CleanOPEN();
					}
			      	else 
			      	{
			      		if(debug==true)System.out.println("Memory is still not saturated");
			      	}
		
				}
			    else
			    {
			    	//if(debug==true) System.out.println("HERE !!!!!!!!!!!");
			    	NoOfDiscardedEditPaths++;
			    }
			    
		  } // end While

		  br.close();

		  
		  if(debug==true) System.out.println("Finishing tracing all the editPaths in fEditPaths");
		  
		  if(debug == true) System.out.println("OPEN .............."+OPEN.size());
		  if(OPEN.size()>0)
		  {
			  if(debug==true){
				  System.out.println("Memory is still not saturated");
				  System.out.println("Saving all the editPaths of OPen in tempFile");
			  }
			  StoreOpen(tempFile, kEditPaths);
			  if (debug == true) System.out.println("**RW**Before GC ::" +getJavaUsedMemory());
	      	 //  OPEN.clear();
			   OPEN = null;
				start =System.currentTimeMillis();
			   System.gc();
				end =System.currentTimeMillis();
				Garbage.GetPrintStream().println((end-start));
			   if (debug == true) System.out.println("**RW***After GC ::" +getJavaUsedMemory());
			   OPEN=  new ArrayList<EditPath>();
		     //  OPEN.add(pmin);
		  }
		  
		//Delete the fEditPaths file
		  
		  if (!fEditPaths.delete()) {
		    if(debug==true)System.out.println("Could not delete file");
		    return ;
		  }

		  //Rename the new file "tmpfile -> fEditPaths"

		  if(debug==true) System.out.println("FILE::::"+fEditPaths.getAbsolutePath());
		  
		  if (!tempFile.renameTo(fEditPaths))
		    System.out.println("Could not rename file");
		  
		}
		catch (FileNotFoundException ex) {
		  ex.printStackTrace();
		}
		catch (IOException ex) {
		  ex.printStackTrace();
		}
	
	}

	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void StoreOpen(File fEditPaths, int kEditPaths) throws IOException {
		// TODO Auto-generated method stub
		// Append the file fEditPaths......

		try
		{
			// if the option kEditPaths is turned on... 
			if(kEditPaths!=-1)
			{
			/*	if(debug==true)
		     	{
					System.out.println("Before ****************"+OPEN.size());
					for(int k = 0 ; k <OPEN.size(); k++)
					{
						System.out.println("totalCost .. "+OPEN.get(k).getTotalCosts());
						System.out.println("Heuristic cost .. "+OPEN.get(k).getHeuristicCosts());
					}
				}
		*/		
				/*
				 *  sort the OPEN list depending on g(p)+h(p) (the function compareTo() in the classe EditPath is 
				 *  the responsible of doing so...
				 */
					
				Collections.sort(OPEN);
				
			/*	if(debug==true)
				{
					System.out.println("After ****************"+OPEN.size());
					for(int k = 0 ; k <OPEN.size(); k++)
					{
						System.out.println("totalCost .. "+OPEN.get(k).getTotalCosts());
						System.out.println("Heuristic cost .. "+OPEN.get(k).getHeuristicCosts());
					}
				}
			*/	
				/*
				 *  keep the first kEditPaths in the OPEN set and delete the other editPaths
				 */
				removeSomeEditPaths(kEditPaths);
				
		/*		if(debug==true)
				{
					System.out.println("Remove****************"+OPEN.size());
					for(int k = 0 ; k <OPEN.size(); k++)
					{
						System.out.println("totalCost .. "+OPEN.get(k).getTotalCosts());
						System.out.println("Heuristic cost .. "+OPEN.get(k).getHeuristicCosts());
					}
				}
		*/
			}
			
			int	size = OPEN.size();
			
			// store the elements of the OPEN set in the file "fEditPaths"
			
		    PrintWriter out = new PrintWriter(new FileWriter(fEditPaths, true));
		    if(debug==true) System.out.println("STORE OPEN----"+OPEN.size());
		    for(int i = 0; i< size ; i++)
			{
		    	double cost = OPEN.get(i).getTotalCosts()+OPEN.get(i).getHeuristicCosts();
		    	if(debug==true)System.out.println("cost::"+cost+"::upperBound::"+upperBound);
				if(cost< upperBound)
				{
			//		System.out.println("store ...."+OPEN.get(i).getTotalCosts()+"---"+upperBound);
					out.println(OPEN.get(i).getTotalCosts()+"	"+OPEN.get(i).getHeuristicCosts()+"	"+OPEN.get(i).toString());
				}
				else
				{
					NoOfDiscardedEditPaths++;
				}
			}
		    
		    out.close();
		   	
		} 
		catch (IOException e) 
		{
		    
		}

	}
	
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// A function that only stores the deepestEditPath in the file fEditPaths
	private void StoreDeepestEditPath(File fEditPaths, EditPath ed) throws IOException {
		
		try
		{
			CleanOPEN();
			
		    PrintWriter out = new PrintWriter(new FileWriter(fEditPaths, true));
			out.println(ed.getTotalCosts()+"	"+ed.getHeuristicCosts()+"	"+ed.toString());
			
		    out.close();
		   	
		} 
		catch (IOException e) 
		{
		    
		}

	}

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * A function that keeps the noOfSavedEditPaths in the OPEN set and delete the other editPaths from the OPEN set
	 */
	private void removeSomeEditPaths(int noOfSavedEditPaths) {
		// TODO Auto-generated method stub

		int size = OPEN.size(); 
		int delta =size-noOfSavedEditPaths;
		if(delta>0)
		{
			for(int i=0; i<delta ; i++){
				OPEN.remove(size-i-1);
			}
				
			
		}
			
		if(debug==true) System.out.println("After DELETING pairs" + OPEN.size());
	}


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * Testing whether or not the function MC-GED has finished it's execution
	 */
	private int MFGED_Finished(File fEditPaths) throws IOException
	{
		int isFinished= -1;
	    BufferedReader reader = new BufferedReader(new FileReader(fEditPaths));
	    
	    // if the file fEditPath.txt is empty and OPEN.size()==0
		if(reader.readLine()==null && OPEN.size()==0) 
		{
			isFinished=1; // finished
		}
		
		reader.close();
		
		return isFinished;
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private EditPath findmincostEditPathWithHeuristic(int noOfIterations, int N) {
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
		
		EditPath pLocalMinPath = OPEN.get(indexmin);
		//System.out.println("before :: pLocalMinPath :::"+pLocalMinPath.getTotalCosts());
	
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
	  			dummyLocalDeepestPath = dummyUpperBound(dummyLocalDeepestPath);
	  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
	    	    //if(debug==true)
	         	if(dummyLocalDeepestPath.getTotalCosts() < upperBound)
	         	{
	         		// delete the previous pmin from the OPEN set.
	         	//	OPEN.remove(pmin);
	         		
	         		// change the current pmin 
	         		pmin = new EditPath(dummyLocalDeepestPath);
		      		upperBound = pmin.getTotalCosts();
		      		if (debug==true ) System.out.println("PatrialMatcher ::::: UPDATING upper bound to be "+upperBound);
		      		
		      		// add it to the OPEN set ..
		      //		OPEN.add(pmin);
	         	}
	      		
	  		}
			
			///////////////////////////////////////////////////////////////////////////////////////////////:
	  		//System.out.println("after :: pLocalMinPath :::"+pLocalMinPath.getTotalCosts());
		}
	
		return pLocalMinPath;
	}	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*	private EditPath findmincostEditPathWithHeuristic() {
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
	
		}
	
		return OPEN.get(indexmin);
	}
*/	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param N
	 */
	private void partialMatcher(int N) {
		
		int noOfIterations = 0;
		int iter =  0;
		while(true == true){
			
			iter++;
		  //  if(debug==true)	System.out.println("No of iterations -----------------------------"+noOfIterations);
			
			// each N iterations, search for a better upperBound and save it 
	/*		if(noOfIterations > N)
			{
				updatePmin();
	      		noOfIterations=-1;
			}
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
			
			EditPath pLocalMin =findmincostEditPathWithHeuristic(noOfIterations,N);
			noOfIterations++;
			if(noOfIterations > N)
			{
	      		noOfIterations=0;
			}
		
			if(debug==true)
			{
				System.out.println("plocalmin (totalCost)::: "+pLocalMin.getTotalCosts());
				System.out.println("plocalmin (heuristicCost) ::: "+pLocalMin.getHeuristicCosts());
				
			}
			
			OPEN.remove(pLocalMin);

			if(pLocalMin.isComplete() == true){
				if(debug==true)System.out.println("OPEN ==="+OPEN.size());
				this.OPEN.clear(); // clear the OPEN list
				pmin = new EditPath(pLocalMin);
				upperBound = pmin.getTotalCosts();
				if(debug==true)System.out.println("COMPLETED............");
				return; // return to the loop () function
			}
			else
			{
			
				if(pLocalMin.getUnUsedNodes1().size() > 0)
				{
					this.CurNode=pLocalMin.getNext();
				//	if(debug==true) System.out.println("Current Node="+this.CurNode.getId());
				
					LinkedList<Node>UnUsedNodes2= pLocalMin.getUnUsedNodes2();
					for(int i=0;i<UnUsedNodes2.size();i++)
					{
						 
						// Substitution case .......
						EditPath newpath = new EditPath(pLocalMin);
						Node w = UnUsedNodes2.get(i);
						addSubstition(newpath,w);
						if(memFilled ==1)
						{
							return;
						}
				    }
							
					// Deletion case ...
					EditPath newpath = new EditPath(pLocalMin);
					addDeletion(newpath);
					if(memFilled ==1)
					{
						return;
					}
				}
				else{
					
					//Insertion case ...
					EditPath newpath = new EditPath(pLocalMin);
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void updatePmin() {
		// TODO Auto-generated method stub
		EditPath pLocalMin = findDeepestEditPath();
		
	
		
  		if(debug==true)
  		{
  			System.out.println("PatrialMatcher ::: COST ::::"+pLocalMin.getTotalCosts());
      		
  		}
  		
  		if(pLocalMin != null)
  		{
  			EditPath tempLocalMin = new EditPath(pLocalMin);
  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: extracting"+pLocalMin.getTotalCosts()+"("+pLocalMin.getNoOfDistortedNodes()+")");
  			tempLocalMin = dummyUpperBound(tempLocalMin);
  		//	if (debug==true ) System.out.println("PatrialMatcher ::::: Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
    	    //if(debug==true)
         	if(tempLocalMin.getTotalCosts() < upperBound)
         	{
         		// delete the previous pmin from the OPEN set.
         	//	OPEN.remove(pmin);
         		
         		// change the current pmin 
         		pmin = new EditPath(tempLocalMin);
	      		upperBound = tempLocalMin.getTotalCosts();
	      		if (debug==true ) System.out.println("PatrialMatcher ::::: UPDATING upper bound to be "+upperBound);
	      		
	      		// add it to the OPEN set ..
	      	//	OPEN.add(pmin);
         	}
      		
  		}
	}

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void addSubstition(EditPath newpath, Node w) {
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
	
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


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
			pmin = new EditPath(newpath);
			upperBound = newpath.getTotalCosts();
		}
				
		if(debug==true) 
		{
			System.out.println("We complete the path by inserting all remaining nodes of G2 CurNode=");
			newpath.printMe();
		}
	}

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int AddEditPath(EditPath newpath) {
		// TODO Auto-generated method stub
		int succeed = 0;
		
		javaUsedMemory =  getJavaUsedMemory();
				
		if (debug == true )System.out.println("**Java-UsedMemory :::"+javaUsedMemory +"...javaMaxMemory :::" + javaMaxMemory +"------OPRN----"+OPEN.size());
		if( (javaUsedMemory + satMemory) <  javaMaxMemory)
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private double getJavaUsedMemory() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		javaTotalMemory = Runtime.getRuntime().totalMemory()/MegaBytes;
		javaFreeMemory = Runtime.getRuntime().freeMemory() / MegaBytes;
		double mem =  (javaTotalMemory - javaFreeMemory)+satMemory;
		return mem;
	}


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sg1;
		String sg2;
		SpeedEvalTest SET = new SpeedEvalTest("MF-GED.txt");
		//sg1 = "./data/5-1.gxl";
	    //sg2 = "./data/5-2.gxl";
	   sg1 = "./data/IAM/GREC/image22_46.gxl";
	   sg2 = "./data/IAM/GREC/image6_36.gxl";
	   //sg1 = "./data/Letter/MED/AP1_0021.gxl";
	  // sg1 = "./data/Letter/MED/AP1_0044.gxl";
	 //  sg2 = "./data/Letter/MED/AP1_0052.gxl";	 
	    

	  // sg2 = "./data/Letter/MED/AP1_0107.gxl";	
	 //  sg2 = "./data/Letter/MED/EP1_0010.gxl";
	  
	    double satMemory = 20.0;
		double nodeCosts=90;
		double edgeCosts=15;
		double alpha=0.5;
		
		
		//nodeCosts=1;
		//edgeCosts=1;
		//alpha=1;
	    //Constants.costFunction= new LetterCostFunction(nodeCosts,edgeCosts,alpha);
		Constants.edgeHandler = new UniversalEdgeHandler();
		
	    Constants.costFunction = new GRECCostFunction(nodeCosts, edgeCosts, alpha);

	    int kEditPaths= 5;
	    int nIterations = 50;
	    
		boolean debug = false;
		
		
		//////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		XMLParser xmlParser = new XMLParser();
		Graph g1 = null;
		Graph g2 = null;
		try {
			g1 = xmlParser.parseGXL(sg1);
			System.out.println("NO of nodes of G1 ::::"+g1.size());
			
			g2= xmlParser.parseGXL(sg2);
			System.out.println("NO of nodes of G2 ::::: "+g2.size());
			SET.StartChrono();
			
			MemoryControlledGED mf = new MemoryControlledGED(g1, g2, satMemory, kEditPaths ,nIterations,Constants.costFunction, Constants.edgeHandler, debug);
		
			SET.StopChrono();
			SET.WriteElapsedTime();
			System.out.println("****************The best solution is=");
			mf.pmin.printMe();
			System.out.println("OPEN :::"+mf.OPEN.size());
			System.out.println("COST :::: "+mf.pmin.getTotalCosts());
			System.out.println("Discarded Edit Paths::::"+mf.NoOfDiscardedEditPaths);
			System.out.println("No of calls Pmin ::: "+mf.noOfCallsMinCost);
			//System.out.println("BEFORE" + mf.getJavaUsedMemory());
			

			////////////////////////////////////////////////////////////////////////
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SET.CloseStream();
		
	}

}
