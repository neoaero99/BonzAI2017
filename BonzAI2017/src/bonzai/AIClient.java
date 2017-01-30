package bonzai;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import bonzai.util.ProcessCPUTimer;
import bonzai.util.TimeoutException;
import Castles.*;
import Castles.api.*;
import Castles.util.*;

/**
 * The AI Client class the used as a sort of wrapper for a
 * Competitor's AI.  It works by Implementing a [channel type] Channel
 * to communicate between the AI thread and the AIHost thread.  While the
 * AIHost is what handles all the talking to the actual game, the AIClient
 * is what handles talking to the competitor's AI.
 * 
 * Realistically, the only thing that you should have to do with this class
 * is change the Scenario reference to reflect your game year
 * 
 * @author Team Secret
 */
public class AIClient implements Runnable {
	private final AI ai; //the AI that this client will represent
	private final Game game; //the refrence to the game

	public static final PrintStream err = System.err;
	public static final InputStream in  = System.in;
	public static final PrintStream out = System.out;

	
	/**
	 *	a basic constructor, just sets some class variables
	 **/
	public AIClient(CastlesScenario scenario, AIJar me, List<AIJar> jars) throws Exception {
		this.ai = me.instantiate();
		this.game = scenario.instantiate(jars);
	}
	

	
	@Override
	public void run() {
		//tell the AIHost that we are ready to begin.
		AIClient.out.println("ACK");
		AIClient.out.flush();
		
		//create a scanner to read the channel between the
		//AIHost and the AIClient
		Scanner scanner = new Scanner(AIClient.in);
		while(true) {
			String command   = scanner.next();
			String arguments = scanner.nextLine().trim();
			//a state machine to handle the different commands that the
			//AIHost could send the AIClient
			switch (command) {
				case "APPLY":
					List<Action> actions = new LinkedList<>();
					
					for (int i = 0; i != Integer.parseInt(arguments); ++i)
						actions.add(AIHost.toAction(scanner.nextLine()));

					AIClient.out.println("ACK");
					AIClient.out.flush();
					game.apply(actions);
					break;
					
				case "QUERY":
					Action action = null;
					
					try { 
						//timer is the ai
						//creates a new AI each turn
						//this is why the AI needs to reestablish its
						//variables each iteration
						AIThread timer = new AIThread();
						//sets the timeout and polling for the thread
						ProcessCPUTimer.execute(timer, 500, 100);
						
						action = timer.action;
						if(timer.exception != null)
							timer.exception.printStackTrace();
						
					} catch(TimeoutException e) {
						action = new ShoutAction("Timed OUT yo");
						System.err.println("Timed Out!");
					}
					
					AIClient.out.println(AIHost.toString(action));
					AIClient.out.flush();
					break;
					
				case "TERMINATE":
					AIClient.out.println("ACK");
					AIClient.out.flush();
					scanner.close();
					return;
					
				default:
					//err.println("Unexpected Message Recieved");
					break;
			}
		}
	}
	
	/**
	 *
	 **/
	public static void main(String[] args) {
		AIClient client = null;
		try {
			// process arguments as a queue, obviously
			Queue<String> arguments = new LinkedList<>(Arrays.asList(args));

			// the first argument should always be the output id for the 
			// client, representing the match and client name
			String id = arguments.poll();
			int teamID = Integer.parseInt(arguments.poll());
			
			// hurry and reassign System.{err,in,out} to use files, where 
			// appropriate, using the output id for file names
			System.setErr(new PrintStream(new File(id + teamID + ".err")));
			System.setIn (null);
			System.setOut(new PrintStream(new File(id + +teamID + ".out")));
	
			// the next two arguments should always be the scenario, and then
			// the path to the AI I should be running
			CastlesScenario scenario = new CastlesScenario(new File(arguments.poll()),teamID);
			AIJar me = new AIJar(new File(arguments.poll()));
			// there should be exactly six remaining arguments, corresponding
			// to the six jars which have been chosen or omitted for the match
			List<AIJar> jars = new ArrayList<>();
			for(String arg : arguments) {
				jars.add(arg.equals("null") ? null : new AIJar(new File(arg)));
			}

			// Initiate the client
			client = new AIClient(scenario, me, jars);	
		}
		catch(Exception e) {
			// if anything happened in our initialization, that would be bad,
			// so we should tell the host
			AIClient.out.println("NAK");
			AIClient.out.flush();
			
			// always try to clean up after ourselves when something goes wrong
			System.exit(1);
		}
		
		// run the client!
		client.run();
	}
	
	private class AIThread extends Thread {
		public Action action;
		public Exception exception;
		
		public AIThread() {
			this.action = null;
			this.exception = null;
		}
		
		public void run() {
			try { action = ai.action(game.turn()); }
			catch(RuntimeException e) { exception = e; }
		}
	}
}
