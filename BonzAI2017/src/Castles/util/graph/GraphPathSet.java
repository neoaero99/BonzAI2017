package Castles.util.graph;

import java.util.HashMap;
import java.util.Set;

import Castles.util.linkedlist.DualLinkList;
import Castles.util.priorityq.*;

/**
 * This class contains a set shortest paths that connect each vertex to each other vertex in a
 * WeightedGraph object.
 * 
 * @author Joshua Hooker
 *
 * @param <E> The type of element stored in the vertices
 */
public class GraphPathSet<E extends Comparable<E>> {
	boolean debug =true;
	/**
	 * The set of shortest paths between any two vertices in the graph.
	 * The Vertex Pair is the starting and end vertices of the graph;
	 * The DualLinkedList of edges it the path.
	 */
	private final HashMap<VertexPair<E, Integer>, DualLinkList<WeightedEdge<E, Integer>>> VPPathsSet;
	/**
	 * The graph associated with the VPPathsSet
	 */
	private final WeightedGraph<E, Integer> graph;

	public static void main(String[] args) {
		WeightedGraph<Integer, Integer> g = new WeightedGraph<Integer, Integer>();
		
		Vertex<Integer, Integer> node0, node1, node2, node3, node4, node5, node6, node7, node8,
									node9, node10;
		WeightedEdge<Integer, Integer> edge0, edge1, edge2, edge3, edge4, edge5, edge6, edge7,
										edge8, edge9, edge10, edge11, edge12, edge13, edge14;

		node0 = new Vertex<Integer, Integer>(0);
		node1 = new Vertex<Integer, Integer>(1);
		node2 = new Vertex<Integer, Integer>(2);
		node3 = new Vertex<Integer, Integer>(3);
		node4 = new Vertex<Integer, Integer>(4);
		node5 = new Vertex<Integer, Integer>(5);
		node6 = new Vertex<Integer, Integer>(6);
		node7 = new Vertex<Integer, Integer>(7);
		node8 = new Vertex<Integer, Integer>(8);
		node9 = new Vertex<Integer, Integer>(9);
		node10 = new Vertex<Integer, Integer>(10);

		edge0 = new WeightedEdge<Integer, Integer>(2);
		edge1 = new WeightedEdge<Integer, Integer>(1);
		edge2 = new WeightedEdge<Integer, Integer>(2);
		edge3 = new WeightedEdge<Integer, Integer>(2);
		edge4 = new WeightedEdge<Integer, Integer>(1);
		edge5 = new WeightedEdge<Integer, Integer>(2);
		edge6 = new WeightedEdge<Integer, Integer>(1);
		edge7 = new WeightedEdge<Integer, Integer>(2);
		edge8 = new WeightedEdge<Integer, Integer>(1);
		edge9 = new WeightedEdge<Integer, Integer>(2);
		edge10 = new WeightedEdge<Integer, Integer>(1);
		edge11 = new WeightedEdge<Integer, Integer>(11);
		edge12 = new WeightedEdge<Integer, Integer>(7);
		edge13 = new WeightedEdge<Integer, Integer>(5);
		edge14 = new WeightedEdge<Integer, Integer>(9);

		WeightedGraph.connect(node0, node1, edge0);
		WeightedGraph.connect(node1, node2, edge1);
		WeightedGraph.connect(node2, node3, edge2);
		WeightedGraph.connect(node3, node4, edge3);
		WeightedGraph.connect(node4, node5, edge4);
		WeightedGraph.connect(node5, node6, edge5);
		WeightedGraph.connect(node6, node7, edge6);
		WeightedGraph.connect(node7, node8, edge7);
		WeightedGraph.connect(node8, node9, edge8);
		WeightedGraph.connect(node9, node10, edge9);
		WeightedGraph.connect(node10, node0, edge10);
		WeightedGraph.connect(node0, node5, edge11);
		WeightedGraph.connect(node2, node6, edge12);
		WeightedGraph.connect(node4, node10, edge13);
		WeightedGraph.connect(node3, node8, edge14);

		g.addNode(node0);
		g.addNode(node1);
		g.addNode(node2);
		g.addNode(node3);
		g.addNode(node4);
		g.addNode(node5);
		g.addNode(node6);
		g.addNode(node7);
		g.addNode(node8);
		g.addNode(node9);
		g.addNode(node10);

		g.addEdge(edge0);
		g.addEdge(edge1);
		g.addEdge(edge2);
		g.addEdge(edge3);
		g.addEdge(edge4);
		g.addEdge(edge5);
		g.addEdge(edge6);
		g.addEdge(edge7);
		g.addEdge(edge8);
		g.addEdge(edge9);
		g.addEdge(edge10);
		g.addEdge(edge11);
		g.addEdge(edge12);
		g.addEdge(edge13);
		g.addEdge(edge14);

		/**/
		
		GraphPathSet<Integer> gpath = new GraphPathSet<Integer>(g);
		gpath.printPaths();
		
		/**/
		
		System.out.println("TEST");
	}
	
	/**
	 * Generates the shortest paths associated with the given graph. It is assumed that the given
	 * graph has all its vertices, edges, and connections.
	 * 
	 * @param g	A fully initialized graph
	 */
	public GraphPathSet(WeightedGraph<E, Integer> g) {
		VPPathsSet = new HashMap<VertexPair<E, Integer>, DualLinkList<WeightedEdge<E, Integer>>>();
		graph = g;
		// Initial the VPPathSet based on g
		generatePaths();
	}

	/**
	 * Returns the path connecting the given two vertices, or null if no such path
	 * exists. It is assumed that v0 != v1.
	 * 
	 * @param v0	A vertex in the graph
	 * @param v1	Another vertex in the graph
	 * @return		A path connecting v0 and v1, in the graph
	 */
	public DualLinkList<WeightedEdge<E, Integer>> getPath(Vertex<E, Integer> v0,
			Vertex<E, Integer> v1) {
		
		Set<VertexPair<E, Integer>> endpoints = VPPathsSet.keySet();
		
		for (VertexPair<E, Integer> vp : endpoints) {
			// Check either direction for a vertex pair
			if ( (vp.getStart().equals(v0) && vp.getEnd().equals(v1)) ||
					(vp.getStart().equals(v1) && vp.getEnd().equals(v0)) ) {
				
				return VPPathsSet.get(vp);
			}
		}
		
		// Invalid start or end vertex
		return null;
	}


	public void printPaths(){
		for(VertexPair<E,Integer> e: VPPathsSet.keySet() ) {
			System.out.printf("%s : %s\n", e, VPPathsSet.get(e));
		}
		
		System.out.println();
	}
	/**
	 * Generates paths creates a hash map containing all shortest paths for a
	 * given graph
	 */
	private void generatePaths() {
		DualLinkList<Vertex<E, Integer>> vertices = graph.vertexList();
		
		for (Vertex<E, Integer> v : graph.vertices) {
			for (Vertex<E, Integer> u : graph.vertices) {
				boolean areEqual = v.equals(u);
				boolean pathExists = getPath(u, v) != null;
				
				if (areEqual || pathExists) {
					continue;
				}
				
				VertexPair<E, Integer> current = new VertexPair<E, Integer>(v, u);
				DualLinkList<WeightedEdge<E, Integer>> path = shortestPath(u, v);
				
				VPPathsSet.put(current, path);
			}
		}
	}
	
	/**
	 * Finds a shortest path, with no repeated edges, between the vertices start and end, which are in the graph
	 * associated with this GraphPathSet. The shortest path is calculated with Dijsktra's Algorithm.
	 * 
	 * @param start	The initial vertex of the path, in the graph associated with this
	 * @param end	The final vertex of the path, in graph associated with this
	 * @return		A dual-link list, with all the edges, which connect start to end, in the path
	 */
	public DualLinkList<WeightedEdge<E, Integer>> shortestPath(Vertex<E, Integer> start, Vertex<E, Integer> end) {
		
		// The list of vertices in the graph
		DualLinkList<Vertex<E, Integer>> vertices = graph.vertexList();
		// Associates data used in the shortest path calculation with each vertex
		HashMap<Vertex<E, Integer>, ExtraData> vertexData = new HashMap<Vertex<E, Integer>, ExtraData>();
		// The queue initially containing all the vertices, ordered by the vertex's distance
		AdaptablePQ<Integer, Vertex<E, Integer>> remaining = new AdaptablePQ<Integer, Vertex<E, Integer>>(
				vertices.size(), new MinComparator<PQEntry<Integer, Vertex<E, Integer>>>());
		
		// Initialize the data associated with every vertex and add them to the queue
		for (Vertex<E, Integer> v : vertices) {
			int iniWeight = (v.equals(start)) ? 0 : Integer.MAX_VALUE;
			vertexData.put(v, new ExtraData(0, iniWeight, remaining.insert(iniWeight, v), null));
		}
		
		/* Continually remove the vertex with the smallest distance value and update all the vertices
		 * adjacent to it based on distance of the vertex and the edge connecting it to a adjacent
		 * vertex. */
		while (!remaining.isEmpty()) {
			Vertex<E, Integer> least = remaining.removeMax();
			ExtraData ltData = vertexData.get(least);
			ltData.flag = 1;
			
			//System.out.printf("%s\n%s\n\n", least, remaining);
			
			if (least == end) {
				// The destination vertex has been found
				break;
			}

			DualLinkList<WeightedEdge<E, Integer>> incEdges = least.incidentEdges();
			
			// Check the distances of adjacent vertices
			for (WeightedEdge<E, Integer> e : incEdges) {
				Vertex<E, Integer> opposite = e.getOpposite(least);

				// Disregard self-loops
				if (!opposite.equals(least)) {
					//System.out.printf("%s\n%s\n%s\n\n", opposite, vertexData, remaining);
					ExtraData oppData = vertexData.get(opposite);
					
					// Only check unvisited vertices (i.e. still contained in the queue)
					if (oppData.flag == 0) {
						Integer newWeight = e.getElement() + ltData.weight;
						
						/* Is the new distance less than the current distance associated
						 * with the adjacent vertex? */
						if (newWeight < oppData.entryRef.getKey()) {
							// Update the ordering of the queue
							remaining.replaceKey(oppData.entryRef.getIndex(), newWeight);
							
							oppData.weight = newWeight;
							oppData.backEdge = e;
						}
					}
				}
			}
		}

		DualLinkList<WeightedEdge<E, Integer>> path = new DualLinkList<WeightedEdge<E, Integer>>();
		Vertex<E, Integer> limbo = end;
		ExtraData data = vertexData.get(limbo);

		/**
		System.out.println("PATH CALCULATED!");

		Set<Vertex<E, Integer>> keys = vertexData.keySet();

		System.out.print("[ ");
		for (Vertex<E, Integer> v : keys) {
			System.out.printf("%s : %s; ", v, vertexData.get(v).backEdge);
		}
		System.out.println("]");

		System.out.println(limbo);
		/**/

		while (limbo != null && limbo != start && data.backEdge != null) {
			path.addToFront(data.backEdge);
			limbo = data.backEdge.getOpposite(limbo);
			data = vertexData.get(limbo);
		}

		//System.out.println("FINISHED!");

		return path;
	}

	// graph getter method
	public WeightedGraph<E, Integer> getGraph() {
		return graph;
	}
	
	/**
	 * Contains the data associated with a vertex for Dijkstra's Algorithm.
	 * 
	 * @author Joshua Hooker
	 */
	private class ExtraData {
		/**
		 * Is this vertex visited?
		 * 0 -> no,
		 * 1 -> yes
		 */
		private int flag;
		
		// The current distance associated with this vertex
		private int weight;
		
		// The reference to the vertex's entry in the queue
		private PQEntry<Integer, Vertex<E, Integer>> entryRef;
		
		/**
		 * The reference to the edge that connects this vertex to the vertex
		 * prior to this one in the shortest path, which is used to build the
		 * path at the end of the algorithm
		 */
		private WeightedEdge<E, Integer> backEdge;

		public ExtraData(int f, int w, PQEntry<Integer, Vertex<E, Integer>> entry,
				WeightedEdge<E, Integer> bEdge) {

			flag = f;
			weight = w;
			entryRef = entry;
			backEdge = bEdge;
		}
		
		public String toString() {
			return String.format("[%s]", entryRef);
		}
	}
}
