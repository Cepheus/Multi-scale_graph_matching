package algorithms;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import util.Edge;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.Node;
import util.UnDirectedEdgeHandler;
import xml.XMLParser;
import xml.XMLWriter;

/**
 * This class contains the tools to use the community detection algorithm:
 * Louvain's method. Note: The method only deals with undirected graphs, and
 * ignores weights on the nodes.
 * 
 * @author Thomas Noguer
 */
public class Louvain {

	/** The original graph */
	private Graph G;
	/** The key of the attribute of the edges */
	private String key;
	/** The scale of graph H */
	private int scale;
	/** Modularity */
	private double Q;
	/** Sum of the weights of all the links in the graph */
	private double m;

	public String getKey() {
		return key;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public double getQ() {
		return Q;
	}

	public String getCommunityPrefix() {
		return communityPrefix;
	}

	private int communityNumber;
	private String communityPrefix = "c";

	/**
	 * Default constructor.
	 * 
	 * @param G1
	 *            To graph from which we want to find the communities
	 * @param key
	 *            The name of the value of the edges we will consider
	 */
	public Louvain(Graph G, String key) {
		Constants.edgeHandler = new UnDirectedEdgeHandler();
		communityNumber = 0;
		m = 0;
		scale = 0;
		this.key = key;

		/* We compute m the sum of all the weights of the edges of the graph */
		for (Object o : G.getEdges()) {
			Edge e = (Edge) o;
			m += Double.valueOf(e.getValue(key).toString());
		}

		/* Setting the initial communities of the nodes */
		for (Object o : G) {
			Node node = (Node) o;
			node.addCommunity(communityPrefix + communityNumber);
			communityNumber++;
		}
		/* Initial modularity */
		Q = 1 / (2 * m);

		this.G = G;
	}

	/**
	 * Find the communities of the actual graph H and adds them into the nodes
	 * of G.
	 */
	public void findCommunities() {

		// We copy the community of scale to scale+1
		for (Object o : G) {
			Node i = (Node) o;
			i.addCommunity(scale + 1, i.getCommunity(scale));
		}
		scale++;
		G.setScaleMax(scale);

		String old_community;
		String current_community;
		String best_community = "";
		double dQ_max = 0;
		double dQ = 0;
		// double old_Q;
		// do {
		// old_Q = Q;
		for (Object oi : G) {
			Node i = (Node) oi;
			// Node iH = (Node) nodesH.toArray()[k];
			dQ_max = 0;
			for (Object oj : G) {
				Node j = (Node) oj;
				if (i != j && i.getCommunity(scale) != j.getCommunity(scale)) {
					// We put i into the community C of j
					old_community = i.getCommunity(scale);
					current_community = j.getCommunity(scale);
					i.editCommunity(scale, current_community);
					// We compute the modularity gain.
					dQ = computeModularityGain(i, current_community);
					// We keep the community with the highest gain.
					if (dQ > dQ_max) {
						dQ_max = dQ;
						best_community = current_community;
					}
					// We put i back into its old community.
					i.editCommunity(scale, old_community);
				}
			}
			// We put i into the best community.
			if (dQ_max > 0) {
				i.editCommunity(scale, best_community);
				// iH.editCommunity(scale, best_community);
			}
		}
		computeModularity();
		// } while (old_Q != Q);
	}

	/**
	 * Compute the modularity gain from putting i into C.
	 * 
	 * @param i
	 *            The node we are changing its community.
	 * @param C
	 *            The community we are putting i into.
	 * @return The modularity gain from putting i into C.
	 */
	private double computeModularityGain(Node i, String C) {

		double sumIn = 0;
		double sumTot = 0;
		double ki = 0;
		double kiin = 0;

		for (Object o : G.getEdges()) {
			Edge e = (Edge) o;
			String start = e.getStartNode().getCommunity(scale);
			String end = e.getEndNode().getCommunity(scale);
			// Sum of the weights of the links inside the community.
			if (start.equals(C) && start.equals(end))
				sumIn += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the links incident to the community.
			if (start.equals(C) && !end.equals(C) || !start.equals(C) && end.equals(C))
				sumTot += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the links from i to nodes in C.
			if (start.equals(C) && e.getEndNode() == i || e.getStartNode() == i
					&& end.equals(C))
				kiin += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the edges attached to vertex i.
			if (e.getStartNode() == i || e.getEndNode() == i)
				ki += Double.parseDouble((String) e.getValue(key));
		}

		double dQ = 0;
		double m2 = 2 * m;
		// (SumIn + kiin)/2m
		dQ = (sumIn + kiin) / m2;
		// - ((sumTot + ki)/2m)²
		dQ = dQ - Math.pow((sumTot + ki) / m2, 2);
		// - [sumIn/2m - (sumTot/2m)² - (ki/2m)²]
		dQ = dQ
				- (sumIn / m2 - Math.pow(sumTot / m2, 2) - Math.pow(ki / m2, 2));

		return dQ;
	}

	/**
	 * Calculate the modularity.
	 */
	private void computeModularity() {
		String ci, cj;
		double ki, kj, Aij;
		Q = 0;
		for (Object oi : G) {
			Node i = (Node) oi;
			for (Object oj : G) {
				Node j = (Node) oj;
				ci = i.getCommunity(scale);
				cj = j.getCommunity(scale);
				if (ci.compareTo(cj) == 0 && i != j) {
					Aij = Double.MIN_VALUE;
					ki = kj = 0;
					for (Object o : G.getEdges()) {
						Edge e = (Edge) o;
						if (e.getStartNode() == i || e.getEndNode() == i)
							ki += Double.parseDouble((String) e.getValue(key));
						if (e.getStartNode() == j || e.getEndNode() == j)
							kj += Double.parseDouble((String) e.getValue(key));
						if (e.getStartNode() == i && e.getEndNode() == j
								|| e.getStartNode() == j && e.getEndNode() == i)
							Aij = Double.parseDouble((String) e.getValue(key));
					}
					if (Aij != Double.MIN_VALUE)
						Q += Aij - (ki * kj) / (2 * m);
				}
			}
		}
		Q = Q / (2 * m);
	}

	/**
	 * Transform the original graph into the new graph formed by the
	 * communities.
	 * 
	 * @param scale
	 *            The scale which communities we take into account.
	 * @return The newly formed graph.
	 */
	/*public Graph getGraphFromScale(int scale) {
		// We construct the new graph formed by the communities.
		LinkedList<Node> to_remove = new LinkedList<Node>();
		LinkedList<Edge> to_remove_edge = new LinkedList<Edge>();
		LinkedList<String> community_done = new LinkedList<String>();
		LinkedList<Node> community_node = new LinkedList<Node>();
		boolean first;
		Edge community_edge = null;
		String ci, cj;
		// For each node
		for (Object oi : H) {
			first = true;
			Node i = (Node) oi;
			for (Object oj : H) {
				Node j = (Node) oj;
				if (!to_remove.contains(j)) {
					ci = i.getCommunity(scale);
					cj = j.getCommunity(scale);

					if (i != j && ci.compareTo(cj) == 0) {
						if (!community_done.contains(ci)) {
							i.setComponentId(ci);
							for (Object o : H.getEdges()) {
								Edge e = (Edge) o;
								Node start = e.getStartNode();
								Node end = e.getEndNode();
								if (!to_remove_edge.contains(e)) {
									// If the edge goes into the community
									if (start.getCommunity(scale).compareTo(ci) != 0
											&& end.getCommunity(scale).compareTo(ci) == 0
											|| start.getCommunity(scale).compareTo(ci) == 0
											&& end.getCommunity(scale).compareTo(ci) != 0) {
										// We change the first edge we find and
										// make
										// it
										// go
										// to the new community node
										if (first) {
											community_edge = e;
											if (e.getStartNode()
													.getCommunity(scale)
													.compareTo(ci) == 0)
												e.setStartNode(i);
											else if (e.getEndNode()
													.getCommunity(scale)
													.compareTo(ci) == 0)
												e.setEndNode(i);
											e.setComponentId(e.getStartNode()
													.getComponentId()
													+ "_<>_"
													+ e.getEndNode()
															.getComponentId());
											first = false;
										}
										// We remove the others and add their
										// value
										// to
										// the
										// first edge we kept
										else {
											Double value = Double
													.parseDouble((String) community_edge
															.getValue(key));
											value += Double
													.parseDouble((String) e
															.getValue(key));
											community_edge.put(key,
													value.toString());
											to_remove_edge.add(e);
										}

									}
									// If the edge is inside the community we
									// remove
									// it
									if (e.getStartNode().getCommunity(scale)
											.compareTo(ci) == 0
											&& e.getEndNode()
													.getCommunity(scale)
													.compareTo(ci) == 0) {
										to_remove_edge.add(e);
									}
								}
							}
							community_done.add(ci);
							community_node.add(i);
							i.isCommunity(true);
						}
						// Finally we remove the node j
						if (!community_node.contains(j))
							to_remove.add(j);
					}
				}
			}
		}

		while (to_remove.size() > 0) {
			Node i = to_remove.removeFirst();
			H.remove(i);
		}
		while (to_remove_edge.size() > 0) {
			Edge e = to_remove_edge.removeFirst();
			H.getEdges().remove(e);
		}
		return H;
	}*/

	/**
	 * Form the subgraph formed by a community.
	 * 
	 * @param community
	 *            The community from which we get the subgraph.
	 * @param scale
	 *            The scale we are working on.
	 * @return The newly formed graph.
	 */
	/*public Graph getGraphFromCommunity(String community, int scale) {

		Graph T = (Graph) G.clone();
		LinkedList<Edge> edges_to_remove = new LinkedList<Edge>();
		LinkedList<Node> nodes_to_remove = new LinkedList<Node>();

		String ci, cj;
		Node i, j;
		Edge e;
		for (Object o : T.getEdges()) {
			e = (Edge) o;
			i = e.getStartNode();
			j = e.getEndNode();
			ci = i.getCommunity(scale);
			cj = j.getCommunity(scale);
			if (!(ci.compareTo(community) == 0 && cj.compareTo(community) == 0))
				edges_to_remove.add(e);
		}

		for (Object o : T) {
			Node node = (Node) o;
			if (node.getCommunity(scale).compareTo(community) != 0)
				nodes_to_remove.add(node);
		}

		while (edges_to_remove.size() > 0) {
			e = edges_to_remove.removeFirst();
			T.getEdges().remove(e);
		}

		while (nodes_to_remove.size() > 0) {
			Node n = nodes_to_remove.removeFirst();
			T.remove(n);
		}

		return T;
	}*/
}
