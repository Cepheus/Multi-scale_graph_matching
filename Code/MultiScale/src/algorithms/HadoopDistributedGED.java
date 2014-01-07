package algorithms;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.UUID;

import util.CoilCostFunction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import util.EditPath;
import util.ExampleBaseJob;
import util.GRECCostFunction;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.MoleculesCostFunction;
import util.MutagenCostFunction;
import util.PairGraphDistance;
import util.UniversalCostFunction;
import util.UniversalEdgeHandler;
import util.WebCostFunction;
import xml.XMLParser;

/**
 * Author : Zeina Abu-Aisheh
 * This task concerns classifying test graphs given a list of training graphs 
 *   The 1NN-classifier is used to classify graphs 
 *   5 similarity measures are used here (where the variable methodNumber has to be set first in order to evaluate one of these similarity measures) 
 *   
 **/

public class HadoopDistributedGED extends ExampleBaseJob {

	/**
	 * Haddoop1NNMapper class that has the map method that outputs intermediate keys and
	 * values to be processed by the HadoopDistributedGEDReducer.
	 * 
	 * The type parameters are the type of the input key, the type of the input
	 * values, the type of the output key and the type of the output values
	 * 
	 * 
	 * Input format (information about the graphs of the training list) : trainingGraphFileName<tab>trainingGraphClass 
	 * 
	 * Output format: <testGraphFileName, trainingGraphClass-distanceGED(testGraph,trainingGraph>
	 * 
	 *
	 * 
	 **/


	public static class HadoopDistributedGEDMapper extends
			Mapper<LongWritable, Text, Text, Text> {

		
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// key: file's line number 
			// Value <trainingGraphFileName<tab>trainingGraphClass>
			/*Context is used to emit <key,value>
			*where key = <testGraphFilePath>
			*value=<trainingGraphClass-distanceGED(testGraph,trainingGraph)>
			*/

			Configuration conf = context.getConfiguration();
			int methodNumber = Integer.parseInt(conf.get("methodNumber"));
			


			

			double alpha=-1;
			double edgeCosts=-1;
			double nodeCosts=-1;
			int dataSetNumber = 0;
			
			boolean debug=false;
			
			String testGraphFileName="";
		    String trainingGraphFileName=""; // training's file name.
		    String trainingGraphClass=""; //training graph's class
		    String testGraphClass=""; //test graph's class
			XMLParser xmlParser = new XMLParser();
			Graph testGraph = null; //  test graph
			Graph trainingGraph = null; // training graph
		    String path=""; // the path where we can find both the test graphs and the training graphs
			
			//System.out.println("TEST PATH ::::"+testGraphPath);
			String trainingGraphPath; // path of training graphs
			String testGraphPath; // path of test graphs
			double distance = 0.0;
			StringTokenizer itr = new StringTokenizer(value.toString());
					
			// tokenizing the value parameter <trainingGraphFileName<tab>trainingGraphClass>
			while (itr.hasMoreTokens()) 
			{	

			    context.progress();
			//    System.out.println("progressing....");
			
				dataSetNumber = Integer.parseInt(itr.nextToken());
					
				
				 if(dataSetNumber ==1)
				{
					// Letter-MED
					nodeCosts=0.7;
					edgeCosts=1.9;
					alpha=0.75;
						
					Constants.costFunction = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
	
					path="/usr/local/IAM/Letter/MED/";
				}
				else if( dataSetNumber == 2)
				{
					// Letter-LOW
						
					nodeCosts=0.3;
					edgeCosts=0.1;
					alpha=0.3;
					Constants.costFunction = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					path="/usr/local/IAM/Letter/LOW/";
				}
				else if( dataSetNumber == 3)
				{
					// Letter-HIGH
					nodeCosts=0.9;
					edgeCosts=1.7;
					alpha=0.75;
					Constants.costFunction = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					path="/usr/local/IAM/Letter/HIGH/";	
				}
				else if( dataSetNumber == 4)
				{
					//GREC-CostFunction
					nodeCosts=90;
					edgeCosts=15;
					alpha=0.50;
					Constants.costFunction = new GRECCostFunction(nodeCosts, edgeCosts, alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					path="/usr/local/IAM/GREC/";
				}
				else if(dataSetNumber == 5)
				{
					//CoilCostFunction
					//propFile = "bin/properties/COILDEL.prop";
						
					nodeCosts=2.0;
					edgeCosts=2.0;
					alpha=0.50;
					Constants.costFunction = new CoilCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					path="/usr/local/IAM/COIL-DEL/";	
				}
				else if(dataSetNumber == 6)
				{
					//CoilCostFunction
					// COIL-RAG
					nodeCosts=2.0;
					edgeCosts=2.0;
					alpha=0.50;
					Constants.costFunction = new CoilCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					path="/usr/local/IAM/COIL-RAG/";	
				}
				else if (dataSetNumber == 7) 
				{
					// AIDS
					//MoleculesCostFunction 
					nodeCosts=1.1;
					edgeCosts=0.1;
					alpha=0.25;
						
					Constants.costFunction = new MoleculesCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
	
					path="/usr/local/IAM/AIDS/";	
				}
				else if (dataSetNumber == 8) 
				{
					// FingerPrint
					nodeCosts=0.7;
					edgeCosts=0.5;
					alpha=0.75;
						
					Constants.costFunction = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
	
					path="/usr/local/IAM/Fingerprint/";
						
				}
				else if (dataSetNumber == 9) 
				{
					//MutagenCostFunction
					// Letter-MED
					nodeCosts=0.11;
					edgeCosts=1.1;
					alpha=0.25;
					
					Constants.costFunction = new MutagenCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
	
					
					path="/usr/local/IAM/Mutagenicity/";
					
				}
				else if (dataSetNumber == 10) 
				{
					//WebCostFunction
					path = "/usr/local/IAM/Web/";
					Constants.costFunction = new WebCostFunction();
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					
				}
				else if (dataSetNumber == 11 || dataSetNumber == 12 || dataSetNumber == 13 || dataSetNumber == 14 ||dataSetNumber == 15 || dataSetNumber == 16 || dataSetNumber == 17 || dataSetNumber == 18 || dataSetNumber == 19 || dataSetNumber == 20) 
				{
					//WebCostFunction
					nodeCosts=0.7;
					edgeCosts=1.9;
					alpha=0.75;
					
					Constants.costFunction = new LetterCostFunction(nodeCosts,edgeCosts,alpha);
					Constants.edgeHandler = new UniversalEdgeHandler(); 
					
					if(dataSetNumber ==11)
					{
						path = "/usr/local/SyntheticGraphs/SG-5/";
					
					}
					else if (dataSetNumber == 12) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-10/";
						
					}
					else if (dataSetNumber == 13) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-15/";
						
					}
					else if (dataSetNumber == 14) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-20/";
						
					}
					else if (dataSetNumber == 15) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-25/";
						
					}
					else if (dataSetNumber == 16) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-30/";
						
					}
					else if (dataSetNumber == 17) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-35/";
						
					}
					else if (dataSetNumber == 18) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-40/";
						
					}
					else if (dataSetNumber == 19) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-45/";
						
					}
					else if (dataSetNumber == 20) 
					{
						//WebCostFunction
						path = "/usr/local/SyntheticGraphs/SG-50/";
						
					}
	
	
				}

				else 
				{
				
				}
				
					
				
			//	if (debug==true) System.out.println("****methodNumber ==== "+methodNumber);
				// getting testGraphFileName
				testGraphFileName = itr.nextToken();
				//if (debug==true) System.out.println("****testGraphFileName ==== "+testGraphFileName);
				
				
				testGraphPath=path+testGraphFileName;
				
				// getting trainingGraphFileName	
				trainingGraphFileName = itr.nextToken();
				//if (debug==true) System.out.println("****trainingGraphFileName ==== "+trainingGraphFileName);
				
				trainingGraphPath = path+trainingGraphFileName;
				try 
				{
					//parsing the training graph and the test graph
					testGraph = xmlParser.parseGXL(testGraphPath);
					trainingGraph= xmlParser.parseGXL(trainingGraphPath);
				} 
				catch (Exception e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//getting the class of the trainingGraph
				trainingGraphClass = itr.nextToken();
				
				testGraphClass = itr.nextToken();
				//System.out.println("graphFileClass:  "+trainingGraphClass+"");
					
				int jobNo = 1;
				
				//SET.StartChrono();
				PartialSearchTree DGED = new PartialSearchTree(testGraph,trainingGraph,Constants.costFunction,Constants.edgeHandler,jobNo,false,context);
						
					
				//----------------------------------------------------------------------
				
				
				// writng the upper bound in a file ....
				
				FSDataOutputStream out;
				Configuration config = new Configuration();
				FileSystem fs = FileSystem.get(config);
				String stt = DGED.pLocalMin.getSource().getId()+"-"+DGED.pLocalMin.getTarget().getId()+".txt";
				Path firstOptimalSolutionFile =  new Path("/home/hduser/data/LocalOptimalSolutions/"+stt);
				
				String firstOptimalSolution = dataSetNumber+"\t"+methodNumber+"\t"+testGraphClass+"\t"+trainingGraphClass+"\t"+DGED.upperBound+"\t"+DGED.pLocalMin.getSource().getId()+"\t"+DGED.pLocalMin.getTarget().getId();
				out = fs.create(firstOptimalSolutionFile);
				out.writeBytes(firstOptimalSolution);
				out.close();
				System.out.println("Finished writing.....");
				
				
				/////////////////////////////////////////////////////////////////////////////////
		
					
					
				for(int i=0;i<DGED.OPEN.size();i++)
				{
					context.progress();
					System.out.println("progressing....");
				    EditPath pp = DGED.OPEN.get(i);
				    String st = pp.toString();
				  // String toReducer= dataSetNumber+"\t"+methodNumber+"\t"+testGraphClass+"\t"+trainingGraphClass+"\t"+DGED.jobNo+"\t"+i+"\t"+st+"\t"+DGED.pmin.getTotalCosts();
				    String toReducer= dataSetNumber+"\t"+methodNumber+"\t"+testGraphClass+"\t"+trainingGraphClass+"\t"+st+"\t"+DGED.pLocalMin.getTotalCosts();
				    context.write(new Text(""), new Text(toReducer));		
				}
	
				/*emit key, value pair from mapper
				*where key = <testGraphFilePath>
				*value=<trainingGraphFileName-trainingGraphClass-distanceGED(testGraph,trainingGraph)>
				*/
					
			    }
					
			}
		}

	/**
	 * Reducer class for the classification problem
	 * 
	 * Input format :<testGraphFilePath, trainingGraphFileName-trainingGraphClass-distanceGED(testGraph,trainingGraph> 
	 * Output format :<testGraphFilePath: recommended class>
	 * 
	 * 
	 **/
	public static class HadoopDistributedGEDReducer extends
			Reducer<Text, Text, Text, Text> {

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context

		) throws IOException, InterruptedException {
          
			for (Text val : values) {
				
				context.write(key, new Text(val));
			}
		}
	}



	@Override
	public int run(String[] args) throws Exception {


		    Configuration conf = new Configuration();
		    conf.set("methodNumber", args[2]); // e.g. 5
		    
	        conf.set("mapred.jar","/usr/local/hadoop-1.0.4/project.jar");
	        conf.set("mapred.map.child.java.opts", "-Xmx1500M"); // assigning 1500MB per map

	        
	        conf.setInt("mapred.cluster.max.reduce.memory.mb", -1);
            conf.setInt("mapred.cluster.max.map.memory.mb", -1);
            
            conf.setInt("mapreduce.input.lineinputformat.linespermap", 1); // one line per map
            conf.setInt("mapred.tasktracker.reduce.tasks.maximum", 1); // one reduce
            
            conf.setInt("mapred.tasktracker.map.tasks.maximum", 1);

	        conf.setBoolean("mapred.map.tasks.speculative.execution", false);
	        conf.setBoolean("mapred.reduce.tasks.speculative.execution", false);
	        conf.setBoolean("mapred.compress.map.output", true);

	        
	        Job job = new Job(conf, "HadoopDistributedGED");

	        job.setInputFormatClass(NLineInputFormat.class); // one line per map
	        job.setMapperClass(HadoopDistributedGEDMapper.class);

	        job.setReducerClass(HadoopDistributedGEDReducer.class);
	        job.setOutputKeyClass(Text.class);
	        job.setOutputValueClass(Text.class);
	       
	        
	        FileInputFormat.addInputPath(job, new Path(args[0]));// specify the input path to be the first command-line argument passed by the user
	        FileOutputFormat.setOutputPath(job, new Path(args[1]));// specify the output path to be the second command-line argument passed by the user
	       // System.exit(job.waitForCompletion(true) ? 0 : 1);
	        return job.waitForCompletion(true) ? 0 : 1;// wait for the job to complete

		
	}

	public static void main(String[] args) throws Exception {

		final double start; // starting time
		final double end; // ending time
		

		File file = new File("/usr/local/hadoop-1.0.4/data/GraphDistance/"+args[2]+".txt");
		
		PrintStream ps = new PrintStream(file);


		start =System.currentTimeMillis();
		int job1 = ToolRunner.run(new Configuration(), new HadoopDistributedGED(), args);

		String[] str={"","",""};
		str[0]="/home/hduser/data/results1";
		str[1]="/home/hduser/data/output1";
	

		////////////////////////////////////////////////////////////////////////////
		Configuration config = new Configuration();
		FileSystem hdfs = FileSystem.get(config);
		Path path1 = new Path(args[1]);
		Path path2 = new Path("/home/hduser/data/results1");
		hdfs.rename(path1, path2);
		 
	    path1 = new Path(str[0]+"/part-r-00000");
	    path2 = new Path(str[0]+"/input.txt");

	    hdfs.rename(path1, path2);
	 
	    path2 = new Path(str[0]+"/_SUCCESS");		
	    hdfs.delete(path2, true);
		//////////////////////////////////////////////////////////////////////////
          
	    int i=2;
        int loop = 1;
        
    //    System.out.println("loop ....."+loop);
		while(loop==1)
		{
	
			str[0]="/home/hduser/data/results"+(i-1);
			str[1]="/home/hduser/data/output"+(i-1);
			if(i==2)
			{
				str[2]="line"; // first iteration of HadoopPartialSearchTree (one line per map)
			}
			else
			{
				str[2]=""; // other iterations have 64MB per map
			}
			
			System.out.println("***********************************generating results"+(i-1));
			
			int job1v1 = ToolRunner.run(new Configuration(), new HadoopPartialSearchTree(), str);
			
			// Deleting the input of HadoopPartialSearchTree after getting it's output...
			path1 = new Path(str[0]);
			if(hdfs.exists(path1))
			{
				hdfs.delete(path1,true);
			}
			
			
			str[0]="/home/hduser/data/results"+i;		
			path1 = new Path(str[1]);
		    path2 = new Path(str[0]);
		  
		    hdfs.rename(path1, path2);
		 
		    path1 = new Path(str[0]+"/part-r-00000");
		    path2 = new Path(str[0]+"/input.txt");

		    hdfs.rename(path1, path2);
		 
		    path2 = new Path(str[0]+"/_SUCCESS");		
		    hdfs.delete(path2, true);
		    
		    // check if input.txt is empty or not ...
		    path2 = new Path(str[0]+"/input.txt");
		    BufferedReader br=new BufferedReader(new InputStreamReader(hdfs.open(path2)));
		    
		    String line;
		    line=br.readLine(); 
		    br.close();
		    
		    if(line==null)
		    {
		    	loop=0;
		    }
		   
		    i++;
		}

		String[] str1={"",""};
		
		str1[0]="/home/hduser/data/LocalOptimalSolutions";
		str1[1]="/home/hduser/data/finalOutput";
		int job2 = ToolRunner.run(new Configuration(), new algorithms.DistributedGraphDistance(), str1);
		end =System.currentTimeMillis();
		ps.println(end-start);
		ps.close();
		System.out.println("FINISHED :::: TIME TAKEN TO CLASSIFY GRAPHS :: "+(end-start));
		System.exit(job2);
			
	}
}