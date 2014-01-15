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

import util.CommunityCostFunction;
import util.CpuMonitorWindows;
import util.Edge;
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
 * @author Thomas Noguer
 * 
 */
public class MultiScaleGraphEditDistance {

	// classe EditPath:
	// EditPath(G1,G2).
	// double cpuUserAvant;
	// double cpuKernelAvant;
	// double wctAvant;
	ArrayList<EditPath> OPEN; // Tiens � jour la liste des noeud non encore
								// utilis�s. Le score du chemin.
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
	public MultiScaleGraphEditDistance(Graph G1, Graph G2,
			ICostFunction costfunction, IEdgeHandler edgehandler, boolean debug) {

		this.debug = debug;
		this.G1 = G1;
		this.G2 = G2;
		G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath = null; // In the beginning, the list is empty.

		// La liste OPEN est initialis�e � VIDE. OPEN est une liste de chemin
		// Zeina: The set open contains the search tree nodes to be processed in
		// the next steps
		OPEN = new ArrayList<EditPath>();

		// avant la boucle
		inti();

		BestEditpath = loop();
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	private EditPath loop() {

		while (true) {

			if (OPEN.isEmpty() == true) {
				System.out
						.println("Error : No more candidates, no complete solution could be found");
				System.out.println("Error : Please check your graph data");
				return null;
			}

			// On cherche dans OPEN le chemin avec le cout minimum (Pmin) et on
			// l'enl�ve de OPEN
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
				// Si on a trait� k < taille de G1

				if (pmin.getUnUsedNodes1().size() > 0) {
					;
					this.CurNode = pmin.getNext();
					if (debug == true)
						System.out.println("Current Node="
								+ this.CurNode.getId());

					// pour tous les noeuds (w) de V2 qui ne sont pas encore
					// utilis�s dans Pmin. on ajoute dans pmin toutes les
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
					// qui ne sont pas encore utilis�s dans Pmin.
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
		// serait une possibilit� optimale.
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
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		System.out
				.println("Before executing the Multi Scale Graph Edit Distance function");

		Constants.edgeHandler = new UnDirectedEdgeHandler();

		/** PARSING */
		int nbGraphs = 0;
		File repertoire = new File("./data/test");
		if (!repertoire.exists()) {
			System.out.println("Le r�pertoire n'existe pas");
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

		/** EXECUTION */
		Graph G1, G2;
		G1 = graphTab[0];
		G2 = graphTab[0];
		Louvain louvain = new Louvain(G1, "valence");
		Constants.costFunction = (ICostFunction) new CommunityCostFunction(1,
				1, "valence", louvain, G2, Constants.edgeHandler);
		double Q;
		do {
			louvain.findCommunities();
			Q = louvain.getQ();
		} while (Q != louvain.getQ());
		
		G1 = louvain.getGraphFromScale(louvain.getScale());
		
		System.out.println("G1");
		for (Object o : G1.getEdges()) {
			Edge e = (Edge) o;
			System.out.println(e.getComponentId());
		}
		
		System.out.println("G2");
		for (Object o : G2.getEdges()) {
			Edge e = (Edge) o;
			System.out.println(e.getComponentId());
		}
		
		
		MultiScaleGraphEditDistance MultiScaleGED = new MultiScaleGraphEditDistance(
				louvain.getGraphFromScale(louvain.getScale()), G2, Constants.costFunction, Constants.edgeHandler, false);
		System.out.println(MultiScaleGED.getBestEditpath().toString());

		/** TESTS */
		// Test Louvain
		/*
		 * Graph g1; g1 = graphTab[0]; Louvain louvain = new Louvain(g1,
		 * "valence"); louvain.findCommunities(); louvain.findCommunities();
		 * Graph H = louvain.getGraphFromScale(louvain.getScale()); for (Node i
		 * : louvain.getNodesH()) { System.out.println(i.getComponentId()); }
		 * for (Object o : H.getEdges()) { Edge e = (Edge) o;
		 * System.out.println(e.getComponentId()); }
		 * 
		 * // Test Get graph from community System.out.println("Get c4"); H =
		 * louvain.getGraphFromCommunity("c4", louvain.getScale()); for (Object
		 * o : H.getEdges()) { Edge e = (Edge) o;
		 * System.out.println(e.getComponentId()); } // for (Node node :
		 * louvain.getNodes()) { // System.out.println("Node "+node.getId() +
		 * " " + node.getCommunity(1)); // for (int i = 0; i <=
		 * louvain.getScale(); i++) { //
		 * System.out.println("Scale "+i+" "+node.getCommunity(i)); // } // }
		 */
		System.out
				.println("After executing the Multi Scale  Graph Edit Distance function ");
	}

	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};
}
