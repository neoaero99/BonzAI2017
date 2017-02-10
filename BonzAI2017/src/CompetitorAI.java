import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Captain No-Beard")
public class CompetitorAI extends AI {

	@Override
	public Action action(Turn turn) {
		Team myTeam=turn.getMyTeam();
		List<PositionData> myPositions =turn.getPositionsControlledBy(myTeam.getColor());
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
			}
		}
		if(max<=0){
			return new ShoutAction("I be Captain No-Beard!");
		}
		
		return new MoveAction();
	}
}