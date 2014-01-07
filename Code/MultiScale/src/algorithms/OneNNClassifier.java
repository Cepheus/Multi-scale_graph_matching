package algorithms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import util.Graph;
import util.GraphCollection;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.PairGraphDistance;
import util.UniversalCostFunction;
import util.UniversalEdgeHandler;
import util.UnlabeledCostFunction;
import xml.XMLParser;



public class OneNNClassifier {

	/**
	 * @param args
	 */
	
	 /*
	  * source is the test set
	  * target is the raining set
	  * e.g.
	  * 	source=data/Letter/MED/test.cxl
	  * 	target=data/Letter/MED/train.cxl
	  */
	 GraphCollection source; // source is the test set
	 private GraphCollection target; // target is the raining set
	 private Graph sourceGraph, targetGraph;
	 public String propFileName;
	 public boolean debug;
	 private int PLAIN_GED=0;
	 private int ASTAR_GED=1;
	 private int MUNKRES_GED=2;
	 private int BEAM_GED=3;
	 private int method; // to choose between PLAIN_GED, ASTAR_GED, MNKRES_GED and BEAM_GED methods
	 public String methodName; // to save the name of the method (algorithm) so as to be used in the string file
	// An Array list of GraphEditPairs where each pair equals (graph, cost)
	 public ArrayList<PairGraphDistance> pairGraphDistances; 
	 
	 double alpha;
	 double edgeCosts;
	 double nodeCosts;
	 int counter;
	 double totalTime;
	 // variables that help to detect the time taken to classify a graph
	public double start; // starting time
	public double end; // ending time
	public int openSize;  // for the beam search
	
	
	//openSize is only used when choosing the Beam Search algorithm
	public OneNNClassifier(String propFileName, boolean debug,int method, String saveFile, int openSize, ICostFunction IC ,IEdgeHandler IH, double alpha, double edgeCosts, double nodeCosts ) throws Exception
	{
		/////////////////////////////////////////////////////////////////////////////
		this.totalTime=0.0;
		this.openSize=openSize;
		this.method=method;
		this.alpha=alpha;
		this.edgeCosts=edgeCosts;
		this.nodeCosts=nodeCosts;
		Constants.costFunction = IC;
		Constants.edgeHandler = IH; 
		
		this.debug=debug;
		this.propFileName = propFileName;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(this.propFileName));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("here");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("here2");
			e.printStackTrace();
		}
		
		XMLParser xmlParser = new XMLParser();
		// Getting the path of the Dataset
		xmlParser.setGraphPath(properties.getProperty("path"));
		// parsing the test set
		this.source = xmlParser.parseCXL(properties.getProperty("source"));
		// parsing the training set
		this.target = xmlParser.parseCXL(properties.getProperty("target"));
		
		int r = source.size();
		int c = target.size();
	 
		if (debug==true)
		{
			System.out.println("Source size ====== "+r);
			System.out.println("Target size ====== "+c);
		}
		
		MatrixGenerator mgen = new MatrixGenerator();
		Munkres munkres = new Munkres();
		MunkresRec munkresRec = new MunkresRec();
		mgen.setMunkres(munkresRec);
		double[][] matrix;
		double distance = 0.0;
		
		if(this.method== PLAIN_GED||this.method==ASTAR_GED||this.method==  MUNKRES_GED||this.method==  BEAM_GED)
		{
			if(this.method == PLAIN_GED){
				methodName = "GED";
				System.out.println("GED ..........");
			}
			else if(this.method == ASTAR_GED){
				methodName = "AStar";
				System.out.println("A-Star ..........");
			}
			else if(this.method == MUNKRES_GED){
				methodName = "Munkres";
				System.out.println("Munkres..........");
			}
	        else if(this.method == BEAM_GED){
	        	methodName = "BeamSearch";
				System.out.println("GraphEditDistanceBeamSearch ..........");
			}
	
			start =System.currentTimeMillis();
			for (int i = 0; i < r; i++) {
				
				if(debug==true) System.out.println("SourceGraph # "+ i);
				sourceGraph = (Graph) source.get(i);

				this.pairGraphDistances = new ArrayList<PairGraphDistance>();
				 // starting the timer
				 if(debug==true)
				{
					System.out.println("Source graph "+sourceGraph.getId()+" is in class ( "+sourceGraph.getClassId()+")" );
					System.out.println("------------------------------------------------------------------------");
				}
				 System.out.println(i + "Graph "+sourceGraph.getId());
				for (int j = 0; j < c; j++) {
					targetGraph = (Graph) target.get(j);	
				
					
					if(debug==true)
					{
						System.out.println("Target graph "+targetGraph.getId()+" is in class ("+targetGraph.getClassId()+")" );
					}
					
					
					///////////////////////////////////////////////////////////////////////
					
					//Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(nodeCosts, edgeCosts, alpha);
					//Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5);
					Constants.costFunction = IC;
					Constants.edgeHandler = IH; 

					
					if(this.method == PLAIN_GED){
						 GraphEditDistance GED = new GraphEditDistance(sourceGraph,targetGraph,Constants.costFunction,Constants.edgeHandler,false);
						 // Adding the pair (targetGraph, distance between the sourceGraph and the targetGraph) to the list of GraphEditPairs
						 this.pairGraphDistances.add(new PairGraphDistance(targetGraph,GED.getBestEditpath().getTotalCosts()));
						 if(debug==true) printPairs();
					}
					else if(this.method == ASTAR_GED)
					{
						 HeuristicGraphEditDistance GED = new HeuristicGraphEditDistance(sourceGraph,targetGraph,Constants.costFunction,Constants.edgeHandler ,false);
						 // Adding the pair (targetGraph, distance between the sourceGraph and the targetGraph) to the list of GraphEditPairs
						 this.pairGraphDistances.add(new PairGraphDistance(targetGraph,GED.getBestEditpath().getTotalCosts())); 
						 if(debug==true) printPairs();
					}
					else if(this.method == MUNKRES_GED)
					{
						matrix = mgen.getMatrix(sourceGraph, targetGraph);
						munkres.setGraphs(sourceGraph, targetGraph);
						distance = munkres.getCosts(matrix);
						// Adding the pair (targetGraph, distance between the sourceGraph and the targetGraph) to the list of GraphEditPairs
						this.pairGraphDistances.add(new PairGraphDistance(targetGraph,distance)); 
						if(debug==true) printPairs();
					}
					else if(this.method == BEAM_GED)
					{
						BeamSearchGraphEditDistance GED = new BeamSearchGraphEditDistance(sourceGraph,targetGraph,this.openSize, Constants.costFunction, Constants.edgeHandler ,false);
						// Adding the pair (targetGraph, distance between the sourceGraph and the targetGraph) to the list of GraphEditPairs
						this.pairGraphDistances.add(new PairGraphDistance(targetGraph,GED.getBestEditpath().getTotalCosts())); 
						if(debug==true) printPairs();
					}
			
						
				}
				// Comparing the class of the test and the nearest neighbor's class
				CompareClasses(sourceGraph,pairGraphDistances);
				if(debug==true)	System.out.println("*************************************************************************");
				
			}

			 // stopping the timer
			end =System.currentTimeMillis();
			//Saving the total time"end-start" in the classifyTime.csv files
			totalTime = end-start;
			System.out.println("end"+end+"--------start"+start+"--- total"+totalTime);

		}
		else
		{
			System.out.println("Unvalid Choice ....");
			return;
		}

			///////////////////////////////////////////////////////////////////////////////
		
	}
	

	private void printPairs() {
		// TODO Auto-generated method stub
		for (int i=0; i<pairGraphDistances.size(); i++) {
	    	  PairGraphDistance pr = pairGraphDistances.get(i);
		      Graph g = pr.getGraph();
		      double dist = pr.getdist();
		      System.out.println(g.getId() + ", " + dist);
	    }
	}

	//  Comparing the class of the test and the nearest neighbor's class
     private void CompareClasses(Graph src,
			ArrayList<PairGraphDistance> pairGraphDistances) 
	{
		// TODO Auto-generated method stub

		Collections.sort(pairGraphDistances);
		Graph tgt = pairGraphDistances.get(0).getGraph();
		if(tgt.getClassId().equals(src.getClassId()))
		{
			if (debug==true) System.out.println(""+src.getId()+"and "+tgt.getId()+" have the same class which is : "+tgt.getClassId());
			this.counter++;
		}
		else
		{
			if (debug==true) System.out.println(""+src.getId()+"and "+tgt.getId()+" doesn't have the same class");
				
		}
		
	} 

     // A funtion that computes the accuracy of classifying graphs 
 	double getAccuracy(String saveFile) throws FileNotFoundException {
 		// TODO Auto-generated method stub
 		PrintStream ps =  new PrintStream(saveFile+methodName+"Accuracy.csv") ;
		double precision = (double)((double)counter/(double) source.size());
		//ps.println("precision");
		ps.println(precision);
		ps.close();
        return precision;
 		
 	}

 	// A funtion that returns the number of the well-classified graphs 
 	int getCorrectlyClassifiedInstances(String saveFile) throws FileNotFoundException {
 		// TODO Auto-generated method stub
 		PrintStream ps =  new PrintStream(saveFile+methodName+"CorrectlyClassifiedInstances.csv");
 		//ps.println("CorrectlyClassifiedInstances");
		ps.println(counter);
 		ps.close();
 		return counter;
 		
 	}

 	double getTotalClassificationTime(String saveFile) throws FileNotFoundException {
 		// TODO Auto-generated method stub
 		PrintStream ps =  new PrintStream(saveFile+methodName+"totalClassificationTime.csv");
 		//ps.println("TotalClassificationTime");
		ps.println(totalTime);
 		ps.close();
 		return totalTime;
 		
 	}
 	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String propFile = "bin/properties/letter.prop";
		String saveFile = "./data/LETTER/";
		
		double alpha=0.5;
		double edgeCosts=10;
		double nodeCosts=10;
		
		ICostFunction IC = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
		IEdgeHandler IH = new UniversalEdgeHandler(); 
		
		
		OneNNClassifier cs =  new OneNNClassifier(propFile,false,2,saveFile,10,IC,IH,alpha,edgeCosts,nodeCosts);
		System.out.println("*******************************************************");
		
		// Calculating the precision of classifying the graphs
		int counter = cs.getCorrectlyClassifiedInstances(saveFile);
		double totalClassificationTime = cs.getTotalClassificationTime(saveFile);
		double precision = cs.getAccuracy(saveFile);
		
		System.out.println("source size : "+cs.source.size());
		System.out.println("**Number of correctly classified instances: "+counter);
		System.out.println("**Total Classification Time: "+totalClassificationTime);
		System.out.println("**the precision of classifying instances : "+precision);
		
		System.out.println("*******************************************************");
		
	
				

	}



}
