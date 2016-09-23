import bonzai.AI;
import bonzai.Action;
import bonzai.Agent;

import bonzai.ShoutAction;
import lazers.api.Turn;
 
@Agent(name = "SimpleAI")
public class CompetitorAI extends AI {
	public Action action(Turn turn) {
		return new ShoutAction("La-De-Da-De-Da");
	}
}