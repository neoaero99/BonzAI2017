package Castles.util.graph;

import Castles.Objects.RallyPoint;
import Castles.util.VectorND;
import Castles.util.linkedlist.InvalidNodeException;
import bonzai.Position;

/**
 * The segmented edge class is used for the edges of the BonzAI Graph structure.
 * It contains a number of way points, which break the edge up into equal
 * segments based on its weighted.
 * 
 * @author Joshua Hooker
 */
public class SegEdge extends Node {
	
	// The nodes, which this edge connects
	public final Vertex first, second;
	private final RallyPoint[] waypoints;
	
	public SegEdge(int weight, Vertex f, Vertex s) {
		super( String.format("!%s-%s", f.ID, s.ID) );
		
		waypoints = new RallyPoint[weight];
		first = f;
		second = s;
		first.addConnection(this);
		second.addConnection(this);
		
		Position start = first.getElement().getPosition();
		Position end = second.getElement().getPosition();
		
		/* Create a direction vector, which is scaled by the the distance
		 * between the first and second vertices over the weight of this
		 * edge. */
		double dist = start.getDistanceBetween(end);
		VectorND dir = new VectorND(end.getX() - start.getX(),
				end.getY() - start.getY());
		dir.scalarMult( dist / ((waypoints.length + 1) * dir.magnitude()) );
		
		// Update the positions of each way point
		for (int idx = 1; idx <= waypoints.length; ++idx) {
			int posX = start.getX() + (int)(idx * dir.get(0));
			int posY = start.getY() + (int)(idx * dir.get(1));
			
			if (waypoints[idx - 1] == null) {
				waypoints[idx - 1] = new RallyPoint(posX, posY, null);
				
			} else {
				waypoints[idx - 1].setPosition( new Position(posX, posY) );
			}
		}
	}
	
	public int getWeight() {
		return waypoints.length;
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
	 * Get the way point associated with the given index value.
	 */
	public RallyPoint getWayPoint(int idx) {
		if (idx >= 0 && idx < waypoints.length) {
			return waypoints[idx];
		}
		
		return null;
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
		
		return String.format("%c %d %c", firstConnect, getWeight(),
				secondConnect);
	}
}
