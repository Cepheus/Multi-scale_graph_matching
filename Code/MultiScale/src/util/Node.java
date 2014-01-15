package util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author   kriesen
 */
public class Node extends GraphComponent implements  java.io.Serializable{
	
	/** the edges that belong to this node */
	private LinkedList edges;
	/** The name of communities the node belongs to */
	private LinkedList<String> communities;


	/** modes: directed, undirected */
	private boolean isDirected=false;
	
	/** Is the node a community. */
	private boolean isCommunity = false;
	
	/** The scale of the node. If the node is a community the value will be different from 0. */
	private int scale = 0;

	public boolean isDirected() {
		return isDirected;
	}
	
	public boolean isCommunity() {
		return isCommunity();
	}
	
	public int getScale() {
		return scale;
	}

	public void setDirected(boolean isDirected) {
		this.isDirected = isDirected;
	}
	
	/**
	 * Declare the node as a community.
	 * @param scale The scale of the community.
	 */
	public void isCommunity(int scale) {
		this.isCommunity = true;
		this.scale = scale;
	}

	public Node() {
		super();
	}

	/**
	 * the constructor
	 */
	public Node(String mode){
		super();
		super.setNode(true);
	
		if(mode.equals("directed")==true){
			this.isDirected = true;
		}
		
		this.edges = new LinkedList();
		this.communities = new LinkedList<String>();
		
	}
	
	public Object clone() {
		// Deep copy
		Node node = new Node();
		node.isDirected = isDirected;
		node.setComponentId(getComponentId());
		node.setNode(isNode());
		node.setTable(getTable());
		node.isCommunity(scale);
		node.communities = new LinkedList<String>();
		for (String s : communities) {
			node.communities.add(s);
		}
		node.edges = new LinkedList();
		return node;
	}
	
	
	/**
	 * New constructor
	 * 
	 * Parsing a string a node
	 * 
	 * String Format = nodeId:x=value1,y=value2, etc.
	 * 
	 * @param str: a string to be parsed
	 * @param mode: directed, undirected
	 * @param exist: a variable that tests if the node has been created before
	 */
	public Node(String str, String mode, boolean exist, Graph graph){
		super();
		super.setNode(true);
	
		String[] idNode1 = str.split(":");
		if(mode.equals("directed")==true){
			this.isDirected = true;
		}
		
		this.setId(idNode1[0]);
		
		
		if(idNode1.length>1)
		{
			String[] node1Children = idNode1[1].split(",");
			for(int j=0; j<node1Children.length ; j++)
			{
				String[] node1Values = node1Children[j].split("=");
				//System.out.println("here we are :::"+node1Values.length);
				this.put(node1Values[0], node1Values[1]);
			}
			
			// if this is the first time that this node has been found
			
			
			// the Case of Distortion ...... Test if it is already exist
			if(exist==true)
			{
				Iterator nodeIterator = graph.iterator();
				exist=false;
				while (nodeIterator.hasNext()) {
					Node node = (Node) nodeIterator.next();
					if (node.getComponentId().equals(this.getComponentId())) {
						exist=true;
						//System.out.println("The node "+this.getId()+"is already in the graph "+graph.getId());
						break;
					}
					
				}
				
			
			}
			
			
			if(exist==false)
			{
				//System.out.println("Adding the node "+this.getId()+"to the graph ..."+graph.getId());
				this.edges = new LinkedList();
				graph.add(this);
			}
		}
		
		
	}
	
	/**
	 * @return communities The LinkedList of communities the node belongs to
	 */
	public LinkedList<String> getCommunities() {
		return communities;
	}

	/**
	 * @return the name of the community stored at the index index the node belongs to
	 */
	public String getCommunity(int index) {
		return (String) communities.get(index);
	}
	
	/**
	 * Adds the name of the community passed in parameter at the end of the list.
	 * @param name The name of the community to add.
	 */
	public void addCommunity(String name) {
		communities.push(name);
	}
	
	/**
	 * Adds the name of the community passed in parameter at position index.
	 * @param index The position to put the community.
	 * @param name The name of the community to add.
	 */
	public void addCommunity(int index, String name) {
		communities.add(index, name);
	}
	
	/**
	 * Changes a community.
	 * @param index The index of the community to change.
	 * @param name The new name of the community.
	 */
	public void editCommunity(int index, String name) {
		communities.set(index, name);
	}
	
	/**
	 * Removes the last community from the list.
	 */
	public void removeCommunity() {
		communities.pop();
	}

	/**
	 * @param communities The LinkedList of communities to set.
	 */
	public void setCommunities(LinkedList<String> communities) {
		this.communities = communities;
	}
	
	/**
	 * @return   Returns the edges.
	 * @uml.property   name="edges"
	 */
	public LinkedList getEdges() {
		return edges;
	}

	/**
	 * @param edges   The edges to set.
	 * @uml.property   name="edges"
	 */
	public void setEdges(LinkedList edges) {
		this.edges = edges;
	}

	  @Override
		/**
		 * @return the node as a string 
		 * Node To String converter
		 */
	  public String toString() {
		  String str=this.getComponentId()+":";
		  Enumeration enumeration = this.getTable().keys();
		  int i=0;
		  int size = this.getTable().size();
			while (enumeration.hasMoreElements()) {
				i=i+1;
				String key = (String) enumeration.nextElement();
				String value = (String) this.getTable().get(key);
				
				str=str+""+key+"="+value;
				if(i!=size)
				{
					str=str+",";
				}
			}
		
	       return str;
	  }
}
