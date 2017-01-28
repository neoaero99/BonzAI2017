package Castles.api;

import java.util.ArrayList;

import javax.swing.text.html.parser.Element;

import Castles.Objects.*;
import Castles.util.graph.Vertex;
import bonzai.Action;

/**
 * The main action for AI's to take in the castles game for BonzAI 2017
 * 
 * 
 * Usage: Create a MoveAction at the beginning of an AI's Turn </br>
 * 		  Have AI add all the soldier movements it would like </br>
 * 		  to take in its turn.</br>
 * 		  Return the MoveAction at the end of the AI's Turn</br>
 * 
 * 
 * @author Dane Jensen
 * @author Team Secret (2016 - 2017)
 */
public class MoveAction implements Action{
	private static CastlesMap map;
	ArrayList<Movement> moves = new ArrayList<Movement>();
	
	
	private class Movement{
		Soldier s;
		RallyPoint target;
		int splitAmount;
		
		public Movement(Soldier s, RallyPoint target, int splitAmount){
			this.s = s;
			this.target = target;
			this.splitAmount = splitAmount;
		}
		
		@Override
		public String toString(){
			return "[ " + s.getName() + " " + target.ID + " " + splitAmount +" ]";
		}
	}
	
	/**
	 * a constructor that does nothing
	 */
	public MoveAction(){}
	
	/**
	 * Adds a movement to an AI's move action based on string
	 * Input, this function has no use for the AI and will function
	 * exactly like the other 2 addMovement functions
	 * 
	 * @param sold the name of the soldier to be moved
	 * @param target the soldier's target
	 * @param splitAmount the amount of unit strength to be split off
	 */
	public void addMovement(String sold, String target, int splitAmount){
		Soldier s = null;
		RallyPoint t = null;
		ArrayList<RallyPoint> elements = map.getAllElements();
		
		for(RallyPoint r : elements) {
			if(r.ID.equals(target)){
				t = r;
				break;
			}
		}
		
		for(ArrayList<Soldier> soldiers : map.getSoldiers()) {
			for (Soldier o : soldiers) {
				if(o.getName().equals(sold)){
					s = o;
					break;
				}
			}
		}
		
		moves.add(new Movement(s, t, splitAmount));
	}
	
	/**
	 * Adds a movement to the move action of an AI
	 * 
	 * 
	 * @param sold the soldier to be moved
	 * @param target the soldier's target
	 */
	public void addMovement(Soldier sold, RallyPoint target){
		moves.add(new Movement(sold, target,sold.getValue()));
	}
	
	/**
	 * Adds a movement to the move action for an AI
	 * 
	 * 
	 * @param sold the soldier to be moved
	 * @param target the soldier's target
	 * @param splitAmount the amount of unit strength being split off
	 */
	public void addMovement(Soldier sold, RallyPoint target, int splitAmount){
		moves.add(new Movement(sold, target, splitAmount));
	}
	
	@Override
	public String toString(){
		String out = "MOVE ";
		for(Movement m: moves){
			out += m.toString() + " ";
		}
		return out;
	}
	
}
