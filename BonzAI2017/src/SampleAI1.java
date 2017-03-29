import java.util.List;

import Castles.api.MoveAction;
import Castles.api.PositionData;
import Castles.api.SoldierData;
import Castles.api.TeamColor;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import Castles.api.ShoutAction;
import bonzai.Team;

/**
 * A simple AI, which sends soldiers first to the closest unclaimed buildings.
 * If no unclaimed buildings exist, then it sends soldiers to enemy buildings.
 * 
 * @author Joshua Hooker
 *
 */
@Agent(name="FindClosest")
public class SampleAI1 extends AI {
	
	/**
	 * The color of the enemy team
	 */
	private TeamColor enemy;
	
	/**
	 * Initialize any instance variables
	 */
	public SampleAI1() {
		enemy = null;
	}
	
	@Override
	public Action action(Turn turn) {
		// Create an empty move action
		MoveAction move = new MoveAction();
		// Keep track of whether a soldier has been moved this turn
		boolean movedSomeSoldier = false;
		
		// Set the enemy color field
		if (enemy == null) {
			enemy = turn.getEnemyTeams().get(0).getColor();
		}
		
		// Pull the list of soldiers controlled by this AI from the turn
		List<SoldierData> soldiers = turn.getSoldiersControlledBy( turn.getMyTeam().getColor() );
		
		/* For each soldier controlled by this AI, determine where to send the
		 * soldier this turn */
		for (SoldierData s : soldiers) {
			
			// Only move soldiers, which are not already moving
			if (s.path == null || s.path.size() < 2) {
				/* Query for the closest unclaimed buildings to a soldier's
				 * current position */
				List<PositionData> unclaimedBuildings = turn.getClosestByColor(s.posID, null);
				
				if (unclaimedBuildings.size() <= 0) {
					/* If no unclaimed buildings exist, then query for enemy
					 * buildings */
					List<PositionData> enemyBuildings = turn.getClosestByColor(s.posID, enemy);
					
					// No enemy buildings exist
					if (enemyBuildings.size() <= 0) {
						continue;
					}
					
					// Pick the first enemy building
					PositionData dest = enemyBuildings.get(0);
					
					/* Determine if the soldier group is large enough to
					 * capture the building */
					if (dest.defVal < s.size) {
						movedSomeSoldier = true;
						/* Order the soldier group to march towards the target
						 * enemy building */
						move.addMove(s.sIdx, s.size, s.posID, dest.ID);
					}
					
				} else {
					/* If unclaimed buildings exist, then pick the first one
					 * found by the previous query */
					PositionData dest = unclaimedBuildings.get(0);
					
					/* Determine if the soldier group is large enough to
					 * capture the building */
					if (dest.defVal <= s.size) {
						movedSomeSoldier = true;
						/* Order the soldier group to march towards the target
						 * unclaimed building */
						move.addMove(s.sIdx, s.size, s.posID, dest.ID);
					}
				}
			}
		}
		
		if (movedSomeSoldier) {
			// submit the move action
			return move;
			
		} else {
			// If no soldier were moved this turn, shout the given string
			return new ShoutAction("No soldiers moved!");
		}
	}

}
