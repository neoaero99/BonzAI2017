package lazers;

import java.awt.Color;
import java.util.*;

import bonzai.Action;
import bonzai.Position;
import lazers.api.LazersMap;
import lazers.api.Turn;


/**
 * A representation of a state of the game board
 **/
public class Game {
	private final List<Turn> history;
	private int remaining;
	private LazersMap map;

	/**
	 *
	 **/
	public Game(int turns, LazersMap map, List<lazers.api.Color> colors, int id) {
		this.remaining = turns;
		this.map = map;
		this.history = new ArrayList<Turn>(turns);
		this.history.add(new Turn(0, map, id));	//Create the first turn of the game
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
	 *
	 **/
	public boolean isValid(Action action) {
		return turn().isValid(action);
	}

	public void apply(Action action) {
		ArrayList<Action> list = new ArrayList<Action>(1);
		list.add(action);
		apply(list);
	}

	// Apply a list of actions
	public void apply(List<Action> actions) {
		Turn curr = turn();
		history.add(curr.apply(actions));
		remaining -= 1;
	}
}