import bonzai.*;
import Castles.Objects.RallyPoint;
import Castles.api.*;
import Castles.util.graph.Vertex;
import Castles.util.graph.WeightedEdge;

@Agent(name = "DaneAI")
public class DaneAI extends AI {
	private final String[] taunts = {};
	private final boolean taunt = false;
	public DaneAI(){
		
	}
	
	public Action action(Turn turn) {
		if(taunt){
			return new ShoutAction(taunts[(int)(Math.random()*taunts.length)]);
		}
		
		return new ShoutAction("GIT REKT!");
	}
	
}
