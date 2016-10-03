package Castles.util.graph;

import java.util.HashMap;

import Castles.util.linkedlist.DualLinkList;
import Castles.util.priorityq.*;

public class GraphPathSet<E extends Comparable<E>, W extends Comparable<W>> {
	/**
	 * The set all distinct paths in the graph; all the paths are organized by
	 * the path's start and end vertices in the outer hashmap. Also, the set of
	 * paths, which connect to the same node, are organized by their length.
	 * 
	 * Yes, it is a hashmap, of hashmaps of doubly linked lists sorted by
	 * the edge weights, sorted by node pairs.
	 */
	private final HashMap<VertexPair<E, W>, HashMap<W, DualLinkList<WeightedEdge<E, W>>>> VPPathsSet;
	private final WeightedGraph<E, W> graph;
	
	public GraphPathSet(WeightedGraph<E, W> g) {
		VPPathsSet = new HashMap<VertexPair<E, W>, HashMap<W, DualLinkList<WeightedEdge<E, W>>>>();
		graph = g;
	}
	
	public DualLinkList<WeightedEdge<E, W>> getPath(/* Parameters? */) {
		// TODO
		return null;
	}
	
	public DualLinkList<WeightedEdge<E, W>> shortestPath(Vertex<E, W> start, Vertex<E, W> end) {
		HashMap<Vertex<E, W>, Integer> flags = new HashMap<Vertex<E, W>, Integer>();
		HashMap<Vertex<E, W>, PQEntry<Integer, Vertex<E, W>>> entryRefs = new HashMap<Vertex<E, W>, PQEntry<Integer, Vertex<E, W>>>();
		AdaptablePQ<Integer, Vertex<E, W>> remaining = new AdaptablePQ<Integer, Vertex<E, W>>();
		
		DualLinkList<Vertex<E, W>> vertices = graph.vertexList();
		
		for (Vertex<E, W> v : vertices) {
			flags.put(v, 0);
			entryRefs.put(v, remaining.insert(Integer.MAX_VALUE, v));
		}
		
		// TODO compute path with Dijkstra's algorithm
		
		return null;
	}
	
	public boolean pathExists(GraphPath<E, W> path) {
		// TODO
		return false;
	}
	
	public WeightedGraph<E, W> getGraph() { return graph; }
}
