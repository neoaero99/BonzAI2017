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

import bonzai.util.ProcessCPUTimer;
import bonzai.util.TimeoutException;
import Castles.*;
import Castles.api.*;
import Castles.util.*;

public class AIClient implements Runnable {
	private final AI ai;
	private final Game game;

	public static final PrintStream err = System.err;
	public static final InputStream in  = System.in;
	public static final PrintStream out = System.out;
	
	/**
	 *
	 **/
	public AIClient(CastlesScenario scenario, AIJar me, List<AIJar> jars) throws Exception {
		this.ai = me.instantiate();
		this.game = scenario.instantiate(jars);
	}
	
	@Override
	public void run() {
		// we passed intialization, so notify host
		AIClient.out.println("ACK");
		AIClient.out.flush();
		
		Scanner scanner = new Scanner(AIClient.in);
		while(true) {
			String command   = scanner.next();
			String arguments = scanner.nextLine().trim();
			
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
						AIThread timer = new AIThread();
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
					return;
					
				default:
					//TODO: Figure out how to gracefully die
					//throw new IOException("Unexpected Message Recieved");
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
