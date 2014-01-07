/**
 * 
 */
package algorithms;

import java.util.Iterator;
import java.util.LinkedList;

import util.Edge;
import util.EditPath;
import util.Graph;
import util.GraphComponent;
import util.Matrix;
import util.MatrixElement;
import util.Node;

/**
 * @author kriesen
 * 
 */
public class Munkres {

	private Matrix m;

	private int k;

	private Graph source, target;

	public double getCosts(double[][] matrix) {
		this.m = new Matrix(matrix);
		this.m.subtractRowMin();
		this.m.subtractColMin();
		this.m.starZeros();
		return this.step1();
	}

	private double step1() {
		this.m.coverStarred();
		if (this.m.getCoverNum() == this.k) {
			return this.done();
		} else {
			return this.step2();
		}
	}

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
				return this.step2();
			}
		} else {
			double e_min = this.m.getUncoveredMin();
			return this.step4(e_min);
		}
	}

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

	private double step4(double e_min) {
		this.m.addToCoveredRows(e_min);
		this.m.subtractFromUncoveredCols(e_min);
		return this.step2();
	}

	private double done() {
		EditPath p = new EditPath(this.source, this.target);
		int[][] indices = this.m.getStarredIndices(this.k);
		for (int i = 0; i < this.k; i++) {
			int r = indices[i][0];
			int c = indices[i][1];
			GraphComponent start;
			GraphComponent end;
			if (r < this.source.size()) {
				start = (GraphComponent) this.source.get(r);
			} else {
				start = Constants.EPS_COMPONENT;
			}
			if (c < this.target.size()) {
				end = (GraphComponent) this.target.get(c);
			} else {
				end = Constants.EPS_COMPONENT;
			}
			if (!(start.equals(Constants.EPS_COMPONENT) && end.equals(Constants.EPS_COMPONENT))){
				p.addDistortion(start, end);
			}
		}
//		p.complete();			
//		LinkedList tempList = new LinkedList();
//		tempList.addAll(p.getUnUsedEdges2());
//		Iterator iter = tempList.iterator();
//		while (iter.hasNext()) {
//			Edge w = (Edge) iter.next();
//			p.addDistortion(Constants.EPS_COMPONENT, w);
//			p.addDistortion(Constants.EPS_COMPONENT, w.getStartNode());
//			p.addDistortion(Constants.EPS_COMPONENT, w.getEndNode());
//		}
		if (!p.allUsed()){
			System.err.println("FATAL ERROR: not all components used...");
			System.out.println("source: "+this.source.getId()+" -->> target: "+this.target.getId());
			System.out.println("the path: ");
			p.printMe();
			System.exit(0);
		}
		if (p.getTotalCosts() < 0){
			 p.printMe();
			 System.exit(0);
		}
//		p.printMe();
//		System.exit(0);
		return p.getTotalCosts();
		
	}
	
	
	
	
	public EditPath ApproximatedEditPath() {
		EditPath p = new EditPath(this.source, this.target);
		int[][] indices = this.m.getStarredIndices(this.k);
		for (int i = 0; i < this.k; i++) {
			int r = indices[i][0];
			int c = indices[i][1];
			GraphComponent start;
			GraphComponent end;
			if (r < this.source.size()) {
				start = (GraphComponent) this.source.get(r);
			} else {
				start = Constants.EPS_COMPONENT;
			}
			if (c < this.target.size()) {
				end = (GraphComponent) this.target.get(c);
			} else {
				end = Constants.EPS_COMPONENT;
			}
			if (!(start.equals(Constants.EPS_COMPONENT) && end.equals(Constants.EPS_COMPONENT))){
				p.addDistortion(start, end);
			}
		}
//		p.complete();		
//		LinkedList tempList = new LinkedList();
//		tempList.addAll(p.getUnUsedEdges2());
//		Iterator iter = tempList.iterator();
//		while (iter.hasNext()) {
//			Edge w = (Edge) iter.next();
//			p.addDistortion(Constants.EPS_COMPONENT, w);
//			p.addDistortion(Constants.EPS_COMPONENT, w.getStartNode());
//			p.addDistortion(Constants.EPS_COMPONENT, w.getEndNode());
//		}
	
		
		if (!p.allUsed()){
			System.err.println("FATAL ERROR: not all components used...");
			System.out.println("source: "+this.source.getId()+" -->> target: "+this.target.getId());
			System.out.println("the path: ");
			p.printMe();
			System.exit(0);
		}
		if (p.getTotalCosts() < 0){
			 p.printMe();
			 System.exit(0);
		}
//		p.printMe();
//		System.exit(0);
		return p;
		
	}

	public void setGraphs(Graph s, Graph t) {
		this.source = s;
		this.target = t;
		this.k = this.source.size() + this.target.size();
	}
	
}
