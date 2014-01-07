package algorithms;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class SDcxlFiles {

	/**
	 * @param args
	 * A class to generate CXL files for the SD database (i.e. Synthetic Database )
	 */
	
	
	public int noOfNodes;
	public int noOfGraphs;
	
	public SDcxlFiles(int noOfNodes, int noOfGraphs) throws FileNotFoundException
	{
		this.noOfNodes = noOfNodes;
		this.noOfGraphs= noOfGraphs;
		// create a cxl file, open it and write in it..
		PrintStream ps =  new PrintStream("./data/Synthetic Dataset (SD)/SD-"+noOfNodes+"/graphs.cxl");
		
		
		//ps.println();
		ps.println("<?xml version=\"1.0\"?>");
		ps.println("<GraphCollection xmlns:ns=\"http://www.iam.unibe.ch/%7Emneuhaus/FAN/1.0\">");
		ps.println("<fingerprints base=\"/scratch/mneuhaus/progs/letter-database/automatic/0.5\" classmodel=\"henry5\" count=\"750\">");
		
		
		for (int i=1 ; i <=noOfGraphs ; i++)
		{
			
			ps.println("<print file=\""+noOfNodes+"-"+i+".gxl\" class=\"A\"/>");
		}
		
		ps.println("</fingerprints>");
		ps.println("</GraphCollection>");
		ps.close();
	
	
	
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		System.out.println("Start to generateg files");
		int noOfNodes = 50;
		int noOfGraphs=50;
		SDcxlFiles cxlFiles = new SDcxlFiles(noOfNodes, noOfGraphs);
		System.out.println("Mission Completed");

	}

}
