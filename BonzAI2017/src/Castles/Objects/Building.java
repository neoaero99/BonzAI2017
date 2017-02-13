package Castles.Objects;

import Castles.api.Color;

public class Building extends RallyPoint {
	
	public final int defenseValue;         // how strong is the castle?
	public final int soldierCreationRate;  // number of soldiers being produced (more details needed!)
	
	private Color myTeamColor;
	
	public Building(int x, int y, String id, Color teamColor, int defValue, int soldSpawnRate) {
		super(x, y, id);
		
		myTeamColor = teamColor;
		defenseValue = defValue;
		soldierCreationRate = soldSpawnRate;
	}
	
	public Color getTeamColor() {
		return myTeamColor;
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
				
		if (myTeamColor != null) {
			/* Add the reinforcements to an already existing soldier group or add a
			 * new soldier group. */
			if (onPoint.size() > 0 && onPoint.get(0).getLeaderColor().equals(myTeamColor)&&onPoint.get(0).getState()==SoldierState.STANDBY) {
				Soldier occupant = onPoint.get(0);
				occupant.setValue(occupant.getValue() + soldierCreationRate);
				return null;
				
			} else {
				// Create a new soldier and return it to be added to the map
				Soldier newSoldier = new Soldier(myTeamColor, soldierCreationRate, ID);
				return newSoldier;
			}
		}
		
		return null;
	}
	
	/*
	 * Decrements soldier count by one.
	 */
	protected void decrementSoldier(){
		//soldiers -= 1;
	}
	
	public boolean isControled(){
		if(myTeamColor == null) return false;
		return true;
	}
	
	public boolean isControledBy(Castles.api.Color team){
		if(myTeamColor == team) return true;
		return false;
	}
	
	@Override
	public RallyPoint copy() {
		Building copy = new Building(getPosition().getX(), getPosition().getY(),
				ID, myTeamColor, defenseValue, soldierCreationRate);
		
		return copy;
	}

	public void setTeamColor(Color teamColor) {
		myTeamColor = teamColor;
	}
}
