import java.util.Random;

import Castles.api.TeamColor;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;

@Agent(name="FailAI")
public class SampleAI2 extends AI {
	
	private TeamColor enemy;
	private static final Random generator;
	
	static {
		generator = new Random(0);
	}
	
	public SampleAI2() {
		enemy = null;
	}

	@Override
	public Action action(Turn turn) {
		float p = generator.nextFloat();
		
		while (true) {
			
		}
	}
	
}
