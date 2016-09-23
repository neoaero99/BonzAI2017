package lazers.api;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Traversable;

/**
 * A breadth-first search pathfinder.
 * <p>
 * Used internally for deteriming if a move action is valid. Can be called by
 * AIs, but it can be expensive if called repeatedly.
 **/
public class Pathfinding {
	
	
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
	protected static Collection<Positionable> getPath(LazersMap m, Positionable start, Positionable goal) {
		
		Queue<Positionable> frontier = new ArrayDeque<>();
		frontier.add(goal);
		
		Map<Positionable, Positionable> visited = new HashMap<>();
		visited.put(goal, null);

		while(!frontier.isEmpty() && !goal.equals(start)) {
			Positionable current = frontier.remove();
			
			for(Positionable neighbor : m.getNeighbors((Identifiable)current)) {
				if(neighbor.equals(start)) {
					List<Positionable> path = new ArrayList<>();
					path.add(start);
				
					Positionable part = current;
					while(part != null) {
						path.add(part);
						part = visited.get(part);
					}
				
					return path;
				}
				
				if(!visited.containsKey(neighbor) ) {
					frontier.add(neighbor);
					visited.put(neighbor, current);
				}
			}
		}

		return new ArrayList<>(Arrays.asList(start));
	}
}
