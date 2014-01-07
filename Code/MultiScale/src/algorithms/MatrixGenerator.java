/**
 * 
 */
package algorithms;

import java.util.LinkedList;

import util.Edge;
import util.Graph;
import util.GraphComponent;
import util.Node;

/**
 * @author kriesen
 * 
 */
public class MatrixGenerator {

	private Graph source, target;

	
	private MunkresRec munkresRec;

	/**
	 * @return the cost matrix for two graphs @param sourceGraph and @param targetGraph
	 * |         |
	 * | c_i,j   | del
	 * |_________|______
	 * |         |
	 * |  ins    |	0
	 * |         |
	 * 
	 */
	public double[][] getMatrix(Graph sourceGraph, Graph targetGraph) {
		this.source = sourceGraph;
		this.target = targetGraph;
		int sSize = sourceGraph.size();
		int tSize = targetGraph.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		double[][] edgeMatrix;
		Node u;
		Node v;
		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = 0; j < tSize; j++) {
				v = (Node) this.target.get(j);
				double costs = Constants.costFunction.getCosts(u, v);
				// adjacency information
				edgeMatrix = this.getEdgeMatrix(u, v);
				costs += this.munkresRec.getCosts(edgeMatrix);
				matrix[i][j] = costs;
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				if ((i - sSize) == j) {
					v = (Node) this.target.get(j);
					double costs = Constants.costFunction.getCosts(
							Constants.EPS_COMPONENT, v);
					double f = v.getEdges().size();
					costs += (f * Constants.costFunction.getEdgeCosts());
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < sSize; i++) {
			u = (Node) this.source.get(i);
			for (int j = tSize; j < dim; j++) {
				if ((j - tSize) == i) {
					double costs = Constants.costFunction.getCosts(u,
							Constants.EPS_COMPONENT);
					double f = u.getEdges().size();
					costs += (f * Constants.costFunction.getEdgeCosts());
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = tSize; j < dim; j++) {
				matrix[i][j] =0.0;
			}
		}
		return matrix;
	}

	
	/**
	 * @return the cost matrix for two sets of graphcomponents
	 * |         |
	 * | c_i,j   | del
	 * |_________|______
	 * |         |
	 * |  ins    |	0
	 * |         |
	 * 
	 */
	public double[][] getMatrix(LinkedList components1, LinkedList components2) {
		int sSize = components1.size();
		int tSize = components2.size();
		int dim = sSize + tSize;
		double[][] matrix = new double[dim][dim];
		GraphComponent u;
		GraphComponent v;
		for (int i = 0; i < sSize; i++) {
			u = (GraphComponent) components1.get(i);
			for (int j = 0; j < tSize; j++) {
				v = (GraphComponent) components2.get(j);
				double costs = Constants.costFunction.getCosts(u, v);
				matrix[i][j] = costs;
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = 0; j < tSize; j++) {
				if ((i - sSize) == j) {
					v = (GraphComponent) components2.get(j);
					double costs = Constants.costFunction.getCosts(
							Constants.EPS_COMPONENT, v);
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < sSize; i++) {
			u = (GraphComponent) components1.get(i);
			for (int j = tSize; j < dim; j++) {
				if ((j - tSize) == i) {
					double costs = Constants.costFunction.getCosts(u,
							Constants.EPS_COMPONENT);
					matrix[i][j] = costs;
				} else {
					matrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = sSize; i < dim; i++) {
			for (int j = tSize; j < dim; j++) {
				matrix[i][j] =0.0;
			}
		}
//		this.printMatrix(matrix);
//		System.exit(0);
		return matrix;
	}
	
	/**
	 * @return the cost matrix for the edge operations 
	 * between the nodes @param u
	 * @param v
	 */
	private double[][] getEdgeMatrix(Node u, Node v) {
		int uSize = u.getEdges().size();
		int vSize = v.getEdges().size();
		int dim = uSize + vSize;
		double[][] edgeMatrix = new double[dim][dim];
		Edge e_u;
		Edge e_v;
		for (int i = 0; i < uSize; i++) {
			e_u = (Edge) u.getEdges().get(i);
			for (int j = 0; j < vSize; j++) {
				e_v = (Edge) v.getEdges().get(j);
				double costs = Constants.costFunction.getCosts(e_u, e_v);
				edgeMatrix[i][j] = costs;
			}
		}
		for (int i = uSize; i < dim; i++) {
			for (int j = 0; j < vSize; j++) {
				// diagonal
				if ((i - uSize) == j) {
					e_v = (Edge) v.getEdges().get(j);
					double costs = Constants.costFunction.getCosts(
							Constants.EPS_COMPONENT, e_v);
					edgeMatrix[i][j] = costs;
				} else {
					edgeMatrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = 0; i < uSize; i++) {
			e_u = (Edge) u.getEdges().get(i);
			for (int j = vSize; j < dim; j++) {
				// diagonal
				if ((j - vSize) == i) {
					double costs = Constants.costFunction.getCosts(e_u,
							Constants.EPS_COMPONENT);
					edgeMatrix[i][j] = costs;
				} else {
					edgeMatrix[i][j] = Double.POSITIVE_INFINITY;
				}
			}
		}
		for (int i = uSize; i < dim; i++) {
			for (int j = vSize; j < dim; j++) {
				edgeMatrix[i][j] = 0.0;
			}
		}
		return edgeMatrix;
	}

	/**
	 *  @return the cost matrix for two graphs @param sourceGraph and @param targetGraph
	 * |         |
	 * | c_i,j   |
	 * |         | (simple version)
	 * 
	 */
	public double[][] getSimpleMatrix(Graph sourceGraph, Graph targetGraph) {
		this.source = sourceGraph;
		this.target = targetGraph;
		double[][] matrix = new double[this.source.size()][this.target.size()];
		double[][] edgeMatrix;
		Node u;
		Node v;
		for (int i = 0; i < this.source.size(); i++) {
			u = (Node) this.source.get(i);
			for (int j = 0; j < this.target.size(); j++) {
				v = (Node) this.target.get(i);
				double costs = Constants.costFunction.getCosts(u, v);
				// adjacency information
				//edgeMatrix = this.getSimpleEdgeMatrix(u, v);
				//costs += this.munkresRec.getCosts(edgeMatrix);
				matrix[i][j] = costs;
			}
		}
		return matrix;
	}

	/**
	 * @return the cost matrix for the edge operations 
	 * between the nodes @param u
	 * @param v (simple version)
	 */
	private double[][] getSimpleEdgeMatrix(Node u, Node v) {
		int uSize = u.getEdges().size();
		int vSize = v.getEdges().size();
		double[][] edgeMatrix = new double[uSize][vSize];
		Edge e_u;
		Edge e_v;
		for (int i = 0; i < uSize; i++) {
			e_u = (Edge) u.getEdges().get(i);
			for (int j = 0; j < vSize; j++) {
				e_v = (Edge) v.getEdges().get(j);
				double costs = Constants.costFunction.getCosts(e_u, e_v);
				edgeMatrix[i][j] = costs;
			}
		}
		return edgeMatrix;
	}

	/**
	 * sets the munkres instances 
	 * @param munkres
	 * @param munkresRec
	 */
	public void setMunkres(MunkresRec munkresRec) {
		this.munkresRec = munkresRec;
	}


	/**
	 * for debugging: print the matrix @param m
	 */
	public void printMatrix(double[][] m){
		System.out.println("MATRIX:");
		for (int i = 0; i < m.length; i++){
			for (int j = 0; j < m[0].length; j++){
				if (m[i][j] < Double.POSITIVE_INFINITY){
					System.out.print(m[i][j]+"\t");
				} else {
					System.out.print("inf\t");
				}
				
			}
			System.out.println();
		}
	}

}
