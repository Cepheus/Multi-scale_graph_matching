package algorithms;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

import util.Graph;
import util.GraphCollection;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.PairGraphDistance;
import util.UniversalCostFunction;
import util.UniversalEdgeHandler;
import util.UnlabeledCostFunction;
import xml.XMLParser;



public class AverageConnectivityRate {

	/**
	 * @param args
	 */
	
	 /*
	  * source is the test set
	  * target is the raining set
	  * e.g.
	  * 	source=data/Letter/MED/test.cxl
	  * 	target=data/Letter/MED/train.cxl
	  */
	 GraphCollection source;
	 private GraphCollection target; 
	 private GraphCollection validation; 
	 private Graph sourceGraph, targetGraph, valdGraph;
	 public String propFileName;
	 public boolean debug;
	 public int maxNumberOfEdges = 0;
	 public int maxNumberOfNodes = 0;
	 public double avgNoOfNodes = 0;
	 public double avgNoOfEdges = 0;
		 
	
	public AverageConnectivityRate(String propFileName, boolean debug) throws Exception
	{

		this.debug=debug;
		this.propFileName = propFileName;
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(this.propFileName));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("here");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("here2");
			e.printStackTrace();
		}
		
		XMLParser xmlParser = new XMLParser();
		// Getting the path of the Dataset
		xmlParser.setGraphPath(properties.getProperty("path"));
		// parsing the test set
		this.source = xmlParser.parseCXL(properties.getProperty("source"));
		// parsing the training set
		this.target = xmlParser.parseCXL(properties.getProperty("target"));
		
		this.validation = xmlParser.parseCXL(properties.getProperty("validation"));
		
		int r = source.size();
		int c = target.size();
		int v = validation.size();
		int noOfGraphs = r+c+v;
		double noOfCompleteEdges=0.0;
		double connectivityRate =0.0;
		
		if (debug==true)
		{
			System.out.println("Source size ====== "+r);
			System.out.println("Target size ====== "+c);
			System.out.println("validation size ====== "+v);
		}

		
		for (int i = 0; i < r; i++) {
				
		
			sourceGraph = (Graph) source.get(i);
			if(sourceGraph.getEdges().size()!=0)
			{
				if(debug==true) System.out.println("*****************SourceGraph # "+ i+ " "+sourceGraph.getId());
				System.out.println("Graphs nodes' size = "+sourceGraph.size());
				noOfCompleteEdges=0.0;
				for (int k = 1; k < sourceGraph.size(); k++) {
					if(debug==true)
					{
						System.out.print(k+ " + ");
					}
					
					avgNoOfNodes = avgNoOfNodes + sourceGraph.size();
					avgNoOfEdges =  avgNoOfEdges + sourceGraph.getEdges().size();
				
					if(sourceGraph.size()>maxNumberOfNodes)
					{
						maxNumberOfNodes = sourceGraph.size();
					}
					if(sourceGraph.getEdges().size()>maxNumberOfEdges)
					{
						maxNumberOfEdges = sourceGraph.getEdges().size();
					}
					
					noOfCompleteEdges = noOfCompleteEdges+k;
	
	
				}
				System.out.println();
				System.out.println("Edges of a complete Graph :::"+noOfCompleteEdges);
				System.out.println("No of Edges :::"+sourceGraph.getEdges().size());
				System.out.println("Conncetivity Rate ::"+sourceGraph.getEdges().size()/noOfCompleteEdges);
				connectivityRate = connectivityRate+(sourceGraph.getEdges().size()/noOfCompleteEdges);
				System.out.println("Sum of current connectivity rates"+connectivityRate);
				if(debug==true) System.out.println();
				}
		}

		
		for (int j = 0; j < c; j++) {
		
			
			targetGraph = (Graph) target.get(j);	
			if(targetGraph.getEdges().size()!=0)
			{
				if(debug==true) System.out.println("***********TrainingGraph # "+j+" "+targetGraph.getId());
				
				System.out.println("Graphs nodes' size = " +targetGraph.size());
				noOfCompleteEdges=0.0;
				for (int k = 1; k < targetGraph.size(); k++) {
					if(debug==true)
					{
						System.out.print(k+ " + ");
					}
					noOfCompleteEdges = noOfCompleteEdges+k;
					
					avgNoOfNodes = avgNoOfNodes + targetGraph.size();
					avgNoOfEdges =  avgNoOfEdges + targetGraph.getEdges().size();
					
					if(targetGraph.size()>maxNumberOfNodes)
					{
						maxNumberOfNodes = targetGraph.size();
					}
					if(targetGraph.getEdges().size()>maxNumberOfEdges)
					{
						maxNumberOfEdges = targetGraph.getEdges().size();
					}
				}
				System.out.println();
				System.out.println("Edges of a complete Graph :::"+noOfCompleteEdges);
				System.out.println("No of Edges :::"+targetGraph.getEdges().size());
				System.out.println("Conncetivity Rate ::"+targetGraph.getEdges().size()/noOfCompleteEdges);
				connectivityRate = connectivityRate+(targetGraph.getEdges().size()/noOfCompleteEdges);
				System.out.println("Sum of current connectivity rates"+connectivityRate);
				if(debug==true) System.out.println();
				}
		
		}
		
	for (int j = 0; j < v; j++) {
		
			
			valdGraph = (Graph) validation.get(j);	
			if(valdGraph.getEdges().size()!=0)
			{
				if(debug==true) System.out.println("***********ValidationGraph # "+j+" "+valdGraph.getId());
				
				System.out.println("Graphs nodes' size = " +valdGraph.size());
				noOfCompleteEdges=0.0;
				for (int k = 1; k < valdGraph.size(); k++) {
					if(debug==true)
					{
						System.out.print(k+ " + ");
					}
					noOfCompleteEdges = noOfCompleteEdges+k;
					avgNoOfNodes = avgNoOfNodes + valdGraph.size();
					avgNoOfEdges =  avgNoOfEdges + valdGraph.getEdges().size();
					
					if(valdGraph.size()>maxNumberOfNodes)
					{
						maxNumberOfNodes = valdGraph.size();
					}
					if(valdGraph.getEdges().size()>maxNumberOfEdges)
					{
						maxNumberOfEdges = valdGraph.getEdges().size();
					}
					
				}
				System.out.println();
				System.out.println("Edges of a complete Graph :::"+noOfCompleteEdges);
				System.out.println("No of Edges :::"+valdGraph.getEdges().size());
				System.out.println("Conncetivity Rate ::"+valdGraph.getEdges().size()/noOfCompleteEdges);
				connectivityRate = connectivityRate+(valdGraph.getEdges().size()/noOfCompleteEdges);
				System.out.println("Sum of current connectivity rates"+connectivityRate);
				if(debug==true) System.out.println();
				}
		
		}
		
		System.out.println("Conncetivity Rate ::"+connectivityRate);
		connectivityRate = connectivityRate/noOfGraphs;
		System.out.println("No of Graphs :::"+noOfGraphs);
		System.out.println("Connectivity Rate :::"+connectivityRate);
		System.out.println("Max no of nodes :::"+maxNumberOfNodes);
		System.out.println("Max no of Edges :::"+maxNumberOfEdges);
		System.out.println("Average no of nodes :::"+avgNoOfNodes/noOfGraphs);
		System.out.println("Average no of Edges :::"+avgNoOfEdges/noOfGraphs);
			

		///////////////////////////////////////////////////////////////////////////////
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		String propFile = "bin/properties/letter.prop";
		
		System.out.println("*******************************************************");
		AverageConnectivityRate cs =  new AverageConnectivityRate(propFile,true);
		
		System.out.println("*******************************************************");
		
	
				

	}



}
