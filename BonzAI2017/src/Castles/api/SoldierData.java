package Castles.api;

import java.util.ArrayList;
import Castles.Objects.Soldier;
import Castles.Objects.SoldierState;
import bonzai.Team;

/**
 * Stores the data associated with a soldier, that an AI needs to know
 * 
 * @author Joshua Hooker
 */
public class SoldierData {
	/**
	 * The team, to which this soldier group belongs
	 */
	public final Team leader;
	
	/**
	 * The current state of the soldier group
	 */
	public final SoldierState state;
	
	/**
	 * The number of soldiers in the group
	 */
	public final int size;
	
	/**
	 * The ID of the node, on which this soldier is on
	 */
	public final String posID;
	
	/**
	 * The path, which this soldier will move along, when in the MOVING state.
	 */
	public final ArrayList<String> path;
	
	/**
	 * Copies the data of the given soldier group to this.
	 * 
	 * @param s	The soldier group, which will have its data copied to this
	 */
	public SoldierData(Soldier s) {
		if (s == null) {
			leader = null;
			size = 0;
			state = SoldierState.CONFLICT;
			posID = "?";
			path = null;
			
		} else {
			leader = s.getLeader();
			size = s.getValue();
			state = s.getState();
			path = new ArrayList<String>( s.getPath() );
			posID = s.getPositionID();
		}
	}
}
