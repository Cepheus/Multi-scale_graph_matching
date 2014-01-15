package util;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * @author kriesen Graph is a list with nodes and there is a list with all edges
 *         of this graph
 */
public class Graph extends LinkedList {
	/** the class of this graph */
	private String classId;
	/** the identifier of the graph */
	private String id;
	public int getScaleMax() {
		return scaleMax;
	}

	public void setScaleMax(int scaleMax) {
		this.scaleMax = scaleMax;
	}

	/** labeled edges: true/false */
	private String edgeId;
	/** modes: directed, undirected */
	private String edgeMode;
	/** the edges of the graph */
	private LinkedList edges;
	/** The scale of the graph. */
	private int scale = 0;
	/** The maximum scale of the graph. */
	private int scaleMax = 0;

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	/**
	 * the constructor
	 */
	public Graph() {
		super();
		this.edges = new LinkedList();
	}

	public Object clone() {
		// Deep copy
		Graph H = new Graph();
		H.classId = classId;
		H.id = id;
		H.edgeId = edgeId;
		H.edgeMode = edgeMode;
		H.scale = scale;
		H.scaleMax = scaleMax;
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		for (Object o : edges) {
			Edge e = (Edge) ((Edge) o).clone();
			String key = e.getStartNode().getComponentId();
			if(nodes.containsKey(key))
				e.setStartNode(nodes.get(key));
			else
				nodes.put(key, e.getStartNode());
			key = e.getEndNode().getComponentId();
			if(nodes.containsKey(key))
				e.setEndNode(nodes.get(key));
			else
				nodes.put(key, e.getEndNode());
			H.edges.add(e);
		}
		for (Node node : nodes.values()) {
			H.add(node);
		}
		return H;
	}

	/**
	 * some getters and setters
	 */
	public String getEdgeId() {
		return edgeId;
	}

	public String getEdgeMode() {
		return edgeMode;
	}

	public String getId() {
		return id;
	}

	public LinkedList getEdges() {
		return edges;
	}

	public void setEdges(LinkedList edges) {
		this.edges = edges;
	}

	public void setEdgeId(String edgeids) {
		this.edgeId = edgeids;
	}

	public void setEdgeMode(String edgemode) {
		this.edgeMode = edgemode;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String toString() {
		String nodes = "Nodes\n";
		Iterator iter = this.iterator();
		while (iter.hasNext()) {
			Node n = (Node) iter.next();
			nodes += n.getComponentId() + " ";
			nodes += n.getTable().get("chem") + " ; ";
		}
		nodes += "\n";
		nodes += "Edges\n";
		Iterator edgeIter = this.edges.iterator();
		while (edgeIter.hasNext()) {
			Edge e = (Edge) edgeIter.next();
			nodes += e.getTable().get("from") + "<-"
					+ e.getTable().get("valence") + "->";
			nodes += e.getTable().get("to") + " ";
			nodes += "\n";
		}
		return nodes;
	}
	
	/**
	 * Transform the original graph into the new graph formed by the
	 * communities.
	 * 
	 * @param scale
	 *            The scale which communities we take into account.
	 * @param key
	 *            The key of value to be used on the edges. 				  
	 * @return The newly formed graph.
	 */
	public Graph getGraphFromScale(int scale, String key) {
		Graph H = (Graph)this.clone();
		/* We construct the new graph formed by the communities. */
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
							i.isCommunity(scale);
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
		H.scale = scale;
		return H;
	}

	/**
	 * Form the subgraph formed by a community.
	 * 
	 * @param community
	 *            The community from which we get the subgraph.
	 * @param scale
	 *            The scale we are working on.
	 * @return The newly formed graph.
	 */
	public Graph getGraphFromCommunity(String community, int scale) {

		Graph T = (Graph) this.clone();
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

		T.scale = 0;
		return T;
	}
}
