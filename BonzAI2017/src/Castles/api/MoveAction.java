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
	
	private ArrayList<Object> actions;
	
	/**
	 * Initializes the list of actions.
	 */
	public MoveAction() {
		actions = new ArrayList<Object>();
	}
	
	/**
	 * 
	 * @param sIdx
	 * @param sAmt
	 * @param sID
	 * @param eID
	 */
	public void addMove(int sIdx, int sAmt, String sID, String eID) {
		actions.add(new MoveSoldier(sIdx, sAmt, sID, eID));
	}
	
	/**
	 * 
	 * @param sIdx
	 * @param pID
	 * @param s
	 */
	public void addUpdate(int sIdx, String pID, SoldierState s) {
		actions.add(new UpdateSoldier(sIdx, pID, s));
	}
	
	/**
	 * 
	 * @param idx
	 * @return
	 */
	public Object get(int idx) {
		return actions.get(idx);
	}
	
	/**
	 * 
	 * @param idx
	 */
	public void remove(int idx) {
		actions.remove(idx);
	}
	
	/**
	 * 
	 * @return
	 */
	public int numOfActions() {
		return actions.size();
	}

	@Override
	public String toString(){
		String actionStr = String.format("MOVE %d", actions.size());
		
		for (Object a : actions) {
			// Append each action on the string
			actionStr += " " + a;
		}
		
		return actionStr;
	}
	
}
