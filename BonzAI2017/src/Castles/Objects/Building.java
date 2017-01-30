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
	
	public Castles.api.Color getColor() {
		return myTeam == null ? null : myTeam.getColor();
	}
	
	@Override
	public RallyPoint copy() {
		Building copy = new Building(getPosition().getX(), getPosition().getY(), ID, myTeam, defenseValue, soldierCreationRate);
		// Copy the soldier, who is occupying the space
		Soldier occupant = getOccupant();
		
		if (occupant != null) {
			copy.occupy(occupant.copy());
		}
		
		return copy;
	}
	
	public Team getTeam(){
		return myTeam;
	}

	public void setTeam(Team leader) {
		myTeam=leader;
		
	}
}
