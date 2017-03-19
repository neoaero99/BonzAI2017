package bonzai.automator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import bonzai.Jar;
import Castles.Simulation;
import Castles.api.*;
import Castles.util.*;

public class Automator {
	//static String root = "automation/";

	static int PORT = 13375;
	//The directory where all the jars and scenarios will be
	static String root = "automationJars";
	static String mapFile = "map.dat";

	public static void main(String[] args) throws IOException {

		/****************
		 * RUN AS CLIENT
		 ***************/
		if (args.length > 1) {
			//if (args.length == 0 || !"-s".equals(args[0])) {
			System.out.println("***RUNNING AS CLIENT***");
			//Argument 0 is ip, argument 1 is ID
			client(args[0],args[1]);

			return;
		} else {
			/****************
			 * RUN AS SERVER
			 ***************/
			System.out.println("***RUNNING AS SERVER***");
			System.out.println("Create file serverip.txt to run as client");
			System.out.println("serverip.txt should contain the server's IP");
		}

		//create serverip.txt
		//		PrintWriter serverInfo = new PrintWriter("serverip.txt");
		//		serverInfo.write(Inet4Address.getLocalHost().getHostAddress());
		//		System.out.println("Creating server.txt with ip = " + Inet4Address.getLocalHost().getHostAddress());
		//		serverInfo.close();
		//Open the output file early on to make sure another process isnt using it
		PrintWriter output = new PrintWriter("ladderinput.txt");

		//Store scores in a hashmap, mapping from team to score
		HashMap<bonzai.Jar,Integer> scores = new HashMap<>();

		//Store everyone who is connected to us
		HashMap<String, Long> connections = new HashMap<>();

		System.out.println("Recursively searching for jars in " + root);

		//Recursively add all jars in this folder
		File rootDirectory = new File(root);

		if (!rootDirectory.exists()) {
			System.out.println("The directory for the AIs did not exist, so it has been created!");
			rootDirectory.mkdirs();
		}

		System.out.println("Loading AIs from " + rootDirectory.getAbsolutePath());
		recursivelyAdd(rootDirectory, scores);
		System.out.println("Loaded " + scores.size() + " jars");

		//Pit every AI agains every other AI
		Set<bonzai.Jar> ais = scores.keySet();

		//Create a set of matches that need to run
		Set<Match> matches = new HashSet<Match>();

		System.out.print("Generating round robin matches... ");
		// UNCOMMENT TO SIMULATE One v One v One
		//		for (bonzai.Jar team1 : ais) {
		//			for (bonzai.Jar team2 : ais) {
		//				for (bonzai.Jar team3 : ais) {
		//					if (team1 == team2 || team1 == team3 || team2 == team3) { continue; }
		//
		//					//This is really bad.
		//					//Make sure we don't have a team in any other order already stored
		//
		//					Match m = new Match(team1,team2,team3);
		//					matches.add(m);
		//				}
		//			}
		//		}
		for (bonzai.Jar team1 : ais) {
			for (bonzai.Jar team2 : ais) {
				if (team1 == team2) { continue; }

				//This is really bad.
				//Make sure we don't have a team in any other order already stored

				Match m = new Match(team1,team2,null);
				System.out.println("Adding match " + m);
				matches.add(m);
			}
		}
		System.out.println("DONE!");
		System.out.println(matches.size() + " matches generated");

		long time = System.currentTimeMillis();
		int finishedSimulations = 0;
		System.out.println("Starting assignment server!");

		//Create a map that lets us know which client is working on which simulation
		//Each task will have a unique HASH given to it (key)
		Map<String, Match> inProgress = new HashMap<>();

		ServerSocket socket = null;
		try {
		socket = new ServerSocket(PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//This is written as synchronous so we don't have to worry about concurrency issues as much
		while (!matches.isEmpty() || !inProgress.isEmpty()) {
			Socket client = socket.accept();
			//Is this client ready, or did it finish a simulation?
			Scanner scanner = new Scanner(client.getInputStream());
			String[] status = scanner.nextLine().split(" ");
			//System.out.println(Arrays.toString(status));

			if (status[0].equals("READY")) {

				//Remember this process
				connections.put(status[1], System.currentTimeMillis());

				int numberofsimulations = Math.min(10, matches.size() + inProgress.size());
				while (connections.size() * numberofsimulations > matches.size()) {
					numberofsimulations--;
				}
				if (numberofsimulations <= 0) { numberofsimulations = 1; }

				//Send kills when there are more computers in the cluster than matches
				if (connections.size() > matches.size() + inProgress.size()) {
					numberofsimulations = -1;
					connections.remove(status[1]);
				}

				//Tell the computer how many simulations are to follow
				PrintWriter pw = new PrintWriter(client.getOutputStream());
				pw.println(numberofsimulations);
				pw.flush();

				//Give up to 10 simulations per computer
				for (int i = 0; i < numberofsimulations; i++) {
					//Give this something to do
					Match m = getNextMatch(matches);
					String key = generateKey(inProgress);

					//If we're done with uncalculated matches,
					//start issuing out processes that are marked as in progress
					//that have not completed yet
					//NOTE: Don't copy if there are less than 10 matches
					//We don't want a stalemate!
					if (m == null && inProgress.size() > 10) {
						//We ran out of matches to assign.
						//We need to move all the inProgress matches to matches
						System.out.println("Marking all " + inProgress.size() + " inProgress matches as orphaned.  Re-assigning");
						for (Match match : inProgress.values()) {
							matches.add(match);
						}
						inProgress.clear();
						m = getNextMatch(matches);
						key = generateKey(inProgress);
					}

					//Remember who we assigned this to
					inProgress.put(key, m);
					//System.out.println(key + " is assigned to " + m);


					//Send the paths and key
					pw.println(key);
					pw.println(jarPath(m.team1));	//Gets the path of the jar
					pw.println(jarPath(m.team2));	//Gets the path of the jar
					pw.println(jarPath(m.team3));	//Gets the path of the jar (Null if not exists)
					pw.flush();

				}

				System.out.println(numberofsimulations + " matches assigned to " + status[1] + " " + client.getInetAddress());
				//We're all done!  Lets move onto the next one!
			} else if (status[0].equals("RESULT")) {

				//How many are they sending us
				int numberofmatches = Integer.parseInt(status[1]);

				for (int i = 0; i < numberofmatches; i++) {
					//Get the result
					String key = scanner.nextLine();
					Match m = inProgress.remove(key);
					if (m == null) { 
						System.out.println(key + " has already been simulated.  Ignoring this result");
					} else {
						String result = scanner.nextLine();
						bonzai.Jar winningTeam = null;

						if (result.equals("1")) {winningTeam = m.team1;}
						else if (result.equals("2")){ winningTeam = m.team2; }
						else { winningTeam = m.team3; System.out.println("THIS SHOULD FAIL: Result: " + result);}

						scores.put(winningTeam, scores.get(winningTeam) + 1);

						finishedSimulations++;	
					}

				}

				System.out.println(numberofmatches + " results from client!");

				long timeElapsed = System.currentTimeMillis() - time;
				long timePerSimulation = (finishedSimulations == 0) ? -1 : timeElapsed / finishedSimulations;
				int remaining = matches.size() + inProgress.size();
				long timeRemaining = timePerSimulation * remaining;

				System.out.println(remaining + " simulations left.  Estimated time: " + timeToString(timeRemaining) + ".  Time taken: " + timeToString(timeElapsed));
			} else {
				System.out.println("Unknown response from child: " + status);
			}



			//Update the number of connections
			//Copy keys
			Set<String> keys = new HashSet<String>();
			for (String s : connections.keySet()) {
				keys.add(s);
			}

			for (String key : keys) {
				long t = connections.get(key);
				if (System.currentTimeMillis() - t > 100000) {
					connections.remove(key);
				}
			}

			System.out.println(connections.size() + " computers in cluster");

			client.close();
		}

		System.out.println("ALL SIMULATIONS COMPLETED!");

		//Calculate time taken
		long timeElapsed = System.currentTimeMillis() - time;
		long avgTime = (finishedSimulations == 0) ? -1 : timeElapsed / finishedSimulations;
		System.out.println("Total time taken: " + timeToString(timeElapsed));
		System.out.println(finishedSimulations + " games simulated.  Avg simulation time: " + timeToString(avgTime));

		//Sort people by their scores
		//List<String> ranking = new ArrayList<String>();

		int rank = 0;
		while (!scores.isEmpty()) {
			//Find the best score
			Set<bonzai.Jar> keys = scores.keySet();
			bonzai.Jar bestAI = keys.iterator().next();

			for (bonzai.Jar key : keys) {
				if (scores.get(key) > scores.get(bestAI)) {
					bestAI = key;
				}
			}

			//Print this AI, and remove it
			rank++;
			int score = scores.remove(bestAI);
			System.out.println(pad(rank + ".", 5) + pad(score + " points", 11) + " " + bestAI.name());

			//Add this to our output file as well
			output.println(bestAI.file().getPath());
		}
		output.close();

	}

	private static String jarPath(Jar team) {
		if (team == null) { return "NULL"; }
		return team.file().getPath();
	}

	public static String pad(String s, int length) {
		if (s.length() > length) {
			return s.substring(0, length);
		}

		while (s.length() < length) {
			s += " ";
		}
		return s;
	}

	public static void recursivelyAdd(File directory, HashMap<bonzai.Jar,Integer> map) {
		if (!directory.isDirectory()) { return; }

		//Add all files in this directory
		for (File f : directory.listFiles()) {
			//If this is a directory, recursively add
			if (f.isDirectory()) {
				recursivelyAdd(f, map);
			} else {
				if (f.getName().contains(".jar")) {
					try {
						System.out.print("Loading " + f.getPath() + " : ");
						bonzai.Jar jar = new AIJar(f);
						map.put(jar, 0);
						System.out.println(jar.name());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Match getNextMatch(Set<Match> matches) {
		if (matches.isEmpty()) { return null; }
		Match m = matches.iterator().next();
		matches.remove(m);
		return m;
	}

	public static String generateKey(Map<String,Match> inProgress) {
		String key = random();
		if (inProgress.containsKey(key)) {
			key = random();
		}
		return key;
	}

	public static String random() {
		String key = "";
		for (int i = 0; i < 10; i++) {
			key += (int)(Math.random() * 10);
		}
		return key;
	}

	public static void client(String ip, String id) throws UnknownHostException, IOException {
		String myKey = id + "";//System.currentTimeMillis() + "";	//Semi-unique id per client


		while (true) {
			try {

				System.out.println("Establishing connection with " + ip + " on port " + PORT + "...");

				//Connect to the server
				Socket socket = new Socket(ip, PORT);
				PrintWriter out = new PrintWriter(socket.getOutputStream());
				Scanner in = new Scanner(socket.getInputStream());

				//Tell the server that we're ready!
				System.out.println("Telling server that we're ready...");
				out.println("READY " + myKey);
				out.flush();

				//Let the server tell us who we are
				int numberofmatches = Integer.parseInt(in.nextLine());

				if (numberofmatches < 0) { System.exit(0); }

				String resultString = "";
				for (int i = 0; i < numberofmatches; i++) {
					String key = in.nextLine();
					System.out.println("We are key: " + key);

					//Now lets read who we're playing against!
					String jar1 = in.nextLine();
					String jar2 = in.nextLine();
					String jar3 = in.nextLine();
					Match match = new Match(jar1,jar2,jar3);

					//Simulate this match!
					//Choose scenario
					CastlesScenario scenario;
					String scenarioPath = root + "/" + mapFile;
					try {
						scenario = new CastlesScenario(new File(scenarioPath),-1);
					} catch (Exception e) {
						System.out.println("Failed to load scenario " + scenarioPath + " --Killing program");
						return;
					}

					System.out.print(match + "... ");
					System.out.flush();

					//Spin up the simulation!
					List<bonzai.Jar> jars = new ArrayList<bonzai.Jar>();
					jars.add(match.team1);
					jars.add(match.team2);
					jars.add(match.team3);
					//The map calls for 6 teams, and it expects 6 things in the list
					jars.add(null);
					jars.add(null);
					jars.add(null);

					int winningTeam = -1;	//-1 means error.  THis should change if everything works

					try {
						Simulation simulation = new Simulation(scenario,jars);
						simulation.start();
						simulation.join();
						TeamColor winner = simulation.getWinner();

						//This is a hacky way of doing this, but team 1 is lochmara
						//and team 2 is jazzberry.  Check who won, and give them a point

						if (winner.equals(TeamColor.RED)) {
							winningTeam = 1;
							System.out.println("Winner: " + pad(match.team1.name(),15));
						} else if (winner.equals(TeamColor.YELLOW)){
							winningTeam = 2;
							System.out.println("Winner: " + pad(match.team2.name(),15));
						} else {
							winningTeam = 3;
							System.out.println("Winner: " + pad(match.team3.name(),15));
						}

					} catch (Exception e) {
						System.out.println("Simulation failed!");
						e.printStackTrace();
					}



					resultString += key + "\n" + winningTeam + "\n";
				}

				//Connect to the server to say what we've done!
				socket = new Socket(ip,13375);
				out = new PrintWriter(socket.getOutputStream());
				in = new Scanner(socket.getInputStream());

				out.println("RESULT " + numberofmatches);
				out.print(resultString);
				out.flush();
			}



			catch (Exception e) {
				//e.printStackTrace();
				System.out.println(e.getMessage());
				System.out.println("Encountered an error.  Waiting 5 seconds, then trying again");
				e.printStackTrace(System.out);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**Convert time in millis to human readable
	 * 
	 * @param time TIME IN MILLIS
	 * @return
	 */
	public static String timeToString(long time) {
		time /= 1000;
		long hours = time / 3600;
		long minutes = (time / 60) % 60;
		long seconds = time % 60;

		return hours + "h " + minutes + "m " + seconds + "s";
	}
}


class Match {
	bonzai.Jar team1 = null;
	bonzai.Jar team2 = null;
	bonzai.Jar team3 = null;

	public Match(bonzai.Jar t1, bonzai.Jar t2, bonzai.Jar t3) {
		team1 = t1;
		team2 = t2;
		team3 = t3;
	}

	public Match(String path1, String path2, String path3) {
		//Linux gets weird with miding slashes... This fixes it
		path1 = path1.replace("\\", "/");
		path2 = path2.replace("\\", "/");
		path3 = path3.replace("\\", "/");


		try {
			File file1 = new File(path1);
			File file2 = new File(path2);
			File file3 = new File(path3);

			if (!path1.toLowerCase().equals("null")) {
				System.out.println("Loading " + path1 + "  <" + file1.getAbsolutePath() + ">");
				team1 = new AIJar(file1);
			}

			if (!path2.toLowerCase().equals("null")) {
				System.out.println("Loading " + path2 + "  <" + file2.getAbsolutePath() + ">");
				team2 = new AIJar(file2);
			}

			if (!path3.toLowerCase().equals("null")) {
				System.out.println("Loading " + path3 + "  <" + file3.getAbsolutePath() + ">");
				team3 = new AIJar(file3);
			}
		} catch (Exception e) {
			System.out.println("Encountered an error while loading jar!");
		}
	}

	public String toString() {
		String team1Name = (team1 == null) ? "NULL" : team1.name();
		String team2Name = (team2 == null) ? "NULL" : team2.name();
		String team3Name = (team3 == null) ? "NULL" : team3.name();
		return Automator.pad(team1Name,15) + " " + Automator.pad(team2Name,15) + " " + Automator.pad(team3Name,15);
	}

	@Override
	public int hashCode() {
		int hash = team1.hashCode();
		if (team2 != null) { hash ^= team2.hashCode(); }
		if (team3 != null) { hash ^= team3.hashCode(); }
		return hash;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Match)) { return false; }
		Match m = (Match)other;

		Set<bonzai.Jar> match1Jars = new HashSet<bonzai.Jar>();
		match1Jars.add(team1);
		match1Jars.add(team2);
		match1Jars.add(team3);

		Set<bonzai.Jar> match2Jars = new HashSet<bonzai.Jar>();
		match2Jars.add(m.team1);
		match2Jars.add(m.team2);
		match2Jars.add(m.team3);

		//Count the similarities
		int similar = 0;
		for (bonzai.Jar j : match1Jars) {
			for (bonzai.Jar k : match2Jars) {
				if (j==k || (j!=null && j.equals(k))) { similar ++; }
			}
		}

		return similar >= 3;
	}
}
