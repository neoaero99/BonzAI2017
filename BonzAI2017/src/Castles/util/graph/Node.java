package Castles.util.graph;

/**
 * A simple node interface to link together the Vertex and SegEdge classes.
 * 
 * @author Joshua Hooker
 */
public abstract class Node {
	
	public final String ID;
	
	public Node(String id) {
		ID = id;
	}
}
