package DavidMohrhardt.animator;

import java.awt.image.BufferedImage;

/**
 * @author David Mohrhardt
 * @version 0.02
 * 
 * This is the animator package.
 * 
 * This file is the FrameData Object.  It is essentially an object that stores the information needed
 * to find the place where the action frame is on the sprite sheet.  The Sprite uses this object as a
 * part of the HashMap of actions to indicate where the action is on the sprite sheet.
 * 
 */
class FrameData {

	private String action;

	private int row;
	private int num_frames;
	private BufferedImage[] frames;


	/**
	 * FrameData(String action_name, int action_row, int number_columns)
	 * 
	 * @param action_name The name of the action that corresponds with this frame data
	 * @param action_row The row on the sprite sheet that contains the action that corresponds with this FrameData
	 * @param number_columns The number of Frames that makeup this column of animation.
	 */
	public FrameData(String action_name, int action_row, int number_columns) {
		action = action_name;
		row = action_row;
		num_frames = number_columns;
		frames = new BufferedImage[number_columns];
	}

	/**
	 * parseFrames(BufferedImage sprite_sheet, int size_x, int size_y, int padding_x, int padding_y)
	 * 
	 * @param sprite_sheet The sprite sheet buffered image that the frames are to be parsed from.
	 * @param size_x The size of a frame in pixels on the X-axis
	 * @param size_y The size of a frame in pixels on the Y-axis
	 * @param padding_x The amount of padding between frames on the X-axis
	 * @param padding_y The amount of padding between frames on the Y-axis
	 */
	protected void parseFrames(BufferedImage sprite_sheet, int size_x, int size_y, int padding_x, int padding_y) {

		for(int i = 0; i < num_frames; i++) {
			
			frames[i] = sprite_sheet.getSubimage(
					(i * size_x) + (i * padding_x), 
					(row * size_y) + (row * padding_y),
					size_x,
					size_y
					);
		}
	}

	/**
	 * getAction()
	 * 
	 * @return String the string that indicates what action this frame data pertains to.
	 */
	public String getAction() {
		return action;
	}

	/**
	 * getFrame(int index)
	 * 
	 * @param index The index of the frame that needs to be returned.
	 * @return The subimage that makes up a single frame of the action.
	 */
	public BufferedImage getFrame(int index) {
		return frames[index];
	}

	/**
	 * getRow()
	 * 
	 * @return The row at which this action is located on the spread sheet.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * getNumberOfFrames()
	 * 
	 * @return num_frames The number of frames total that make up an action animation
	 */
	public int getNumberOfFrames() {
		return num_frames;
	}


}
