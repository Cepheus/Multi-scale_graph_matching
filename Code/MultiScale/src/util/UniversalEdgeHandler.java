/**
 * 
 */
package util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;

import algorithms.Constants;

/**
 * @author Romain
 * 
 */
public class UniversalEdgeHandler implements IEdgeHandler {

	/**
	 * handles the edge operations - for detail information 
	 * @see masters thesis of k. riesen p.17ff
	 */
	public void handleEdges(EditPath p, GraphComponent u_start,
			GraphComponent u_end) {
		/**
		 * node deletion
		 */
		if (u_end.getComponentId().equals(Constants.EPS_ID)) {
			LinkedList edges = ((Node) u_start).getEdges();
			Node v;
			for (int i = 0; i < edges.size(); i++) {
				Edge e = (Edge) edges.get(i);
				v = e.getOtherEnd((Node) u_start);
				//si noeud cibe de g1 est d�ja utilis� alors on surpprime l'arc
				if (!p.getUnUsedNodes1().contains(v)) {
					p.addDistortion(e, Constants.EPS_COMPONENT);
				}
			}
		}
		/**
		 * node insertion
		 */
		if (u_start.getComponentId().equals(Constants.EPS_ID)) {
			LinkedList edges = ((Node) u_end).getEdges();
			Node v;
			for (int i = 0; i < edges.size(); i++) {
				Edge e = (Edge) edges.get(i);
				v = e.getOtherEnd((Node) u_end);
				//si noeud source est d�ja utilis� dans g2 alors on insert l'arc dans g1
				if (!p.getUnUsedNodes2().contains(v)) {
					p.addDistortion(Constants.EPS_COMPONENT, e);
				}
			}
		}
		/**
		 * node substitution
		 */
		if (!u_end.getComponentId().equals(Constants.EPS_ID)
				&& !u_start.getComponentId().equals(Constants.EPS_ID)) {
			
			//System.out.println("SUBSTITUTION CASE ............................................................");
			//on r�cup�re tous les arcs du noeud de G1
			LinkedList edges = ((Node) u_start).getEdges();
			GraphComponent v_start;
			GraphComponent v_end;
			//	pour tous les arcs du noeud de G1
			//System.out.println("Edges SIZE ("+u_start.getId()+")" + edges.size());
			for (int i = 0; i < edges.size(); i++){
				//on r�cup�re l'arc courant dans g1
				Edge e_start = (Edge) edges.get(i);
			//	System.out.println("e-start ::::"+e_start.getId());
				//on r�cup�re le noeud cible dans g1 de l'arc courant
				v_start = e_start.getOtherEnd((Node) u_start);
			//	System.out.println("Get the other end (v-start)  ::::"+v_start.getId());
				//Si la distortion sur  les noeuds a d�ja �t� faite alors on peut s'attaquer aux arcs 
				
				  Enumeration enumeration = p.getDistortions().keys();
					 
				  

				    int containsKey=0;
					GraphComponent gc=new GraphComponent();
					while (enumeration.hasMoreElements())
					{
						GraphComponent key = (GraphComponent) enumeration.nextElement();
						GraphComponent value = (GraphComponent) p.getDistortions().get(key);
						//System.out.println("Distortions"+key.getId()+" , "+value.getId());
						if(v_start.getId().equals(key.getId()))
						{
							
							//get the other end;
							gc = (GraphComponent) p.getDistortions().get(key);
							containsKey=1;
				//			System.out.println("FOUND :: "+gc.getId());
							break;
						}
						
					
					}
					
					if (containsKey==1){
					//System.out.println("the distortion of "+v_start.getId()+ " is found ....");
					//on r�cup�re le noeud appareill� dans g2 (vstart est apparaill� avec vend).
					//vend est un noeud de g2		System.out.println("("+d1+"-"+d2+")"+Math.abs(d1-d2));
				//	v_end = (GraphComponent) p.getDistortions().get(v_start);
					v_end=gc;
					//System.out.println("v_end ::"+v_end.getId());
					//si le noeud vend  de g2 doit �tre d�truit alors on d�truit l'arc aussi
					if (v_end.getComponentId().equals(Constants.EPS_ID)){
						//System.out.println("**Adding Distortion ("+v_start.getId()+",eps)");
						p.addDistortion(e_start, Constants.EPS_COMPONENT);
					} else {
						//si le noeud vend de g2  doit �tre substitu� alors l'arc aussi
						//on r�cup�re l'arc entre les deux noeuds de g2 uend et vend
						Edge e_end;
						e_end = getEdgeBetween((Node) u_end, v_end);	
						//System.out.println("Getting the edge (e_end) between u_end and v_end where u_end ="+u_end.getId()+" and v_end = "+v_end.getId());
						
						if (e_end != null){
							//System.out.println("e_end  :::"+e_end.getId());
							if(((Node) u_end).isDirected() == true){
								boolean gooddirection =AreEdgesTheSameDirection(e_start,e_end,(Node)u_start,(Node)v_start,(Node)u_end,(Node)v_end);
								e_start.setInverted(!gooddirection);
								e_end.setInverted(!gooddirection);
							}
							
							
							p.addDistortion(e_start, e_end);
						} else {
							//Il n'y a pas d'arc entre les noeud du graphe g2 alors on supprime l'arc dans g1
							//entre uend et vend il n'y a pas d'arc alors en surpprime l'arc entre ustart et vstart
							//System.out.println("e_end = null Thus:");
							//System.out.println("Add-Distortion ( "+e_start+",eps)");
							p.addDistortion(e_start, Constants.EPS_COMPONENT);
						}

					}
				}
			}

			//on r�cup�re tous les arcs du noeud de G2
			edges = ((Node) u_end).getEdges();
			//System.out.println("Edges SIZE ("+u_end.getId()+")" + edges.size());
			Edge e_start;
			//	pour tous les arcs du noeud de G2
			for (int i = 0; i < edges.size(); i++){
				//on r�cup�re l'arc courant dans g2
				Edge e_end = (Edge) edges.get(i);
				//System.out.println("e-end ::::"+e_end.getId());
				//on r�cup�re le noeud cible dans g2
				v_end = e_end.getOtherEnd((Node) u_end);
				//System.out.println("Get the other end (v-end)  ::::"+v_end.getId());
				//on regarde si le noeud cible a �t� appareill� avec un noeud de un noeud de g1
				
				
				Enumeration enumeration = p.getDistortions().keys();
				GraphComponent key = new GraphComponent();
				GraphComponent value = new GraphComponent();
				 
			    int containsKey=0;
				GraphComponent gc=new GraphComponent();
				while (enumeration.hasMoreElements())
				{
					key = (GraphComponent) enumeration.nextElement();
					value = (GraphComponent) p.getDistortions().get(key);
					//System.out.println("Distortions"+key.getId()+" , "+value.getId());
					if(v_end.getId().equals(value.getId()))
					{
						
						//get the other end;
						gc = (GraphComponent) p.getDistortions().get(key);
						containsKey=1;
					//	System.out.println("FOUND :: "+gc.getId());
						break;
					}
					
				
				}
				
				
				
				//if (p.getDistortions().containsValue(v_end))
				if (containsKey==1){
					//System.out.println("the distortion of "+v_end.getId()+ " is found ....");
					//on r�cup�re le noeud appareill� de g1
					//annuaire invers� des distortions
					//v_start = (GraphComponent) p.getStart(v_end);
					v_start =  key;
					//System.out.println("v_start ::"+v_start.getId());
					//on r�cup�re l'arc du noeud de g1
				    e_start = getEdgeBetween((Node) u_start, v_start);
				    /*if (e_start != null)
				    {
				    	System.out.println("Getting the edge (e_start) between u_start and v_start where u_start ="+u_start.getId()+" and v_start = "+v_start.getId());
				    }
				    */
					//si il n'y a pas d'arc entre les deux noeuds de g1
					//entre vend et uend il y a une arc mais entre ustart et vstart ll n'y en pas 
					//alors on ins�re un arc entre vstart et ustart
					if (e_start == null){
						//on fait une insertion d'un arc dans g1.
						//System.out.println("e_start equal null, Thus");
						//System.out.println("Add Distortion (eps,"+e_end.getId()+")");
						p.addDistortion(Constants.EPS_COMPONENT, e_end);
					}



				}
			}
		
		}
	}

	private boolean AreEdgesTheSameDirection(Edge e_start, Edge e_end,
			GraphComponent u_start, Node v_start,
			GraphComponent u_end, Node v_end) {
		// TODO Auto-generated method stub
		if(e_start !=null && e_end !=null){
			if(e_start.getStartNode()==v_start && e_end.getStartNode() == v_end) return true;
		}
		return false;
	}

	private Edge getDirectedEdgeBetween(Node n1, GraphComponent n2) {
		// TODO Auto-generated method stub
		Iterator iter = n1.getEdges().iterator();
		Node temp;
		while (iter.hasNext()){
			Edge e = (Edge) iter.next();
			temp =e.getStartNode();
			//temp = e.getOtherEnd(n1);
			if (temp.equals(n2)){
				return e;
			}
		}
		return null;
	}

	/**
	 * @return the egde between two nodes: @param n1
	 * @param n2 
	 */
	private Edge getEdgeBetween(Node n1, GraphComponent n2) {
		Iterator iter = n1.getEdges().iterator();
		Node temp;
		while (iter.hasNext()){
			Edge e = (Edge) iter.next();
			temp = e.getOtherEnd(n1);
			//if (temp.equals(n2)){
			//	return e;
			//}
			if(temp.getId().equals(n2.getId()))
			{
				return e;
			}
				
		}
		return null;
	}



}