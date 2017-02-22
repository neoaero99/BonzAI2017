package Castles.util.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The segmented edge class is used for the edges of the BonzAI Graph structure.
 * It contains a number of way points, which break the edge up into equal
 * segments based on its weight.
 * 
 * @author Joshua Hooker
 */
public class SegEdge extends Node {
	
	// The nodes, which this edge connects
	public final Vertex first, second;
	private final String[] waypointIDs;
	
	/**
	 * Creates an edge, with the given ID, which connects the two given edges.
	 * 
	 * @param weight	The number of way points on the edge
	 * @param f			One vertex connected to the edge
	 * @param s			The other vertex connected to the edge
	 */
	public SegEdge(int weight, Vertex f, Vertex s) {
		super( String.format("!%s-%s", f.ID, s.ID) );
		
		waypointIDs = new String[weight];
		
		for (int idx = 0; idx < weight; ++idx) {
			waypointIDs[idx] = String.format("%s:%s", ID, idx + 1);
		}
		
		first = f;
		second = s;
		first.addConnection(this);
		second.addConnection(this);
	}
	
	/**
	 * Testing the various methods of a SegEdge
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		Vertex v0 = new Vertex("P0");
		Vertex v1 = new Vertex("V0");
		Vertex v2 = new Vertex("R0");
		
		SegEdge e0 = new SegEdge(1, v0, v1);
		SegEdge e1 = new SegEdge(3, v1, v2);
		
		System.out.printf("%s\n", e1.adjPositionIDs(e1.waypointIDs[2]));
		System.out.printf("%s\n", e1.getWayPointConnection(v2));
	}
	
	/**
	 * Returns the number of way points associated with the edge.
	 * 
	 * @return	The length of the edge in terms of way points
	 */
	public int getWeight() {
		return waypointIDs.length;
	}
	
	/**
	 * Return the opposing node connected to this edge of the give node. If
	 * node is not equal to either edge, then null is returned.
	 * 
	 * @param node	A node connected to this edge
	 * @return		The opposite graph node connected to this edge that is not
	 * 				equal to the given node
	 * @throws		InvalidNodeException- if node is not connected to this edge
	 */
	public Vertex getOpposite(Vertex node) {
		
		if (first == node) {
			return second;
			
		} else if (second == node) {
			return first;
		}
		
		return null;
	}
	
	/**
	 * Determines if either first or second are references to node's address
	 * space.
	 * 
	 * @param node	The node to check this edge for a connection
	 * @return		If first or second are references to node's address space
	 */
	public boolean isConnected(Vertex node) {
		return node != null && (node == first || node == second);
	}
	
	/**
	 * Returns the ID of the waypoint that is adjacent to the given connected
	 * node. If the node is not connected to this edge, then null is returned.
	 * 
	 * @param node	A vertex connected to this edge
	 * @return		The ID of the adjcent waypoint
	 */
	protected String getWayPointConnection(Vertex node) {
		
		if (node == first) {
			return waypointIDs[0];
			
		} else if (node == second) {
			return waypointIDs[waypointIDs.length - 1];
		}
		
		return null;
	}
	
	/**
	 * The IDs of the positions adjacent to the waypoint with the given ID.
	 * 
	 * @param rID	The ID of the waypoint
	 * @return		IDs of the adjacent waypoints
	 */
	public ArrayList<String> adjPositionIDs(String rID) {
		// Find the index of the waypoint with the given ID
		int wpIdx = indexOf(rID);
		ArrayList<String> adjWPIDs = new ArrayList<String>();
		
		if (wpIdx != -1) {
			// The given ID is valid
			if (waypointIDs.length == 1) {
				// This edge has only one waypoint
				adjWPIDs.add(first.ID);
				adjWPIDs.add(second.ID);
				
			} else if (wpIdx == 0) {
				// The waypoint is adjacent first vertex
				adjWPIDs.add(first.ID);
				adjWPIDs.add(waypointIDs[wpIdx + 1]);
				
			} else if (wpIdx == (waypointIDs.length - 1)) {
				// The waypoint is adjacent to the second vertex
				adjWPIDs.add(waypointIDs[wpIdx - 1]);
				adjWPIDs.add(second.ID);
				
			} else {
				// The waypoint is adjacent to neither vertex
				adjWPIDs.add(waypointIDs[wpIdx - 1]);
				adjWPIDs.add(waypointIDs[wpIdx + 1]);
			}
		}
		
		return adjWPIDs;
	}
	
	/**
	 * The index of the waypoint associated with the given ID, if it exists. If
	 * no waypoint with the given ID exists, then -1 is returned.
	 * 
	 * @param rID	The ID of the waypoint
	 * @return		The index of the waypoint with the given ID
	 */
	public int indexOf(String rID) {
		int wpIdx = -1;
		
		try {
			/* Parse the waypoint's edge ID and index from the ID of the
			 * waypoint */
			int colonIdx = rID.indexOf(":");
			String prefix = rID.substring(0, colonIdx);
			wpIdx = Integer.parseInt( rID.substring(colonIdx + 1, rID.length()) );
			
			if (ID.equals(prefix) && wpIdx > 0 && wpIdx <= waypointIDs.length) {
				return wpIdx - 1;
			}
			
		} catch (Exception Ex) {
			Ex.printStackTrace();
		}
		
		return -1;
	}
	
	protected String[] wayPointIDs() {
		return waypointIDs.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SegEdge) {
			SegEdge edge = (SegEdge)obj;
			// Compare edge weights
			return getWeight() == edge.getWeight();
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		/* Display weight value and if the edge has a connection for first and
		 * second */
		char firstConnect = (first == null) ? '(' : '<';
		char secondConnect = (second == null) ? ')' : '>';
		
		return String.format("%c %s %d %c", firstConnect, ID, getWeight(),
				secondConnect);
	}
}
