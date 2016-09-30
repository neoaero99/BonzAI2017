package graph;

import java.util.HashMap;
import java.util.Set;

import linkedlist.*;

/**
 * A graph node object for an adjacency list graph structure.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of object stored in the node
 */
public class GraphNode<E, W extends Comparable<W>> {
	
	// The node's adjacency hashmap or set of all adjacent edges
	private HashMap<GraphEdge<E, W>, Integer> adjEdges;
	private E element;
	
	public GraphNode(E e) {
		adjEdges = new HashMap<GraphEdge<E, W>, Integer>();
		element = e;
	}
	
	/**
	 * Adds the given edge to this node's adjacency hashmap.
	 * 
	 * @param edge	The edge to connect to this node
	 */
	public void addConnection(GraphEdge<E, W> edge) {
		adjEdges.put(edge, 0);
	}
	
	/**
	 * Removes the given edge from this node's adjacency hashmap, if it exists.
	 * 
	 * @param edge	The edge from which to remove all connections
	 * @return		The value associated with this edge in the adjacency
	 * 				hashmap
	 */
	public Integer removeConnection(GraphEdge<E, W> edge) {
		return adjEdges.remove(edge);
	}
	
	/**
	 * Determines if the given node is adjacent to this node.
	 * 
	 * @param node	A non-null graph node
	 * @return		If the given node is adjacent to this node
	 */
	public boolean isAdjacent(GraphNode<E, W> node) {
		DualLinkList<GraphNode<E, W>> adjNodes = adjacentVertices();
		return adjNodes.findNextRef(adjNodes.Head, node) != null;
	}
	
	/**
	 * Return a list of nodes, which are adjacent to this node.
	 * 
	 * @return	A list of adjacent nodes
	 */
	public DualLinkList<GraphNode<E, W>> adjacentVertices() {
		DualLinkList<GraphNode<E, W>> copyList =
				new DualLinkList<GraphNode<E, W>>();
		Set<GraphEdge<E, W>> keys = adjEdges.keySet();
		
		for (GraphEdge<E, W> edge : keys) {
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
		if (obj instanceof GraphNode) {
			GraphNode node = (GraphNode)obj;
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
