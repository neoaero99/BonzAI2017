package Castles.Objects;

import Castles.api.Color;
import bonzai.Team;

public class Village extends Building {
	
	public Village(int x, int y, String id, Team c) {
		super(x, y, id, c, 3, 1);
	}
	
	public Village(int x, int y, String id, Team c, int defVal, int soldSpawnRate) {
		super(x, y, id, c, defVal, soldSpawnRate);
	}
}
