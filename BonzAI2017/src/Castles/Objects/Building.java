package Castles.Objects;

import java.awt.Color;
import bonzai.*;

public class Building extends RallyPoint {
	
							   // the ID of the building
	private final int defenseValue;        // how strong is the castle?
	private int soldiers;                  // the number of soldiers in the castle
	private int counter;                   // 
	private final int captureValue;        // number of turns to capture building
	private int soldierCreationRate;       // number of soldiers being produced (more details needed!)
	private Team myTeam;
	
	public Building(int newDefenseValue, int newCaptureValue, int newID){
		super(0,0,""+newID);
		defenseValue = newDefenseValue;
		captureValue = newCaptureValue;
		soldierCreationRate = 0;
		myTeam= null;
		}                    // Determine which team is which
	
	public Building(int x, int y, int newDefenseValue, int newCaptureValue, String newID, Team newTeam){
		super(x,y,newID);
		defenseValue = newDefenseValue;
		captureValue = newCaptureValue;
		soldierCreationRate = 0;
		myTeam=newTeam;
	}
	
	/**************************
	 *     Defense Stuff      *
	 **************************/
	
	public int getDefenseValue(){
		return defenseValue;
	}
	
	/**************************
	 *     Soldier Stuff      *
	 **************************/
	
	public void setSoldierCount(int soldierCount){
		soldiers = soldierCount;
	}
	
	public int getSoldierCount(){
		return soldiers;
	}
	
	/*
	 * Increments soldier count by one.
	 */
	public void incrementSoldier(){
		soldiers += 1;
	}
	
	/*
	 * Decrements soldier count by one.
	 */
	public void decrementSoldier(){
		soldiers -= 1;
	}
	public Castles.api.Color getColor(){
		return myTeam.getColor();
	}
	public RallyPoint copy(){
		return new Building(super.getPosition().getX(),super.getPosition().getY(),defenseValue,captureValue,ID,myTeam);
	}
	
	public Team getTeam(){
		return myTeam;
	}
}
