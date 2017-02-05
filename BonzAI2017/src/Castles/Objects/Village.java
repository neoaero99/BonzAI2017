package Castles.Objects;

import Castles.api.Color;

public class Village extends Building {
	
	public Village(int x, int y, String id, Color c) {
		super(x, y, id, c, 3, 1);
	}
	
	public Village(int x, int y, String id, Color c, int defVal, int soldSpawnRate) {
		super(x, y, id, c, defVal, soldSpawnRate);
	}
}
