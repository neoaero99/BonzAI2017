package Castles.Objects;

import Castles.api.Color;

public class Village extends Building {

	public Village(int newDefenseValue, int newCaptureValue, int newID) {
		super(10, 10, newID);
		// TODO Auto-generated constructor stub
	}

	public Village(int x, int y, String newID, Color c) {
		super(x, y, 10, 10, newID, c);
		// TODO Auto-generated constructor stub
	}

}
