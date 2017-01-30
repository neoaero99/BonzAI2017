package Castles.Objects;

import java.util.ArrayList;

import bonzai.Position;

/**
 * 
 * Rally Point is a node that soldiers can traverse
 * to get to castles and villages
 */
public class RallyPoint implements Comparable<RallyPoint> {
	
	public final String ID;
	private Position pos;
	public ArrayList<Soldier> onPoint;
	
	/**
	 * initialize start position
	 * 
	 * @param x		x position
	 * @param y		y position
	 * @param ID	The unique identifier of the rally point
	 */
	public RallyPoint(int x, int y, String ID) {
		this.ID = ID;
		pos = new Position(x, y);
		onPoint = new ArrayList<Soldier>();
	}
	
	/**
	 * Returns the position
	 */
	public Position getPosition(){
		return pos;
	}
	
	/**
	 * Set the position to a new position
	 */
	public void setPosition(Position pos){
		this.pos = pos;
	}
	
	public void setSoldier(Soldier s) {
		if (onPoint.size() == 0) {
			onPoint.add(s);
			
		} else {
			onPoint.set(0, s);
		}	
	}
	
	public void reinforce(int reinforcement) {
		
		if (s != null && onPoint.size() == 1) {
			Soldier occupant = onPoint.get(0);
			
			if (occupant != null && occupant.getLeader() == s.getLeader()) {
				occupant.setValue(occupant.getValue() + s.getValue());
			}
		}
	}
	
	public int getSoldierCount() {
		return onPoint.size() != 1 ? 0 : onPoint.get(0).getValue();
	}
	
	@Override
	public int compareTo(RallyPoint o) {
		return pos.compareTo(o.getPosition());
	}
	
	public RallyPoint copy() {
		RallyPoint copy = new RallyPoint(pos.getX(), pos.getY(), ID);
		copy.setSoldier(getSoldier());
		return new RallyPoint(pos.getX(),pos.getY(),ID);
	}
	
	@Override
	public String toString() {
		return ID;
	}
}
