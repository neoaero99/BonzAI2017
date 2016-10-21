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
	
	public String toString() {
		if (hasName) {
			return "    <" + x + ", " + y + ", " + name + ">";
		}
		return "    <" + x + ", " + y + ">";
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
		
		File f = new File(name + ".dat");
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
		
		
		//COUNT THE PLAYERS
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int c = image.getRGB(x, y);
				c &= 0x00ffffff;	//Ignore first 2 for transparency
				
				
				//Calculate the angle to the center of the map
				/*float xi = x - (image.getWidth() / 2.0f);
				float yi = y - (image.getHeight() / 2.0f);*/
				
				//Get the angle fom xi,yi to 0 0
				/*float rotation = (float)Math.toDegrees(Math.atan(yi/xi));
				if (xi > 0 / 2.0f) { rotation += 180; }*/
				
				//Red
				if (c == 0x00ff00ff) {
					castles.add(new Entry(x,y,"C"+castles.size()));
				//black
				} else if (c == 0x00000000) {
					
				//yellow
				} else if (c == 0x00fff000) {
					players.add(new Entry(x,y, "P"+players.size()));
				//blue
				} else if (c == 0x000000ff) {
					rallys.add(new Entry(x,y, "R"+rallys.size()));
				//green
				}else if (c == 0x00008000){
					villages.add(new Entry(x,y, "V"+villages.size()));
				//white
				}else if (c == 0x00ffffff) {
				//not reconized
				} else {
					System.out.printf("Color 0x%x unknown at x=%d y=%d\n",c,x,y);
				}
			}
		}
		
		for(Path s : paths){
			System.out.println(s);
			
		}
		System.exit(0);
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
	
	
	private class Path{
		ArrayList<Entry> points = new ArrayList<Entry>();
		Integer rx = 0, ry = 0;
		String name;
		
		public Path(String name){
			this.name = name;
		}
		
		public int getRequiredX(){
			return rx;
		}
		
		public int getRequiredY(){
			return ry;
		}
		
		public boolean insertPoint(int x, int y){
			if(rx == 0 && ry == 0){
				if(points.size() == 0){
					points.add(new Entry(x,y));
				}else{
					if(points.get(0).x == x){
						rx = x;
						points.add(new Entry(x,y));
					}else if(points.get(0).y == y){
						points.add(new Entry(x,y));
					}else{
						return false;
					}
				}
			}else if(rx == 0 && y == ry){
				points.add(new Entry(x,y));
			}else if(ry == 0 && x == rx){
				points.add(new Entry(x,y));
			}else{
				return false;
			}
			return true;
		}
	}
}
