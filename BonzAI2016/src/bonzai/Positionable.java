package bonzai;

import java.util.LinkedList;

/**
 * Positionable things have a position. This includes positions, tiles, and
 * bases.
 **/
public interface Positionable {
	/**
	 * @return the position the positionable occupies
	 **/
	public Position getPosition();
	
	
	public LinkedList<Positionable> getNeighbors();
}