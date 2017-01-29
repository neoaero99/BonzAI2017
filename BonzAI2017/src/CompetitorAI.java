import bonzai.*;
import Castles.Objects.RallyPoint;
import Castles.api.*;
import Castles.util.graph.Vertex;

/**
 * This is the class for your AI.  It will handle all
 * Interactions with the map and the game in the form
 * of a returned action.
 * 
 * 
 * 
 * 
 * @author BonzAI Competitor
 *
 */
//put your AI's name in this section so the 
//game can tell your AI apart from the rest
@Agent(name = "MeanAI")
public class CompetitorAI extends AI {
	public CompetitorAI(){}
	/**
	 * @param Turn the current state of the map that your AI will act upon
	 * 
	 * @return the action your AI will take
	 * @return There are 2 possible return classes, the ShoutAction and the 
	 * 		   Move action.  The shout action just puts text on the screen 
	 * 		   at the location of your AI's original castle, the move action
	 * 		   will contain a list of movements that your troops will perform
	 * 		   with the inputed game state
	 */
	public Action action(Turn turn) {
		//put your AI's stuff here
		
		return  new ShoutAction("I am shouting!");
	}
	
}