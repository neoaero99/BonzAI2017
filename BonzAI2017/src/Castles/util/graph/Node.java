package Castles.util.graph;

/**
 * A simple node interface to link together the Vertex and WeightedEdge classes.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of element to store in the node
 */
public interface Node<E> {
	
	/**
	 * Sets the value of this node's element.
	 * 
	 * @param e	The new value for the node's element
	 */
	public abstract void setElement(E e);
	
	/**
	 * @return	The current value of the node's element
	 */
	public abstract E getElement();
}
