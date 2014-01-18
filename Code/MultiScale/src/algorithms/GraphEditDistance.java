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

import util.CpuMonitorWindows;
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

public class GraphEditDistance {

	// classe EditPath:
	// EditPath(G1,G2).
	// double cpuUserAvant;
	// double cpuKernelAvant;
	// double wctAvant;
	ArrayList<EditPath> OPEN; // Tiens à jour la liste des noeud non encore
								// utilisés. Le score du chemin.
	private Graph G1; // Graph 1
	private Graph G2; // Graph 2
	private Node CurNode; // Current node
	private int G2NbNodes; // Number of nodes of G2
	public int openCounterSize; // a variable that tracks the maximum size of
								// the set OPEN
	public int editPathCounter; // a variable that tracks number of edit paths

	// debug info
	// debug variable
	int nbexlporednode = 0;
	int maxopensize = 0;

	private EditPath BestEditpath; // Zeina: The best answer
	boolean debug;

	// A function that returns the best edit path "i.e. the optimal solution"
	public EditPath getBestEditpath() {
		return BestEditpath;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	public GraphEditDistance(Graph G1, Graph G2, ICostFunction costfunction,
			IEdgeHandler edgehandler, boolean debug) {

		this.debug = debug;
		this.G1 = G1;
		this.G2 = G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath = null; // In the beginning, the list is empty.

		// La liste OPEN est initialisée à VIDE. OPEN est une liste de chemin
		// Zeina: The set open contains the search tree nodes to be processed in
		// the next steps
		OPEN = new ArrayList<EditPath>();

		// avant la boucle
		inti();

		BestEditpath = loop();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	private EditPath loop() {

		while (true == true) {

			if (OPEN.isEmpty() == true) {
				System.out
						.println("Error : No more candidates, no complete solution could be found");
				System.out.println("Error : Please check your graph data");
				return null;
			}

			// On cherche dans OPEN le chemin avec le cout minimum (Pmin) et on
			// l'enléve de OPEN
			EditPath pmin = findmincostEditPath(OPEN);
			OPEN.remove(pmin);
			if (debug == true) {
				System.out
						.println("----------------------------------------------------------");
				System.out.println("Current Path=");
				pmin.printMe();
			}

			if (pmin.isComplete() == true) {
				return pmin;
			} else {
				// K est l'index du noeud de G1 en cours de traintement. Premier
				// courp, k=1.
				// Si on a traité k < taille de G1

				if (pmin.getUnUsedNodes1().size() > 0) {
					;
					this.CurNode = pmin.getNext();
					if (debug == true)
						System.out.println("Current Node="
								+ this.CurNode.getId());

					// pour tous les noeuds (w) de V2 qui ne sont pas encore
					// utilisés dans Pmin. on ajoute dans pmin toutes les
					// substitutions de uk+1 avec w.
					LinkedList<Node> UnUsedNodes2 = pmin.getUnUsedNodes2();
					for (int i = 0; i < UnUsedNodes2.size(); i++) {
						EditPath newpath = new EditPath(pmin);

						Node w = UnUsedNodes2.get(i);
						newpath.addDistortion(this.CurNode, w);

						if (debug == true) {
							System.out
									.println("Substitution CurNode and ith node of G2= "
											+ this.CurNode.getId()
											+ " -- "
											+ w.getId());
							newpath.printMe();
						}

						this.OPEN.add(newpath);
						if (debug == true)
							TrackOpenEditPathSize();

						if (debug == true)
							System.out
									.println("----------------------------------------------------------");
					}
					// On met dans pmin une suppresion de uk+1.
					EditPath newpath = new EditPath(pmin);
					newpath.addDistortion(this.CurNode, Constants.EPS_COMPONENT);

					if (debug == true) {
						System.out.println("Deletion CurNode="
								+ this.CurNode.getId());
						newpath.printMe();
						System.out
								.println("----------------------------------------------------------");
					}

					this.OPEN.add(newpath);
					if (debug == true)
						TrackOpenEditPathSize();

				} else {
					// Sinon si k= taille de G1
					// On met dans pmin toutes les insertions des noeuds de V2
					// qui ne sont pas encore utilisés dans Pmin.
					// On remet Pmin dans OPEN
					EditPath newpath = new EditPath(pmin);
					newpath.complete();
					this.OPEN.add(newpath);
					if (debug == true)
						TrackOpenEditPathSize();
					if (debug == true) {
						System.out
								.println("We complete the path by inserting all remaining nodes of G2 CurNode="
										+ this.CurNode.getId());
						newpath.printMe();
					}

				}// fin si k<(this.G1NbNodes-1)
			}// fin si chemin optimal

		}// boucle while
	}

	// A function that returns the minimal edit path's cost in the set OPEN
	private EditPath findmincostEditPath(ArrayList<EditPath> oPEN2) {
		// TODO Auto-generated method stub
		int i = 0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		int indexmin = -1;

		for (i = 0; i < nbpaths; i++) {
			EditPath p = OPEN.get(i);
			if (p.getTotalCosts() < minvalue) {
				minvalue = p.getTotalCosts();
				indexmin = i;
			}
		}

		this.nbexlporednode++;
		maxopensize = Math.max(this.OPEN.size(), this.maxopensize);
		return OPEN.get(indexmin);
	}

	// the initialization function
	private void inti() {
		// TODO Auto-generated method stub
		for (int i = 0; i < G2NbNodes; i++) {
			// Dans la liste OPEN on met toutes les substitions de u1 avec tous
			// les noeuds de g2.
			// index du noeud courant

			EditPath p = new EditPath(G1, G2); // An intialization for both G1
												// and G2
												// "to show that we did not use neither edges nor nodes of both G1 and G2"
			Node v = (Node) G2.get(i);
			this.CurNode = p.getNext();
			/*
			 * Add distortion between the current node u1 and each node in G2
			 * distortion means insertion or substitution but not deletion as we
			 * are sure that G2 has nodes as we are inside the for loop of G2
			 */
			p.addDistortion(this.CurNode, v);
			if (debug == true)
				System.out
						.println("Substitution CurNode G1 and ith node of G2= ("
								+ this.CurNode.getId()
								+ "  "
								+ v.getId()
								+ ")   Cost =" + p.getTotalCosts());
			OPEN.add(p);
			if (debug == true)
				TrackOpenEditPathSize();
		}

		// On met dans OPEN une suppresion de u1. Dans le cas ou supprimer u1
		// serait une possibilité optimale.
		EditPath p = new EditPath(G1, G2);
		this.CurNode = p.getNext();
		p.addDistortion(this.CurNode, Constants.EPS_COMPONENT); // Zeina:
																// deletion
		if (debug == true)
			System.out.println("Deletion CurNode=" + this.CurNode.getId()
					+ " -- Cost = " + p.getTotalCosts());
		OPEN.add(p);
		if (debug == true)
			TrackOpenEditPathSize();
	}

	// A function that tracks the no of unexamined "added" edit Paths and the
	// maximum size of the set OPEN
	private void TrackOpenEditPathSize() {
		// TODO Auto-generated method stub
		editPathCounter++;
		if (openCounterSize < OPEN.size()) {
			openCounterSize = OPEN.size();
			if (debug == true) {
				System.out.println("OPEN max size = " + openCounterSize);
				System.out.println("No of edit paths = " + editPathCounter);

			}
		}
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Before executing the Graph Edit Distance function");
		// t1(); // gxl graphs
		// t2();
		t3(); // linear graphs
		System.out.println("After executing the Graph Edit Distance function ");

	}

	private static void t3() throws IOException {
		double alpha = 1;
		double edgeCosts = 1;
		double nodeCosts = 1;

		/** PARSING */
		int nbGraphs = 0;
		File repertoire = new File("./data/LETTERHIGH");
		if (!repertoire.exists()) {
			System.out.println("Le répertoire n'existe pas");
		}
		File[] files = repertoire.listFiles(gxlFileFilter);
		Graph[] graphTab = new Graph[files.length];

		XMLParser xmlParser = new XMLParser();
		try {
			for (int i = 0; i < files.length; i++) {
				graphTab[i] = xmlParser.parseGXL(files[i].toString());
				nbGraphs++;
				System.out.println(files[i].toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Constants.costFunction = (ICostFunction) new UnlabeledCostFunction(
				nodeCosts, edgeCosts, alpha);
		Constants.edgeHandler = new UniversalEdgeHandler();

		GraphEditDistance GED;

		Graph G1, G2;
		SpeedEvalTest SET = new SpeedEvalTest("tmp.csv");
		FileWriter fw = new FileWriter("GraphEditDistance" + ".csv", false);
		BufferedWriter output = new BufferedWriter(fw);
		output.write("Graph1;Graph2;Cout;Temps(ms)\n");
		for (int i = 0; i < nbGraphs; i++) {
			G1 = graphTab[i];
			for (int j = i; j < nbGraphs; j++) {
				G2 = graphTab[j];
				System.out.println(i + " " + j + " on " + nbGraphs);

				if (i*j == 49621) {
					System.out.println("FINISHED!");
					System.in.read();
				}
				output.write(G1.getId() + ";" + G2.getId());
				SET.StartChrono();
				GED = new GraphEditDistance(G1, G2, Constants.costFunction,
						Constants.edgeHandler, false);
				output.write(";" + GED.getBestEditpath().getTotalCosts());

				SET.StopChrono();
				output.write(";" + SET.ElapsedTimeToString());
				output.write("\n");
				output.flush();
			}
		}
		output.close();

	}

	private static void t1() {
		// TODO Auto-generated method stub
		double alpha = 0.5;
		double edgeCosts = 2;
		double nodeCosts = 2;
		String sg1;
		String sg2;
		SpeedEvalTest SET = new SpeedEvalTest("Dijkstra 5-7.txt");

		sg1 = "./data/example/15.gxl";
		sg2 = "./data/example/30.gxl";
		// GraphCollection source, target;
		// TODO Auto-generated method stub

		// set the appropriate cost function
		// Constants.costFunction = (ICostFunction)new
		// UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1
		// Constants.costFunction = (ICostFunction)new
		// MoleculesCostFunction(nodeCosts, edgeCosts);
		// Constants.costFunction = (ICostFunction)new
		// UnlabeledCostFunction(nodeCosts, edgeCosts, alpha);
		Constants.costFunction = (ICostFunction) new UniversalCostFunction(0.5);

		// set the appropriate edgehandler (directed or undirected)
		Constants.edgeHandler = new UniversalEdgeHandler();// UnDirectedEdgeHandler();

		// source = new GraphCollection();
		// source.setCollectionName(args[0]);

		// target = new GraphCollection();
		// target.setCollectionName(args[1]);

		XMLParser xmlParser = new XMLParser();
		Graph g1 = null;
		Graph g2 = null;
		try {
			g1 = xmlParser.parseGXL(sg1);
			g2 = xmlParser.parseGXL(sg2);
			SET.StartChrono();
			GraphEditDistance GED = new GraphEditDistance(g1, g2,
					Constants.costFunction, Constants.edgeHandler, false);
			SET.StopChrono();
			SET.WriteElapsedTime();
			System.out.println("****************The best solution is=");
			GED.getBestEditpath().printMe();
			System.out.println("COST :::: "
					+ GED.getBestEditpath().getTotalCosts());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// source.add(g2);
		// target.add(g1);
		SET.CloseStream();
	}

	//

	// ///////////////////////////////////////////////////////////////////////////////

	// Romain's Codes For time monitoring

	// Zeina has commented this code
	/*
	 * private static void t1() { // TODO Auto-generated method stub double
	 * alpha=0.5; double edgeCosts=0.5; double nodeCosts=0.5; String sg1; String
	 * sg2; //sg1 = args[0]; //sg2 = args[1]; //sg1 = "./data/g1test.gxl"; //sg2
	 * = "./data/g2test.gxl"; //sg1 = "./data/example/5.gxl"; // sg2 =
	 * "./data/example/7.gxl"; //GraphCollection source, target; // TODO
	 * Auto-generated method stub // set the appropriate cost function
	 * //Constants.costFunction = (ICostFunction)new
	 * UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1
	 * //Constants.costFunction = (ICostFunction)new
	 * MoleculesCostFunction(nodeCosts, edgeCosts); Constants.costFunction =
	 * (ICostFunction)new UniversalCostFunction(0.5);
	 * 
	 * // set the appropriate edgehandler (directed or undirected)
	 * Constants.edgeHandler = new
	 * UniversalEdgeHandler();//UnDirectedEdgeHandler();
	 * 
	 * // source = new GraphCollection(); // source.setCollectionName(args[0]);
	 * 
	 * 
	 * //target = new GraphCollection(); //target.setCollectionName(args[1]);
	 * 
	 * XMLParser xmlParser = new XMLParser(); Graph g1 = null; Graph g2 = null;
	 * try { g1 = xmlParser.parseGXL(sg1); g2= xmlParser.parseGXL(sg2);
	 * GraphEditDistance GED = new
	 * GraphEditDistance(g1,g2,Constants.costFunction
	 * ,Constants.edgeHandler,false); GED.getBestEditpath().printMe(); } catch
	 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * //source.add(g2); //target.add(g1); } private static void t2() throws
	 * FileNotFoundException { // TODO Auto-generated method stub SpeedEvalTest
	 * SET = new SpeedEvalTest("gedspeedheuristic" +10 + ".txt");
	 * Constants.costFunction = (ICostFunction)new UniversalCostFunction(0.5);
	 * // set the appropriate edgehandler (directed or undirected)
	 * Constants.edgeHandler = new UniversalEdgeHandler();
	 * 
	 * Graph g1 = SET.SyntheticLinearGraph(10);
	 * 
	 * 
	 * for(int i=0;i<10;i++){ Graph g2 = SET.SyntheticLinearGraph(i+1);
	 * SET.StartChrono(); GraphEditDistance GED = new
	 * GraphEditDistance(g1,g2,Constants
	 * .costFunction,Constants.edgeHandler,false);
	 * System.out.println("i="+(i+1)+
	 * "   "+GED.getBestEditpath().getTotalCosts()); SET.StopChrono();
	 * SET.WriteElapsedTime();
	 * 
	 * } SET.CloseStream(); }
	 */

	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};

	private static void t4() {
		Constants.costFunction = (ICostFunction) new LetterCostFunction(1, 1, 1);
		Constants.edgeHandler = new UnDirectedEdgeHandler();

		File repertoire = new File("./data/test");
		if (!repertoire.exists()) {
			System.out.println("Le répertoire n'existe pas");
		}

		File[] files = repertoire.listFiles(gxlFileFilter);
		Graph[] graphTab = new Graph[files.length];

		XMLParser xmlParser = new XMLParser();
		try {
			for (int i = 0; i < files.length; i++) {
				graphTab[i] = xmlParser.parseGXL(files[i].toString());
				System.out.println(files[i].toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			SpeedEvalTest SET = new SpeedEvalTest("tmp.csv");
			FileWriter fw = new FileWriter(GraphEditDistance.class.getName()
					+ ".csv", false);
			BufferedWriter output = new BufferedWriter(fw);
			output.write("Graph1;Graph2;Cout;Temps\n");
			Graph g2, g1;
			g2 = g1 = null;
			SET.StartChrono();
			for (int i = 0; i < graphTab.length; i++) {
				g1 = graphTab[i];

				for (int j = 0; j < graphTab.length; j++) {
					g2 = graphTab[j];

					output.write(g1.getId() + ";" + g2.getId());
					// SET.StartChrono();
					GraphEditDistance GED = new GraphEditDistance(g1, g2,
							Constants.costFunction, Constants.edgeHandler,
							false);
					// SET.StopChrono();
					output.write(";" + GED.getBestEditpath().getTotalCosts()
							+ ";NULL");
					System.out.println(i + " " + j);
					output.write("\n");
				}
			}
			SET.StopChrono();
			output.write(";" + "NULL;" + SET.ElapsedTimeToString());
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
