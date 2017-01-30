package Castles.Objects;

import bonzai.Team;

public class Castle extends Building {

	public Castle(int x, int y, String id, Team c) {
		super(x, y, id, c, 5, 3);
	}
	
	public Castle(int x, int y, String id, Team c, int defVal, int soldSpawnRate) {
		super(x, y, id, c, defVal, soldSpawnRate);
	}
}
