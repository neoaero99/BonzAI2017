package Castles.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import Castles.Objects.Building;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import Castles.Objects.SoldierState;
import Castles.util.graph.IDPair;
import Castles.util.graph.SegEdge;
import Castles.util.graph.Vertex;
import Castles.util.graph.CastlesMapGraph;
import bonzai.Position;
import bonzai.Team;

public class CastlesMap {
	
	// TODO 2017: Read in map files here. 

	private static int width, height;
	
	private static HashMap<String, String> fields;
	
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
	 * 
	 * 
	 * @param i
	 * @return
	 */
	public Position getEntity(int i) {
		if(!players[i]){
			return null;
		}
		
		Castles.api.Color c=Castles.api.Color.values()[i];
		ArrayList<RallyPoint> elementList = getAllPositions();
		
		for(RallyPoint r : elementList) {
			if(r instanceof Building ){
				if(((Building)r).getTeamColor()==c){
					return r.getPosition().clone();
				}
			}
		}
		return null;
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
	protected void updateTeamScores(int[] additions) {
		for (int idx = 0; idx < teams.size(); ++idx) {
			Team oldRef = teams.get(idx);
			teams.set(idx, new Team(oldRef, oldRef.getScore() + additions[idx]));
		}
	}
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public ArrayList<Soldier>[] getSoldiers(){
		return soldiers;
	}
	
	/**
	 * Spawn a new soldier to the map
	 * 
	 * @param s the soldier to add
	 */
	protected void addSoldiers(Soldier s) {
		if (s != null) {
			// Add to team soldiers list
			soldiers[s.getLeaderColor().ordinal()].add(s);
			// Add to the initial position
			getPosition(s.getPositionID()).addOccupant(s);
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
	protected void moveSoldiers() {
		for (ArrayList<Soldier> soldierGroup : soldiers) {
			for (Soldier s : soldierGroup) {
				String oldPosID = s.updatePositionID();
				
				if (oldPosID != null) {
					getPosition(oldPosID).removeOccupant(s);
					getPosition(s.getPositionID()).addOccupant(s);
				}
			}
		}
	}
	
	/**
	 * Splits the passed soldiers into two groups
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
					s2=null;
					return 2;
				}
				else if(amount<0){
					s2.setValue(amount);
					soldiers[s1.getLeaderColor().ordinal()].remove(s1);
					s1=null;
					return 3;
				}
				else{
					soldiers[s1.getLeaderColor().ordinal()].remove(s1);
					s1=null;
					soldiers[s2.getLeaderColor().ordinal()].remove(s2);
					s2=null;
					return 4;
				}
			}
			else{
				int def=((Building)r).defenseValue;
				Color team=((Building)r).getTeamColor();
				if(team==s1.getLeaderColor()){
					int amount= (s1.getValue()+def)-s2.getValue();
					if(amount>0){
						s1.setValue(amount);
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						s2=null;
						return 2;
					}
					else if(amount<0){
						s2.setValue(amount);
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						s1=null;
						return 3;
					}
					else{
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						s1=null;
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						s2=null;
						return 4;
					}
				}
				else{
					int amount= s1.getValue()-(s2.getValue()+def);
					if(amount>0){
						s1.setValue(amount);
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
						s2=null;
						return 2;
					}
					else if(amount<0){
						s2.setValue(amount);
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						s1=null;
						return 3;
					}
					else{
						soldiers[s1.getLeaderColor().ordinal()].remove(s1);
						s1=null;
						soldiers[s2.getLeaderColor().ordinal()].remove(s2);
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
			for(int i =0;i<2;i++){
				if(onPoint.get(i)==null){
					onPoint.remove(i);
				}
			}
			return temp;
		}
		/* TODO This will not work
		 * Since the Building class extends RallyPoint, "r instanceof RallyPoint"
		 * will return true for all Building objects */
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
		else{
			int def=((Building)r).defenseValue;
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
	 */
	public ArrayList<String> getPath(String From, String To) {
		Vertex f=graph.getVertex(From);
		Vertex t=graph.getVertex(To);
		return CastlesMapGraph.getPath(pathIDsMap, f, t);
	}
}
