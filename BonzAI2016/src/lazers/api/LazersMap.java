package lazers.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import bonzai.Identifiable;
import bonzai.Position;
import bonzai.Positionable;
import bonzai.Team;
import bonzai.util.Predicate;
import bonzai.util.Utility;

public class LazersMap {
	
	// TODO 2017: Read in map files here. 

	private int max_repeat_id = 0, max_target_id = 0, max_source_id = 0;
	private int min, mid;
	int width,height;
	
	private static HashMap<Integer, Collection<Integer>> hitMatrix = new HashMap<>();
	private HashMap<String, String> fields = new HashMap<>();


	public LazersMap(){
		
	}

	public LazersMap(LazersMap previousTurn) {
		this(previousTurn, true);
	}

	/**
	 * Copy constructor
	 * 
	 * @param previousTurn - the map of the previous turn to clone
	 * @param decCooldown - whether or not to decrement Repeater cooldowns
	 */
	public LazersMap(LazersMap previousTurn, boolean decCooldown) {
	}
	
}
