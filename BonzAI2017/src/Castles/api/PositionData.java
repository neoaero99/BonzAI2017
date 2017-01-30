package Castles.api;

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
	 * The number of soldiers currently on this position
	 */
	public final int occupantSize;
	
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
			occupantSize = 0;
			
		} else {
			Soldier s = r.getOccupant();
			ID = r.ID;
			
			if (r instanceof Building) {
				leader = ((Building)r).getTeam();
				defVal = ((Building)r).defenseValue;
				
			} else {
				leader = null;
				defVal = 0;
			}
			
			occupantSize = (s == null) ? 0 : s.getValue();
			occupant = (s == null) ? null : s.getLeader();
		}
	}
}
