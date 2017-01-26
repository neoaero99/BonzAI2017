package Castles.util.graph;

import Castles.util.linkedlist.*;

/**
 * A simple weight edge with references to the connected vertices.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of element contained in the vertices
 * @param <W>	The type of element contained in the edges
 */
public class WeightedEdge<E, W extends Comparable<W>> extends Node<W>
	implements Comparable<WeightedEdge<E, W>> {
	
	// The nodes, which this edge connects
	private Vertex<E, W> first, second;
	// The weight value associated with this edge
	private W weight;
	
	public WeightedEdge(String id, W iniWeight) {
		super(id);
		
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
	public Vertex<E, W> getOpposite(Vertex<E, W> node) throws
				InvalidNodeException {
		
		if (!isConnected(node)) {
			throw new InvalidNodeException("Must be connected to the edge!");
			
		} else if (first == node) {
			return second;
		}
		
		return first;
	}
	
	/**
	 * Determines if either first or second are references to node's address
	 * space.
	 * 
	 * @param node	The node to check this edge for a connection
	 * @return		If first or second are references to node's address space
	 */
	public boolean isConnected(Vertex<E, W> node) {
		return node != null && (node == first || node == second);
	}
	
	// Getter and setters
	
	public void setFirst(Vertex<E, W> node) { first = node; }
	public Vertex<E, W> getFirst() { return first; }
	
	public void setSecond(Vertex<E, W> node) { second = node; }
	public Vertex<E, W> getSecond() { return second; }
	
	@Override
	public void setElement(W e) { weight = e; }
	
	@Override
	public W getElement() { return weight; }
	
	@Override
	public int compareTo(WeightedEdge<E, W> edge) {
		/* Compare weights */
		int weightComp;
		
		if (weight == null && edge.weight == null) {
			weightComp = 0;
			
		} else if (weight == null) {
			return -1;
			
		} else if (edge.weight == null) {
			return 1;
			
		} else {
			weightComp = weight.compareTo(edge.weight);
		}
		
		if (weightComp != 0) {
			return weightComp;
		}
		
		/* Use number of connections as tie breaker */
		int conn = 0, eConn = 0;
		
		if (first != null) { ++conn; }
		if (second != null) { ++conn; }
		
		if (edge.first != null) { ++eConn; }
		
		if (edge.second != null) { ++eConn; }
		
		return (conn - eConn);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof WeightedEdge) {
			WeightedEdge edge = (WeightedEdge)obj;
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
		
		return String.format("%c %s %c", firstConnect, weight,
				secondConnect);
	}


}
