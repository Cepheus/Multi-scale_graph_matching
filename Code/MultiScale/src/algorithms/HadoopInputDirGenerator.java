package algorithms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import nanoxml.XMLElement;

import util.Graph;
import util.GraphCollection;
import xml.XMLParser;

public class HadoopInputDirGenerator {

	 GraphCollection testGraphsList; // the test set
	 GraphCollection trainingGraphsList; // the raining set
	 public String propFileName;
	 public boolean debug;
	 
	public HadoopInputDirGenerator(String propFileName,int noDataSet ,boolean debug) throws Exception
	{
		this.propFileName = propFileName;
		this.debug = debug;

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
		String graphsPath = properties.getProperty("path");
		// parsing the test set
		String testGraphsListPath = properties.getProperty("source");
		// parsing the training set
		String trainingGraphsListPath =properties.getProperty("target");
		
		XMLElement testXML = new XMLElement();
		FileReader testReader = new FileReader(testGraphsListPath);
		testXML.parseFromReader(testReader);
		Vector testListchildren = testXML.getChildren();
		XMLElement testRoot = (XMLElement) testListchildren.get(0);
		Enumeration testEnumerator = testRoot.enumerateChildren();
		String testGraphFileName="";
		String testGraphClass="";
		
		XMLElement trainingXML = new XMLElement();
		FileReader trainingReader = new FileReader(trainingGraphsListPath);
		trainingXML.parseFromReader(trainingReader);
		Vector trainingListchildren = trainingXML.getChildren();
		XMLElement trainingRoot = (XMLElement) trainingListchildren.get(0);
		
		String trainingGraphFileName="";
		String trainingGraphClass="";
		// Preparing the txt file ...
		PrintStream ps;

		File theDir = new File(properties.getProperty("path")+"inputs");

		  // if the directory does not exist, create it
		  if (!theDir.exists())
		  {
		    System.out.println("creating directory: ");
		    boolean result = theDir.mkdir();  
		    if(result){    
		       System.out.println("DIR created");  
		     }

		  }


		
		while (testEnumerator.hasMoreElements()) {
			XMLElement testChild = (XMLElement) testEnumerator.nextElement();
			testGraphFileName=(String) testChild.getAttribute("file", null);
			testGraphClass = (String) testChild.getAttribute("class", "NO_CLASS");
			Enumeration trainingEnumerator = trainingRoot.enumerateChildren();
			
			while (trainingEnumerator.hasMoreElements()) {
				XMLElement trainingChild = (XMLElement) trainingEnumerator.nextElement();
				trainingGraphFileName=(String) trainingChild.getAttribute("file", null);
				trainingGraphClass = (String) trainingChild.getAttribute("class", "NO_CLASS");
				ps =  new PrintStream(properties.getProperty("path")+"inputs/"+testGraphFileName+"-"+trainingGraphFileName+".txt");
				System.out.println(noDataSet+"	"+testGraphFileName+"	"+trainingGraphFileName+"	"+testGraphClass+"	"+trainingGraphClass);
				ps.println(noDataSet+"	"+testGraphFileName+"	"+trainingGraphFileName+"	"+testGraphClass+"	"+trainingGraphClass);
				ps.close();
			}			
		}

		
		
	}
	
	
    public static void main(String[] args) throws Exception {
		
    	/* This code takes two parameters, e.g. "bin/properties/letter.prop";
    	 * 1st parameter = the directory of the property i.e. letter, GRED, etc....
    	 * 2nd parameter = method number s.t. ((1 = GED), (2 = A*), (3 = Beam-Search), (4 = Munkres), ( 5 = DGED))
    	 * 
    	 */
    	
    	/*
    	 * This code takes two parameters:
    	 * 1- The number of the data set that will be used 
    	 * 	a- (1) LetterCostFunction
    	 * 	b- (2) ImageCostFunction
    	 * 	c- (3) GRECCostFunction
    	 * 	d- (4) MoleculesCostFunction
    	 * 	e- (5) VectorGraphCostFunction
    	 * 	f- (6) VectorCostFunction
    	 * 
    	 * 2- The number of the method that we want to use s.t. ((1 = GED), (2 = A*), (3 = Beam-Search), (4 = Munkres), ( 5 = DGED)
    	 * 		
    	 */
		String propFile = "";

		int noDataSet = Integer.parseInt(args[0]);

		if( noDataSet == 2)
		{
			propFile = "bin/properties/LetterLow.prop";
		}
		else if( noDataSet == 1)
		{
			propFile = "bin/properties/Letter.prop";
		}
		else if( noDataSet == 3)
		{
			propFile = "bin/properties/LetterHigh.prop";
		}
		else if(noDataSet == 4)
		{
			//GREC-CostFunction
			propFile = "bin/properties/GREC.prop";
		}
		else if(noDataSet == 5)
		{
			//CoilCostFunction
			propFile = "bin/properties/COILDEL.prop";
		}
		else if(noDataSet == 6)
		{
			//CoilCostFunction
			propFile = "bin/properties/COILRAG.prop";
		}
		else if (noDataSet == 7) 
		{
			//MoleculesCostFunction
			propFile = "bin/properties/AIDS.prop";
			
		}
		else if (noDataSet == 8) 
		{
			//LetterCostFunction
			propFile = "bin/properties/FingerPrint.prop";
			
		}
		else if (noDataSet == 9) 
		{
			//MutagenCostFunction
			propFile = "bin/properties/Mutagen.prop";
			
		}
		else if (noDataSet == 10) 
		{
			//MutagenCostFunction
			propFile = "bin/properties/WebPages.prop";
			
		}
		else if (noDataSet == 11) 
		{
			//MutagenCostFunction
			propFile = "bin/properties/SG5.prop";
			
		}
		else if (noDataSet == 12) 
		{
			//MutagenCostFunction
			propFile = "bin/properties/SG10.prop";
			
		}
		else 
		{
			System.out.println("unValidNo ...");
			return;
		}
		
		
		//((1 = GED), (2 = A*), (3 = Beam-Search), (4 = Munkres), ( 5 = DGED)
		

    	HadoopInputDirGenerator hIFG = new HadoopInputDirGenerator(propFile,noDataSet,true);
    	
    	System.out.println("After generating the txt file ");
		
	}
	
	
	
}
