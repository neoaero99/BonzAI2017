package Castles.util.graph;

import Castles.Objects.RallyPoint;
import Castles.util.VectorND;
import bonzai.Position;

/**
 * This is the edge class for the BonzAI Graph structure, that will contain a number of way points associated with the weight of the edge.
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
	 * Space out the way points of this edge based on the weight associated with the edge
	 */
	private void updateWayPoints() {
		if (getElement() != waypoints.length) {
			/* Resize the way points array to match the weight of the edge */
			waypoints = new RallyPoint[ getElement() ];
		}
		
		if (getFirst() != null && getSecond() != null) {
			/* Update the positions of each way point based on the vertices connected to this edge */
			Position start = getFirst().getElement().getPosition();
			Position end = getSecond().getElement().getPosition();
			
			double dist = start.getDistanceBetween(end);
			
			VectorND direction = new VectorND(end.getX() - start.getX(), end.getY() - start.getY());
			direction.normalize();
			
			for (int idx = 0; idx < waypoints.length; ++idx) {
				int posX = start.getX() + (int)(idx * direction.get(0) / dist);
				int posY = start.getY() + (int)(idx * direction.get(1) / dist);
				
				if (waypoints[idx] == null) {
					waypoints[idx] = new RallyPoint(posX, posY);
				} else {
					waypoints[idx].setPosition( new Position(posX, posY) );
				}
			}
		}
	}
	
	/**
	 * TODO
	 * @param idx
	 * @return
	 */
	public RallyPoint getWayPoint(int idx) {
		if (idx >= 0 && idx < waypoints.length) {
			return waypoints[idx];
		}
		
		return null;
	}
}
