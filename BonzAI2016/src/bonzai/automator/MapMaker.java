package bonzai.automator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import bonzai.Position;
import lazers.api.Rotatable;

class Entry {
	int x,y;
	float rotation;
	boolean hasRotation = false;
	
	public Entry(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Entry(int x, int y, float rotation) {
		this(x,y);
		this.rotation = rotation;
		hasRotation = true;
	}
	
	public String toString() {
		if (hasRotation) {
			return "    <" + x + ", " + y + ", " + rotation + ">";
		}
		return "    <" + x + ", " + y + ">";
	}
}

public class MapMaker {
	public static void main(String[] args) throws Exception{
		
		System.out.println("Key:");
		System.out.println("     Repeaters: #0000ff (blue)");
		System.out.println("     Emitters:  #fff000 (yellow)");
		System.out.println("     Targets:   #ff00ff (red)");
		System.out.println("     Walls:     #000000 (black)");
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
		
		ArrayList<Entry> repeaters = new ArrayList<Entry>();
		ArrayList<Entry> emitters = new ArrayList<Entry>();
		ArrayList<Entry> walls = new ArrayList<Entry>();
		ArrayList<Entry> targets = new ArrayList<Entry>();
		
		//Populated afterward with duplicates
		ArrayList<Entry> rotatable = new ArrayList<Entry>();
		
		
		
		//COUNT THE PLAYERS
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int c = image.getRGB(x, y);
				c &= 0x00ffffff;	//Ignore first 2 for transparency
				
				
				//Calculate the angle to the center of the map
				float xi = x - (image.getWidth() / 2.0f);
				float yi = y - (image.getHeight() / 2.0f);
				
				//Get the angle fom xi,yi to 0 0
				float rotation = (float)Math.toDegrees(Math.atan(yi/xi));
				if (xi > 0 / 2.0f) { rotation += 180; }
				
				if (point == 2) {
					rotation += 180;
				}else if (point == 3) {
					rotation = (float)Math.random() * 360;
				}
				
				
				if (c == 0x00ff0000) {
					targets.add(new Entry(x,y));
				} else if (c == 0x00000000) {
					walls.add(new Entry(x,y));
				} else if (c == 0x00fff000) {
					emitters.add(new Entry(x,y,rotation));
				} else if (c == 0x000000ff) {
					repeaters.add(new Entry(x,y,rotation));
				} else if (c == 0x00ffffff) {
					//White!
				} else {
					System.out.printf("Color 0x%x unknown at x=%d y=%d\n",c,x,y);
				}
			}
		}
		
		rotatable.addAll(emitters);
		rotatable.addAll(repeaters);
		
		/********************************
		 * TOWARD / AWAY NEAREST TARGET *
		 ********************************/
		if (point == 4 || point == 5) {
			//Find the nearest target
			for (Entry r : rotatable) {
				//Find the closest target
				Entry nearest = targets.get(0);
				for (Entry t : targets) {
					if (distance(t.x, t.y, r.x, r.y) < distance(nearest.x, nearest.y, r.x, r.y)) {
						nearest = t;
					}
				}
				
				//Point at it
				r.rotation = getRotation(r, nearest);
				if (point == 5) {
					r.rotation += 180;
				}
			}
			
		}
		
		/********************************
		 * TOWARD / AWAY NEAREST WALL   *
		 ********************************/
		if (point == 6 || point == 7) {
			//Find the nearest target
			for (Entry r : rotatable) {
				//Find the closest target
				Entry nearest = targets.get(0);
				for (Entry t : walls) {
					if (distance(t.x, t.y, r.x, r.y) < distance(nearest.x, nearest.y, r.x, r.y)) {
						nearest = t;
					}
				}
				
				//Point at it
				r.rotation = getRotation(r, nearest);
				if (point == 7) {
					r.rotation += 180;
				}
			}
			
		}
		
		/**********************************
		 * TOWARD / AWAY NEAREST REPEATER *
		 **********************************/
		if (point == 8 || point == 9) {
			//Find the nearest target
			for (Entry r : rotatable) {
				//Find the closest target
				Entry nearest = targets.get(0);
				for (Entry t : repeaters) {
					if (distance(t.x, t.y, r.x, r.y) < distance(nearest.x, nearest.y, r.x, r.y)) {
						nearest = t;
					}
				}
				
				//Point at it
				r.rotation = getRotation(r, nearest);
				if (point == 9) {
					r.rotation += 180;
				}
			}
			
		}
		
		
		pw.println("playercount: " + emitters.size());
		pw.println();
		pw.println("players:");
		for (Entry s : emitters) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("repeaters:");
		for (Entry s : repeaters) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("targets:");
		for (Entry s : targets) {
			pw.println(s);
		}
		
		pw.println();
		pw.println("walls:");
		for (Entry s : walls) {
			pw.println(s);
		}
		
		pw.close();
		System.out.println("DONE!");
		System.out.println("Be sure to move this into the scenarios folder");
		System.out.println("Current: " + f.getAbsolutePath());
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
