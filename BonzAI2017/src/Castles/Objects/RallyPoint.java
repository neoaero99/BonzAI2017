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
	 * @param x x position
	 * @param y y position
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
	protected void setPosition(Position pos){
		this.pos = pos;
	}
	
	@Override
	public int compareTo(RallyPoint o) {
		return pos.compareTo(o.getPosition());
	}
	
	public RallyPoint copy(){
		return new RallyPoint(pos.getX(),pos.getY(),ID);
	}
	
	@Override
	public String toString() {
		return ID;
	}
}
