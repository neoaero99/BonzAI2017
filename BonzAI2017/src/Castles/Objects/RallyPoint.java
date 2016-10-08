package Castles.Objects;
import bonzai.*;
/**
 * 
 * Rally Point is a node that soldiers can traverse
 * to get to castles and villages
 */
public class RallyPoint {
Position pos;
/**
 * initialize start position
 * @param x x position
 * @param y y position
 */
	public RallyPoint(int x, int y){
		pos = new Position(x,y);
		
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
}
