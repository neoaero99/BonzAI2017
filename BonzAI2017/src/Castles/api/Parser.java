package Castles.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import Castles.Objects.*;
import bonzai.Position;
import bonzai.Team;

public class Parser {
	private Parser() {}
	//sets the different states that can be parsed
	private static final int PARSE_VILLAGE = 5,PARSE_NONE = 0, PARSE_PLAYER = 1;
	private static final int PARSE_CASTLE = 2, PARSE_RALLY = 3, PARSE_PATH = 4;
	private static final int PARSE_FIELD = 6;
	private static int parse_mode;

	public static CastlesMap parseFile(String file) throws FileNotFoundException{
		return parseFile(new File(file));
	}

	public static CastlesMap parseFile(File file) throws FileNotFoundException {
		System.out.println("" + file.getPath());
		//return new CastlesMap();
		Scanner in = new Scanner(file);

		CastlesMap map = new CastlesMap();
		
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();

			if (!line.isEmpty() && !line.matches("#.*")) {			// Ignore comments
				String[] ls = line.trim().split(": ?");				// Split into "header" and "data"
				String[] row = null;
				
				if(ls.length > 1){
					parse_mode = PARSE_FIELD;						// set to parse a field
				}else if (ls[0].charAt(0) != '<'){					
					parse_mode = PARSE_NONE;						// set to parse none if and only if
																	// ls only has one string in it
																	// and the first char isn't <
				}else{
					//prep to parse an object
					row = ls[0].substring(1, ls[0].length() - 1).split("> #")[0].split(",? ");
				}

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
					map.addVillage(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					break;
					
				case PARSE_FIELD:
					map.setField(ls[0], ls[1]);
					break;

				default: // parse mode/general
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
						
					case "villages":
						parse_mode = PARSE_VILLAGE;
						break;

					default:
						parse_mode = PARSE_NONE;
						break;
					}

					break;
				}

			}
		}
		

		if(in != null) {
			in.close();                                    // RAII semantics are really useful
		};
		
		map.calculatePaths();
		

		return map;
	}
}