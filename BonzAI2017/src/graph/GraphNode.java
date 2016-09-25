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
		GraphNode<Integer, Double> node1, node2, node3, node4, node5;
		GraphEdge<Integer, Double> edge1, edge2, edge3, edge4;
		
		node1 = new GraphNode<Integer, Double>(0);
		node2 = new GraphNode<Integer, Double>(1);
		node3 = new GraphNode<Integer, Double>(2);
		node4 = new GraphNode<Integer, Double>(3);
		node5 = new GraphNode<Integer, Double>(4);
		
		edge1 = new GraphEdge<Integer, Double>(5.0);
		edge2 = new GraphEdge<Integer, Double>(10.0);
		edge3 = new GraphEdge<Integer, Double>(8.0);
		edge4 = new GraphEdge<Integer, Double>(3.0);
		
		formConnection(node1, node2, edge1);
		formConnection(node1, node1, edge2);
		System.out.printf("%s\n", node1.adjEdges);
		System.out.printf("%s\n", node2.adjEdges);
		
		node1.removeEdge(edge2);
		
		System.out.printf("%s\n", node1.adjEdges);
	}
	
	public static <E, W extends Comparable<W>> void formConnection(GraphNode<E, W>
					fNode, GraphNode<E, W> bNode, GraphEdge<E, W> edge) throws
						InvalidNodeException, InvalidEdgeException {
		
		if (fNode == null || bNode == null) {
			throw new InvalidNodeException("nodes cannot be null!");
			
		} else if (edge == null) {
			throw new InvalidEdgeException("edge cannot be null!");
		}
		
		edge.setFirst(fNode);
		edge.setSecond(bNode);
		fNode.addEdge(edge);
		bNode.addEdge(edge);
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
	 * Removes all instances of the given edge from this node's adjacency list.
	 * 
	 * @param toRemove	The edge to remove
	 * @return			The number of removed edges
	 */
	public int removeEdge(GraphEdge<E, W> toRemove) {
		int removed = 0;
		
		while (adjEdges.removeRef(toRemove)) {
			++removed;
		}
		return removed;
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
