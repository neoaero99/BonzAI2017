package bonzai.gui;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;

/**
 * A simple MouseAdapter which uses dragged and wheel moved events to produce
 * scale and translate transformations. The transformations can then be applied
 * to a Graphics2D object.
 * <p>
 * Dragging the mouse will accumulate a translation transformation. The applied
 * translation is scaled by the scaling transformation so that movement remains
 * smooth regardless of the scaling factor. Moving the mouse wheel will
 * accumulate a scaling transformation. The zoom effect will always be relative
 * to the center of the screen.
 **/
public class AffineMouseAdapter extends MouseAdapter {
	/** The scaling transformation. **/
	protected final AffineTransform scale;

	/** The translation transformation. **/
	protected final AffineTransform translate;

	/** The previous mouse pressed / released event **/
	protected MouseEvent previous;

	/**
	 * Constructs a new mouse adapter with no scale or translation applied.
	 **/
	public AffineMouseAdapter() {
		this.scale = new AffineTransform();
		this.translate = new AffineTransform();

		this.previous = null;
	}

	/**
	 * Apply the transformations.
	 *
	 * @param g
	 *            the Graphics2D object on which to apply the transformations.
	 **/
	public void apply(Graphics2D g) {
		g.transform(scale);
		g.transform(translate);
	}

	/**
	 * Reset the current translations to their identity.
	 **/
	public void reset() {
		scale.setToIdentity();
		translate.setToIdentity();
	}

	/**
	 * Captures the mouse event.
	 * <p>
	 * Invoked when a mouse button has been pressed on a component.
	 **/
	@Override
	public void mousePressed(MouseEvent e) {
		previous = e;
	}

	/**
	 * Resets the captured mouse event.
	 * <p>
	 * Invoked when a mouse button has been released on a component.
	 **/
	@Override
	public void mouseReleased(MouseEvent e) {
		previous = null;
	}

	/**
	 * Accumulates a translation given the current and previous mouse event
	 * positions. The translation is scaled by the current scaling factor.
	 **/
	@Override
	public void mouseDragged(MouseEvent e) {
		double deltaX = (e.getX() - previous.getX()) / scale.getScaleX() / 250;
		double deltaY = (e.getY() - previous.getY()) / scale.getScaleY() / 250;
		translate.translate(deltaX, deltaY);

		previous = e;
	}

	/**
	 * Accumulates a scale given the mouse wheel rotation. Different trackpads
	 * and mice will report different values for wheel rotation at different
	 * weights. The current implementation accounts for the extreme case of a
	 * Macbook Pro trackpad, by far the most sensitive of currently tested
	 * hardware.
	 **/
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double factor = Math.pow(1.5, -e.getWheelRotation());
		scale.scale(factor, factor);
	}
}