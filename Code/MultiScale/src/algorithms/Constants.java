/**
 * 
 */
package algorithms;

import util.*;


/**
 * @author riesen
 * 
 */
public class Constants {

	/**
	 * the eps-components
	 */
	public final static GraphComponent EPS_COMPONENT = new GraphComponent(
			Constants.EPS_ID);

	public final static String EPS_ID = "eps_id";

	/**
	 * the cost function has to be changed for different databases (see 1. todo)
	 */
	public static ICostFunction costFunction;

	/**
	 * the edgehandler has to be changed for several databases (see 2. todo)
	 */
	public static IEdgeHandler edgeHandler;

	public Constants(double nodeCosts, double edgeCosts, double alpha) {
		// set the appropriate cost function
		Constants.costFunction = (ICostFunction)new UnlabeledCostFunction(nodeCosts, edgeCosts, alpha); // TODO 1
                // set the appropriate edgehandler (directed or undirected)
                Constants.edgeHandler = new UnDirectedEdgeHandler(); 
	}
	
	

}
