package algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import util.EditPath;
import util.Graph;
import util.ICostFunction;
import util.IEdgeHandler;
import util.LetterCostFunction;
import util.Node;
import util.UnDirectedEdgeHandler;
import xml.XMLParser;

public class LowerUpperBoundsGED {

	private boolean debug;
	private Graph G1;
	private Graph G2;
	private int heuristicmethod;
	EditPath BestEditpath;
	private ArrayList<EditPath> OPEN;
	private Node CurNode;
	private int nbexlporednode;
	private int maxopensize;
	private double UBCOST;
	private MatrixGenerator mgen;
	private Munkres munkres;
	private MunkresRec munkresRec;
	private EditPath UB;

	static public int NoHeuristic=0;
	static public int NoAssigmentHeuristic=1;
	static public int MunkresAssigmentHeuristic=2;
	static public int LAPAssigmentHeuristic=3;

	public LowerUpperBoundsGED(Graph G1, Graph G2,ICostFunction costfunction, IEdgeHandler edgehandler,int heuristicmethod,boolean debug){

		this.debug = debug;
		this.G1 =G1;
		this.G2=G2;
		this.heuristicmethod=heuristicmethod;
		int G2NbNodes = G2.size(); // Number of nodes of G2
		BestEditpath=null; // In the beginning, the list is empty. 
		//La liste OPEN est initialisée à VIDE. OPEN est une liste de chemin
		//The set open contains the search tree nodes to be processed in the next steps
		OPEN = new ArrayList<EditPath>();

		init();

		BestEditpath = loop();
	}

	private void init() {
		// TODO Auto-generated method stub
		EditPath ROOT = new EditPath(this.G1,this.G2,this.heuristicmethod);
		OPEN.add(ROOT);

		nbexlporednode=0;
		maxopensize=0;
		this.UBCOST=Double.MAX_VALUE;
		//UB=ROOT;

		UB = ComputeUpperBound(0);
	}

	private EditPath ComputeUpperBound(int upperboundmethod) {
		// TODO Auto-generated method stub

		EditPath res=null;
		if(upperboundmethod == 0){
			mgen = new MatrixGenerator();
			munkres = new Munkres();
			munkresRec = new MunkresRec();
			mgen.setMunkres(munkresRec);

			double[][] matrix = mgen.getMatrix(this.G1, G2);
			munkres.setGraphs(G1, G2);
			UBCOST = munkres.getCosts(matrix);
			res = munkres.ApproximatedEditPath();
			//EditPath ROOT = new EditPath(this.G1,this.G2,this.heuristicmethod); 
			//res = this.dummyUpperBound(ROOT);
			UBCOST = res.getTotalCosts();

		}
		
		if(upperboundmethod == 1){
			EditPath ROOT = new EditPath(this.G1,this.G2,this.heuristicmethod); 
			res = this.dummyUpperBound(ROOT);
			UBCOST = res.getTotalCosts();

		}
		return res;
	}

	private EditPath loop() {
		// TODO Auto-generated method stub

		while(true == true){

			if(OPEN.isEmpty() == true){
				//System.out.println("Error : No more candidates, no complete solution could be found");
				//System.out.println("Error : Please check your graph data");
				return UB;
			}

			//On cherche dans OPEN le chemin avec le cout minimum (Pmin) et on l'enléve de OPEN
			EditPath pmin =findmincostEditPathWithHeuristic(OPEN);

			OPEN.remove(pmin);

			if(pmin.isComplete() == true){
				return pmin;
			}else{
				this.UpdateUpperBound(pmin);
				// K est l'index du noeud de G1 en cours de traintement. Premier courp, k=1. 
				// Si on a traité  k < taille de G1

				if(pmin.getUnUsedNodes1().size() > 0){;
				this.CurNode=pmin.getNext();

				// pour tous les noeuds (w)  de V2  qui ne sont pas encore utilisés dans Pmin. on ajoute dans pmin toutes les substitutions de uk+1 avec w.
				LinkedList<Node>UnUsedNodes2= pmin.getUnUsedNodes2();
				for(int i=0;i<UnUsedNodes2.size();i++){
					EditPath newpath = new EditPath(pmin);

					Node w = UnUsedNodes2.get(i);
					newpath.addDistortion(this.CurNode, w);
					AddPath(newpath);	

				}
				// On met dans pmin une suppresion de uk+1.
				EditPath newpath = new EditPath(pmin);
				newpath.addDistortion(this.CurNode, Constants.EPS_COMPONENT);
				AddPath(newpath);	

				}else{
					//Sinon si k= taille de G1
					//On met dans pmin toutes les insertions des noeuds  de V2  qui ne sont pas encore utilisés dans Pmin. 
					//On remet Pmin dans OPEN
					EditPath newpath = new EditPath(pmin);
					newpath.complete();	
					AddPath(newpath);	
					
					if(newpath.getTotalCosts() < this.UBCOST){
						this.UB = newpath;
						this.UBCOST = UB.getTotalCosts();
					}
					

				}//fin si k<(this.G1NbNodes-1)
			}//fin si chemin optimal

		}//boucle while
	}//fin méthode loop




	private void UpdateUpperBound(EditPath pmin) {
		// TODO Auto-generated method stub
		EditPath tmpub = this.dummyUpperBound(pmin);
		if(tmpub.getTotalCosts() < this.UBCOST){
			this.UB = tmpub;
			this.UBCOST = UB.getTotalCosts();
		}

	}

	private void AddPath(EditPath newpath) {
		// TODO Auto-generated method stub
		double h = newpath.ComputeHeuristicCosts(this.heuristicmethod);
		double g = newpath.getTotalCosts();
		double f = h+g;
		if(f<UBCOST){
			this.OPEN.add(newpath);
		}

	}

	//A function that returns the minimal edit path's cost in the set OPEN
	private EditPath findmincostEditPathWithHeuristic(ArrayList<EditPath> oPEN2) {
		// TODO Auto-generated method stub
		int i=0;
		int nbpaths = OPEN.size();
		double minvalue = Double.MAX_VALUE;
		double h=0.0,g=0.0;
		int indexmin = -1;

		for(i=0;i<nbpaths;i++){
			EditPath p = OPEN.get(i);
			g = p.getTotalCosts(); // computing the cost of the edit path from the root till the current node...
			h=p.ComputeHeuristicCosts(this.heuristicmethod);


			if((g+h) <minvalue){
				minvalue = g+h;
				indexmin = i;
			}
		}

		//debug part
		this.nbexlporednode++;
		maxopensize = Math.max(this.OPEN.size(), this.maxopensize);
		//end debug part

		return OPEN.get(indexmin);
	}

	private EditPath dummyUpperBound(EditPath p) {
		EditPath ptmp=null;
		if(p.isComplete() == false){
			ptmp = new EditPath(p);
			// TODO Auto-generated method stub
			int G1NbNodes = ptmp.getUnUsedNodes1().size();
			int G2NbNodes = ptmp.getUnUsedNodes2().size();
			// Substitution case
			//System.out.println("Substitution phase");
			for(int i=0;i<G1NbNodes  ;i++)
			{

				Node u = (Node) ptmp.getUnUsedNodes1().getFirst();
				if(i<G2NbNodes) {
					Node v = (Node) ptmp.getUnUsedNodes2().getFirst();
					ptmp.addDistortion(u, v);
				}

			}

			//Deletion case
			if(G1NbNodes > G2NbNodes)
			{
				//System.out.println("Deletion phase");
				int noOfDeletedNodes = G1NbNodes- G2NbNodes; 
				for(int i=0;i<noOfDeletedNodes;i++)
				{

					Node u = ptmp.getNext();
					ptmp.addDistortion( u , Constants.EPS_COMPONENT);

				}
			}

			//Insertion case
			else if(G1NbNodes < G2NbNodes)
			{
				//System.out.println("Insertion phase");
				int noOfInsertedNodes = G2NbNodes- G1NbNodes; 
				for(int i=0;i<noOfInsertedNodes;i++)
				{
					Node u = ptmp.getNextG2();
					ptmp.addDistortion( Constants.EPS_COMPONENT , u );	
				}
			}
		}else{
			return p;
		}

		return ptmp;
	}




	public int getNbExploredNode() {
		// TODO Auto-generated method stub
		return this.nbexlporednode;
	}

	public int getMaxSizeOpen() {
		// TODO Auto-generated method stub
		return this.maxopensize;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		t4();
	}

	private static FilenameFilter gxlFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".gxl");
		}
	};

	private static void t4()
	{
		Constants.costFunction = (ICostFunction)new LetterCostFunction(1,1,1); 
		Constants.edgeHandler = new UnDirectedEdgeHandler();

		File repertoire = new File("./data/test");
		File[] files=repertoire.listFiles(gxlFileFilter);
		//Graph[] graphTab = new Graph[files.length];

		if(!repertoire.exists()){
			System.out.println("Le répertoire n'existe pas");
		}

		//		XMLParser xmlParser = new XMLParser();
		//		try
		//		{
		//			for(int i=0;i<files.length;i++)
		//			{	
		//				//graphTab[i] = xmlParser.parseGXL(files[i].toString());
		//				System.out.println(files[i].toString());
		//			}
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		try {
			SpeedEvalTest SET = new SpeedEvalTest("gedspeed" +100 + ".txt");
			FileWriter fw = new FileWriter("./data/testjavaxy.csv", true);
			BufferedWriter output = new BufferedWriter(fw);
			output.write("Graph1;Graph2;Cout;Temps\n");
			Graph g2,g1;
			g2=g1=null;
			for(int i=0;i<files.length;i++)
			{
				try {
					XMLParser xmlParser = new XMLParser();
					g1 =  xmlParser.parseGXL(files[i].toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int j=0;j<files.length;j++)//graphTab.length;j++)
				{	
					try {
						XMLParser xmlParser = new XMLParser();
						g2 =  xmlParser.parseGXL(files[j].toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					output.write(g1.getId() + ";" + g2.getId());
					SET.StartChrono();
					LowerUpperBoundsGED HGED = new LowerUpperBoundsGED(g1,g2,Constants.costFunction,Constants.edgeHandler,LowerUpperBoundsGED.MunkresAssigmentHeuristic,false);
					SET.StopChrono();
					output.write(";"+HGED.getBestEditpath().getTotalCosts()+";"+SET.ElapsedTimeToString());
					System.out.println(i + " " + j );
					output.write("\n");
				}
			}
			output.flush();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public EditPath getBestEditpath() {
		// TODO Auto-generated method stub
		return this.BestEditpath;
	}

}
