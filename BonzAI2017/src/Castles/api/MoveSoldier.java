package Castles.api;

/**
 * Used by the Move Action to store the update for a soldier.
 * 
 * @author Joshua Hooker
 */
public class MoveSoldier {
	public int soldierIdx, splitAmt;
	public String startID, endID;
	
	/**
	 * @param sIdx	The index of the soldier in the list of the soldier's on
	 * 				the position
	 * @param sAmt	The partition of the soldier set to set on the given path
	 * @param sID	The initial position of the soldiers
	 * @param eID	The end position of the soldiers
	 */
	public MoveSoldier(int sIdx, int sAmt, String sID, String eID) {
		soldierIdx = sIdx;
		splitAmt = sAmt;
		startID = sID;
		endID = eID;
	}
	
	@Override
	public String toString() {
		return String.format("PATH %d %d %s %s", soldierIdx, splitAmt, startID,
				endID);
	}
}
