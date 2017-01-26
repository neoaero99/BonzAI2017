package Castles.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import Castles.Objects.RallyPoint;
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
public abstract class GraphPathSet {

	public static void main(String[] args) {
		/**
		
		WeightedGraph<Integer, Integer> g = new WeightedGraph<Integer, Integer>();
		
		Vertex<Integer, Integer> node0, node1, node2, node3, node4, node5, node6, node7, node8,
									node9, node10;
		SegEdge<Integer, Integer> edge0, edge1, edge2, edge3, edge4, edge5, edge6, edge7,
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

		edge0 = new SegEdge<Integer, Integer>(2);
		edge1 = new SegEdge<Integer, Integer>(1);
		edge2 = new SegEdge<Integer, Integer>(2);
		edge3 = new SegEdge<Integer, Integer>(2);
		edge4 = new SegEdge<Integer, Integer>(1);
		edge5 = new SegEdge<Integer, Integer>(2);
		edge6 = new SegEdge<Integer, Integer>(1);
		edge7 = new SegEdge<Integer, Integer>(2);
		edge8 = new SegEdge<Integer, Integer>(1);
		edge9 = new SegEdge<Integer, Integer>(2);
		edge10 = new SegEdge<Integer, Integer>(1);
		edge11 = new SegEdge<Integer, Integer>(11);
		edge12 = new SegEdge<Integer, Integer>(7);
		edge13 = new SegEdge<Integer, Integer>(5);
		edge14 = new SegEdge<Integer, Integer>(9);

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

		/**
		
		GraphPathSet<Integer> gpath = new GraphPathSet<Integer>(g);
		
		Set<IDPair> endpoints = PathIDsMap.keySet();
		
		for(IDPair e: endpoints ) {
			System.out.printf("%s : %s\n", e, PathIDsMap.get(e));
		}
		
		System.out.println();
		
		/**/
		
		System.out.println("TEST");
	}
	/**
	 * Returns the path connecting the given two vertices, or null if no such path
	 * exists. It is assumed that v0 != v1.
	 * 
	 * @param v0	A vertex in the graph
	 * @param v1	Another vertex in the graph
	 * @return		A path connecting v0 and v1, in the graph
	 */
	public static ArrayList<String> getPath(HashMap<IDPair, ArrayList<String>> pathsMap,
			Vertex v0, Vertex v1) {
		
		Set<IDPair> endpoints = pathsMap.keySet();
		
		for (IDPair idp : endpoints) {
			// Check either direction for a vertex pair
			if ( (idp.first.equals(v0.ID) && idp.second.equals(v1.ID)) ||
					(idp.first.equals(v1.ID) && idp.second.equals(v0.ID)) ) {
				
				return pathsMap.get(idp);
			}
		}
		
		// Invalid start or end vertex
		return null;
	}

	/**
	 * Generates paths creates a hash map containing all shortest paths for a
	 * given graph
	 */
	public static HashMap<IDPair, ArrayList<String>> generatePaths(WeightedGraph g) {
		HashMap<IDPair, ArrayList<String>> pathIDsMap = new HashMap<IDPair, ArrayList<String>>();
		ArrayList<Vertex> vertices = g.vertexList();
		
		for (Vertex v : vertices) {
			for (Vertex u : vertices) {
				boolean areEqual = v.equals(u);
				boolean pathExists = getPath(pathIDsMap, u, v) != null;
				
				if (areEqual || pathExists) {
					continue;
				}
				
				IDPair endpoints = new IDPair(v.ID, u.ID);
				ArrayList<Node> path = shortestPath(g, u, v);
				
				/* Convert the path into a list of IDs of vertices and edges in
				 * the path and put the list into the map */
				ArrayList<String> pathIDs = new ArrayList<String>();
				
				for (Node n : path) {
					pathIDs.add(n.ID);
				}
				
				pathIDsMap.put(endpoints, pathIDs);
			}
		}
		
		return pathIDsMap;
	}
	
	/**
	 * Finds a shortest path, with no repeated edges, between the vertices start and end, which are in the graph
	 * associated with this GraphPathSet. The shortest path is calculated with Dijsktra's Algorithm.
	 * 
	 * @param start	The initial vertex of the path, in the graph associated with this
	 * @param end	The final vertex of the path, in graph associated with this
	 * @return		A dual-link list, with all the edges, which connect start to end, in the path
	 */
	public static ArrayList<Node> shortestPath(WeightedGraph g, Vertex start, Vertex end) {
		
		// The list of vertices in the graph
		ArrayList<Vertex> vertices = g.vertexList();
		// Associates data used in the shortest path calculation with each vertex
		HashMap<Vertex, ExtraData> vertexData = new HashMap<Vertex, ExtraData>();
		// The queue initially containing all the vertices, ordered by the vertex's distance
		AdaptablePQ<Integer, Vertex> remaining = new AdaptablePQ<Integer, Vertex>(
				vertices.size(), new MinComparator<PQEntry<Integer, Vertex>>());
		
		// Initialize the data associated with every vertex and add them to the queue
		for (Vertex v : vertices) {
			int iniWeight = (v.equals(start)) ? 0 : Integer.MAX_VALUE;
			vertexData.put(v, new ExtraData(0, iniWeight, remaining.insert(iniWeight, v), null));
		}
		
		/* Continually remove the vertex with the smallest distance value and update all the vertices
		 * adjacent to it based on distance of the vertex and the edge connecting it to a adjacent
		 * vertex. */
		while (!remaining.isEmpty()) {
			Vertex least = remaining.removeMax();
			ExtraData ltData = vertexData.get(least);
			ltData.flag = 1;
			
			//System.out.printf("%s\n%s\n\n", least, remaining);
			
			if (least == end) {
				// The destination vertex has been found
				break;
			}

			ArrayList<SegEdge> incEdges = least.incidentEdges();
			
			// Check the distances of adjacent vertices
			for (SegEdge e : incEdges) {
				Vertex opposite = e.getOpposite(least);

				// Disregard self-loops
				if (!opposite.equals(least)) {
					//System.out.printf("%s\n%s\n%s\n\n", opposite, vertexData, remaining);
					ExtraData oppData = vertexData.get(opposite);
					
					// Only check unvisited vertices (i.e. still contained in the queue)
					if (oppData.flag == 0) {
						Integer newWeight = e.getWeight() + ltData.weight;
						
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

		ArrayList<Node> path = new ArrayList<Node>();
		Node limbo = end;
		Vertex lastVertex = null;

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

		while (limbo != null && limbo != start) {
			path.add(limbo);
			
			if (limbo instanceof Vertex) {
				lastVertex = (Vertex)limbo;
				limbo = vertexData.get(limbo).backEdge;
				
			} else if (limbo instanceof SegEdge) {
				limbo = ((SegEdge)limbo).getOpposite(lastVertex);
			}
		}

		//System.out.println("FINISHED!");

		return path;
	}
}
