package algorithms;

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
	
	/**
	 * Default constructor.
	 * @param G1 To graph from which we want to find the communities
	 */
	public Louvain(Graph G){
		Constants.edgeHandler = new UnDirectedEdgeHandler();
		
	}

}
