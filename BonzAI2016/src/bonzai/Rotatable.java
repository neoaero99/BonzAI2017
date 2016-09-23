package bonzai;


public interface Rotatable {
	
	/**
	 * @retrn the direction that the object is facing, in degrees.
	 * A value of 0 means that the object points straight to the right.
	 * A value of 180 means that the object points straight to the left.
	 */
	public float getRotation();
	

	/**
	 * @param degree the rotation, in degrees, that the object should point at
	 * A value of 0 means the object points to the right.
	 * A value of 180 means the object points to the left. 
	 */
	void setRotation(float degree);
	
	/**
	 * Rotate the element by the given degrees
	 * @param delta Degree of rotation
	 */
	void rotate(float delta);
}
