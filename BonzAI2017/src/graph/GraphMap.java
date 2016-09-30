package graph;

import linkedlist.*;

public class GraphMap<E, W extends Comparable<W>> {
	
	/**
	 * The graphs lists of nodes and edges.
	 */
	private DualLinkList<GraphNode<E>> nodes;
	private DualLinkList<GraphEdge<W>> edges;
	
	public GraphMap() {
		nodes = new DualLinkList<GraphNode<E>>();
		edges = new DualLinkList<GraphEdge<W>>();
	}
	
	public static void main(String[] args) {
		GraphNode<Integer> node1, node2, node3, node4, node5;
		GraphEdge<Double> edge1, edge2, edge3, edge4;
		
		node1 = new GraphNode<Integer>(0);
		node2 = new GraphNode<Integer>(1);
		node3 = new GraphNode<Integer>(2);
		node4 = new GraphNode<Integer>(3);
		node5 = new GraphNode<Integer>(4);
		
		edge1 = new GraphEdge<Double>(5.0);
		edge2 = new GraphEdge<Double>(10.0);
		edge3 = new GraphEdge<Double>(8.0);
		edge4 = new GraphEdge<Double>(3.0);
		
		connect(node1, node2, edge1);
		connect(node1, node1, edge2);
		connect(node1, node3, edge3);
		connect(node4, node5, edge4);
		
		System.out.printf("%d: %s\n", node1.getElement(), node1.adjacentVertices());
		System.out.printf("%d: %s\n", node2.getElement(), node2.adjacentVertices());
		System.out.printf("%d: %s\n", node3.getElement(), node3.adjacentVertices());
		System.out.printf("%d: %s\n", node4.getElement(), node4.adjacentVertices());
		System.out.printf("%d: %s\n", node5.getElement(), node5.adjacentVertices());
		
		System.out.printf("%s\n", edge1);
		System.out.printf("%s\n", edge2);
		System.out.printf("%s\n", edge3);
		System.out.printf("%s\n", edge4);
		
		System.out.printf("Node %d is connected to Edge %f: %b\n",
				node3.getElement(), edge1.getWeight(), edge1.isConnected(node3));
		
		disconnect(node1, edge2);
		
		System.out.printf("%d: %s\n", node1.getElement(), node1.adjacentVertices());
		
		System.out.printf("%s\n", edge2);
	}
	
	/**
	 * Form the connection fNode <--> edge <--> sNode.
	 * 
	 * @param fNode	A non-null graph node
	 * @param sNode	A non-null graph node
	 * @param edge	A non-null graph edge
	 * @throws 		InvalidNodeException- if either fNode or sNode are null
	 * @throws 		InvalidEdgeException- if edge is null
	 */
	public static <E, W extends Comparable<W>> void connect(GraphNode<E> fNode,
			GraphNode<E> sNode, GraphEdge<W> edge) throws InvalidNodeException,
			InvalidEdgeException {
		
		if (fNode == null || sNode == null) {
			throw new InvalidNodeException("nodes cannot be null!");
			
		} else if (edge == null) {
			throw new InvalidEdgeException("edge cannot be null!");
		}
		
		edge.setFirst(fNode);
		edge.setSecond(sNode);
		fNode.addConnection(edge);
		sNode.addConnection(edge);
	}
	
	/**
	 * Disconnect the given node from the given edge.
	 * 
	 * @param node	A non-null graph node
	 * @param edge	A non-null graph edge
	 * @throws 		InvalidNodeException- if node is null
	 * @throws 		InvalidEdgeException- if edge is null
	 */
	public static <E, W extends Comparable<W>> void disconnect(GraphNode<E> node,
			GraphEdge<W> edge) throws InvalidNodeException, InvalidEdgeException  {
		
		if (node == null) {
			throw new InvalidNodeException("node cannot be null!");
			
		} else if (edge == null) {
			throw new InvalidEdgeException("edge cannot be null");	
		}
		
		/* Remove the node references from the edge */
		
		if (edge.getFirst() == node) {
			edge.setFirst(null);
		}
		
		if (edge.getSecond() == node) {
			edge.setSecond(null);
		}
		
		/* Remove the edge references from the node */
		node.removeConnection(edge);
	}
	
	public void addNode(GraphNode<E> newNode) throws InvalidNodeException {
		if (nodes.findNextRef(nodes.Head, newNode) != null) {
			throw new InvalidNodeException("newNode already exists in the graph!");
		}
		
		nodes.addToBack(newNode);
	}
	
	public void adddEdge(GraphEdge<W> newEdge) throws InvalidEdgeException {
		if (edges.findNextRef(edges.Head, newEdge) != null) {
			throw new InvalidNodeException("newEdge already exists in the graph!");
		}
		
		edges.addToBack(newEdge);
	}
}
