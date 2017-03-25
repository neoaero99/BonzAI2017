import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Castles.api.PositionData;
import Castles.api.SoldierData;
import Castles.api.TeamColor;
import Castles.api.Turn;
import bonzai.AI;
import bonzai.Action;

public class SampleAI2 extends AI {
	
	private TeamColor enemy;
	
	public SampleAI2() {
		enemy = null;
	}

	@Override
	public Action action(Turn turn) {
		// TODO
		return null;
	}
	
	private List<SoldierData> greatestSize(Turn t) {
		List<SoldierData> soldiers = t.getSoldiersControlledBy(  t.getMyTeam().getColor() );
		HashMap<Integer, List<SoldierData>> sizeToGroupMap = new HashMap<Integer, List<SoldierData>>();
		int greatest = 0;
		
		for (SoldierData s : soldiers) {
			
			if (s.size >= greatest) {
				List<SoldierData> soldiersWithSize;
				
				if (s.size > greatest) {
					greatest = s.size;
					soldiersWithSize = new ArrayList<SoldierData>();
					sizeToGroupMap.put(greatest,  soldiersWithSize);
					
				} else {
					soldiersWithSize = sizeToGroupMap.get(greatest);
				}
				
				soldiersWithSize.add(s);
			}
		}
		
		List<SoldierData> greatestGroups = sizeToGroupMap.get(greatest);
		return (greatestGroups != null) ? greatestGroups : new ArrayList<SoldierData>();
	}
	
	private List<PositionData> getOptimalBuilding(List<SoldierData> sGroups) {
		// TODO
		return null;
	} 
	
}
