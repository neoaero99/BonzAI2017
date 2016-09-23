package lazers.api;

import static bonzai.util.Utility.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import bonzai.Entity;
import bonzai.Identifiable;
import bonzai.Positionable;
import bonzai.Rotatable;

public class LazersMap {
	
	private int max_repeat_id = 0, max_target_id = 0, max_source_id = 0;
	private int min, mid;

	private LinkedList<Repeater> repeaters;
	private LinkedList<Target> targets;
	private LinkedList<Wall> walls;
	private LinkedList<Emitter> emitters;
	
	private HashMap<String, String> fields = new HashMap<>();
	private boolean[][] adjacency;
	
	
	public LazersMap(){
		repeaters = new LinkedList<>();
		targets = new LinkedList<>();
		walls = new LinkedList<>();
		emitters = new LinkedList<>();
	}
	
	/**
	 * Basic Getters
	 */
	public Collection<Repeater> getRepeaters() {
		return repeaters;
	}

	public Collection<Target> getTargets() {
		return targets;
	}

	public Collection<Wall> getWalls() {
		return walls;
	}

	public Collection<Emitter> getEmitters() {
		return emitters;
	}
	
	public String getField(String key) {
		return fields.get(key);
	}
	
	/**
	 * Tests if a can hit b by querying the adjacency matrix
	 * 
	 * @param a Should not be a target 
	 * @param b
	 */
	public boolean canHit(Identifiable a, Identifiable b) {
		return adjacency[a.getID()][b.getID()];
	}
	
	/**
	 * Returns all objects that can be hit from the given object
	 * @param obj - a Rotatable object (Emitter/Repeater)
	 * @return a list of Entities that can be hit
	 */
	@SuppressWarnings("rawtypes")
	public LinkedList<Entity> hittableFrom(Rotatable obj) {
		int id = ((Identifiable)obj).getID();
		LinkedList<Entity> ret = new LinkedList<>();
		
		if (id < 0 || id >= adjacency.length) {	
			for (int i = 0; i != adjacency.length; ++i) {
				if (i != id && adjacency[id][i]) {
					ret.add(getObject(i));
				}
			}
		}
		
		return ret;
	}
	
	/*
	 * Should we delete this ???
	 * The only benefit over hittableFrom is that this can be called with a Target
	 * But I don't know how useful that'd be
	 */
	public Collection<Positionable> getNeighbors(Identifiable curr) {
		List<Positionable> ret = new LinkedList<>();
			
		for (int i = 0; i != adjacency.length; ++i) {
			if (adjacency[curr.getID()][i]) {
				ret.add((Positionable)getObject(i));
			}
		}
		
		return ret;
	}
	
	/**
	 * Get the most up-to-date version of the repeater/target
	 * @param r/t
	 * @return
	 */
	public Repeater getRecent(Repeater r) {
		return find(getRepeaters(), r);
	}
	
	public Target getRecent(Target t) {
		return find(getTargets(), t);
	}

	/**
	 *  Get the rotation necessary for 'source' to point to 'target'
	 */
	public float getRotationTo(Rotatable source, Identifiable target) {
		return RotateAction.calcRotation(((Positionable)source).getPosition(), ((Positionable)target).getPosition());
	}
	
	/**
	 * Test whether two objects are connected by a lazer
	 * -- Really tests whether the first is rotated so that they would be connecting
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean hasConnection(Rotatable source, Identifiable target) {
		return source.getRotation() == getRotationTo(source, target);
	}

	/**
	 * Get a list of hittable repeaters for the given Rotatable
	 * @param r
	 * @return
	 */
	@SuppressWarnings("rawtypes") 
	public LinkedList<Repeater> hittableRepeaters(Rotatable r) {
		LinkedList<Repeater> ret = new LinkedList<>();
		
		for (Entity e : hittableFrom(r))
			if (e instanceof Repeater)
				ret.add((Repeater)e);
			
		return ret;
	}
	
	/**
	 * Get a list of hittable targets for the given Repeater (Emitter's can't hit targets)
	 * @param repeater
	 * @return
	 */
	@SuppressWarnings("rawtypes") 
	public LinkedList<Target> hittableTargets(Repeater repeater) {
		LinkedList<Target> ret = new LinkedList<>();
		
		for (Entity e : hittableFrom(repeater))
			if (e instanceof Target)
				ret.add((Target)e);
		
		return ret;
	}
	
	
	
	/**
	 * Package level methods
	 * WARNING: DO NOT CALL !!!
	 */
	boolean addRepeater(Repeater r) {
		max_repeat_id = r.getID();
		return repeaters.add(r);
	}
	
	boolean addTarget(Target t) {
		max_target_id = t.getID();
		return targets.add(t);
	}
	
	boolean addWall(Wall w) {
		return walls.add(w);
	}

	boolean addEmitter(Emitter s, int uid) {
		return emitters.add(s);
	}
	
	boolean setMatrix(boolean array[][]) {
		adjacency = array;			// Will I need to copy the array ???

		int max = Math.max(max_source_id, Math.max(max_repeat_id, max_target_id));
		min = Math.min(max_source_id, Math.min(max_repeat_id, max_target_id));
		mid = max_repeat_id + max_target_id + max_source_id - min - max;
		
		return true;
	}

	void setField(String key, String value) {
		fields.put(key, value);
	}
	
	@SuppressWarnings("rawtypes")
	Entity getObject(int id) {
		if (id > adjacency.length || id < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}

		int select = (id < min) ? min : ((id < mid) ? mid : adjacency.length);
		
		if (select == max_repeat_id) {
			return repeaters.get(max_repeat_id - id);
			
		} else if (select == max_target_id) {
			return targets.get(max_target_id - id);
			
		} else {
			return emitters.get(max_source_id - id);
		}
	}
}
