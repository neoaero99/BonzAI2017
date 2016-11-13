// to run the ladder:
// first do an 'ant build' in main project folder
// 'ant ladder' in main project folder
// run automator to make ladderinput.txt
// go to BonzAI2016/bonzai2016
// run ladder.jar


package bonzai.automator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import Castles.util.*;

public class Ladder {	
	public static void main(String[] args) throws FileNotFoundException {				
		
		//****** Loading AIS *********
		
		//I hard coded this in another place (Automator.java)
		//Make sure to update both if you are stupid and change this path
		File file = new File("ladderinput.txt");

		if (!file.exists()) {
			System.out.println("Cannot find ladderinput.txt");
			System.out.println("Please run the automator to generate this file");
			return;
		}

		Scanner scanner = new Scanner(file);
		Stack<bonzai.Jar> aiStack = new Stack<>();

		System.out.println("Loading AIs");
		while (scanner.hasNextLine()) {
			String aiPath = scanner.nextLine();
			try {
				aiStack.push(new AIJar(new File(aiPath)));
				System.out.println("     " + aiPath);
			} catch (Exception e) {
				System.out.println("Could not load " + aiPath);
			}
		}
		scanner.close();

		
		//****** Loading Maps ***************
		List<File> scenarios = new ArrayList<>();
		//Read all the scenario files
		File directory = new File("scenarios");
		//Add the first one twice (to 0)
		for (File f : directory.listFiles()) {
			scenarios.add(f);
		}

		
		Scanner input = new Scanner(System.in);
		
		//****** Setting up ladder structure **********
		System.out.println("How many AIs do you want to run at once?  (2-6)");
		
		String countString = input.nextLine();
		int count = 2;
		try {
			count = Integer.parseInt(countString);
		} catch (Exception e) {
			System.out.println("Interpreting vague answer as 2");
		}
		if (count < 2) { count = 2; }
		if (count > 6) { count = 6; }


		//******* New or Load? ***************
		
		System.out.println("Starting tournament");
		System.out.println("Do you want to (1) start a new or (2) load from previous or (3) replay?");
		String line = input.nextLine();
		if (line.equals("3")) {
			replay();
		} else if( line.equals("2")) {
			load(aiStack, scenarios, count);
		} else if (line.equals("1")) {
			//overwriting results.txt
			try {
				PrintWriter w = new PrintWriter(new File("results.txt"));
				w.print("");
				w.close();
			} catch (Exception e) {
				System.out.println("Cant clear results.txt");
			}
			tournament(aiStack, scenarios, count);
		} else {
			System.out.println("Did not recognize choice");
		}
	}

	
	public static int chooseMap(List<File> scenarios) {
		Scanner input = new Scanner(System.in);
		System.out.println("Which map for the round?");
		for (int i = 0; i < scenarios.size(); i++) {
			System.out.println(i + ". " + scenarios.get(i).getName());
		}
		String mapString = input.nextLine();
		int map = 0;
		try {
			map = Integer.parseInt(mapString);
		} catch (Exception e) {
			System.out.println("Interpreting vague answer as 0");
		}
		if (map < 0) { map = 0; }
		if (map >= scenarios.size()) { map = scenarios.size()-1; }
		
		return map;
	}
	
	
	public static bonzai.Jar runGame(ArrayList<bonzai.Jar> players, File map) {
		ArrayList<String> execArgs = new ArrayList<String>();
		execArgs.add("java");
		execArgs.add("-jar");
		execArgs.add("lazers.jar");
		execArgs.add("-run");
		execArgs.add(map.getPath());
		
		
		for (bonzai.Jar j : players) {
			execArgs.add(j.file().getPath());
		}
		//Fill the remaining entries with null
		for (int i = 0; i < 6 - players.size(); i++) {
			execArgs.add("null");
		}

		System.out.println("Next Up");
		for (bonzai.Jar j : players) {
			System.out.println("---   " + j.name() + "   ---");
		}
		
		countdown();
		
		
		//java -jar lazers.jar -run [scenarios] <ai/null> <ai/null> <ai/null> <ai/null> <ai/null> <ai/null>
		String exec = "java";
		for (String s : execArgs) {
			exec += String.format(" [%s]", s);
		}
		
		try {
			Process p = new ProcessBuilder(execArgs).start();
			Scanner resultScanner = new Scanner(p.getInputStream());
			int winner = -1;

			//Catch the output from the process and try to parse out the winner. 
			while (true) {
				try {
					String l = resultScanner.nextLine();
					String[] line = l.split(" ");
					if (line[0].equals("RESULT")) {
						//Try to get the result
						winner = Integer.parseInt(line[1]);
						break;
					}
				} catch (Exception e) {
					System.out.println("Unable to detect winner!");
					break;
				}
			}
			p.destroy();
			
			if (winner == -1) {
				System.out.println("Problem in determining the winner");
				return null;
			} else {		
				return players.get(winner);
			}
			

		} catch (Exception e) {
			System.out.println("Error in running simulation");
		}
		return null;
	}
	
	
	public static void tournament(Stack<bonzai.Jar> aistack, List<File> scenarios, int count) {
		
		int map = chooseMap(scenarios);
		while (aistack.size() > 1) {
			
			//put the players in a collection to pass to runGame
			ArrayList<bonzai.Jar> players = new ArrayList<>(); 
			for (int i = 0; i < count; i++) {
				bonzai.Jar currentJar = aistack.pop();
				players.add(players.size(),currentJar);
			}
			
			//Run the game, save the results
			
			bonzai.Jar winner = runGame(players, scenarios.get(map));
			System.out.println("The round goes to " + winner.name() + "\n");
			saveResults(players, scenarios.get(map), winner, count);
			aistack.push(winner);
			

		}
	
		System.out.println("THE WINNER IS: \n\n\n" + aistack.pop().name() + "\n\n!!!!!!!!!!!!!!!");
	}
	
	public static void replay() {
		ArrayList<String> lines = new ArrayList<>();
		try {
			int x = 0;
			Scanner s = new Scanner(new File("results.txt"));
			while(s.hasNext()) {
				lines.add(s.nextLine());
				String[] z = lines.get(lines.size() - 1).split(" ");
				// x is the index available, z[1:6] are the players
				System.out.println("(" + x + ") " + z[1] + " " + z[2] + " "  + z[3] + " "  + z[4]);
				x++;
			}
		} catch (Exception e) {
			System.out.println("Cound not load file");
		}
		
		Scanner ss = new Scanner(System.in);
		String input = ss.nextLine();
		int choice = -1;
		try {
			choice = Integer.parseInt(input);
		} catch (Exception e) {
			System.out.println("Could not understand the choice");
		}
		
		
		if (choice >= 0 && choice < lines.size()) {
			ArrayList<bonzai.Jar> players = new ArrayList<>();
			String line = lines.get(choice);
			String[] split = line.split(" ");
			try { // try to load all the AIs that are named (not == null)
				for (int i = 4; i < split.length; i++) {
					if (! split[i].equals("null")) {
						System.out.println(split[i] + "  split[i]");
						players.add(new AIJar(new File(split[i])));
					}
				}
			} catch (Exception e) {
				System.out.println("Could not start game");
			}
			bonzai.Jar winner = runGame(players, new File(split[1]));
			System.out.println("The winner is: " + winner.name());
		}
		
	}
	
	// run the tournament using the winners from results.txt
	public static void load(Stack<bonzai.Jar> aistack, List<File> scenarios, int count) {
		
		Scanner s = null;
		try {
			s = new Scanner(new File("results.txt"));
			while (s.hasNext()) {
				System.out.println("new line");
				String[] line = s.nextLine().split(" ");
				String winner = line[0];
				bonzai.Jar winnerJar = null;		
				
				for (int x = 0; x < count; x++) {
					bonzai.Jar current = aistack.pop();
					if (winner.equals(current.file().getPath())) {
						winnerJar = current;
					}
				}
				aistack.push(winnerJar);
			}
			s.close();
		} catch (Exception e) {
			System.out.println("Can't open results.txt");
			return;
		}
		tournament(aistack ,scenarios, count);
		
	}
	
	
	//append the results of a game to the end of 'results.txt'. This can be used to rebuild a tournament or replay a game. 
	public static void saveResults(ArrayList<bonzai.Jar> aisPlayed, File map, bonzai.Jar aiwon, int count) {
		//format:
		//winner map numPlayers player1/null player2/null player3/null player4/null player5/null player6/null\n
		try {
			FileWriter fw = new FileWriter("results.txt", true);
			String line = "";
			line += aiwon.file().getPath() + " ";
			line += map.getPath() + " ";
			line += aisPlayed.size() + " ";
			
			for (bonzai.Jar j : aisPlayed) {
				line += " " + j.file().getPath();
			}
			for (int i = 0; i < 6 - count; i++) {
				line += " null";
			}
			line += "\n";
			
			fw.write(line);
			fw.close();
		} catch (Exception e) {
			System.out.println("Error in saving results");
		}
	}

	public static void countdown() {
		System.out.print("Next match starting in ");
		for (int i = 5; i >= 0; i--) {
			System.out.println(i + " ");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println("Im not sleepy");
			}
		}
		System.out.println();
	}
}
