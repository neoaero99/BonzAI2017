package Castles.Objects;

import Castles.api.Color;
import bonzai.Team;

public class Village extends Building {

	public Village(int newDefenseValue, int newCaptureValue, int newID) {
		super(10, 10, newID);
		// TODO Auto-generated constructor stub
	}

	public Village(int x, int y, String newID, Team c) {
		super(x, y, 10, 10, newID, c, 0);
		// TODO Auto-generated constructor stub
	}

}
