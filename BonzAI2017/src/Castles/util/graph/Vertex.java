package Castles.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import Castles.Objects.RallyPoint;

/**
 * A node in the CastlesMapGraph, which is connected to some number edges in the
 * graph. A vertex contains a reference to the ID of a rally point, or building,
 * which is associated with the vertex.
 * 
 * @author Joshua Hooker
 */
public class Vertex extends Node {
	
	// The vertex's adjacency hash map or set of all adjacent edges
	private final HashMap<String, SegEdge> incEdges;
	
	/**
	 * Creates a new vertex, whose element is given.
	 * 
	 * @param r	The element to store in the vertex
	 */
	public Vertex(String ID) {
		super(ID);
		incEdges = new HashMap<String, SegEdge>();
	}
	
	/**
	 * Adds the given edge to this vertex's adjacency hashmap.
	 * 
	 * @param edge	The edge to connect to this node
	 */
	protected void addConnection(SegEdge edge) {
		incEdges.put(edge.ID, edge);
	}
	
	/**
	 * Compiles a list of IDs of the waypoints, which connect this node to each
	 * of its incident edges.
	 * 
	 * @return	A list of IDs of positions adjacent to this vertex
	 */
	public ArrayList<String> adjPositionIDs() {
		ArrayList<SegEdge> incEdges = incidentEdges();
		ArrayList<String> wpIDs = new ArrayList<String>();
		
		for (SegEdge e : incEdges) {
			// Add the ID of each adjacent waypoint
			wpIDs.add(e.getWayPointConnection(this));
		}
		
		return wpIDs;
	}
	
	/**
	 * @return	A list of edges that connect to this vertex
	 */
	public ArrayList<SegEdge> incidentEdges() {
		ArrayList<SegEdge> dupEdges = new ArrayList<SegEdge>();
		Collection<SegEdge> edges = incEdges.values();
		
		for (SegEdge edge : edges) {
			dupEdges.add(edge);
		} 
		
		return dupEdges;
	}
	
	/**
	 * Returns the number of edges connected to this vertex.
	 * 
	 * @return	the degree of this vertex
	 */
	public int degree() { return incEdges.size(); }
	
	/**
	 * Determines if the given node is adjacent to this vertex.
	 * 
	 * @param node	A non-null graph node
	 * @return		If the given node is adjacent to this vertex
	 */
	public boolean isAdjacent(Vertex node) {
		Collection<SegEdge> edgeSet = incEdges.values();
		
		for (SegEdge e : edgeSet) {
			if (e.isConnected(node)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Return a list of nodes, which are adjacent to this vertex.
	 * 
	 * @return	A list of adjacent vertices
	 */
	public ArrayList<Vertex> adjacentVertices() {
		ArrayList<Vertex> adjVertices = new ArrayList<Vertex>();
		Collection<SegEdge> edgeSet = incEdges.values();
		
		for (SegEdge edge : edgeSet) {
			adjVertices.add( edge.getOpposite(this) );
		}
		
		return adjVertices;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			Vertex node = (Vertex)obj;
			
			// Compare elements
			if (ID == null && node.ID == null) {	
				return true;
				
			} else if (ID == null || node.ID == null) {
				return false;
				
			} else {
				return node.ID.equals(ID);
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("( %s )", ID);
	}
}
