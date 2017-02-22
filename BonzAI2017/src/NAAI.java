import java.util.List;
import java.util.Random;

import Castles.api.MoveAction;
import Castles.api.SoldierData;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;
import bonzai.ShoutAction;

/**
 * A simple AI used for testing the game.
 * 
 * @author Joshua Hooker
 */
@Agent(name = "NAAI")
public class NAAI extends AI {
	
	private static final Random generator;
	
	static {
		generator = new Random(0);
	}

	@Override
	public Action action(Turn turn) {
		double prob = generator.nextDouble();
		
		/**
		
		List<SoldierData> soldiers = turn.getSoldiersControlledBy(turn.getMyTeam().getColor());
		
		System.out.printf("Group #: %d\n", soldiers.size());
		
		for (SoldierData s : soldiers) {
			System.out.printf("%s\n", s);
		}
		
		System.out.println();
		
		/**
		
		if (prob >= 0.1) {
			return new ShoutAction("...");
		}
		
		/**/
		
		MoveAction ma = new MoveAction();
		
		if (turn.getMyTeam().getID() == 0) {
			ma.addMove(0, 1, "P0", "P1");
			
		} else {
			ma.addMove(0, 1, "P1", "P0");
		}
		
		return ma;
	}
}
