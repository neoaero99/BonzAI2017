package Castles.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Castles.Objects.Building;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import Castles.Objects.SoldierState;
import Castles.util.graph.IDPair;
import Castles.util.graph.Vertex;
import Castles.util.graph.SegEdge;
import Castles.util.graph.CastlesMapGraph;
import Castles.util.linkedlist.DualLinkList;
import bonzai.Position;
import bonzai.Team;

public class CastlesMap {
	
	// TODO 2017: Read in map files here. 

	private int width, height;
	
	private HashMap<String, String> fields;
	
	private CastlesMapGraph graph;
	private static HashMap<IDPair, ArrayList<String>> pathIDsMap;
	
	private boolean[] players;
	
	private ArrayList<Team> teams;
	private ArrayList<Soldier>[] soldiers;
	
	static {
		pathIDsMap = null;
	}
	
	public CastlesMap(HashMap<String, String> f, CastlesMapGraph g, ArrayList<Team> t, int w, int h) {
		width = w;
		height = h;
		
		fields = f;
		
		graph = g;
		pathIDsMap = g.generatePaths();
		
		players = new boolean[] { true,true,true,true,true,true };
		teams = t;
		
		soldiers = (ArrayList<Soldier>[])new ArrayList[6];
		for(int i=0;i<6;i++){
			soldiers[i]=new ArrayList<Soldier>();
		}
	}

	/**
	 * Copy constructor
	 * 
	 * I WANT A WAY AROUND THIS
	 * Moves on to the next turn
	 * 
	 * @param previousTurn - the map of the previous turn to clone
	 * @return 
	 */
	public CastlesMap(CastlesMap previousTurn) {
		
		height = previousTurn.getHeight();
		width = previousTurn.getWidth();
		
		//set fields
		fields = previousTurn.getFields();
	
		//define new variables
		graph = previousTurn.getGraph().clone();
		
		//copy the list of teams
		players = previousTurn.getPlayers();
		teams = (ArrayList<Team>) previousTurn.getTeams();
		
		soldiers = previousTurn.getSoldiers();
	}
	


//github.com/neoaero99/BonzAI2017
	
	
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
	
	public void removePlayer(int i) {
		players[i]=false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Position getEntity(int i) {
		if(!players[i]){
			return null;
		}
		Castles.api.Color c=Castles.api.Color.values()[i];
		ArrayList<Vertex> list = graph.vertexList();
		for(Vertex v:list){
			if(v.getElement() instanceof Building ){
				if(((Building)v.getElement()).getColor()==c){
					return v.getElement().getPosition();
				}
			}
		}
		return null;
	}
	
	/**
	 * returns an entity based on a unique id
	 * @param s
	 * @return
	 */
	public RallyPoint getEntity(String s){
		for (Vertex v : graph.vertexList()){
			if(v.getElement().ID.equals(s)){
				return v.getElement();
			}
		}
		return null;
	}
	
	public CastlesMapGraph getGraph() {
		return graph;
	}
	/**
	 * 
	 * @return the players
	 */
	public boolean[] getPlayers(){
		return players;
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	protected HashMap<String, String> getFields(){
		return fields;
	}
	
	public DualLinkList<RallyPoint> getAllNodes() {
		DualLinkList<RallyPoint> nodes = new DualLinkList<RallyPoint>();
		ArrayList<Vertex> vertexList = graph.vertexList();
		
		for (Vertex v : vertexList) {
			// Pull all the elements from all the vertices in the graph
			nodes.addToBack(v.getElement());
		}
		
		return nodes;
	}
	
	protected ArrayList<Soldier>[] getSoldiers(){
		return soldiers;
	}
	
	/**
	 * Spawn a new soldier to the map
	 * @param s the soldier to add
	 */
	protected void addSoldiers(Soldier s){
		soldiers[s.getLeader().getID()].add(s);
	}
	
	/**
	 * Splits the passed soldiers into two groups
	 * @param s the first solder
	 * @param num the number of soldier being split
	 * @param path the path the split is going on
	 * @return the second soldier
	 */
	public Soldier splitSoliders(Soldier s, int num,ArrayList<String> path){
		if(num<s.getValue()){
			Soldier split = new Soldier(s.getLeader(), num, s.getPosition());
			split.setState(SoldierState.MOVING);
			split.setPath(path);
			split.setValue(num);
			s.setValue(s.getValue()-num);
			addSoldiers(split);
		return split;
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
	private int mergeSoldiers(Soldier s1, Soldier s2){
		if(!s1.getPosition().equals(s2.getPosition())){
			return -1;
		}
		if(s1.getLeader().equals(s2.getLeader())){
			ArrayList<String> s1Path = s1.getPath();
			ArrayList<String> s2Path = s2.getPath();
			
			if( s1.getState() == SoldierState.STANDBY || s1Path.get(s1Path.size() - 1).equals(s2Path.get(s2Path.size() - 1))) {
				s1.setValue(s1.getValue()+s2.getValue());
				soldiers[s2.getLeader().getID()].remove(s2);
				s2=null;
				return 1;
			}
			return 0;
		}
		else{
			int amount= s1.getValue()-s2.getValue();
			if(amount>0){
				s1.setValue(amount);
				soldiers[s2.getLeader().getID()].remove(s2);
				s2=null;
				return 2;
			}
			else if(amount<0){
				s2.setValue(amount);
				soldiers[s1.getLeader().getID()].remove(s1);
				s1=null;
				return 3;
			}
			else{
				soldiers[s1.getLeader().getID()].remove(s1);
				s1=null;
				soldiers[s2.getLeader().getID()].remove(s2);
				s2=null;
				return 4;
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
	public int mergeSoldiers(ArrayList<Soldier> onPoint){
		if(onPoint.size()<=1){
			return 0;
		}
		if(onPoint.size()==2){
			int temp =mergeSoldiers(onPoint.get(0),onPoint.get(1));
			for(int i =0;i<2;i++){
				if(onPoint.get(i)==null){
					onPoint.remove(i);
				}
			}
			return temp;
		}
		int num[]=new int[6];
		int total[]=new int[6];
		for(Soldier s: onPoint){
			num[s.getLeader().getID()]++;
			total[s.getLeader().getID()]+= s.getValue();
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
			if(s.getLeader().getID()!=maxid){
				onPoint.remove(s);
				soldiers[s.getLeader().getID()].remove(s);
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
				soldiers[temp.getLeader().getID()].remove(temp);
			}
		}
		return 5;
	}
}
