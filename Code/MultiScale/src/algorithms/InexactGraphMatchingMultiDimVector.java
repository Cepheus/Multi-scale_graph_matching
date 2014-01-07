package algorithms;

import java.io.FileWriter;
import java.io.IOException;

import util.Graph;
import util.GraphCollection;
import util.ICostFunction;
import util.UnlabeledCostFunction;
import util.VectorCostFunction;
import xml.XMLParser;
import xml.XMLWriter;

public class InexactGraphMatchingMultiDimVector {
	
	public InexactGraphMatchingMultiDimVector(){
		
	}
	
	/**
	 * the collections
	 */
	private GraphCollection source, target;

	/**
	 * the results of all matchings
	 */
	private double[][] distanceMatrix;

	/**
	 * the source and target graphs
	 */
	private Graph sourceGraph, targetGraph;

	/**
	 * boolean to check if swapped or not
	 */
	private boolean swapped = false;

	
	/**
	 * reads the graphs sets and triggers the individual matchings. results are
	 * stored in the distancematrix[][]
	 */
	public void CompareTwoGraphs(String sg1, String sg2)
			throws Exception {
		 source = new GraphCollection();
		 source.setCollectionName(sg1);
		 
		 
		 target = new GraphCollection();
		 target.setCollectionName(sg2);
		
		XMLParser xmlParser = new XMLParser();
		Graph g1 = xmlParser.parseGXL(sg1);
		Graph g2 = xmlParser.parseGXL(sg2);
		source.add(g2);
		target.add(g1);
		
		int r = this.source.size();
		int c = this.target.size();
		this.distanceMatrix = new double[r][c];
		// check every combination
		MatrixGenerator mgen = new MatrixGenerator();
		Munkres munkres = new Munkres();
		MunkresRec munkresRec = new MunkresRec();
		mgen.setMunkres(munkresRec);
		double[][] matrix;
		double distance = 0.0;
		// prepare the constants
		Constants constants = new Constants(1,1,0.5);
		constants.costFunction = (ICostFunction)new VectorCostFunction(1,1,0.5); // TODO 1
		
		
		int counter = 0;
		for (int i = 0; i < r; i++) {
			sourceGraph = (Graph) this.source.get(i);
			for (int j = 0; j < c; j++) {
				targetGraph = (Graph) this.target.get(j);
				if (targetGraph.size() > sourceGraph.size()) {
					this.swapGraphs();
				}
				counter++;
				//System.out.println("Progress: " + (counter++));
				//System.out.println("from : " + sourceGraph.getId() + "\tto : " + targetGraph.getId());
				matrix = mgen.getMatrix(sourceGraph, targetGraph);
				munkres.setGraphs(sourceGraph, targetGraph);
				distance = munkres.getCosts(matrix);
				distance *= 1000.0;
				distance = Math.round(distance);
				distance /= 1000.0;
				System.out.println(distance);
				this.distanceMatrix[i][j] = distance;
				if (swapped) {
					this.swapGraphs();
				}

			}
		}
		// print results
		//this.toMatrixFile("result.m"); 
		// write out the distance file
		//XMLWriter xmlWriter = new XMLWriter();
		//xmlWriter.setResultName("result.dxl");
		//xmlWriter.writeDXL(this.distanceMatrix);
		
		FileWriter output = new FileWriter("result.m");
		output.write(""+distanceMatrix[0][0] );
		output.flush();
		output.close();
	}
	
	private String toMatrix() {
		String s = new String();

		
		for (int i = 0; i < this.target.size(); i++){
			for (int j = 0; j < this.source.size(); j++)
		   		s += distanceMatrix[i][j] + "\t";		   
			s += "\n";
		}
		return s;
	}
	

	private void toMatrixFile(String fileName) throws IOException{
		FileWriter output = new FileWriter(fileName);
		output.write(this.toMatrixWithGraphId());
		output.flush();
		output.close();
	}

	/**
	 * swaps the source and target graph
	 */
	private void swapGraphs() {
		Graph temp = new Graph();
		temp = this.sourceGraph;
		this.sourceGraph = targetGraph;
		this.targetGraph = temp;
		this.swapped = !this.swapped;

	}
	
	
	private String toMatrixWithGraphId() {
		String s = new String();
		
		/*
		 * with file names
		 */
		s += "\t";
		for(int i = 0; i < this.source.size(); i++) {
			s += ((Graph)(this.source.get(i))).getClassId() + ((Graph)(this.source.get(i))).getId() + "\t";
		}
		s += "\n";
		
		for (int i = 0; i < this.target.size(); i++){
			s += ((Graph)(this.source.get(i))).getClassId() + ((Graph)(this.target.get(i))).getId() + "\t";
			for (int j = 0; j < this.source.size(); j++)
		   		s += distanceMatrix[i][j] + "\t";		   
			s += "\n";
		}
		return s;
	}
	
	
	public static void main(String[] args) throws NumberFormatException, Exception{
		
		InexactGraphMatchingMultiDimVector IGMMDV = new InexactGraphMatchingMultiDimVector();
		IGMMDV.CompareTwoGraphs(args[0], args[1]);
	}
}
