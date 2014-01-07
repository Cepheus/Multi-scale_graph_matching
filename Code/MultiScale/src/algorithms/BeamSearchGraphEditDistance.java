package algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.jar.Pack200.Unpacker;

import javax.management.openmbean.OpenDataException;

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

public class BeamSearchGraphEditDistance {
	
	// L1: variables
	ArrayList<EditPath> OPEN; // List of nodes that are not used yet...
	private Graph G1; // Graph 1	
	private Graph G2; // Graph 2
	int UnProcessedNoEditPaths;
	private Node CurNode; // Current node
	private int G2NbNodes; //Number of nodes of G2
	private EditPath BestEditpath; // The best answer
	boolean debug; 
	public  int editPathCounter; // a variable that tracks number of edit paths


	//debug info
	//debug variable
	int nbexlporednode=0;
	int maxopensize=0;
			
	//-------------------------------------------------------------------------------------------------
	// A function that returns the best edit path "optimal solution"
	public EditPath getBestEditpath() {
		return BestEditpath;
	}
	//-------------------------------------------------------------------------------------------------
	
	public BeamSearchGraphEditDistance(Graph G1, Graph G2,int UnProcessedNoEditPaths,ICostFunction costfunction, IEdgeHandler edgehandler,boolean debug){
		
		if (debug==true) System.out.println("OPEN SIZE IS : "+ UnProcessedNoEditPaths);
		this.UnProcessedNoEditPaths=UnProcessedNoEditPaths;
		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath=null; // In the beginning, the list is empty. 
		// La liste OPEN est initialisée à VIDE. OPEN est une liste de chemin
		//The set open contains the search tree nodes to be processed in the next steps
		OPEN = new ArrayList<EditPath>();
		//avant la boucle
		inti();
		BestEditpath = loop();
	}
	
	//-------------------------------------------------------------------------------------------------
	
	////////////////////////////////////////////////////////////////////////////////////////////
	private EditPath loop() {
		// TODO Auto-generated method stub

		while(true == true){
			
			if(OPEN.isEmpty() == true){
				System.out.println("No more candidates, no complete solution could be found");
				System.out.println("Please check your graph data");
				return null;
			}
			if(debug==true) System.out.println("----------------------------------------------------------");
			
			Collections.sort(OPEN);
			 removeSomeEditPaths();
			 EditPath pmin = OPEN.get(0);
			 
			//As the list is sorted so the edit path that has the minimal cost is the one that is located at index 0 
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
				//K est l'index du noeud de G1 en cours de traintement. Premier courp, k=1. 
				//Si on a traité  k < taille de G1
				
				if(pmin.getUnUsedNodes1().size() > 0){
					this.CurNode=pmin.getNext();
					if(debug==true) System.out.println("Current Node="+this.CurNode.getId());
					
					//pour tous les noeuds (w)  de V2  qui ne sont pas encore utilisés dans Pmin. on ajoute dans pmin toutes les substitutions de uk+1 avec w.
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
						if(debug==true) editPathCounter++;
						if(debug==true) System.out.println("----------------------------------------------------------");
					}
					//On met dans pmin une suppresion de uk+1.
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
					if(debug==true) editPathCounter++;

				}else{
					//Sinon si k= taille de G1
					//On met dans pmin toutes les insertions des noeuds  de V2  qui ne sont pas encore utilisés dans Pmin. 
					//On remet Pmin dans OPEN
					EditPath newpath = new EditPath(pmin);
					newpath.complete();	
					this.OPEN.add(newpath);
					if(debug==true) editPathCounter++;
					if(debug==true)
					{
						System.out.println("We complete the path by inserting all remaining nodes of G2 CurNode="+this.CurNode.getId());
						newpath.printMe();
					}
						
				}//fin si k<(this.G1NbNodes-1)
			}//fin si chemin optimal

		}//boucle while
	}
	
	

	//----------------------------------------------------------------------------------------
	// Zeina: A function to substitute u1 to each element (vertex) of G2
	private void inti() {
		// TODO Auto-generated method stub
		for(int i=0;i<G2NbNodes;i++){
			//Dans la liste OPEN on met toutes les substitions de u1 avec tous les noeuds de g2.
			//index du noeud courant
		
			EditPath p = new EditPath(G1,G2); // An intialization for both G1 and G2 "to show that we did not use neither edges nor nodes of both G1 and G2"
			Node v = (Node)G2.get(i);
			this.CurNode = p.getNext();
			/* Add distortion between the current node u1 and each node in G2 
			 distortion means insertion or substitution but not deletion as we are sure that
			 G2 has nodes as we are inside the for loop of G2 */
			p.addDistortion(this.CurNode, v);
			if(debug==true) System.out.println("Substitution CurNode G1 and ith node of G2="+this.CurNode.getId()+"  "+v.getId());
			OPEN.add(p);
			if(debug==true) editPathCounter++;
		}

		//On met dans OPEN une suppresion de u1. Dans le cas ou supprimer u1 serait une possibilité optimale.
		EditPath p = new EditPath(G1,G2);
		this.CurNode = p.getNext();
		p.addDistortion(this.CurNode, Constants.EPS_COMPONENT); // Zeina: deletion
		if(debug==true) System.out.println("Deletion CurNode="+this.CurNode.getId());
		OPEN.add(p);
		if(debug==true) editPathCounter++;
	}
	//----------------------------------------------------------------------------------------

	// A function that deleted some edit paths in order to only have s nodes in the OPEN set
	private void removeSomeEditPaths() {
		// TODO Auto-generated method stub
		//System.out.println("UnProcessedNoEditPaths ==" + UnProcessedNoEditPaths);
		int size = OPEN.size();
		int delta =size-UnProcessedNoEditPaths;
		if(delta>0)
		{
			for(int i=0; i<delta ; i++){
				OPEN.remove(size-i-1);
			}
				
			
		}
			
		if(debug==true) System.out.println("After DELETING pairs" + OPEN.size());
		
		this.nbexlporednode++;
		maxopensize = Math.max(this.OPEN.size(), this.maxopensize);
	}
	
	
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Before executing the Heuristic Graph Edit Distance function");
		//t1();
	
		//t2();
		t3();
		System.out.println("After executing the Heuristic Graph Edit Distance function ");
		
	}
	
	private static void t3() {
		// TODO Auto-generated method stub
		double alpha=0.5;
		double edgeCosts=2;
		double nodeCosts=2;
		int noOFNodes;
		int noOfNodesG12=10;
		
		SpeedEvalTest SET = new SpeedEvalTest("BeamSearch-TIME.txt");
		SpeedEvalTest COST = new SpeedEvalTest("BeamSearch-COST.txt");
		SpeedEvalTest SIZE = new SpeedEvalTest("BeamSearch-SIZE.csv");
		
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
			BeamSearchGraphEditDistance GED = new BeamSearchGraphEditDistance(g1,g2,10,Constants.costFunction,Constants.edgeHandler,false);
	//		SET.CpuDisplay(noOFNodes);
			//	System.out.println("i="+(i+1)+"   "+GED.getBestEditpath().getTotalCosts());
			//SET.StopChrono();
			//SET.WriteElapsedTime();
			
			System.out.println("The best solution is=");
			GED.getBestEditpath().printMe();

			System.out.print("The total cost of "+noOfNodesG12+" nodes is :");
			System.out.println(GED.getBestEditpath().getTotalCosts());

			
			COST.GetPrintStream().print(noOfNodesG12);
			COST.GetPrintStream().println (";"+GED.getBestEditpath().getTotalCosts());
			
			SIZE.GetPrintStream().print(noOFNodes);
			SIZE.GetPrintStream().print(";"+GED.UnProcessedNoEditPaths);
			SIZE.GetPrintStream().println(";"+GED.editPathCounter);
			
			System.out.println("OPEN max size = "+GED.UnProcessedNoEditPaths);
			System.out.println("No of edit paths = "+GED.editPathCounter);
			
			
			//-----------------------------------------------------
			
		
		}
		COST.CloseStream();
		//SET.CloseStream();
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
			BeamSearchGraphEditDistance GED = new BeamSearchGraphEditDistance(g1,g2,5,Constants.costFunction,Constants.edgeHandler,true);
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
		
		File repertoire = new File("./data/test");
		File[] files=repertoire.listFiles(gxlFileFilter);
		Graph[] graphTab = new Graph[files.length];
		
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
			FileWriter fw = new FileWriter("./data/testjavaxy.csv", true);
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
					GraphEditDistance GED = new GraphEditDistance(g1,g2,Constants.costFunction,Constants.edgeHandler,false);
					SET.StopChrono();
					output.write(";"+GED.getBestEditpath().getTotalCosts()+";"+SET.ElapsedTimeToString());
					System.out.println(i + " " + j );
					output.write("\n");
				}
			}
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getNbExploredNode() {
		// TODO Auto-generated method stub
		return nbexlporednode;
	}

	public int getMaxSizeOpen() {
		// TODO Auto-generated method stub
		return this.maxopensize;
	}
	
	
}

