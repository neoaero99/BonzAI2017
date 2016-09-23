package lazers.api;

import bonzai.Action;
import bonzai.Rotatable;
import bonzai.Position;
import bonzai.Positionable;

public class RotateAction implements Action {
	
	private final int target;
	private final float rotation;
	
	/**
	 * @param target the id of the rotatable object to rotate
	 * @param rotation the ending rotation, in degrees, that the object will rotate towards
	 */
	public RotateAction(int target, float rotation) {
		this.target = target;
		this.rotation = rotation;
	}
	
	/**
	 * @param source_id: the object to be rotated.
	 * @param toward: the position that the source object will point to
	 */
	public RotateAction(int source_id, Position initial, Position toward) {
		this.target = source_id;
		this.rotation = calcRotation(initial, toward);
	}
	
	/**
	 * @param initial: position of the object that will be rotated.
	 * @param toward: position that the initial object will point to.
	 */
	public static float calcRotation(Position s_pos, Position t_pos) {
		double dx = t_pos.getX() - s_pos.getX(), dy = t_pos.getY() - s_pos.getY();
		
		return (float)Math.asin(dy / dx);
		// Math.atan2(dx, dy) * 180 / Math.PI + 180
	}
	
	/**
	 * @return the id of the target object to rotate
	 */
	public int getTarget() {
		return target;
	}
	
	/**
	 * @return the new rotation that the target object should point at
	 */
	public float getRotation() {
		return rotation;
	}
}
