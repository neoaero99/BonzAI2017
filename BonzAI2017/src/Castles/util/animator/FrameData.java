package Castles.util.animator;

import java.util.ArrayList;

public class FrameData {
	
	private String action;
	
	private int row;
	private int num_frames;
	
//	private ArrayList<Integer> frameSize_x;
	// Try to keep frameSize_y constant

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
	public FrameData(String action_name, int action_row, int number_columns) {
		action = action_name;
		row = action_row;
		num_frames = number_columns;
		
	}
}
