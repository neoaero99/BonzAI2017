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
	 * @param sIdx	The index of soldier on the list of soldiers on the
	 * 				position, with which the soldier is associated
	 * @param sAmt	The amount of soldiers, which will be given the new path:
	 * 				this field must be a positive integer that is less than
	 * 				or equal to the number of soldiers in the soldier group,
	 * 				which is located at the starting position
	 * @param sID	The position, where the target soldier is
	 * @param eID	The ending position of the new path for a partition of the
	 * 				target soldier group
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
