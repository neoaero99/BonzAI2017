package graph;

import linkedlist.*;

/**
 * A graph node object for an adjacency list graph structure.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of object stored in the node
 */
public class GraphNode<E, W extends Comparable<W>> {
	
	// List of the node's connected edges
	private DualLinkList<GraphEdge<E, W>> adjEdges; 
	private E element;
	
	public GraphNode(E e) {
		adjEdges = new DualLinkList<GraphEdge<E, W>>();
		element = e;
	}
	
	public static void main(String[] args) {
		GraphNode<Integer, Double> node1, node2, node3, node5;
		GraphEdge<Integer, Double> edge1, edge2, edge3, edge4;
		
	}
	
	/**
	 * Connected an edge to this node.
	 * 
	 * @param edge	The edge to connect to this node
	 */
	public void addEdge(GraphEdge<E, W> edge) {
		adjEdges.addToBack(edge);
	}
	
	/**
	 * Remove the given edge from the adjacency list of this node, if it is
	 * adjacent to this node.
	 * 
	 * @param toRemove	The edge to remove
	 * @return			Whether the edge was successfully removed
	 */
	public boolean removeEdge(GraphEdge<E, W> toRemove) {
		return adjEdges.removeRef(toRemove);
	}
	
	/**
	 * Return a list of nodes, which are adjacent to this node.
	 * 
	 * @return	A list of adjacent nodes
	 */
	public DualLinkList<GraphNode<E, W>> adjacentVertices() {
		DualLinkList<GraphNode<E, W>> copyList = new DualLinkList<GraphNode<E, W>>();
		
		for (GraphEdge<E, W> edge : adjEdges) {
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
