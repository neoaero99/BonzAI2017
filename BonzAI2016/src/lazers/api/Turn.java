package lazers.api;

import lazers.api.TurnInterface;
import lazers.LazersScenario;
import lazers.api.Repeater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import bonzai.Action;
import bonzai.Entity;
import bonzai.Identifiable;
import bonzai.Positionable;
import bonzai.Team;
import bonzai.Traversable;
import bonzai.Position;
import lazers.api.Emitter;

/**
 * A snapshot in time at the current turn. In addition to accessing current 
 * state, stale references to entities can be used to get fresh references to
 * those entities at this turn.
 **/
public class Turn implements TurnInterface{
	
	private static final int MAX_TURNS = LazersScenario.NUM_TURNS;
	
	private final LazersMap map;
	private final Collection<Team> teams;			// Never initialized 
	private final int turnNumber;
	
	private int team_id;						// [0..]
	
	
	//First turn is constructed with this constructor
	public Turn(int turnNumber, LazersMap map, int id) {
		this.turnNumber = turnNumber;
		this.map = map;
		this.team_id = id;
		
		teams = new ArrayList<Team>();
		for (Emitter e : map.getEmitters()) {
			teams.add(e.getTeam());
		}
	}
	
	//All turns other than first are made with this constructor
	public Turn(Turn turn) {
		map = new LazersMap(turn.map);
		turnNumber = turn.turnNumber + 1;
		teams = turn.teams;
		team_id = turn.team_id;
	}

	public Collection<Repeater> getRepeaters() {
		return map.getRepeaters();
	}
	
	public Collection<Positionable> getPaths(Positionable a, Positionable b) {
		return Pathfinding.getPath(map, a, b);
	}
	
	public Collection<Target> getTargets() {
		return map.getTargets();
	}
	
	public Collection<Wall> getWalls() {
		return map.getWalls();
	}
	
	public Collection<Emitter> getEmitters() {
		return map.getEmitters();
	}
	
	public LazersMap getMap() {
		return map;
	}
	
	public int turnsRemaining() { 
		return MAX_TURNS - turnNumber;
	}
	
	public int getScore(Team team) {
		return team.getScore();
	}
	
	public Team getMyTeam() {
		Iterator<Team> iter = teams.iterator();
		
		for (int i = 0; i != team_id; ++i)
			iter.next();

		return iter.next();
	}
	
	public Emitter getMyEmitter() {
		Team me = getMyTeam();
		
		for (Emitter e : map.getEmitters())
			if (e.getTeam().equals(me))
				return e;

		return null;
	}
	
	public boolean inLineOfSight(Positionable a, Positionable b) {
		if (!(a instanceof Identifiable || b instanceof Identifiable)) return false;
		
		return map.canHit((Identifiable)a, (Identifiable)b);
	}
	
	public Collection<Team> getTeams() {
		return teams;
	}

	public boolean isValid(Action action) {
		if (action instanceof RotateAction) {
			RotateAction r = (RotateAction) action;
			
			//If rotation not within bounds
			if (!(r.getRotation() <= 360) || r.getRotation() >= 0 ) {
				return false;
			}
			
			//If object to rotate is not an Emitter or Repeater
			Entity t = map.getObject(r.getTarget());
			return t instanceof Emitter || t instanceof Repeater; 

		} else if (action instanceof bonzai.ShoutAction) {
			return true;
		} else {
			//If this executes, the competitor managed to make a new type of
			//action available within a copy of the code they are not able
			//to touch. This is amazing, please give them a medal.
			// Question: What Team are we giving the medal to?
			System.out.println("Give this team a medal.");
			return false;
		}
	}

	/**
	 * @param action - the Action to apply to the current turn
	 * @return a new Turn object that represents the current Turn
	 * after the specified action has been applied to it
	 */
	public Turn apply(List<Action> actions) {
		Turn next = new Turn(this);
		
		//Holds a list of the unit ID's each AI wants to move.
		//This is an ArrayList for checks against null
		ArrayList<Integer> unitsChosen = new ArrayList<Integer>(actions.size());
		//Holds a list of chosen rotations for each unit.
		float[] rotationsChosen = new float[actions.size()];
		
		int index = 0;
		for (Action action : actions) {
			
			/******************************************
			/* THIS IS WHERE BONZAI TURN LOGIC GOES!
			 * create all variables that need to be
			 * created to initialize a new turn object
			 ******************************************/
			if (isValid(action)) {
				if (action instanceof RotateAction) { //Store for later checks/use.
					unitsChosen.add(((RotateAction) action).getTarget());
					rotationsChosen[index] = ((RotateAction) action).getRotation();
				} else {
					//TODO: Apply the ShoutAction to the turn.
				}
			}
			
			index += 1;
		}
		
		//Careful. Loop indexes are i = 0..N-2, j = i+1..N-1
		for (int i = 0; i < unitsChosen.size()-1; i++) {
			Integer element = unitsChosen.get(i);
			if (element != null) {
				//Check to see if the element exists further down the list.
				for (int j = i+1; j < unitsChosen.size(); j++) {
					if (element == unitsChosen.get(j)) {
						
						if (map.getObject(element) instanceof Repeater){
							next.map.replace(element, new Repeater((Entity<Repeater>)map.getObject(element), 4));
						}
						
						//Remove the conflicting items.
						unitsChosen.set(j, null);
						unitsChosen.set(i, null);
					}
					
					//If the i-th item is unique, rotate it.
					if (j == unitsChosen.size() - 1 && unitsChosen.get(i) != null) {
						//Rotate the i-th item. Will look something like:
						//map.get(i).clone(rotationsChosen[i]);
						//Where ".clone" means the copy constructor or something.
						if (map.getObject(element) instanceof Repeater) {
							next.map.replace(element, new Repeater((Entity<Repeater>)map.getObject(element), 4, rotationsChosen[i]));
						} else if (map.getObject(element) instanceof Emitter) {
							next.map.replace(element, new Emitter((Entity<Emitter>)map.getObject(element), rotationsChosen[i]));
						}
					}
				}
			}
		}
		
		//decrement all objects on cooldown
		for (Repeater r : next.map.getRepeaters()) {
			next.map.replace(r.getID(),
					new Repeater(r, Math.max(r.getCooldown() - 1, 0)));
		}
		
		return next;
	}
	
	void setCurTeam(int id) {
		team_id = id;
	}
}
