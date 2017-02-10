package bonzai.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 * A simple UI style for a slider which represents a the buffered tracking/seek
 * selection in a set of playback controls. Only valid for a horizontal slider.
 **/
public class TrackSliderUI extends BasicSliderUI {
	/** A specific reference to the buffered version of a slider **/
	protected BufferedSlider slider;

	/**
	 * Constructs the buffered tracking/seek slider's UI.
	 *
	 * @param the
	 *            slider to style
	 **/
	public TrackSliderUI(BufferedSlider slider) {
		super(slider);
		this.slider = slider;
	}

	/**
	 * Draws the specified component on the specificed Graphics context. This
	 * function attempts to set the antialiasing render hint and then calls the
	 * super class paint method.
	 *
	 * @param g
	 *            the Graphics context on which to paint
	 * @param c
	 *            the JComponent to paint
	 **/
	@Override
	public void paint(Graphics g, JComponent c) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.paint(g, c);
	}

	/**
	 * The thumb size. Used to determine the vertical scale of the track and the
	 * extent to which the thumb can be selected or is redrawn.
	 *
	 * @return the dimension of the thumb
	 **/
	@Override
	protected Dimension getThumbSize() {
		return new Dimension(3, 16);
	}

	/**
	 * Paint the track.
	 *
	 * @param g
	 *            the Graphics context on which to paint
	 **/
	@Override
	public void paintTrack(Graphics g) {
		final int x0 = trackRect.x;
		final int x1 = trackRect.x + trackRect.width;
		final int y0 = trackRect.y + 2;
		final int y1 = trackRect.y + trackRect.height;

		// assumes minimum value of 0
		@SuppressWarnings("unused")
		final int val = slider.getValue();
		final int avl = slider.getAvailable();
		final int max = slider.getMaximum();
		final int pos = max == 0 ? 0 : avl * (x1 - x0) / max + 1;

		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(x0, y0, x1 - x0, y1 - y0, 5, 5);

		g.setColor(Color.GRAY);
		g.fillRoundRect(x0, y0, pos, y1 - y0, 5, 5);

		g.setColor(Color.BLACK);
		for (int x = 1; x < 20; x += 1) {
			g.drawLine(x0 + (int) (x * trackRect.width / 20.), y1 - 6, x0
					+ (int) (x * trackRect.width / 20.), y1);
		}
	}

	/**
	 * Paint the thumb.
	 *
	 * @param g
	 *            the Graphics context on which to paint
	 **/
	@Override
	public void paintThumb(Graphics g) {
		final int x0 = thumbRect.x;
		final int x1 = thumbRect.x + 1;
		final int y0 = thumbRect.y;
		final int y1 = thumbRect.y + thumbRect.height + 1;

		g.setColor(Color.WHITE);
		g.drawLine(x0, y0, x0, y1);
		g.drawLine(x1, y0, x1, y1);
	}

	/**
	 * Changes the slider value based on a click in the track.
	 * <p>
	 * The default behavior is to move the thumb a fixed distance from its
	 * current location in the relative direction of the click. The overridden
	 * behavior is to relocate the thumb to the exact location of the click.
	 * <p>
	 * This method simply requests a tracking/seek operation. Due to the
	 * buffered nature of this slider, it is up to the implementation if this
	 * operation is allowed or ignored.
	 *
	 * @param dir
	 *            ignored
	 **/
	@Override
	public void scrollDueToClickInTrack(int dir) {
		slider.setValue(this.valueForXPosition(slider.getMousePosition().x));
	}
}