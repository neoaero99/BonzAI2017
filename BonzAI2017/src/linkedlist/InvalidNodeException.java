package linkedlist;

/**
 * A simple Exception that is thrown when an error occurs due to an invalid
 * node being passed as a parameter to a method in the dual linked list.
 * 
 * @author Joshua Hooker
 */
public class InvalidNodeException extends RuntimeException {
	private static final long serialVersionUID = -3211426265417059560L;

	public InvalidNodeException(String msg) {
		super(msg);
	}
}
