package Castles.api;

import java.util.ArrayList;
import java.util.List;

import Castles.Objects.SoldierState;
import bonzai.Action;

/**
 * The main action for AI's to take in the castles game for BonzAI 2017
 * 
 * 
 * Usage: Create a MoveAction at the beginning of an AI's Turn </br>
 * 		  Have AI add all the soldier movements it would like </br>
 * 		  to take in its turn.</br>
 * 		  Return the MoveAction at the end of the AI's Turn</br>
 * 
 * 
 * @author Dane Jensen
 * @author Team Secret (2016 - 2017)
 */
public class MoveAction implements Action {
	
	private ArrayList<Object> cmdList;
	
	/**
	 * Initializes the move action.
	 */ 
	public MoveAction() {
		cmdList = new ArrayList<Object>();
	}
	
	/**
	 * Adds a move command to this move action. A move command will partition
	 * the target soldier group based on the spAmt parameter and tell the
	 * partition of the specified size to move to the specified end point, while
	 * leaving the rest of the soldiers on their origin path.
	 * 
	 * @param sIdx	The index of target soldier group
	 * @param spAmt	The number of soldier from that soldier group affected by
	 * 				the command
	 * @param sID	The current position of the target soldier group
	 * @param eID	The end point for the move command
	 */
	public void addMove(int sIdx, int spAmt, String sID, String eID) {
		cmdList.add(new MoveSoldier(sIdx, spAmt, sID, eID));
	}
	
	/**
	 * Adds an update command to this move action. An update command will
	 * change the state of the soldier group specified by the position and
	 * soldier index to the specified state.
	 * 
	 * @param sIdx		The index of the soldier group to update
	 * @param posID		The ID of the position occupied by the target soldier
	 * 					group
	 * @param s			The new state of the soldier group
	 */
	public void addUpdate(int sIdx, String posID, SoldierState s) {
		cmdList.add(new UpdateSoldier(sIdx, posID, s));
	}
	
	/**
	 * Returns the command of this move action at the specified index.
	 * 
	 * @param idx	The index of a command of this move action
	 * @return		The command at the specified index
	 */
	public Object get(int idx) {
		if (idx >= 0 && idx < cmdList.size()) {
			return cmdList.get(idx);
		}
		
		return null;
	}
	
	/**
	 * Removes the command at the specified index from this action's list of
	 * commands.
	 * 
	 * @param idx	The index of a command in this action
	 */
	public void remove(int idx) {
		if (idx >= 0 && idx < cmdList.size()) {
			cmdList.remove(idx);
		}
	}
	
	/**
	 * @return	The number of commands in this action
	 */
	public int numOfActions() {
		return cmdList.size();
	}

	@Override
	public String toString(){
		String actionStr = String.format("MOVE %d", cmdList.size());
		
		for (Object a : cmdList) {
			// Append each action on the string
			actionStr += " " + a;
		}
		
		return actionStr;
	}
	
}
