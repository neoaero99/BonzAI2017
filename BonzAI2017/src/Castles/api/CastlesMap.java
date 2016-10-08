package Castles.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Castles.Objects.*;
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
		// TODO Auto-generated method stub
		
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Position getEntity(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addPlayer(int x, int y, String name){
		
	}
	
	public void addCastle(int x, int y, String name){
		
	}
	
	public void addRally(int x, int y, String name){
		
	}
	
	public void connect(String n1, String n2, int weight){
		
	}
	
	/*
	 * change to canPassThroug(), makes a list
	 * of nodes and edges that troops CAN pass through
	 */
	public void calculatePaths(){
		
	}
	
}
