/**
 * 
 */
package algorithms;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import util.Graph;
import util.GraphCollection;
import xml.XMLParser;
import xml.XMLWriter;

/**
 * @author kriesen
 * 
 */
public class MainMunkres {

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
	 * 
	 */

	/**
	 * reads the graphs sets and triggers the individual matchings. results are
	 * stored in the distancematrix[][]
	 */
	public MainMunkres(double n, double e, double a, String prop)
			throws Exception {
		// read in the source and the target collection
		Properties properties = new Properties();
		properties.load(new FileInputStream(prop));
		XMLParser xmlParser = new XMLParser();
		xmlParser.setGraphPath(properties.getProperty("path"));
		this.source = xmlParser.parseCXL(properties.getProperty("source"));
		this.target = xmlParser.parseCXL(properties.getProperty("target"));
		// prepare the constants
		Constants constants = new Constants(n, e, a);
		// create a distance matrix to store the results
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
		int counter = 0;
		for (int i = 0; i < r; i++) {
			sourceGraph = (Graph) this.source.get(i);

			for (int j = 0; j < c; j++) {
				targetGraph = (Graph) this.target.get(j);
				if (targetGraph.size() > sourceGraph.size()) {
					this.swapGraphs();
				}
				System.out.println("Progress: " + (counter++));
				System.out.println("from : " + sourceGraph.getId() + "\tto : " + targetGraph.getId());
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
		this.toMatrixFile(properties.getProperty("result") + "one_0.7.m"); 
		// write out the distance file
		XMLWriter xmlWriter = new XMLWriter();
		xmlWriter.setResultName(properties.getProperty("result") + "_"
				+ n + "_" + e +".dxl");
		xmlWriter.writeDXL(this.distanceMatrix);
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

	private String toMatrix() {
		String s = new String();

		
		for (int i = 0; i < this.target.size(); i++){
			for (int j = 0; j < this.source.size(); j++)
		   		s += distanceMatrix[i][j] + "\t";		   
			s += "\n";
		}
		return s;
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

	private void toMatrixFile(String fileName) throws IOException{
		FileWriter output = new FileWriter(fileName);
		output.write(this.toMatrixWithGraphId());
		output.flush();
		output.close();
	}

	/**
	 * the main method -- where it all begins
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			double n = Double.parseDouble(args[0]);
			double e = Double.parseDouble(args[1]);
			double a = Double.parseDouble(args[2]); 
			String p = args[3];
			MainMunkres munkres = new MainMunkres(n, e, a, p);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
