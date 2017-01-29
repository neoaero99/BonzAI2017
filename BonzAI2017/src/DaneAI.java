import bonzai.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import Castles.Objects.RallyPoint;
import Castles.Objects.Soldier;
import Castles.api.*;
import Castles.util.graph.Vertex;
import Castles.util.linkedlist.DualLinkList;


/**
 * 
 * @author Dane Jensen
 *
 */
@Agent(name = "DaneAI")
public class DaneAI extends AI {
	
	private final String[] taunts = {"Your Mother Was a Hampster", "Your Father smelt of Elderberries"};
	private final boolean taunt = true;
	private int turnNumber = 0;
	private Turn turn;
	private CastlesMap map;
	private ArrayList<RallyPoint> cStructs,uStructs,eStructs;
	/**
	private ArrayList<Troops> troops;
	private ArrayList<Path> currentMovements;
	/**/
	public DaneAI(){
	
	}
	
	public Action action(Turn turn) {
		/**
		this.turn = turn;
		++turnNumber;
		if(taunt || turnNumber == 1){
			return new ShoutAction(taunts[(int)(Math.random()*taunts.length)]);
		}
		map = turn.getMap();
		MoveAction move = new MoveAction();
		cStructs = new ArrayList<>();
		uStructs = new ArrayList<>();
		eStructs = new ArrayList<>();
		troops = new ArrayList<>();
		PriorityQueue<Path> possibleMoves = new PriorityQueue<>();
		for(RallyPoint p: map.getAllElements()){
			if(p.isControledBy(MyTeam)){
				cStructs.add(p);
				continue;
			}
			if(!p.isControled()){
				uStructs.add(p);
				continue;
			}
			eStructs.add(p);
		}
		
		for(Soldier s: map.getSoldiers(turn.getMyTeam())){
			troops.add(new Troops(s.getValue(), s.getRallyPoint()));
		}
		//second turn send all my troops to the nearest castle
		if(turnNumber == 2){
			Soldier s = map.getSoldierAt(cStructs.get(0));
			s = map.splitSoliders(s, 210, map.createPath(
					cStructs.get(0)), map.getClosestCastle(cStructs.get(0)));
			move.addMovement(s, map.getClosestCastle(cStructs.get(0)));
			return move;
		}
		
		for(int size = 0; size < 1;){
			possibleMoves = getPossibleMoves();
			size = possibleMoves.size();
			if(size == 0) break;
			currentMovements.add(possibleMoves.poll());
		}
		
		for(Path p : currentMovements){
			move.addMovement(map.getSoldierAt(p.from), p.to, p.soldiersCommited);
		}
		
		return move;
		
		//TODO
		/***********************************************************************
		 * Methods We need to add											   *
		 * 	CastlesMap.getSoldierAt(RallyPoint)								   *
		 * 	RallyPoint.getControlingTeam()        							   *
		 * 	RallyPoint.isControledBy(a team)								   *
		 *  RallyPoint.isControled()										   *
		 * 	CastlesMap.getClosestCastle(RallyPoint)							   *
		 * 	CastlesMap.getClosestVillage(RallyPoint)						   *
		 * 	CastlesMap.updateSoldiers(); THIS SHOULD BE PRIVATE				   *
		 *  CastlesMap.createPath(From, To)									   *
		 *  CastlesMap.getSoldiers(a team)									   *
		 *  Soldier.getRallyPoint()											   *
		 *  Put the inital Values in CastlesMap as static finals			   *
		 *  Put the per turn values in the Castles map as static finals        *
		 ***********************************************************************/
		return new ShoutAction("");
		/**/
	}
	
	/**
	private PriorityQueue<Path> getPossibleMoves(){
		PriorityQueue<Path> pm = new PriorityQueue<>();
		//YAY!!!! ORDER N^2 ALGORITHM
		for(RallyPoint owned : cStructs){
			if(!(getSoldierCount(owned).value > map.VILLAGE_INIT)) continue;
			for(RallyPoint unowned : uStructs){
				if(getSoldierCount(owned) > map.getSoldierAt(unowned) + 1){
					DualLinkList<RallyPoint> temp = createPath(owned, unowned);
					pm.add(new Path(temp, temp.size(),unowned,owned,map.getSoldierAt(unowned) +1));
				}
			}
			for(RallyPoint eowned : eStructs){
				DualLinkList<RallyPoint> temp = createPath(owned, eowned);
				int count = getSoldierAt(eowned);
				count += getTroopGain(eowned) * temp.size();
				if(getSoldierCount(owned) > count + 1){
					pm.add(new Path(temp, temp.size(),eowned,owned,map.getSoldierAt(eowned) +1));
				}
			}
		}
		return pm;
	}
	
	private int getTroopGain(RallyPoint p){
		if(p instanceof Castle){
			return CastlesMap.CASTE_SOLDIER_GAIN;
		}else if(p instanceof Village){
			return CastlesMap.VILLAGE_SOLDIER_GAIN;
		}else{
			return 0;
		}
	}
	
	private int getSoldierCount(RallyPoint p){
		int count = map.getSoldierAt(p);
		for(Path path: currentMovements){
			if(p == path.from){
				count -= path.soldiersCommited;
			}
		}
		return count;
	}
	
	private int getEnemySoldierCount(RallyPoint p){
		int count = map.getSoldierAt(p);
		
	}
	
	private class Troops{
		int strength;
		RallyPoint location;
		public Troops(int strength, RallyPoint location){
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
	
	/**/
}
