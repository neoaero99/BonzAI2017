package graph;

import linkedlist.*;

/**
 * A simple weight edge with references to the connected vertices.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of element contained in the nodes connected to this
 * 				type of edge
 */
public class GraphEdge<E, W extends Comparable<W>> {
	// The nodes, which this edge connects
	private GraphNode<E, W> first, second;
	// The weight value associated with this edge
	private W weight;
	
	public GraphEdge() {
		first = null;
		second = null;
		weight = null;
	}
	
	public GraphEdge(W iniWeight) {
		setWeight(iniWeight);
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
	public GraphNode<E, W> getOpposite(GraphNode<E, W> node) {
		
		if (!isConnected(node)) {
			throw new InvalidNodeException("Must be connected to the edge!");
			
		} else if (first == node) {
			return second;
			
		} else if (first != node) {
			return first;
			
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
	public boolean isConnected(GraphNode<E, W> node) {
		return node != null && (node == first || node == second);
	}
	
	// Getter and setters
	
	public void setFirst(GraphNode<E, W> node) {
		first = node;
	}
	
	public GraphNode<E, W> getFirst() {
		return first;
	}
	
	public void setSecond(GraphNode<E, W> node) {
		second = node;
	}
	
	public GraphNode<E, W> getSecond() {
		return second;
	}
	
	public void setWeight(W newWeight) {
		weight = newWeight;
	}
	
	public W getWeight() { return weight; }
	
	@Override
	@SuppressWarnings("rawtypes")
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
	
	public String toString() {
		// Display weight value and if the edge has a connection
		return String.format("[ w: %d, c: %b ]", weight, (first != null && second != null));
	}
}
