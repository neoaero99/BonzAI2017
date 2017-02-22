package Castles.Objects;

import Castles.api.TeamColor;
import Castles.Objects.PType;

public class Building extends RallyPoint {
	
	/**
	 * The defense value for this building
	 */
	public static final int BASE_DV = 10,
							CASTLE_DV = 5,
							VILLAGE_DV = 3;
	/**
	 * The soldier spawn rate for this building
	 */
	public static final int	BASE_SR = 4,
							CASTLE_SR = 3,
							VILLAGE_SR = 2;	
	
	public final PType type;
	
	private TeamColor myTeamColor;
	
	public Building (int x, int y, String id, TeamColor team) {
		super(x, y, id);
		
		char prefix = id.charAt(0);
		
		if (prefix == 'P') {
			// Building is a player base
			type = PType.BASE;
			
		} else if (prefix == 'C') {
			// Building is a castle
			type = PType.CASTLE;
			
		} else if (prefix == 'V') {
			// Building is village
			type = PType.VILLAGE;
			
		} else {
			type = null;
		}
		
		myTeamColor = team;
	}
	
	public TeamColor getTeamColor() {
		return myTeamColor;
	}
	
	/**
	 * @return	The defense value associated with this building's type
	 */
	public int getDefVal() {
		
		switch (type) {
			case BASE:		return BASE_DV;
			case CASTLE:	return CASTLE_DV;
			case VILLAGE:	return VILLAGE_DV;
			default:		return 0;
		}
	}
	
	/**
	 * @return	The soldier spawn rate associated with this buidling's type
	 */
	public int getSoldSpwnRate() {
		switch (type) {
			case BASE:		return BASE_SR;
			case CASTLE:	return CASTLE_SR;
			case VILLAGE:	return VILLAGE_SR;
			default:		return 0;
		}
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
				occupant.setValue(occupant.getValue() + getSoldSpwnRate());
				return null;
				
			} else {
				// Create a new soldier and return it to be added to the map
				Soldier newSoldier = new Soldier(myTeamColor, getSoldSpwnRate(), ID);
				return newSoldier;
			}
		}
		
		return null;
	}
	
	@Override
	public RallyPoint copy() {
		Building copy = new Building(getPosition().getX(), getPosition().getY(),
				ID, myTeamColor);
		
		return copy;
	}

	public void setTeamColor(TeamColor teamColor) {
		myTeamColor = teamColor;
	}
}
