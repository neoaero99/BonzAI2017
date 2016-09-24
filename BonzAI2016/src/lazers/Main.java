package lazers;

import bonzai.gui.*;

import java.util.*;

import java.io.*;

//TODO 2017: Change references to "lazers" and "Lazers" to the title of your new game!
//Specifics of what should be in these files are in comments within the files themselves.

public class Main {
	//Instantiate your game thread instance
	public static Lazers lazers = new Lazers();

	/**
	 * Start up a game that keeps track of the simulation and history states of the game.
	 * 
	 * If command line arguments were specified, we know which map/AI's we want to run.
	 * This method is used by the simulation runner to determine the 
	 */
	public static void startHeadless(Queue<String> arguments) throws Exception {
		bonzai.Scenario scenario = lazers.scenario(new File(arguments.poll()));
		
		List<bonzai.Jar> jars = new ArrayList<>();
		
		//TODO 2017: Change the "6" to the number of AI's that you want to support at once.
		while(!arguments.isEmpty() && jars.size() < 6) {
			String arg = arguments.poll();
			jars.add("null".equals(arg.toLowerCase()) ? null : lazers.jar(new File(arg)));
		}
		
		lazers.run(scenario, jars);
		
		Simulation simulation = lazers.simulation();
		while(simulation.isAlive()) {
			try { simulation.join(); } catch(InterruptedException e) { }
		}
		
		simulation.join();
	}
	
	/**
	 * Start up a game that doesn't keep track of the history states. Shouldn't need to
	 * worry too much about this except for changing the game names.
	 * 
	 * If command line arguments are specified, we know which map/AI's we want to run.
	 */
	public static void startHeadfull(Queue<String> arguments) throws Exception {
		bonzai.Scenario scenario = lazers.scenario(new File(arguments.poll()));
		
		List<bonzai.Jar> jars = new ArrayList<>();
		
		//TODO 2017: Change the "6" to the number of AI's that you want to support at once.
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
	
	/**
	 * Start up the game instance that controls the AI picker and spin up the GUI.
	 */
	public static void startGUI(Queue<String> arguments) throws Exception {
		new Thread(lazers).start();
		BonzAIFrame.create("Lazers", lazers);
	}

	/**
	 * Grab arguments if running from command line. Then figure out which
	 * "starter" we want to call.
	 */
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