package Castles;

import java.util.*;

import bonzai.Action;
import bonzai.Team;
import Castles.api.*;

/**
 * A representation of a state of the game board
 */
public class Game {
	private final List<Turn> history;
	private int remaining;
	private CastlesMap map;
	
	/**
	 * list of colors that have jars selected
	 */
	private List<Castles.api.TeamColor> activeColors;

	/**
	 *
	 **/
	public Game(int teamID, int turns, CastlesMap map, List<Castles.api.TeamColor> colors) {
		this.remaining = turns;
		this.map = map;
		this.history = new ArrayList<Turn>(turns);
		this.history.add(new Turn(teamID, 0, map, turns));	//Create the first turn of the game
	}
	
	/**
	 *
	 **/
	public int turns() {
		return history.size();
	}

	/**
	 *
	 **/
	public Turn turn() {
		return history.get(history.size() - 1);
	}

	/**
	 *
	 **/
	public Turn turn(int x) {
		return history.get(x);
	}

	/**
	 *
	 **/
	public List<Turn> history() {
		return new ArrayList<Turn>(history);
	}

	/**
	 *
	 **/
	public int remaining() {
		return remaining;
	}

	/**
	 * 2017: Unused
	 * 
	 * See verifyMoveAction()
	 **/
	public boolean isValid(Team t, Action action) {
		return turn().isValid(t, action);
	}

	// Apply a list of actions
	public void apply(List<Action> actions) {
		Turn curr = turn();
		history.add(curr.apply(actions));
		remaining -= 1;
	}
}