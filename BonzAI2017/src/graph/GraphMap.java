package graph;

import linkedlist.*;

public class GraphMap<E, W extends Comparable<W>> {
	
	private DualLinkList<GraphNode<E, W>> nodes;
	private DualLinkList<GraphEdge<E, W>> edges;
	
	public GraphMap() {
		nodes = new DualLinkList<GraphNode<E, W>>();
		edges = new DualLinkList<GraphEdge<E, W>>();
	}
}
