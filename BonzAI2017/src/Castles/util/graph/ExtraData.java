package Castles.util.graph;

import Castles.util.priorityq.PQEntry;

/**
 * Contains the data associated with a vertex for Dijkstra's Algorithm.
 * 
 * @author Joshua Hooker
 */
public class ExtraData {
	/**
	 * Is this vertex visited?
	 * 0 -> no,
	 * 1 -> yes
	 */
	protected int flag;
	
	// The current distance associated with this vertex
	protected int weight;
	
	// The reference to the vertex's entry in the queue
	protected PQEntry<Integer, Vertex> entryRef;
	
	/**
	 * The reference to the edge that connects this vertex to the vertex
	 * prior to this one in the shortest path, which is used to build the
	 * path at the end of the algorithm
	 */
	protected SegEdge backEdge;

	public ExtraData(int f, int w, PQEntry<Integer, Vertex> entry,
			SegEdge bEdge) {

		flag = f;
		weight = w;
		entryRef = entry;
		backEdge = bEdge;
	}
	
	public String toString() {
		return String.format("[%s]", entryRef);
	}
}
