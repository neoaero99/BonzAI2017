package Castles.api;

import java.util.ArrayList;
import java.util.Arrays;

import Castles.Objects.Building;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;

/**
 * Stores the information for a position on the graph, which an AI will need to
 * know.
 * 
 * @author Joshua Hooker
 */
public class PositionData {
	/**
	 * The unique ID associated with a position on the map.
	 */
	public final String ID;
	
	/**
	 * The team, who currently controls this position, or null if it is
	 * unclaimed
	 */
	public final Color leader;
	
	/**
	 * The team, who has soldiers currently occupying this position, or null if
	 * no soldiers exists at this position.
	 */
	public final Color occupant;
	
	/**
	 * The bonus given to the defending team's soldiers, if this position is
	 * attacked by another team's soldiers.
	 */
	public final int defVal;
	
	/**
	 * The list of sizes of soldier groups at this position
	 */
	public final int[] occupantSizes;
	
	/**
	 * Fill the data of the Position with that of the given rally point.
	 * 
	 * @param r	The rally point, which will have its data copied to this
	 */
	public PositionData(RallyPoint r) {
		if (r == null) {
			ID = "!@#$%^&*()";
			leader = null;
			occupant = null;
			defVal = -9999;
			occupantSizes = new int[] { 0 };
			
		} else {
			ID = r.ID;
			
			if (r instanceof Building) {
				leader = ((Building)r).getTeamColor();
				defVal = ((Building)r).defenseValue;
				
			} else {
				leader = null;
				defVal = 0;
			}
			
			ArrayList<Soldier> occupants = r.getOccupants();
			occupantSizes = new int[occupants.size()];
			
			for (int idx = 0 ; idx < occupants.size(); ++idx) {
				occupantSizes[idx] = occupants.get(idx).getValue();
			}
			
			occupant = (occupants.size() == 0) ? null : occupants.get(0).getLeaderColor();
		}
	}
	
	public boolean isControled(){
		return (leader == null) ? false : true;
	}
	
	public boolean isControledBy(Castles.api.Color team){
		return (leader == team) ? true : false;
	}
	
	public String toString() {
		String claimedBy = (leader == null) ? "N/A" : leader.name();
		String occupantColor = (occupant == null) ? "N/A" : occupant.name();
		
		return String.format("id:%s def:%d claimed_by:%s occupants:%s : %s", ID,
				defVal, claimedBy, occupantColor, Arrays.toString(occupantSizes));
	}
}
