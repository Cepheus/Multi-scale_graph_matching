package util;
import java.util.Arrays;
import java.lang.Comparable;


public class PairGraphDistance implements Comparable {
	Graph graph;
	double cost;
	
	public PairGraphDistance(Graph graph, double cost){
		this.graph = graph;
		this.cost = cost;
		
	}
	
	public Graph getGraph(){
		return graph;
	}
	
	public double getdist(){
		return cost;
	}
	
	
	public int compareTo(Object other) {
		// TODO Auto-generated method stub
	    if (!(other instanceof PairGraphDistance))
	        throw new ClassCastException("A Graph object expected.");
		  double nombre1 = ((PairGraphDistance) other).getdist(); 
	      double nombre2 = this.getdist(); 
	      if (nombre1 > nombre2)  return -1; 
	      else if(nombre1 == nombre2) return 0; 
	      else return 1; 
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}