package Castles.util.graph;

/**
 * An exception that indicates that an edge passed as an argument to a method
 * is not legal.
 * 
 * @author Joshua Hooker
 */
public class InvalidEdgeException extends RuntimeException {
	private static final long serialVersionUID = 2368278960494560642L;
	
	public InvalidEdgeException(String msg) {
		super(msg);
	}
}
