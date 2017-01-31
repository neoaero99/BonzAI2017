package Castles.Objects;

import java.util.ArrayList;

import bonzai.Position;
import bonzai.Team;

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
	 * Copy constructor for a rally point.
	 * 
	 * @param x			x position
	 * @param y			y position
	 * @param ID		The unique identifier of the rally point
	 * @param occupants	The list of soldiers at this position
	 */
	public RallyPoint(int x, int y, String ID, ArrayList<Soldier> occupants) {
		this.ID = ID;
		pos = new Position(x, y);
		onPoint = new ArrayList<Soldier>();
		
		for (Soldier s : occupants) {
			// Copy each soldier
			onPoint.add(s.copy());
		}
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
	
	public Soldier getOccupant(int idx) {
		if (idx >= 0 && idx < onPoint.size()) {
			return onPoint.get(idx);
		}
		
		return null;
	}
	
	public void addOccupant(Soldier s) {
		// Do not add null values!
		if (s != null) {
			onPoint.add(s);
		}
	}
	
	public void setOccupant(int idx, Soldier s) {
		if (s != null && idx >= 0 && idx < onPoint.size()) {
			onPoint.add(idx, s);
		}
	}
	
	public Soldier reinforce(Team leader, int reinforcement) {
		
		if (reinforcement <= 0) {
			// Only add positive reinforcement values
			return null;
		}
		
		/* Add the reinforcements to an already existing soldier group or add a
		 * new soldier group. */
		if (onPoint.size() > 0) {
			Soldier occupant = onPoint.get(0);
			occupant.setValue(occupant.getValue() + reinforcement);
			return null;
			
		} else {
			Soldier newSoldier = new Soldier(leader, reinforcement, ID);
			onPoint.add(newSoldier);
			return newSoldier;
		}
	}
	
	public Soldier removeOccupant(int idx) {
		if (idx >= 0 && idx < onPoint.size()) {
			return onPoint.remove(idx);
		}
		
		return null;
	}
	
	public ArrayList<Soldier> getOccupants() {
		return onPoint;
	}
	
	@Override
	public int compareTo(RallyPoint o) {
		return pos.compareTo(o.getPosition());
	}
	
	public RallyPoint copy() {
		RallyPoint copy = new RallyPoint(pos.getX(), pos.getY(), ID, onPoint);
		return copy;
	}
	
	@Override
	public String toString() {
		return ID;
	}
}
