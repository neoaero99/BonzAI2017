/**
 * A breadth-first search pathfinder.
 * <p>
 * Used internally for deteriming if a move action is valid. Can be called by
 * AIs, but it can be expensive if called repeatedly.
 **/
public class Pathfinding {
	
	
	//TODO 2017: This is an example of pathfinding code used in a bonzai. 
	// Implementation and extent of pathfinding have to be determined for any indivdual game. 
	

	/**
	 * Returns a list of positions representing the path from the start
	 * position to the goal position. If no such path exists, then this method
	 * will return a list containing only the start.
	 *
	 * @param turn the turn in which to path
	 * @param start the starting position
	 * @param goal the end position
	 * @return a list of positions representing the path
	 */
	public static List<Position> getPath(Turn turn, Position start, Position goal) {
		Queue<Position> frontier = new ArrayDeque<Position>();
		frontier.add(goal);
		
		Map<Position, Position> visited = new HashMap<Position, Position>();
		visited.put(goal, null);

		while(!frontier.isEmpty() && !goal.equals(start)) {
			Position current = frontier.remove();
			
			for(Position neighbor : current.neighbors()) {
				if(neighbor.equals(start)) {
					List<Position> path = new ArrayList<>();
					path.add(start);
				
					Position part = current;
					while(part != null) {
						path.add(part);
						part = visited.get(part);
					}
				
					return path;
				}
				
				if(!visited.containsKey(neighbor) && turn.tileAt(neighbor) != null && turn.unitAt(neighbor) == null) {
					frontier.add(neighbor);
					visited.put(neighbor, current);
				}
			}
		}

		return new ArrayList<Position>(Arrays.asList(start));
	}
}