package Castles.api;

import java.util.ArrayList;
import java.util.Arrays;

import Castles.Objects.Building;
import Castles.Objects.*;
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
	 * The unique ID associated with this position.
	 */
	public final String ID;
	
	/**
	 * The type of building, which this position is (i.e. base, castle or
	 * village).
	 */
	public final PType type;
	
	/**
	 * The AI, who currently controls this position; null if it is
	 * unclaimed.
	 */
	public final TeamColor leader;
	
	/**
	 * The bonus given to the defending team's soldiers, if this position is
	 * attacked by another team's soldiers.
	 */
	public final int defVal;
	
	/**
	 * The list soldier groups at this position. The index of the soldier group
	 * in this list is directly related to the soldier group's soldier index.
	 */
	public final SoldierData[] occupantData;
	
	/**
	 * Fill the data of the Position with that of the given position.
	 * 
	 * @param r	The position, which will have its data copied to this
	 */
	public PositionData(RallyPoint r) {
		if (r == null) {
			ID = "!@#$%^&*()";
			leader = null;
			defVal = -9999;
			this.occupantData = new SoldierData[0];
			type= null;
			
		} else {
			ID = r.ID;
			
			if (r instanceof Building) {
				Building b = (Building)r;
				
				type = b.type;
				leader = b.getTeamColor();
				defVal = b.getDefVal();
				
			} else {
				leader = null;
				type = PType.RALLY;
				defVal = 0;
			}
			
			ArrayList<Soldier> occupants = r.getOccupants();
			occupantData = new SoldierData[occupants.size()];
			
			for (int idx = 0; idx < occupants.size(); ++idx) {
				occupantData[idx] = new SoldierData(occupants.get(idx), idx);
			}
		}
	}
	
	/**
	 * Returns the team color of the soldiers occupying this position. It is
	 * entirely possible that a soldier group belonging to one AI is
	 * occupying a building controlled by another AI.
	 * 
	 * @return	The team color of occupying soldiers
	 */
	public TeamColor getOccupantLeader() {
		if (occupantData.length > 0) {
			return occupantData[0].leader;
		}
		
		return null;
	}
	
	public boolean isControled(){
		return (leader == null) ? false : true;
	}
	
	public boolean isControledBy(Castles.api.TeamColor team){
		return (leader == team) ? true : false;
	}
	
	@Override
	public String toString() {
		String claimedBy = (leader == null) ? "N/A" : leader.name();
		
		return String.format("id:%s def:%d claimed_by:%s occupants:%s", ID,
				defVal, claimedBy, Arrays.toString(occupantData));
	}
}
