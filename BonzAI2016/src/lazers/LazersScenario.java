package lazers;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import lazers.api.Emitter;
import lazers.api.LazersMap;
import lazers.api.Parser;
import lazers.api.Repeater;
import lazers.api.Target;
import lazers.api.Wall;

import java.util.List;
import java.util.Scanner;

public class LazersScenario implements bonzai.Scenario {
	public static final int NUM_TURNS = 500;
	public static final boolean CONCURRENT_TURNS = false;

	private final File file;
	private static LazersMap map;
	private static String name;
	
	private final Image image;
	
	public LazersScenario(File file) throws IOException {
		this.file = file;
		
		try {
			//Get the map object we need to instantiate the game.
			map = Parser.parseFile(file);
			
			String[] size = map.getField("size").split(", *");
			LazersRenderer.setMapSize(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
			name = map.getField("name");
			
		} catch (Exception e) {
			System.err.println("Exception occured when reading the file!");
			e.printStackTrace();
			System.err.println("Exiting...");
			System.exit(-1);
		}
		
		
		//Graphics get passed in to render here
		{
			BufferedImage buffer = new BufferedImage(700, 500, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)(buffer.getGraphics());
			//g.setColor(Color.BLACK);
			//g.fillRect(0, 0, 700, 500);
		
			g.translate(700 / 2, 500 / 2);
			g.scale(700 / 2, 500 / 2);
			
			LazersRenderer.render(g, map);
			
			this.image = buffer;
		}
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
	 * @return the number of teams that the map supports
	 */
	public int getNumTeams() {
		return map.getEmitters().size();
	}
	
	/**
	 * @return a collection containing all the emitters on the map
	 */
	public Collection<Emitter> getEmitters() {
		return map.getEmitters();
	}
	
	/**
	 * @return a collection containing all the repeaters on the map
	 */
	public Collection<Repeater> getRepeaters() {
		return map.getRepeaters();
	}
	
	/**
	 * @return a collection containing all the targets on the map
	 */
	public Collection<Target> getTargets() {
		return map.getTargets();
	}

	/**
	 * @return a collection containing all the walls on the map
	 */
	public Collection<Wall> getWalls() {
		return map.getWalls();
	}
	
	/**
	 * @return the LazersMap object representing the map
	 */
	public LazersMap getMap() {
		return map;
	}
	
	
	//TODO: Is this right? Should it be public???
	/**
	 * @return the image representing the current frame
	 */
	@Override
	public Image getImage() {
		return image;
	}
	
	/**
	 * @return the File object where map data was loaded from
	 */
	@Override
	public File getFile() {
		return file;
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
	 *
	 **/
	public Game instantiate(List<? extends bonzai.Jar> jars, int id) {
		List<lazers.api.Color> colors = new ArrayList<>();
		
		for(int x = 0; x < this.getNumTeams(); x += 1) {
			if(jars.get(x) != null) {
				lazers.api.Color c = lazers.api.Color.values()[x];
				colors.add(c);
			}
		}
		return new Game(NUM_TURNS, map, colors, id);
	}
}
