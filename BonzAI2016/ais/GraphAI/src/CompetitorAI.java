import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import bonzai.Identifiable;
import bonzai.Rotatable;
import bonzai.Team;
import bonzai.util.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import static bonzai.util.Utility.*;
import lazers.api.*;


class MaxAdjacency implements Comparator<Repeater> {
	private LazersMap map;
	
	public MaxAdjacency(LazersMap map) {
		this.map = map;
	}
	
	public int compare(Repeater arg0, Repeater arg1) {
		return map.hittableFrom(arg0).size() - map.hittableFrom(arg1).size();
	}
}

class MaxScore implements Comparator<Target> {
	private int multiplier;

	public MaxScore(ArrayList<Repeater> path, int handicap) {
		multiplier = path.size() - handicap;
	}
	
	public int compare(Target arg0, Target arg1) {
		return GraphAI.points(arg0, multiplier) - GraphAI.points(arg1, multiplier);
	}
}

class IsNotControlled implements Predicate<Repeater> {
	public boolean test(Repeater e) {
		return e.getControllers().isEmpty() && e.getCooldown() < 2;
	}
}


/*
 * TODO
 * Implement GraphAI.points
 * Work on threshold calculations
 * Ensure all methods are implemented
 */

@Agent(name = "GraphAI")
public class CompetitorAI extends AI {
	private HashMap<Integer, LinkedList<Repeater>> coloring = new HashMap<>();
	private LinkedList<Repeater> queue = null;
	private ArrayList<Repeater> my_path = new ArrayList<>(), tmp_path = null;
	private Team me = null;
	private Emitter my_emitter = null;
	private Repeater last = null;
	private int cur_turn = 0, color = -1, delay = 0;
	
	private static final int RED = 0, BLUE = 1, GREEN = 2, YELLOW = 3;
	
	public GraphAI() {
		coloring.put(RED, new LinkedList<>());
		coloring.put(BLUE, new LinkedList<>());
		coloring.put(GREEN, new LinkedList<>());
		coloring.put(YELLOW, new LinkedList<>());
	}
	
	public Action action(Turn turn) {
		LazersMap map = turn.getMap();
		Action action = null;
		
		// First Turn
		if (cur_turn == 0) {
			me = turn.getMyTeam();
			action = firstTurn(turn, map);
			
		// Second Turn
		} else if (cur_turn == 1) {
			colorGraph(queue, coloring, map);

			// Start building up my path
			action = makeAction(my_emitter, chooseOption(map.hittableRepeaters(my_emitter), map, color), map);

		// End Game
		} else if (delay == 0 && turn.turnsRemaining() < my_path.size() + 4 && my_path.get(turn.turnsRemaining() - 2).canMove(me)) {
			// Shrink the path to possible moves
			while (!my_path.get(my_path.size() - 1).canMove(me))
				my_path.remove(my_path.size() - 1);
			
			// Move the last repeater
			Repeater source = my_path.remove(my_path.size() - 1);
			action = makeAction(source, chooseTarget(map.hittableTargets(source)), map);
		
		// Connection Setup
		} else if (delay > 1) {
			// Check if the move succeeded (burn everything if it didn't)
			if (tmp_path.size() != 0 && last.getRotation() != map.getRecent(last).getRotation()) {
				tmp_path = new ArrayList<>();
				
				last = my_path.get(my_path.size() - 1);
				
				color = (color + (4 - tmp_path.size())) % 4;
			}
			
			// Build up path until I can move the last repeater 
			Repeater choice = chooseOption(map.hittableRepeaters(last), map, color);
			action = makeAction(last, choice, map);
			
			tmp_path.add(choice);
			
			--delay;
			
		// Connect the paths (Guaranteed to succeed)
		} else if (delay == 1) {
			action = makeAction(my_path.get(my_path.size() - 1), tmp_path.get(0), map);
			my_path.addAll(tmp_path);
			
			--delay;
			
		// Normal Play (Guaranteed to succeed)
		} else
			action = makeAction(last, makeDecision(turn, color, true), map);
		
		color = (color + 1) % 4;
		++cur_turn;
		return action;
	}
	
	private Identifiable makeDecision(Turn turn, int color, boolean consider_targets) {
		final int threshold = 10;
		
		LazersMap map = turn.getMap();
		
		Target t = chooseTarget(map.hittableTargets(last));
		LinkedList<Repeater> options = map.hittableRepeaters(last);

		// Decide if I'm going to choose a target based on points, proximity to the end game, and open repeaters
		if (options.isEmpty() || (turn.turnsRemaining() > my_path.size() && points(t, my_path.size()) > threshold)) {
			delay = 4;
			tmp_path = new ArrayList<>();
			return t;
		}
		
		Repeater option = chooseOption(options, map, color);
		my_path.add(option);
		return option;
	}
	
	private Target chooseTarget(LinkedList<Target> targets) {
		return max(targets, new MaxScore(my_path, 0));
	}
	
	private Repeater chooseOption(LinkedList<Repeater> repeaters, LazersMap map, int color) {
		// Return the most connected repeater that is of the desired color and not already controlled by a player
		return max(new LinkedList<>(retain(intersect(repeaters, coloring.get(color)), new IsNotControlled())), new MaxAdjacency(map));
	}

	private RotateAction firstTurn(Turn turn, LazersMap map) {
		PriorityQueue<Repeater> tmp_queue = new PriorityQueue<>(map.getRepeaters().size(), new MaxAdjacency(map));
		
		// Initialize Coloring queue
		for (Repeater r : map.getRepeaters()) tmp_queue.add(r);
		queue = new LinkedList<>(tmp_queue);
		
		// Find my emitter
		Emitter opponent = null;

		for (Emitter e : turn.getEmitters()) {
			if (e.getTeam().equals(turn.getMyTeam()))
				my_emitter = e;
			else
				opponent = e;
		}
		
		// Attempt to rotate one of my opponents likely options
		Repeater select = any(map.hittableRepeaters(opponent));
		return makeAction(select, map.getRotationTo(select, opponent));
	}
	
	private RotateAction makeAction(Rotatable source, double rotation) {
		return new RotateAction(((Identifiable)source).getID(), (float)rotation);
	}
	
	private RotateAction makeAction(Rotatable source, Identifiable target, LazersMap map) {
		if (target instanceof Repeater)
			last = (Repeater)target;
		
		return new RotateAction(((Identifiable)source).getID(), map.getRotationTo(source, target));
	}
	
	static void colorGraph(LinkedList<Repeater> queue, HashMap<Integer, LinkedList<Repeater>> coloring, LazersMap map) {
		int i = 0;
		
		while (!queue.isEmpty() && i != 4) {
			int j = 0;
			HashSet<Repeater> bad_choices = new HashSet<>();				// List of repeaters that can't be chosen because of adjacency
			
			while (j < queue.size()) {
				if (!bad_choices.contains(queue.get(j))) {				// If the repeater can be assigned the given color
					Repeater node = queue.remove(j--);
					coloring.get(i).add(node);
					
					bad_choices.addAll(map.hittableRepeaters(node));
				}
				
				++j;
			}
			
			++i;
		}
	}

	static int points(Target t, int multiplier) {
		return multiplier * (t.isHit() ? 0 : 1);
	}
}
