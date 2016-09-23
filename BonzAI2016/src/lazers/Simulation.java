package lazers;

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
import bonzai.gui.*;

import java.text.*;
import java.util.*;
import java.io.*;

import lazers.api.Turn;
import lazers.api.Color;


/**
 *
 **/
public class Simulation extends Thread {
	private final Queue<AIHost> clients;
	private final Map<Color, bonzai.Jar> jars;
	private final Game game;
	private final List<Action> actions;
	private Color winningTeam;
	
	public Simulation(bonzai.Scenario scenario, List<bonzai.Jar> jars) throws Exception {
		String timestamp = new SimpleDateFormat("HH-mm-ss").format(new Date());
		
		this.clients = new LinkedList<AIHost>();
		this.jars = new HashMap<Color, bonzai.Jar>();
		int team = 0;
		
		for(bonzai.Jar jar : jars) {
			if(jar != null) {
				this.clients.add(AIHost.spawn(timestamp, scenario, jar, jars, team++));
			}
			
			this.jars.put(Color.values()[team], jar);
		}
		
		this.game = ((lazers.LazersScenario)scenario).instantiate(jars, -1);
		this.actions = new ArrayList<Action>();
	}
	
	public Color getWinner() {
		return winningTeam;
	}
	
	public bonzai.Jar jar(lazers.api.Color color) {
		return jars.get(color);
	}
	
	public int availableFrames() {
		return actions.size();
	}
	
	public int totalFrames() {
		return LazersScenario.NUM_TURNS;
	}
	
	public Turn turn(int current) {
		return current >= game.turns() ? game.turn() : game.turn(current);
	}
	
	public Action action(int current) {
		return actions.get(current >= actions.size() ? actions.size() - 1 : current);
	}
	
	@Override
	public void run() {
		while(game.remaining() > 0) {
			// Handle concurrent turns (ie. Lazers)
			if (LazersScenario.CONCURRENT_TURNS) {
				List<bonzai.Action> acts = new LinkedList<>();
				
				for (AIHost client : clients)
					acts.add(client.query());
				
				// Reduce actions
				{
					
					
				}
				
				game.apply(acts);
				
				for(AIHost client : clients)
					client.apply(acts);
				
				for (Action a : acts)
					actions.add(a);
				
			// Handle normal turn-based gameplay
			} else {
				Action action = clients.peek().query();
				
				game.apply(action);
				actions.add(action);
				
				for(AIHost client : clients)
					client.apply(action);
				
				clients.offer(clients.poll());
			}
		}
		
		for(AIHost client : clients) {
			client.terminate();
		}
		
		//Set the winner
		Team winner = null;
		for (Team team : game.turn(game.turns()-1).getTeams()) {
			if (winner == null || winner.getScore() < team.getScore()) {
				winner = team;
			}
		}
		this.winningTeam = winner.getColor();
	}
}
