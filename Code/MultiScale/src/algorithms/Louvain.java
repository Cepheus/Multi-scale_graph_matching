package algorithms;

import java.util.LinkedHashSet;

import util.Edge;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.Node;
import util.UnDirectedEdgeHandler;
import xml.XMLParser;
import xml.XMLWriter;

/**
 * This class contains the tools to use the community detection algorithm: Louvain's method.
 * Note: The method only deals with undirected graphs, and ignores weights on the nodes.
 * @author Thomas Noguer
 */
public class Louvain {
	
	private Graph G;
	/** The key of the attribute of the edges */
	private String key;
	/** Modularity */
	private double Q;
	/** Sum of the weights of all the links in the graph */
	private double m;
	/** The list of the nodes of the graph */
	private LinkedHashSet<Node> nodes;
	private int communityNumber;
	private String communityPrefix = "c";
	
	/**
	 * Default constructor.
	 * @param G1 To graph from which we want to find the communities
	 * @param key The name of the value of the edges we will consider
	 */
	public Louvain(Graph G, String key){
		Constants.edgeHandler = new UnDirectedEdgeHandler();
		nodes = new LinkedHashSet<Node>();
		communityNumber = 0;
		m = 0;
		this.key = key;
		/* Construction of the list of nodes */
		for (Object o : G.getEdges()) {
			Edge e = (Edge) o;
			nodes.add(e.getStartNode());
			nodes.add(e.getEndNode());
			/* We compute m the sum of all the weights of the edges of the graph */
			m += Double.valueOf(e.getValue(key).toString());
		}
		/* Setting the initial communities of the nodes */
		for (Node node : nodes) {
			node.addCommunity(communityPrefix+communityNumber);
			communityNumber++;
		}	
		/* Initial modularity */
		Q = 1/(2*m);
	}

}
