package util;

import java.io.IOException;
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
	 * The first graph to compare.
	 */
	private Graph G1;

	/**
	 * The second graph to compare.
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
	 * @param G1
	 *            The first graph to compare.
	 * @param G2
	 *            The second graph to compare.
	 * @param edgehandler
	 *            The edge handler.
	 */
	public CommunityCostFunction(double nodeCost, double edgeCost, String key,
			Graph G1, Graph G2, IEdgeHandler edgehandler) {
		this.nodeCost = nodeCost;
		this.edgeCost = edgeCost;
		this.key = key;
		this.G1 = G1;
		this.G2 = G2;
		this.edgehandler = edgehandler;
	}

	@Override
	public double getCosts(GraphComponent start, GraphComponent end) {
		
		Graph H1, H2;
		if (start.isNode() && end.isNode()) {
			Node start_node = (Node) start;
			Node end_node = (Node) end;
			// G1 and G2 are communities
			if (start_node.isCommunity() && end_node.isCommunity()) {
				H1 = G1.getGraphFromCommunity(start_node.getComponentId(),
						start_node.getScale());
				H1 = H1.getGraphFromScale(start_node.getScale() - 1, key);

				H2 = G2.getGraphFromCommunity(end_node.getComponentId(),
						end_node.getScale());
				H2 = H2.getGraphFromScale(end_node.getScale() - 1, key);

				GraphEditDistance GED = new GraphEditDistance(H1, H2, this,
						edgehandler, false);
				return GED.getBestEditpath().getTotalCosts();
			} else if (start_node.isCommunity()) {
				H1 = G1.getGraphFromCommunity(start_node.getComponentId(),
						start_node.getScale());
				H1 = H1.getGraphFromScale(start_node.getScale() - 1, key);

				return (H1.size()-1) * nodeCost + H1.getEdges().size() * edgeCost;
			} else if (end_node.isCommunity()) {
				H2 = G2.getGraphFromCommunity(end_node.getComponentId(),
						end_node.getScale());
				H2 = H2.getGraphFromScale(end_node.getScale() - 1, key);

				return (H2.size()-1) * nodeCost + H2.getEdges().size() * edgeCost;
			}
			return 0.0;
		}
		if (start.isNode() || end.isNode()) {
			if (start.getComponentId().equals(Constants.EPS_ID) || end.getComponentId().equals(Constants.EPS_ID)) {
				if (start.getComponentId().equals(Constants.EPS_ID) && ((Node)end).isCommunity() ) {
					Node end_node = (Node) end;
					H2 = G2.getGraphFromCommunity(end_node.getComponentId(),
							end_node.getScale());
					H2 = H2.getGraphFromScale(end_node.getScale() - 1, key);
					return H2.size() * nodeCost + H2.getEdges().size() * edgeCost;
				}
				else if(end.getComponentId().equals(Constants.EPS_ID) && ((Node)start).isCommunity()) {
					Node start_node = (Node) start;
					H1 = G1.getGraphFromCommunity(start_node.getComponentId(),
							start_node.getScale());
					H1 = H1.getGraphFromScale(start_node.getScale() - 1, key);

					return H1.size() * nodeCost + H1.getEdges().size() * edgeCost;
				}
				return nodeCost;
			}					
		}
		else {
			if (start.getComponentId().equals(Constants.EPS_ID) || end.getComponentId().equals(Constants.EPS_ID)) {
				return this.edgeCost;
			}
		}

		return 0.0;
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
