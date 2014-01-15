package util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import algorithms.Constants;
import algorithms.GraphEditDistance;
import algorithms.Louvain;

/**
 * This class gives to tools to handle the cost function of communities. The
 * cost of a community c is GED(c). The cost of a node is 1. The cost of an edge
 * is value(key) of the edge.
 * 
 * @author Thomas
 * 
 */
public class CommunityCostFunction implements ICostFunction {

	/**
	 * Constant cost.
	 */
	private double nodeCost;

	/**
	 * Constant cost.
	 */
	private double edgeCost;

	/**
	 * The key of the value to take into account for the edges.
	 */
	private String key;

	/**
	 * The class that is used to get the communities subgraphs.
	 */
	private Louvain louvain;

	/**
	 * The graph we will do GED of communities with.
	 */
	private Graph G2;

	/**
	 * The edge handler for the call of GED.
	 */
	private IEdgeHandler edgehandler;

	/**
	 * @param nodeCost
	 *            The constant cost of a node.
	 * @param edgeCost
	 *            The constant cost of an edge.
	 * @param key
	 *            The key of the value to take into account.
	 * @param louvain
	 *            The class that is used to get the communities subgraphs.
	 * @param G2
	 *            The graph we will do GED of communities with.
	 */
	public CommunityCostFunction(double nodeCost, double edgeCost, String key,
			Louvain louvain, Graph G2, IEdgeHandler edgehandler) {
		this.nodeCost = nodeCost;
		this.edgeCost = edgeCost;
		this.key = key;
		this.louvain = louvain;
		this.G2 = G2;
		this.edgehandler = edgehandler;
	}

	@Override
	public double getCosts(GraphComponent start, GraphComponent end) {

		// Node handling.
		if (start.isNode() || end.isNode()) {
			// Start is a community.
			if (start.isNode() && ((Node) start).isCommunity()) {
				Node node = (Node) start;
				louvain.setScale(louvain.getScale()-1);
				GraphEditDistance GED = new GraphEditDistance(
						louvain.getGraphFromCommunity(node.getComponentId(),
								louvain.getScale()), G2, this, edgehandler,
						false);
				return GED.getBestEditpath().getTotalCosts();
			}
			// End is a community
			if (end.isNode() && ((Node) end).isCommunity()) {
				Node node = (Node) start;
				louvain.setScale(louvain.getScale()-1);
				GraphEditDistance GED = new GraphEditDistance(
						louvain.getGraphFromCommunity(node.getComponentId(),
								louvain.getScale()), G2, this, edgehandler,
						false);
				return GED.getBestEditpath().getTotalCosts();
			}
			return nodeCost;
		}

		return edgeCost;
	}

	@Override
	public double getEdgeCosts() {
		return edgeCost;
	}

	@Override
	public double getNodeCosts() {
		return nodeCost;
	}
}
