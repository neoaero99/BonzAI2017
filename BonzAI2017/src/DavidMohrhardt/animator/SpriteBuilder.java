package DavidMohrhardt.animator;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * @author David Mohrhardt
 * @version 0.01
 * 
 * This is the animator package.
 * 
 * This file is the SpriteBuilder.  The SpriteBuilder is essentially a parser for reading sprite
 * scripts and loading the sprite sheet into the program itself.
 * 
 */
class SpriteBuilder {

	private BufferedImage sprite_sheet;

	private String name;

	private int num_of_actions;
	private int size_x;
	private int size_y;
	private int padding_x;
	private int padding_y;

	HashMap<String, FrameData> actions = new HashMap<String, FrameData>();

	/**
	 * SpriteBuilder(String spritePath, String spriteScript)
	 * 
	 * @param spritePath The path to the sprite sheet.
	 * @param spriteScript The path to the script for the sprite sheet.
	 */
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

	/**
	 * getSpriteSheet()
	 * 
	 * @return BufferedImage return the loaded sprite sheet.
	 */
	public BufferedImage getSpriteSheet() {
		ColorModel cm = sprite_sheet.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = sprite_sheet.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	/**
	 * getName()
	 * 
	 * @return String The name of this sprite as interpreted by the parser.
	 */
	public String getName() {
		String name_copy = name;
		return name_copy;
	}
	
	/**
	 * getNumberOfActions()
	 * 
	 * @return int The number of actions in the sprite sheet.
	 */
	public int getNumberOfActions() {
		return num_of_actions;
	}

	/**
	 * getSizeX()
	 * 
	 * @return int The size of one frame in pixels on the x-axis
	 */
	public int getSizeX() {
		return size_x;
	}

	/**
	 * getSizeY()
	 * 
	 * @return int The size of one frame in pixels on the y-axis
	 */
	public int getSizeY() {
		return size_y;
	}

	/**
	 * getPaddingX()
	 * 
	 * @return int The size of the padding between frames in the X-axis
	 */
	public int getPaddingX() {
		return padding_x;
	}

	/**
	 * getPaddingY()
	 * 
	 * @return int The size of the padding between frames in the Y-axis
	 */
	public int getPaddingY() {
		return padding_y;
	}

	/**
	 * getActions()
	 * 
	 * @return The hash map of the action and it's respective frame data.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, FrameData> getActions() {
		return (HashMap<String, FrameData>) actions.clone();
	}

}
