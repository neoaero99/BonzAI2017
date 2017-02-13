import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="SimpleAI")
public class CompetitorAI extends AI {

	Team myTeam;
	@Override
	public Action action(Turn turn) {
			Team myTeam=turn.getMyTeam();
		if(myTeam==null){
			return new ShoutAction("No team? BUG!");
		}
		Color t=myTeam.getColor();
		if(t==null){
			return new ShoutAction("No color? BUG!");
		}
		List<PositionData> myPositions =turn.getPositionsControlledBy(t);
		int max=-1;
		PositionData data=null;
		
		for(PositionData p:myPositions){
			int [] sizes=p.occupantSizes;
			int temp=0;
			for(int i=0;i<sizes.length;i++){
				temp+=sizes[i];
			}
			if(temp>max){
				data=p;
				max=temp;
			}
		}
		if(data==null){
			return new ShoutAction("Data is wrong");
		}
		if(max<=0){
			return new ShoutAction("I be Captain No-Beard!");
		}

		List<Team> teams=turn.getAllTeams();
		teams.remove(myTeam);
		Team otherTeam=null;
		int i=(int)Math.random()*teams.size();
		otherTeam=teams.get(i);
		if(otherTeam==null){
			return new ShoutAction("Captain No-Beard wins my default! No other Challengers!");
		}
		MoveAction movements= new MoveAction();
		List<PositionData> otherData=turn.getPositionsControlledBy(otherTeam.getColor());
		int x=(int)Math.random()*otherData.size();
		PositionData go=otherData.get(x);
		movements.addMove(0, max, data.ID, go.ID);
		return movements;
	}
}


//import bonzai.*;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//import Castles.Objects.RallyPoint;
//import Castles.Objects.SoldierState;
//import Castles.api.*;
//import Castles.util.graph.Vertex;
//
///**
// * This is the class for your AI.  It will handle all
// * Interactions with the map and the game in the form
// * of a returned action.
// * 
// * 
// * 
// * 
// * @author BonzAI Competitor
// *
// */
////put your AI's name in this section so the 
////game can tell your AI apart from the rest
//@Agent(name = "MeanAI")
//public class CompetitorAI extends AI {
//	
//	public CompetitorAI(){}
//	
//	/**
//	 * @param Turn the current state of the map that your AI will act upon
//	 * 
//	 * @return the action your AI will take
//	 * @return There are 2 possible return classes, the ShoutAction and the 
//	 * 		   Move action.  The shout action just puts text on the screen 
//	 * 		   at the location of your AI's original castle, the move action
//	 * 		   will contain a list of movements that your troops will perform
//	 * 		   with the inputed game state
//	 */
//	public Action action(Turn turn) {
//		//put your AI's stuff here
//		Random generator = new Random( System.currentTimeMillis() );
//		int val = generator.nextInt(3);
//		
//		if (val == 2) {
//			return new MoveAction(0, 0, new ArrayList<String>());
//			
//		} else if (val == 1) {
//			return new UpdateAction("[ID]", 0, SoldierState.STANDBY);	
//		}
//		
//		return new ShoutAction("I am shouting!");
//	}
//	
//}
