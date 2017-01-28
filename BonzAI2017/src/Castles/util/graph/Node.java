package Castles.util.graph;

/**
 * A simple node interface to link together the Vertex and SegEdge classes.
 * 
 * @author Joshua Hooker
 */
public abstract class Node {
	/**
	 * A unique Identifier for all nodes
	 */
	public final String ID;
	
	/**
	 * Creates a new node with the given ID value.
	 * 
	 * @param id	The unique identifier for the node
	 */
	public Node(String id) {
		ID = id;
	}
}
