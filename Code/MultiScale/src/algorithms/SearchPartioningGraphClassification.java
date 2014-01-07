package algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import util.EditPath;
import util.Graph;
import util.GraphCollection;
import util.ICostFunction;
import util.IEdgeHandler;
import util.Node;

public class SearchPartioningGraphClassification {
	
	private Graph query;
	private GraphCollection dataset;
	
	private double UBCOST;
	private MatrixGenerator mgen;
	private Munkres munkres;
	private MunkresRec munkresRec;
	private EditPath UB;
	private ICostFunction costfunction;
	private IEdgeHandler edgehandler;
	private int heuristicmethod;
	private boolean debug;

	public SearchPartioningGraphClassification(Graph query, GraphCollection dataset, ICostFunction costfunction, IEdgeHandler edgehandler,int heuristicmethod,boolean debug){
		this.query = query;
		
		this.dataset = dataset;
		
		this.costfunction=costfunction;
		this.edgehandler=edgehandler;
		this.heuristicmethod=heuristicmethod;
		this.debug =debug;
		
	}
	boolean run() throws IOException{
		BufferedWriter BW;
		BufferedReader BR;
		FileWriter FW;
		FileReader FR;
		
		FW = new FileWriter(new File("tmpr.txt")); 
		BW = new BufferedWriter(FW);
		UB=init(BW);
		UBCOST = UB.getTotalCosts();
		BW.close();
		FR = new FileReader(new File("tmpr.txt")); 
		BR = new BufferedReader(FR);
		FW = new FileWriter(new File("tmpw.txt")); 
		BW = new BufferedWriter(FW);
		
		boolean flag=true;
		while(flag==true){
			
			flag =false;
			String line;
			//boolean finalres=false;
			while(((line = BR.readLine()) != null) ){
				EditPath ED = new EditPath(line);
				PathExtender(ED,BW,100);
				flag = true;
			}
			BW.close();
			BR.close();
			
			Path source =new File("tmpw.txt").toPath();
			Path dest = new File("tmpr.txt").toPath();
			
			Files.copy( source, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			FR = new FileReader(new File("tmpr.txt")); 
			BR = new BufferedReader(FR);
			FW = new FileWriter(new File("tmpw.txt")); 
			BW = new BufferedWriter(FW);
		}
		return true;
	}
	
	
	
	private boolean PathExtender(EditPath ED, BufferedWriter BW,
			 int maxed) {
		// TODO Auto-generated method stub
		EditPathExtender EPE = new EditPathExtender(ED,this.UB,this.UBCOST,this.costfunction,this.edgehandler,this.heuristicmethod,maxed,BW,this.debug);
		if(this.UBCOST>EPE.getBestEditpath().getTotalCosts()){
			this.UBCOST = EPE.getBestEditpath().getTotalCosts();
			this.UB = EPE.getBestEditpath();
		}
		
		return false;
	}
	private EditPath ComputeUpperBound(int upperboundmethod, Graph G1, Graph G2) {
		// TODO Auto-generated method stub

		EditPath res=null;
		if(upperboundmethod == 0){
			mgen = new MatrixGenerator();
			munkres = new Munkres();
			munkresRec = new MunkresRec();
			mgen.setMunkres(munkresRec);

			double[][] matrix = mgen.getMatrix(G1, G2);
			munkres.setGraphs(G1, G2);
			UBCOST = munkres.getCosts(matrix);
			res = munkres.ApproximatedEditPath();
			res.complete();
			UBCOST = res.getTotalCosts();

		}
		return res;
	}

	private EditPath init(BufferedWriter bW) throws IOException {
		// TODO Auto-generated method stub
		double min = Double.MAX_VALUE;
		EditPath mined = null;
		for(Graph g:(LinkedList<Graph>)this.dataset){
			EditPath ED = new EditPath(query,g);
			bW.write(ED.toString()+"\n");
			EditPath tmp=this.ComputeUpperBound(0, query, g);
			if(tmp.getTotalCosts() <min){
				min = tmp.getTotalCosts();
				mined = tmp;
			}
		}
		bW.flush();
		return mined;
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
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
