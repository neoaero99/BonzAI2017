package Castles.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import Castles.Objects.*;
import Castles.util.graph.Vertex;
import Castles.util.graph.WeightedEdge;
import Castles.util.linkedlist.DualLinkList;
import bonzai.Action;
import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Team;
import bonzai.ShoutAction;

/**
 * A snapshot in time at the current turn. In addition to accessing current 
 * state, stale references to entities can be used to get fresh references to
 * those entities at this turn.
 **/
public class Turn {
	final CastlesMap map;
	final ArrayList<Team> teams;
	final ArrayList<Boolean> success;
	final int turnNumber;

	final ArrayList<ShoutAction> shoutActions;
	
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
		this.shoutActions = new ArrayList<>();

		teams = new ArrayList<>();
		success = new ArrayList<>();
		/*for (Emitter e : this.map.getEmitters()) {
			teams.add(e.getTeam());
			success.add(true);
		}*/

		//util = new TurnUtil(this);

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
	 * Returns the map object for the current turn
	 * 
	 * @return - the map object
	 */
	public CastlesMap getMap() {
		return map;
	}

	/**
	 * Returns a Collection of Positionables that represent a 
	 * valid path between points A and B. The path is not
	 * guaranteed to be a shortest, longest, best, or worst path.
	 * There are also no guarantees that each Repeater on this 
	 * path is controllable at the current time by your AI.
	 * 
	 * @param a - the start point for the path-generator
	 * @param b - the end point for the path-generator 
	 * @return - a Collection of Positionable objects (usually Repeaters)
	 * that can be chained together to form a Lazer path. 
	 */
	/*public Collection<Positionable> getPath(Rotatable a, Traversable b) {
		return Pathfinding.getPath(a, b,this.getMyTeam(), map );
	}*/
	
	/**
	 * Returns a Collection of all team objects on the map
	 * 
	 * @return - a Collection of Teams
	 */
	/*public Collection<Team> getAllTeams() {
		return teams;
	}*/
	
	
	/*
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

	//TODO
	/**
	 * find a the teams list
	 * @return
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
		CastlesMap map = new CastlesMap(this.map, false);

		//This is used to store the RotateActions.
		//Maps ID's of targeted Entities (Repeaters or Emitters)
		//to the team ID that wants to perform them
		HashMap<Integer, Team> rotationsToPerform = new HashMap<>();

		//Store the desired rotation at [teamNum] of this array.
		//Thus, if team 3 wanted to rotate something, their chosen rotation
		//will be at rotationsDesired[3]. If they didn't choose to rotate
		//something, then the array will contain null at that index.
		Float [] rotationsDesired = new Float [teams.size()];

		// Store the teams whose action failed
		LinkedList<Team> failedTeams = new LinkedList<>();
		

		//Parse the list of all actions for validity and move collisions
		//Reduce the list to only the moves that actually need to be performed
		currentTeam = 0;
		for (Action action : actions) {
			//TODO Actions are Handled here
			if (isValid(action)) {
				/*if (action instanceof RotateAction) {
					RotateAction r = (RotateAction)action;

					rotationsDesired[currentTeam] = r.getRotation();

					//Revert the rotation if two AIs attempt to move the same repeater
					if (rotationsToPerform.containsKey(r.getRotatedObjectId())) {
						failedTeams.add(rotationsToPerform.get(r.getRotatedObjectId()));
						failedTeams.add(teams.get(currentTeam));
						
						rotationsToPerform.put(r.getRotatedObjectId(), null);
						
					} else {
						//Otherwise put the move in the list (in case of later conflicts)
						//Indicate that the currentTeam performed the move.
						rotationsToPerform.put(r.getRotatedObjectId(), teams.get(currentTeam));
					}
					
					shoutActions.add(null);
				} else {
					shoutActions.add((ShoutAction)action);
				}*/
			} else {
				//TODO Write code for an invalid action
				shoutActions.add(null);
			}

			currentTeam++;
		}

		//Apply all valid RotateActions to the game state.
		for (int id : rotationsToPerform.keySet()) {

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
		}
		
		//map.calculateParentsTargetsAndOwners();

		//Generate the new Turn object. We apply any earned points onto this new Turn.
		Turn newTurn = new Turn(this, oldID, map, failedTeams);
		
		
		for (Team team : this.getAllTeams()) {
			currentTeam = team.getID();
			int multiplier = 1;

			//Only score if something was moved
			boolean validPoints = false;

			//To prevent infinite loops if a circular path is found
			LinkedList<Positionable> visited = new LinkedList<>();

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

	private Object getUtil() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	void addPoints(int teamID, int points) {
		Team old = teams.get(teamID);
		teams.set(teamID, new Team(old, old.getScore() + points));
		((Emitter) map.getEntity(teamID)).setTeam(teams.get(teamID));			// Update the team's emitter to have the correct reference
	}
	*/
	
	/**
	 * Returns all rally points, buildings (i.e. castles, villages) in
	 * the map.
	 * 
	 * @return	A lsit of all rally points, buildings in the map
	 */
	public DualLinkList<RallyPoint> getAllNodes() {
		DualLinkList<RallyPoint> nodes = new DualLinkList<RallyPoint>();
		DualLinkList<Vertex<RallyPoint, Integer>> vertexList =
				map.getGraph().vertexList();
		
		for (Vertex<RallyPoint, Integer> v : vertexList) {
			// Pull all the elements from all the vertices in the graph
			nodes.addToBack(v.getElement());
		}
		
		return nodes;
	}
	
	public DualLinkList<Position> getRallyPointsPositions(){
		DualLinkList<Position> pos= new DualLinkList<Position>();
		for(Vertex<RallyPoint, Integer> r:map.getGraph().vertexList()){
			if(!(r.getElement()instanceof Building)){
				pos.addToFront(r.getElement().getPosition());
			}
		}
		return pos;
	}
	public DualLinkList<Building> getBuilding(){
		DualLinkList<Building> pos= new DualLinkList<Building>();
		for(Vertex<RallyPoint, Integer> r:map.getGraph().vertexList()){
			if(r.getElement()instanceof Building&&!(r.getElement()instanceof Castle)){
				pos.addToFront((Building)r.getElement());
			}
		}
		return pos;
	}
	public DualLinkList<Building> getCastle(){
		DualLinkList<Building> pos= new DualLinkList<Building>();
		for(Vertex<RallyPoint, Integer> r:map.getGraph().vertexList()){
			if(r.getElement()instanceof Castle){
				pos.addToFront((Building)r.getElement());
			}
		}
		return pos;
	}
	public DualLinkList<WeightedEdge<RallyPoint, Integer>> getEdges(){
		return map.getGraph().edgeList();
	}
}
