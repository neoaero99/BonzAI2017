package Castles.util.graph;

import java.util.HashMap;

import Castles.util.linkedlist.DualLinkList;
import Castles.util.priorityq.*;

public class GraphPathSet<E extends Comparable<E>> {
	/**
	 * The set all distinct paths in the graph; all the paths are organized by
	 * the path's start and end vertices in the outer hashmap. Also, the set of
	 * paths, which connect to the same node, are organized by their length.
	 * 
	 * Yes, it is a hashmap, of hashmaps of doubly linked lists sorted by
	 * the edge weights, sorted by node pairs.
	 */
	private final HashMap<VertexPair<E, Integer>, HashMap<Integer, DualLinkList<WeightedEdge<E, Integer>>>> VPPathsSet;
	private final WeightedGraph<E, Integer> graph;
	
	public GraphPathSet(WeightedGraph<E, Integer> g) {
		VPPathsSet = new HashMap<VertexPair<E, Integer>, HashMap<Integer, DualLinkList<WeightedEdge<E, Integer>>>>();
		graph = g;
	}
	
	public DualLinkList<WeightedEdge<E, Integer>> getPath(/* Parameters? */) {
		// TODO
		return null;
	}
	
	public DualLinkList<WeightedEdge<E, Integer>> shortestPath(Vertex<E, Integer> start, Vertex<E, Integer> end) {
		DualLinkList<Vertex<E, Integer>> vertices = graph.vertexList();
		
		HashMap<Vertex<E, Integer>, ExtraData> vertexData = new HashMap<Vertex<E, Integer>, ExtraData>();
		AdaptablePQ<Integer, Vertex<E, Integer>> remaining = new AdaptablePQ<Integer, Vertex<E, Integer>>( 
				vertices.size(), new MinComparator<PQEntry<Integer, Vertex<E, Integer>>>() );
		
		
		
		for (Vertex<E, Integer> v : vertices) {
			int iniWeight = (v == start) ? 0 : Integer.MAX_VALUE;
			vertexData.put(v, new ExtraData(0, iniWeight, remaining.insert(iniWeight, v), null));
		}
		
		while (!remaining.isEmpty()) {
			Vertex<E, Integer> least = remaining.removeMax();
			ExtraData ltData = vertexData.get(least);
			ltData.flag = 1;
			
			if (least == end) { break; }
			
			DualLinkList<WeightedEdge<E, Integer>> incEdges = least.incidentEdges();
			
			for (WeightedEdge<E, Integer> e : incEdges) {
				Vertex<E, Integer> opposite = e.getOpposite(least);
				ExtraData oppData = vertexData.get(opposite);
				
				if (oppData.flag == 0) {
					Integer newWeight = e.getElement() + ltData.weight;
					
					if (newWeight < oppData.entryRef.getKey()) {
						remaining.replaceKey(oppData.entryRef.getIndex(), newWeight);
						oppData.weight = newWeight;
						oppData.backEdge = e;
					}
				}
			}
		}
		
		DualLinkList<WeightedEdge<E, Integer>> path = new DualLinkList<WeightedEdge<E, Integer>>();
		Vertex<E, Integer> limbo = end;
		ExtraData data = vertexData.get(limbo);
		
		while (limbo != null && limbo != start && data.backEdge != null) {
			path.addToFront(data.backEdge);
			limbo = data.backEdge.getOpposite(limbo);
		}
		
		return path;
	}
	
	public boolean pathExists(GraphPath<E, Integer> path) {
		// TODO
		return false;
	}
	
	public WeightedGraph<E, Integer> getGraph() { return graph; }
	
	private class ExtraData {
		private int flag;
		private int weight;
		private PQEntry<Integer, Vertex<E, Integer>> entryRef;
		private WeightedEdge<E, Integer> backEdge;
		
		public ExtraData(int f, int w, PQEntry<Integer, Vertex<E, Integer>>
								entry, WeightedEdge<E, Integer> bEdge) {
			
			flag = f;
			weight = w;
			entryRef = entry;
			backEdge = bEdge;
		}
	}
}
