package Castles.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import Castles.Objects.*;
import bonzai.Position;
import bonzai.Team;

public class Parser {
	private Parser() {}

	private static final int PARSE_VILLAGE = 5,PARSE_NONE = 0, PARSE_PLAYER = 1, PARSE_CASTLE = 2, PARSE_RALLY = 3, PARSE_PATH = 4;
	private static int parse_mode;

	public static CastlesMap parseFile(String file) throws FileNotFoundException{
		return parseFile(new File(file));
	}

	public static CastlesMap parseFile(File file) throws FileNotFoundException {
		System.out.println("" + file.getPath());
		//return new CastlesMap();
		Scanner in = new Scanner(file);

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
				case PARSE_PLAYER:								
					map.addPlayer(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					break;

				case PARSE_CASTLE:								// walls
					map.addCastle(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					break;
				case PARSE_RALLY:
					map.addRally(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					break;
					
				case PARSE_PATH:
					map.connect(row[0], row[1], Integer.parseInt(row[2]));
					break;
					
				case PARSE_VILLAGE:
					
					break;

				default: // parse mode/general
					id--;	//This is not considered a game object
					switch(ls[0]) {
					case "players":
						parse_mode = PARSE_PLAYER;
						break;

					case "castles":
						parse_mode = PARSE_CASTLE;
						break;

					case "rally":
						parse_mode = PARSE_RALLY;
						break;

					case "paths":
						parse_mode = PARSE_PATH;
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
		/*String[] size = map.getField("size").split(",");
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
		}*/

		if(in != null) {
			in.close();                                    // RAII semantics are really useful
		};
		
//		map.calculatePaths();

		return map;
	}
}