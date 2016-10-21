package Castles.Objects;
import bonzai.*;
/**
 * 
 * Rally Point is a node that soldiers can traverse
 * to get to castles and villages
 */
public class RallyPoint implements Comparable<RallyPoint> {
Position pos;
final String ID;
/**
 * initialize start position
 * @param x x position
 * @param y y position
 */
	public RallyPoint(int x, int y, String ID){
		pos = new Position(x,y);
		this.ID=ID;
		
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
		this.pos=pos;
	}
	@Override
	public int compareTo(RallyPoint o) {
		return pos.compareTo(o.getPosition());
	}
	public String getName(){
		return ID;
	}
	public RallyPoint copy(){
		return new RallyPoint(pos.getX(),pos.getY(),ID);
	}
}
