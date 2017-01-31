package Castles.api;

import java.util.ArrayList;

import Castles.Objects.Building;
import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import bonzai.Team;

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
	public final Team leader;
	
	/**
	 * The team, who has soldiers currently occupying this position, or null if
	 * no soldiers exists at this position.
	 */
	public final Team occupant;
	
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
				leader = ((Building)r).getTeam();
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
			
			occupant = (occupants.size() == 0) ? null : occupants.get(0).getLeader();
		}
	}
}
