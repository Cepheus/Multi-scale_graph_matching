package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.ArrayList;

import util.EditPath;
import util.Graph;
import util.ICostFunction;
import util.LetterCostFunction;
import util.UnDirectedEdgeHandler;
import xml.XMLParser;

public class GEDLikeRunningTimeTestLetterDB {
	
	private String inputdir;
	static private int PLAIN_GED=0;
	static private int ASTAR_GED=1;
	static private int MUNKRES_GED=2;
	static private int BEAM_GED=3;
	ArrayList<Integer> ListOfMethods;
	PrintStream ps;
	private String outputdir;
	private Graph[] graphTab;
	private File[] files;
	boolean debug=false;
	private MatrixGenerator mgen;
	private Munkres munkres;
	private MunkresRec munkresRec;

	public GEDLikeRunningTimeTestLetterDB(String inputdir, String outputdir) throws FileNotFoundException{
		this.inputdir=inputdir;
		this.outputdir = outputdir;
		init();
		LaunchOnAllMethods();
		
	}

	private void LaunchOnAllMethods() throws FileNotFoundException {
		// TODO Auto-generated method stub
		for(Integer method : this.ListOfMethods){
			System.out.println("Method="+method);
			RunOneMethod(method);
		}
	}

	private void RunOneMethod(Integer method) throws FileNotFoundException {
		// TODO Auto-generated method stub
		if(method==GEDLikeRunningTimeTestLetterDB.PLAIN_GED){
			
			RunPlainGED();
			
		}
		
		
		if(method==GEDLikeRunningTimeTestLetterDB.ASTAR_GED){
			ArrayList<Integer> ListOfParameters = new ArrayList<Integer>();
			ListOfParameters.add(EditPath.NoAssigmentHeuristic);
			ListOfParameters.add(EditPath.MunkresAssigmentHeuristic);
			ListOfParameters.add(EditPath.LAPAssigmentHeuristic);
			for(Integer param:ListOfParameters){
				System.out.println("Param="+param);
				RunAstarGED(param);
			}
		}
		
		
		if(method==GEDLikeRunningTimeTestLetterDB.BEAM_GED){
			ArrayList<Integer> ListOfParameters = new ArrayList<Integer>();
			ListOfParameters.add(10);
			ListOfParameters.add(100);
			ListOfParameters.add(1000);
			for(Integer param:ListOfParameters){
				System.out.println("Param="+param);
				RunBeamSearchGED(param);
			}
		}
		
		if(method==GEDLikeRunningTimeTestLetterDB.MUNKRES_GED){
				mgen = new MatrixGenerator();
				munkres = new Munkres();
				munkresRec = new MunkresRec();
				mgen.setMunkres(munkresRec);
				
				RunMunkresGED();
			
		}
		
		
	}

	private void RunMunkresGED() throws FileNotFoundException {
		// TODO Auto-generated method stub
		ps = new PrintStream(outputdir+"/"+BeamSearchGraphEditDistance.class.getName()+"speed.csv");
		long start = System.currentTimeMillis();
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
				double[][] matrix = mgen.getMatrix(g1, g2);
				munkres.setGraphs(g1, g2);
				double distance = munkres.getCosts(matrix);
			}
		}
		
		long end = System.currentTimeMillis();
		long delay = end-start;
		ps.println(delay);
		ps.close();
	}

	private void RunBeamSearchGED(Integer param) throws FileNotFoundException {
		// TODO Auto-generated method stub
		ps = new PrintStream(outputdir+"/"+BeamSearchGraphEditDistance.class.getName()+"param_"+param+"speed.csv");
		long start = System.currentTimeMillis();
		
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
				BeamSearchGraphEditDistance GED = new BeamSearchGraphEditDistance(g1,g2,param,Constants.costFunction,Constants.edgeHandler,false);
			}
		}
		
		long end = System.currentTimeMillis();
		long delay = end-start;
		ps.println(delay);
		ps.close();
	}

	private void RunAstarGED(Integer param) throws FileNotFoundException {
		// TODO Auto-generated method stub
		ps = new PrintStream(outputdir+"/"+HeuristicGraphEditDistance.class.getName()+"param_"+param+"speed.csv");
		long start = System.currentTimeMillis();
		
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
				HeuristicGraphEditDistance GED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,param,false);
			}
		}
		
		long end = System.currentTimeMillis();
		long delay = end-start;
		ps.println(delay);
		ps.close();
	}

	private void RunPlainGED() throws FileNotFoundException {
		// TODO Auto-generated method stub
		ps = new PrintStream(outputdir+"/"+GraphEditDistance.class.getName()+"speed.csv");
		long start = System.currentTimeMillis();
		
		for(Graph g1:graphTab){
			for(Graph g2:graphTab){
				GraphEditDistance GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
			}
		}
		
		long end = System.currentTimeMillis();
		long delay = end-start;
		ps.println(delay);
		ps.close();
	}

	private void init() throws FileNotFoundException {
		// TODO Auto-generated method stub
		double n=1;
		double e=1;
		double a=1;
		Constants.costFunction = (ICostFunction)new LetterCostFunction(n,e,a); 
		
		 // set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UnDirectedEdgeHandler();//UnDirectedEdgeHandler(); 

		ListOfMethods = new ArrayList<Integer>();
		ListOfMethods.add(GEDLikeRunningTimeTestLetterDB.PLAIN_GED);
		ListOfMethods.add(GEDLikeRunningTimeTestLetterDB.ASTAR_GED);
		ListOfMethods.add(GEDLikeRunningTimeTestLetterDB.BEAM_GED);
		ListOfMethods.add(GEDLikeRunningTimeTestLetterDB.MUNKRES_GED);
	
		
		LoadGraph(this.inputdir);
		
		File repertoire = new File(this.outputdir);
		if(!repertoire.exists()){
			System.out.println("Le répertoire output n'existe pas");
			repertoire.mkdirs();
		}
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
		graphTab = new Graph[files.length];
		
		XMLParser xmlParser = new XMLParser();
		try
		{
			for(int i=0;i<files.length;i++)
			{	
				graphTab[i] = xmlParser.parseGXL(files[i].toString());
				System.out.println(files[i].toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		new GEDLikeRunningTimeTestLetterDB("./data/test","./data/result");
	}

}
