package Castles.api;

/**
 * 
 * 
 * @author Joshua Hooker
 */
public class SoldierUpdate {
	public int soldierIdx, splitAmt;
	public String startID, endID;
	
	/**
	 * 
	 * 
	 * @param sIdx
	 * @param sAmt
	 * @param sID
	 * @param eID
	 */
	public SoldierUpdate(int sIdx, int sAmt, String sID, String eID) {
		soldierIdx = sIdx;
		splitAmt = sAmt;
		startID = sID;
		endID = eID;
	}
}
