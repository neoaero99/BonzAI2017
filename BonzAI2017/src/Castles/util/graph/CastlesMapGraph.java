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
 * A simple graph, which cannot be modified once it is created.
 * 
 * @author Joshua Hooker
 */
public class CastlesMapGraph {
	
	/**
	 * The graphs lists of nodes and edges.
	 */
	private final HashMap<String, Vertex> vertices;
	private final HashMap<String, SegEdge> edges;
	
	/**
	 * Creates a graph with the given set of vertices and edges.
	 * 
	 * @param V	The set of vertices in the graph
	 * @param E	The set of edges in the graph
	 */
	public CastlesMapGraph(ArrayList<Vertex> V, ArrayList<SegEdge> E) {
		vertices = new HashMap<String, Vertex>();
		edges = new HashMap<String, SegEdge>();
		
		// Add all vertices to the graph
		for (Vertex v : V) {
			vertices.put(v.ID, v);
		}
		
		// Add all edges to the graph
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
			Vertex node = new Vertex(Integer.toString(idx));
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
					
				} else if (v1 == null && v.ID.equals(connections[idx][1])) {
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
		
		/**
		
		Vertex v0 = vertexList.get(0),
			   v1 = vertexList.get(1),
			   v3 = vertexList.get(3),
			   v5 = vertexList.get(5);
		SegEdge e5 = edgeList.get(5),
				e6 = edgeList.get(6);
		
		System.out.printf("%s, %s : %b\n", v0, v1, g.areAdjacent(v0.ID, v1.ID));
		System.out.printf("%s, %s : %b\n", v0, v3, g.areAdjacent(v0.ID, v3.ID));
		System.out.printf("%s, %s : %b\n", v5, e5, g.areAdjacent(v5.ID, e5.wayPointIDs()[0]));
		System.out.printf("%s, %s : %b\n", e5, e6, g.areAdjacent(e5.wayPointIDs()[0], e6.wayPointIDs()[0]));
		
		/**/
		
		System.out.println("TEST");
	}
	
	/**
	 * Returns whether the positions with the given ID values are adjacent on
	 * the map. Keep in mind that vertices are not adjacent to one another,
	 * instead the end waypoints on an edge adjacent to the vertices
	 * connected to the edge.
	 * 
	 * @param rID1	A position ID
	 * @param rID2	Another position ID
	 * @return		Are there positions adjacent?
	 */
	public boolean areAdjacent(String rID1, String rID2) {
		Node n1 = getVertex(rID1);
		Node n2 = getVertex(rID2);
		
		if (n1 == null) {
			n1 = getEdge(rID1);
		}
		
		if (n2 == null) {
			n2 = getEdge(rID2);
		}
		
		if (n1 == null || n2 == null) {
			return false;
		}
		
		if (n1 instanceof SegEdge && n2 instanceof SegEdge) {
			SegEdge e1 = (SegEdge)n1;
			SegEdge e2 = (SegEdge)n2;
			
			if (e1 == e2) {	// Are the way points on the same edge?
				// Are the indices of the way points 1 apart?
				return Math.abs(e1.indexOf(rID1) - e2.indexOf(rID2)) == 1;
			}
			
		} else if (n1 instanceof Vertex && n2 instanceof SegEdge) {
			return testEdgeVertexConnection((SegEdge)n2, rID2, (Vertex)n1);
			
		} else if (n1 instanceof SegEdge && n2 instanceof Vertex) {
			return testEdgeVertexConnection((SegEdge)n1, rID1, (Vertex)n2);
		}
		
		return false;
	}
	
	/**
	 * Determines if the given waypoint ID, which corresponds to a waypoint on
	 * the given edge, e, is adjacent to the given vertex, v.
	 * 
	 * @param e				Some edge in the graph
	 * @param wayPointID	The ID of a waypoint on an edge
	 * @param v				A vertex connected to e
	 * @return				Are v and the waypoint of wayPointID adjacent?
	 */
	private boolean testEdgeVertexConnection(SegEdge e, String wayPointID, Vertex v) {
		int idx = e.indexOf(wayPointID);
		return (e.isConnected(v)) && ( (idx == 0 && e.first == v)
									|| (idx == (e.getWeight() - 1) && e.second == v) );
	}
	
	/**
	 * Searches the graph for the vertex with the given ID value and returns
	 * that vertex, or null if no such vertex exists.
	 * 
	 * @param eID	The ID of the vertex to find
	 * @return		The vertex with the given ID or null if no such vertex
	 * 				exists
	 */
	public Vertex getVertex(String vID) {
		return vertices.get(vID);
	}
	
	/**
	 * Searches the graph for the edge with the given ID value and returns that
	 * edge, or null if no such edge exists.
	 * 
	 * @param eID	The ID of the edge to find
	 * @return		The edge with the given ID or null if no such edge exists
	 */
	public SegEdge getEdge(String eID) {
		int idxOfColon = eID.indexOf(':');
		
		if (idxOfColon > 0) {
			// The ID may be that of a way point associated with the edge
			String superID = eID.substring(0, idxOfColon);
			return edges.get(superID);
		}
		
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
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getPath(HashMap<IDPair, ArrayList<String>> pathsMap,
			Vertex v0, Vertex v1) {
		
		Set<IDPair> endpoints = pathsMap.keySet();
		
		for (IDPair idp : endpoints) {
			// Check either direction for a vertex pair
			if ( (idp.first.equals(v0.ID) && idp.second.equals(v1.ID)) ||
					(idp.first.equals(v1.ID) && idp.second.equals(v0.ID)) ) {
				
				ArrayList<String> path = pathsMap.get(idp);
				
				// Reverse the path if the start and end vertices are switched
				if (path != null && !path.get(0).equals(v0.ID)) {
					ArrayList<String> reverse = new ArrayList<String>();
					
					for (int idx = path.size() - 1; idx >= 0; --idx) {
						reverse.add(path.get(idx));
					}
					
					return reverse;
					
				} else {
					return (ArrayList<String>)path.clone();
				}
			}
		}
		
		// Invalid start or end vertex
		return new ArrayList<String>();
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
				boolean pathExists = getPath(pathIDsMap, u, v).size() > 0;
				
				if (areEqual || pathExists) {
					continue;
				}
				
				IDPair endpoints = new IDPair(v.ID, u.ID);
				ArrayList<Node> path = shortestPath(u, v);
				
				/* Convert the path into a list of IDs of vertices and edges in
				 * the path and put the list into the map */
				ArrayList<String> pathIDs = new ArrayList<String>();
				
				for (int nIdx = 0; nIdx < path.size(); ++nIdx) {
					Node n = path.get(nIdx);
					
					if (n instanceof Vertex) {
						pathIDs.add(((Vertex)n).ID);
						
					} else if (n instanceof SegEdge) {
						SegEdge e = (SegEdge)n;
						String[] wpIDs = e.wayPointIDs();
						/* Depending on whether the prior vertex is the first
						 * or second reference of this edge, the way points
						 * need to be added in forward or reverse order
						 * respectively */
						if (nIdx > 0 && path.get(nIdx - 1) == e.first) {
							for (int idx = 0; idx < wpIDs.length; ++idx) {
								pathIDs.add(wpIDs[idx]);
							}
							
						} else {
							for (int idx = wpIDs.length - 1; idx >= 0; --idx) {
								pathIDs.add(wpIDs[idx]);
							}
						}
					}
					
				}
				
				//System.out.printf("%s\n", pathIDs);
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
		
		if (limbo != null) {
			path.add(start);
		}

		//System.out.println("FINISHED!");

		return path;
	}
	
	/**
	 * @return	A copy of this graph
	 */
	protected CastlesMapGraph clone() {
		
		ArrayList<Vertex> vertexCopies = new ArrayList<Vertex>();
		ArrayList<SegEdge> edgeCopies = new ArrayList<SegEdge>();
		
		HashMap<Integer, Vertex> OldToNewVertex = new HashMap<Integer, Vertex>();
		
		// Copy the vertices and add them to a map
		ArrayList<Vertex> vertices = vertexList();
		for (Vertex v : vertices) {
			Vertex vertexCopy = new Vertex(v.ID);
			
			vertexCopies.add(vertexCopy);
			OldToNewVertex.put(v.hashCode(), vertexCopy);
		}
		
		// Copy the edges and the connections between edges and vertices
		ArrayList<SegEdge> edges = edgeList();
		for (SegEdge e : edges) {
			// Connect the new vertices with the new edge
			Vertex first = OldToNewVertex.get( e.first.hashCode() );
			Vertex second = OldToNewVertex.get( e.second.hashCode() );
			
			SegEdge edgeCopy = new SegEdge(e.getWeight(), first, second);
			edgeCopies.add(edgeCopy);
		}
		
		return new CastlesMapGraph(vertexCopies, edgeCopies);
	}
}
