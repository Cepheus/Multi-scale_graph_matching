package util;

import java.util.Hashtable;

import algorithms.Constants;



/**
 * @author kriesen
 * GraphComponent is an edge or a node
 */
public class GraphComponent {
	
	/** boolean to check if gc 
	 * is a node or an edge
	 */
	private boolean isNode;
	
	/** the attributes of the gc:
	 *  key - value 
	 *  (e.g. x = 3; y = 5)
	 */
	private Hashtable table;
	
	/** the identifier of the gc */
	private String componentId;
		
	

	/**
	 * the constructor
	 */
	public GraphComponent(){
		this.table = new Hashtable();
	}
	
	public GraphComponent(String id){
		this.componentId = id;
	}
	
	
	/**
	 * puts a new attribute ¬
	 * in the table
	 */
	public void put(String key, String value){
		this.table.put(key, value);
	}
	
	public Object getValue(String key){
		return this.table.get(key);
	}

	/**
	 * returns the table
	 */
	public Hashtable getTable() {
		return table;
	}

	/**
	 * @param table   The table to set.
	 * @uml.property   name="table"
	 */
	public void setTable(Hashtable table) {
		this.table = table;
	}

	public void setId(String id) {
		this.componentId = id;
	}

	/**
	 * @return   Returns the componentId.
	 * @uml.property   name="componentId"
	 */
	public String getComponentId() {
		return componentId;
	}

	/**
	 * @param componentId   The componentId to set.
	 * @uml.property   name="componentId"
	 */
	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	/**
	 * @return
	 * @uml.property   name="isNode"
	 */
	public boolean isNode() {
		return isNode;
	}

	/**
	 * @param isNode   The isNode to set.
	 * @uml.property   name="isNode"
	 */
	public void setNode(boolean isNode) {
		this.isNode = isNode;
	}
	
	public String getId() {
		return componentId;
	}

	
}
