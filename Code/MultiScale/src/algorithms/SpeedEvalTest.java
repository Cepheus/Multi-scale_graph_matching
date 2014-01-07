package algorithms;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Iterator;

import util.CpuMonitorWindows;
import util.Edge;
import util.Graph;
import util.GraphComponent;
import util.ICostFunction;
import util.Node;
import util.VectorCostFunction;
import xml.XMLParser;

public class SpeedEvalTest {

	SpeedEvalTest(){
	
		
	}
	
	SpeedEvalTest(String file){
		try {
			 ps = new PrintStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private long start;
	private long end;
	private PrintStream ps;
	private Constants constants;

	
	double cpuUserAvant;
	double cpuKernelAvant;
	double wctAvant;

	
	public Graph ReadGraph(String f) throws Exception{

		XMLParser xmlParser = new XMLParser();
		Graph g1 = xmlParser.parseGXL(f);
		return g1;
	}


	public  Graph SyntheticLinearGraph (int nbNoeud)
	{
		Graph graph = new Graph();
		graph.setEdgeMode("undirected");
		//Ajout des noeuds au graphe
		for (int i = 0; i < nbNoeud; i++) {

			Node node = new Node("undirected");
			node.setId(""+i);

			graph.add (node);
		}


		for (int i = 0; i < nbNoeud; i++) {
			if (i != 0) {
				//labelEdge
				Node ns = (Node) graph.get(i-1);
				Node nt = (Node) graph.get(i);
				Edge edge = new Edge("undirected");
				edge.setStartNode(ns);
				edge.setEndNode(nt);
				edge.setId(""+(i-1)+"_"+i);
				ns.getEdges().add(edge);
				nt.getEdges().add(edge);
				graph.getEdges().add(edge);

			}
		}

		return graph;
	}

    // Zeina: complete graph means each node is connected to all nodes in the graph
	public  Graph SyntheticCompleteGraph (int nbNoeud)
	{
		Graph graph = new Graph();

		//Ajout des noeuds au graphe
		for (int i = 0; i < nbNoeud; i++) 
		{

			Node node = new Node("undirected");
			node.setId(""+i);

			graph.add(node);
		}

		
		for (int i = 0; i < nbNoeud; i++) {
			Node ns = (Node) graph.get(i);
			for (int j = 0; j < nbNoeud; j++) {
				if (i != j) {
					//labelEdge
					
					Node nt = (Node) graph.get(j);
					Edge edge = new Edge("undirected");
					edge.setStartNode(ns);
					edge.setEndNode(nt);
					edge.setId(""+(i-1)+"_"+i);
					ns.getEdges().add(edge);
					nt.getEdges().add(edge);
					graph.getEdges().add(edge);

				}
			}
		}

		return graph;
	}




	public  Graph SyntheticLinearLabelGraph (int nbNoeud)
	{
		Graph graph = new Graph();

		//Ajout des noeuds au graphe
		for (int i = 0; i < nbNoeud; i++) {

			Node node = new Node("undirected");
			node.setId(""+i);
			node.put("val", ""+0.1);
			graph.add (node);
		}


		for (int i = 0; i < nbNoeud; i++) {
			if (i != 0) {
				//labelEdge
				Node ns = (Node) graph.get(i-1);
				Node nt = (Node) graph.get(i);
				Edge edge = new Edge("undirected");
				edge.setStartNode(ns);
				edge.setEndNode(nt);
				edge.setId("");
				edge.put("val", ""+0.1);
				ns.getEdges().add(edge);
				graph.getEdges().add(edge);

			}
		}

		return graph;
	}

	public void StartChrono ()
	{
		//stopWatch = new Stopwatch ();

		//stopWatch.Start ();
		start =System.currentTimeMillis();
	}


	public void StopChrono ()
	{

		end =System.currentTimeMillis();
	}

	public void DisplayElapsedTime ()
	{

		System.out.println("Total elapsed time in execution of  method callMethod() is :"+ (end-start));
	}

	public void WriteElapsedTime ()
	{

		ps.println("Total elapsed time in execution of  method callMethod() is :"+ (end-start));
	}


	public  void TestSyntheticGraph () throws FileNotFoundException
	{

		for (int j=1000; j<=10000; j=j+1000) {
			ps = new PrintStream("speed" + j + ".txt");
			for (int i=0; i<100; i++) {
				this.StartChrono ();
				for (int k=0; k<100; k++) {
					this.SyntheticLinearGraph (j);
				}
				this.StopChrono ();
				this.DisplayElapsedTime ();
				this.WriteElapsedTime ();
			}
			// close the stream
			ps.close ();
		}
	}
	
	public void CloseStream(){
		// close the stream
		ps.close ();
	}


	public  double ComputeSumOfLabels (Graph g)
	{
		double SOD = 0;
		//DoubleLabel labelzero = new DoubleLabel (0.0);
		GraphComponent GC = new GraphComponent();
		GC.setId("de");

		GC.put("val", ""+0.1);
		Iterator it = g.iterator();
		while(it.hasNext()==true){
			Node n1=(Node) it.next();
			GC.setNode(true);
			SOD += constants.costFunction.getCosts(n1,GC);

			Iterator it1 = n1.getEdges().iterator();
			while(it1.hasNext()==true){
				Edge e1=(Edge) it1.next();
				GC.setNode(false);

				SOD += constants.costFunction.getCosts(e1,GC);

			}


		}
		return SOD;
	}


	public  void TestDissimilarityCall () throws FileNotFoundException
	{
		constants = new Constants(1,1,0.5);
		constants.costFunction = (ICostFunction)new VectorCostFunction(1,1,0.5); // TODO 1
		for (int j=1000; j<=10000; j=j+1000) {
			ps = new PrintStream("speed" + j + ".txt");
			Graph g = this.SyntheticLinearLabelGraph (j);
			for (int i=0; i<100; i++) {
				this.StartChrono ();
				for (int k=0; k<1000; k++) {
					//this.SyntheticCompleteGraph (j);
					this.ComputeSumOfLabels (g);
				}
				this.StopChrono ();
				this.DisplayElapsedTime ();
				this.WriteElapsedTime ();
			}

			// close the stream
			ps.close ();
		}
	}

	public static void main(String[] args) throws NumberFormatException, Exception{
		SpeedEvalTest SET = new SpeedEvalTest();
		//SET.TestSyntheticGraph();
		SET.TestDissimilarityCall ();
	}
	
	
	public void SetUpTimeSensors() {
		// TODO Auto-generated method stub
		// instrumentation Avant
		cpuUserAvant = CpuMonitorWindows.getUserTime();
		cpuKernelAvant = CpuMonitorWindows.getKernelTime();
		wctAvant = System.currentTimeMillis();
		// fin instrumentation Avant
	}
	
	public PrintStream GetPrintStream()
	{
		return ps;
	}
	
	public void CpuDisplay(int noOFNodes) {
		// TODO Auto-generated method stub
		// instrumentation Après
		double cpuUserApres = CpuMonitorWindows.getUserTime();
		double cpuKernelApres = CpuMonitorWindows.getKernelTime();
		long wctApres = System.currentTimeMillis();
		// fin de l'instrumentation Après

		
		// affichage des résultats de l'instrumentation
		double cpuUser = cpuUserApres - cpuUserAvant;
		double cpuKernel = cpuKernelApres - cpuKernelAvant;
		double wct = wctApres - wctAvant;
		wct = wct/1000; // passage de wct de millisec en secondes
		ps.println("( "+noOFNodes+" )");
		ps.println("cpu time in user space   : "
				   + cpuUser + " sec");
	     
		ps.println("cpu time in kernel space : "
				   + cpuKernel + " sec");
	     //  ps.append("\n");
		ps.println("total cpu time           : "
				   + (cpuUser+cpuKernel) + " sec");
	//        ps.append("\n");
		ps.println("wall clock time          : " 
				   + wct + " sec");
	//	ps.append("\n");
		// precision de wct : millisecondes
		// precision de cpu : intervalles de 10 nanosecondes
		// on ne garde que les 3 premières décimales des secondes pour la différence
		double diff = wct - (cpuUser+cpuKernel);

		ps.println("wall clock - cpu        =  "
				   + diff + " sec = "
				   + (100 * diff / wct) + " %");
		ps.println("\n");
	}

	public double ElapsedTimeToString() {
		// TODO Auto-generated method stub
		return (end-start);
	}
	

}
