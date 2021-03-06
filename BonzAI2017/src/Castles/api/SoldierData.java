package Castles.api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import Castles.Objects.Soldier;

/**
 * Stores the data associated with a soldier, that an AI needs to know
 * 
 * @author Joshua Hooker
 */
public class SoldierData {
	/**
	 * The team, to which this soldier group belongs
	 */
	public final TeamColor leader;
	
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
	 * The index of soldier on its position's list of soldiers
	 */
	public final int sIdx;
	
	/**
	 * The path, which this soldier will move along, when in the MOVING state.
	 */
	public final List<String> path;
	
	/**
	 * Copies the data of the given soldier group to this.
	 * 
	 * @param s	The soldier group, which will have its data copied to this
	 */
	public SoldierData(Soldier s, int sdx) {
		sIdx = sdx;
		
		if (s == null) {
			leader = null;
			size = 0;
			state = SoldierState.STANDBY;
			posID = "?";
			path = null;
			
		} else {
			leader = s.getLeaderColor();
			size = s.getValue();
			state = s.getState();
			
			if (s.getPath() != null) {
				path = new ArrayList<String>( s.getPath() );
				
			} else {
				path = new ArrayList<String>();
			}
			
			posID = s.getPositionID();
		}
	}
	
	@Override
	public String toString() {
		return String.format("id=%d leader:%s state:%s size:%d pos:%s",
				sIdx, leader, state, size, posID);
	}
}
