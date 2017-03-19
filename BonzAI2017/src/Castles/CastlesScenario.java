package Castles;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.List;

import Castles.api.TeamColor;
import bonzai.Team;

public class CastlesScenario implements bonzai.Scenario {
  public static final int NUM_TURNS = 15;

	
	//TODO 2017: This is used in simulation. If the turns are not applied at the same time,
	// change this to false. 
	public static final boolean CONCURRENT_TURNS = true;

	private final File file;
	private CastlesMap map;
	private String name;
	
	private final Image image;
	private final int teamID;
	private final boolean[] teams = new boolean[6];
	
	public CastlesScenario(File file, int teamID) throws IOException {
		//TODO 2017: This is what we used to parse and use the map files for our game. This is an example.
		this.file = file;
		this.teamID = teamID;
		CastlesMap map = null;
		
		try {
			//Get the map object we need to instantiate the game.
			map = Parser.parseFile(file);
			CastlesRenderer.loadImages();
//			String[] size = map.getField("size").split(", *");
//			LazersRenderer.setMapSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
			name = map.getField("name");
			
		} catch (Exception e) {
			System.err.println("Exception occured when reading the file!");
			e.printStackTrace();
			System.err.println("Exiting...");
			System.exit(-1);
		}
		this.map = map;
		//Graphics get passed in to render here
		
			BufferedImage buffer = new BufferedImage(700, 500, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)(buffer.getGraphics());
			//g.setColor(Color.BLACK);
			//g.fillRect(0, 0, 700, 500);
		
			g.translate(700 / 2, 500 / 2);
			g.scale(700 / 2, 500 / 2);
			
			CastlesRenderer.render(g, map);
			
			this.image = buffer;
		
	}
	
	
	/**
	 * @return a File object that was used to generate the map/scenario
	 */
	public File file() {
		return file;
	}

	/**
	 * @return the name of the Scenario
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param scenario - the scenario to compare against
	 * @return - 0 if the scenarios are equal, -1 if the current scenario's
	 * name is lexicographically less than the scenario we are comparing against,
	 * and 1 if the current scenario's name is lexicographically greater than
	 * the scenario we are comparing against.
	 */
	@Override
	public int compareTo(bonzai.Scenario scenario) {
		int teamCmp = Integer.compare(this.getNumTeams(), scenario.getNumTeams());
		return teamCmp != 0 ? teamCmp : name.compareTo(scenario.getName());
	}

	/**
	 *  TODO 2017: This method starts a new game object with the current map. 
	 **/
	public Game instantiate(List<? extends bonzai.Jar> jars) {
		//active colors in game; some colors may not have been given AI's to use
		List<Castles.api.TeamColor> colors = new ArrayList<>();
		
		CastlesMap map = new CastlesMap(this.map);
		
		for(int x = 0; x < this.getNumTeams(); x += 1) {
			if(jars.get(x) != null) {
				Castles.api.TeamColor c = Castles.api.TeamColor.values()[x];
				colors.add(c);
				teams[x] = true;
			}
		}

		for (int i = 0; i < teams.length; i++) {
			if (!teams[i]) {
				map.removePlayer(i);
			}
		}

		int i = 0;
		//String turns = map.getField("turns");
		Game game = new Game(teamID, CastlesScenario.NUM_TURNS, map, colors);
		for (Team t : game.turn(0).getAllTeams()) {
			if(jars.size() == 0) break;
			if(t == null) break;
			if(jars.get(i) == null) break;
			if(jars.get(i).name() == null) break;
			t.setName(jars.get(i).name());
			i++;
		}
		return game;
	}


	@Override
	public File getFile() {
		return file;
	}


	@Override
	public int getNumTeams() {
		return Integer.parseInt(map.getField("playercount"));
	}


	@Override
	public Image getImage() {
		return image;
	}
}
