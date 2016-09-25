package Castles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import bonzai.AIHost;
import bonzai.Action;
import bonzai.Team;
import Castles.api.CastlesScenario;
import Castles.api.*;

/**
 * A Simulation holds a history state of how a game played out. It keeps a 
 * Game object that holds references to the state of the Map at each turn.
 * In addition, the Simulation keeps a list of actions that were applied. 
 **/
public class Simulation extends Thread {
	private final Queue<AIHost> clients;
	private final Map<Color, bonzai.Jar> jars;
	private final Game game;
	private final List<Action> actions;
	private Castles.api.Color winningTeam;
	int numTurns;
	
	/**
	 * Constructor 
	 */
	public Simulation(bonzai.Scenario scenario, List<bonzai.Jar> jars) throws Exception {
		String timestamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
		
		this.clients = new LinkedList<AIHost>();
		this.jars = new HashMap<Color, bonzai.Jar>();
		int team = 0;
		
		//For each AI jar that is in the game, make an AIHost thread for it.
		for(bonzai.Jar jar : jars) {
			if(jar != null) {
				this.clients.add(AIHost.spawn(timestamp, scenario, jar, jars, team));
			}
			this.jars.put(Color.values()[team++], jar);
		}
		
		//Initialize the game with the required jars and scenario.
		//Effectively, this makes the game at it's initial point, before anyone has taken an action.
		this.game = ((Castles.api.CastlesScenario)scenario).instantiate(jars);
		
		this.actions = new ArrayList<Action>();
	}
	
	/**
	 * Get the winning team
	 */
	public Color getWinner() {
		return winningTeam;
	}

	/**
	 * Return the team color associated with a specific AI's jar.
	 */
	public bonzai.Jar jar(Castles.api.Color color) {
		return jars.get(color);
	}
	
	/**
	 * Has the effect of returning the number of turns taken thus far.
	 * In the playback renderer, this is used to determine how far ahead 
	 * we can fast-forward 
	 */
	public int availableFrames() {
		return actions.size();
	}
	
	/**
	 * Return the total number of turns. Each "frame" is considered
	 * to be the state of the game at that turn. 
	 */
	public int totalFrames() {
		return game.turns();
	}
	
	/**
	 * Return the turn object at a specified turn, if that turn is between
	 * 0-game.turns(). If it isn't, return the last turn in the history. 
	 */
	public Turn turn(int current) {
		return current >= game.turns() ? game.turn() : game.turn(current);
	}
	
	/**
	 * Return the action taken at a specified turn. If the turn is not between
	 * 0-game.turns(), return the last turn in the history.
	 */
	public Action action(int current) {
		return actions.get(current >= actions.size() ? actions.size() - 1 : current);
	}
	
	/**
	 * Run the game!
	 */
	@Override
	public void run() {
		while(game.remaining() > 0) {
			//Handle concurrent turns - i.e., everyone takes their action at the same time.
			if (CastlesScenario.CONCURRENT_TURNS) {
				List<bonzai.Action> acts = new LinkedList<>();
				
				for (AIHost client : clients) {
					acts.add(client.query());
				}

				game.apply(acts);
				
				//Tell all the clients to apply the actions
				//that happened this turn.
				for(AIHost client : clients) {
					client.apply(acts);
				}
				
				for (Action a : acts) {
					actions.add(a);
				}
				
			} else { //turn-based game. Each AI takes its turn independent of other AI's
				
				List<bonzai.Action> acts = new LinkedList<>();
				Action act = clients.peek().query();
				
				acts.add(act);
				
				game.apply(acts);
				
				//Tell all the clients to apply the actions
				//that happened this turn.
				for(AIHost client : clients) {
					client.apply(acts);
				}
				
				actions.add(act);
				
				clients.offer(clients.poll());
			}
		}
		
		//The game is over, clean up all the clients.
		for(AIHost client : clients) {
			client.terminate();
		}
		
		//Set the winner of the game!
		Team winner = null;
		for (Team team : game.turn(game.turns()-1).getAllTeams()) {
			if (winner == null || winner.getScore() < team.getScore()) {
				winner = team;
			}
		}
		this.winningTeam = winner.getColor();
	}
}
