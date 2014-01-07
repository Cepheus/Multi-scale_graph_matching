/**
 * 
 */
package util;

import java.util.Iterator;
import java.util.LinkedList;

import algorithms.Constants;

/**
 * @author kriesen
 * 
 */
public class UnDirectedEdgeHandler implements IEdgeHandler {

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
				//si noeud cibe de g1 est déja utilisé alors on surpprime l'arc
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
				//si noeud source est déja utilisé dans g2 alors on insert l'arc dans g1
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
			//on récupère tous les arcs du noeud de G1
			LinkedList edges = ((Node) u_start).getEdges();
			GraphComponent v_start;
			GraphComponent v_end;
			//	pour tous les arcs du noeud de G1
			for (int i = 0; i < edges.size(); i++){
				//on récupère l'arc courant dans g1
				Edge e_start = (Edge) edges.get(i);
				//on récupère le noeud cible dans g1 de l'arc courant
				v_start = e_start.getOtherEnd((Node) u_start);
				//Si la distortion sur  les noeuds a déja été faite alors on peut s'attaquer aux arcs 
				if (p.getDistortions().containsKey(v_start)){
					//on récupère le noeud appareillé dans g2 (vstart est apparaillé avec vend).
					//vend est un noeud de g2
					v_end = (GraphComponent) p.getDistortions().get(v_start);
					//si le noeud vend  de g2 doit être détruit alors on détruit l'arc aussi
					if (v_end.getComponentId().equals(Constants.EPS_ID)){
						p.addDistortion(e_start, Constants.EPS_COMPONENT);
					} else {
						//si le noeud vend de g2  doit être substitué alors l'arc aussi
						//on récupère l'arc entre les deux noeuds de g2 uend et vend
						Edge e_end = getEdgeBetween((Node) u_end, v_end);
						if (e_end != null){
							p.addDistortion(e_start, e_end);
						} else {
							//Il n'y a pas d'arc entre les noeud du graphe g2 alors on supprime l'arc dans g1
							//entre uend et vend il n'y a pas d'arc alors en surpprime l'arc entre ustart et vstart
							p.addDistortion(e_start, Constants.EPS_COMPONENT);
						}
					}
				}
			}
			
			//on récupère tous les arcs du noeud de G2
			edges = ((Node) u_end).getEdges();
			Edge e_start;
			//	pour tous les arcs du noeud de G2
			for (int i = 0; i < edges.size(); i++){
				//on récupère l'arc courant dans g2
				Edge e_end = (Edge) edges.get(i);
				//on récupère le noeud cible dans g2
				v_end = e_end.getOtherEnd((Node) u_end);
				//on regarde si le noeud cible a été appareillé avec un noeud de un noeud de g1
				if (p.getDistortions().containsValue(v_end)){
					//on récupère le noeud appareillé de g1
					//annuaire inversé des distortions
					v_start = (GraphComponent) p.getStart(v_end);
					//on récupère l'arc du noeud de g1
					e_start = getEdgeBetween((Node) u_start, v_start);
					
					//si il n'y a pas d'arc entre les deux noeuds de g1
					//entre vend et uend il y a une arc mais entre ustart et vstart ll n'y en pas 
					//alors on insère un arc entre vstart et ustart
					if (e_start == null){
						//on fait une insertion d'un arc dans g1.
						p.addDistortion(Constants.EPS_COMPONENT, e_end);
					}
				}
			}
				
		}
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
			if (temp.equals(n2)){
				return e;
			}
		}
		return null;
	}
	
	

}
