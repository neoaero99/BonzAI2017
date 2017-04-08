package Castles.api;

import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Castles.CastlesMap;
import Castles.CastlesRenderer;
import Castles.CastlesScenario;
import Castles.Parser;
import Castles.Objects.*;
import Castles.util.graph.CastlesMapGraph;
import Castles.util.graph.Vertex;
import Castles.util.priorityq.AdaptablePQ;
import bonzai.Action;
import bonzai.Position;
import bonzai.Team;

/**
 * A snapshot in time at the current turn. In addition to accessing current 
 * state, stale references to entities can be used to get fresh references to
 * those entities at this turn. 
 **/
public class Turn {
	
	public static final Random generator;
	
	private final CastlesMap map;
	// Data used for AI queries
	private final HashMap<String, PositionData> positions;
	private final HashMap<TeamColor, List<PositionData>> teamPositions;
	private final List<PositionData> unclaimedPositions;
	private final HashMap<TeamColor, List<SoldierData>> teamSoldiers;
	
	final ArrayList<Boolean> success;
	final int turnNumber;
	
	private final HashMap<TeamColor, Action> AIActionsMap;

	private String errorMessage = "";

	// The current team that is acting.
	// For each client, this will ALWAYS be their teamId
	// This decides the context that isValid() will be run for.
	// Example, If I'm on team 1, I can move team 1's things.  If I'm on team 2, I can't
	//making this a class so all the pathfinding stuff is in one place

	// Example, If I'm on team 1, I can move team 1's things.  If I'm on team 2, I cannot

	int currentTeam;
	int MAX_TURNS = CastlesScenario.NUM_TURNS;
	
	static {
		generator = new Random(0);
	}

	/**
	 * Turn constructor.
	 * 
	 * @param teamNumber	the team number that this current Turn object
	 *					  	was made for
	 * @param turnNumber	the current turn number
	 * @param map			the map object to clone to start the turn off
	 */
	public Turn(int teamNumber, int turnNumber, CastlesMap map) {
		this.currentTeam = teamNumber;
		this.turnNumber = turnNumber;

		this.map = map;
		positions = new HashMap<String, PositionData>();
		teamPositions = new HashMap<TeamColor, List<PositionData>>();
		unclaimedPositions = new ArrayList<PositionData>();
		teamSoldiers = new HashMap<TeamColor, List<SoldierData>>();
		
		AIActionsMap = new HashMap<TeamColor, Action>();

		List<Team> teams = map.getTeams();
		success = new ArrayList<Boolean>();
		
		/**
		 * Initialize the team's list of positions and soldiers.
		 */
		for (Team t : teams) {
			teamPositions.put(t.getColor(), new ArrayList<PositionData>());
			teamSoldiers.put(t.getColor(), new ArrayList<SoldierData>());
		}
		
		ArrayList<RallyPoint> elements = map.getAllPositions();
		
		for (RallyPoint r : elements) {
			/* Associated claimed positions with the color of the team, which
			 * owns the position and then with the positions ID. Unclaimed
			 * positions are simply associated with their ID. */
			PositionData p = new PositionData(r);
			positions.put(p.ID, p);
			
			if (r instanceof Building) {
				Building b = (Building)r;
				
				if (b.getTeamColor() != null) {
					teamPositions.get(b.getTeamColor()).add(p);
					
				} else {
					unclaimedPositions.add(p);
				}
				
			} else {
				unclaimedPositions.add(p);
			}
			
			// Add the soldier data to the team list of soldiers.
			for (SoldierData sd : p.occupantData) {
				TeamColor c = sd.leader;
				teamSoldiers.get(c).add(sd);
			}
		}
	}

	/**
	 * Alternate turn constructor, used to specify a number of turns
	 * that the match should last for
	 * 
	 * @param teamNumber	the team number that this current Turn object
	 *						was made for
	 * @param turnNumber	the current turn number
	 * @param map			the map object to clone to start the turn off
	 * @param MAX_TURNS		the total number of turns in the match
	 */
	public Turn(int teamNumber, int turnNumber, CastlesMap map, int MAX_TURNS) {
		this(teamNumber, turnNumber, map);
		this.MAX_TURNS = MAX_TURNS;
	}

	/**
	 * Copy constructor for Turn
	 * 
	 * @param turn			the Turn object to copy
	 * @param teamNumber
	 * @param map
	 */
	public Turn(Turn turn, int teamNumber, CastlesMap map, Collection<Team> failedActions) {
		this(teamNumber, turn.turnNumber + 1, map);
		this.MAX_TURNS = turn.MAX_TURNS;

		//for (Team t : failedActions) {
			//success.set(t.getID(), false);
		//}
	}
	
	/**
	 * Testing the Turn class and AI API
	 * 
	 * @param args	Unused
	 */
	public static void main(String[] args) {
		
		CastlesMap map = null;
		
		try {
			map = Parser.parseFile("scenarios/triangles.dat");
			map.addSoldiers(new Soldier(TeamColor.RED, 6, "R0"));
			map.addSoldiers(new Soldier(TeamColor.RED, 3, "R0"));
			map.addSoldiers(new Soldier(TeamColor.RED, 8, "R0"));
			map.addSoldiers(new Soldier(TeamColor.RED, 9, "R0"));
			
			map.addSoldiers(new Soldier(TeamColor.RED, 12, "R1"));
			map.addSoldiers(new Soldier(TeamColor.RED, 5, "R1"));
			map.addSoldiers(new Soldier(TeamColor.RED, 2, "R1"));
			map.addSoldiers(new Soldier(TeamColor.RED, 11, "R1"));
			
			Turn turn = new Turn(0, 1, map, 5);
			
			Team team = turn.getMyTeam();
			TeamColor enemy = turn.getEnemyTeams().get(0).getColor();
			List<SoldierData> soldiers = turn.getSoldiersControlledBy(team.getColor());
			
			System.out.println(soldiers);
			MoveAction m = new MoveAction();
			
			System.out.println(team.getColor());
			
			/* Test move commands *
			m.addMove(0, 1, "R0", "P1");
			m.addMove(-5, 1, "R0", "P1");
			m.addMove(5, 1, "R0", "P1");
			m.addMove(0, -5, "R0", "P1");
			m.addMove(0, 7, "R0", "P1");
			m.addMove(0, 1, null, "P1");
			m.addMove(3, 1, "P0", "P1");
			m.addMove(0, 1, "R0", null);
			/**/
			
			/* Test update commands */
			m.addUpdate(0, "P0", SoldierState.MOVING);
			m.addUpdate(0, "P0", SoldierState.STANDBY);
			m.addUpdate(-5, "P0", SoldierState.MOVING);
			m.addUpdate(0, "P0", null);
			m.addUpdate(2, "P0", null);
			/**/
			
			System.out.println( turn.verifyMoveAction(team, m) );
			
			turn.AIActionsMap.put(team.getColor(), m);
			
			/**
			System.out.println(t.getMyTeam().getColor());
			System.out.println(enemy);
			
			/**
			if (soldiers.size() > 0) {
				SoldierData s = soldiers.get(0);
				List<PositionData> closestBuildings = t.getClosestByColor(s.posID, null);
				System.out.println(closestBuildings);
				/**
				List<String> path = t.getPath(s.posID, closestBuildings.get(0).ID);
				
				System.out.printf("%s\n", path);
				/**
			}
			
			/**
			ArrayList<String> path = map.getPath("!P0-R0:1", "P0");
			System.out.printf("%s\n", path);
			
			path = map.getPath("!C0-V0:2", "P0");
			System.out.printf("%s\n", path);
			
			path = map.getPath("!C0-V0:1", "!P0-R0:1");
			System.out.printf("%s\n", path);
			
			/**
			RallyPoint r = map.getPosition("R0");
			//((Building)r).setTeamColor(TeamColor.BLUE);
			
			Soldier s1 = new Soldier(TeamColor.RED, 13, r.ID);
			Soldier s2 = new Soldier(TeamColor.RED, 6, r.ID);
			Soldier s3 = new Soldier(TeamColor.RED, 8, r.ID);
			
			map.addSoldiers(s1);
			map.addSoldiers(s2);
			map.addSoldiers(s3);
			
			int[] sizeDiff = map.mergeSoldiers(r);
			
			System.out.printf("%s\n", r.getOccupants());
			
			/**
			ArrayList<Team> teams = (ArrayList<Team>) map.getTeams();
			
			ArrayList<String> path0 = new ArrayList<String>();
			path0.add("P0");
			path0.add("C0");
			path0.add("V0");
			
			Turn t = new Turn(0, 1, map);
			
			t.outputState();
			
			ArrayList<Action> actions = new ArrayList<Action>();
			
			SoldierData[] soldierSet = t.getPosition("P0").occupantData;
			SoldierData s0 = soldierSet[0];
			
			List<PositionData> positions = t.getPositionsControlledBy(TeamColor.RED);
			System.out.println(positions.size());
			
			for (PositionData pd : positions) {
				for (SoldierData sd : pd.occupantData) {
					System.out.printf("%d\n", sd.size);
				}
			}
			
			MoveAction a = new MoveAction();
			a.addMove(0, s0.size, "P0", "V0");
			actions.add(a);
			
			for (RallyPoint r : t.map.getAllPositions()) {
				System.out.printf("%s: %s\n", r.ID, r.getOccupants());
			}
			
			Turn nextT = t.apply(actions);
			nextT.outputState();
			
			for (RallyPoint r : nextT.map.getAllPositions()) {
				System.out.printf("%s: %s\n", r.ID, r.getOccupants());
			}
			
			nextT = nextT.apply(new ArrayList<Action>());
			nextT.outputState();
			
			for (RallyPoint r : nextT.map.getAllPositions()) {
				System.out.printf("%s: %s\n", r.ID, r.getOccupants());
			}
			
			/**/
			
		} catch (Exception Ex) {
			Ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Outputs the data associated with the Turn to the console.
	 */
	private void outputState() {
		Collection<List<SoldierData>> soldierGroups = teamSoldiers.values();
		
		for (List<SoldierData> groups : soldierGroups) {
			System.out.printf("%s\n", groups);
		}
		System.out.println();
		
		Collection<List<PositionData>> claimed = teamPositions.values();
		
		for (List<PositionData> positions : claimed) {
			System.out.printf("%s\n", positions);
		}
		System.out.println();
		
		System.out.printf("%s\n\n\n", unclaimedPositions);
	}
	
	/*************************************************************************
	 * 
	 *						  ADD API METHODS HERE!!!!!!!!!
	 *
	 */
	
	/**
	 * @return	A list of all positions on the map
	 */
	public List<PositionData> getAllPositions() {
		return new ArrayList<PositionData>( positions.values() );
	}
	
	/**
	 * Returns the position with the given ID value, if one exists. If no
	 * position exits with the given ID, null is returned.
	 * </br>
	 * A vertex position's ID is a single character followed by a number.
	 * </br>
	 * Rally points have IDs R0, R1, R2, ...
	 * </br>
	 * Villages have IDs V0, V1, V2, ...
	 * </br>
	 * Castles have IDs C0, C1, C2, ...
	 * </br>
	 * The two bases in a scenario have the IDs P0 and P1
	 * </br>
	 * </br>
	 * Edge waypoint IDs are a bit more complicated.
	 * </br>
	 * Edge waypoint ID = !first-last:<x>
	 * </br>
	 * first is the ID of a vertex connected to the edge, to which the
	 * position belongs
	 * </br>
	 * second is the ID of the other vertex connected to the edge, to
	 * which the position belongs
	 * </br>
	 * x is the index of the position with respect to other waypoints
	 * on the edge. If an edge has 3 waypoints, then those waypoints
	 * would have x values 0, 1, and 2.
	 * </br>
	 * 
	 * @param ID	The unique identifier of a position in the map
	 * @return		The position associated with the given ID, if it exists in
	 * 				the map
	 */
	public PositionData getPosition(String ID) {
		return positions.get(ID);
	}
	
	/**
	 * Returns the base position for the given team or null if the given team
	 * is invalid.
	 * 
	 * @param t	The team, of which to find the base
	 * @return	The base of the given team
	 */
	public PositionData getBaseFor(Team t) {
		if (t != null) {
			return getPosition( String.format("P%d", t.getID()) );
		}
		
		return null;
	}
	
	/**
	 * Returns a list of positions that are adjacent to the position with the
	 * given ID. If the position is invalid, an empty set is returned.
	 * 
	 * @param ID	The ID of a position
	 * @return		The positions adjacent to the position with the given ID
	 */
	public List<PositionData> adjacentTo(String ID) {
		List<PositionData> adjPositions = new ArrayList<PositionData>();
		List<String> adjPosIDs = map.getAdjPosIDs(ID);
		
		for (String adjPosID : adjPosIDs) {
			adjPositions.add( getPosition(adjPosID) );
		}
		
		return adjPositions;
	}
	
	/**
	 * Returns a set of positions controlled by the team of the given color. If
	 * the given color is null, then a list of uncontrolled positions will be
	 * returned (this includes rally points).
	 * 
	 * @param teamColor	The color of the team controlling positions, of which
	 * 					will by queried, or null to query for uncontrolled
	 * 					positions
	 * @return			A list of positions controlled by the team of the given
	 * 					color
	 */
	public List<PositionData> getPositionsControlledBy(TeamColor teamColor) {
		if(teamColor==null){
			return unclaimedPositions;
		}
		return teamPositions.get(teamColor);
	}
	
	/**
	 * Returns a set of soldiers, which are controlled by the team with the
	 * given color. If no team with the given color exists, then an empty
	 * list is returned.
	 * 
	 * @param teamColor	The color of the team, whose soldier data to get
	 * @return			The list of soldiers associated with the team with the
	 * 					given color
	 */
	public List<SoldierData> getSoldiersControlledBy(TeamColor teamColor) {
		return teamSoldiers.get(teamColor);
	}
	
	/**
	 * Returns a list of IDs corresponding to positions, which form a path from
	 * the position with startID to the position with endID. The path includes
	 * the starting and ending IDs.
	 * 
	 * @param startID	The ID of a vertex on the map
	 * @param endID		The ID of another vertex on the map
	 * @return			The path between the two vertices of given IDs
	 */
	public List<String> getPath(String startID, String endID) {
		return map.getPath(startID, endID);
	}
	
	/**
	 * Returns the positions with the given building type that are the closest
	 * to the given position.
	 * 
	 * @param refID		The reference position
	 * @param target	The type of building to find
	 * @return			A list of positions with the given type that are
	 * 					the closest to the given position
	 */
	public List<PositionData> getClosestByType(String refID, PType target) {
		List<PositionData> positions = new ArrayList<PositionData>();
		PositionData p = getPosition(refID);
		
		if (p != null) {
			HashMap<String, Boolean> posVisited = new HashMap<String, Boolean>();
			ArrayList<String> curLayer = map.getAdjPosIDs(refID);
			
			posVisited.put(refID, true);
			
			for (String ID : curLayer) {
				posVisited.put(ID, true);
				PositionData adjP = getPosition(ID);
				
				if (adjP.type == target) {
					positions.add(adjP);
				}
			}
			
			while (positions.size() == 0 && curLayer.size() > 0) {
				ArrayList<String> nextLayer = new ArrayList<String>();
				
				for (String ID : curLayer) {
					ArrayList<String> adjPosIDs = map.getAdjPosIDs(ID);
					
					for (String adjID : adjPosIDs) {
						Boolean visited = posVisited.get(adjID);
						
						if (visited == null) {
							posVisited.put(adjID, true);
							PositionData adjP = getPosition(adjID);
							
							if (adjP.type == target) {
								positions.add(adjP);
							}
							
							nextLayer.add(adjID);
						}
					}
				}
				
				curLayer = nextLayer;
			}
		}
		
		return positions;
	}
	
	/**
	 * Returns the positions controlled by the team with the given color that
	 * are the closest to the given position.
	 * 
	 * @param refID		The reference position
	 * @param target	The color of a team
	 * @return			A list of positions controlled by the team with the
	 * 					given color that are the closest to the given position
	 */
	public List<PositionData> getClosestByColor(String refID, TeamColor target) {
		List<PositionData> positions = new ArrayList<PositionData>();
		PositionData p = getPosition(refID);
		
		if (p != null) {
			HashMap<String, Boolean> posVisited = new HashMap<String, Boolean>();
			ArrayList<String> curLayer = map.getAdjPosIDs(refID);
			
			posVisited.put(refID, true);
			
			for (String ID : curLayer) {
				posVisited.put(ID, true);
				PositionData adjP = getPosition(ID);
				
				if (adjP.type != PType.RALLY && adjP.leader == target) {
					positions.add(adjP);
				}
			}
			
			while (positions.size() == 0 && curLayer.size() > 0) {
				ArrayList<String> nextLayer = new ArrayList<String>();
				
				for (String ID : curLayer) {
					ArrayList<String> adjPosIDs = map.getAdjPosIDs(ID);
					
					for (String adjID : adjPosIDs) {
						Boolean visited = posVisited.get(adjID);
						
						if (visited == null) {
							posVisited.put(adjID, true);
							PositionData adjP = getPosition(adjID);
							
							if (adjP.type != PType.RALLY && adjP.leader == target) {
								positions.add(adjP);
							}
							
							nextLayer.add(adjID);
						}
					}
				}
				
				curLayer = nextLayer;
			}
		}
		
		return positions;
	}
	
	/**
	 * Returns your AI's team.
	 * 
	 * @return	your AI's team
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
	 * Returns all the enemy teams.
	 * 
	 * @return	a list of all enemy teams
	 */
	public List<Team> getEnemyTeams() {
		List<Team> eTeams = new ArrayList<Team>();		
		
		for (Team t : getAllTeams()) {
			if (t.getID() != currentTeam) {
				eTeams.add(t);
			}
		}
		
		return eTeams;
	}
	
	/**
	 * Returns a list of all AI team's, which are participating in the match.
	 * 
	 * @return	The list of all teams
	 */
	public List<Team> getAllTeams() {
		return map.getTeams();
	}
	
	/*************************************************************************/
	
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
	 * Returns whether this is the first turn or not
	 * 
	 * @return
	 */
	public boolean isFirstTurn() {
		return turnNumber == 0;
	}
	
	/**
	 * Determines if the game has finished. This can occur if there are no more
	 * turns remaining or an AI controls all buildings on the map.
	 * 
	 * @return	The end of the game has been reached
	 */
	public boolean gameOver() {
		for (Team t : map.getTeams()) {
			// Does an AI control no buildings
			List<PositionData> buildings = getPositionsControlledBy(t.getColor());
			
			if (buildings.size() == 0) {
				return true;
			}
		}
		
		return getTurnsRemaining() <= 1;
	}

	/**
	 * Returns the score for the specified Team
	 * 
	 * @param t	the Team object to get the score for
	 * @return	the Team's score
	 */
	public int getScore(Team t) { 
		return t.getScore();
	}
	
	/**
	 * Returns the soldier loss count for the team with the given ID.
	 * 
	 * @param teamID	The ID of some team
	 * @return			The number of soldiers that they have lost
	 */
	public int getTeamLossCount(int teamID) {
		return map.getTeamLossCount(teamID);
	}

	/**
	 * Returns the number of remaining turns in the match
	 * 
	 * @return	the number of remaining turns
	 */
	public int getTurnsRemaining() {
		return MAX_TURNS - turnNumber;
	}
	
	/**
	 * Checks all commands in the move action and determines if any of the
	 * command are invalid. The returned map maps the index of an invalid
	 * action with a message describing the error; valid commands will not
	 * appear in the map. In addition, general errors with the given parameters
	 * for the method are associated with the key -1 in the map.
	 * 
	 * @param team	The team of the AI, to which the action belongs
	 * @param ms	The move action to verify
	 */
	public HashMap<Integer, String> verifyMoveAction(Team team, MoveAction ma) {
		HashMap<Integer, String> errorMessages = new HashMap<Integer, String>();
		
		// General errors have key -1
		if (team == null) {
			errorMessages.put(-1, "Null team");
			
		} else if (ma == null) {
			errorMessages.put(-1, "Null action");
			
		} else if (ma.numOfActions() == 0) {
			errorMessages.put(-1, "Empty action set");
		
		} else {
			/* Command specific errors have a key corresponding to the index of the
			 * command in the action */
			for (int idx = 0; idx < ma.numOfActions(); ++idx) {
				
				Object cmd = ma.get(idx);
				String error = null;
				
				if (cmd instanceof MoveSoldier) {
					error = verifyMSCMD(team, (MoveSoldier)cmd);
					
				} else if (cmd instanceof UpdateSoldier) {
					error = verifyUSCMD(team, (UpdateSoldier)cmd);
					
				} else {
					// Should never happen
					error = "Invalid command type: " + cmd.getClass();
				}
				
				if (error != null) {
					// Error exists with the command
					errorMessages.put(idx, error);
				}
			}
		}
		
		return errorMessages;
	}
	
	/**
	 * Checks all the parameters of the given move command assuming the given
	 * team controls the soldier specified by the command. If the command is
	 * valid, null is returned. Otherwise a string describing the error is
	 * returned.
	 * 
	 * @param team	The team of the AI, who is giving the command
	 * @param cmd	The move command to verify
	 * @return		An error message or null if the command is valid
	 */
	private String verifyMSCMD(Team team, MoveSoldier cmd) {
		// Check a soldier path update action
		RallyPoint r = map.getPosition(cmd.endID);
		
		if (r != null) {
			r = map.getPosition(cmd.startID);
			
			if (r != null) {
				Soldier s = r.getOccupant(cmd.soldierIdx);
				
				if (s != null && s.getLeaderColor() == team.getColor()) {
					/* Clamp the split amount for the move action
					 * within the valid range for the target
					 * soldier group */
					cmd.splitAmt = Math.max(1, Math.min(cmd.splitAmt,
							s.getValue()));
					
					return null;
					
				} else {
					if (s == null) {
						return String.format("No soldier %d on position %s",
								cmd.soldierIdx, cmd.startID);
						
					} else {
						return String.format("Soldier %d on %s does not belong to team %s",
								cmd.soldierIdx, cmd.startID, team.getColor());
					}
				}
				
			} else {
				return "No position: " + cmd.startID;
			}
			
		} else {
			return "No position: " + cmd.endID;
		}
	}
	
	/**
	 * Checks all the parameters of the given update command assuming the given
	 * team controls the soldier specified by the command. If the command is
	 * valid, null is returned. Otherwise a string describing the error is
	 * returned.
	 * 
	 * @param team	The team of the AI, who is giving the command
	 * @param cmd	The update command to verify
	 * @return		An error message or null if the command is valid
	 */
	private String verifyUSCMD(Team team, UpdateSoldier cmd) {
		// Check a soldier state update action;
		RallyPoint r = map.getPosition(cmd.posID);
		
		if (r != null) {
			Soldier s = r.getOccupant(cmd.soldierIdx);
			
			if (s != null) {
				
				if (s.getLeaderColor() == team.getColor()) {
					
					if (cmd.newState == SoldierState.STANDBY || cmd.newState == SoldierState.MOVING) {
						return null;
						
					} else {
						return String.format("Invalid state: %s", cmd.newState);
					}
					
				} else {
					return String.format("Soldier %d on %s does not belong to team %s",
							cmd.soldierIdx, cmd.posID, team.getColor());
				}
				
			} else {
				return String.format("No soldier %d at position, %s", cmd.soldierIdx, r.ID);
			}
				
		} else {
			return String.format("No position: %s", cmd.posID);
		}
	}

	/**
	 * 2017: Unused
	 * 
	 * See verifyMoveAction()
	 * 
	 * @param team
	 * @param action
	 * @return
	 */
	public boolean isValid(Team team, Action action) {
		//TODO 2017: This is important for us and competitors.
		return action instanceof ShoutAction;
	}

	/**
	 * Applies an action to the current turn. This should 
	 * not be called by an AI as it is almost useless.
	 * 
	 * @param actions	the list of actions to apply
	 * @return			the new Turn object created after applying 
	 * 					the actions
	 */
	public Turn apply(List<Action> actions) {
		
		//TODO 2017: This is where we applied each action from the playing ai's. This is an example of our process. 
		// Most of the game logic goes here.
		
		/**
		 * Clones soldiers and graph elements into the new map.
		 */
		CastlesMap newMap = new CastlesMap(map);

		// Store the teams whose action failed
		LinkedList<Team> failedTeams = new LinkedList<Team>();
		List<Team> teams = newMap.getTeams();
		int[] teamScoreAdditions = new int[teams.size()];
		int teamID = 0;
		
		/**
		 * Resolve all actions
		 */
		for (Action action : actions) {
			//TODO Actions are Handled here
			Team t = teams.get(teamID);
			
			if (action instanceof ShoutAction) {
				AIActionsMap.put(t.getColor(), action);
				
			} else if (action instanceof MoveAction) {
				MoveAction move = (MoveAction)action;
				int sdSize = getSoldiersControlledBy(t.getColor()).size();
				// Determine if at least one command was successful
				boolean cmdPassed = false;
				
				/* Only at most a number of commands equal to the number of
				 * soldier groups for an AI */
				for (int idx = 0; idx < sdSize && idx < move.numOfActions(); ++idx) {
					Object obj = move.get(idx);
					
					if (obj instanceof MoveSoldier) {
						MoveSoldier ms = (MoveSoldier)obj;
						
						if (verifyMSCMD(t, ms) == null) {
							cmdPassed = true;
							// Update the path of a soldier
							ArrayList<String> path = new ArrayList<String>( getPath(ms.startID, ms.endID) );
							RallyPoint src = newMap.getPosition(ms.startID);
							Soldier target = src.getOccupant(ms.soldierIdx);
							
							if (target.getValue() == ms.splitAmt) {
								// Move the entire group	
								target.setPath(path);
								target.setState(SoldierState.MOVING);
								
							} else {
								/* Split off a portion of the soldier group to move along
								 * the given path */
								newMap.splitSoliders(target, ms.splitAmt, path);
							}
						}
						
					} else {
						UpdateSoldier us = (UpdateSoldier)obj;
						
						if (verifyUSCMD(t, us) == null) {
							cmdPassed = true;
							// Update a soldier's state
							RallyPoint r = newMap.getPosition(us.posID);
							Soldier s = r.getOccupant(us.soldierIdx);
							s.setState(us.newState);
						}
					}
				}
				
				if (!cmdPassed) {
					// No commands were successful
					failedTeams.add(teams.get(teamID));
				}
				
				AIActionsMap.put(teams.get(teamID).getColor(), action);
			}

			teamID++;
		}

		// TODO apply any earned points onto this new Turn.
				
		newMap.moveSoldiers();
		
		/**
		
		for (ArrayList<Soldier> teamSoldiers : newMap.getSoldiers()) {
			for (Soldier s : teamSoldiers) {
				System.out.printf("%s - %s\n", s.getLeaderColor(), s.getPath());
			}
		}
		
		for (RallyPoint r : newMap.getAllPositions()) {
			System.out.printf("%s - %s\n", r.ID, r.onPoint);
		}
		
		/**/
		
		/**
		 * Resolve any soldier conflicts and building occupations
		 */
		ArrayList<RallyPoint> rally = newMap.getAllPositions();
		for (RallyPoint r: rally) {
			newMap.mergeSoldiers(r);
			
			/* Determine if the remaining soldiers on a position can capture an
			 * unclaimed or enemy position. */
			if (r instanceof Building) {
				Building b = (Building)r;
				ArrayList<Soldier> occupants = r.getOccupants();
				
				if (occupants.size() > 0) {
					TeamColor leaderColor = occupants.get(0).getLeaderColor();
					
					if (b.getTeamColor() == null || b.getTeamColor() != leaderColor) {
						int occupantSize = 0;
						
						for (Soldier s : occupants) {
							occupantSize += s.getValue();
						}
						/* The total number of soldiers must be greater than
						 * the defense value of the position in order to
						 * capture it */
						if (occupantSize >= b.getDefVal()) {
							b.setTeamColor(leaderColor);
						}
					}
				}
				
				// Apply reinforcements for claimed positions
				Soldier s = b.reinforce();
				
				if (s != null) {
					newMap.addSoldiers(s);
				}
				
				TeamColor c = b.getTeamColor();
				if(c != null){
						int ID =c.ordinal();
						teamScoreAdditions[ID]+=b.getDefVal();
				}
			}
		}
		
		newMap.updateTeamScores(teamScoreAdditions);
		return new Turn(this, currentTeam, newMap, failedTeams);
	}
	
	/**
	 * Returns a random xy position corresponding to a building, which is
	 * controlled by the AI with the given team color. If the AI controls
	 * no positions, null is returned.
	 * 
	 * @param target	The color of the target team
	 * @return			The position of some building on the map
	 */
	public Position getRanOccupiedPos(TeamColor target) {
		ArrayList<RallyPoint> possiblePositions = new ArrayList<RallyPoint>();
		ArrayList<RallyPoint> elementList = map.getAllPositions();
		
		// Find all positions controlled by the AI with the given team color
		for(RallyPoint r : elementList) {
			if(r instanceof Building) {
				if(((Building)r).getTeamColor() == target) {
					possiblePositions.add(r);
				}
			}
		}
		
		if (possiblePositions.size() > 0) {
			return possiblePositions.get(0).getPosition();
		}
		// No positions exist
		return null;
	}
	
	/**
	 * Returns the message shouted by the AI with the given color, or null if
	 * the AI did not choose a shout action this turn.
	 * 
	 * @param tc	The color of team, of which to get the message
	 * @return		The message shouted by this AI this turn
	 */
	public String getShoutFor(TeamColor tc) {
		Action a = AIActionsMap.get(tc);
		
		if (a instanceof ShoutAction) {
			return ((ShoutAction)a).getMessage();
		}
		
		return null;
	}
	
	/**
	 * Renders all the elements of the map, including the background for this
	 * turn.  
	 * 
	 * @param g	The graphics object used for rendering
	 */
	public void renderMap(Graphics2D g) {
		try{
			if (g != null) {
				CastlesRenderer.renderBackground(g, map);
				CastlesRenderer.renderPaths(g, map);
				CastlesRenderer.renderBuildings(g, map);
				CastlesRenderer.renderSoldiers(g, map);
			}
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
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
	
	/**
	 * Gets the castle closest to the position P
	 * 
	 * @param p:	The data of a position
	 * @return:		The position data of the closest castle
	 */
	public PositionData getClosestCastle(PositionData p) {
		ArrayList<String> out = null;
		PositionData out1 = null;
		ArrayList<PositionData> castles = new ArrayList<>(); 
		
		for(PositionData pd : unclaimedPositions){
			if(pd.type == PType.CASTLE){
				castles.add(pd);
			}
		}
		
		for(List<PositionData> d : teamPositions.values()){
			for(PositionData pd : d){
				if(pd.type == PType.CASTLE){
					castles.add(pd);
				}
			}
		}
		
		for(PositionData d : castles){
			if(out == null){ 
				out = map.getPath(d.ID, p.ID);
				out1 = d;
			}
			ArrayList<String> temp = map.getPath(d.ID, p.ID);
			if(temp.size() < out.size()){
				temp = out;
				out1 = d;
			}
		}
		
		return out1;
	}
	
	/**
	 * Gets the village closest to the position P
	 * 
	 * @param p:	The data of a position
	 * @return:		The position data of the closest village
	 */
	public PositionData getClosestVillage(PositionData p){
		ArrayList<String> out = null;
		PositionData out1 = null;
		ArrayList<PositionData> castles = new ArrayList<>(); 
		for(PositionData pd : unclaimedPositions){
			if(pd.type == PType.VILLAGE){
				castles.add(pd);
			}
		}
		for(List<PositionData> d : teamPositions.values()){
			for(PositionData pd : d){
				if(pd.type == PType.VILLAGE){
					castles.add(pd);
				}
			}
		}
		for(PositionData d : castles){
			if(out == null){ 
				out = map.getPath(d.ID, p.ID);
				out1 = d;
			}
			ArrayList<String> temp = map.getPath(d.ID, p.ID);
			if(temp.size() < out.size()){
				temp = out;
				out1 = d;
			}
		}
		
		return out1;
	}
}
