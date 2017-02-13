

import bonzai.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import Castles.Objects.SoldierState;
import Castles.api.*;
import bonzai.*;

@Agent(name ="Test")
public class CompetitorAI extends AI {

	Team myTeam;
	@Override
	public Action action(Turn turn) {

		//put your AI's stuff here
		Random generator = new Random( System.currentTimeMillis() );
		int val = generator.nextInt(3);
		
		if (val == 2) {
			MoveAction a = new MoveAction();
			a.addMove(0, 2, "P0", "P1");
			return a;
			
		} else if (val == 1) {
			MoveAction a = new MoveAction();
			a.addUpdate(0, "P0", SoldierState.STANDBY);
			return a;	
		}
		Team myTeam=turn.getMyTeam();
		List<PositionData> myPositions =turn.getPositionsControlledBy(myTeam.getColor());
		if(myTeam==null){
			return new ShoutAction("No team? BUG!");
		}
		Color t=myTeam.getColor();
		if(t==null){
			return new ShoutAction("No color? BUG!");
		}

		int numSoldiers=0;
		List<Team> teams=turn.getAllTeams();
		teams.remove(myTeam);
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
