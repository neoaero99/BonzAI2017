import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Test")
public class CompetitorAI extends AI {
	
	@Override
	public Action action(Turn turn) {
		
		Team t = turn.getMyTeam();
		MoveAction m = new MoveAction();
		List<SoldierData> soldiers = turn.getSoldiersControlledBy( t.getColor() );
		
		if (soldiers.size() > 0) {
			
			SoldierData s = soldiers.get(0);
			
			if (s.path != null && s.path.size() > 1) {
				
				if (s.size > PositionData.BASE_DV) {
					
					PositionData enemyBase = turn.getBaseFor(t);
				
					m.addMove(s.sIdx, s.size, s.posID, enemyBase.ID);
					
				}
			}
		}
		
		return ( m.numOfActions() > 0 ) ? m : new ShoutAction("No soldiers moved!");
	}
}
