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
	
	public Castles.api.Color getColor() {
		return myTeam == null ? null : myTeam.getColor();
	}
	
	/**
	 * Adds reinforcements to this building if it is controlled by an AI. If
	 * their is no one occupying this position, then a new soldier is created
	 * in the Standby state with no given path: this soldier is returned by
	 * this method. If an ally soldier is on this position, then that
	 * soldier's number increases instead. If a building is occupied by enemy
	 * soldiers, then no reinforcements will spawn.
	 * 
	 * @return	A new soldier, if it was created, or null
	 */
	public Soldier reinforce() {
				
		if (myTeam != null) {
			/* Add the reinforcements to an already existing soldier group or add a
			 * new soldier group. */
			if (onPoint.size() > 0 && onPoint.get(0).getLeader().getColor().equals(myTeam.getColor())) {
				Soldier occupant = onPoint.get(0);
				occupant.setValue(occupant.getValue() + soldierCreationRate);
				return null;
				
			} else {
				// Create a new soldier and return it to be added to the map
				Soldier newSoldier = new Soldier(myTeam, soldierCreationRate, ID);
				return newSoldier;
			}
		}
		
		return null;
	}
	
	@Override
	public RallyPoint copy() {
		Building copy = new Building(getPosition().getX(), getPosition().getY(),
				ID, myTeam, defenseValue, soldierCreationRate);
		
		return copy;
	}
	
	public Team getTeam(){
		return myTeam;
	}

	public void setTeam(Team leader) {
		myTeam=leader;
		
	}
}
