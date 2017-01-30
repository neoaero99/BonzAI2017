package Castles.Objects;

import bonzai.Team;

public class Building extends RallyPoint {
	
	public final int defenseValue;         // how strong is the castle?
	public final int soldierCreationRate;  // number of soldiers being produced (more details needed!)
	
	private Team myTeam;
	
	public Building(int x, int y, String id, Team newTeam, int defValue, int soldSpawnRate) {
		super(x, y, id);
		
		myTeam = newTeam;
		defenseValue = defValue;
		soldierCreationRate = soldSpawnRate;
	}
	
	/**************************
	 *     Soldier Stuff      *
	 **************************/
	
	public Castles.api.Color getColor() {
		return myTeam == null ? null : myTeam.getColor();
	}
	
	public RallyPoint copy(){
		return new Building(getPosition().getX(), getPosition().getY(), ID, myTeam, defenseValue, soldierCreationRate);
	}
	
	public Team getTeam(){
		return myTeam;
	}

	public void setTeam(Team leader) {
		myTeam=leader;
		
	}
}
