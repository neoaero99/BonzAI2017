import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Test")
public class CompetitorAI extends AI {
	
	@Override
	public Action action(Turn turn) {
		Storage s = new Storage();
		s.x = 5;
		return new ShoutAction("Test " + Integer.toString(s.x) + " value");
	}
	
	private class Storage {
		int x;
	}
}
