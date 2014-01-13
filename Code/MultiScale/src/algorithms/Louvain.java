package algorithms;

import java.util.ArrayList;
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
 * This class contains the tools to use the community detection algorithm:
 * Louvain's method. Note: The method only deals with undirected graphs, and
 * ignores weights on the nodes.
 * 
 * @author Thomas Noguer
 */
public class Louvain {

	/** The original graph */
	private Graph G;
	/** The actual graph, modified at each call of findCommunities */
	private Graph H;
	/** The key of the attribute of the edges */
	private String key;
	/** The scale of graph H */
	private int scale;
	/** Modularity */
	private double Q;
	/** Sum of the weights of all the links in the graph */
	private double m;
	/** The list of the nodes of the graph */
	private LinkedHashSet<Node> nodes;
	/** The list of the nodes of the graph H*/
	private LinkedHashSet<Node> nodesH;

	public String getKey() {
		return key;
	}

	public int getScale() {
		return scale;
	}

	public double getQ() {
		return Q;
	}

	public LinkedHashSet<Node> getNodes() {
		return nodes;
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
		nodes = new LinkedHashSet<Node>();
		nodesH = new LinkedHashSet<Node>();
		communityNumber = 0;
		m = 0;
		scale = 0;
		this.key = key;
		/* Construction of the list of nodes */
		for (Object o : G.getEdges()) {
			Edge e = (Edge) o;
			nodes.add(e.getStartNode());
			nodes.add(e.getEndNode());
			/* We compute m the sum of all the weights of the edges of the graph */
			m += Double.valueOf(e.getValue(key).toString());
		}
		
		System.out.println("NodesG");
		/* Setting the initial communities of the nodes */
		for (Node node : nodes) {
			node.addCommunity(communityPrefix + communityNumber);
			communityNumber++;
			System.out.println(node.getComponentId());
		}
		/* Initial modularity */
		Q = 1 / (2 * m);

		this.G = G;
		H = (Graph)G.clone();
		for (Object o : H.getEdges()) {
			Edge e = (Edge) o;
			System.out.println(e.getComponentId());
			System.out.println(e.getStartNode().getComponentId()+" "+e.getEndNode().getComponentId());
			//System.out.println(e.getStartNode().getCommunity(scale)+" "+e.getEndNode().getCommunity(scale));
			nodesH.add(e.getStartNode());
			nodesH.add(e.getEndNode());
		}
		System.out.println("Nodes");
		for (Node node : nodesH) {
			System.out.println(node.getComponentId());
		}
	}

	/**
	 * Find the communities of the actual graph H and adds them into the nodes
	 * of G
	 * 
	 * @return The original graph with the communities stored inside the nodes
	 */
	public Graph findCommunities() {

		// We copy the community of scale to scale+1
		for (Node i : nodes) {
			i.addCommunity(scale + 1, i.getCommunity(scale));
		}
		scale++;

		String old_community;
		String current_community;
		String best_community = "";
		double dQ_max = 0;
		double dQ = 0;
		// double old_Q;
		// do {
		// old_Q = Q;
		for (Node i : nodes) {
			dQ_max = 0;
			for (Node j : nodes) {
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
			}
		}
		computeModularity();
		// } while (old_Q != Q);

		/* We construct the new graph formed by the communities. */
		LinkedHashSet<Node> nodesH = new LinkedHashSet<Node>();
		for (Object o : H.getEdges()) {
			Edge e = (Edge) o;
			nodesH.add(e.getStartNode());
			nodesH.add(e.getEndNode());
		}
		boolean[] to_remove = new boolean[nodesH.size()];
		for (int i = 0; i < nodesH.size(); i++) {
			to_remove[i] = false;
		}
		boolean first;
		Edge community_edge = null;
		String ci, cj;
		int k, l;
		k = l = 0;
		// For each node
		for (Node i : nodesH) {
			// We search for another node which is in the same community.
			l = 0;
			for (Node j : nodesH) {
				if (!to_remove[l]) {
					ci = i.getCommunity(scale);
					cj = j.getCommunity(scale);
					first = true;
					if (i != j && ci == cj) {
						i.setId(ci);
						// If we found one we transforme the edges to those
						// nodes
						for (Object o : H.getEdges()) {
							Edge e = (Edge) o;
							// If the edge goes to node j
							if (e.getStartNode() == j
									&& e.getEndNode().getCommunity(scale) != ci
									|| e.getEndNode() == j
									&& e.getStartNode().getCommunity(scale) != ci) {
								// We change the first edge we find and make it
								// go
								// to the new community node
								if (first) {
									community_edge = e;
									if (e.getStartNode() == j)
										e.setStartNode(i);
									else if (e.getEndNode() == j)
										e.setEndNode(i);
									first = false;
								}
								// We remove the others and add their value to
								// the
								// first edge we kept
								else {
									Double value = Double
											.parseDouble((String) community_edge
													.getValue(key));
									value += Double.parseDouble((String) e
											.getValue(key));
									community_edge.put(key, value.toString());
									H.remove(o);
								}
							}
							// If the node goes to i
							else if (e.getStartNode() == i
									&& e.getEndNode().getCommunity(scale) != ci
									|| e.getEndNode() == i
									&& e.getStartNode().getCommunity(scale) != ci) {
								// We change the first edge we find and make it
								// go
								// to the new community node
								if (first) {
									community_edge = e;
									if (e.getStartNode() == i)
										e.setStartNode(i);
									else if (e.getEndNode() == i)
										e.setEndNode(i);
									e.setComponentId(e.getStartNode().getId()
											+ "<>" + e.getEndNode().getId());
									first = false;
								}
								// We remove the others and add their value to
								// the
								// first edge we kept
								else {
									Double value = Double
											.parseDouble((String) community_edge
													.getValue(key));
									value += Double.parseDouble((String) e
											.getValue(key));
									community_edge.put(key, value.toString());
									H.remove(o);
								}
							}
							// If the node is inside the community we remove it
							else if (e.getStartNode() == i
									&& e.getEndNode() == j
									|| e.getStartNode() == j
									&& e.getEndNode() == i) {
								H.remove(o);
							}
						}
						// Finally we remove the node j
						to_remove[l] = true;
					}

				}
				l++;
			}
			k++;
		}
		k = 0;
		for (Object o : nodesH) {
			if (to_remove[k])
				nodesH.remove(o);
		}
		return H;
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
			if (start == C && start == end)
				sumIn += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the links incident to the community.
			if (start == C && end != C || start != C && end == C)
				sumTot += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the links from i to nodes in C.
			if (start == C && e.getEndNode() == i || e.getStartNode() == i
					&& end == C)
				kiin += Double.parseDouble((String) e.getValue(key));
			// Sum of the weights of the edges attached to vertex i.
			if (e.getStartNode() == i || e.getEndNode() == i)
				ki += Double.parseDouble((String) e.getValue(key));
		}

		double dQ = 0;
		double m2 = 2 * m;
		// (SumIn + kiin)/2m
		dQ = (sumIn + kiin) / m2;
		// - ((sumTot + ki)/2m)�
		dQ = dQ - Math.pow((sumTot + ki) / m2, 2);
		// - [sumIn/2m - (sumTot/2m)� - (ki/2m)�]
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
		for (Node i : nodes) {
			for (Node j : nodes) {
				ci = i.getCommunity(scale);
				cj = j.getCommunity(scale);
				if (ci == cj && i != j) {
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
}
