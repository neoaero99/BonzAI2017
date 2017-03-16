import java.util.List;

import Castles.api.MoveAction;
import Castles.api.PositionData;
import Castles.api.SoldierData;
import Castles.api.TeamColor;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import bonzai.ShoutAction;
import bonzai.Team;

@Agent(name="AnAI")
public class AnAI extends AI {
	
	private TeamColor enemy;
	
	public AnAI() {
		enemy = null;
	}
	
	@Override
	public Action action(Turn turn) {
		MoveAction move = new MoveAction();
		boolean movedSomeSoldier = false;
		
		if (enemy == null) {
			enemy = turn.getEnemyTeams().get(0).getColor();
		}
		
		List<SoldierData> soldiers = turn.getSoldiersControlledBy( turn.getMyTeam().getColor() );
		
		for (SoldierData s : soldiers) {
			
			if (s.path == null || s.path.size() < 2) {
				List<PositionData> unclaimedBuildings = turn.getClosestByColor(s.posID, null);
				
				if (unclaimedBuildings.size() <= 0) {
					List<PositionData> enemyBuildings = turn.getClosestByColor(s.posID, enemy);
					
					if (enemyBuildings.size() <= 0) {
						break;
					}
					
					PositionData dest = enemyBuildings.get(0);
					
					if (dest.defVal < s.sIdx) {
						movedSomeSoldier = true;
						move.addMove(s.sIdx, s.size, s.posID, dest.ID);
					}
					
				} else {
					PositionData dest = unclaimedBuildings.get(0);
					
					if (dest.defVal < s.size) {
						movedSomeSoldier = true;
						move.addMove(s.sIdx, s.size, s.posID, dest.ID);
					}
				}
			}
		}
		
		if (movedSomeSoldier) {
			return move;
			
		} else {
			return new ShoutAction("No soldiers moved!");
		}
	}

}
