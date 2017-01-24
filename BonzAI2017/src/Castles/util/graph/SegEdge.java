package Castles.util.graph;

import Castles.Objects.RallyPoint;
import Castles.util.VectorND;
import bonzai.Position;

/**
 * The segmented edge class is used for the edges of the BonzAI Graph structure.
 * It contains a number of way points, which break the edge up into equal
 * segments based on its weighted.
 * 
 * @author Joshua Hooker
 *
 */
public class SegEdge extends WeightedEdge<RallyPoint, Integer> {
	private RallyPoint[] waypoints;
	
	public SegEdge(int weight) {
		super(weight);
		waypoints = new RallyPoint[weight];
	}
	
	@Override
	public void setElement(Integer e) {
		super.setElement(e);
		updateWayPoints();
	}
	
	@Override
	public void setFirst(Vertex<RallyPoint, Integer> node) {
		super.setFirst(node);
		updateWayPoints();
	}
	
	@Override
	public void setSecond(Vertex<RallyPoint, Integer> node) {
		super.setSecond(node);
		updateWayPoints();
	}
	
	/**
	 * Space out the way points of this edge based on the weight associated with
	 * the edge and the distance between the vertices connected to this edge.
	 */
	private void updateWayPoints() {
		if (getElement() != waypoints.length) {
			// Resize the way points array to match the weight of the edge
			waypoints = new RallyPoint[ getElement() ];
		}
		
		if (getFirst() != null && getSecond() != null) {
			Position start = getFirst().getElement().getPosition();
			Position end = getSecond().getElement().getPosition();
			
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
					waypoints[idx - 1] = new RallyPoint(posX, posY,null);
					
				} else {
					waypoints[idx - 1].setPosition( new Position(posX, posY) );
				}
			}
		}
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
}
