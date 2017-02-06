package DavidMohrhardt.animator;

/**
 * @author David Mohrhardt
 * @version 0.01
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
	 * getRow()
	 * 
	 * @return row The row at which this action is located on the spread sheet.
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
