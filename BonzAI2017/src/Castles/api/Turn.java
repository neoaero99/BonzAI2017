package Castles.api;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Castles.CastlesRenderer;
import Castles.Objects.*;
import Castles.util.graph.CastlesMapGraph;
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

		this.map = map;
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
			
			teamSoldiers.put(t.getColor(), soldierGroups);
			teamPositions.put(t.getColor(), new HashMap<String, PositionData>());
		}
		
		ArrayList<RallyPoint> elements = map.getAllPositions();
		
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
	 * Testing the Turn class and AI API
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		
		try {
			CastlesMap map = Parser.parseFile("scenarios/testmap.dat");
			ArrayList<Team> teams = (ArrayList<Team>) map.getTeams();
			
			Soldier s0 = new Soldier(teams.get(0), 5, "P0");
			Soldier s1 = new Soldier(teams.get(1), 3, "P1");
			
			ArrayList<String> path0 = new ArrayList<String>();
			path0.add("P0");
			path0.add("C0");
			path0.add("V0");
			
			s0.setPath(path0);
			s0.setState(SoldierState.MOVING);
			
			map.addSoldiers(s0);
			map.addSoldiers(s1);
			
			Turn t = new Turn(0, 1, map);
			t.outputState();
			
			Turn nextT = t.apply(new ArrayList<Action>());
			nextT.outputState();
			
		} catch (Exception Ex) {
			Ex.printStackTrace();
		}
	}
	
	/**
	 * Outputs the data associated with the Turn to the console.
	 */
	private void outputState() {
		Collection<HashMap<String, SoldierData>> soldierGroups = teamSoldiers.values();
		
		for (HashMap<String, SoldierData> groups : soldierGroups) {
			System.out.printf("%s\n", groups);
		}
		System.out.println();
		
		Collection<HashMap<String, PositionData>> claimed = teamPositions.values();
		
		for (HashMap<String, PositionData> positions : claimed) {
			System.out.printf("%s\n", positions);
		}
		System.out.println();
		
		System.out.printf("%s\n\n\n", unclaimedPositions);
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
	public boolean isValid(Team team, Action action) {
		//TODO 2017: This is important for us and competitors.
		
		if (team == null || action == null) {
			return false;
		}
		
		if (action instanceof ShoutAction) {
			return true;
			
		} else if(action instanceof MoveAction) {
			MoveAction move = (MoveAction)action;
			ArrayList<String> pathIDs = move.getPathIDs();
			/* A path must contain the starting position, occupied by soldiers,
			 * and an ending position */
			if (pathIDs != null && pathIDs.size() > 1) {
				RallyPoint prev = map.getPosition(pathIDs.get(0));
				Soldier target = prev.getOccupant(move.getSoldierIdx());
				
				if (target.getLeader().equals(team) && prev != null && target != null &&
						target.getValue() > move.getSplitAmount()) {
					/* The positions in the path must exist and form a chain of
					 * adjacent positions */
					for (int idx = 1; idx < pathIDs.size(); ++idx) {
						RallyPoint curr = map.getPosition(pathIDs.get(idx));
						
						if (curr == null || map.areAdjacent(prev.ID, curr.ID)) {	
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
			RallyPoint r = map.getPosition(update.getSrcID());
			
			if (r != null) {
				Soldier target = r.getOccupant(update.getSoldierIdx());
				
				return target != null && target.getLeader().equals(team) &&
						(update.getState() == SoldierState.MOVING ||
						update.getState() == SoldierState.STANDBY);
			}
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

		/**
		 * Clones soldiers and graph elements into the new map. Also, updates
		 * reinforcements.
		 */
		CastlesMap newMap = new CastlesMap(map);

		// Store the teams whose action failed
		LinkedList<Team> failedTeams = new LinkedList<Team>();

		int teamID = 0;
		
		/**
		 * Resolve all actions
		 */
		for (Action action : actions) {
			//TODO Actions are Handled here
			if (isValid(teams.get(teamID), action)) {
				if (action instanceof ShoutAction) {
					shoutActions.add((ShoutAction)action);
					
				} else if (action instanceof MoveAction) {
					MoveAction move = (MoveAction)action;
					ArrayList<String> path = move.getPathIDs();
					RallyPoint src = newMap.getPosition(path.get(0));
					/* Split off a group of soldiers and give them the path
					 * specified by the move action */
					Soldier s = src.getOccupant(move.getSoldierIdx());
					Soldier partition = newMap.splitSoliders(s, move.getSplitAmount(), path);
					partition.setState(SoldierState.MOVING);
					newMap.addSoldiers(partition);
					
					moveActions.add(move);
					
				} else if (action instanceof UpdateAction) {
					UpdateAction update = (UpdateAction)action;
					RallyPoint src = newMap.getPosition(update.getSrcID());
					Soldier target = src.getOccupant(update.getSoldierIdx());
					
					target.setState(update.getState());
					
					updateActions.add((UpdateAction)action);
				}
				
			} else {
				// TODO Add AI to failedTeams list
			}

			teamID++;
		}

		// TODO apply any earned points onto this new Turn.
		
		newMap.moveSoldiers();
		
		/**
		 * Resolve any soldier conflicts and building occupations
		 */
		ArrayList<RallyPoint> rally = newMap.getAllPositions();
		for (RallyPoint r: rally) {
			//newMap.mergeSoldiers(r.onPoint, r);
			
			/* Determine if the remaining soldiers on a position can capture an
			 * unclaimed or enemy position. */
			if (r instanceof Building) {
				Building b = (Building)r;
				ArrayList<Soldier> occupants = r.getOccupants();
				
				if (occupants.size() > 0) {
					Team leader = occupants.get(0).getLeader();
					
					if (b.getTeam() == null || !b.getTeam().equals(leader)) {
						int occupantSize = 0;
						
						for (Soldier s : occupants) {
							occupantSize += s.getValue();
						}
						/* The total number of soldiers must be greater than
						 * the defense value of the position in order to
						 * capture it */
						if (occupantSize > b.defenseValue) {
							b.setTeam(leader);
						}
					}
				}
				
				Soldier s = b.reinforce();
				
				if (s != null) {
					newMap.addSoldiers(s);
				}
			}
		}
		
		return new Turn(this, teamID, newMap, failedTeams);
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
