package Castles.api;

import java.awt.Color;

/**
 * The possible colors associated with each team.
 * <p>
 * There is no strategic value to this enumeration.
 */
public enum TeamColor {
	RED(new Color(255, 0, 0)),
	YELLOW(new Color(255, 255, 0)),
	BLUE(new Color(0, 0, 255)),
	GREEN(new Color(0, 255, 0)),
	ORANGE(new Color(255, 119, 0)),
	PURPLE(new Color(162, 0, 255));
	
	private final Color c;
	
	private TeamColor(Color c) {
		this.c = c;
	}
	
	public Color getColor() {
		return c;
	}
}