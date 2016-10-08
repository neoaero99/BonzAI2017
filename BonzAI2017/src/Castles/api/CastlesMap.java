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
	
	//E will be the parent for castles and rallypoint, once created;
	private WeightedGraph<Position,Double> graph=new WeightedGraph<>();
	private GraphPathSet<Position> paths= new GraphPathSet<>(graph);
	
	//WeightedGraph
	//GraphPathSet
	//
	
	public CastlesMap(){
		
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
		// 
		
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
		//add starting point
	}
	
	public void addCastle(int x, int y, String name){
		//add each unclaimed castle
	}
	
	public void addRally(int x, int y, String name){
		//add a rally point
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
