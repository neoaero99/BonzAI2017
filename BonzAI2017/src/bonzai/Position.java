package bonzai;

/**
 * A coordinate pair.
 **/
public class Position implements Comparable<Position> {
	private final int x;
	private final int y;

	/**
	 * Creates a coordinate using the specified x and y values.
	 *
	 * @param x the x value
	 * @param y the y value
	 **/
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x coordinate value of this position
	 **/
	public int getX() {
		return x;
	}

	/**
	 * @return the y coordinate value of this position
	 **/
	public int getY() {
		return y;
	}
	
	/**
	 * Calculates the distance between this position and another specified position
	 * @param position the position to calculate the distance between
	 * @return the distance between the two positions
	 */
	public double getDistanceBetween(Position position) {
		return Math.sqrt(Math.pow((this.x - position.x), 2) + Math.pow((this.y - position.y), 2));
	}
	
	/**
	 * @return itself (the position of a position is itself)
	 **/
	public Position getPosition() {
		return this;
	}
	
	//TODO: Wut
	@Override
	public int hashCode() {
		int hash = 17;
        hash = ((hash + x) << 5) - (hash + x);
        hash = ((hash + y) << 5) - (hash + y);
        return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Position)) { return false; }
		Position p = (Position)o;
		return x == p.x && y == p.y;
	}
	
	@Override
	public String toString() {
		return String.format("[%d, %d]", x, y);
	}

	@Override
	public int compareTo(Position o) {
		if(this.x<o.getX()){
			return -1;
		}
		if(this.x>o.getX()){
			return 1;
		}
		if(this.y<o.getY()){
			return -1;
		}
		if(this.y>o.getY()){
			return 1;
		}
		return 0;
	}
}
