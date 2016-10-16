package Castles.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Castles.Objects.*;
import Castles.util.*;
import Castles.util.graph.*;
import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Team;
import bonzai.util.Predicate;
import bonzai.util.Utility;

public class CastlesMap {
	
	// TODO 2017: Read in map files here. 

	private int max_repeat_id = 0, max_target_id = 0, max_source_id = 0;
	private int min, mid;
	int width,height;
	
	private HashMap<String, String> fields = new HashMap<>();
	private HashMap<Integer, Traversable> entities = new HashMap<>();
	
	private WeightedGraph<RallyPoint,Integer> graph=new WeightedGraph<>();
	private GraphPathSet<RallyPoint> paths= new GraphPathSet<>(graph);
	
	boolean players[]={true,true,true,true,true,true};
	
	private int x;
	
	public CastlesMap(){
		x=0;
	}

	public CastlesMap(CastlesMap previousTurn) {
		this(previousTurn, true);
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
	
	
	/**
	 * @param input
	 * @return
	 */
	public String getField(String input){
		return fields.get(input);
	}

	public void addTarget(Target target) {
		// TODO Auto-generated method stub
		
	}

	public void addWall(Wall wall) {
		// TODO Auto-generated method stub
		
	}

	public void setField(String string, String string2) {
		if(!string.equals("size")){
			fields.put(string, string2);
		}else{
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
		// will return the place where the shouts will eventually come from
		return null;
	}
	
	public void addPlayer(int x, int y, String name){
		Castle temp=new Castle(x,y,name,Castles.api.Color.values()[x]);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
		x++;
	}
	
	public void addCastle(int x, int y, String name){
		Castle temp=new Castle(x,y,name,null);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
	}
	
	public void addRally(int x, int y, String name){
		RallyPoint temp=new RallyPoint(x,y,name);
		Vertex <RallyPoint,Integer> temp2=new Vertex<RallyPoint, Integer>(temp);
		graph.addNode(temp2);
	}
	
	public void connect(String n1, String n2, int weight){
		//connect 2 points, either castle, rally point, and starting points
	}
	
	/*
	 * change to canPassThroug(), makes a list
	 * of nodes and edges that troops CAN pass through
	 */
	public void calculatePaths(){
		
	}
	
}
