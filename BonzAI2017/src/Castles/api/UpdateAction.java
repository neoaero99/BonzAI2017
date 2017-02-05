package Castles.api;

import Castles.Objects.SoldierState;
import bonzai.Action;

/**
 * Allows an AI to simply update the state of a soldier.
 * 
 * @author Joshua Hooker
 */
public class UpdateAction implements Action {
	
	private String srcID;
	private int soldierIdx;
	private SoldierState newState;
	
	/**
	 * Updates the state of the soldier to the given state. If a soldier has no
	 * defined path and the MOVE state is passed, then the soldier should do
	 * nothing.
	 * 
	 * @param srcID		The ID of the rally point, on which the soldier is
	 * @param sdx		The index of the soldier in the list of soldiers on a
	 * 					position
	 * @param state		The state that the soldier should become
	 */
	public UpdateAction(String srcID, int sdx, SoldierState state) {
		this.srcID = srcID;
		soldierIdx = sdx;
		newState = state;
	}
	
	/**
	 * @return	The ID of the rally point, where the target soldier group is.
	 */
	public String getSrcID() {
		return srcID;
	}
	
	public int getSoldierIdx() {
		return soldierIdx;
	}
	
	/**
	 * @return	The new state of the soldier
	 */
	public SoldierState getState() {
		return newState;
	}
	
	@Override
	public String toString() {
		return String.format("SOLDIER %s %d %s", srcID, soldierIdx, newState.name());
	}
}
