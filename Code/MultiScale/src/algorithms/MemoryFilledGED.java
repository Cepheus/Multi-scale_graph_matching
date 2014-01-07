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
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.naming.Context;

import util.EditPath;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.Node;
import util.UniversalEdgeHandler;
import xml.XMLParser;

public class MemoryFilledGED {

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
    public boolean foundLocalEditPath;

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
    
	 public MemoryFilledGED(Graph G1, Graph G2, double satMemory,int dataSetNumber, int methodNumber, String testGraphClass2, String trainingGraphClass2 ,ICostFunction costfunction, IEdgeHandler edgehandler,boolean debug) throws IOException, InterruptedException{
		
		//System.out.println("************************************Partial Search Tree************************************");
		//this.jobNo = jobNo;
		 
		this.satMemory = satMemory;
		this.dataSetNumber = dataSetNumber;
		this.methodNumber = methodNumber;
		this.testGraphClass = testGraphClass2;
		this.trainingGraphClass = trainingGraphClass2;
		
		this.javaMaxMemory = Runtime.getRuntime().maxMemory() / MegaBytes;
		
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		G1NbNodes = G1.size();
		OPEN = new ArrayList<EditPath>(); // empty OPEN list
		foundLocalEditPath = false;
		pmin = null; // empty pmin 
		SPEEDMatcher = new SpeedEvalTest("./speedMatcher.csv");
		SPEEDReadWrite = new SpeedEvalTest("./speedrw.csv");
		Parsing = new SpeedEvalTest("./speedParsing.csv");
		Garbage = new SpeedEvalTest("./speedGC.csv");

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
		
		init(); // 1st step
		
		EditPath p = new EditPath(G1,G2); //EditPath between G1 and G2
		pmin = dummyUpperBound(p); // 2nd step
		upperBound = pmin.getTotalCosts();
	
	}




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

			//foundLocalEditPath = false;
		
	
			if(debug == true) System.out.println("BEFORE partialMatcher:::upper bound :::"+upperBound);
			memFilled = 0;
			start =System.currentTimeMillis();
			partialMatcher(); // STEP ONE ......................
			end =System.currentTimeMillis();
			if(debug == true) System.out.println("AFTER partialMatcher:::upper bound :::"+upperBound);
			SPEEDMatcher.GetPrintStream().println((end-start));
			
			int isFinished  = MFGED_Finished(fEditPaths);	
			if ( isFinished ==1)
			{
				if (debug == true) System.out.println("COMPLETED...............");
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
		      		//EditPath pLocalMin =findmincostEditPath();
		      		EditPath pLocalMin = findDeepestEditPath();
		      		if(debug==true)
		      		{
		      			System.out.println("finding the dummy upper bound of ::: "+pLocalMin.toString());
		      			System.out.println("COST ::::"+pLocalMin.getTotalCosts());
			      		
		      		}
		      		
		      		if(pLocalMin != null)
		      		{
		      			pLocalMin = dummyUpperBound(pLocalMin);
			         	System.out.println("OUTER ::::::Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
			    	    //if(debug==true)
			         	if(pLocalMin.getTotalCosts() < upperBound)
			         	{
			         		pmin = pLocalMin;
				      		upperBound = pLocalMin.getTotalCosts();
				      		foundLocalEditPath = true;
				      		System.out.println("OUTER ::::::UPDATING upper bound to be "+upperBound);
			         	}
			      		
		      		}
		      			
		      		
		      		
		      		if(debug==true)System.out.println("OUTER ::::::store outside....."+upperBound);
					StoreOpen(fEditPaths);
					
					if (debug == true) System.out.println("OUTER ::::::Before GC ::" +getJavaUsedMemory());
				//	OPEN.clear();
					OPEN = null;
					start =System.currentTimeMillis();
					System.gc();
					end =System.currentTimeMillis();
					Garbage.GetPrintStream().println((end-start));
					if (debug == true) System.out.println("OUTER ::::::After GC ::" +getJavaUsedMemory());
					OPEN=  new ArrayList<EditPath>(); 
					
				}
		      	
		  
	      		while( MFGED_Finished(fEditPaths)!=1)
				{
	      			if(debug==true) System.out.println("MFGED not finished ... continue :-)");
					 start =System.currentTimeMillis();	
				     readWrite(fEditPaths); // STEP TWO ......................
				     end =System.currentTimeMillis();
					 SPEEDReadWrite.GetPrintStream().println((end-start));	
				}
		      
				
				 
				//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
				
			}
			
		} // end while
		
	}
	
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
			return OPEN.get(indexmax);
		}
		else 
		{
			return null;
		}
	}
	
	public void readWrite(File fEditPaths) {

		EditPath p= null;
		//int t=0;
		try {

		  //Construct the new file that will later be renamed to the original filename.
		  File tempFile = new File(fEditPaths.getAbsolutePath() + ".tmp");
		  tempFile.createNewFile();
		  
		  if(debug == true)System.out.println(" temp file :::" +fEditPaths.getAbsolutePath() + ".tmp");

		  BufferedReader br = new BufferedReader(new FileReader(fEditPaths));
		  PrintWriter pw = new PrintWriter(new FileWriter(tempFile),true);

		  String line = null;
		  double start;
		  double end;
		  //Read from the original file and write to the new
		  //unless content matches data to be removed.
		  
		  int i =0;
		  while ((line = br.readLine()) != null) 
		  {
			 
			i++;		  
			StringTokenizer itr = new StringTokenizer(line);
			double editPathCost =Double.parseDouble(itr.nextToken());
	
		    if (editPathCost < upperBound) 
		    {
		    	start =System.currentTimeMillis();
		    	EditPath pi = new EditPath(itr.nextToken());
		    	end =System.currentTimeMillis();
				Parsing.GetPrintStream().println((end-start));
				
		    	OPEN.add(pi);

		    	if(debug == true){
		    		System.out.println("Before PartialMatcher:::"+OPEN.size());
		    		System.out.println("BEFORE partialMatcher:::upper bound :::"+upperBound);
		    	}
		    	memFilled = 0;
		    	start =System.currentTimeMillis();
		    	partialMatcher();
		    	end =System.currentTimeMillis();
		    	SPEEDMatcher.GetPrintStream().println((end-start));
		    	if(debug==true) System.out.println("AFTER partialMatcher:::upper bound :::"+upperBound);
		    	javaUsedMemory =  getJavaUsedMemory();
		     	if(OPEN.size()==0)
		    	{
		    	//	OPEN.clear();
					OPEN = null;
					start = System.currentTimeMillis();
					System.gc();
					end =System.currentTimeMillis();
					Garbage.GetPrintStream().println((end-start));
					OPEN=  new ArrayList<EditPath>();
		    	}
		    	
		    	if (debug == true){
		    		System.out.println("******RW*****Java-UsedMemory :::"+javaUsedMemory +"...javaMaxMemory :::" + javaMaxMemory );
		    		System.out.println("After PartialMatcher:::"+OPEN.size());
			    	System.out.println("A local solution is found ?"+foundLocalEditPath);
		    	}
		    	
		   
		    	//if memory saturation happens
		      	if( memFilled ==1)
				{
		      		
		      		//File ftemp = new File("./data/temp/"+num+"/"+num+".txt");
		      		///////////////////////////////////////////////////////////
		      		// SOME MODIFICATIONS  WILL BE ADDED HERE ...............
		      		//////////////////////////////////////////////////////////
		      		//EditPath pLocalMin =findmincostEditPath();
		      		EditPath pLocalMin = findDeepestEditPath();
		      		
		      		if(pLocalMin != null)
		      		{
		      			pLocalMin = dummyUpperBound(pLocalMin);
				      	//	System.out.println("Comparing between "+upperBound+ " and "+pLocalMin.getTotalCosts());
		      			if(pLocalMin.getTotalCosts() < upperBound)
			         	{
			         		pmin = pLocalMin;
				      		upperBound = pLocalMin.getTotalCosts();		
				      		foundLocalEditPath = true;
				      		System.out.println("OUTER ::::::UPDATING upper bound to be "+upperBound);
			         	}
		      		}
		      		
		      		
		      		////////////////////////////////////////////////////////////
		      		
		      		
		      		StoreOpen(tempFile);
		      		//StoreOpen(ftemp);
		      		if (debug == true) System.out.println("**RW**Before GC ::" +getJavaUsedMemory());
		      	//	OPEN.clear();
					OPEN = null;
					start =System.currentTimeMillis();	
					System.gc();
					end =System.currentTimeMillis();
					Garbage.GetPrintStream().println((end-start));
					
					if (debug == true) System.out.println("**RW***After GC ::" +getJavaUsedMemory());
					OPEN=  new ArrayList<EditPath>(); 
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
		  
		  if(OPEN.size()>0)
		  {
			  if(debug==true){
				  System.out.println("Memory is still not saturated");
				  System.out.println("Saving all the editPaths of OPen in tempFile");
			  }
			  StoreOpen(tempFile);
			  if (debug == true) System.out.println("**RW**Before GC ::" +getJavaUsedMemory());
	      	 //  OPEN.clear();
			   OPEN = null;
				start =System.currentTimeMillis();
			   System.gc();
				end =System.currentTimeMillis();
				Garbage.GetPrintStream().println((end-start));
			   if (debug == true) System.out.println("**RW***After GC ::" +getJavaUsedMemory());
			   OPEN=  new ArrayList<EditPath>();
			  
		  }
		  
		//Delete the original file
		  
		  if (!fEditPaths.delete()) {
		    if(debug==true)System.out.println("Could not delete file");
		    return ;
		  }

		  //Rename the new file to the filename the original file had.

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

	
	
	

	private void StoreOpen(File fEditPaths) throws IOException {
		// TODO Auto-generated method stub
		// Append the file fEditPaths......
		
		//BufferedReader reader = new BufferedReader(new FileReader(fEditPaths));
		/* BufferedWriter writer = new BufferedWriter(new FileWriter(fEditPaths));
		writer.write("helloooooooo");
		writer.close();
		*/
		
		try
		{
		    PrintWriter out = new PrintWriter(new FileWriter(fEditPaths, true));
			for(int i = 0; i< OPEN.size() ; i++)
			{
				if(OPEN.get(i).getTotalCosts() < upperBound)
				{
				
				out.println(OPEN.get(i).getTotalCosts()+"	"+OPEN.get(i).toString());
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




	private int MFGED_Finished(File fEditPaths) throws IOException
	{
		int isFinished= -1;
		if(foundLocalEditPath ==true)
		{
			BufferedReader reader = new BufferedReader(new FileReader(fEditPaths));
			if(reader.readLine()==null)
			{
				isFinished=1; // finished
			}
			else
			{
				isFinished = 0;  // a local map is found but the global solution is not found yet
			}
			
			reader.close();
		}
		else
		{
			isFinished = -1;  // Memory saturation ......... 
		}
		
		return isFinished;
		
	}
	
	private EditPath findmincostEditPathWithHeuristic() {
		// TODO Auto-generated method stub
		int i=0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		double h=0.0,g=0.0;
		int indexmin = -1;

		for(i=0;i<nbpaths;i++){
	
			EditPath p = OPEN.get(i);
			g = p.getTotalCosts(); // computing the cost of the edit path from the root till the current node...
			h=p.ComputeHeuristicCosts(); // estimating the cost of the edit path from the current node till the leaf nodes
			
			if((g+h) <minvalue){
				minvalue = g+h;
				indexmin = i;
			}
		}

		return OPEN.get(indexmin);
	}
	

	private void partialMatcher() {
		

		while(true == true){
			
			if(OPEN.isEmpty() == true)
			{
		//		System.out.println("No more candidates, no complete solution could be found");
		//		System.out.println("Please check your graph data");
				return;
			}
			
			EditPath pLocalMin =findmincostEditPathWithHeuristic();
		    
	
		//	if(debug==true)System.out.println("removing the edit path that has a cost ="+pLocalMin.getTotalCosts()+ " and nodes = "+pLocalMin.getNoOfDistortedNodes());

			OPEN.remove(pLocalMin);

			if(pLocalMin.isComplete() == true){
				this.OPEN.clear(); // clear the OPEN list
				//OPEN = null;
				foundLocalEditPath = true; // a local edit path has been found
				pmin = pLocalMin;
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
						newpath.addDistortion(this.CurNode, w);
						newpath.setID(this.CurNode.getId()+"_"+w.getId());
								
						
						int succeed = AddEditPath(newpath);
						if(succeed==-1) 
						{
							memFilled= 1;
							System.out.println("MEMORY SATURATED ..........");
							return;
						}
						else if(succeed==1)
						{
							this.OPEN.add(newpath);
						}			
				    }
							
					EditPath newpath = new EditPath(pLocalMin);
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
				else{
					//Sinon si k= taille de G1
					//On met dans pmin toutes les insertions des noeuds  de V2  qui ne sont pas encore utilis�s dans Pmin. 
					//On remet Pmin dans OPEN
					EditPath newpath = new EditPath(pLocalMin);
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
						pmin = newpath;
						upperBound = newpath.getTotalCosts();
					}
					 // ep will be the only edit path in the OPEN list
							
					if(debug==true) 
					{
						System.out.println("We complete the path by inserting all remaining nodes of G2 CurNode=");
						newpath.printMe();
					}
					
	
				}
											
			}


		
		}//boucle while

		
	}




	private int AddEditPath(EditPath newpath) {
		// TODO Auto-generated method stub
		int succeed = 0;
		
		javaUsedMemory =  getJavaUsedMemory();
				
		//if (debug == true )System.out.println("**Java-UsedMemory :::"+javaUsedMemory +"...javaMaxMemory :::" + javaMaxMemory +"------OPRN----"+OPEN.size());
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




	private double getJavaUsedMemory() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		javaTotalMemory = Runtime.getRuntime().totalMemory()/MegaBytes;
		javaFreeMemory = Runtime.getRuntime().freeMemory() / MegaBytes;
		double mem =  (javaTotalMemory - javaFreeMemory)+satMemory;
		return mem;
	}




	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String sg1;
		String sg2;
		SpeedEvalTest SET = new SpeedEvalTest("MF-GED.txt");
		sg1 = "./data/10-1.gxl";
	    sg2 = "./data/10-2.gxl";
	    

	    double satMemory = 20.0;
		double nodeCosts=1;
		double edgeCosts=1;
		double alpha=1;
	    Constants.costFunction= new LetterCostFunction(nodeCosts,edgeCosts,alpha);
		Constants.edgeHandler = new UniversalEdgeHandler();
		
		int dataSetNumber= 11;
	    int methodNumber = 5;
	    String testGraphClass= "A";
	    String trainingGraphClass = "A";
	    
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
			
			MemoryFilledGED mf = new MemoryFilledGED(g1, g2, satMemory, dataSetNumber, methodNumber, testGraphClass, trainingGraphClass,     Constants.costFunction,     Constants.edgeHandler, debug);
		
			SET.StopChrono();
			SET.WriteElapsedTime();
			System.out.println("****************The best solution is=");
			mf.pmin.printMe();
			System.out.println("COST :::: "+mf.pmin.getTotalCosts());
			System.out.println("Discarded Edit Paths::::"+mf.NoOfDiscardedEditPaths);
			//System.out.println("BEFORE" + mf.getJavaUsedMemory());
			

			////////////////////////////////////////////////////////////////////////
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SET.CloseStream();
		
	}

}
