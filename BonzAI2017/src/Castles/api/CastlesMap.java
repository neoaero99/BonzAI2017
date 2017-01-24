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

	int width, height;
	
	private HashMap<String, String> fields;
	
	private WeightedGraph<RallyPoint,Integer> graph;
	private GraphPathSet<RallyPoint> paths;
	
	private boolean[] players;
	
	private ArrayList<Team> teams;
	private ArrayList<Soldier>[] soldiers;
	
	public CastlesMap(HashMap<String, String> f, WeightedGraph<RallyPoint, Integer> g, ArrayList<Team> t, int w, int h) {
		width = w;
		height = h;
		
		fields = f;
		
		graph = g;
		paths = new GraphPathSet<RallyPoint>(g);
		
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
		graph = cloneGraph( previousTurn.getGraph() );
		paths = new GraphPathSet<RallyPoint>(graph);
		
		//copy the list of teams
		players = previousTurn.getPlayers();
		teams = (ArrayList<Team>) previousTurn.getTeams();
		
		soldiers = previousTurn.getSoldiers();
	}
	
	/**
	 * Duplicates all the vertices and edges, the elements of each, and the connections between nodes
	 * and vertices.
	 * 
	 * @param g	A graph object
	 * @return	A copy of the given graph
	 */
	public static WeightedGraph<RallyPoint, Integer> cloneGraph(WeightedGraph<RallyPoint, Integer> g) {
		
		if (g == null) {
			return null;
		}
		
		DualLinkList<Vertex<RallyPoint, Integer>> vertexCopies = new DualLinkList<Vertex<RallyPoint, Integer>>();
		DualLinkList<WeightedEdge<RallyPoint, Integer>> edgeCopies = new DualLinkList<WeightedEdge<RallyPoint, Integer>>();
		
		HashMap<Integer, Vertex<RallyPoint, Integer>> OldToNewVertex = new HashMap<Integer, Vertex<RallyPoint, Integer>>();
		
		// Copy the vertices and add them to a map
		DualLinkList<Vertex<RallyPoint, Integer>> vertices = g.vertexList();
		for (Vertex<RallyPoint, Integer> v : vertices) {
			Vertex<RallyPoint, Integer> vertexCopy = new Vertex<RallyPoint, Integer>(v.getElement().copy());
			// TODO update Soldier path references
			vertexCopies.addToBack(vertexCopy);
			OldToNewVertex.put(v.hashCode(), vertexCopy);
		}
		
		// Copy the edges and the connections between edges and vertices
		DualLinkList<WeightedEdge<RallyPoint, Integer>> edges = g.edgeList();
		for (WeightedEdge<RallyPoint, Integer> e : edges) {
			WeightedEdge<RallyPoint, Integer> edgeCopy = new WeightedEdge<RallyPoint, Integer>(e.getElement().intValue());
			edgeCopies.addToBack(edgeCopy);
			
			// Connect the new vertices with the new edge
			Vertex<RallyPoint, Integer> first = OldToNewVertex.get( e.getFirst().hashCode() );
			Vertex<RallyPoint, Integer> second = OldToNewVertex.get( e.getSecond().hashCode() );
			WeightedGraph.connect(first, second, edgeCopy);
		}
		
		return new WeightedGraph<RallyPoint, Integer>(vertexCopies, edgeCopies);
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
		DualLinkList<Vertex<RallyPoint, Integer>> vertexList = graph.vertexList();
		
		for (Vertex<RallyPoint, Integer> v : vertexList) {
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
	public void addSoldiers(Soldier s){
		soldiers[s.leader.getID()].add(s);
	}
	/**
	 * Splits the passed soldiers into two groups
	 * @param s the first solder
	 * @param num the number of soldier being split
	 * @param path the path the split is going on
	 * @return the second soldier
	 */
	public Soldier splitSoliders(Soldier s, int num,DualLinkList<WeightedEdge<RallyPoint, Integer>> path){
		if(num<s.value){
			Soldier split=new Soldier(s.position);
			split.leader=s.leader;
			split.state=SoldierState.MOVING;
			split.given_path=path;
			split.value=num;
			s.value=s.value-num;
			addSoldiers(split);
		return split;
		}
		return null;
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
