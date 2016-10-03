package Castles.util.graph;

import Castles.util.linkedlist.*;

/**
 * A generic graph with weighted edges.
 * 
 * @author Joshua Hooker
 *
 * @param <E>	The type of object stored in the vertices
 * @param <W>	The type of element contained in the edges
 */
public class WeightedGraph<E, W extends Comparable<W>> {
	
	/**
	 * The graphs lists of nodes and edges.
	 */
	private DualLinkList<Vertex<E, W>> vertices;
	private DualLinkList<WeightedEdge<E, W>> edges;
	
	public WeightedGraph() {
		vertices = new DualLinkList<Vertex<E, W>>();
		edges = new DualLinkList<WeightedEdge<E, W>>();
	}
	
	/**
	 * Testing stuffs ...
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		WeightedGraph<Integer, Double> g = new WeightedGraph<Integer, Double>();
		Vertex<Integer, Double> node1, node2, node3, node4, node5;
		WeightedEdge<Integer, Double> edge1, edge2, edge3, edge4;
		
		node1 = new Vertex<Integer, Double>(0);
		node2 = new Vertex<Integer, Double>(1);
		node3 = new Vertex<Integer, Double>(2);
		node4 = new Vertex<Integer, Double>(3);
		node5 = new Vertex<Integer, Double>(4);
		
		edge1 = new WeightedEdge<Integer, Double>(5.0);
		edge2 = new WeightedEdge<Integer, Double>(10.0);
		edge3 = new WeightedEdge<Integer, Double>(8.0);
		edge4 = new WeightedEdge<Integer, Double>(3.0);
		
		WeightedGraph.connect(node1, node2, edge1);
		WeightedGraph.connect(node1, node1, edge2);
		WeightedGraph.connect(node1, node3, edge3);
		WeightedGraph.connect(node4, node5, edge4);
		
		System.out.printf("%d: %s\n", node1.getElement(), node1.adjacentVertices());
		System.out.printf("%d: %s\n", node2.getElement(), node2.adjacentVertices());
		System.out.printf("%d: %s\n", node3.getElement(), node3.adjacentVertices());
		System.out.printf("%d: %s\n", node4.getElement(), node4.adjacentVertices());
		System.out.printf("%d: %s\n", node5.getElement(), node5.adjacentVertices());
		
		System.out.printf("%s\n", edge1);
		System.out.printf("%s\n", edge2);
		System.out.printf("%s\n", edge3);
		System.out.printf("%s\n", edge4);
		
		
		g.addNode(node1);		
		g.addNode(node2);
		g.addNode(node3);
		g.addNode(node4);
		g.addNode(node5);
		
		g.addEdge(edge1);
		g.addEdge(edge2);
		g.addEdge(edge3);
		g.addEdge(edge4);
		
		System.out.printf("g:\n%s\n", g);
		
		g.removeNode(node1);
		
		System.out.printf("g:\n%s\n", g);
		System.out.printf("%s\n", edge1);
		
		
		System.out.printf("Node %d is connected to Edge %f: %b\n",
				node3.getElement(), edge1.getElement(), edge1.isConnected(node3));
		
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
	public static <E, W extends Comparable<W>> void connect(Vertex<E, W> fNode,
			Vertex<E, W> sNode, WeightedEdge<E, W> edge) throws InvalidNodeException,
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
	public static <E, W extends Comparable<W>> void disconnect(Vertex<E, W> node,
			WeightedEdge<E, W> edge) throws InvalidNodeException, InvalidEdgeException  {
		
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
	
	/**
	 * Adds the given node to the graph unless the node already belongs to the
	 * graph.
	 * 
	 * @param newNode	The node to add to the graph
	 * @throws 			InvalidNodeException-  If the give node is null or
	 * 						already belongs to the graph
	 */
	public void addNode(Vertex<E, W> newNode) throws InvalidNodeException {
		if (newNode == null) {
			throw new InvalidNodeException("newNode cannot be null!");
			
		} else if (vertices.findNextRef(vertices.Head, newNode) != null) {
			throw new InvalidNodeException("newNode already exists in the graph!");
		}
		
		vertices.addToBack(newNode);
	}
	
	/**
	 * Removes the given node from the graph, if it belongs to in the graph.
	 * The node is invalidated, when successfully removed from the graph.
	 * 
	 * @param target	The node to remove from the graph
	 * @return			The value stored in the node, or null if the node is
	 * 					not removed
	 * @throws 			InvalidNodeException- if the node does not exists in
	 * 						the graph or is null
	 */
	public E removeNode(Vertex<E, W> target) throws InvalidNodeException {
		if (target == null) {
			throw new InvalidNodeException("target cannot be null!");
			
		}
		
		if (vertices.removeRef(target)) {
			return invalidateNode(target);
		}
		// Does not exist in the graph
		return null;
	}
	
	/**
	 * Adds the given edge to the graph unless the edge already belongs to the
	 * graph.
	 * 
	 * @param newEdge	The edge to add to the graph
	 * @throws 			InvalidEdgeException- if the edge is null or already
	 * 						belongs to the graph
	 */
	public void addEdge(WeightedEdge<E, W> newEdge) throws InvalidEdgeException {
		if (newEdge == null) {
			throw new InvalidEdgeException("newEdge cannot be null!");
			
		} else if (edges.findNextRef(edges.Head, newEdge) != null) {
			throw new InvalidEdgeException("newEdge already exists in the graph!");
		}
		
		edges.addToBack(newEdge);
	}
	
	/**
	 * If the given edge belongs to the graph, then it is removed and
	 * invalidated. An exception is thrown otherwise.
	 * 
	 * @param target	The edge to remove from the graph
	 * @return			The weight of the edge, or null if the edge is not
	 * 					removed
	 * @throws 			InvalidEdgeException- If the edge does not belong to
	 * 						the graph or it is null
	 */
	public W removeEdge(WeightedEdge<E, W> target) throws InvalidEdgeException {
		if (target == null) {
			throw new InvalidEdgeException("target cannot be null!");
			
		}
		
		if (edges.removeRef(target)) {
			return invalidateEdge(target);
		}
		// Does not exists in the graph
		return null;
	}
	
	/**
	 * Removes all adjacent edges from the node and removes its element reference.
	 * 
	 * @param node	A non-null node
	 * @return		The element stored in the node
	 * @throws		InvalidNodeException- if the node is null
	 */
	private static <E, W extends Comparable<W>> E invalidateNode(Vertex<E, W> node)
			throws InvalidNodeException {
		
		if (node == null) {
			throw new InvalidNodeException("node cannot be null!");	
		}
		
		/* Remove node connections */
		DualLinkList<WeightedEdge<E, W>> adjEdges = node.adjEdges();
		for (WeightedEdge<E, W> edge : adjEdges) {
			disconnect(node, edge);
		}
		
		E val = node.getElement();
		node.setElement(null);
		return val;
	}
	
	/**
	 * Removes the edge's node references as well as its weight reference.
	 * 
	 * @param edge	A non-null edge
	 * @return		The weight of the edge
	 * @throws		InvalidEdgeException- if the edge is null
	 */
	private static <E, W extends Comparable<W>> W invalidateEdge(WeightedEdge<E, W> edge)
			throws InvalidEdgeException {
		
		if (edge == null) {
			throw new InvalidEdgeException("edge cannot be null!");	
		}
		
		/* Remove node connections */
		if (edge.getFirst() != null) {
			disconnect(edge.getFirst(), edge);
		}
		
		if (edge.getSecond() != null) {
			disconnect(edge.getSecond(), edge);
		}
		
		W val = edge.getElement();
		edge.setElement(null);
		return val;
	}
	
	/**
	 * @return	A copy of the list of nodes in the graph
	 */
	public DualLinkList<Vertex<E, W>> vertexList() {
		DualLinkList<Vertex<E, W>> copy = new DualLinkList<Vertex<E, W>>();
		
		for (Vertex<E, W> v : vertices) {
			copy.addToBack(v);
		}
		
		return copy;
	}
	
	/**
	 * @return	A copy of the list of edges in the graph
	 */
	public DualLinkList<WeightedEdge<E, W>> edgeList() {
		DualLinkList<WeightedEdge<E, W>> copy =
				new DualLinkList<WeightedEdge<E, W>>();
		
		for (WeightedEdge<E, W> v : edges) {
			copy.addToBack(v);
		}
		
		return copy;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (obj instanceof WeightedGraph) {
			WeightedGraph g = (WeightedGraph)obj;
			/* Check if each graph has equivalent nodes and edges */
			return vertices.equals(g.vertices) && edges.equals(g.edges);
			
			/* TODO trace graphs to check all connections */
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		/* List nodes followed by edges, each on separate lines */
		return String.format("N: %s\nE: %s", vertices, edges);
	}
}
