package Castles.util.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
	public static void main(String[] args){
		WeightedGraph<Integer, Integer> g = new WeightedGraph<Integer, Integer>();
		GraphPathSet<Integer> gpath = new GraphPathSet<Integer>(g);
		Vertex<Integer, Integer> node0, node1, node2, node3, node4, node5, node6,node7, node8, node9, node10;
		WeightedEdge<Integer, Integer> edge1, edge2, edge3, edge4, edge5, edge6, edge7,edge8,edge9,edge10,edge11,edgei1,edgei2,edgei3,edgei4;
		
		node1 = new Vertex<Integer, Integer>(1);
		node2 = new Vertex<Integer, Integer>(2);
		node3 = new Vertex<Integer, Integer>(3);
		node4 = new Vertex<Integer, Integer>(4);
		node5 = new Vertex<Integer, Integer>(5);
		node0 = new Vertex<Integer, Integer>(0);
		node6 = new Vertex<Integer, Integer>(6);
		node7 = new Vertex<Integer, Integer>(7);
		node8 = new Vertex<Integer, Integer>(8);
		node9 = new Vertex<Integer, Integer>(9);
		node10 = new Vertex<Integer, Integer>(10);
		
		edge1 = new WeightedEdge<Integer, Integer>(2);
		edge2 = new WeightedEdge<Integer, Integer>(1);
		edge3 = new WeightedEdge<Integer, Integer>(2);
		edge4 = new WeightedEdge<Integer, Integer>(2);
		edge5 = new WeightedEdge<Integer, Integer>(1);
		edge6 = new WeightedEdge<Integer, Integer>(2);
		edge7 = new WeightedEdge<Integer, Integer>(1);
		edge8 = new WeightedEdge<Integer, Integer>(2);
		edge9 = new WeightedEdge<Integer, Integer>(1);
		edge10 = new WeightedEdge<Integer, Integer>(2);
		edge11 = new WeightedEdge<Integer, Integer>(1);
		edgei1 = new WeightedEdge<Integer, Integer>(11);
		edgei2 = new WeightedEdge<Integer, Integer>(7);
		edgei3 = new WeightedEdge<Integer, Integer>(5);
		edgei4 = new WeightedEdge<Integer, Integer>(9);
		
		WeightedGraph.connect(node0, node1, edge1);
		WeightedGraph.connect(node1, node2, edge2);
		WeightedGraph.connect(node2, node3, edge3);
		WeightedGraph.connect(node3, node4, edge4);
		WeightedGraph.connect(node4, node5, edge5);
		WeightedGraph.connect(node5, node6, edge6);
		WeightedGraph.connect(node6, node7, edge7);
		WeightedGraph.connect(node7, node8, edge8);
		WeightedGraph.connect(node8, node9, edge9);
		WeightedGraph.connect(node9, node10, edge10);
		WeightedGraph.connect(node10, node0, edge11);
		WeightedGraph.connect(node0, node5, edgei1);
		WeightedGraph.connect(node2, node6, edgei2);
		WeightedGraph.connect(node4, node10, edgei3);
		WeightedGraph.connect(node3, node8, edgei4);
		g.addNode(node1);		
		g.addNode(node2);
		g.addNode(node3);
		g.addNode(node4);
		g.addNode(node5);
		g.addNode(node0);		
		g.addNode(node6);
		g.addNode(node7);
		g.addNode(node8);
		g.addNode(node9);
		g.addNode(node10);
		
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
		g.addEdge(edgei1);
		g.addEdge(edgei2);
		g.addEdge(edgei3);
		g.addEdge(edgei4);
		
		DualLinkList<WeightedEdge<Integer, Integer>> path = gpath.shortestPath(node0, node1);
		for(WeightedEdge<Integer, Integer> t: path){
			System.out.printf("([%s] to [%s]) ", t.getFirst(), t.getSecond());
		}

	}
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
		
		System.out.println(remaining.toString(1));
		
		while (!remaining.isEmpty()) {
			Vertex<E, Integer> least = remaining.removeMax();
			ExtraData ltData = vertexData.get(least);
			ltData.flag = 1;
			System.out.printf("%s\n", remaining);
			if (least == end) { break; }
			
			DualLinkList<WeightedEdge<E, Integer>> incEdges = least.incidentEdges();
			
			for (WeightedEdge<E, Integer> e : incEdges) {
				Vertex<E, Integer> opposite = e.getOpposite(least);
				
				if (opposite != least) {
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
		}
		
		System.out.println("PATH CALCULATED!");
		/**/
		Set<Vertex<E, Integer>> keys = vertexData.keySet();
		
		System.out.print("[ ");
		for (Vertex<E, Integer> v : keys) {
			System.out.printf("%s : %s; ", v, vertexData.get(v).backEdge);
		}
		System.out.println("]");
		/**/
		DualLinkList<WeightedEdge<E, Integer>> path = new DualLinkList<WeightedEdge<E, Integer>>();
		Vertex<E, Integer> limbo = end;
		System.out.println(limbo);
		ExtraData data = vertexData.get(limbo);
		
		while (limbo != null && limbo != start && data.backEdge != null) {
			path.addToFront(data.backEdge);
			limbo = data.backEdge.getOpposite(limbo);
			data = vertexData.get(limbo);
		}
		
		System.out.println("FINISHED!");
		
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
