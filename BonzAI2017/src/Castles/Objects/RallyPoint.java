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
	
	public void occupy(Soldier s) {
		// Do not add null values!
		if (s != null) {
			if (onPoint.size() == 0) {
				onPoint.add(s);
				
			} else {
				onPoint.set(0, s);
			}	
		}
	}
	
	public void reinforce(int reinforcement) {
		
		if (onPoint.size() == 1) { // Is this space occupied?
			Soldier occupant = onPoint.get(0);
			
			if (reinforcement > 0) {
				// Only add positive reinforcement values
				occupant.setValue(occupant.getValue() + reinforcement);
			}
		}
	}
	
	public Soldier getOccupant() {
		return onPoint.size() != 1 ? null : onPoint.get(0);
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
		// Copy the soldier, who is occupying the space
		Soldier occupant = getOccupant();
		
		if (occupant != null) {
			copy.occupy(occupant.copy());
		}
		
		return copy;
	}
	
	@Override
	public String toString() {
		return ID;
	}
}
