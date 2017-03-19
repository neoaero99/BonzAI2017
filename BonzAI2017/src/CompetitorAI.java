import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Test")
public class CompetitorAI extends AI {
	
	@Override
	public Action action(Turn turn) {
		return new ShoutAction("Hello, world!");
	}
}