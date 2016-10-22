package bonzai.automator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

class Entry {
	int x,y;
	String name;
	boolean hasName = false;
	
	public Entry(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Entry(int x, int y, String name) {
		this(x,y);
		this.name = name;
		hasName = true;
	}

	public String getName(){
		return name;
	}
	
	public String toString() {
		if (hasName) {
			return "    <" + x + ", " + y + ", " + name + ">";
		}
		return "    <" + x + ", " + y + ">";
	}
	
	
}

class Path{
	ArrayList<Entry> points = new ArrayList<Entry>();
	int rx = 0, ry = 0;
	String name,v1,v2;
	char cd = 0;
	
	public Path(String name){
		this.name = name;
	}
	
	public boolean insertPoint(int x, int y){
		if(points.size() <= 1){
			if(points.size() == 0){
				points.add(new Entry(x,y));
			}else{
				if(points.get(0).x == x && points.get(0).y + 1 == y){
					cd = 'x';
					rx = x;
					points.add(new Entry(x,y));
				}else if(points.get(0).y == y && points.get(0).x + 1 == x){
					cd = 'y';
					ry = y;
					points.add(new Entry(x,y));
				}else{
					return false;
				}
			}
		}else if(y == ry && cd == 'y' && points.get(points.size()-1).x+1 == x){
			points.add(new Entry(x,y));
		}else if(x == rx && cd == 'x' && points.get(points.size()-1).y+1 == y){
			points.add(new Entry(x,y));
		}else{
			return false;
		}
		return true;
	}
	
	public char getCriticalDimention(){
		return cd;
	}
	
	public int[] getVertex1Coords(){
		int[] out = new int[2];
		if(cd == 'x'){
			out[0] = points.get(0).x;
			out[1] = points.get(0).y-1;
		}else{
			out[0] = points.get(0).x-1;
			out[1] = points.get(0).y;
		}
		return out;
	}
	
	public int[] getVertex2Coords(){
		int[] out = new int[2];
		if(cd == 'x'){
			out[0] = points.get(points.size()-1).x;
			out[1] = points.get(points.size()-1).y+1;
		}else{
			out[0] = points.get(points.size()-1).x+1;
			out[1] = points.get(points.size()-1).y;
		}
		return out;
	}
	
	public void setVertex1(String n){
		v1 = n;
	}
	
	public void setVertex2(String n){
		v2 = n;
	}
	
	@Override
	public String toString(){
		if(points.size() == 1) return null;
		return "    <" + v1 + ", " + v2 + ", " + points.size() + ">";
	}
}

public class MapMaker {
	public static void main(String[] args) throws Exception{
		
		System.out.println("Key:");
		System.out.println("     Rally: #0000ff (blue)");
		System.out.println("     Player:  #fff000 (yellow)");
		System.out.println("     Castles:   #ff00ff (red)");
		System.out.println("     Path:     #000000 (black)");
		System.out.println("     Village:  #008000 (green)");
		System.out.println();
		
		System.out.println("Please enter path to map file (must be .bmp)");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine();
		
		BufferedImage image = ImageIO.read(new File("/users/danejensen/bonzai2017/bonzai2017/bonzai2017/bonzai2017"
				+ "/src/bonzai/automator/images/" + path));
		
		String name = path.replace(".bmp", "");
		
		System.out.println("Saving map as " + name + ".dat");
		
		File f = new File("/users/danejensen/bonzai2017/bonzai2017/bonzai2017/bonzai2017"
				+ "/src/bonzai/automator/images/" + name + ".dat");
		PrintWriter pw = new PrintWriter(f);
		
		pw.println("# BONZAI CONFIDENTIAL");
		pw.println("# This map was auto generated");
		pw.println();
		
		pw.println("name: " + name);
		pw.println("size: " + image.getWidth() + ", " + image.getHeight());
		pw.println("theme: DEFAULT");
		
		ArrayList<Entry> rallys = new ArrayList<Entry>();
		ArrayList<Entry> players = new ArrayList<Entry>();
		ArrayList<Entry> castles = new ArrayList<Entry>();
		ArrayList<Path> paths = new ArrayList<Path>();
		ArrayList<Entry> villages = new ArrayList<Entry>();
		
		
		/*****************************************
		 * Read Bit Map and convert to entities  *
		 ****************************************/
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int c = image.getRGB(x, y);
				c &= 0x00ffffff;	//Ignore first 2 for transparency
				
				
				//Red = castles
				if (c == 0x00ff00ff) {
					castles.add(new Entry(x,y,"C"+castles.size()));
				//black = paths
				} else if (c == 0x00000000) {
					//Insert the point into an existing path if
					//it is on one of the path's critical dimentions
					//and is only 1 point away on its other dimention
					boolean inserted = false;
					for(Path p : paths){
						if(p.insertPoint(x, y)){
							inserted = true;
							break;
						}
					}
					if(!inserted){
						Path p;
						p = new Path("P" + paths.size());
						p.insertPoint(x, y);
						paths.add(p);
					}
				//yellow = players
				} else if (c == 0x00fff000) {
					players.add(new Entry(x,y, "P"+players.size()));
				//blue = rally points
				} else if (c == 0x000000ff) {
					rallys.add(new Entry(x,y, "R"+rallys.size()));
				//green = villages
				}else if (c == 0x00008000){
					villages.add(new Entry(x,y, "V"+villages.size()));
				//white = blank
				}else if (c == 0x00ffffff) {
				//not reconized
				} else {
					System.out.printf("Color 0x%x unknown at x=%d y=%d\n",c,x,y);
				}
			}
		}
		
		//Set name for each path
		//needs to find the end point on each side of the path
		//the end point is either a castle, village, rally point or player
		//put all parsed objects, except path points, into the same arraylist
		//and search that list for each end point of each path
		ArrayList<Entry> e = new ArrayList<Entry>();
		e.addAll(castles);
		e.addAll(villages);
		e.addAll(rallys);
		e.addAll(players);
		for(Path s : paths){
			int[] v1 = s.getVertex1Coords();
			int[] v2 = s.getVertex2Coords();
			for(int i = 0; i < e.size(); i++){
				if(e.get(i).x == v1[0] && e.get(i).y == v1[1]){
					s.setVertex1(e.get(i).getName());
					continue;
				}
			
				if(e.get(i).x == v2[0] && e.get(i).y == v2[1]){
					s.setVertex2(e.get(i).getName());
					continue;
				}
				
			}
			
		}
		/*******************************************************
		 * Prints the Parsed information to [inputed name].dat *
		 ******************************************************/
		pw.println("playercount: " + players.size());
		pw.println();
		pw.println("players:");
		for (Entry s : players) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("castles:");
		for (Entry s : castles) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("villages:");
		for(Entry s : villages){
			pw.println(s);
		}
		
		pw.println();
		pw.println("rally:");
		for (Entry s : rallys) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("paths:");
		for (Path s : paths) {
			pw.println(s);
		}
		
		pw.close();
		System.out.println("DONE!");
		System.out.println("Be sure to move this into the scenarios folder");
		System.out.println("Current: " + f.getAbsolutePath());
		scanner.close();
	}
	
	public static float distance(int x1, int y1, int x2, int y2) {
		float deltaX = (x2-x1);
		deltaX *= deltaX;
		float deltaY = (y2-y1);
		deltaY *= deltaY;
		return (float) Math.sqrt(deltaX + deltaY);
	}
	
	public static float getRotation(Entry a, Entry b) {
		return (float)Math.atan2(b.y - a.y, b.x - a.x) * (float)(180 / Math.PI);
	}
	
	
	
}
