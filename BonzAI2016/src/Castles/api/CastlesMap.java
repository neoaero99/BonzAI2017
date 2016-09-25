package Castles.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import Castles.Objects.Target;
import Castles.Objects.Wall;
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
	
	private static HashMap<Integer, Collection<Integer>> hitMatrix = new HashMap<>();
	private HashMap<String, String> fields = new HashMap<>();


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
	
	
	
	
	
	
	
	//TODO	
	/**
	 *	not needed for our map, not needed for now
	 * @param input
	 * @return
	 */
	public String getField(String input){
		return null;
	}

	public void addTarget(Target target) {
		// TODO Auto-generated method stub
		
	}

	public void addWall(Wall wall) {
		// TODO Auto-generated method stub
		
	}

	public void setField(String string, String string2) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * change to canPassThroug(), makes a list
	 * of nodes and edges that troops CAN pass through
	 */
	public void calculateCanHit() {
		// TODO Auto-generated method stub
		
	}

	public void removePlayer(int i) {
		// TODO Auto-generated method stub
		
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Position getEntity(int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
