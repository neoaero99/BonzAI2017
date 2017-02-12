package Castles.util.graph;

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
	 * TODO
	 * 
	 * @param rID
	 * @return
	 */
	public int indexOf(String rID) {
		for (int idx = 0; idx < waypointIDs.length; ++idx) {
			if (rID.equals(waypointIDs[idx])) {
				return idx;
			}
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
