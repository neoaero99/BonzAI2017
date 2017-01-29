package Castles.util.animator;

import java.awt.image.BufferedImage;

import Castles.CastlesRenderer.*;

public class Animator {
	
	// Objects needed by animator
	private Sprite sprite;
	
	
	// Functions needed by animator
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
	public Animator(String spritePath, String spriteScript) {
		sprite = new Sprite(spritePath, spriteScript);
		
	}
	
	/*
	 * GetNextFrame()
	 * 
	 * Returns BufferedImage
	 * 
	 * This method will get the next frame of the given sprite based on the...
	 * 
	 */
	public BufferedImage getNextFrame() {
		return null;
	}
}