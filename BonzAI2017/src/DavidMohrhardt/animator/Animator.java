package DavidMohrhardt.animator;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author David Mohrhardt
 * @version 0.02
 * 
 * This is the animator package.
 * 
 * This file is the Animator object.  It handles the interface between the programmer and 
 * the sprite.  It also builds the sprite and it's action set.
 * 
 */
public class Animator {

	// Primitive Types Needed by the Animator
	int current_frame;

	// Objects needed by animator
	private Sprite sprite;
	private String current_action;
	private ArrayList<String> actions;


	// Functions needed by animator
	/**
	 * Animator(String spritePath, String spriteScript)
	 * 
	 * @param spritePath The path to the sprite sheet
	 * @param spriteScript The path to the spite script
	 */
	public Animator(String spritePath, String spriteScript) {
		sprite = new Sprite(spritePath, spriteScript);
		actions = sprite.getActions();
	}


	/**
	 * startActionAnimation(String Action)
	 * 
	 * @param action The action that is to be animate as dictated by the script.
	 * @return The first frame of the action that is to be animated
	 */
	public BufferedImage startActionAnimation(String action) {
		current_frame = 0;
		current_action = action;

		return sprite.getFrame(action, current_frame);
	}

	/**
	 * getNextFrame()
	 * 
	 * @return The next frame of the current action.
	 */
	public BufferedImage getNextFrame() {
		++current_frame;
		
		if ( !sprite.checkIndex(current_action, current_frame) ) {
			current_frame = 0;
		}

		return sprite.getFrame(current_action, current_frame);
	}

	/**
	 * getFrameAtIndex(String action, int index)
	 * 
	 * @param action The string indicating what action the Animator should be using to get the frame
	 * @param index The index of the frame that we want to get.
	 * 
	 * @return A subimage of the sprite's sprite sheet
	 */
	public BufferedImage getFrameAtIndex(String action, int index) {
		current_action = action;
		current_frame = index;

		return sprite.getFrame(current_action, current_frame);
	}
	
	/**
	 * getSpriteSizeX()
	 * 
	 * @return The size of a single frame of the sprite in pixels on the X-axis
	 */
	public int getSpriteSizeX() {
		return sprite.getSizeX();
	}
	
	/**
	 * getSpriteSizeY()
	 * 
	 * @return The size of a single frame of the sprite in pixels on the Y-axis
	 */
	public int getSpriteSizeY() {
		return sprite.getSizeY();
	}

	// Debugging information
	/**
	 * verifyActionExists(String Action)
	 * 
	 * @param action The string indicating what action the Animator should be looking for.
	 * @return A boolean that indicates whether the passed action string is a valid string
	 */
	public boolean verifyActionExists(String action) {
		if (actions.contains(action)) {
			return true;
		}

		return false;
	}
	
	/**
	 * debugSprite(int i)
	 * 
	 * @param i The integer value that indicates what information the debugger should print out.  
	 * 0 = All Information of the Sprite.  
	 * 1 = General Information of the Sprite.
	 */
	public void debugSprite(int i) {
		sprite.debugSprite(i);
	}

}