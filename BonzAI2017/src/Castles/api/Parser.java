package Castles.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import Castles.Objects.*;
import Castles.util.graph.SegEdge;
import Castles.util.graph.Vertex;
import Castles.util.VectorND;
import Castles.util.graph.CastlesMapGraph;
import Castles.util.linkedlist.DualLinkList;
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
		
		int width = 0, height = 0;
		HashMap<String, String> fields = new HashMap<String, String>();
		
		HashMap<String, RallyPoint> graphElements = new HashMap<String, RallyPoint>();
		
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
					r = new Castle(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], newTeam.getColor());
					graphElements.put(r.ID, r);
					vertices.add(new Vertex(r.ID));
					break;

				case PARSE_CASTLE:
					r = new Castle(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], null);
					graphElements.put(r.ID, r);
					vertices.add(new Vertex(r.ID));
					break;
					
				case PARSE_RALLY:
					r = new RallyPoint(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2]);
					graphElements.put(r.ID, r);
					vertices.add(new Vertex(r.ID));
					break;
					
				case PARSE_PATH:
					// Connect the two vertices and add the edge
					Vertex v1 = getVertex(vertices, row[0]);
					Vertex v2 = getVertex(vertices, row[1]);
					SegEdge edge = new SegEdge(Integer.parseInt(row[2]), v1, v2);
					
					edges.add(edge);
					/* Build the way point list associated with the edge and
					 * add the points to the graph elements */
					RallyPoint r1 = graphElements.get(row[0]);
					RallyPoint r2 = graphElements.get(row[1]);
					RallyPoint[] wayPoints = buildWayPoints(edge, r1.getPosition(), r2.getPosition());
					
					for (RallyPoint rp : wayPoints) {
						graphElements.put(rp.ID, rp);
					}
					break;
					
				case PARSE_VILLAGE:
					r = new Village(Integer.parseInt(row[0]), Integer.parseInt(row[1]), row[2], null);
					graphElements.put(r.ID, r);
					vertices.add(new Vertex(r.ID));
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
		
		return new CastlesMap(fields, graphElements, new CastlesMapGraph(vertices, edges), teams, width, height);
	}
	
	private static Vertex getVertex(ArrayList<Vertex> list, String s){
		for (Vertex v : list){
			if (v.ID.equals(s)){
				return v;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns a set of rally points that are associated with the given
	 * segEdge.
	 * 
	 * @param e	The edge for which to build the list of way points
	 * @return	The list of way points associated with the given edge
	 */
	private static RallyPoint[] buildWayPoints(SegEdge e, Position start, Position end) {
		RallyPoint[] waypoints = new RallyPoint[e.getWeight()];
		
		/* Create a direction vector, which is scaled by the the distance
		 * between the first and second vertices over the weight of this
		 * edge. */
		double dist = start.getDistanceBetween(end);
		VectorND dir = new VectorND(end.getX() - start.getX(),
				end.getY() - start.getY());
		dir.scalarMult( dist / ((e.getWeight() + 1) * dir.magnitude()) );
		
		// Define the positions of each way point
		for (int idx = 1; idx <= e.getWeight(); ++idx) {
			int posX = start.getX() + (int)(idx * dir.get(0));
			int posY = start.getY() + (int)(idx * dir.get(1));
			
			String rID = String.format("%s:%s", e.ID, idx);
			waypoints[idx - 1] = new RallyPoint(posX, posY, rID);
		}
		
		return waypoints;
	}
}