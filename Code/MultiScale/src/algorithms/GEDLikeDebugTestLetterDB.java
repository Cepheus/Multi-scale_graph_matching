package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;

import util.CoilCostFunction;
import util.CoilRAGCostFunction;
import util.EditPath;
import util.FingerprintCostFunction;
import util.GRECCostFunction;
import util.Graph;
import util.ICostFunction;
import util.LetterCostFunction;
import util.MoleculesCostFunction;
import util.MutagenCostFunction;
import util.UnDirectedEdgeHandler;
import util.UniversalCostFunction;
import util.UnlabeledCostFunction;
import util.WebCostFunction;
import xml.XMLParser;

public class GEDLikeDebugTestLetterDB {

	private String inputdir;
	static private int PLAIN_GED=0;
	static private int ASTAR_GED=1;
	static private int MUNKRES_GED=2;
	static private int BEAM_GED=3;
	static private int LowerUpper_GED=4;
	static private int DepthFirstLowerUpper_GED=5;
	private static Integer SKIP=-1;
	ArrayList<Integer> ListOfMethods;
	PrintStream ps;
	private String outputfile;
	private Graph[] graphTab;
	private File[] files;
	boolean debug=false;
	private MatrixGenerator mgen;
	private Munkres munkres;
	private MunkresRec munkresRec;
	//static private int nodesize=10;
	//static private int edgesize=20;
	
	static private int nodesize=Integer.MAX_VALUE;
	static private int edgesize=Integer.MAX_VALUE;

	public GEDLikeDebugTestLetterDB(String inputdir, String outputfile) throws FileNotFoundException {
		this.inputdir=inputdir;
		this.outputfile = outputfile;
		init();
		LaunchOnAllMethods();
	}

	private void LaunchOnAllMethods() throws FileNotFoundException {
		// TODO Auto-generated method stub
		for(Integer method : this.ListOfMethods){
			System.out.println("Method="+method);
			RunOneMethod(method);
		}
		ps.close();
	}

	private void RunOneMethod(Integer method) throws FileNotFoundException {
		// TODO Auto-generated method stub
		if(method==GEDLikeDebugTestLetterDB.PLAIN_GED){

			RunPlainGED();

		}


		if(method==GEDLikeDebugTestLetterDB.ASTAR_GED){
			ArrayList<Integer> ListOfParameters = new ArrayList<Integer>();
			//ListOfParameters.add(this.SKIP);
			ListOfParameters.add(this.SKIP);
		    //ListOfParameters.add(EditPath.NoAssigmentHeuristic);
			ListOfParameters.add(EditPath.MunkresAssigmentHeuristic);
			ListOfParameters.add(EditPath.LAPAssigmentHeuristic);
			//ListOfParameters.add(-1);
			for(Integer param:ListOfParameters){
				System.out.println("Param="+param);
				if(param ==this.SKIP){
					this.RunSKIP();
				}else{
					RunAstarGED(param);
				}
			}
		}

		if(method==GEDLikeDebugTestLetterDB.BEAM_GED){
			ArrayList<Integer> ListOfParameters = new ArrayList<Integer>();
			ListOfParameters.add(1);
			ListOfParameters.add(10);
			ListOfParameters.add(100);

			for(Integer param:ListOfParameters){
				System.out.println("Param="+param);
				RunBeamSearchGED(param);
			}
		}



		if(method==GEDLikeDebugTestLetterDB.MUNKRES_GED){
			mgen = new MatrixGenerator();
			munkres = new Munkres();
			munkresRec = new MunkresRec();
			mgen.setMunkres(munkresRec);

			RunMunkresGED();
		}	


		if(method==GEDLikeDebugTestLetterDB.LowerUpper_GED){

			RunLowerUpperBoundGED();

		}
		
		
		if(method==GEDLikeDebugTestLetterDB.DepthFirstLowerUpper_GED){

			RunDepthFirstGraphEditDistance();

		}

		if(method==GEDLikeDebugTestLetterDB.SKIP){
			RunSKIP();

		}

	}

	private void RunSKIP() {
		// TODO Auto-generated method stub
		int count = 0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
					ps.print("SKIP;");
					ps.print("rien;");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					//LowerUpperBoundsGED GED = new LowerUpperBoundsGED(g1,g2,Constants.costFunction,Constants.edgeHandler,LowerUpperBoundsGED.MunkresAssigmentHeuristic,false);
					ps.print(-1+";");
					ps.print((int)-1+";");
					ps.print((int)-1+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(-1);
				
			}
			count++;
			DisplayProgression(count);
		}
	}

	private void RunLowerUpperBoundGED() {
		// TODO Auto-generated method stub
		int count = 0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
			
					ps.print("LowerUpperBoundGED;");
					ps.print("rien;");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					LowerUpperBoundsGED GED = new LowerUpperBoundsGED(g1,g2,Constants.costFunction,Constants.edgeHandler,EditPath.LAPAssigmentHeuristic,false);
					ps.print(GED.getBestEditpath().getTotalCosts()+";");
					ps.print((int)GED.getNbExploredNode()+";");
					ps.print((int)GED.getMaxSizeOpen()+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
					
			}
			count++;
			DisplayProgression(count);
		}
	}
	
	
	private void RunDepthFirstGraphEditDistance(){
		
		int count = 0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
			
					ps.print("DepthFirstGED;");
					ps.print("rien;");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					DepthFirstGraphEditDistance GED = new DepthFirstGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
					ps.print(GED.upperBound+";");
					ps.print((int)GED.getNbExploredNode()+";");
					ps.print((int)GED.getMaxSizeOpen()+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
					
			}
			count++;
			DisplayProgression(count);
		}
	}

	private void RunMunkresGED() {
		// TODO Auto-generated method stub
		int count = 0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
				
					ps.print("Munkres GED;");
					ps.print("rien;");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					double[][] matrix = mgen.getMatrix(g1, g2);
					munkres.setGraphs(g1, g2);
					double distance = munkres.getCosts(matrix);
					ps.print(distance+";");
					ps.print("rien;");
					ps.print("rien;");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
				}
			
			count++;
			DisplayProgression(count);
		}
	}

	boolean Filter(Graph g1, Graph g2){

//		if(g1.size()>=nodesize){
//			return false;
//		}
//
//		if(g2.size()>=nodesize){
//			return false;
//		}
//
//		if(g1.getEdges().size()>=edgesize){
//			return false;
//		}
//
//		if(g2.getEdges().size()>=edgesize){
//			return false;
//		}
		
		if(g1.size()!=nodesize){
			return false;
		}

		if(g2.size()!=nodesize){
			return false;
		}


		return true;
	}

	private void RunBeamSearchGED(Integer param) {
		// TODO Auto-generated method stub
		int count = 0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
					ps.print("BeamSearch GED;");
					ps.print(param+";");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					BeamSearchGraphEditDistance GED = new BeamSearchGraphEditDistance(g1,g2,param,Constants.costFunction,Constants.edgeHandler,false);
					ps.print(GED.getBestEditpath().getTotalCosts()+";");
					ps.print((int)GED.getNbExploredNode()+";");
					ps.print((int)GED.getMaxSizeOpen()+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
			}
			count++;
			DisplayProgression(count);
		}
	}

	private void RunAstarGED(Integer param)  {
		// TODO Auto-generated method stub
		int count=0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
					ps.print("GED A*;");
					ps.print(param+";");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					HeuristicGraphEditDistance GED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,param,false);
					ps.print(GED.getBestEditpath().getTotalCosts()+";");
					ps.print((int)GED.getNbExploredNode()+";");
					ps.print((int)GED.getMaxSizeOpen()+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
			}
			count++;
			DisplayProgression(count);
		}
	}

	private void RunPlainGED()  {
		// TODO Auto-generated method stub
		int count=0;
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
					ps.print("PlainGED;");
					ps.print("rien;");
					ps.print(g1.getId()+";");
					ps.print(g2.getId()+";");
					long start = System.currentTimeMillis();

					GraphEditDistance GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
					ps.print(GED.getBestEditpath().getTotalCosts()+";");
					ps.print((int)GED.getNbExploredNode()+";");
					ps.print((int)GED.getMaxSizeOpen()+";");
					long end = System.currentTimeMillis();
					long delay = end-start;
					ps.println(delay);
			}
			count++;
			DisplayProgression(count);
		}



	}

	private void DisplayProgression(int count) {
		// TODO Auto-generated method stub
		System.out.println(count/(double)this.graphTab.length);

	}

	private void init() throws FileNotFoundException {
		// TODO Auto-generated method stub


		// set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UnDirectedEdgeHandler();//UnDirectedEdgeHandler(); 

		ListOfMethods = new ArrayList<Integer>();

		//ListOfMethods.add(GEDLikeDebugTestLetterDB.PLAIN_GED);
		ListOfMethods.add(-1);


		//ListOfMethods.add(GEDLikeDebugTestLetterDB.ASTAR_GED);
		ListOfMethods.add(-1);
		ListOfMethods.add(-1);
		ListOfMethods.add(-1);

		//ListOfMethods.add(GEDLikeDebugTestLetterDB.BEAM_GED);
		ListOfMethods.add(-1);
		ListOfMethods.add(-1);
		ListOfMethods.add(-1);

		//ListOfMethods.add(GEDLikeDebugTestLetterDB.MUNKRES_GED);
		ListOfMethods.add(-1);

		//ListOfMethods.add(GEDLikeDebugTestLetterDB.LowerUpper_GED);
		ListOfMethods.add(-1);
		
		ListOfMethods.add(GEDLikeDebugTestLetterDB.DepthFirstLowerUpper_GED);

		LoadGraph(this.inputdir);

		ps = new PrintStream(this.outputfile);
		ps.println("Method;Param;Graph1 Name;Graph2 Name;distance;#explored nodes;max open size;time");
		ps.println(""+this.graphTab.length+";;;;;;;");
	}

	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};

	private void LoadGraph(String inputdir2) {
		// TODO Auto-generated method stub
		File repertoire = new File(inputdir2);
		if(!repertoire.exists()){
			System.out.println("Le répertoire n'existe pas");
		}

		files=repertoire.listFiles(gxlFileFilter);
		int nbgraphs=0;
		
		XMLParser xmlParser = new XMLParser();
		try
		{
			for(int i=0;i<files.length;i++)
			{	
				Graph g = xmlParser.parseGXL(files[i].toString());
				if(this.Filter(g, g)){
					nbgraphs++;
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		graphTab = new Graph[nbgraphs];
		int tmpindex=0;
		xmlParser = new XMLParser();
		try
		{
			for(int i=0;i<files.length;i++)
			{	
				Graph g = xmlParser.parseGXL(files[i].toString());
				if(this.Filter(g, g)){
					graphTab[tmpindex]=g;
					tmpindex++;
					System.out.println(files[i].toString());
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws FileNotFoundException &
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
//			for(int i=10;i<15;i++){
//				nodesize=i;
//				double n=11;
//				double e=1.1;
//				double a=0.25;
//				Constants.costFunction = (ICostFunction)new MutagenCostFunction(n,e,a); 
//				new GEDLikeDebugTestLetterDB("./data/MUTA","./data/result/resultperfmutagen"+nodesize+".csv");
//			
//			//new GEDLikeDebugTestLetterDB("/Users/romainraveaux/Dropbox/recherche/graph/database/Mutagenicity/data/","./data/result/resultperfletter.csv");
//
//			}
		
		
//
//		double n=90;
//		double e=15;
//		double a=0.5;
//		Constants.costFunction = (ICostFunction)new CoilCostFunction(n,e,a); 
//
//		new GEDLikeDebugTestLetterDB("./data/testCOILDEL","./data/result/resultperfletter.csv");


		//		Constants.costFunction = (ICostFunction)new WebCostFunction(); 	
		//		new GEDLikeDebugTestLetterDB("./data/testWEB","./data/result/resultperfletter.csv");

		//		double n=1;
		//		double e=1;
		//		double a=0.5;
		//		Constants.costFunction = (ICostFunction)new CoilRAGCostFunction(n,e,a); 
		//		
		//		new GEDLikeDebugTestLetterDB("./data/testCOILRAG","./data/result/resultperfletter.csv");

//
//				double n=3;
//				double e=3;
//				double a=0.8;
//				Constants.costFunction = (ICostFunction)new MoleculesCostFunction(n,e,a); 
//				
//				new GEDLikeDebugTestLetterDB("./data/testMOLECULES","./data/result/resultperfletter.csv");


		//		double n=1;
		//		double e=1;
		//		double a=0.2;
		//		Constants.costFunction = (ICostFunction)new FingerprintCostFunction(n,e,a); 
		//		
		//		new GEDLikeDebugTestLetterDB("./data/testFINGERPRINT","./data/result/resultperfletter.csv");
//		double n=90;
//		double e=15;
//		double a=0.5;
//		Constants.costFunction = (ICostFunction)new GRECCostFunction(n,e,a); 
//		new GEDLikeDebugTestLetterDB("./data/testGRECsmall","./data/result/resultperfletter.csv");

		for(int i=1;i<15;i++){
			nodesize=i;
			double n=90;
			double e=15;
			double a=0.5;
			Constants.costFunction = (ICostFunction)new GRECCostFunction(n,e,a); 
			new GEDLikeDebugTestLetterDB("./data/testGREC","./data/result/resultperfGRECDFS"+nodesize+".csv");
		}
				
//				double n=0.3;
//				double e=0.5;
//				double a=0.75;
//				Constants.costFunction = (ICostFunction)new LetterCostFunction(n,e,a); 
//				
//				
//				new GEDLikeDebugTestLetterDB("./data/testLETTER","./data/result/resultperfletterv2.csv");

//
//				double n=0.3;
//				double e=0.5;
//				double a=0.75;
//				Constants.costFunction = (ICostFunction)new LetterCostFunction(n,e,a); 
//				
//				
//				new GEDLikeDebugTestLetterDB("./data/test","./data/result/resultperfletter.csv");


	}

}
