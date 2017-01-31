package Castles.Objects;

import java.util.ArrayList;

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
	
	public Building(int x, int y, String id, ArrayList<Soldier> occupants, Team newTeam, int defValue, int soldSpawnRate) {
		super(x, y, id, occupants);
		
		myTeam = newTeam;
		defenseValue = defValue;
		soldierCreationRate = soldSpawnRate;
	}
	
	public Castles.api.Color getColor() {
		return myTeam == null ? null : myTeam.getColor();
	}
	
	@Override
	public RallyPoint copy() {
		Building copy = new Building(getPosition().getX(), getPosition().getY(),
				ID, getOccupants(), myTeam, defenseValue, soldierCreationRate);
		
		return copy;
	}
	
	public Team getTeam(){
		return myTeam;
	}

	public void setTeam(Team leader) {
		myTeam=leader;
		
	}
}
