import java.util.*;

import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import bonzai.Position;
import lazers.api.Turn;

import lazers.api.Target;
import bonzai.Positionable;
import lazers.api.Repeater;
import lazers.api.RotateAction;

	 
@Agent(name = "BasicAI")
public class CompetitorAI extends AI {

	/**
	 * Given a turn, representing the state of the game at a fixed time, return
	 * an action for the current unit.
	 *
	 * @param turn the current turn
	 * @return the selected action
	 **/
	public Action action(Turn turn) {
		int myID = turn.getMyTeam().getID();
		ArrayList<Target> directHits = getDirectHits(turn);

		if (directHits.size() > 0) {
			return new RotateAction(myID, turn.getMyEmitter().getPosition(), directHits.get(0).getPosition());
		}
		return null;
	}
	
	public ArrayList<Target> getDirectHits(Turn turn) {
		ArrayList<Target> list = new ArrayList<>();
		for (Target target : turn.getTargets()) {
			if (turn.getMap().canHit(turn.getMyEmitter(), target)) {
				list.add(target);
			}
		}
		return list;
	}
	
	public HashMap<Integer, LinkedList<Positionable>> getAllPaths (Turn turn) {
		HashMap<Integer, LinkedList<Positionable>> hashMap = new HashMap<>();
		for (Target target : turn.getTargets()) {
			hashMap.put(target.getID(), new LinkedList<>());
			for (Positionable path : turn.getPaths(turn.getMyEmitter(), target)) {
				hashMap.get(target.getID()).add(path);
			}
		}
		return hashMap;
	}

	public ArrayList<Target> oneStepHits(Turn turn) {
		ArrayList<Target> targets = new ArrayList<>();
		
		for (Repeater repeater : turn.getRepeaters()) {
			for (Target target : turn.getTargets()) {
				if (turn.getMap().canHit(repeater, target) && repeater.getCooldown() == 0) {
					targets.add(target);
				}
			}
		}
		
		return targets;
	}
}

