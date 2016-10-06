package Castles.util.graph;

import java.util.HashMap;
import java.util.Set;

import Castles.util.linkedlist.*;

/**
 * A graph node object for an adjacency list graph structure.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of object stored in the vertices
 * @param <W>	The type of element contained in the edges
 */
public class Vertex<E, W extends Comparable<W>> implements Node<E>{
	
	// The vertex's adjacency hashmap or set of all adjacent edges
	private HashMap<WeightedEdge<E, W>, Integer> incEdges;
	private E element;
	
	public Vertex(E e) {
		incEdges = new HashMap<WeightedEdge<E, W>, Integer>();
		element = e;
	}
	
	/**
	 * Adds the given edge to this vertex's adjacency hashmap.
	 * 
	 * @param edge	The edge to connect to this node
	 * @return		The old value associated with this connection, or null if
	 * 				no such connection exists
	 */
	public Integer addConnection(WeightedEdge<E, W> edge) {
		return incEdges.put(edge, 0);
	}
	
	/**
	 * Adds the given edge to this vertex's adjacency hashmap with the given
	 * integer value.
	 * 
	 * @param edge	The edge to connect to this node
	 * @param val	The integer to associate with the connection between
	 * 				the given edge and this node
	 * @return		The old value associated with this connection, or null if
	 * 				no such connection exists
	 */
	public Integer addConnection(WeightedEdge<E, W> edge, int val) {
		return incEdges.put(edge, val);
	}
	
	/**
	 * Returns the integer value associated with the connection between the
	 * given edge and this vertex. If the vertex is not connected, then null is
	 * returned.
	 * 
	 * @param edge	The edge to query for a connection
	 * @return		The value associated with the connection, or null if
	 * 				there is no connection
	 */
	public Integer associatedInt(WeightedEdge<E, W> edge) {
		return incEdges.get(edge);
	}
	
	/**
	 * Removes the given edge from this vertex's adjacency hashmap, if it
	 * exists.
	 * 
	 * @param edge	The edge from which to remove all connections
	 * @return		The value associated with this edge in the adjacency
	 * 				hashmap
	 */
	public Integer removeConnection(WeightedEdge<E, W> edge) {
		return incEdges.remove(edge);
	}
	
	/**
	 * @return	A list of edges that connect to this vertex
	 */
	public DualLinkList<WeightedEdge<E, W>> incidentEdges() {
		DualLinkList<WeightedEdge<E, W>> dupEdges = new
				DualLinkList<WeightedEdge<E, W>>();
		Set<WeightedEdge<E, W>> edgeSet = incEdges.keySet();
		
		for (WeightedEdge<E, W> edge : edgeSet) {
			dupEdges.addToBack(edge);
		} 
		
		return dupEdges;
	}
	
	/**
	 * Returns the number of edges connected to this vertex.
	 * 
	 * @return	the degree of this vertex
	 */
	public int degree() { return incEdges.size(); }
	
	/**
	 * Determines if the given node is adjacent to this vertex.
	 * 
	 * @param node	A non-null graph node
	 * @return		If the given node is adjacent to this vertex
	 */
	public boolean isAdjacent(Vertex<E, W> node) {
		DualLinkList<Vertex<E, W>> adjNodes = adjacentVertices();
		return adjNodes.findNextRef(adjNodes.Head, node) != null;
	}
	
	/**
	 * Return a list of nodes, which are adjacent to this vertex.
	 * 
	 * @return	A list of adjacent vertices
	 */
	public DualLinkList<Vertex<E, W>> adjacentVertices() {
		DualLinkList<Vertex<E, W>> copyList =
				new DualLinkList<Vertex<E, W>>();
		Set<WeightedEdge<E, W>> keys = incEdges.keySet();
		
		for (WeightedEdge<E, W> edge : keys) {
			copyList.addToBack(edge.getOpposite(this));
		}
		
		return copyList;
	}
	
	// Element getter and setter
	public void setElement(E e) { element = e; }
	public E getElement() { return element; }
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			Vertex node = (Vertex)obj;
			// Compare elements
			if ((element == null && node.element == null) ||
					element.equals(node.element)) {
				
				return true;
			}
			
			return node.getElement().equals(element);
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("( %s )", element);
	}
}
