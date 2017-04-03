package Castles.api;

/**
 * A data class used by the MoveAction to store an AI's update command for a
 * specific soldier group.
 * 
 * @author Joshua Hooker
 */
public class UpdateSoldier {
	public int soldierIdx;
	public String posID;
	public SoldierState newState;
	
	/**
	 * @param sIdx	The index of the soldier on the position
	 * @param pID	The position of the soldier
	 * @param state	The new state of soldier
	 */
	public UpdateSoldier(int sIdx, String pID, SoldierState state) {
		soldierIdx = sIdx;
		posID = pID;
		newState = state;
	}
	
	@Override
	public String toString() {
		return String.format("STATE %d %s %s", soldierIdx, posID, newState);
	}
}
