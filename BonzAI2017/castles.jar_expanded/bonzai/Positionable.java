package bonzai;

import java.util.LinkedList;

/**
 * Positionable things have a position. This includes positions, tiles, and
 * bases.
 **/
public abstract class Positionable implements Identifiable {
	private Position position;
	
	/**
	 * Constructor
	 * 
	 * @param position - the position of the object 
	 */
	public Positionable(Position position) {
		this.position = position;
	}
	
	/**
	 * Gets the Position that this Positionable occupies.
	 * 
	 * @return the Position that the Positionable occupies
	 **/
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Returns the distance^2 between this Positionable and a target Positionable
	 * @param target - the target to hit
	 * @return - the distance between the two Positionables.
	 */
	public int distanceSquared(Positionable target) {
		int deltaX = target.getPosition().getX()-this.getPosition().getX();
		int deltaY = target.getPosition().getY()-this.getPosition().getY();
		
		return (deltaX*deltaX)+(deltaY*deltaY);
	}
	
	/**
	 * Checks whether this Positionable and a specified object "o" are the same object.
	 * 
	 * This means that they occupy the same position.
	 * 
	 * @return whether this Positionable and the specified object are the same object
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Positionable)) return false;
		return ((Positionable)o).position.equals(position);
	}
}