package Castles.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * 
 * 
 * @author Joshua Hooker
 */
public class WeightedGraph {
	
	/**
	 * The graphs lists of nodes and edges.
	 */
	private final HashMap<String, Vertex> vertices;
	private final HashMap<String, SegEdge> edges;
	
	public WeightedGraph(ArrayList<Vertex> V, ArrayList<SegEdge> E) {
		vertices = new HashMap<String, Vertex>();
		edges = new HashMap<String, SegEdge>();
		// Add all vertices and edges to the graph
		for (Vertex v : V) {
			vertices.put(v.ID, v);
		}
		
		for (SegEdge e : E) {
			edges.put(e.ID, e);
		}
	}
	
	/**
	 * Testing stuffs ...
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		/**
		
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
		
		/**/
	}
	
	/**
	 * 
	 * @param vID
	 * @return
	 */
	public Vertex getVertex(String vID) {
		return vertices.get(vID);
	}
	
	/**
	 * 
	 * @param eID
	 * @return
	 */
	public SegEdge getEdge(String eID) {
		return edges.get(eID);
	}
	
	/**
	 * @return	A copy of the list of nodes in the graph
	 */
	public ArrayList<Vertex> vertexList() {
		ArrayList<Vertex> copy = new ArrayList<Vertex>();
		Collection<Vertex> vertexList = vertices.values();
		
		for (Vertex v : vertexList) {
			copy.add(v);
		}
		
		return copy;
	}
	
	/**
	 * @return	A copy of the list of edges in the graph
	 */
	public ArrayList<SegEdge> edgeList() {
		ArrayList<SegEdge> copy = new ArrayList<SegEdge>();
		Collection<SegEdge> edgeList = edges.values();
		
		for (SegEdge v : edgeList) {
			copy.add(v);
		}
		
		return copy;
	}
	
	@Override
	public String toString() {
		/* List nodes followed by edges, each on separate lines */
		return String.format("N: %s\nE: %s", vertices, edges);
	}
}
