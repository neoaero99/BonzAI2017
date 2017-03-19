
import bonzai.*;
import java.util.*;
import Castles.Objects.*;
import Castles.api.*;
import Castles.util.linkedlist.DualLinkList;


/**
 * Dane's Test AI for the 2017 BonzAI Brawl,
 * What this AI does:
 * 	~10% chance of just taunting the whole time
 * 
 * Actual Strategy:
 *  Throw away turn 1
 *  Rush a Castle turn 2 to maximize points
 *  On each other turn:
 *  	For each of my buildings, compare the soldiers that building
 *  	contains against the defense value + soldier per turn of all
 *  	other buildings.  If the AI has enough soldiers to capture 
 *  	the point, it will send the troops to the building to capture it.
 *  	Uses a priority queue to sort the viable actions to which is the
 *  	most efficient in terms of turns to target
 *  
 *  	if there isn't any viable moves in a turn, just one can never go
 *  	wrong with some taunts, everyone loves dem taunts anyways.
 *  	
 * Things Tested By this AI:
 * 		Shout Action
 * 		Move action moving troops from one struct to another
 * 		Multiple moves per move action
 * 		The various querying methods in turn and the data wrapper classes
 * 			Full List: 
 * 				getClosest*(), getSoldiersAt(), getPath(), getMyTurn,
 * 				getSoldiersControledBy(), PositionData.isOwned, PositionData.isOwnedBy,
 * 				Soldier States, possible more
 * 	
 * 
 * 
 * @author Dane Jensen
 *
 */
@Agent(name = "ManticoreAI")
public class DaneAI extends AI {
	
	private final String[] taunts = {
			"Your Mother Was a Hampster", 
			"Your Father smelt of Elderberries", 
			"Make Castles Great Again!!!",
			"404, Your score not found",
			"Loading next move....",
			"I AM MANTICORE!!!!!!!",
			"Bad move, sad"
			};
	private final boolean taunt;
	private final boolean test = false;
	private int turnNumber = 0;
	private Turn turn;
	private ArrayList<PositionData> cStructs,uStructs,eStructs;
	
	private ArrayList<SoldierData> troops;
	private ArrayList<Path> currentMovements;

	@SuppressWarnings("unused")
	public DaneAI(){
		//decides if today is a good day to taunt
		int rand = (int)(Math.random() * 99);
		if(rand%42 == 0 || rand%69 == 0 || rand%17 == 0 || test){
			taunt = true;
		}else{
			taunt = false;
		}
	}

	
	public Action action(Turn turn) {

		this.turn = turn;
		Team MyTeam = turn.getMyTeam();
		++turnNumber;
		//on turn one, assert my dominance with a taunt,
		//or if I just feel like taunting today
		if(turnNumber == 1 || taunt){
			return new ShoutAction(taunts[(int)(Math.random()*taunts.length)]);
		}
		MoveAction move = new MoveAction(); //the move for the turn
		//Data Structures to sort the state of all buildings
		//on the map
		cStructs = new ArrayList<>();
		uStructs = new ArrayList<>();
		eStructs = new ArrayList<>();
		//a list of my troops
		troops = new ArrayList<>();
		//a queue for every move I can make
		PriorityQueue<Path> possibleMoves = new PriorityQueue<>();
		
		//sort the buildings on the map for the current state
		for(PositionData p: turn.getAllElements()){
			if(p.ID.contains("R")) continue;
			if(p.isControledBy(MyTeam.getColor())){
				cStructs.add(p);
				continue;
			}
			if(!p.isControled()){
				uStructs.add(p);
				continue;
			}
			eStructs.add(p);
		}
		
		
		//second turn send all my troops to the nearest castle
		if(turnNumber == 2){
			ArrayList<SoldierData> s = turn.getSoldiersAt(cStructs.get(0).ID);
			SoldierData bob = null;
			for(SoldierData glen : s){
				if(glen.state == SoldierState.STANDBY){
					bob = glen;
					break;
				}
			}
			move.addMove(bob.sIdx, bob.size, cStructs.get(0).ID, turn.getClosestCastle(cStructs.get(0)).ID);
			return move;
		}
		//get all of my soldiers
		for(SoldierData s: turn.getSoldiersControlledBy(MyTeam.getColor())){
			troops.add(s);
		}
		
		//get all moves I can make in a turn
		for(int size = 0; size < 1;){
			possibleMoves = getPossibleMoves();
			size = possibleMoves.size();
			if(size == 0) break;
			currentMovements.add(possibleMoves.poll());
		}
		if(currentMovements.size() == 0){
			return new ShoutAction(taunts[(int)(Math.random()*taunts.length)]);
		}
		
		//add all moves
		for(Path p : currentMovements){
			SoldierData JohnLucPicard = null;
			for(SoldierData s : turn.getSoldiersAt(p.from.ID)){
				if(s.state == SoldierState.STANDBY){
					JohnLucPicard = s;
				}
			}
			move.addMove(JohnLucPicard.sIdx, p.soldiersCommited, p.to.ID, p.from.ID);
		}
		
		return move;
		
	}
	
	@SuppressWarnings("unchecked")
	private PriorityQueue<Path> getPossibleMoves() throws ClassCastException{
		PriorityQueue<Path> pm = new PriorityQueue<>();
		//YAY!!!! ORDER N^2 ALGORITHM
		for(PositionData owned : cStructs){
			if(!(getSoldierCount(owned) > Turn.VILLAGE_INIT)) continue;
			//finds the list of turns I can use for un-owned buildings
			//from each owned buildings
			for(PositionData unowned : uStructs){
				if(getSoldierCount(owned) > getSoldierCount(unowned) + 1){
					DualLinkList<String> temp = (DualLinkList<String>) turn.getPath(owned.ID, unowned.ID);
					pm.add(new Path(temp, temp.size(),unowned,owned,getSoldierCount(unowned) +1));
				}
			}
			
			//finds the list of turns I can use for un-owned buildings
			//from each owned building
			for(PositionData eowned : eStructs){
				DualLinkList<String> temp = (DualLinkList<String>) turn.getPath(owned.ID, eowned.ID);
				int count = getSoldierCount(eowned);
				count += getTroopGain(eowned) * temp.size();
				if(getSoldierCount(owned) > count + 1){
					pm.add(new Path(temp, temp.size(),eowned,owned, getSoldierCount(eowned) +1));
				}
			}
		}
		return pm;
	}
	
	private int getTroopGain(PositionData p){
		char type = p.ID.charAt(0);
		switch(type){
		case 'C':
			return Turn.CASTLE_PER_TURN;
		case 'V':
			return Turn.VILLAGE_PER_TURN;
		default:
			return 0;
		}
	}
	
	private int getSoldierCount(PositionData p){
		ArrayList<SoldierData> soldiers = turn.getSoldiersAt(p.ID);
		SoldierData Josh = null;
		int count = 0;
		for(SoldierData david : soldiers){
			if(david.state == SoldierState.STANDBY){
				Josh = david;
				break;
			}
		}
		count = Josh.size;
		for(Path path: currentMovements){
			if(p.ID.equals(path.from)){
				count -= path.soldiersCommited;
			}
		}
		return count;
	}
	
	private class Path implements Comparable<Path>{
		@SuppressWarnings("unused")
		DualLinkList<String> p = new DualLinkList<>();
		int length;
		PositionData to,from;
		int soldiersCommited;
		public Path(DualLinkList<String> temp, int length, PositionData unowned, PositionData owned, int comitted){
			this.p = temp;
			this.length = length;
			this.to = unowned;
			this.from = owned;
			this.soldiersCommited = comitted;
		}
		

		@Override
		public int compareTo(Path p2 ) {
			if(p2.length < length){
				return -1;
			}else if(p2.length > length){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
}
