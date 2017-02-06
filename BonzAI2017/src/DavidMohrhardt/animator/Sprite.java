package DavidMohrhardt.animator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author David Mohrhardt
 * @version 0.01
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

	private String current_action;

	/**
	 * Sprite(String spritePath, String spriteScript)
	 * 
	 * @param spritePath The path to the sprite sheet for this sprite.
	 * @param spriteScript The path to the sprite script for the given sprite sheet.
	 */
	public Sprite(String spritePath, String spriteScript) {
		SpriteBuilder parser = new SpriteBuilder(spritePath, spriteScript);

		name 			= parser.getName();
		sprite_sheet 	= parser.getSpriteSheet();
		size_x 			= parser.getSizeX();
		size_y 			= parser.getSizeY();
		padding_x 		= parser.getPaddingX();
		padding_y 		= parser.getPaddingY();
		actions 		= parser.getActions();

		parser = null; // Deletes the parser
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
		int row = actions.get(action).getRow();

		return sprite_sheet.getSubimage((index * size_x) + (index * padding_x), 
				(row * size_y) + (row * padding_y),
				size_x,
				size_y
				);
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
	
	
	// Any Debug Methods I believe I need

	/**
	 * checkIndex(int index)
	 * 
	 * @param index The index of the frame we are trying to get
	 * 
	 * @return boolean Whether the index is in bounds or not.
	 */
	public boolean checkIndex(int index) {
		if (index < actions.get(current_action).getNumberOfFrames() ) {
			return true;
		}

		return false;
	}

	public void debugSprite() {
		// This is a method designed to simply print sprite information
		// TODO
	}

}
