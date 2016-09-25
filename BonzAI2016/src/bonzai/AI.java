package bonzai;

import Castles.api.Turn;

/**
 * The base class for AIs. An AI must provide a single function, action, which
 * takes a Turn object and returns the action for the current unit. The action
 * method will only be called when the current unit is a member of that AI's
 * team.
 **/
public abstract class AI {

	/**
	 * Given a turn, representing the state of the game at a fixed time, return
	 * an action for the current unit.
	 *
	 * @param turn the current turn
	 * @return the selected action
	 **/
	public abstract Action action(Turn turn);
}

// Change to a functional interface
	// It's an abstract class anyways. People can't be instantiating it