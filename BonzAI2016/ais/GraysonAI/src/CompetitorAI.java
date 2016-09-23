
import java.util.Comparator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;

import bonzai.Identifiable;
import bonzai.util.Predicate;

import static bonzai.util.Utility.*;
import lazers.api.*;


class MaxPoints implements Comparator<Identifiable> {
	private int size;
	
	public MaxPoints(LinkedList<Repeater> path) {
		size = path.size();
	}

	@Override
	public int compare(Identifiable arg0, Identifiable arg1) {
		return GraysonAI.points((Target)arg0, size) - GraysonAI.points((Target)arg1, size);
	}
}

@Agent(name = "GraysonAI")
public class GraysonAI extends AI {
	private Map<Repeater, LinkedList<Repeater>> graph = new HashMap<>();
	private LinkedList<Repeater> my_path = new LinkedList<>();
	private Emitter me = null;
	private int threshold = Integer.MAX_VALUE;
	private int choice = 0;
	
	// options isn't guaranteed to be a LinkedList
	
	// keep a record of previous state in case of conflicts ???
	// threshold should be a varying measure (threshold = turn.turnsRemaining() ???)
	// Need to rework based on how I can store Emitters / etc. (relying on getID(), should rely on Entity)
	
	public Action action(Turn turn) {
		threshold = Math.min(threshold, turn.turnsRemaining());
		LazersMap map = turn.getMap();
		
		// Set up the ai state
		if (graph.size() == 0) {
			for (Repeater r : turn.getRepeaters()) {	
				LinkedList<Repeater> adj = new LinkedList<>();
				
				for (Identifiable obj : map.hittableRepeaters(r))
					adj.add((Repeater)obj);
				
				graph.put(r, adj);
			}
			
			for (Emitter e : turn.getEmitters())
				if (e.getTeam().equals(turn.getMyTeam()))
					me = e;
			
			// Rotate a random repeater
			return new RotateAction(any(turn.getRepeaters()).getID(), (float) (Math.random() * 360));
			
		} else if (my_path.size() == 0)
			return rotateMyEmitter(map);
			
		else {
			List<Repeater> options = null;
			choice = my_path.size() - 1;
			
			do {
				Predicate<Repeater> rotatable = new isRotatable(turn.getMyTeam());
				while (!rotatable.test(my_path.get(choice))) my_path.remove(choice--);
				
				options = retain(graph.get(my_path.get(choice--)), rotatable);
				if (options.size() > 0) break;
				
				my_path.remove(choice + 1);
			} while (choice >= 0);
			
			// Start all over again
			if (choice < 0) return rotateMyEmitter(map);
			
			// Build up the path
			LinkedList<Target> targets = map.hittableTargets(my_path.get(choice));
			
			// Decide whether to rotate to hit a repeater or not
			if (shouldRotateRepeater(targets))
				return chooseRepeater(map, options);
			else
				return chooseTarget(map, targets);
		}
	}

	// Need to determine how threshold interaction is going to work
	private boolean shouldRotateRepeater(LinkedList<Target> targets) {
		return my_path.size() < threshold;
	}

	private RotateAction rotateMyEmitter(LazersMap map) {
		my_path = new LinkedList<>();
		my_path.addFirst(getBestOption(map.hittableRepeaters(me)));
		
		return new RotateAction(me.getID(), map.getRotationTo(me, my_path.getFirst()));
	}
	
	private RotateAction chooseRepeater(LazersMap map, List<Repeater> options) {
		my_path.add(getBestOption(options));

		return new RotateAction(my_path.get(choice).getID(), map.getRotationTo(my_path.get(choice), my_path.getLast()));
	}

	// Need a better selection function
	private Repeater getBestOption(List<Repeater> linkedList) {
		return (Repeater)linkedList.get(0);
	}
	
	private RotateAction chooseTarget(LazersMap map, LinkedList<Target> targets) {
		Repeater last = my_path.getLast();
		my_path.removeLast();
		my_path.removeLast();
		my_path.removeLast();
		
		return new RotateAction(last.getID(), map.getRotationTo(last, max(targets, new MaxPoints(my_path))));
	}
	
	static int points(Target t, int size) {
		return 0;
	}
}
