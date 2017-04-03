package DaneJensenBrackets;
import bonzai.*;
import java.util.*;
import Castles.*;
import Castles.api.*;
import Castles.util.*;

import java.io.*;

/**
 * This class runs the Bracket for the BonzAI competition.
 * In previous years we used a ladder system, but in 2017,
 * the BonzAI team decided to try a more fair bracket system
 * due to some participants being angry that their AI's only
 * got to run once while other went 20 times, then lost.
 * 
 * @author Dane Jensen
 * 
 */
public class Bracket {
	private String aiDir;
	private String outFile;
	private String matchFile;
	private int numSeedMatches;
	private ArrayList<bonzai.Jar> ais = new ArrayList<>();
	/**
	 * @param args
	 * args[0] is the directory with all the ais
	 * args[1] is the output file
	 * args[2] is the path to the scenario.dat file
	 * args[3] is the number of matches run to seed teams
	 */
	public static void main(String[] args){
		if(args.length < 4){
			System.err.println("Usage: <dir to ais> <output file> <seeding scenario file> <num seeding matches>");
			System.exit(1);
		}
		Bracket b = new Bracket(args[0], args[1], args[2], args[3]);
		b.generateBracket();
	}
	
	public Bracket(String aiDir, String outFile, String matchFile, String numSeedMatches){
		this.aiDir = aiDir;
		this.outFile = outFile;
		this.matchFile = matchFile;
		this.numSeedMatches = Integer.parseInt(numSeedMatches);
	}
	
	
	protected void generateBracket(){
		//use a tree with a bottom up apporch
		//need to generate seeds first
		File dir = new File(aiDir);
		//get all the ais in the aiDirectory
		String[] aiFiles = dir.list();
		if(aiFiles == null){
			System.err.println("Invalid directory: " + aiDir);
			System.exit(1);
		}
		System.out.println("AIS Being Used: " + Arrays.toString(aiFiles));
		
		
		//for each string in the aiFiles array
		//check if it is a directory, if not
		//open the ai and
		//add to the ais arraylist
		for(String ai: aiFiles){
			try{
				File temp = new File(ai);
				if(temp.isDirectory()){
					continue;
				}else{
					ais.add(new AIJar(temp));
				}
			}catch(Exception e){
				System.err.println(e.getMessage());
				e.printStackTrace(System.err);
			}
		}
		System.out.println("AIS ARE OPEN");
		
		//Seed the ais
		//5 seeding matches per ai, which means that each round
		//will run ais.size()/2 matches between 2 teams
		bonzai.Jar[][][] seedMatches = new bonzai.Jar[5][(ais.size()/2)][2];
		//the scores of each ai, indexed by the ai
		TreeMap<bonzai.Jar,Integer> scores = new TreeMap<bonzai.Jar,Integer>();
		for(int i = 0; i < ais.size();i++) scores.put(ais.get(i), 0);//make sure each ai
																	 //is in the map of scores
		int[] chosen = new int[ais.size()];//ensures each ai is chosen once
		   								   //and only once
		//make the matches
		for(int i = 0; i < seedMatches.length; i++){//make 5 matches
			System.out.println("MATCHMAKING ROUND "+i);
			for(int j = 0; j < ais.size()/2; j++){
				System.out.println("MATCH " + j);
				int chosen1 = (int)(Math.random()*ais.size());
				int chosen2 = (int)(Math.random()*ais.size());
				while(chosen[chosen1] >= i+1){
					chosen1++;
					if(chosen1 >= ais.size()){
						chosen1 = 0;
					}
				}
				chosen[chosen1]++; //mark the chosen ais as chosen
				while(chosen[chosen2] >= i+1 && ais.get(chosen2) != ais.get(chosen1)){
					chosen2++;
					if(chosen2 >= ais.size()){
						chosen2 = 0;
					}
				}
				chosen[chosen2]++; //mark the chosen ais as chosen
				seedMatches[i][j][0] = ais.get(chosen1);
				seedMatches[i][j][1] = ais.get(chosen2);
				System.out.println("" + seedMatches[i][j][0].name() + " against " + seedMatches[i][j][1].name());
			}
		}
		
		System.out.println("MATCHES ARE DECIDED\nRUNNING THE MATCHES");
		
		//run the seeding stuffs
		for(int i = 0; i < seedMatches.length; i++){
			System.out.println("Match Round " + i);
			//each round
			for(int j = 0; j < seedMatches[i].length; j++){
				
				//each match
				ArrayList<bonzai.Jar> matchJars = new ArrayList<>();
				//players
				matchJars.add(seedMatches[i][j][0]);
				matchJars.add(seedMatches[i][j][1]);
				//scenarios takes in 6 jar file arguments, need
				//to fill the last in with nulls
				matchJars.add(null);
				matchJars.add(null);
				matchJars.add(null);
				matchJars.add(null);
				System.out.println("\n" + matchJars.get(0).name() + " against " + matchJars.get(1).name() + "\n");
				try {
					CastlesScenario scenario = new CastlesScenario(new File(matchFile),-1);
					Simulation simulation = new Simulation(scenario,matchJars);
					simulation.start(); //start the simuation
					simulation.join();  //wait for the simulation to finish
					int[] matchScores = simulation.getScores();
					
					//add the scores to the map
					for(int z = 0; z < 2; z++){
						int score = scores.get(seedMatches[i][j][z]);
						score = score + matchScores[z];
						scores.put(seedMatches[i][j][z], score);
						System.out.println("");
						System.out.println(seedMatches[i][j][z].name() + " is up to " + score);
						System.out.println("");
					}
				} catch (Exception e) {
					System.out.println("Simulation failed!");
					e.printStackTrace();
				}
				
			}
			
		}
		
		
		
		//make averages
		double[][] avgScores = new double[ais.size()][2];
		for(int i = 0; i < ais.size(); i++){
			double score = scores.get(ais.get(i));//get score from map
			System.out.println("In " + chosen[i] + " Matches, " + ais.get(i).name() + " Scored " + score);
			score = score / (double)chosen[i];//average it
			avgScores[i][0] = score;
			avgScores[i][1] = i;//original index of the ai
		}
		
		//implements a selection sort to sort team from best to worst
		for(int i = 0; i < ais.size(); i++){
			double max = avgScores[i][0];
			int index = i;
			for(int j = i; j < ais.size(); j++){
				if(avgScores[j][0] > max){
					max = avgScores[j][0];
					index = j;
				}
			}
			double temp = avgScores[i][0];
			double temp2 = avgScores[i][1];
			avgScores[i][0] = avgScores[index][0];
			avgScores[i][1] = avgScores[index][1];
			avgScores[index][0] = temp;
			avgScores[index][1] = temp2;
		}
		System.out.println("SEEDING COMPLETED, PRINTING RESAULTS");
		for(int i=0; i < ais.size(); i++){
			System.out.println("" + i + ": " + ais.get((int)avgScores[i][1]).name());
		}
		/*
		//generate the bracket
		int n = 2;
		while(n < (ais.size()/2)+1){
			n*=2;
		}
		
		bonzai.Jar[][] bracket = new bonzai.Jar[n][2];
		int start = ais.size()/2;
		int m = 0;
		while(start+m < ais.size()){
			bracket[m][0] = ais.get((int)avgScores[start+m][1]);
			bracket[m][1] = ais.get((int)avgScores[start-m][1]);
		}
		
		//odd number of teams
		if(ais.size()%2 == 1){
			
			
		//even number of teams
		}else{
			
		}*/
		
		
		
	}
	
}
