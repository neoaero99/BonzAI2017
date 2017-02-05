package Castles.Objects;

import Castles.api.Color;
import bonzai.Team;

public class Castle extends Building {

	public Castle(int x, int y,String newID,Team c) {
		super(x,y,0,0,newID,c, 0);
		// TODO Auto-generated constructor stub
	}
	public Castle(int x, int y,String newID){
		super(x,y,0,0,newID,null,0);
	}
	
}
