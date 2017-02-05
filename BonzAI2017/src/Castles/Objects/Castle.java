package Castles.Objects;

import Castles.api.Color;

public class Castle extends Building {

	public Castle(int x, int y, String id, Color c) {
		super(x, y, id, c, 5, 3);
	}
	public Castle(int x, int y,String newID){
		super(x,y,0,0,newID,null,0);
	}
	
	public Castle(int x, int y, String id, Color c, int defVal, int soldSpawnRate) {
		super(x, y, id, c, defVal, soldSpawnRate);
	}
}
