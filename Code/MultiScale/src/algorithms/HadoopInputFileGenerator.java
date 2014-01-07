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

public class HadoopInputFileGenerator {

	 GraphCollection testGraphsList; // the test set
	 GraphCollection trainingGraphsList; // the raining set
	 public String propFileName;
	 public boolean debug;
	 
	public HadoopInputFileGenerator(String propFileName, boolean debug) throws Exception
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
		System.out.println("Path ::::"+properties.getProperty("path")+"inputFile.txt");
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
				System.out.println(testGraphFileName+"	"+trainingGraphFileName+"	"+testGraphClass+"	"+trainingGraphClass);
				ps.println(testGraphFileName+"	"+trainingGraphFileName+"	"+testGraphClass+"	"+trainingGraphClass);
				ps.close();
			}			
		}

		
		
	}
	
	
    public static void main(String[] args) throws Exception {
		
		String propFile = "bin/properties/letter.prop";
    	HadoopInputFileGenerator hIFG = new HadoopInputFileGenerator(propFile,true);
    	
    	System.out.println("After generating the txt file ");
		
	}
	
	
	
}
