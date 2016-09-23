package lazers;

import bonzai.*;
import bonzai.Jar;
import bonzai.gui.*;

import java.util.*;

import javax.swing.JPanel;

import static bonzai.util.Utility.*;

import java.awt.Color;
import java.io.*;

import lazers.api.*;
import lazers.api.util.*;

public class Main {
	public static Lazers lazers = new Lazers();

	public static void startHeadless(Queue<String> arguments) throws Exception {
		bonzai.Scenario scenario = lazers.scenario(new File(arguments.poll()));
		
		List<bonzai.Jar> jars = new ArrayList<>();
		while(!arguments.isEmpty() && jars.size() < 6) {
			String arg = arguments.poll();
			jars.add("null".equals(arg.toLowerCase()) ? null : lazers.jar(new File(arg)));
		}
		
		lazers.run(scenario, jars);
		
		Simulation simulation = lazers.simulation();
		while (simulation.isAlive()) {
			try { simulation.join(); } catch(InterruptedException e) { }
		}
		simulation.join();
		
		Turn fin = simulation.turn(simulation.totalFrames() - 1);
		
		// this only works because of Entity.id shenanigans, but so what?
		for(Team team : order(fin.getTeams(), new TeamScore())) {
			System.out.println(simulation.jar(team.getColor()).name() + " " + team);
		}
	}
	
	public static void startHeadfull(Queue<String> arguments) throws Exception {
		bonzai.Scenario scenario = lazers.scenario(new File(arguments.poll()));
		
		List<bonzai.Jar> jars = new ArrayList<>();
		while(!arguments.isEmpty() && jars.size() < 6) {
			String arg = arguments.poll();
			jars.add("null".equals(arg.toLowerCase()) ? null : lazers.jar(new File(arg)));
		}
	
		lazers.run(scenario, jars);
		
		new Thread(lazers).start();
		BonzAIFrame.createGameOnly("Lazers", lazers);
		
		//Start it up! (Full screen, full speed, auto play)
		lazers.enableAutoStart();
	}

	public static void startGUI(Queue<String> arguments) throws Exception {
		new Thread(lazers).start();
		BonzAIFrame.create("Lazers", lazers);
	}

	@SuppressWarnings("fallthrough")
	public static void main(String[] args) throws Exception {
		Queue<String> arguments = new ArrayDeque<>(Arrays.asList(args));

		if(arguments.isEmpty()) { arguments.add("-gui"); }
		
		switch(arguments.peek().toLowerCase()) {
			case "-headless":
				arguments.poll();
				startHeadless(arguments);
				return;
				
			case "-run":
				arguments.poll();
				startHeadfull(arguments);
				return;
				
			case "-gui": arguments.poll();
			default    : startGUI(arguments);
		}
	}
}