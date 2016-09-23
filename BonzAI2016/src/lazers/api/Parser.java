package lazers.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import bonzai.Position;
import bonzai.Team;

public class Parser {
	private Parser() {}
	
	private static final int PARSE_NONE = 0, PARSE_EMITTER = 1, PARSE_REPEATER = 2, PARSE_TARGET = 3, PARSE_WALL = 4, PARSE_MATRIX = 5;
	private static int parse_mode;
	
	public static LazersMap parseFile(String file) throws FileNotFoundException{
		return parseFile(new File(file));
	}

	public static LazersMap parseFile(File file) throws FileNotFoundException {
		Scanner in = new Scanner(file);
		
		LazersMap map = new LazersMap();
		boolean[][] matrix = null;
		int mat_idx = 0;
		
		while (in.hasNextLine()) {
			String line = in.nextLine();
			
			if (!line.isEmpty() && !line.matches("#.*")) {			// Ignore comments
				String[] ls = line.trim().split(": ?");				// Split into "header" and "data"
				String[] row = null;
				
				
				if (ls[0].charAt(0) != '<')
					parse_mode = PARSE_NONE;
				else
					row = ls[0].substring(1, ls[0].length() - 1).split(", ?");
					//row = ls[0].substring(1, ls[0].length() - 1).split("> #")[0].split(",? ");
				
				switch(parse_mode) {
					case PARSE_EMITTER:								// emitter					
						int id = Integer.parseInt(ls[1]) - 1;
						map.addEmitter(new Emitter(
							new Team(lazers.api.Color.values()[id], id),
							Float.parseFloat(row[2]),
							new Position(
								Integer.parseInt(row[0]),
								Integer.parseInt(row[1])),
							id), id);
						break;
						
					case PARSE_REPEATER:							// repeater
						map.addRepeater(new Repeater(
							Float.parseFloat(row[2]),
							new Position(
								Integer.parseInt(row[0]),
								Integer.parseInt(row[1])),
							0,
							Integer.parseInt(ls[1]) - 1));
						break;
						
					case PARSE_TARGET:								// target
						map.addTarget(new Target(
							new Position(
								Integer.parseInt(row[0]),
								Integer.parseInt(row[1])),
							Integer.parseInt(ls[1]) - 1));
						break;
						
					case PARSE_WALL:								// walls
						map.addWall(new Wall(
							new Position(
								Integer.parseInt(row[0]),	// x1
								Integer.parseInt(row[1])),	// y1
							new Position(
								Integer.parseInt(row[2]),	// x2
								Integer.parseInt(row[3]))	// y2
							));
						break;
						
					case PARSE_MATRIX:								// matrix
						if (matrix == null)
							matrix = new boolean[row.length][row.length];
						
						for (int i = 0; i != row.length; ++i) {
							matrix[mat_idx][i] = "1".equals(row[i]);
						}

						++mat_idx;
						break;
						
					default:										// parse mode/general
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
								
							case "matrix":
								parse_mode = PARSE_MATRIX;
								break;
								
							default:
								map.setField(ls[0], ls[1]);
								break;
						}

						break;
				}

			}
		}
		if(in != null)
			in.close();                                    // RAII semantics are really useful

		map.setMatrix(matrix);
		
		return map;
	}
}
