package Castles.api;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Castles.CastlesRenderer;
import Castles.Objects.*;
import bonzai.Action;
import bonzai.Position;
import bonzai.Team;
import bonzai.ShoutAction;

/**
 * A snapshot in time at the current turn. In addition to accessing current 
 * state, stale references to entities can be used to get fresh references to
 * those entities at this turn.
 **/
public class Turn {
	
	final CastlesMap map;
	// Data used for AI queries
	private final HashMap<Color, HashMap<String, SoldierData>> teamSoldiers;
	private final HashMap<Color, HashMap<String, PositionData>> teamPositions;
	private final HashMap<String, PositionData> unclaimedPositions;
	
	final ArrayList<Team> teams;
	final ArrayList<Boolean> success;
	final int turnNumber;

	private final ArrayList<ShoutAction> shoutActions;
	private final ArrayList<MoveAction> moveActions;
	private final ArrayList<UpdateAction> updateActions;
	
	// How long the Repeaters can go before being rotated again
	private final int cooldownAmount = 3;

	private String errorMessage = "";

	// The current team that is acting.
	// For each client, this will ALWAYS be their teamId
	// This decides the context that isValid() will be run for.
	// Example, If I'm on team 1, I can move team 1's things.  If I'm on team 2, I cant

	//making this a class so all the pathfinding stuff is in one place
	Pathfinding pathfinding;

	// Example, If I'm on team 1, I can move team 1's things.  If I'm on team 2, I cannot

	int currentTeam;
	int MAX_TURNS = CastlesScenario.NUM_TURNS;

	/**
	 * Turn constructor.
	 * 
	 * @param teamNumber - the team number that this current Turn object
	 *					   was made for
	 * @param turnNumber - the current turn number
	 * @param map - the map object to clone to start the turn off
	 */
	public Turn(int teamNumber, int turnNumber, CastlesMap map) {
		this.currentTeam = teamNumber;
		this.turnNumber = turnNumber;

		//Clone the old map (so we can retain history)
		this.map = new CastlesMap(map);
		teamSoldiers = new HashMap<Color, HashMap<String, SoldierData>>();
		teamPositions = new HashMap<Color, HashMap<String, PositionData>>();
		unclaimedPositions = new HashMap<String, PositionData>();
		
		shoutActions = new ArrayList<ShoutAction>();
		moveActions = new ArrayList<MoveAction>();
		updateActions = new ArrayList<UpdateAction>();

		teams = (ArrayList<Team>) map.getTeams();
		success = new ArrayList<Boolean>();
		
		for (Team t : teams) {
			/* Associate each group of soldiers with their team color and with
			 * their position */
			HashMap<String, SoldierData> soldierGroups = new HashMap<String, SoldierData>();
			ArrayList<Soldier> soldiers = map.getSoldiers(t);
			
			for (Soldier s : soldiers) {
				SoldierData data = new SoldierData(s);
				soldierGroups.put(data.posID, data);
			}
			
			teamPositions.put(t.getColor(), new HashMap<String, PositionData>());
		}
		
		ArrayList<RallyPoint> elements = map.getAllElements();
		
		for (RallyPoint r : elements) {
			/* Associated claimed positions with the color of the team, which
			 * owns the position and then with the positions ID. Unclaimed
			 * positions are simply associated with their ID. */
			PositionData p = new PositionData(r);
			
			if (r instanceof Building) {
				Building b = (Building)r;
				
				if (b.getColor() != null) {
					teamPositions.get(b.getColor()).put(p.ID, p);
					
				} else {
					unclaimedPositions.put(p.ID, p);
				}
				
			} else {
				unclaimedPositions.put(p.ID, p);
			}
		}
	}

	/**
	 * Alternate turn constructor, used to specify a number of turns
	 * that the match should last for
	 * 
	 * @param teamNumber - the team number that this current Turn object
	 *					   was made for
	 * @param turnNumber - the current turn number
	 * @param map - the map object to clone to start the turn off
	 * @param MAX_TURNS - the total number of turns in the match
	 */
	public Turn(int teamNumber, int turnNumber, CastlesMap map, int MAX_TURNS) {
		this(teamNumber, turnNumber, map);
		this.MAX_TURNS = MAX_TURNS;
	}

	/**
	 * Copy constructor for Turn
	 * 
	 * @param turn - the Turn object to copy
	 * @param teamNumber
	 * @param map
	 */
	public Turn(Turn turn, int teamNumber, CastlesMap map, Collection<Team> failedActions) {
		this(teamNumber, turn.turnNumber + 1, map);
		this.MAX_TURNS = turn.MAX_TURNS;

		for (Team t : failedActions) {
			success.set(t.getID(), false);
		}
	}
	
	/**
	 * 
	 * 
	 * @param g
	 */
	public void renderMap(Graphics2D g) {
		if (g != null) {
			CastlesRenderer.renderBackground(g, map);
			CastlesRenderer.renderPaths(g, map);
			CastlesRenderer.renderBuildings(g, map);
			CastlesRenderer.renderSoldiers(g, map);
		}
	}
	
	/**
	 * Returns whether this is the first turn or not
	 * 
	 * @return
	 */
	public boolean isFirstTurn() {
		return turnNumber == 0;
	}

	/**
	 * Returns the score for the specified Team
	 * 
	 * @param t - the Team object to get the score for
	 * @return - the Team's score
	 */
	public int getScore(Team t) { 
		return t.getScore();
	}

	/**
	 * Returns the number of remaining turns in the match
	 * 
	 * @return - the number of remaining turns
	 */
	public int getTurnsRemaining() {
		return MAX_TURNS - turnNumber;
	}
	
	/**
	 * Get a list of the ShoutActions performed on this turn.
	 * This has LITERALLY ZERO USE TO YOUR CODE, but if you want to
	 * make an AI that evaluates the humor level of other teams'
	 * shouts, be our guest. :D
	 * 
	 * @return a Collection of the ShoutActions performed on this turn
	 */
	public Collection<ShoutAction> getShoutActions() {
		return shoutActions;
	}
	
	/**
	 * Returns a list of move actions performed on this turn.
	 * 
	 * @return	a Collection of move actions performed on this turn
	 */
	public Collection<MoveAction> getMoveActions() {
		return moveActions;
	}
	
	/**
	 * @return	The list of update actions performed this turn
	 */
	public Collection<UpdateAction> getUpdateActions() {
		return updateActions;
	}
	
	/**
	 * Returns your AI's Team object
	 * 
	 * @return - your AI's Team object
	 */
	public Team getMyTeam() {
		for (Team t : getAllTeams()) {
			if (t.getID() == currentTeam) {
				return t;
			}
		}
		return null;
	}
	
	/**
	 * @return	The list of all teams
	 */
	public List<Team> getAllTeams() {
		return map.getTeams();
	}

	/**
	 * Test if a given Action is a valid one if it were applied on this Turn object.
	 * This method returns true if the Action is a valid Action, false otherwise.
	 * There are no guarantees that the Action will be performed, even if it is
	 * valid, as another team may be performing an Action that interferes
	 * with the given Action. (For example, if two teams try to rotate the same
	 * Repeater on the same turn.) 
	 * 
	 * If this method returns false, calling getIsValidError() will return the
	 * reason for failure.
	 * 
	 * @param action - the Action object to check for validity
	 * 
	 * @return - true if the Action is valid for the current gamestate,
	 * 			 false otherwise
	 */
	public boolean isValid(Action action) {
		//TODO 2017: This is important for us and competitors.
		
		if (action instanceof ShoutAction) {
			return true;
			
		} else if(action instanceof MoveAction) {
			MoveAction move = (MoveAction)action;
			ArrayList<String> pathIDs = move.getPathIDs();
			/* A path must contain the starting position, occupied by soldiers,
			 * and an ending position */
			if (pathIDs != null && pathIDs.size() > 1) {
				RallyPoint prev = map.getElement(pathIDs.get(0));
				
				if (prev != null && prev.getOccupant() != null &&
						prev.getOccupant().getValue() > move.getSplitAmount()) {
					/* The positions in the path must exist and form a chain of
					 * adjacent positions */
					for (int idx = 1; idx < pathIDs.size(); ++idx) {
						RallyPoint curr = map.getElement(pathIDs.get(idx));
						
						if (curr == null || map.getGraph().areAdjacent(prev.ID, curr.ID)) {	
							return false;
						}
					}
					
					return true;
				}
			}
			
		} else if (action instanceof UpdateAction) {
			UpdateAction update = (UpdateAction)action;
			/* The position must exist and have a soldier group and you can
			 * only command soldiers to move or halt */
			RallyPoint r = map.getElement(update.getSrcID());
			return r != null && r.getOccupant() != null &&
					(update.getState() == SoldierState.MOVING ||
					update.getState() == SoldierState.STANDBY);
		}

		return false;
	}

	/**
	 * If isValid() returns false, this method returns
	 * a String containing a more detailed error message
	 * 
	 * @return - a String explaining why a move is invalid
	 */
	public String getIsValidError() {
		return errorMessage;
	}

	/**
	 * Applies an action to the current turn. This should 
	 * not be called by an AI as it is almost useless.
	 * 
	 * @param actions - the list of actions to apply
	 * @return - the new Turn object created after applying 
	 * the actions
	 */
	public Turn apply(List<Action> actions) {
		
		
		//TODO 2017: This is where we applied each action from the playing ai's. This is an example of our process. 
		// Most of the game logic goes here. 
		
		
		int oldID = this.currentTeam;

		//Clone the map. All actions are applied to this new clone.
		CastlesMap map = new CastlesMap(this.map);

		//This is used to store the RotateActions.
		//Maps ID's of targeted Entities (Repeaters or Emitters)
		//to the team ID that wants to perform them
		HashMap<Integer, Team> rotationsToPerform = new HashMap<>();

		// Store the teams whose action failed
		LinkedList<Team> failedTeams = new LinkedList<>();
		

		//Parse the list of all actions for validity and move collisions
		//Reduce the list to only the moves that actually need to be performed
		currentTeam = 0;
		for (Action action : actions) {
			//TODO Actions are Handled here
			if (isValid(action)) {
				if (action instanceof ShoutAction) {
					shoutActions.add((ShoutAction)action);
					
				} else if (action instanceof MoveAction) {
					moveActions.add((MoveAction)action);
					
				} else if (action instanceof UpdateAction) {
					updateActions.add((UpdateAction)action);
				}
				
			} else {
				// TODO Write code for an invalid action
			}

			currentTeam++;
		}

		//Apply all valid RotateActions to the game state.
		//for (int id : rotationsToPerform.keySet()) {

			/*Rotatable r = (Rotatable)map.getEntity(id);

			if (r instanceof Emitter) {

				LinkedList<Emitter> emitters = (LinkedList<Emitter>)map.getEmitters();

				//Create a new Emitter object to replace the old one. The new Emitter is set to have the new rotation.
				// Okay to not use LazersMap.replace as emitters have the lowest id's
				emitters.set(r.getID(), new Emitter((Emitter)r, rotationsDesired[rotationsToPerform.get(id).getID()], map));

			} else {
				//Create a new Repeater object to replace the old one (as there is no setRotation method)
				Team teamRotating = rotationsToPerform.get(id);

				if (teamRotating == null) {	//Two teams tried to move the same Repeater
					map.replace(id, new Repeater((Repeater)r, r.getRotation(), null, cooldownAmount + 1, map));
				} else {
					//Some team is performing a RotateAction on Repeater r.


					map.replace(id, new Repeater((Repeater)r, rotationsDesired[teamRotating.getID()], teamRotating, cooldownAmount + 1, map));
				}
			}*/
		//}
		
		//map.calculateParentsTargetsAndOwners();

		//Generate the new Turn object. We apply any earned points onto this new Turn.
		Turn newTurn = new Turn(this, oldID, map, failedTeams);
		
		
		for (Team team : this.getAllTeams()) {
			currentTeam = team.getID();
			
			ArrayList<Soldier>[] soldiers =map.getSoldiers();
			for(int i=0;i<6;i++){
					for(Soldier s:soldiers[i]){
						s.gotoNext(map);
				 		}
				 	}
			ArrayList<RallyPoint> rally= map.getAllElements();
				for(RallyPoint r: rally){
						map.mergeSoldiers(r.onPoint,r);
						if (r instanceof Building && ((Building)r).onPoint.size() > 0) {
				 				Soldier sol= r.onPoint.get(0);
				 				if(!sol.getLeader().equals(((Building) r).getTeam())){
				 					((Building)r).setTeam(sol.getLeader());
				 				}
						}
				}
			//Positionable current = newTurn.getUtil().updateEntity(util.getMyEmitter());
			//Team myTeam = ((Emitter) current).getTeam();
			/*
			//Traverse the path of the Emitter/Repeater chain until we hit a Wall or Target.
			while (current != null && !visited.contains(current) && current instanceof Rotatable) {				
				if (current instanceof Repeater) {
					Repeater r = (Repeater) current;

					//If the current team has control, or if no one has control
					if (myTeam.equals(r.getOwner())) {
						visited.add(current);

						multiplier *= 2;
					} else { 
						//The person does not have control of this Repeater. They should not get points, as their lazer isn't emitting.
						validPoints = false;
						break;
					}

					//Prevent ais from getting points by moving through an opponent's emitter
				} else if (current instanceof Emitter && !current.getPosition().equals(util.getMyEmitter().getPosition())) {
					validPoints = false;
					break;
				}

				validPoints = validPoints || rotationsToPerform.containsKey(((Identifiable)current).getID());
				current = ((Rotatable)current).getTarget();
			}
			*/
			//The lazer's path traveled to a Target this turn. See if it is a valid hit.
			//"Valid" is defined as "this team performed a valid rotation action this turn on some Rotatable
			//that is within the path from Emitter to Target" (including rotation actions on the Emitter itself).
			//We also only want to calculate points if the team has not hit the target before.
			/*if ((current instanceof Target) && validPoints) {
				//Refresh the target in case another team hit it this turn
				//Due to dangling references from getTarget, the hit array won't be accurate
				//I'm not sure how to better explain this, just trust me
				Target hitTarget = (Target)newTurn.getUtil().updateEntity((Target)current);

				//Don't calculate points if a team has hit the target before
				if (hitTarget.isDiscoveredByTeam(team)) {
					break;
				}

				//Get the number of points the target was worth last turn
				int points = ((Target) map.updateEntity(hitTarget)).getPointValue();

				//Increment the players points total
				newTurn.addPoints(team.getID(), points * multiplier);
				
				//Update the next turn's target with the correct hit records
				newTurn.map.replace(hitTarget, new Target(hitTarget, team.getID()));
			}*/
		}
		//TODO uncomment everything
		this.currentTeam = oldID;

		return newTurn;
	}
	
	/**
	 * Who really knows ...
	 * 
	 * @param i
	 * @return
	 */
	public Position getEntity(int i) {
		return map.getEntity(i);
	}
	
	/**
	 * @return	The width of the map
	 */
	public int getMapWidth() {
		return map.getWidth();
	}
	
	/**
	 * @return	The height of the map
	 */
	public int getMapHeight() {
		return map.getHeight();
	}
	
	
}
