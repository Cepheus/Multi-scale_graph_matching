/**
 * 
 */
package algorithms;

import java.util.Iterator;
import java.util.LinkedList;

import util.Graph;
import util.GraphComponent;
import util.Matrix;
import util.MatrixElement;

/**
 * @author kriesen
 * 
 */
public class MunkresRec {

	/**
	 * the munkres matrix - operations will
	 * be executed on this data structure
	 */
	private Matrix m;

	/**
	 * the dimension of m - needed 
	 * for determination
	 */
	private int k;
	
	/**
	 * the original cost matrix
	 */
	private double[][] costMatrix;

	/**
	 * @return the optimal cost value
	 * of the mapping in @param matrix
	 */
	public double getCosts(double[][] matrix) {
		this.costMatrix = matrix;
		this.k = matrix.length;
		this.m = new Matrix(matrix);
		this.m.subtractRowMin();
//		this.m.subtractColMin();
		this.m.starZeros();
		return this.step1();
	}

	/**
	 * Step 1 - checks whether or not the 
	 * mapping is complete
	 */
	private double step1() {
		this.m.coverStarred();
		if (this.m.getCoverNum() == this.k) {
			return this.done();
		} else {
			double repeat = this.step2();
			while (repeat == -1.0){
				repeat = this.step2();
			}
			return repeat;
		}
	}

	/**
	 * Step 2 - searchs an uncovered zero and prime it
	 * if no such element is found the min element is saved
	 */
	private double step2() {
		MatrixElement Z0 = this.m.getUncoveredZero();
		if (Z0 != null) {
			this.m.primeElement(Z0);
			MatrixElement e = this.m.getStarredZeroInR(Z0);
			if (e == null) {
				return this.step3(Z0);
			} else {
				this.m.coverRow(Z0);
				this.m.unCoverCol(e);
				return -1; // repeat step 2
			}
		} else {
			double e_min = this.m.getUncoveredMin();
			return this.step4(e_min);
		}
	}

	/**
	 * creates a serie of alternating primed and starred zeros
	 * starting with @param Z0
	 */
	private double step3(MatrixElement Z0) {
		LinkedList s = new LinkedList();
		s.add(Z0);
		MatrixElement act = Z0;
		while (this.m.isStarredCol(act)) {
			MatrixElement Z1 = this.m.getStarredZeroInC(act);
			s.add(Z1);
			MatrixElement Z2 = this.m.getPrimedZeroInR(Z1);
			s.add(Z2);
			act = Z2;
		}
		Iterator iter = s.iterator();
		while (iter.hasNext()) {
			MatrixElement e = (MatrixElement) iter.next();
			this.m.handleElement(e);
		}
		this.m.unPrimeAll();
		this.m.unCoverAll();
		return this.step1();
	}

	/**
	 * adds @param e_min to covered rows 
	 * and subtract it from uncovered cols
	 */
	private double step4(double e_min) {
		this.m.addToCoveredRows(e_min);
		this.m.subtractFromUncoveredCols(e_min);
		return this.step2();
	}

	/**
	 * finished - optimal mapping is found
	 */
	private double done() {
		double totCosts = 0.0;
		int[][] indices = this.m.getStarredIndices(this.k);
		for (int i = 0; i < this.k; i++) {
			int r = indices[i][0];
			int c = indices[i][1];
			totCosts += this.costMatrix[r][c];
		}
		return totCosts;
	}
	
	public int[][] getStarredIndices(){
		int[][] indices = this.m.getStarredIndices(this.k);
		return indices;
	}

	public double getSubstituionsCost(LinkedList unUsedNodes12,
			LinkedList unUsedNodes22) {
		// TODO Auto-generated method stub
		double sum=0.0;
		
		int[][] indices = this.m.getStarredIndices(this.k);
		
		for (int i = 0; i < this.k; i++) {
			int r = indices[i][0];
			int c = indices[i][1];
			GraphComponent start;
			GraphComponent end;
			if (r < unUsedNodes12.size()) {
				start = (GraphComponent) unUsedNodes12.get(r);
			} else {
				start = Constants.EPS_COMPONENT;
			}
			if (c < unUsedNodes22.size()) {
				end = (GraphComponent) unUsedNodes22.get(c);
			} else {
				end = Constants.EPS_COMPONENT;
			}
			
			if( (start.equals(Constants.EPS_COMPONENT) ==false ) 
					&& 
					(end.equals(Constants.EPS_COMPONENT) ==false ) ){
				sum+=this.costMatrix[r][c];//Constants.costFunction.getCosts(start, end);
			}
		}

		return sum;
	}
	
	
	
}
