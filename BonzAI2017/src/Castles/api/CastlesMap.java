package Castles.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Castles.Objects.Building;
import Castles.Objects.Castle;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import Castles.Objects.SoldierState;
import Castles.Objects.Target;
import Castles.Objects.Traversable;
import Castles.Objects.Village;
import Castles.Objects.Wall;
import Castles.util.graph.GraphPathSet;
import Castles.util.graph.Vertex;
import Castles.util.graph.WeightedEdge;
import Castles.util.graph.WeightedGraph;
import Castles.util.linkedlist.DualLinkList;
import bonzai.Position;
import bonzai.Team;

public class CastlesMap {
	
	// TODO 2017: Read in map files here. 

	private int max_repeat_id = 0, max_target_id = 0, max_source_id = 0;
	private int min, mid;
	int width,height;
	
	private HashMap<String, String> fields = new HashMap<String, String>();
	private HashMap<Integer, Traversable> entities = new HashMap<>();
	
	private WeightedGraph<RallyPoint,Integer> graph = new WeightedGraph<>();
	private GraphPathSet<RallyPoint> paths = new GraphPathSet<>(graph);
	
	private boolean players[]={true,true,true,true,true,true};
	
	private int numTeams;
	
	private ArrayList<Team> teams= new ArrayList<Team>();
	private ArrayList<Soldier> soldiers[]=(ArrayList<Soldier>[])new ArrayList[6];
	
	public CastlesMap(){
		graph=new WeightedGraph<>();
		paths= new GraphPathSet<>(graph);
		for(int i=0;i<6;i++){
			soldiers[i]=new ArrayList<Soldier>();
		}
		numTeams=0;
	}

	public CastlesMap(CastlesMap previousTurn) {
		if(previousTurn == null){
			graph=new WeightedGraph<>();
			paths= new GraphPathSet<>(graph);
			numTeams=0;
		}else{
			//define new variables
			graph=new WeightedGraph<>();
			paths= new GraphPathSet<>(graph);
			numTeams=0;
			//copy the list of teams
			teams=(ArrayList<Team>) previousTurn.getTeams();
			//check if the previous turn is returning a null map
			if(previousTurn.getGraph() == null) throw new NullPointerException();
			//copy vertices
			DualLinkList<Vertex<RallyPoint, Integer>> list=previousTurn.getGraph().vertexList();
			if(list.size() == 0) throw new NullPointerException();
			for(Vertex<RallyPoint, Integer> v:list){
				Vertex<RallyPoint,Integer> newVert=new Vertex<RallyPoint, Integer>(v.getElement().copy());
				graph.addNode(newVert);
			}
			
			//copy edges
			DualLinkList<WeightedEdge<RallyPoint,Integer>> list2=previousTurn.getGraph().edgeList();
			if(list2.size() == 0) throw new NullPointerException();
			for(WeightedEdge<RallyPoint,Integer> w:list2){
				if(w == null || w.getFirst() == null || w.getElement() == null){
					System.err.println("Null edge");
					break;
				}
				int weight=w.getElement();
				WeightedEdge<RallyPoint,Integer> nw = new WeightedEdge<>(weight);
				nw.setFirst(new Vertex<RallyPoint,Integer>(copy(w.getFirst().getElement())));
				nw.setSecond(new Vertex<RallyPoint, Integer>(copy(w.getSecond().getElement())));
				graph.addEdge(nw);
				
			}
			
			//set fields
			fields = previousTurn.getFields();
			players=previousTurn.getPlayers();
			height=previousTurn.getHeight();
			width=previousTurn.getWidth();
			paths=previousTurn.getPaths();
		}
		paths=previousTurn.getPaths();
		soldiers=previousTurn.getSoldiers();
	}

	/**
	 * Copy constructor
	 * 
	 * I WANT A WAY AROUND THIS
	 * Moves on to the next turn
	 * 
	 * @param previousTurn - the map of the previous turn to clone
	 * @param decCooldown - whether or not to decrement Repeater cooldowns
	 * @return 
	 */
	protected CastlesMap(CastlesMap previousTurn, boolean decCooldown) {
		
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

	public void addTarget(Target target) {
		
	}

	public void addWall(Wall wall) {
		
	}

	public void setField(String string, String string2) {
		if(!string.equals("size")){
			fields.put(string, string2);
			System.out.println(string  + " set to " + fields.get(string));
		} else {
			String[] parts = string2.split(", ");
			width = Integer.parseInt(parts[0]);
			height = Integer.parseInt(parts[1]);
		}
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
		DualLinkList<Vertex<RallyPoint, Integer>> list=graph.vertexList();
		for(Vertex<RallyPoint, Integer> v:list){
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
		for(Vertex<RallyPoint, Integer> v : graph.vertexList()){
			if(v.getElement().getName().equals(s)){
				return v.getElement();
			}
		}
		return null;
	}
	
	/**
	 * returns the vertex containing an entity based on a unique id
	 * the I param is useless, so set it to whatever you want
	 * @param s the unique id of the entitiy
	 * @param i useless
	 * @return
	 */
	private Vertex<RallyPoint, Integer> getNode(String s){
		for(Vertex<RallyPoint, Integer> v : graph.vertexList()){
			if(v.getElement().getName().equals(s)){
				return v;
			}
		}
		return null;
	}
	
	public void addPlayer(int x, int y, String name){
		Team newTeam=new Team(Castles.api.Color.values()[numTeams],numTeams);
		Castle temp=new Castle(x,y,name,newTeam);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
		numTeams++;
		teams.add(newTeam);
	}
	
	public void addCastle(int x, int y, String name){
		Castle temp=new Castle(x,y,name,null);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
	}
	public void addVillage(int x, int y, String name){
		Village temp=new Village(x,y,name,null);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
	}
	
	public void addRally(int x, int y, String name){
		RallyPoint temp=new RallyPoint(x,y,name);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
	}
	/**
	 * POTENTIAL ISSUE:
	 *     ALL points must be loaded in prior to calling this method
	 * @param n1
	 * @param n2
	 * @param weight
	 */
	public void connect(String n1, String n2, int weight){
		Vertex<RallyPoint, Integer> one= getNode(n1);
		Vertex<RallyPoint, Integer> two= getNode(n2);
		WeightedEdge<RallyPoint, Integer> temp=new WeightedEdge<RallyPoint, Integer>(weight);
		temp.setFirst(one);
		temp.setSecond(two);
		graph.addEdge(temp);

	}
	
	/*
	 * change to canPassThroug(), makes a list
	 * of nodes and edges that troops CAN pass through
	 */
	public GraphPathSet<RallyPoint> getPaths(){
		return paths;
	}
	public WeightedGraph<RallyPoint,Integer> getGraph(){
		if(graph == null) throw new NullPointerException("I lost my map");
		return graph;
	}
	public boolean[] getPlayers(){
		return players;
	}
	
	public DualLinkList<Position> getRallyPointsPositions(){
		DualLinkList<Position> pos= new DualLinkList<Position>();
		for(Vertex<RallyPoint, Integer> r: getGraph().vertexList()){
			if(!(r.getElement()instanceof Building)){
				pos.addToFront(r.getElement().getPosition());
			}
		}
		return pos;
	}
	public DualLinkList<Building> getDefaultss(){
		DualLinkList<Building> pos= new DualLinkList<Building>();
		for(Vertex<RallyPoint, Integer> r: getGraph().vertexList()){
			if(r.getElement()instanceof Building&&!(r.getElement()instanceof Castle)){
				pos.addToFront((Building)r.getElement());
			}
		}
		return pos;
	}
	public DualLinkList<Building> getCastles(){
		DualLinkList<Building> pos= new DualLinkList<Building>();
		for(Vertex<RallyPoint, Integer> r: getGraph().vertexList()){
			if(r.getElement()instanceof Castle){
				pos.addToFront((Building)r.getElement());
			}
		}
		return pos;
	}
	
	/**
	 * Takes in 2 object unique identifiers and checks if they are in the
	 * graph and are adjacent
	 * 
	 * @param o1 an object
	 * @param o2 an object
	 * @return true if o1 and o2 are in the graph and have an edge
	 * 			connecting them
	 */
	public boolean isAdjecent(String o1, String o2){
		Vertex<RallyPoint, Integer> p1 = getNode(o1);
		Vertex<RallyPoint, Integer> p2 = getNode(o2);
		if(p1 == null || p2 == null){
			return false;
		}
		return p1.isAdjacent(p2);
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	protected HashMap<String, String> getFields(){
		return fields;
	}
	
	public DualLinkList<RallyPoint> getAllNodes() {
		DualLinkList<RallyPoint> nodes = new DualLinkList<RallyPoint>();
		DualLinkList<Vertex<RallyPoint, Integer>> vertexList = graph.vertexList();
		
		for (Vertex<RallyPoint, Integer> v : vertexList) {
			// Pull all the elements from all the vertices in the graph
			nodes.addToBack(v.getElement());
		}
		
		return nodes;
	}
	
	/**
	 * prints out a copy of the given rally point
	 */
	private RallyPoint copy(RallyPoint r){
		if(r instanceof Castle){
			Castle c = (Castle)r;
			Castle temp = new Castle(r.getPosition().getX(), r.getPosition().getY(), r.getName(),  c.getTeam());
			return temp;
		}
		if(r instanceof Village){
			Village temp = new Village(r.getPosition().getX(), r.getPosition().getY(), r.getName(), ((Village) r).getTeam());
			return temp;
		}
		RallyPoint temp = new RallyPoint(r.getPosition().getX(), r.getPosition().getY(), r.getName());
		return temp;
	}
	
	protected ArrayList<Soldier>[] getSoldiers(){
		return soldiers;
	}
	public void addSoldiers(Soldier s){
		soldiers[s.leader.getID()].add(s);
	}
	public Soldier splitSoliders(Soldier s, int num,DualLinkList<WeightedEdge<RallyPoint, Integer>> path){
		Soldier split=new Soldier(s.position);
		split.leader=s.leader;
		split.state=SoldierState.MOVING;
		split.given_path=path;
		split.value=num;
		s.value=s.value-num;
		addSoldiers(split);
		return split;
		
	}
	/**
	 * Handles merging of Soldiers, as well as battling
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
	public int mergeSoldiers(Soldier s1, Soldier s2){
		if(!s1.position.equals(s2.position)){
			return -1;
		}
		if(s1.leader.equals(s2.leader)){
			if(s1.state==SoldierState.STANDBY||s1.given_path.Tail.getPrevious().equals(s2.given_path.Tail.getPrevious())){ //We need to figure out when the soldiers should merge
				s1.value=s1.value+s2.value;
				soldiers[s2.leader.getID()].remove(s2);
				s2=null;
				return 1;
			}
			return 0;
		}
		else{
			int amount=s1.value-s2.value;
			if(amount>0){
				s1.value=amount;
				soldiers[s2.leader.getID()].remove(s2);
				s2=null;
				return 2;
			}
			else if(amount<0){
				s2.value=amount;
				soldiers[s1.leader.getID()].remove(s1);
				s1=null;
				return 3;
			}
			else{
				soldiers[s1.leader.getID()].remove(s1);
				s1=null;
				soldiers[s2.leader.getID()].remove(s2);
				s2=null;
				return 4;
			}
		}
		
	}
}
