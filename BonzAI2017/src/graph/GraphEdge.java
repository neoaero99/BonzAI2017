package graph;

import linkedlist.*;

/**
 * A simple weight edge with references to the connected vertices.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of element contained in the nodes
 * @param <W>	The type of element contained in the edges
 */
@SuppressWarnings("rawtypes")
public class GraphEdge<W extends Comparable<W>> {
	// The nodes, which this edge connects
	private GraphNode first, second;
	// The weight value associated with this edge
	private W weight;
	
	public GraphEdge() {
		first = null;
		second = null;
		weight = null;
	}
	
	public GraphEdge(W iniWeight) {
		first = null;
		second = null;
		weight = iniWeight;
	}
	
	/**
	 * Return the opposing node connected to this edge of the give node. If
	 * node is not equal to either edge, then null is returned.
	 * 
	 * @param node	A node connected to this edge
	 * @return		The opposite graph node connected to this edge that is not
	 * 				equal to the given node
	 * @throws		InvalidNodeException- if node is not connected to this edge
	 */
	@SuppressWarnings("unchecked")
	public <E> GraphNode<E> getOpposite(GraphNode<E> node) {
		
		if (!isConnected(node)) {
			throw new InvalidNodeException("Must be connected to the edge!");
			
		} else if (first == node) {
			return (GraphNode<E>)second;
			
		} else if (second == node) {
			return (GraphNode<E>)first;
			
		}
		
		return null;
	}
	
	/**
	 * Determines if either first or second are references to node's address
	 * space.
	 * 
	 * @param node	The node to check this edge for a connection
	 * @return		If first or second are references to node's address space
	 */
	public boolean isConnected(GraphNode node) {
		return node != null && (node == first || node == second);
	}
	
	// Getter and setters
	
	public void setFirst(GraphNode node) {
		first = node;
	}
	
	@SuppressWarnings("unchecked")
	public <E> GraphNode<E> getFirst() {
		return (GraphNode<E>)first;
	}
	
	public void setSecond(GraphNode node) {
		second = node;
	}
	
	@SuppressWarnings("unchecked")
	public <E> GraphNode<E> getSecond() {
		return (GraphNode<E>)second;
	}
	
	public void setWeight(W newWeight) {
		weight = newWeight;
	}
	
	public W getWeight() { return weight; }
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GraphEdge) {
			GraphEdge edge = (GraphEdge)obj;
			// Compare edge weights
			if ((weight == null && edge.weight == null) ||
					weight.equals(edge.weight)) {
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		/* Display weight value and if the edge has a connection for first and
		 * second */
		char firstConnect = (first == null) ? '(' : '<';
		char secondConnect = (second == null) ? ')' : '>';
		
		return String.format("%c w: %s %c", firstConnect, weight,
				secondConnect);
	}
}
