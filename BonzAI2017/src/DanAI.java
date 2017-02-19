import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Captain No-Beard")
public class DanAI extends AI {

	Team myTeam;
	@Override
	public Action action(Turn turn) {
			Team myTeam=turn.getMyTeam();
		if(myTeam==null){
			return new ShoutAction("No team? BUG!");
		}
		TeamColor t=myTeam.getColor();
		if(t==null){
			return new ShoutAction("No color? BUG!");
		}
		int numSoldiers=0;
		List<Team> teams=turn.getAllTeams();
		teams.remove(myTeam);
		List<PositionData> myPositions =turn.getPositionsControlledBy(t);
		MoveAction movements= new MoveAction();
		for(PositionData p:myPositions){
			int max=0;
			for(int i=0;i<p.occupantSizes.length;i++){
				numSoldiers++;
				max+=p.occupantSizes[i];
			}
			Team otherTeam=null;
			int i=(int)Math.random()*teams.size();
			otherTeam=teams.get(i);
			if(otherTeam==null){
				return new ShoutAction("Captain No-Beard wins my default! No other Challengers!");
			}
			List<PositionData> otherData=turn.getPositionsControlledBy(otherTeam.getColor());
			int x=(int)Math.random()*otherData.size();
			PositionData go=otherData.get(x);
			for(int j=0;j<p.occupantSizes.length;j++){
				movements.addMove(j, max, p.ID, go.ID);
			}
		}
		if(numSoldiers<=0){
			return new ShoutAction("I be Captain No-Beard!");
		}
		return movements;
	}
}