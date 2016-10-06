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
	private final HashMap<VertexPair<E, Double>, HashMap<Double, DualLinkList<WeightedEdge<E, Double>>>> VPPathsSet;
	private final WeightedGraph<E, Double> graph;
	
	public GraphPathSet(WeightedGraph<E, Double> g) {
		VPPathsSet = new HashMap<VertexPair<E, Double>, HashMap<Double, DualLinkList<WeightedEdge<E, Double>>>>();
		graph = g;
	}
	
	public DualLinkList<WeightedEdge<E, Double>> getPath(/* Parameters? */) {
		// TODO
		return null;
	}
	
	public DualLinkList<WeightedEdge<E, Double>> shortestPath(Vertex<E, Double> start, Vertex<E, Double> end) {
		DualLinkList<Vertex<E, Double>> vertices = graph.vertexList();
		
		HashMap<Vertex<E, Double>, ExtraData> vertexData = new HashMap<Vertex<E, Double>, ExtraData>();
		AdaptablePQ<Double, Vertex<E, Double>> remaining = new AdaptablePQ<Double, Vertex<E, Double>>( 
				vertices.size(), new MinComparator<PQEntry<Double, Vertex<E, Double>>>() );
		
		
		
		for (Vertex<E, Double> v : vertices) {
			double iniWeight = (v == start) ? 0.0 : Double.MAX_VALUE;
			vertexData.put(v, new ExtraData(0, iniWeight, remaining.insert(iniWeight, v), null));
		}
		
		while (!remaining.isEmpty()) {
			Vertex<E, Double> least = remaining.removeMax();
			ExtraData ltData = vertexData.get(least);
			ltData.flag = 1;
			
			if (least == end) { break; }
			
			DualLinkList<WeightedEdge<E, Double>> incEdges = least.incidentEdges();
			
			for (WeightedEdge<E, Double> e : incEdges) {
				Vertex<E, Double> opposite = e.getOpposite(least);
				ExtraData oppData = vertexData.get(opposite);
				
				if (oppData.flag == 0) {
					Double newWeight = e.getElement() + ltData.weight;
					
					if (newWeight < oppData.entryRef.getKey()) {
						remaining.replaceKey(oppData.entryRef.getIndex(), newWeight);
						oppData.weight = newWeight;
						oppData.backEdge = e;
					}
				}
			}
		}
		
		DualLinkList<WeightedEdge<E, Double>> path = new DualLinkList<WeightedEdge<E, Double>>();
		Vertex<E, Double> limbo = end;
		ExtraData data = vertexData.get(limbo);
		
		while (limbo != null && limbo != start && data.backEdge != null) {
			path.addToFront(data.backEdge);
			limbo = data.backEdge.getOpposite(limbo);
		}
		
		return path;
	}
	
	public boolean pathExists(GraphPath<E, Double> path) {
		// TODO
		return false;
	}
	
	public WeightedGraph<E, Double> getGraph() { return graph; }
	
	private class ExtraData {
		private int flag;
		private double weight;
		private PQEntry<Double, Vertex<E, Double>> entryRef;
		private WeightedEdge<E, Double> backEdge;
		
		public ExtraData(int f, double w, PQEntry<Double, Vertex<E, Double>>
								entry, WeightedEdge<E, Double> bEdge) {
			
			flag = f;
			weight = w;
			entryRef = entry;
			backEdge = bEdge;
		}
	}
}
