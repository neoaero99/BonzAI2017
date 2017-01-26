package Castles.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import Castles.Objects.RallyPoint;
import Castles.util.priorityq.AdaptablePQ;
import Castles.util.priorityq.MinComparator;
import Castles.util.priorityq.PQEntry;

/**
 * 
 * 
 * @author Joshua Hooker
 */
public class CastlesMapGraph {
	
	/**
	 * The graphs lists of nodes and edges.
	 */
	private final HashMap<String, Vertex> vertices;
	private final HashMap<String, SegEdge> edges;
	
	public CastlesMapGraph(ArrayList<Vertex> V, ArrayList<SegEdge> E) {
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
	 * Testing the path generation for a graph
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		
		/**/
		
		ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
		ArrayList<SegEdge> edgeList = new ArrayList<SegEdge>();
		
		for (int idx = 0; idx < 11; ++idx) {
			Vertex node = new Vertex(new RallyPoint(0, idx, Integer.toString(idx)));
			vertexList.add(node);
		}
		
		String[][] connections = new String[15][];
		int idx = 0;
		
		connections[idx++] = new String[] { "0", "1" };
		connections[idx++] = new String[] { "1", "2" };
		connections[idx++] = new String[] { "2", "3" };
		connections[idx++] = new String[] { "3", "4" };
		connections[idx++] = new String[] { "4", "5" };
		connections[idx++] = new String[] { "5", "6" };
		connections[idx++] = new String[] { "6", "7" };
		connections[idx++] = new String[] { "7", "8" };
		connections[idx++] = new String[] { "8", "9" };
		connections[idx++] = new String[] { "9", "10" };
		connections[idx++] = new String[] { "10", "0" };
		connections[idx++] = new String[] { "0", "5" };
		connections[idx++] = new String[] { "2", "6" };
		connections[idx++] = new String[] { "4", "10" };
		connections[idx++] = new String[] { "3", "8" };
		
		int[] weights = new int[15];
		idx = 0;
		
		weights[idx++] = 2;
		weights[idx++] = 1;
		weights[idx++] = 2;
		weights[idx++] = 2;
		weights[idx++] = 1;
		weights[idx++] = 2;
		weights[idx++] = 1;
		weights[idx++] = 2;
		weights[idx++] = 1;
		weights[idx++] = 2;
		weights[idx++] = 1;
		weights[idx++] = 11;
		weights[idx++] = 7;
		weights[idx++] = 5;
		weights[idx++] = 9;
		
		for (idx = 0; idx < connections.length; ++idx) {
			Vertex v0 = null;
			Vertex v1 = null;
			
			for (Vertex v : vertexList) {
				if (v0 == null && v.ID.equals(connections[idx][0])) {
					v0 = v; 
					
				} else if (v1 == null && v.ID.equals(connections[idx][0])) {
					v1 = v;
				}
			}
			
			edgeList.add( new SegEdge(weights[idx], v0, v1) );
		}
		
		/**/

		CastlesMapGraph g = new CastlesMapGraph(vertexList, edgeList);
		HashMap<IDPair, ArrayList<String>> SPPathsMap = g.generatePaths();
		System.out.printf("%s\n", g);
		
		Set<IDPair> keys = SPPathsMap.keySet();
		
		for (IDPair sp : keys) {
			System.out.printf("%s -> %s\n", sp, SPPathsMap.get(sp));
		}
		
		/**/
		
System.out.println("TEST");
		
System.out.println("TEST");
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
	
	/**
	 * Returns the path connecting the given two vertices, or null if no such path
	 * exists. It is assumed that v0 != v1.
	 * 
	 * @param pathsMap	The list of paths to search in the graph
	 * @param v0		A vertex in the graph
	 * @param v1		Another vertex in the graph
	 * @return			A path connecting v0 and v1, in the graph
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
	/**
	 * Generates a list of paths, which are associated with the IDs of the
	 * vertices, which are the end points of the path. The paths themselves
	 * are a list of IDs of all the nodes in the path.
	 * 
	 * @param g	The graph, for which to generate the paths
	 * @return	The list of paths mapped to the IDs of their end points
	 */
	public HashMap<IDPair, ArrayList<String>> generatePaths() {
		HashMap<IDPair, ArrayList<String>> pathIDsMap = new HashMap<IDPair, ArrayList<String>>();
		ArrayList<Vertex> vertices = vertexList();
		
		for (Vertex v : vertices) {
			for (Vertex u : vertices) {
				boolean areEqual = v.equals(u);
				boolean pathExists = getPath(pathIDsMap, u, v) != null;
				
				if (areEqual || pathExists) {
					continue;
				}
				
				IDPair endpoints = new IDPair(v.ID, u.ID);
				ArrayList<Node> path = shortestPath(u, v);
				
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
	public ArrayList<Node> shortestPath(Vertex start, Vertex end) {
		
		// The list of vertices in the graph
		ArrayList<Vertex> vertices = vertexList();
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