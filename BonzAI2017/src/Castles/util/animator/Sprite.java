package Castles.util.animator;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Sprite {
	
	// Objects Useful to the sprite class
	
	
	// Types Needed by the Sprite
	private BufferedImage sprite_sheet;
	
	private String name;

	private int num_frames;
	private int size_x;
	private int size_y;
	private int padding_x;
	private int padding_y;
	
	private HashMap<String, FrameData> actions;
	
	private String currentAction;
//	private 
	
	/*
	 * Animator()
	 * 
	 * Arguments:
	 * 	String spritePath - The path to the sprite sheet
	 * 	String spriteScript - The path to the .ssc file that indicates what properties the sprite has
	 * 
	 * Description:
	 * 	This is a constructor for the animator used in Castles.  It will load into active memory the sprite
	 * sheet that is needed to draw and animate sprites.
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
	
	/*
	 * Animator()
	 * 
	 * Arguments:
	 * 	String spritePath - The path to the sprite sheet
	 * 	String spriteScript - The path to the .ssc file that indicates what properties the sprite has
	 * 
	 * Description:
	 * 	This is a constructor for the animator used in Castles.  It will load into active memory the sprite
	 * sheet that is needed to draw and animate sprites.
	 */
	
	
	
	/*
	 * Animator()
	 * 
	 * Arguments:
	 * 	String spritePath - The path to the sprite sheet
	 * 	String spriteScript - The path to the .ssc file that indicates what properties the sprite has
	 * 
	 * Description:
	 * 	This is a constructor for the animator used in Castles.  It will load into active memory the sprite
	 * sheet that is needed to draw and animate sprites.
	 */
	public void debugSprite() {
		// This is a method designed to simply print sprite information
		// TODO
	}
	
}
