package bonzai.gui;

import javax.swing.JSlider;

/**
 * A simple extension to a JSlider adding the ability to track how much of the
 * slider's domain is available, starting from 0. The TrackSliderUI provides a
 * visualization for this additional slider parameter.
 **/
@SuppressWarnings("serial")
public class BufferedSlider extends JSlider {
	private int available;

	/**
	 * Creates a BufferedSlider, setting the min, max, and current values to 0.
	 **/
	public BufferedSlider() {
		super(0, 0, 0);
		this.available = 0;
	}

	/**
	 * Sets the extent of availability in the slider.
	 *
	 * @param available
	 *            the new availability
	 **/
	public void setAvailable(int available) {
		this.available = available;
		if (getValue() > available) {
			setValue(available);
		}
	}

	/**
	 * Returns the current availability.
	 *
	 * @return the current availability
	 **/
	public int getAvailable() {
		return available;
	}
}
