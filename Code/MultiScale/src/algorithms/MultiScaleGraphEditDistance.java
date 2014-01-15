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
 * @author Thomas Noguer This class contains all the tools to perform a multi
 *         scaled graph edit distance.
 */
public class MultiScaleGraphEditDistance {

	private Graph G1;
	private Graph G2;
	boolean debug;
	private EditPath bestEditPath;

	/**
	 * 
	 * @param G1
	 *            The first graph to compare.
	 * @param G2
	 *            The second graph to compare.
	 * @param key
	 *            The name of the value to be used in the edges.
	 * @param edgehandler
	 *            The edge handler.
	 * @param debug
	 */
	public MultiScaleGraphEditDistance(Graph G1, Graph G2, String key,
			IEdgeHandler edgehandler, boolean debug) {

		this.debug = debug;
		this.G1 = G1;
		this.G2 = G2;

		// Louvain on both G1 and G2
		Louvain L1 = new Louvain(G1, key);
		double Q;
		do {
			Q = L1.getQ();
			L1.findCommunities();
		} while (Q != L1.getQ());
		Louvain L2 = new Louvain(G2, key);

		// We find all the communities.
		do {
			Q = L2.getQ();
			L2.findCommunities();
		} while (Q != L2.getQ());

		// We create the cost function with the original graphs.
		Constants.costFunction = new CommunityCostFunction(1, 1, key, G1, G2,
				edgehandler);

		// We put the last scaled graphs for the GED
		G1 = G1.getGraphFromScale(G1.getScaleMax(), key);
		G2 = G2.getGraphFromScale(G2.getScaleMax(), key);

		// GED
		GraphEditDistance GED = new GraphEditDistance(G1, G2,
				Constants.costFunction, edgehandler, debug);
		bestEditPath = GED.getBestEditpath();
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		System.out
				.println("Before executing the Multi Scale Graph Edit Distance function");

		/** PARSING */
		int nbGraphs = 0;
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
				nbGraphs++;
				System.out.println(files[i].toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		Constants.edgeHandler = new UnDirectedEdgeHandler();
		/** EXECUTION */
		 Graph G1, G2;
		 G1 = graphTab[0];
		 G2 = graphTab[1];
		 
//		 Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(1, 1, 1);
//		 GraphEditDistance GED = new GraphEditDistance(G1, G2, Constants.costFunction, Constants.edgeHandler, false);
//		 System.out.println(GED.getBestEditpath().getTotalCosts());
		
		 MultiScaleGraphEditDistance MultiScaleGED = new MultiScaleGraphEditDistance(G1, G2, "valence", Constants.edgeHandler, false);
		 System.out.println(MultiScaleGED.getBestEditpath().getTotalCosts());

		/** TESTS */
		// Test Louvain
//		Graph G, H;
//		G = graphTab[0];
//		Louvain louvain = new Louvain(G, "valence");
//		
//		double Q;
//		do {
//			Q = louvain.getQ();
//			louvain.findCommunities();
//		} while(Q != louvain.getQ());
//		
//		H = G.getGraphFromScale(G.getScaleMax(), "valence");
//		for (Object object : H) {
//			Node n = (Node) object;
//			System.out.println(n.getComponentId() + " " + n.isCommunity());
//		}
		
	}

	private EditPath getBestEditpath() {
		return bestEditPath;
	}

	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};
}
