import bonzai.*;
import lazers.api.*;

@Agent(name = "SimpleAI")
public class CompetitorAI extends AI {
	public Action action(Turn turn) {
		//This is where
		
		return new ShoutAction("I am shouting!");
	}
}