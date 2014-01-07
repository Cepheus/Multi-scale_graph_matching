package algorithms;

import java.io.IOException;

import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.UniversalEdgeHandler;
import util.UnlabeledCostFunction;


public class TestGraphEditDistanceAlgorithms {
	
	boolean debug;
	TestGraphEditDistanceAlgorithms()
	{
		
	}
	
	
	/* In this function, linear graphs are used to measure the Set Open's size,
	number of edit paths, the best solution "i.e. best edit path" and the symmetry test */
	
	void testBestEditSolution(boolean debug) throws Exception{
		double n=2;
		double e=2;
		double a=0.5;
		int noOfNodesG12=10;
		int openSize=1;
		
        Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(n, e, a); 
		Constants.edgeHandler = new UniversalEdgeHandler(); 
		
		SpeedEvalTest SET,COST,SIZE,Symm;
		
		//---------------------------------------------------------------------------
		// 1st algorithm : GED Algorithm
	    SET = new SpeedEvalTest("GraphEditDistance-TIME.csv");
		COST = new SpeedEvalTest("GraphEditDistance-COST.csv");
		SIZE = new SpeedEvalTest("GraphEditDistance-SIZE.csv");
		Symm = new SpeedEvalTest("GraphEditDistance-SYMMETRY.csv");
		//Calling the function that displays the results of testing the GED algorithm
		testGED(n,e,a,noOfNodesG12,SET,COST,SIZE,Symm ); 
		
		//---------------------------------------------------------------------------
		//2nd Algorithm : GED A-Star
		
		SET = new SpeedEvalTest("HeuristicGraphEditDistance-TIME.csv");
		COST = new SpeedEvalTest("HeuristicGraphEditDistance-COST.csv");
	    SIZE = new SpeedEvalTest("HeuristicGraphEditDistance-SIZE.csv");
	    Symm = new SpeedEvalTest("HeuristicGraphEditDistance-SYMMETRY.csv");
	  //Calling the function that displays the results of testing the Heuristic GED algorithm
		testHeuristicGED(n,e,a,noOfNodesG12,SET,COST,SIZE,Symm ); 
		
		//---------------------------------------------------------------------------
		//3rd Algorithm : GED Beam Search
		SET = new SpeedEvalTest("BeamSearch-TIME.csv");
		COST = new SpeedEvalTest("BeamSearch-COST.csv");
		SIZE = new SpeedEvalTest("BeamSearch-SIZE.csv");
		Symm = new SpeedEvalTest("BeamSearch-SYMMETRY.csv");
		//Calling the function that displays the results of testing the BeamSearch algorithm
		testBeamSearchGED(n,e,a,noOfNodesG12,SET,COST,SIZE,Symm,openSize);
	
		
		//---------------------------------------------------------------------------
		
		//4th Algorithm: Munkres' algorithm
		
		SET = new SpeedEvalTest("Munkres-TIME.csv");
		COST = new SpeedEvalTest("Munkres-COST.csv");
		Symm = new SpeedEvalTest("Munkres-SYMMETRY.csv");
		//Calling the function that displays the results of testing the Munkres' algorithm
		testMukres(n,e,a,noOfNodesG12,SET,COST,Symm );
		
	}
	
	//*************************************************************************************
	
	
	
	private void testMukres(double n, double e, double a, int noOfNodesG12,
			SpeedEvalTest SET,SpeedEvalTest COST,SpeedEvalTest SYMM) {
		
		
		MatrixGenerator mgen = new MatrixGenerator();
		Munkres munkres = new Munkres();
		MunkresRec munkresRec = new MunkresRec();
		mgen.setMunkres(munkresRec);
		COST.GetPrintStream().println("No of Nodes;Distance");
		SET.GetPrintStream().println("No of Nodes;TIME");
		SYMM.GetPrintStream().println("No of Nodes;Distance(G1-G2);Distance(G2-G1)");
		
		double[][] matrix;
		double[][] matrixSymm; // this matrix is used to test the symmetry of the Munkres' algorithm
		
		double distance = 0.0;
		double distanceSymm = 0.0;
		
		// TODO Auto-generated method stub
		  Graph g1 = SET.SyntheticLinearGraph(noOfNodesG12);
			int noOFNodes;
			for(int i=0;i<noOfNodesG12;i++){
				noOFNodes=i+1;
				System.out.println("--------------"+noOFNodes+"-----------------------");
				Graph g2 = SET.SyntheticLinearGraph(noOFNodes);
				
				double start =System.currentTimeMillis();
				matrix = mgen.getMatrix(g1, g2);
				munkres.setGraphs(g1, g2);
				distance = munkres.getCosts(matrix);
				double end =System.currentTimeMillis();
 
				// Testing the symmetry of the algorithm
				/*matrixSymm = mgen.getMatrix(g2, g1);
				munkres.setGraphs(g2, g1);
				distanceSymm = munkres.getCosts(matrixSymm);
				SYMM.GetPrintStream().print(noOFNodes);
				SYMM.GetPrintStream().print(";"+distance);
				SYMM.GetPrintStream().println(";"+distanceSymm); */
				///////////////////////////////////////////////
				
				if (debug==true)System.out.print("The total cost is : " + distance);
				
				COST.GetPrintStream().print(noOFNodes);
				COST.GetPrintStream().println(";"+distance);
				
				SET.GetPrintStream().print(noOFNodes);
				SET.GetPrintStream().println(";"+(end-start));
				
				
				//-----------------------------------------------------
				
			
			}
			COST.CloseStream();
			SET.CloseStream();
			SYMM.CloseStream();	
			

		
	}

	//*************************************************************************************
	private void testBeamSearchGED(double n, double e, double a,
			int noOfNodesG12, SpeedEvalTest SET, SpeedEvalTest COST,
			SpeedEvalTest SIZE,SpeedEvalTest SYMM, int openSize) {
		// TODO Auto-generated method stub
	    Graph g1 = SET.SyntheticLinearGraph(noOfNodesG12);
		int noOFNodes;
		BeamSearchGraphEditDistance GED;
		BeamSearchGraphEditDistance GEDSymm;
		
		SIZE.GetPrintStream().println("No of Nodes;OPEN size;EditPath Size");
		COST.GetPrintStream().println("No of Nodes;Distance");
		SET.GetPrintStream().println("No of Nodes;TIME");
		SYMM.GetPrintStream().println("No of Nodes;Distance(G1-G2);Distance(G2-G1)");
		
		for(int i=0;i<noOfNodesG12;i++){
			noOFNodes=i+1;
			System.out.println("--------------"+noOFNodes+"-----------------------");
			Graph g2 = SET.SyntheticLinearGraph(noOFNodes);
			
			double start =System.currentTimeMillis();
			GED = new BeamSearchGraphEditDistance(g1,g2,openSize,Constants.costFunction,Constants.edgeHandler,false);
			double end =System.currentTimeMillis();
			
			if (debug==true)
			{
				System.out.println("The best solution is=");
				GED.getBestEditpath().printMe();
	
				System.out.print("The total cost of "+noOfNodesG12+" nodes is :");
				System.out.println(GED.getBestEditpath().getTotalCosts());
			}
			
			// Testing the symmetry of the algorithm
		/*	GEDSymm = new BeamSearchGraphEditDistance(g2,g1,10,Constants.costFunction,Constants.edgeHandler,false);
			SYMM.GetPrintStream().print(noOFNodes);
			SYMM.GetPrintStream().print(";"+GED.getBestEditpath().getTotalCosts());
			SYMM.GetPrintStream().println(";"+GEDSymm.getBestEditpath().getTotalCosts()); */
			///////////////////////////////////////////////
			
			
			COST.GetPrintStream().print(noOFNodes);
			COST.GetPrintStream().println (";"+GED.getBestEditpath().getTotalCosts());
			
			SIZE.GetPrintStream().print(noOFNodes);
			SIZE.GetPrintStream().print(";"+GED.UnProcessedNoEditPaths);
			SIZE.GetPrintStream().println(";"+GED.editPathCounter);
		
			SET.GetPrintStream().print(noOFNodes);
			SET.GetPrintStream().println(";"+(end-start));
			
			if (debug==true)
			{
				System.out.println("OPEN max size = "+GED.UnProcessedNoEditPaths);
				System.out.println("No of edit paths = "+GED.editPathCounter);
			}
			
			//-----------------------------------------------------
			
		
		}
		COST.CloseStream();
		SET.CloseStream();
		SIZE.CloseStream();
		SYMM.CloseStream();	
		
	}

	//*************************************************************************************
	
	private void testHeuristicGED(double n, double e, double a,
			int noOfNodesG12, SpeedEvalTest SET, SpeedEvalTest COST,
			SpeedEvalTest SIZE,SpeedEvalTest SYMM) {
		// TODO Auto-generated method stub
		HeuristicGraphEditDistance GED;
		HeuristicGraphEditDistance GEDSymm;
		
		SIZE.GetPrintStream().println("No of Nodes;OPEN size;EditPath Size");
		COST.GetPrintStream().println("No of Nodes;Distance");
		SET.GetPrintStream().println("No of Nodes;TIME");
		SYMM.GetPrintStream().println("No of Nodes;Distance(G1-G2);Distance(G2-G1)");
		
        Graph g1 = SET.SyntheticLinearGraph(noOfNodesG12);
        int noOFNodes;
        double costGH;
        double costGHSymm;
		
		for(int i=0;i<noOfNodesG12;i++){
			noOFNodes=i+1;
			Graph g2 = SET.SyntheticLinearGraph(noOFNodes);
			System.out.println("--------------"+noOFNodes+"-----------------------");
	
			double start =System.currentTimeMillis();
			GED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
			double end =System.currentTimeMillis();
			costGH = GED.getBestEditpath().getTotalCosts()+GED.getBestEditpath().getHeuristicCosts();
			if (debug==true)
			{
				System.out.println("The best solution is=");
				GED.getBestEditpath().printMe();	
				System.out.print("The total cost of "+noOfNodesG12+" nodes is :");
				System.out.println(costGH);
			}
			
			// Testing the symmetry of the algorithm
		/*	GEDSymm = new HeuristicGraphEditDistance(g2,g1,Constants.costFunction,Constants.edgeHandler,false);
			costGHSymm = GEDSymm.getBestEditpath().getTotalCosts()+GEDSymm.getBestEditpath().getHeuristicCosts();
			
			SYMM.GetPrintStream().print(noOFNodes);
			SYMM.GetPrintStream().print(";"+costGH);
			SYMM.GetPrintStream().println(";"+costGHSymm); */
			///////////////////////////////////////////////
			
			
			
			COST.GetPrintStream().print(noOFNodes);
			COST.GetPrintStream().println (";"+costGH);
			
			SIZE.GetPrintStream().print(noOFNodes);
			SIZE.GetPrintStream().print(";"+GED.openCounterSize);
			SIZE.GetPrintStream().println(";"+GED.editPathCounter);
			
			
			SET.GetPrintStream().print(noOFNodes);
			SET.GetPrintStream().println(";"+(end-start));
		
			if (debug==true)
			{
				System.out.println("OPEN max size = "+GED.openCounterSize);
				System.out.println("No of edit paths = "+GED.editPathCounter);
			}
		
		}
		COST.CloseStream();
		SET.CloseStream();
		SIZE.CloseStream();
		SYMM.CloseStream();	
		
		
	}

	//*************************************************************************************
	
	private void testGED(double n, double e, double a,int noOfNodesG12, SpeedEvalTest SET,
			SpeedEvalTest COST, SpeedEvalTest SIZE,SpeedEvalTest SYMM) throws IOException {
		// TODO Auto-generated method stub
		
		Graph g1 = SET.SyntheticLinearGraph(noOfNodesG12);
		GraphEditDistance GED ;
		GraphEditDistance GEDSymm ;
		SIZE.GetPrintStream().println("No of Nodes;OPEN size;EditPath Size");
		COST.GetPrintStream().println("No of Nodes;Distance");
		SET.GetPrintStream().println("No of Nodes;TIME");
		SYMM.GetPrintStream().println("No of Nodes;Distance(G1-G2);Distance(G2-G1)");
		int noOFNodes;
		for(int i=0;i<noOfNodesG12;i++){
			System.out.println("---------------------"+(i+1)+"---------------------------------");
			noOFNodes=i+1;
			Graph g2 = SET.SyntheticLinearGraph(noOFNodes);


			double start =System.currentTimeMillis();
			GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
			double end =System.currentTimeMillis();
			
			if (debug==true)
			{
				System.out.println("The best solution is=");
				GED.getBestEditpath().printMe();
				
				System.out.print("The total cost of "+noOfNodesG12+" nodes is :");
				System.out.println(GED.getBestEditpath().getTotalCosts());
			}
			
			// Testing the symmetry of the algorithm
			/*GEDSymm = new GraphEditDistance(g2,g1,Constants.costFunction,Constants.edgeHandler,false);
			SYMM.GetPrintStream().print(";"+GED.getBestEditpath().getTotalCosts());
			SYMM.GetPrintStream().println(";"+GEDSymm.getBestEditpath().getTotalCosts()); */
			///////////////////////////////////////////////
						
			
			
			COST.GetPrintStream().print(noOFNodes);
			COST.GetPrintStream().println (";"+GED.getBestEditpath().getTotalCosts());
			
			
			SIZE.GetPrintStream().print(noOFNodes);
			SIZE.GetPrintStream().print(";"+GED.openCounterSize);
			SIZE.GetPrintStream().println(";"+GED.editPathCounter);
		
			SET.GetPrintStream().print(noOFNodes);
			SET.GetPrintStream().println(";"+(end-start));
			
			if (debug==true)
			{				
				System.out.println("OPEN max size = "+GED.openCounterSize);
				System.out.println("No of edit paths = "+GED.editPathCounter);
			}
			
		}
		
		SET.CloseStream();
		COST.CloseStream();
		SIZE.CloseStream();	
		SYMM.CloseStream();	
		
		
	}
	
	//*************************************************************************************

	/* In this function, gxl graphs are used to measure the accuracy of the 4 algorithms 
	 while classifying graphs */
	
	private void ClassificationTest(boolean debug) throws Exception {
		// TODO Auto-generated method stub
		String propFile = "bin/properties/letter.prop";
		String saveFile = "./data/LETTER/";
		int counter; 
		double precision; 
		double totalTime;
		OneNNClassifier cs;
		double alpha=0.5;
		double edgeCosts=10;
		double nodeCosts=10;
		
		ICostFunction IC = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
		IEdgeHandler IH = new UniversalEdgeHandler(); 
		//1st algorithm (GED)
		cs =  new OneNNClassifier(propFile,false,0,saveFile,-1,IC,IH,alpha,edgeCosts,nodeCosts);		
		// Calculating the precision of classifying the graphs
		counter = cs.getCorrectlyClassifiedInstances(saveFile);
		precision = cs.getAccuracy(saveFile);
		totalTime = cs.getTotalClassificationTime(saveFile);
		System.out.println("source size : "+cs.source.size());
		System.out.println("**Number of correctly classified instances: "+counter);
		System.out.println("**¨Precision of classifying instances : "+precision);
		System.out.println("**Total classification time: "+totalTime); 
		
		//-----------------------------------------------------------------------
	
		//2nd algorithm (ASTAR)
		cs =  new OneNNClassifier(propFile,false,1,saveFile,-1,IC,IH,alpha,edgeCosts,nodeCosts);		
		// Calculating the precision of classifying the graphs
		counter = cs.getCorrectlyClassifiedInstances(saveFile);
		precision = cs.getAccuracy(saveFile);
		totalTime = cs.getTotalClassificationTime(saveFile);
		System.out.println("source size : "+cs.source.size());
		System.out.println("**Number of correctly classified instances: "+counter);
		System.out.println("**¨Precision of classifying instances : "+precision);
		System.out.println("**Total classification time: "+totalTime); 
		//-----------------------------------------------------------------------
		
		//3rd algorithm (Munkres)
		cs =  new OneNNClassifier(propFile,false,2,saveFile,-1,IC,IH,alpha,edgeCosts,nodeCosts);		
		// Calculating the precision of classifying the graphs
		counter = cs.getCorrectlyClassifiedInstances(saveFile);
		precision = cs.getAccuracy(saveFile);
		totalTime = cs.getTotalClassificationTime(saveFile);
		System.out.println("source size : "+cs.source.size());
		System.out.println("**Number of correctly classified instances: "+counter);
		System.out.println("**¨Precision of classifying instances : "+precision);
		System.out.println("**Total classification time: "+totalTime);
		
		//-----------------------------------------------------------------------
		
		//4th algorithm (Beam Search)
			cs =  new OneNNClassifier(propFile,false,3,saveFile,3,IC,IH,alpha,edgeCosts,nodeCosts);		
		// Calculating the precision of classifying the graphs
		counter = cs.getCorrectlyClassifiedInstances(saveFile);
		precision = cs.getAccuracy(saveFile);
		totalTime = cs.getTotalClassificationTime(saveFile);
		System.out.println("source size : "+cs.source.size());
		System.out.println("**Number of correctly classified instances: "+counter);
		System.out.println("**¨Precision of classifying instances : "+precision);
		System.out.println("**Total classification time: "+totalTime);
		
		
	}

	//************************************************************************************
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TestGraphEditDistanceAlgorithms TGEDA = new TestGraphEditDistanceAlgorithms();
		
		TGEDA.testBestEditSolution(false);
		TGEDA.ClassificationTest(false);
		System.out.println("The end of the Execution ...");
	}



	


}
