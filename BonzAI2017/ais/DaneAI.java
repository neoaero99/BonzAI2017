
import bonzai.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import Castles.Objects.*;
import Castles.api.*;
import Castles.util.graph.Vertex;
import Castles.util.linkedlist.DualLinkList;


/**
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
			"I AM MANTICORE!!!!!!!"};
	private final boolean taunt = true;
	private int turnNumber = 0;
	private Turn turn;
	private ArrayList<PositionData> cStructs,uStructs,eStructs;
	
	private ArrayList<Troops> troops;
	private ArrayList<Path> currentMovements;

	public DaneAI(){	}

	
	public Action action(Turn turn) {

		this.turn = turn;
		Team MyTeam = turn.getMyTeam();
		++turnNumber;
		if(taunt || turnNumber == 1){
			return new ShoutAction(taunts[(int)(Math.random()*taunts.length)]);
		}
		//MoveAction move = new MoveAction();
		cStructs = new ArrayList<>();
		uStructs = new ArrayList<>();
		eStructs = new ArrayList<>();
		troops = new ArrayList<>();
		PriorityQueue<Path> possibleMoves = new PriorityQueue<>();
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
		
		for(SoldierData s: turn.getSoldiersControlledBy(MyTeam.getColor())){
			troops.add(new Troops(s.size, s.posID));
		}
		//second turn send all my troops to the nearest castle
		//TODO FIX THIS SHIT
		if(turnNumber == 2){
			//SoldierData s = turn.getSoldierAt(cStructs.get(0).ID);
			//s = map.splitSoliders(s, 210, turn.createPath(
			//		cStructs.get(0)), turn.getClosestCastle(cStructs.get(0)));
			//move.addMovement(s, turn.getClosestCastle(cStructs.get(0)));
			//return move;
					return null;
		}
		
		for(int size = 0; size < 1;){
			possibleMoves = getPossibleMoves();
			size = possibleMoves.size();
			if(size == 0) break;
			currentMovements.add(possibleMoves.poll());
		}
		//TODO FIX TH15 S41T
		for(Path p : currentMovements){
			//move.addMovement(turn.getSoldierAt(p.from), p.to, p.soldiersCommited);
		}
		return null;
		//return move;
		
		//TODO
		/***********************************************************************
		 * Methods We need to add											   *
		 * 	CastlesMap.getSoldierAt(RallyPoint)								   *
		 * 	Building.getControlingTeam()        							   *
		 * 	Building.isControledBy(a team)								       *
		 *  Building.isControled()										       *
		 * 	CastlesMap.getSoldierAt(RallyPoint)	Done							   *
		 * 	RallyPoint.getControlingTeam()      in Building, not RallyPoint, RallyPoints are not controlled  							   *
		 * 	RallyPoint.isControledBy(a team)	Ditto /\							   *
		 *  RallyPoint.isControled()			Ditto							   *
		 * 	CastlesMap.getClosestCastle(RallyPoint)							   *
		 * 	CastlesMap.getClosestVillage(RallyPoint)						   *
		 * 	CastlesMap.updateSoldiers(); THIS SHOULD BE PRIVATE				   *
		 *  CastlesMap.createPath(From, To)		Done							   *
		 *  CastlesMap.getSoldiers(a team)		Done						   *
		 *  Soldier.getRallyPoint()				Done							   *
		 *  Put the inital Values in CastlesMap as static finals			   *
		 *  Put the per turn values in the Castles map as static finals        *
		 ***********************************************************************/
		//return new ShoutAction("");
	
	}
	
	private PriorityQueue<Path> getPossibleMoves(){
		/*PriorityQueue<Path> pm = new PriorityQueue<>();
		//YAY!!!! ORDER N^2 ALGORITHM
		for(PositionData owned : cStructs){
			if(!(getSoldierCount(owned).value > turn.VILLAGE_INIT)) continue;
			for(PositionData unowned : uStructs){
				if(getSoldierCount(owned) > turn.getSoldierAt(unowned) + 1){
					DualLinkList<RallyPoint> temp = createPath(owned, unowned);
					pm.add(new Path(temp, temp.size(),unowned,owned,turn.getSoldierAt(unowned) +1));
				}
			}
			for(PositionData eowned : eStructs){
				DualLinkList<RallyPoint> temp = createPath(owned, eowned);
				int count = getSoldierAt(eowned);
				count += getTroopGain(eowned) * temp.size();
				if(getSoldierCount(owned) > count + 1){
					pm.add(new Path(temp, temp.size(),eowned,owned,turn.getSoldierAt(eowned) +1));
				}
			}
		}
		return pm;*/return null;
	}
	
	private int getTroopGain(RallyPoint p){
		if(p instanceof Castle){
			return turn.CASTLE_PER_TURN;
		}else if(p instanceof Village){
			return turn.VILLAGE_PER_TURN;
		}else{
			return 0;
		}
	}
	
	private int getSoldierCount(RallyPoint p){
		/*int count = turn.getSoldierAt(p);
		for(Path path: currentMovements){
			if(p == path.from){
				count -= path.soldiersCommited;
			}
		}
		return count;*/return 0;
	}
	
	private int getEnemySoldierCount(RallyPoint p){
	//	int count = turn.getSoldierAt(p);
		return 0;
	}
	
	private class Troops{
		int strength;
		String location;
		public Troops(int strength, String location){
			this.strength = strength;
			this.location = location;
		}
	}
	
	private class Path implements Comparable<Path>{
		DualLinkList<RallyPoint> p = new DualLinkList<>();
		int length;
		RallyPoint to,from;
		int soldiersCommited;
		public Path(DualLinkList<RallyPoint> p, int length, RallyPoint to, RallyPoint from, int comitted){
			this.p = p;
			this.length = length;
			this.to = to;
			this.from = from;
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