import java.util.Random;

import Castles.api.MoveAction;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import bonzai.ShoutAction;

@Agent(name = "NAAI")
public class NAAI extends AI {

	@Override
	public Action action(Turn turn) {
		Random generator = new Random( System.currentTimeMillis() );
		
		double prob = generator.nextDouble();
		
		if (prob >= 5.0) {
			MoveAction ma = new MoveAction();
			
			if (turn.getMyTeam().getID() == 0) {
				ma.addMove(0, 1, "P0", "P1");
				
			} else {
				ma.addMove(0,  1, "P1", "P0");
			}
			
			return ma;
		}
		
		return new ShoutAction("...");
	}
}
