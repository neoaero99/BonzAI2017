package bonzai;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
	
	public void apply(Action action) {
		if(isAlive) {
			out.printf("APPLY 1%n%s%n", AIHost.toString(action)); 
			out.flush();
			
			expects("ACK");
		}
	}
	
	// Send a list of actions
	public void apply(List<Action> actions) {
		if (isAlive) {
			out.printf("APPLY %d%n", actions.size());
			
			for (Action a : actions)
				out.printf("%s%n", AIHost.toString(a));
				
			out.flush();
			expects("ACK");
		}
	}
	
	public Action query() {
		if(isAlive) {
			out.println("QUERY");
			out.flush();
			
			String response = expects("(?s).*");
			Action action = response == null ? null : AIHost.toAction(response);
			return action;
		}
		
		return null;
	}
	
	public void terminate() {
		if(isAlive) {
			out.println("TERMINATE");
			out.flush();
		
			expects("ACK");
		}
	}
	
	public int getTeam() {
		return teamID;
	}
	
	// Why does this require the try-catch block (if response.matches(pattern) return response)
	private String expects(String pattern) {
		try { 
			String response = in.nextLine(); 
			System.out.println("Pattern = " + pattern);
			System.out.println("Response = " + response);
			if (!response.matches(pattern)) { throw new IOException("Inappropriate response given.  Expected pattern " + pattern + " but got " + response); }
			return response;
			
		} catch(Exception e) {
			e.printStackTrace(); 
//			String error=err.nextLine();
//			System.out.println(error);
			isAlive = false;
			process.destroy();
			System.err.print("Expected pattern: " + pattern + ". The process has been nuked!");
		}
		
		return null;
	}
	
	public static Action toAction(String message) {
		Scanner arguments = new Scanner(message);
		
		switch(arguments.next()) {
			/*case "ROTATE":
				int id = arguments.nextInt();
				float rotation = arguments.nextFloat();
				return new RotateAction(id, rotation);*/
			case "SHOUT":
				return new ShoutAction(arguments.nextLine());
			default:
				return null;
		}
	}
	
	public static String toString(Action action) {
		
		/*if(action instanceof RotateAction) {
			int target = ((RotateAction)action).getRotatedObjectId();
			float rotation = ((RotateAction)action).getRotation();
			
			return String.format("ROTATE %d %f", target, rotation);
		}*/
		if(action instanceof ShoutAction) {
			ShoutAction shout = (ShoutAction)action;
			return String.format("SHOUT %s", shout.getMessage());
		}
		return "NONE";
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
