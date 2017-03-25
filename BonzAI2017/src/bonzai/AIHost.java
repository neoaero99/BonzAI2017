package bonzai;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import Castles.Objects.SoldierState;
import Castles.api.MoveAction;
import Castles.api.MoveSoldier;
import Castles.api.ShoutAction;

public class AIHost {
	private final Process process;
	private boolean isAlive;
	
	private final Scanner err;
	private final Scanner in;
	private final PrintStream out;
	final int teamID;
	
	/**
	 *
	 **/
	public AIHost(Process process, int teamID) {
		this.process = process;
		this.isAlive = true;
		this.teamID = teamID;
		
		this.err = new Scanner(process.getErrorStream());
		this.in  = new Scanner(process.getInputStream());
		this.out = new PrintStream(process.getOutputStream());
		
		expects("ACK");
	}
	
	/**
	 * The simulation is sending a single action that
	 * occured doing the last turn to the AI
	 * 
	 * @param action the action that occured in the previous turn
	 */
	public void apply(Action action) {
		if(isAlive) {
			out.printf("APPLY 1%n%s%n", AIHost.toString(action)); 
			out.flush();
			
			expects("ACK");
		}
	}
	
	/**
	 * The simulation is sending a list of actions that occured
	 * durring the last turn to the AI
	 * 
	 * @param actions the list of actions that occured during the last turn
	 */
	public void apply(List<Action> actions) {
		if (isAlive) {
			//tells the AI how many actions to expect
			out.printf("APPLY %d\n", actions.size());
			
			//sends each action individually
			for (Action a : actions){
				out.printf("%s\n", AIHost.toString(a));
			}
			
			out.flush();//might be problem causing
			expects("ACK");//waits for the AI to send an acknowlagment
		}
	}
	
	/**
	 * Asks the AI for its next move
	 * 
	 * @return the action that the AI intends to make
	 */
	public Action query() {
		if(isAlive) {
			//sends the QUERY command to the AI
			out.println("QUERY");
			out.flush();
			String response = expects("(?s).*");
			//if the action returned was null, return null
			//otherwise return the parsed action
			Action action = response == null ? null : AIHost.toAction(response);
			return action;
		}
		//return null if the AI is dead
		return null;
	}
	
	/**
	 * terminates the AI thread
	 */
	public void terminate() {
		if(isAlive) {
			out.println("TERMINATE");
			out.flush();
		
			expects("ACK");
		}
	}
	
	/**
	 * gets the team ID for the AI that this AIHost represents
	 * @return
	 */
	public int getTeam() {
		return teamID;
	}
	
	/**
	 * checks the response from AIClient to see if it matches the expected
	 * response
	 * nukes process if the response doesn't match
	 * 
	 * @param patteren A regular expression to define the set of expected responses
	 * @return the response of the AI
	 */
	private String expects(String pattern) {
		try { 
			//waits for the AIClient to send its message
			//while(!in.hasNextLine()){}
			//reads in the AI's response
			String response = in.nextLine();
			//Prints out to the command line the regular expression
			//of expected responses and the Response of the AI
			System.out.println("Pattern = " + pattern);
			System.out.println("Response = " + response);
			//checks if the response is in the language defined by the
			//user defined regular expression
			if (!response.matches(pattern)) { 
				throw new IOException("Inappropriate response given.  Expected pattern " 
										+ pattern + " but got " + response); 
			}
			return response;
			
		} catch(Exception e) {
			//just error tracking stuff
			//if there happened to be an error in
			//the response, the method throws an IOException
			e.printStackTrace(); 
//			String error=err.nextLine();
//			System.out.println(error);
			isAlive = false;
			process.destroy();
			System.err.print("Expected pattern: " + pattern + ". The process has been nuked!\n");
			
		}
		
		return null;
	}
	
	/**
	 * converts a message to an action
	 * @param message the message from the AI that needs to be parsed
	 * @return the parsed action
	 */
	public static Action toAction(String message) {
		Scanner arguments = new Scanner(message);
		String args;
		//uses a state machine to select the correct action
		//this is where you put your actions so that the
		//program can execute AI commands
		switch(arguments.next()) {

			case "MOVE":
				MoveAction action = new MoveAction();
				int moveNum = arguments.nextInt();
				
				for (int idx = 0; idx < moveNum; ++idx) {
					String sym = arguments.next();
					
					if (sym.equals("PATH")) {
						// Parse a soldier move action
						action.addMove(arguments.nextInt(),
								arguments.nextInt(), arguments.next(),
								arguments.next());
						
					} else if (sym.equals("STATE")) {
						// Parse an update soldier action
						int sIdx = arguments.nextInt();
						String pID = arguments.next();
						String stateName = arguments.next();
						SoldierState state = SoldierState.STANDBY;
						// parse the soldier state based on its name
						if (stateName.equals("MOVING")) {
							state = SoldierState.MOVING;	
						}
						
						action.addUpdate(sIdx, pID, state);
					}
				}
				
				arguments.close();
				return action;
				
			case "SHOUT":
				args = arguments.nextLine();
				arguments.close();
				//Fun banter command that is a staple of BonzAI
				//this shouldn't need to be touched and can
				//be used to help debug the process of AI communication
				//because if you are having problems with a written
				//command for your BonzAI brawl, it works best to have
				//a command that you can be guaranteed works.  If it
				//doesn't work, there is likely a communication problem
				//between the AIHost and AIClient classes
				return new ShoutAction(args);
				
			default:
				arguments.close();
				//if the message is an unidentified action,
				//the program a shout action that tells the user
				//the done messed up
				return new ShoutAction("I tried to do something that I cannot do");
		}
	}
	
	/**
	 * used so the AIClient can convert the action into
	 * a string to sent to the AIHost
	 * 
	 * @param action The action to be converted
	 * @return	a string that represents that action
	 */
	public static String toString(Action action) {
		//here is where you need to write your Action
		//conversion.  Make sure it follows the same pattern
		//as your toAction() method that appeared earlier
		
		//again, ShoutActions should work, use them to debug the communication
		//between the AI and the game
		if (action instanceof Action) {
			return action.toString();
			
		} else {
			return "NONE";
		}
	}
	
	public static AIHost spawn(String timestamp, bonzai.Scenario scenario, bonzai.Jar me, List<bonzai.Jar> jars, int id) throws IOException {
		List<String> command = new ArrayList<>();
		command.addAll(Arrays.asList("java", "-cp", "castles.jar", "bonzai.AIClient"));
		command.add(String.format("%s", me.name()));
		command.add(String.format("%d", id));
		command.add(scenario.getFile().getAbsolutePath());
		command.add(me.file().getAbsolutePath());
		
		for(bonzai.Jar choice : jars) { 
			command.add(choice == null ? "null" : choice.file().getAbsolutePath()); 
		}
		System.out.println(command.toString());
		return new AIHost(new ProcessBuilder(command).start(), id);
	}
}
