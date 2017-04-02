import java.util.List;

import Castles.api.MoveAction;
import Castles.api.PositionData;
import Castles.api.ShoutAction;
import Castles.api.SoldierData;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;

@Agent(name="NAAI")
public class Turtle extends AI {
	
	private List<String> path;
	
	public Turtle() {
		path = null;
	}
	
	@Override
	public Action action(Turn turn) {
		
		if (path == null) {
			PositionData myBase = turn.getBaseFor( turn.getMyTeam() );
			PositionData enemyBase = turn.getBaseFor( turn.getEnemyTeams().get(0) );
			
			path = turn.getPath(myBase.ID, enemyBase.ID);
		}
		
		if (turn.getTurnsRemaining() <= path.size()) {
			List<SoldierData> soldiers = turn.getSoldiersControlledBy( turn.getMyTeam().getColor() );
			
			if (soldiers.size() > 0) {
				MoveAction move = new MoveAction();
				SoldierData s = soldiers.get(0);
				
				move.addMove(s.sIdx, s.size, s.posID, path.get( path.size() - 1 ));
				
				return move;
			}
		}
		
		return new ShoutAction( Integer.toString( turn.getTurnsRemaining() ) );
	}

}
