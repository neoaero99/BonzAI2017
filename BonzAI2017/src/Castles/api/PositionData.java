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
	 * The defense value for the building type
	 */
	public static final int BASE_DV = 10,
							CASTLE_DV = 5,
							VILLAGE_DV = 3;
	/**
	 * The soldier spawn rate for the building type
	 */
	public static final int	BASE_SR = 4,
							CASTLE_SR = 3,
							VILLAGE_SR = 2;	
	
	/**
	 * The unique ID associated with this position.
     * </br>
	 * A vertex position's ID is a single character followed by a number.
	 * </br>
	 * Rally points have IDs R0, R1, R2, ...
	 * </br>
	 * Villages have IDs V0, V1, V2, ...
	 * </br>
	 * Castles have IDs C0, C1, C2, ...
	 * </br>
	 * The two bases in a scenario have the IDs P0 and P1
	 * </br>
	 * </br>
	 * Edge waypoint IDs are a bit more complicated.
	 * </br>
	 * Edge waypoint ID = !first-last:<x>
	 * </br>
	 * first is the ID of a vertex connected to the edge, to which the
	 * position belongs
	 * </br>
	 * second is the ID of the other vertex connected to the edge, to
	 * which the position belongs
	 * </br>
	 * x is the index of the position with respect to other waypoints
	 * on the edge. If an edge has 3 waypoints, then those waypoints
	 * would have x values 0, 1, and 2.
	 * </br>
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
	 */
	public TeamColor getOccupantLeader() {
		if (occupantData.length > 0) {
			return occupantData[0].leader;
		}
		
		return null;
	}
	
	/**
	 * Determines if this position has been claimed by an AI.
	 * 
	 * @return	whether the position is claimed	
	 */
	public boolean isControled() {
		return (leader == null) ? false : true;
	}
	
	/**
	 * Determines if the AI with the given team color controls this position.
	 * 
	 * @param team	an AI's team color
	 * @return		if the AI with the given color controls this position
	 */
	public boolean isControledBy(TeamColor team){
		return (leader == team) ? true : false;
	}
	
	@Override
	public String toString() {
		String claimedBy = (leader == null) ? "N/A" : leader.name();
		
		return String.format("id:%s def:%d claimed_by:%s occupants:%s", ID,
				defVal, claimedBy, Arrays.toString(occupantData));
	}
}
