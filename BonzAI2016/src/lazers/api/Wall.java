package lazers.api;

import java.util.LinkedList;

import bonzai.Position;
import bonzai.Positionable;

public class Wall implements Positionable {
	private Position positions[];
	private Position center;
	
	public Wall(Position p1, Position p2) {
		center = new Position((p1.getX() + p2.getX())/2,
							(p1.getY() + p2.getY())/2);
		
		positions = new Position[]{p1, p2};
	}
	/**
	 * 
	 * @return both positions of the wall
	 */
	public Position[] getPositions() {
		return positions;
	}
	@Override
	public Position getPosition() {
		return center;
	}
	@Override
	public LinkedList<Positionable> getNeighbors() {
		//TODO
		return null;
	}

}
