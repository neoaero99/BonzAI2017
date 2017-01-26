package Castles.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import Castles.Objects.*;
import Castles.util.graph.SegEdge;
import Castles.util.graph.Vertex;
import Castles.util.graph.CastlesMapGraph;
import Castles.util.linkedlist.DualLinkList;
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
		
		int width = 0, height = 0;
		HashMap<String, String> fields = new HashMap<String, String>();
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		ArrayList<SegEdge> edges = new ArrayList<SegEdge>();
		
		ArrayList<Team> teams = new ArrayList<Team>();
		
		while (in.hasNextLine()) {
			String line = in.nextLine().trim();
			RallyPoint r = null;
			
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
					Team newTeam = new Team(Castles.api.Color.values()[teams.size()], teams.size());
					teams.add(newTeam);
					r = new Castle(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], newTeam);
					vertices.add(new Vertex(r));
					break;

				case PARSE_CASTLE:
					r = new Castle(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], null);
					vertices.add(new Vertex(r));
					break;
					
				case PARSE_RALLY:
					r = new RallyPoint(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					vertices.add(new Vertex(r));
					break;
					
				case PARSE_PATH:
					Vertex v1 = getVertex(vertices, row[0]);
					Vertex v2 = getVertex(vertices, row[1]);
					SegEdge edge = new SegEdge(Integer.parseInt(row[2]), v1, v2);
					
					edges.add(edge);
					break;
					
				case PARSE_VILLAGE:
					r = new Village(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], null);
					vertices.add(new Vertex(r));
					break;
					
				case PARSE_FIELD:
					if(!ls[0].equals("size")){
						fields.put(ls[0], ls[1]);
						System.out.println(ls[0]  + " set to " + fields.get(ls[0]));
					} else {
						String[] parts = ls[1].split(", ");
						width = Integer.parseInt(parts[0]);
						height = Integer.parseInt(parts[1]);
					}
					
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
			in.close(); // RAII semantics are really useful
		}
		
		return new CastlesMap(fields, new CastlesMapGraph(vertices, edges), teams, width, height);
	}
	
	private static Vertex getVertex(ArrayList<Vertex> list, String s){
		for (Vertex v : list){
			if (v.getElement().ID.equals(s)){
				return v;
			}
		}
		
		return null;
	}
}