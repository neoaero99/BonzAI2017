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
		System.out.println();
		
		System.out.println("Please enter path to map file (must be .bmp)");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine();
		
		BufferedImage image = ImageIO.read(new File(path));
		System.out.println("Where would you like repeaters to point?");
		System.out.println("1:  Map center");
		System.out.println("2:  Away from map center");
		System.out.println("3:  Random");
		System.out.println("4:  Nearest Target");
		System.out.println("5:  Away From Nearest Target");
		System.out.println("6:  Nearest Wall");
		System.out.println("7:  Away From Nearest Walls");
		System.out.println("8:  Nearest Repeater");
		System.out.println("9:  Away From Nearest Repeater");
		
		
		int point = scanner.nextInt();
		
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
		ArrayList<Entry> paths = new ArrayList<Entry>();
		
		
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
				if (c == 0x00ff0000) {
					castles.add(new Entry(x,y,"C"+castles.size()));
				//black
				} else if (c == 0x00000000) {
					//paths.add(new Entry(x,y));
				//yellow
				} else if (c == 0x00fff000) {
					players.add(new Entry(x,y, "P"+players.size()));
				//blue
				} else if (c == 0x000000ff) {
					rallys.add(new Entry(x,y, "R"+rallys.size()));
				//white
				} else if (c == 0x00ffffff) {
				//not reconized
				} else {
					System.out.printf("Color 0x%x unknown at x=%d y=%d\n",c,x,y);
				}
			}
		}
		
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
		pw.println("rally:");
		for (Entry s : rallys) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("paths:");
		for (Entry s : paths) {
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
