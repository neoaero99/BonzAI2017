package lazers.api;

import java.util.LinkedList;

import bonzai.Entity;
import bonzai.Identifiable;
import bonzai.Positionable;
import bonzai.Traversable;
import bonzai.Position;

public class Target extends Entity<Target> implements Positionable, Identifiable, Traversable {
	private int uid;
	private boolean isHit;
	private Position position;

	/**
	 * Constructor that takes in a position and creates a target at that position
	 * @param position This is the position of the target
	 */
	public Target(Position p, int id) {
		position = p;
		isHit = false;
		uid = id;
	}

	/**
	 * Copy constructor so that many targets can be made easily
	 */
	public Target(Target target) {
		this(target.getPosition(), target.uid);
	}
	
	/**
	 * Copy constructor so that targets can get "hit"
	 */
	public Target(Target target, boolean hitChange) {
		this(target.getPosition(), target.getID());
		isHit = hitChange || target.isHit;
	}
	
	
	//TODO Ask mitch pointed questions about points calculation
	
	public int getID() {
		return uid;
	}

	public boolean isHit() {
		return isHit;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public LinkedList<Positionable> getNeighbors() {
		// TODO 
		return null;
	}

}
