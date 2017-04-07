import java.util.List;

import Castles.api.*;
import bonzai.*;

/**
 * Simple example AI, which moves the first soldier in the AI's list of soldiers
 * towards the enemy base.
 * 
 * @author Joshua
 */
@Agent(name ="Test")
public class CompetitorAI extends AI {
	
	/* This AI does not use a default constructor! */
	
	@Override
	public Action action(Turn turn) {
		
		Team myTeam = turn.getMyTeam();
		// Move action constructor takes no parameters
		MoveAction m = new MoveAction();
		// Get the list of soldier groups controlled by your team
		List<SoldierData> soldiers = turn.getSoldiersControlledBy( myTeam.getColor() );
		
		System.out.println(soldiers.size());
		
		// Check to make sure you have soldiers
		if (soldiers.size() > 0) {
			// Get the first soldier group in the list of soldiers
			SoldierData s = soldiers.get(0);
			
			/* Only gives the soldier group a command, if they have not
			 * already been issued a command */
			if (s.path != null || s.path.size() < 2) {
				
				Team enemy = turn.getEnemyTeams().get(0);
				
				// Get the enemy base position
				PositionData enemyBase = turn.getBaseFor(enemy);
				
				// Tell the soldier group to move to the enemy's base
				m.addMove(s.sIdx, s.size, s.posID, enemyBase.ID);
				
				// Verify the move action
				System.out.println( turn.verifyMoveAction(myTeam, m) );
			}
		}
		
		if (m.numOfActions() > 0) {
			return m;
			
		} else {
			// If no commands were issued, then shout a message
			return new ShoutAction("No commands given!");
		}
	}
}
