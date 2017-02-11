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
	
	private final CastlesMap map;
	// Data used for AI queries
	private final HashMap<Color, HashMap<String, SoldierData>> teamSoldiers;
	private final HashMap<Color, HashMap<String, PositionData>> teamPositions;
	private final HashMap<String, PositionData> unclaimedPositions;
	
	final ArrayList<Boolean> success;
	final int turnNumber;

	private final ArrayList<ShoutAction> shoutActions;
	private final ArrayList<MoveAction> moveActions;

	private String errorMessage = "";

	// The current team that is acting.
	// For each client, this will ALWAYS be their teamId
	// This decides the context that isValid() will be run for.
	// Example, If I'm on team 1, I can move team 1's things.  If I'm on team 2, I can't
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

		List<Team> teams = map.getTeams();
		success = new ArrayList<Boolean>();
		
		for (Team t : teams) {
			/* Associate each group of soldiers with their team color and with
			 * their position */
			HashMap<String, SoldierData> soldierGroups = new HashMap<String, SoldierData>();
			ArrayList<Soldier> soldiers = map.getSoldiers(t);
			
			for (Soldier s : soldiers) {
				SoldierData data = getSoldierData(s);
				
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
				
				if (b.getTeamColor() != null) {
					teamPositions.get(b.getTeamColor()).put(p.ID, p);
					
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
		
		try {
			CastlesMap map = Parser.parseFile("scenarios/testmap.dat");
			ArrayList<Team> teams = (ArrayList<Team>) map.getTeams();
			
			ArrayList<String> path0 = new ArrayList<String>();
			path0.add("P0");
			path0.add("C0");
			path0.add("V0");
			
			Turn t = new Turn(0, 1, map);
			t.outputState();
			
			ArrayList<Action> actions = new ArrayList<Action>();
			
			ArrayList<SoldierData> soldierSet = t.getSoldiersAt("P0");
			SoldierData s0 = soldierSet.get(0);
			
			List<PositionData> positions = t.getPositionsControlledBy(Color.RED);
			System.out.println(positions.size());
			
			for (PositionData pd : positions) {
				for (int sNum : pd.occupantSizes) {
					System.out.printf("%d\n", sNum);
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
	 * @param s
	 * @return
	 */
	private SoldierData getSoldierData(Soldier s) {
		RallyPoint r = map.getPosition(s.getPositionID());
		
		if (r != null) {
			
			for (int idx = 0; idx < r.getOccupants().size(); ++idx) {
				if (s == r.getOccupant(idx)) {
					return new SoldierData(s, idx);
				}
			}
		}
		
		return null;
	}
	
	/*************************************************************************
	 * 
	 *						  ADD API METHODS HERE!!!!!!!!!
	 *
	 */
	
	/**
	 * Returns the position with the given ID value, if one exists. If no
	 * position exits with the given ID, null is returned.
	 * 
	 * @param ID	The unique identifier of a position in the map
	 * @return		The position associated with the given ID, if it exists in
	 * 				the map
	 */
	public PositionData getPosition(String ID) {
		Collection<HashMap<String, PositionData>> claimedPositions = teamPositions.values();
		// Check each list of positions owned by teams for the position
		for (HashMap<String, PositionData> positionsSet : claimedPositions) {
			PositionData pos = positionsSet.get(ID);
			
			if (pos != null) {
				return pos;
			}
		}
		// Finally, check the unclaimed positions
		return unclaimedPositions.get(ID);
	}
	
	/**
	 * Returns a list of soldiers, which occupy the position with the given ID.
	 * If the position with the given ID is unoccupied, an empty set is
	 * returned. If no position with the given ID exists, then null is
	 * returned.
	 * 
	 * @param ID	The ID of a position on the map
	 * @return		The list of soldiers occupying the position with the given
	 * 				ID
	 */
	public ArrayList<SoldierData> getSoldiersAt(String ID) {
		ArrayList<SoldierData> data = new ArrayList<SoldierData>();
		RallyPoint r =map.getPosition(ID);
		for(Soldier s: r.onPoint){
			data.add(getSoldierData(s));
		}
		return data;
	}
	
	/**
	 * Returns a set of positions controlled by the team of the given color. If
	 * the given color is null, then a list of uncontrolled positions will be
	 * returned.
	 * 
	 * @param teamColor	The color of the team controlling positions, of which
	 * 					will by queried, or null to query for uncontrolled
	 * 					positions
	 * @return			A list of positions controlled by the team of the given
	 * 					color
	 */
	public List<PositionData> getPositionsControlledBy(Color teamColor) {
		HashMap<String, PositionData> step1 = teamPositions.get(teamColor);
		Collection<PositionData> step2=step1.values();
		ArrayList<PositionData> step3=new ArrayList<PositionData>();
		step3.addAll(step2);
		return step3;
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
	public List<SoldierData> getSoldiersControlledBy(Color teamColor) {
		HashMap<String, SoldierData> step1 = teamSoldiers.get(teamColor);
		Collection<SoldierData> step2=step1.values();
		return new ArrayList<SoldierData>(step2);
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
	
	/*************************************************************************/
	
	
	
	
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
	 * If isValid() returns false, this method returns
	 * a String containing a more detailed error message
	 * 
	 * @return - a String explaining why a move is invalid
	 */
	public String getIsValidError() {
		return errorMessage;
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
		
		if (team == null) {
			errorMessage = "Team argument cannot be null.";
			
		} else if (action == null) {
			errorMessage = "Action argument cann be null";
			
		} else if (action instanceof ShoutAction) {
			errorMessage = "";
			return true;
			
		} else if (action instanceof MoveAction) {
			MoveAction move = (MoveAction)action;
			
			for (int idx = 0; idx < move.numOfActions(); ++idx) {
				Object obj = move.get(idx);
				
				if (obj instanceof MoveSoldier) {
					// Check a soldier path update action
					MoveSoldier ms = (MoveSoldier)obj;
					RallyPoint r = map.getPosition(ms.endID);
					
					if (r != null) {
						r = map.getPosition(ms.startID);
						
						if (r != null) {
							Soldier s = r.getOccupant(ms.soldierIdx);
							
							if (s != null) {
								int sa = ms.splitAmt;
								
								if (sa > 0 && sa <= s.getValue()) {
									errorMessage = "";
									continue;
									
								} else {
									ms.splitAmt=s.getValue();
								}
								
								
							} else {
								errorMessage = String.format("No soldier on position %s", ms.startID);
							}
							
						} else {
							errorMessage = String.format("No position %s", ms.endID);
						}
						
					} else {
						errorMessage = String.format("No position %s", ms.startID);
					}
					
					
				} else if (obj instanceof UpdateSoldier) {
					// Check a soldier state update action
					UpdateSoldier us = (UpdateSoldier)obj;
					RallyPoint r = map.getPosition(us.posID);
					
					if (r != null) {
						Soldier s = r.getOccupant(us.soldierIdx);
						
						if (s != null) {
							
							if (us.newState == SoldierState.STANDBY || us.newState == SoldierState.MOVING) {
								errorMessage = "";
								continue;
								
							} else {
								errorMessage = String.format("Invalid state %s", us.newState);
							}
							
						} else {
							errorMessage = String.format("No soldiers at position %s", r.ID);
						}
							
					} else {
						errorMessage = String.format("No position %s", us.posID);
					}
					
				} else {
					// Invalid action
					errorMessage = "Invalid soldier action";
				}
				
				return false;
			}
			
			return true;
			
		} else {
			errorMessage = "Invalid action given.";
		}
		
		return false;
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
			if (isValid(teams.get(teamID), action)) {
				if (action instanceof ShoutAction) {
					shoutActions.add((ShoutAction)action);
				} else if (action instanceof MoveAction) {
					MoveAction move = (MoveAction)action;
					
					for (int idx = 0; idx < move.numOfActions(); ++idx) {
						Object obj = move.get(idx);
						
						if (obj instanceof MoveSoldier) {
							// Update the path of a soldier
							MoveSoldier ms = (MoveSoldier)obj;
							ArrayList<String> path = new ArrayList<String>( getPath(ms.startID, ms.endID) );
							RallyPoint src = newMap.getPosition(ms.startID);
							Soldier target = src.getOccupant(ms.soldierIdx);
							
							if (target.getValue() == ms.splitAmt) {
								target.setPath(path);
								
							} else {
								newMap.splitSoliders(target, ms.splitAmt, path);
							}
							
							target.setState(SoldierState.MOVING);
							
						} else {
							// Update a soldier's state
							UpdateSoldier us = (UpdateSoldier)obj;
							RallyPoint r = newMap.getPosition(us.posID);
							Soldier s = r.getOccupant(us.soldierIdx);
							s.setState(us.newState);
						}
					}
					
					moveActions.add(move);
				}
				
			} else {
				failedTeams.add(teams.get(teamID));
				// Test scoring
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
			newMap.mergeSoldiers(r.onPoint, r);
			
			/* Determine if the remaining soldiers on a position can capture an
			 * unclaimed or enemy position. */
			if (r instanceof Building) {
				Building b = (Building)r;
				ArrayList<Soldier> occupants = r.getOccupants();
				
				if (occupants.size() > 0) {
					Color leaderColor = occupants.get(0).getLeaderColor();
					
					if (b.getTeamColor() == null || b.getTeamColor() != leaderColor) {
						int occupantSize = 0;
						
						for (Soldier s : occupants) {
							occupantSize += s.getValue();
						}
						/* The total number of soldiers must be greater than
						 * the defense value of the position in order to
						 * capture it */
						if (occupantSize > b.defenseValue) {
							b.setTeamColor(leaderColor);
						}
					}
				}
				
				// Apply reinforcements for claimed positions
				Soldier s = b.reinforce();
				
				if (s != null) {
					newMap.addSoldiers(s);
				}
				Color c =b.getTeamColor();
				if(c!=null){
						int ID =c.ordinal();
						teamScoreAdditions[ID]+=b.defenseValue;
				}
			}
		}
		newMap.updateTeamScores(teamScoreAdditions);
		return new Turn(this, currentTeam, newMap, failedTeams);
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
	 * Renders all the elements of the map, including the background for this
	 * turn.
	 * 
	 * @param g	The graphics object used for rendering
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
