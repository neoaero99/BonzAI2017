package Castles.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import Castles.Objects.*;
import bonzai.Position;
import bonzai.Team;

public class Parser {
	private Parser() {}

	private static final int PARSE_NONE = 0, PARSE_EMITTER = 1, PARSE_REPEATER = 2, PARSE_TARGET = 3, PARSE_WALL = 4;
	private static int parse_mode;

	public static CastlesMap parseFile(String file) throws FileNotFoundException{
		return parseFile(new File(file));
	}

	public static CastlesMap parseFile(File file) throws FileNotFoundException {
		return new CastlesMap();
		/*Scanner in = new Scanner(file);

		CastlesMap map = new CastlesMap();
		int id = -1;	//Incremented before first use
		
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();

			if (!line.isEmpty() && !line.matches("#.*")) {			// Ignore comments
				String[] ls = line.trim().split(": ?");				// Split into "header" and "data"
				String[] row = null;


				if (ls[0].charAt(0) != '<')
					parse_mode = PARSE_NONE;
				else
					row = ls[0].substring(1, ls[0].length() - 1).split("> #")[0].split(",? ");

				id++;
				switch(parse_mode) {
				case PARSE_TARGET:								// target
					map.addTarget(new Target(
							new Position(
									Integer.parseInt(row[0]),
									Integer.parseInt(row[1])),
							id));
					break;

				case PARSE_WALL:								// walls
					map.addWall(new Wall(
							Integer.parseInt(row[0]),	// x1
							Integer.parseInt(row[1]),	//y1
							id));
					break;

				default: // parse mode/general
					id--;	//This is not considered a game object
					switch(ls[0]) {
					case "players":
						parse_mode = PARSE_EMITTER;
						break;

					case "repeaters":
						parse_mode = PARSE_REPEATER;
						break;

					case "targets":
						parse_mode = PARSE_TARGET;
						break;

					case "walls":
						parse_mode = PARSE_WALL;
						break;

					default:
						map.setField(ls[0], ls[1]);
						break;
					}

					break;
				}

			}
		}
		
		//Get the map size
		String[] size = map.getField("size").split(",");
		map.width = Integer.parseInt(size[0].trim());
		map.height = Integer.parseInt(size[1].trim());

		//Add walls all the way around
		for (int x = -1; x <= map.width+1; x++) {
			map.addWall(new Wall(x,-1,id++));
			map.addWall(new Wall(x, 0,id++));
			map.addWall(new Wall(x,map.height,id++));
			map.addWall(new Wall(x,map.height+1,id++));
		}
		for (int y = -1; y < map.height; y++) {
			map.addWall(new Wall(-1,y,id++));
			map.addWall(new Wall( 0,y,id++));
			map.addWall(new Wall(map.width,y,id++));
			map.addWall(new Wall(map.width+1,y,id++));
		}

		if(in != null) {
			in.close();                                    // RAII semantics are really useful
		};

		//Set up the initial targets for each emitter and repeater.
		//Need to do this once the map is fully made, rather than on 
		//creation of the emitter or repeater.
		/*for (Emitter e : map.getEmitters()) {
			e.findTarget();
		}

		for (Repeater r : map.getRepeaters()) {
			r.findTarget();
		}*/
		/*
		map.calculateCanHit();

		return map;*/
	}
}