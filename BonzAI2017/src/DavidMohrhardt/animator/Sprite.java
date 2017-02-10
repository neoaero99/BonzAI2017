package DavidMohrhardt.animator;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * @author David Mohrhardt
 * @version 0.02
 * 
 * This is the animator package.
 * 
 * This file is the Sprite object.  This file handles the containment of the sprite image data and
 * any queries for a particular frame.  It uses a SpriteBuilder object 
 * 
 */
class Sprite {

	private BufferedImage sprite_sheet;

	private String name;

	//	private int num_frames;
	private int size_x;
	private int size_y;
	private int padding_x;
	private int padding_y;

	private HashMap<String, FrameData> actions;

	/**
	 * Sprite(String spritePath, String spriteScript)
	 * 
	 * @param spritePath The path to the sprite sheet for this sprite.
	 * @param spriteScript The path to the sprite script for the given sprite sheet.
	 */
	public Sprite(String spritePath, String spriteScript) {
		sprite_sheet = null;
		actions = new HashMap<String, FrameData>();

		// Debugging
//		System.out.println("The sprite path is " + spritePath);
//		System.out.println("The Script path is " + spriteScript);

		// Get the sprite sheet
		try {
			sprite_sheet = ImageIO.read(new File(spritePath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Check for proper script extension
		String script = "ssc";
		String extension = spriteScript.substring(spriteScript.lastIndexOf(".") + 1, spriteScript.length());

		// Debugging
//		System.out.println("The Extension is " + extension + " and the script extension is " + script);

		if (script.compareTo(extension) != 0) {
			System.out.println("Improper file extension!  Please link a .ssc script!");
			System.exit(-1);
		}

		FileReader sprite_script;
		BufferedReader sprite_script_reader;

		String line;
		String data;
		int data_read = 0;

		String action_name = null;
		FrameData action_data = null;


		// Read the script and parse the information;
		try {
			sprite_script = new FileReader(spriteScript);

			sprite_script_reader = new BufferedReader(sprite_script);

			while ( (line = sprite_script_reader.readLine()) != null ) {
				if (line.charAt(0) == '*') {
					continue;
				}

				if (line.compareTo("END") == 0) {
					break;
				}
				
				data = line.substring(line.lastIndexOf(":=") + 2, line.lastIndexOf(';'));
				
				// debugging
//				System.out.println("Data = " + data + " and the dataread = " + data_read);

				switch (data_read) {
				case (0):
					name = data;
				break;
				
				case (1):
					size_x = Integer.parseInt(data);
				break;
				
				case (2):
					size_y = Integer.parseInt(data);
				break;
				
				case (3):
					padding_x = Integer.parseInt(data);
				break;
				
				case (4):
					padding_y = Integer.parseInt(data);
				break;
				
				default:
					break;

				}

				// Read the action data
				if (data_read >= 6) {
					
					// Debugging
//					System.out.println("Reading Frame Data " + data_read + " mod 2 = " + data_read % 2);
					
					switch (data_read % 2) {
					case (0):
						action_name = data;
					break;
					
					case (1):
						action_data = new FrameData(action_name, data_read - 7, Integer.parseInt(data));
					    actions.put(action_name, action_data);
					break;
					}
				}
				
				++data_read;
			}

			sprite_script_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		for (String x: actions.keySet()) {
			//debugging
//			System.out.println("String X = " + x);
			actions.get(x).parseFrames(sprite_sheet, size_x, size_y, padding_x, padding_y);
		}

//		parser = null; // Deletes the parser
	}

	/**
	 * getActions()
	 * 
	 * @return An arraylist of all the Keys of the HashMap for the actions.
	 */
	public ArrayList<String> getActions() {
		ArrayList<String> actionKeys = new ArrayList<String>();
		actionKeys.addAll(actions.keySet());
		return actionKeys;
	}

	// Things Needed for getting Actions

	/**
	 * getFrame(String action, int index)
	 * 
	 * @param action The Action of which the program is trying to get a frame of.
	 * @param index The index of the frame we are trying to get.
	 * @return BufferedImage The subimage that makes up a frame of the animation.
	 */
	public BufferedImage getFrame(String action, int index) {
		return actions.get(action).getFrame(index);
	}

	// Getters and Setters
	/**
	 * getName()
	 * 
	 * @return name The name of the current sprite.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * getSizeX()
	 * 
	 * @return The size of a single frame of the sprite on the X-Axis
	 */
	public int getSizeX() {
		return size_x;
	}
	
	/**
	 * getSizeY()
	 * 
	 * @return The size of a single frame of the sprite in pixels on the Y-Axis
	 */
	public int getSizeY() {
		return size_y;
	}


	// Any Debug Methods I believe I need
	/**
	 * checkIndex(int index)
	 * 
	 * @param index The index of the frame we are trying to get
	 * 
	 * @return boolean Whether the index is in bounds or not.
	 */
	public boolean checkIndex(String current_action, int index) {
		if (index < actions.get(current_action).getNumberOfFrames() ) {
			return true;
		}

		return false;
	}

	/**
	 * debugSprite(int i)
	 * 
	 * @param i Indicates what debugging statements you need. 0 = All Info, 1 = General Info
	 */
	public void debugSprite(int i) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("Debugging Sprite: " + name);
		
		switch(i) {
		// All Information
		case (0):
			System.out.println("Size_X = " + size_x);
			System.out.println("Size_Y = " + size_y);
			System.out.println("Padding_X = " + padding_x);
			System.out.println("Padding_Y = " + padding_y);
			for (String x: actions.keySet()) {
				System.out.println("Action = " + x);
				System.out.println("FrameData.row = " + actions.get(x).getRow());
				System.out.println("FrameData.numberOfFrames = " + actions.get(x).getNumberOfFrames());
			}
		break;

		// General Information
		case (1):
			System.out.println("Size_X = " + size_x);
			System.out.println("Size_Y = " + size_y);
			System.out.println("Padding_X = " + padding_x);
			System.out.println("Padding_Y = " + padding_y);
		break;

		// Current FrameData
		case (2):
			
			break;
		
		default:
			
			break;

		}
		
		System.out.println("End Debugging for " + name);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println();
	}

}
