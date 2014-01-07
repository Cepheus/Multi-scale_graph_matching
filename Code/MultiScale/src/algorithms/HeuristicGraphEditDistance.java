package algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.mortbay.jetty.AbstractGenerator.Output;

import util.EditPath;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.Node;
import util.UnDirectedEdgeHandler;
import util.UniversalCostFunction;
import util.UniversalEdgeHandler;
import util.UnlabeledCostFunction;
import xml.XMLParser;
import xml.XMLWriter;

/**
 * @author Romain Raveaux
 * 
 */

public class HeuristicGraphEditDistance {


	ArrayList<EditPath> OPEN; //Tiens à jour la liste des noeud non encore utilisés. Le score du chemin.
	private Graph G1; // Graph 1	
	private Graph G2; // Graph 2
	private Node CurNode; //Current node
	private int G2NbNodes; //number of nodes of G2
	public int openCounterSize; // a variable that tracks the maximum size of the set OPEN
	public  int editPathCounter; // a variable that tracks number of edit paths
	
	//static public int NoHeuristic=0;
	//static public int NoAssigmentHeuristic=1;
	//static public int MunkresAssigmentHeuristic=2;
	//static public int LAPAssigmentHeuristic=3;

	//debug info
		//debug variable
		int nbexlporednode=0;
		int maxopensize=0;
	
	private EditPath BestEditpath; // Zeina: The best answer
	boolean debug;
	private int heuristicmethod;

	// A function that returns the best edit path "the optimal solution"
	public EditPath getBestEditpath() {
		return BestEditpath;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public HeuristicGraphEditDistance(Graph G1, Graph G2,ICostFunction costfunction, IEdgeHandler edgehandler,int heuristicmethod,boolean debug){
			
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		this.heuristicmethod=heuristicmethod;
		G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath=null; // In the beginning, the list is empty. 
		//La liste OPEN est initialisée à VIDE. OPEN est une liste de chemin
		//The set open contains the search tree nodes to be processed in the next steps
		OPEN = new ArrayList<EditPath>();
		//avant la boucle
		inti();
		BestEditpath = loop();
	}
	
	
	public HeuristicGraphEditDistance(Graph G1, Graph G2,ICostFunction costfunction, IEdgeHandler edgehandler,boolean debug){
		
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		this.heuristicmethod=EditPath.NoAssigmentHeuristic;
		G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath=null; // In the beginning, the list is empty. 
		//La liste OPEN est initialisée à VIDE. OPEN est une liste de chemin
		//The set open contains the search tree nodes to be processed in the next steps
		OPEN = new ArrayList<EditPath>();
		//avant la boucle
		inti();
		BestEditpath = loop();
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	private EditPath loop() {
		// TODO Auto-generated method stub

		while(true == true){
			
			if(OPEN.isEmpty() == true){
				System.out.println("Error : No more candidates, no complete solution could be found");
				System.out.println("Error : Please check your graph data");
				return null;
			}
			if(debug==true) System.out.println("----------------------------------------------------------");
			
			//On cherche dans OPEN le chemin avec le cout minimum (Pmin) et on l'enléve de OPEN
			EditPath pmin =findmincostEditPathWithHeuristic(OPEN);

			OPEN.remove(pmin);
			
			if(debug==true) 
			{
				System.out.println("---------------Choosing the minimal cost-------------------------------------------");
				pmin.ComputeHeuristicCosts();
				System.out.println("Cost (g):"+pmin.getTotalCosts());
				System.out.println("Cost (h):"+pmin.getHeuristicCosts());
				System.out.println("Current Path=");
				pmin.printMe();
			}
			

			if(pmin.isComplete() == true){
				return pmin;
			}else{
				// K est l'index du noeud de G1 en cours de traintement. Premier courp, k=1. 
				// Si on a traité  k < taille de G1
				
				if(pmin.getUnUsedNodes1().size() > 0){;
					this.CurNode=pmin.getNext();
					if(debug==true) System.out.println("Current Node="+this.CurNode.getId());
					
					// pour tous les noeuds (w)  de V2  qui ne sont pas encore utilisés dans Pmin. on ajoute dans pmin toutes les substitutions de uk+1 avec w.
					LinkedList<Node>UnUsedNodes2= pmin.getUnUsedNodes2();
					for(int i=0;i<UnUsedNodes2.size();i++){
						EditPath newpath = new EditPath(pmin);
						
						Node w = UnUsedNodes2.get(i);
						newpath.addDistortion(this.CurNode, w);
						if(debug==true)
						{
							System.out.println("Substitution CurNode and ith node of G2= "+ this.CurNode.getId()+ " -- "+ w.getId());
							newpath.ComputeHeuristicCosts();
						    newpath.printMe();
						}
							
						this.OPEN.add(newpath);
						if(debug==true) TrackOpenEditPathSize();
						if(debug==true) System.out.println("----------------------------------------------------------");
					}
					// On met dans pmin une suppresion de uk+1.
					EditPath newpath = new EditPath(pmin);
					newpath.addDistortion(this.CurNode, Constants.EPS_COMPONENT);
					if(debug==true) 
					{
						System.out.println("Deletion CurNode="+this.CurNode.getId());
						newpath.ComputeHeuristicCosts();
						newpath.printMe();
						System.out.println("----------------------------------------------------------");
					}
					
					this.OPEN.add(newpath);
					if(debug==true) TrackOpenEditPathSize();

				}else{
					//Sinon si k= taille de G1
					//On met dans pmin toutes les insertions des noeuds  de V2  qui ne sont pas encore utilisés dans Pmin. 
					//On remet Pmin dans OPEN
					EditPath newpath = new EditPath(pmin);
					newpath.complete();	
					this.OPEN.add(newpath);
					if(debug==true) TrackOpenEditPathSize();
					
					if(debug==true)
					{
						System.out.println("We complete the path by inserting all remaining nodes of G2 CurNode="+this.CurNode.getId());
						newpath.printMe();
					}
						
				}//fin si k<(this.G1NbNodes-1)
			}//fin si chemin optimal

		}//boucle while
	}
	
	

	//A function that returns the minimal edit path's cost in the set OPEN
	private EditPath findmincostEditPathWithHeuristic(ArrayList<EditPath> oPEN2) {
		// TODO Auto-generated method stub
		int i=0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		double h=0.0,g=0.0;
		int indexmin = -1;

		for(i=0;i<nbpaths;i++){
			EditPath p = OPEN.get(i);
			g = p.getTotalCosts(); // computing the cost of the edit path from the root till the current node...
			h=p.ComputeHeuristicCosts(this.heuristicmethod);
			/*if(this.heuristicmethod==NoAssigmentHeuristic) h=p.ComputeHeuristicCosts(); // estimating the cost of the edit path from the current node till the leaf nodes
			if(this.heuristicmethod==MunkresAssigmentHeuristic) h=p.ComputeHeuristicCostsAssignment();
			if(this.heuristicmethod==LAPAssigmentHeuristic) h=p.ComputeHeuristicCostsAssignmentLAP();
			if(this.heuristicmethod==NoHeuristic) h=0;*/
			
			
			if((g+h) <minvalue){
				minvalue = g+h;
				indexmin = i;
			}
		}

		this.nbexlporednode++;
		maxopensize = Math.max(this.OPEN.size(), this.maxopensize);
		return OPEN.get(indexmin);
	}
	
	//----------------------------------------------------------------------------------------
	// the initialization function
	private void inti() {
		if(G1.size() ==0 && G2.size() ==0){
			System.out.println("G1 has no node inside");
			System.out.println("G2 has no node inside");
			System.out.println("I cannot work !!!");
		}
		
		if(G1.size() ==0 ){
			System.out.println("G1 has no node inside");
			this.G1=G2;
			this.G2=G1;
			G2NbNodes = G2.size();
			//p = new EditPath(G2,G1,this.heuristicmethod);
		}
		
		// TODO Auto-generated method stub
		for(int i=0;i<G2NbNodes;i++){
			//Dans la liste OPEN on met toutes les substitions de u1 avec tous les noeuds de g2.
			//index du noeud courant
			
						
			EditPath p = new EditPath(G1,G2,this.heuristicmethod); // An intialization for both G1 and G2 "to show that we did not use neither edges nor nodes of both G1 and G2"
			Node v = (Node)G2.get(i);
			this.CurNode = p.getNext();
			/*Add distortion between the current node u1 and each node in G2 
			 distortion means insertion or substitution but not deletion as we are sure that
			 G2 has nodes as we are inside the for loop of G2 */
			p.addDistortion(this.CurNode, v);
			if(debug==true) System.out.println("Substitution CurNode G1 and ith node of G2="+this.CurNode.getId()+"  "+v.getId());
			OPEN.add(p);
			if(debug==true) TrackOpenEditPathSize();
		}

		//L3 : On met dans OPEN une suppresion de u1. Dans le cas ou supprimer u1 serait une possibilité optimale.
		EditPath p = new EditPath(G1,G2,this.heuristicmethod);
		this.CurNode = p.getNext();
		p.addDistortion(this.CurNode, Constants.EPS_COMPONENT); // Zeina: deletion
		if(debug==true) System.out.println("Deletion CurNode="+this.CurNode.getId());
		OPEN.add(p);
		if(debug==true) TrackOpenEditPathSize();
	}
	//----------------------------------------------------------------------------------------

	
	// A function that tracks the no of unexamined "added" edit Paths and the maximum size of the set OPEN
	private void TrackOpenEditPathSize() {
		// TODO Auto-generated method stub
		this.editPathCounter++;
		if(openCounterSize < OPEN.size())
		{
			openCounterSize=OPEN.size();
			if(debug==true)
			{
				System.out.println("OPEN max size = "+openCounterSize);
				System.out.println("No of edit paths = "+editPathCounter);
			
			}
		}
	}

	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Before executing the Heuristic Graph Edit Distance function");
		//t1(); cxl files
	
		//t2();
		//t3(); // linear files
		t4();
		System.out.println("After executing the Heuristic Graph Edit Distance function ");
		
	}
	
	private static void t3() {
		// TODO Auto-generated method stub
		double alpha=0.5;
		double edgeCosts=2;
		double nodeCosts=2;
		int noOfNodesG12=10;
		int noOFNodes;
		double costGH;
		
		SpeedEvalTest SET = new SpeedEvalTest("HeuristicGraphEditDistance-TIME.txt");
		SpeedEvalTest COST = new SpeedEvalTest("HeuristicGraphEditDistance-COST.txt");
		SpeedEvalTest SIZE = new SpeedEvalTest("HeuristicGraphEditDistance-SIZE.csv");
		
		
	//	Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5); // to give a value for alpha which is 0.5
		Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1 
		// set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler(); 
		XMLWriter xmlWriter = new XMLWriter();
		Graph g1 = SET.SyntheticLinearGraph(noOfNodesG12);
		
		
		for(int i=0;i<noOfNodesG12;i++){
			noOFNodes=i+1;
			Graph g2 = SET.SyntheticLinearGraph(noOFNodes);
			
		//	SET.StartChrono();
		//	SET.SetUpTimeSensors();
			HeuristicGraphEditDistance GED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,0,false);
		//	SET.CpuDisplay(noOFNodes);
			//	System.out.println("i="+(i+1)+"   "+GED.getBestEditpath().getTotalCosts());
			//SET.StopChrono();
			//SET.WriteElapsedTime();
			
			costGH = GED.getBestEditpath().getTotalCosts()+GED.getBestEditpath().getHeuristicCosts();
			System.out.print("The total cost of "+noOfNodesG12+" nodes is :");
			System.out.println(costGH);
			
			
			COST.GetPrintStream().print(noOfNodesG12);
			COST.GetPrintStream().println (";"+costGH);
			
			SIZE.GetPrintStream().print(noOFNodes);
			SIZE.GetPrintStream().print(";"+GED.openCounterSize);
			SIZE.GetPrintStream().println(";"+GED.editPathCounter);
			
		
			
			System.out.println("OPEN max size = "+GED.openCounterSize);
			System.out.println("No of edit paths = "+GED.editPathCounter);
			
		
		}
		COST.CloseStream();
		SET.CloseStream();
		SIZE.CloseStream();
	
	}
	
	
	private static void t1() {
		// TODO Auto-generated method stub
		double alpha=0.5;
		double edgeCosts=1;
		double nodeCosts=1;
		String sg1;
		String sg2;
		SpeedEvalTest SET = new SpeedEvalTest("Astar 7-5.txt");
		//sg1 =  args[0];
		//sg2 =  args[1];
		//sg1 = "./data/g1test.gxl";
		//sg2 = "./data/g2test.gxl";
		sg1 = "./data/example/7.gxl";
	    sg2 = "./data/example/5.gxl";
		 //GraphCollection source, target;
		// TODO Auto-generated method stub
		// set the appropriate cost function
		//Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1
		//Constants.costFunction = (ICostFunction)new MoleculesCostFunction(nodeCosts, edgeCosts); 
		Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5); 
		
		 // set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler();//UnDirectedEdgeHandler(); 
		
		// source = new GraphCollection();
		// source.setCollectionName(args[0]);
		 
		 
		 //target = new GraphCollection();
		 //target.setCollectionName(args[1]);
		
		XMLParser xmlParser = new XMLParser();
		Graph g1 = null;
		Graph g2 = null;
		try {
			g1 = xmlParser.parseGXL(sg1);
			g2= xmlParser.parseGXL(sg2);
			SET.StartChrono();
			HeuristicGraphEditDistance GED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,0,false);
			SET.StopChrono();
			SET.WriteElapsedTime();
			System.out.println("****************The best solution is=");
			GED.getBestEditpath().printMe();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//source.add(g2);
		//target.add(g1);
		SET.CloseStream();
	}
	
	// Zeina has commented this code
	/*
	private static void t1() {
		// TODO Auto-generated method stub
		double alpha=0.5;
		double edgeCosts=0.5;
		double nodeCosts=0.5;
		String sg1;
		String sg2;
		//sg1 =  args[0];
		//sg2 =  args[1];
		//sg1 = "./data/g1test.gxl";
		//sg2 = "./data/g2test.gxl";
		sg1 = "./data/example/5.gxl";
	   sg2 = "./data/example/7.gxl";
		 //GraphCollection source, target;
		// TODO Auto-generated method stub
		// set the appropriate cost function
		//Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1
		//Constants.costFunction = (ICostFunction)new MoleculesCostFunction(nodeCosts, edgeCosts); 
		Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5); 
		
		 // set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler();//UnDirectedEdgeHandler(); 
		
		// source = new GraphCollection();
		// source.setCollectionName(args[0]);
		 
		 
		 //target = new GraphCollection();
		 //target.setCollectionName(args[1]);
		
		XMLParser xmlParser = new XMLParser();
		Graph g1 = null;
		Graph g2 = null;
		try {
			g1 = xmlParser.parseGXL(sg1);
			g2= xmlParser.parseGXL(sg2);
			GraphEditDistance GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
			GED.getBestEditpath().printMe();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//source.add(g2);
		//target.add(g1);
	}
	private static void t2() throws FileNotFoundException {
		// TODO Auto-generated method stub
		SpeedEvalTest SET = new SpeedEvalTest("gedspeedheuristic" +10 + ".txt");
		Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5); 
		 // set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler(); 
		
		Graph g1 = SET.SyntheticLinearGraph(10);
	
		
		for(int i=0;i<10;i++){
			Graph g2 = SET.SyntheticLinearGraph(i+1);
			SET.StartChrono();
			GraphEditDistance GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
			System.out.println("i="+(i+1)+"   "+GED.getBestEditpath().getTotalCosts());
			SET.StopChrono();
			SET.WriteElapsedTime();
			
		}
		SET.CloseStream();
	}
*/
	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};
	
	private static void t4()
	{
		Constants.costFunction = (ICostFunction)new LetterCostFunction(1,1,1); 
		Constants.edgeHandler = new UnDirectedEdgeHandler();
		
		File repertoire = new File("./data/focus");
		File[] files=repertoire.listFiles(gxlFileFilter);
		//Graph[] graphTab = new Graph[files.length];
		
		if(!repertoire.exists()){
			System.out.println("Le répertoire n'existe pas");
		}
		
//		XMLParser xmlParser = new XMLParser();
//		try
//		{
//			for(int i=0;i<files.length;i++)
//			{	
//				//graphTab[i] = xmlParser.parseGXL(files[i].toString());
//				System.out.println(files[i].toString());
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		try {
			SpeedEvalTest SET = new SpeedEvalTest("gedspeed" +100 + ".txt");
			FileWriter fw = new FileWriter("./data/focus/comparetmp.csv", true);
			BufferedWriter output = new BufferedWriter(fw);
			output.write("Graph1;Graph2;Cout;Temps\n");
			Graph g2,g1;
			g2=g1=null;
			for(int i=0;i<files.length;i++)
			{
				try {
					XMLParser xmlParser = new XMLParser();
					 g1 =  xmlParser.parseGXL(files[i].toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int j=0;j<files.length;j++)//graphTab.length;j++)
				{	
					try {
						XMLParser xmlParser = new XMLParser();
						 g2 =  xmlParser.parseGXL(files[j].toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					output.write(g1.getId() + ";" + g2.getId());
					SET.StartChrono();
					HeuristicGraphEditDistance HGED = new HeuristicGraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,EditPath.MunkresAssigmentHeuristic,false);
					SET.StopChrono();
					output.write(";"+HGED.getBestEditpath().getTotalCosts()+";"+SET.ElapsedTimeToString());
					System.out.println(i + " " + j );
					output.write("\n");
				}
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int getNbExploredNode() {
		// TODO Auto-generated method stub
		return this.nbexlporednode;
	}

	public int getMaxSizeOpen() {
		// TODO Auto-generated method stub
		return this.maxopensize;
	}
}
