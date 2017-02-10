import java.util.List;

import Castles.api.*;
import bonzai.*;

@Agent(name ="Captain No-Beard")
public class DanAI extends AI {
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
		List<Team> teams=turn.getAllTeams();
		Team otherTeam=null;
		while(true){
			int i=(int)Math.random()*teams.size();
			if(!teams.get(i).equals(myTeam)){
				otherTeam=teams.get(i);
				break;
			}
		}
		if(otherTeam==null){
			return new ShoutAction("Captain No-Beard wins my default! No other Challengers!");
		}
		List<PositionData> otherData=turn.getPositionsControlledBy(otherTeam.getColor());
		int i=(int)Math.random()*otherData.size();
		PositionData go=otherData.get(i);
		List<String> path=turn.getPath(data.ID,go.ID);
		return new MoveAction(0,max,path);
	}

}
