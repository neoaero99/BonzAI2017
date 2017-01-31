package Castles.api;

import java.util.ArrayList;
import java.util.List;
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
	
	private int splitAmount;
	private ArrayList<String> pathIDs;
	
	/**
	 * 
	 * 
	 * @param splitAmt
	 * @param pathIDs
	 */
	public MoveAction(int splitAmt, List<String> pathIDs) {
		splitAmount = splitAmt;
		this.pathIDs = new ArrayList<String>(pathIDs);
	}
	
	/**
	 * @return
	 */
	public ArrayList<String> getPathIDs() {
		return new ArrayList<String>( pathIDs );
	}

	/**
	 * @return
	 */
	public int getSplitAmount() {
		return splitAmount;
	}

	@Override
	public String toString(){
		String actionStr = String.format("MOVE %d %d", splitAmount, pathIDs.size());
		
		for (int idx = 0; pathIDs != null && idx < pathIDs.size(); ++idx) {
			// Append each ID of the position in the path
			actionStr += " " + pathIDs.get(idx);
		}
		
		return actionStr;
	}
	
}
