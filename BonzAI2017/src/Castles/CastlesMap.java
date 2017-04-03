package Castles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import Castles.Objects.Building;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import Castles.api.SoldierState;
import Castles.api.TeamColor;
import Castles.util.graph.IDPair;
import Castles.util.graph.Node;
import Castles.util.graph.SegEdge;
import Castles.util.graph.Vertex;
import Castles.util.graph.CastlesMapGraph;
import bonzai.Position;
import bonzai.Team;

public class CastlesMap {
	
	// TODO 2017: Read in map files here. 
	private int width, height;
	
	private HashMap<String, String> fields;
	
	private CastlesMapGraph graph;
	private HashMap<IDPair, ArrayList<String>> pathIDsMap;
	private final HashMap<String, RallyPoint> graphElements;
	
	private boolean[] players;
	
	private ArrayList<Team> teams;
	private ArrayList<Soldier>[] soldiers;
	
	@SuppressWarnings("unchecked")
	public CastlesMap(HashMap<String, String> f, HashMap<String, RallyPoint> ge,
			ArrayList<Soldier> initialSoldiers, CastlesMapGraph g, ArrayList<Team> t,
			int w, int h) {
		
		width = w;
		height = h;
		
		fields = f;
		
		graphElements = ge;
		graph = g;
		
		pathIDsMap = g.generatePaths();
		
		players = new boolean[] { true,true,true,true,true,true };
		teams = t;
		
		soldiers = (ArrayList<Soldier>[])new ArrayList[6];
		for(int i=0;i<6;i++) {
			soldiers[i]=new ArrayList<Soldier>();
		}
		
		for (Soldier s : initialSoldiers) {
			addSoldiers(s);
		}
	}

	/**
	 * Copy constructor for a CastlesMap
	 * 
	 * @param previousTurn - the map of the previous turn to clone
	 */
	@SuppressWarnings("unchecked")
	public CastlesMap(CastlesMap previousTurn) {
		
		width = previousTurn.getWidth();
		height = previousTurn.getHeight();
		
		fields = previousTurn.getFields();
		
		//copy the list of teams
		players = previousTurn.players;
		teams = previousTurn.teams;
		
		soldiers = (ArrayList<Soldier>[])new ArrayList[6];
		for(int i=0;i<6;i++){
			soldiers[i]=new ArrayList<Soldier>();
		}
		
		graph = previousTurn.graph;
		pathIDsMap = previousTurn.pathIDsMap;
		
		// Copy the rally points, buildings, etc.
		graphElements = new HashMap<String, RallyPoint>();
		Collection<RallyPoint> rallyPoints = previousTurn.graphElements.values();
		
		for (RallyPoint r : rallyPoints) {
			RallyPoint rCopy = r.copy();
			graphElements.put(r.ID, rCopy);
			
			ArrayList<Soldier> occupants = r.getOccupants();
			// Add the soldiers to the list of soldier references
			for (Soldier s : occupants) {
				addSoldiers( s.copy() );
			}
		}
	}
	
	/**
	 * Compiles a list of positions, which are adjacent to the position with
	 * the given ID.
	 * 
	 * @param ID	The ID of some position on the map
	 * @return		A list of IDs of adjacent positions
	 */
	public ArrayList<String> getAdjPosIDs(String ID) {
		
		try {
			char first = ID.charAt(0);
			
			if (first == '!') {
				// The position is on an edge
				return graph.getEdge(ID).adjPositionIDs(ID);
				
			} else {
				// The position is on a vertex
				return graph.getVertex(ID).adjPositionIDs();
			}
			
			
		} catch (Exception Ex) {
			// Invalid ID
			return new ArrayList<String>();
		}
	}
	
	/**
	 * A list of IDs pertaining to the vertices that are adjacent to the vertex
	 * with the given ID. If no vertex has the given ID, then null is returned.
	 * 
	 * @param ID	The ID of a vertex on the map
	 * @return		The list of IDs of adjacent vertices
	 */
	public ArrayList<String> getAdjVertexIDs(String ID) {
		Vertex v = graph.getVertex(ID);
		
		if (v != null) {
			ArrayList<String> adjVIDs = new ArrayList<String>();
			List<Vertex> adjVertices = v.adjacentVertices();
			
			for (Vertex adjV : adjVertices) {
				adjVIDs.add(adjV.ID);
			}
			
			return adjVIDs;
		}
		
		return null;
	}
	
	/**
	 * Returns the position, with which the given ID is associated or null if
	 * no such position exists.
	 * 
	 * @param ID	The ID of the position to get
	 * @return		The position corresponding to the given ID
	 */
	public RallyPoint getPosition(String ID) {
		return graphElements.get(ID);
	}
	
	/**
	 * @return	A list of positions on the map
	 */
	public ArrayList<RallyPoint> getAllPositions() {
		return new ArrayList<RallyPoint>( graphElements.values() );
	}
	
	/**
	 * @return	A list of edges in the graph
	 */
	public ArrayList<SegEdge> edgeList() {
		return graph.edgeList();
	}
	
	/**
	 * Determines if the positions with the given IDs are adjacent based on the
	 * map's vertex and edge relations.
	 * 
	 * @param rID1	The ID of some position
	 * @param rID2	The ID of another position
	 * @return		If the positions of the given IDs are adjacent
	 */
	public boolean areAdjacent(String rID1, String rID2) {
		return graph.areAdjacent(rID1, rID2);
	}
	
	/**
	 * @return	The map's graph structure
	 */
	protected CastlesMapGraph getGraph() {
		return graph;
	}
	
	/**
	 * @param input
	 * @return
	 */
	public String getField(String input){
		if(input.equals("size")){
			return height + " " + width;
		}
		if(fields.get(input) == null){
			throw new IllegalArgumentException(input);
		}
		return fields.get(input);
	}
	
	protected void removePlayer(int i) {
		players[i]=false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	protected HashMap<String, String> getFields(){
		return fields;
	}
	
	/**
	 * Adds the values from the array to each respective team's score. This
	 * method does update the references of the teams as a result.
	 * 
	 * @param additions
	 */
	public void updateTeamScores(int[] additions) {
		for (int idx = 0; idx < teams.size(); ++idx) {
			Team oldRef = teams.get(idx);
			teams.set(idx, new Team(oldRef, additions[idx]));
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Team> getTeams() {
		return (List<Team>) teams.clone();
	}
	
	public ArrayList<Soldier>[] getSoldiers(){
		return soldiers;
	}
	
	/**
	 * Spawn a new soldier to the map
	 * 
	 * @param s the soldier to add
	 */
	public void addSoldiers(Soldier s) {
		
		if (s != null) {
			// Add to team soldiers list
			soldiers[s.getLeaderColor().ordinal()].add(s);
			// Add to the initial position
			try {
			getPosition(s.getPositionID()).addOccupant(s);
			} catch (Exception Ex) {
				if (Ex instanceof NullPointerException) {
					System.err.println(s.getPositionID());
					System.err.printf("PLIST: %s\n", graphElements.keySet());
				}
				
				throw Ex;
			}
		}
	}
	
	/**
	 * Removes a soldier from the map
	 * 
	 * @param s	The soldier group to remove from the map
	 */
	private void removeSoldiers(Soldier s) {
		
		/* PLEASR DAN, USE THIS METHOD! I BEG OF YOU!
		 *      - JOSHUA */
		
		if (s != null) {
			// Remove from the team soldiers list
			soldiers[s.getLeaderColor().ordinal()].remove(s);
			// Remove from the current position 
			getPosition(s.getPositionID()).removeOccupant(s);
		}
	}
	
	/**
	 * Moves all active soldiers one position across their designated path.
	 */
	public void moveSoldiers() {
		for (ArrayList<Soldier> soldierGroup : soldiers) {
			for (int j=0;j<soldierGroup.size();j++) {
				Soldier s=soldierGroup.get(j);
				String oldPosID = s.updatePositionID();
				
				if (oldPosID != null) {
					getPosition(oldPosID).removeOccupant(s);
					
					RallyPoint r = getPosition(s.getPositionID());
					r.addOccupant(s);
					
					/* Check any adjacent soldier groups to determine, if they
					 * would cross paths with the soldier group, which just
					 * moved and merge with soldier groups, which would pass
					 * this one. */
					int sdx2 = 0;
					while (sdx2 < r.getOccupants().size()) {
						Soldier neighbor = r.getOccupant(sdx2);
						
						if (s.getLeaderColor() != neighbor.getLeaderColor()) {
							ArrayList<String> path = neighbor.getPath();
							
							if (path != null && path.size() > 1 && path.get(1).equals(oldPosID)) {
								// The soldier groups cross paths
								byte ret = mergeSoldierPair(s, r.getOccupant(sdx2), r);
								
								if (ret != 1) {
									// The soldier group, which just moved was lost
									--j;
									break;
									
								} else if (ret != 2) {
									// The other soldier group was lost
									continue;
								}
							}
							
						}
						
						++sdx2;
					}
				}
			}
		}
	}
	
	/**
	 * Splits the passed soldiers into two groups
	 * 
	 * @param s the first solder
	 * @param num the number of soldier being split
	 * @param path the path the split is going on, with 0 the starting point
	 * @return the second soldier
	 */
	public Soldier splitSoliders(Soldier s, int num,ArrayList<String> path){
		if (s.getValue() == 0) {
			// This should never be true!
			return null;
		}
		
		ArrayList<String> newPath=new ArrayList<String>();
		
		if(path.get(0)!=s.getPositionID()){
			for(String t: path){
				newPath.add(t);
			}
		}
		else{
			newPath=path;
		}
		if(num > 0 && num < s.getValue()){
			Soldier split = new Soldier(s.getLeaderColor(), num, s.getPositionID());
			
			split.setState(SoldierState.MOVING);
			split.setPath(newPath);
			s.setValue(s.getValue()-num);
			
			addSoldiers(split);
			
			return split;
		}
		else if (num == s.getValue()) {
			// Redefine the path for the entire soldier group
			s.setPath(newPath);
			s.setState(SoldierState.MOVING);
		}
		return null;
	}
	
	/**
	 * Merges the soldier groups, which occupy the given position.
	 * 
	 * @param r	The position, at which to merge soldiers
	 * @return	The initial and final number of soldier groups on the position
	 * 			with respect to merging
	 */
	@SuppressWarnings("unchecked")
	public int[] mergeSoldiers(RallyPoint r) {
		ArrayList<Soldier> occupants = r.getOccupants();
		int initialOccupants = occupants.size();
		
		if (occupants.size() == 2) {
			mergeSoldierPair(r.getOccupant(0), r.getOccupant(1), r);
			
		} else if (occupants.size() > 2) {
			List<Team> teams = getTeams();
			ArrayList<Soldier>[] teamSoldierLists = (ArrayList<Soldier>[]) new ArrayList[teams.size()];
			int[] teamTotals = new int[teams.size()];
			TeamColor greatestTeam = null, sndGreatestTeam = null;
			
			// Initialize the data structures
			for (Team t : getTeams()) {
				teamSoldierLists[t.getColor().ordinal()] = new ArrayList<Soldier>();
				teamTotals[t.getColor().ordinal()] = 0;
				
				if (r instanceof Building) {
					Building b = (Building)r;
					
					if (b.getTeamColor() == t.getColor()) {
						/* Defending team gains a buff for occupying their
						 * building */
						teamTotals[t.getColor().ordinal()] = b.getDefVal();
					}
				}
			}
			
			int teamCount = 0;
			/* Separate soldiers by color and order the list of soldiers by
			 * size in ascending order */
			for (int sdx = 0; sdx < occupants.size(); ++sdx) {
				Soldier s = occupants.get(sdx);
				ArrayList<Soldier> soldierList = teamSoldierLists[s.getLeaderColor().ordinal()];
				int idx = 0;
				
				while ( idx < soldierList.size() && soldierList.get(idx).getValue() < s.getValue()) { ++idx; }
				
				soldierList.add(idx, s);
				teamTotals[s.getLeaderColor().ordinal()] += s.getValue();
				
				if (soldierList.size() == 1) {
					/* Keep track of the number of teams with soldiers on the
					 * given position */
					++teamCount;
				}
			}
			
			// Multiple teams must be present to initiate a merge
			if (teamCount >= 2) {
				// Find the greatest and second greatest team sizes
				for (Team t : teams) {
					int total = teamTotals[t.getColor().ordinal()];
					int greatestTotal = (greatestTeam == null) ? 0 : teamTotals[greatestTeam.ordinal()];
					int sndGreatestTotal = (sndGreatestTeam == null) ? 0 : teamTotals[sndGreatestTeam.ordinal()];
					
					if (total > greatestTotal) {
						if (greatestTotal > sndGreatestTotal) {
							/* Push the greatest team down to the second
							 * greatest */
							sndGreatestTeam = greatestTeam;
						}
						
						greatestTeam = t.getColor();
						
					} else if (total == greatestTotal) {
						/* If there are more than one greatest team, then
						 * everyone loses */
						greatestTeam = null;
						
					} else if (total > sndGreatestTotal) {
						sndGreatestTeam = t.getColor();
					}
				}
				
				// Update the soldiers occupying the space
				for (Team t : teams) {
					ArrayList<Soldier> soldierList = teamSoldierLists[t.getColor().ordinal()];
					
					if (soldierList.size() > 0) {
						if (t.getColor() == greatestTeam) {
							/* Remove enough soldiers to satisfy the second
							 * greatest team's total starting with the soldier
							 * groups with the greatest values */
							int counter = teamTotals[sndGreatestTeam.ordinal()];
							int idx = soldierList.size() - 1;
							
							if (r instanceof Building) {
								Building b = (Building)r;
								
								if (b.getTeamColor() == t.getColor()) {
									/* Defending team gains a buff for occupying
									 * their building */
									counter -= b.getDefVal();
								}
							}
							
							while (idx >= 0 && counter > 0) {
								Soldier s = soldierList.get(idx--);
								counter -= s.getValue();
								
								if (counter < 0) {
									s.setValue(-counter);
									
								} else {
									removeSoldiers(s);
								}
							}
							
						} else {
							// Remove all the soldiers of the teams, which lost
							for (Soldier s : soldierList) {
								removeSoldiers(s);
							}
						}
					}
				}
			}
			
			/* Merge ally soldier groups that are on the same path */
			if (occupants.size() > 2) {
				HashMap<String, ArrayList<Soldier>> destToGroupMap = new HashMap<String, ArrayList<Soldier>>();
				// Associate soldiers with their destination position
				for (Soldier s : occupants) {
					ArrayList<Soldier> groups = destToGroupMap.get(s.getDestID());
					
					if (groups == null) {
						groups = new ArrayList<Soldier>();
						destToGroupMap.put(s.getDestID(), groups);
					}
					
					groups.add(s);
				}
				// Merge all soldiers, whose destination positions are equal
				for (ArrayList<Soldier> groups : destToGroupMap.values()) {
					if (groups.size() > 1) {
						Soldier base = groups.get(0);
						
						for (int idx = 1; idx < groups.size(); ++idx) {
							base.setValue( base.getValue() + groups.get(idx).getValue() );
							removeSoldiers( groups.get(idx) );
						}
					}
				}
			}
		}
		
		/* Return a set containing the initial number of occupying soldier
		 * groups and the remaining soldier groups */
		return new int[] { initialOccupants, occupants.size() };
	}
	
	/**
	 * Merges the two soldier groups s1 and s2, if they are on different teams.
	 * 
	 * @param s1	A soldier group at position r
	 * @param s2	Another soldier group at position r
	 * @param r		The position containing s1 and s2
	 * @return		0 -> both s1 and s2 were lost
	 * 				1 -> s2 was lost
	 * 				2 -> s1 was lost
	 */
	private byte mergeSoldierPair(Soldier s1, Soldier s2, RallyPoint r) {
		byte ret = 0;
		// Multiple teams must be present for a merge
		if (s1.getLeaderColor() != s2.getLeaderColor()) {
			int s1DiffS2 = s1.getValue() - s2.getValue();
			
			if (r instanceof Building) {
				Building b = (Building)r;
				// Defending team gains a buff for occupying their building
				if (b.getTeamColor() == s1.getLeaderColor()) {
					s1DiffS2 = s1.getValue() - Math.max(0, s2.getValue() - b.getDefVal());
					
				} else if (b.getTeamColor() == s2.getLeaderColor()) {
					s1DiffS2 = Math.max(0, s1.getValue() - b.getDefVal()) - s2.getValue();
				}
			}
			
			/* Remove the soldiers for each team up the difference between
			 * the sizes of the soldier groups occupying the position. */
			
			if (s1DiffS2 <= 0) {
				removeSoldiers(s1);
				
				if (s1DiffS2 < 0) {
					ret |= 0x2;
					s2.setValue(-s1DiffS2);
				}
			}
			
			if (s1DiffS2 >= 0) {
				removeSoldiers(s2);
				
				if (s1DiffS2 > 0) {
					ret |= 0x1;
					s1.setValue(s1DiffS2);
				}
			}
			
		} else {
			// Merge soldiers, if destination positions are equal
			if (s1.getDestID().equals(s2.getDestID())) {
				s1.setValue( s1.getValue() + s2.getValue() );
				removeSoldiers(s2);
			}
		}
		
		return ret;
	}
	
	/**
	 * Handles merging of Soldiers, as well as battling for 2 entities, called by the other mergeSoldiers
	 * @param s1 Soldier 1, any team
	 * @param s2 Soldier 2, any team
	 * @return a value depending on the result:
	 * 			-1:s1 and s2 are not on the same position
	 * 			 0:s1 and s2 are not heading to the same end position, so no merge necessary
	 * 			 1:s1 and s2 have merged, s2 has been destroyed
	 * 			 2:s1 has defeated the the number of soldiers in s2, s2 has been deleted
	 * 			 3:s2 has defeated the the number of soldiers in s1, s2 has been deleted
	 * 			 4:s1 and s2 have both been deleted
	 */
	private int mergeSoldiers(Soldier s1, Soldier s2, RallyPoint r){
		if(!s1.getPositionID().equals(s2.getPositionID())){
			return -1;
		}
		if(s1.getLeaderColor().equals(s2.getLeaderColor())){
			ArrayList<String> s1Path = s1.getPath();
			ArrayList<String> s2Path = s2.getPath();
			
			if( s1.getState() == SoldierState.STANDBY || s1Path.get(s1Path.size() - 1).equals(s2Path.get(s2Path.size() - 1))) {
				s1.setValue(s1.getValue()+s2.getValue());
				soldiers[s2.getLeaderColor().ordinal()].remove(s2);
				r.onPoint.remove(s2);
				s2=null;
				return 1;
			}
			return 0;
		}
		else{
			if(r instanceof RallyPoint){
				int amount= s1.getValue()-s2.getValue();
				if(amount>0){
					s1.setValue(amount);
					soldiers[s2.getLeaderColor().ordinal()].remove(s2);
					r.onPoint.remove(s2);
					s2=null;
					return 2;
				}
				else if(amount<0){
					s2.setValue(amount);
					soldiers[s1.getLeaderColor().ordinal()].remove(s1);
					r.onPoint.remove(s1);
					s1=null;
					return 3;
				}
				else{
					soldiers[s1.getLeaderColor().ordinal()].remove(s1);
					r.onPoint.remove(s1);
					s1=null;
					soldiers[s2.getLeaderColor().ordinal()].remove(s2);
					r.onPoint.remove(s2);
					s2=null;
					return 4;
				}
			}
			else{
				int def=((Building)r).getDefVal();
				TeamColor team=((Building)r).getTeamColor();
				if(team==s1.getLeaderColor()){
					int amount= (s1.getValue()+def)-s2.getValue();
					if(amount>0){
						s1.setValue(amount);
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						r.onPoint.remove(s2);
						s2=null;
						return 2;
					}
					else if(amount<0){
						s2.setValue(amount);
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						r.onPoint.remove(s1);
						s1=null;
						return 3;
					}
					else{
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						r.onPoint.remove(s1);
						s1=null;
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						r.onPoint.remove(s2);
						s2=null;
						return 4;
					}
				}
				else{
					int amount= s1.getValue()-(s2.getValue()+def);
					if(amount>0){
						s1.setValue(amount);
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						r.onPoint.remove(s2);
						s2=null;
						return 2;
					}
					else if(amount<0){
						s2.setValue(amount);
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						r.onPoint.remove(s1);
						s1=null;
						return 3;
					}
					else{
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						r.onPoint.remove(s1);
						s1=null;
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						r.onPoint.remove(s2);
						s2=null;
						return 4;
					}
				}
			}
		}
		
	}
	
	/**
	 * Deals with merging soldiers on the same point
	 * @param onPoint the onpoint array for each node
	 * @return   a value depending on the result:
	 * 			-2:Error
	 * 			-1:s1 and s2 are not on the same position
	 * 			0 :no merge necessary
	 * 			1 :s1 and s2 have merged, s2 has been destroyed
	 * 			2 :s1 has defeated the the number of soldiers in s2, s2 has been deleted
	 * 			3 :s2 has defeated the the number of soldiers in s1, s2 has been deleted
	 * 			4 :s1 and s2 have both been deleted
	 * 			5 : Soldiers have merged successfully
	 */
	public int mergeSoldiers(ArrayList<Soldier> onPoint, RallyPoint r){
		if(onPoint.size()<=1){
			return 0;
		}
		if(onPoint.size()==2){
			int temp =mergeSoldiers(onPoint.get(0),onPoint.get(1),r);
			for(int i =0;i<onPoint.size();i++){
				if(onPoint.get(i)==null){
					onPoint.remove(i);
				}
			}
			return temp;
		}
		
		if(r instanceof RallyPoint){
			int num[]=new int[6];
			int total[]=new int[6];
			for(Soldier s: onPoint){
				num[s.getLeaderColor().ordinal()]++;
				total[s.getLeaderColor().ordinal()]+= s.getValue();
			}
			int max=0;
			int maxid=-1;
			for(int i=0;i<6;i++){
				if(total[i]>max){
					maxid=i;
					max=total[i];
				}
			}
			if(maxid==-1){
				return -2;
			}
			for (Soldier s: onPoint) {
				if(s.getLeaderColor().ordinal()!=maxid){
					onPoint.remove(s);
					soldiers[s.getLeaderColor().ordinal()].remove(s);
					s=null;
				}
			}
			max=0;
			for(int i=0;i<6;i++){
				if(i!=maxid){
					max+=total[i];
				}
			}
			Soldier.quickSort(onPoint);
			while(max>0){
				max-=onPoint.get(0).getValue();
				if(max>0){
					Soldier temp=onPoint.remove(0);
					soldiers[temp.getLeaderColor().ordinal()].remove(temp);
				}
			}
			return 5;
		}
		else{
			int def=((Building)r).getDefVal();
			int teamID=((Building)r).getTeamColor().ordinal();
			int num[]=new int[6];
			int total[]=new int[6];
			for(Soldier s: onPoint){
				num[s.getLeaderColor().ordinal()]++;
				total[s.getLeaderColor().ordinal()]+= s.getValue();
			}
			total[teamID]+=def;
			int max=0;
			int maxid=-1;
			for(int i=0;i<6;i++){
				if(total[i]>max){
					maxid=i;
					max=total[i];
				}
			}
			if(maxid==-1){
				return -2;
			}
			for(Soldier s: onPoint){
				if(s.getLeaderColor().ordinal()!=maxid){
					onPoint.remove(s);
					soldiers[s.getLeaderColor().ordinal()].remove(s);
					s=null;
				}
			}
			max=0;
			for(int i=0;i<6;i++){
				if(i!=maxid){
					max+=total[i];
				}
			}
			Soldier.quickSort(onPoint);
			while(max>0){
				max-=onPoint.get(0).getValue();
				if(max>0){
					Soldier temp=onPoint.remove(0);
					soldiers[temp.getLeaderColor().ordinal()].remove(temp);
				}
			}
			return 5;
		}
	}
	
	/**
	 * 
	 * @param A: a Team
	 * @return	All Soldiers in team A
	 */
	public ArrayList<Soldier> getSoldiers(Team A){
		ArrayList<Soldier> temp = new ArrayList<Soldier>();
		for(Soldier s: soldiers[A.getID()]){
			temp.add(s);
		}
		return temp;
	}
	
	/**
	 * 
	 * @param From: The ID of where the soldiers is coming from
	 * @param To:	the ID of where the soldiers is going to
	 * @return	the path of RallyPoint IDs
	 * @throws NullPointerException	If either From or To are invalid IDs
	 */
	public ArrayList<String> getPath(String From, String To) throws NullPointerException {
		Vertex from = graph.getVertex(From),
			   to = graph.getVertex(To);
		
		if (from != null && to != null) {
			// Both IDs refer to vertices
			return CastlesMapGraph.getPath(pathIDsMap, from, to);
		}
		
		String wpID;
		Vertex v;
		ArrayList<String> path, otherWayPoints = null;
		
		if (from == null) {
			// From refers to a waypoint
			wpID = From;
			
			if (to == null) {
				// To refers to a waypointalso
				otherWayPoints = new ArrayList<String>();
				SegEdge firstE = graph.getEdge(wpID),
						secondE = graph.getEdge(To);
				ArrayList<String> firstPath = CastlesMapGraph.getPath(pathIDsMap, firstE.first, secondE.first),
								  secondPath = CastlesMapGraph.getPath(pathIDsMap, firstE.first, secondE.second);
				int endIdx = secondE.indexOf(To);
				
				/* Keep track of the waypoints on the second edge, which are
				 * part of the path */
				if (firstPath.size() <= secondPath.size()) {
					v = secondE.first;
					
					for (int idx = 0; idx <= endIdx && idx < secondE.getWeight(); ++idx) {
						otherWayPoints.add( String.format("%s:%d", secondE.ID, idx + 1) );
					}
					
				} else {
					v = secondE.second;
					
					for (int idx = secondE.getWeight() - 1; idx >= endIdx && idx >= 0; --idx) {
						otherWayPoints.add( String.format("%s:%d", secondE.ID, idx + 1) );
					}
					
				}
				
			} else {
				v = to;
			}
			
		} else {
			// To refers to a waypoint
			wpID = To;
			v = from;
		}
		
		/* Find the path between vertex, v, to the edge containing the waypoint
		 * with ID, wpID */
		path = getPathToEdge(graph.getEdge(wpID), wpID, v, from == null);
		
		if (otherWayPoints != null) {
			/* Add the waypoints from the second edge to the path */
			for (String ID : otherWayPoints) {
				path.add(ID);
			}
		}
		
		return path;
	}
	
	/**
	 * Returns the list of position IDs, which connect the waypoint of the given
	 * edge and ID to the given vertex. If the parameter edgeToVertex is true,
	 * then the first position will be the ID of the waypoint, otherwise the
	 * first ID will be that of the vertex.
	 * 
	 * @param e				The given edge
	 * @param wpID			The ID of the target waypoint on the given edge
	 * @param v				The given vertex
	 * @param edgeToVertex	Determines the order of the posiiton IDs in the path
	 * @return				An ordered list of position IDs
	 */
	private ArrayList<String> getPathToEdge(SegEdge e, String wpID, Vertex v,
			boolean edgeToVertex) {
		
		ArrayList<String> path, firstPath, secondPath;
		int idx, endIdx, incVal;
		
		if (edgeToVertex) {
			// The path goes from the edge to the vertex
			firstPath = CastlesMapGraph.getPath(pathIDsMap, e.first, v);
			secondPath = CastlesMapGraph.getPath(pathIDsMap, e.second, v);
			
		} else {
			// The path goes from the vertex to the edge
			firstPath = CastlesMapGraph.getPath(pathIDsMap, v, e.first);
			secondPath = CastlesMapGraph.getPath(pathIDsMap, v, e.second);
		}
		
		// Which vertex is on the path?
		if (firstPath.size() <= secondPath.size()) {
			idx = 0;
			endIdx = e.indexOf(wpID);
			incVal = 1;
			path = firstPath;
			
		} else {
			idx = e.getWeight() - 1;
			endIdx = e.indexOf(wpID);
			incVal = -1;
			path = secondPath;
		}
		
		if (path.size() == 0) {
			// The vertex is adjacent to the edge
			path.add(v.ID);
		}
		
		/* Add the edge waypoints to the path, which connect the target waypoint
		 * to the vertex path */
		for (; idx >= 0 && idx != endIdx && idx < e.getWeight(); idx += incVal) {
			
			if (edgeToVertex) {
				path.add(0, String.format("%s:%d", e.ID, idx + 1));
				
			} else {
				path.add(String.format("%s:%d", e.ID, idx + 1));
			}
		}
		
		// Add the target waypoint to the path
		if (edgeToVertex) {
			path.add(0, String.format("%s:%d", e.ID, idx + 1));
			
		} else {
			path.add(String.format("%s:%d", e.ID, idx + 1));
		}
		
		return path;
	}
}
