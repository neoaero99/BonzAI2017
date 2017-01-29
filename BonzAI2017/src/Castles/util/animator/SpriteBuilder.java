package Castles.util.animator;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class SpriteBuilder {

	// Types Needed by the SSC parser

	private BufferedImage sprite_sheet;

	private String name;

	private int num_of_actions;
	private int size_x;
	private int size_y;
	private int padding_x;
	private int padding_y;
	
	HashMap<String, FrameData> actions = new HashMap<String, FrameData>();

	// Constructor
	public SpriteBuilder(String spritePath, String spriteScript) {
		sprite_sheet = null;

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

		if (script != extension) {
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
				
				data = line.substring(line.lastIndexOf(":="), line.lastIndexOf(';'));
				switch (data_read) {
				case (0):
					name = data;
					break;
				case (1):
					num_of_actions = Integer.parseInt(data);
					break;
				case (2):
					size_x = Integer.parseInt(data);
					break;
				case (3):
					size_y = Integer.parseInt(data);
					break;
				case (4):
					padding_x = Integer.parseInt(data);
					break;
				case (5):
					padding_y = Integer.parseInt(data);
					break;
				}
				
				++data_read;
				
				// Read the action data
				if (data_read >= 6) {
					switch (data_read % 2) {
					case (0):
						action_name = data;
						break;
					case (1):
						action_data = new FrameData(action_name, data_read - 6, Integer.parseInt(data));
						actions.put(action_name, action_data);
						break;
					}
				}
			}

			sprite_script_reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/*
	 * 
	 * Returns the SpriteSheet
	 */
	public BufferedImage getSpriteSheet() {
		ColorModel cm = sprite_sheet.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = sprite_sheet.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public String getName() {
		String name_copy = name;
		return name_copy;
	}
	
	public int getSizeX() {
		return size_x;
	}
	
	public int getSizeY() {
		return size_y;
	}
	
	public int getPaddingX() {
		return padding_x;
	}
	
	public int getPaddingY() {
		return padding_y;
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, FrameData> getActions() {
		return (HashMap<String, FrameData>) actions.clone();
	}

}
