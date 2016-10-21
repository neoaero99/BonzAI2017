package Castles.Objects;

import java.awt.Color;


import java.awt.Color;

public class Building extends RallyPoint {
	
							   // the ID of the building
	private final int defenseValue;        // how strong is the castle?
	private int soldiers;                  // the number of soldiers in the castle
	private int counter;                   // 
	private final int captureValue;        // number of turns to capture building
	private int soldierCreationRate;       // number of soldiers being produced (more details needed!)
	
	Castles.api.Color color = null;                    // not sure what this is for yet
	
	public Building(int newDefenseValue, int newCaptureValue, int newID){
		super(0,0,""+newID);
		defenseValue = newDefenseValue;
		captureValue = newCaptureValue;
		soldierCreationRate = 0;
	Castles.api.Color color = null;}                    // Determine which team is which
	
	public Building(int x, int y, int newDefenseValue, int newCaptureValue, String newID,Castles.api.Color c){
		super(x,y,newID);
		defenseValue = newDefenseValue;
		captureValue = newCaptureValue;
		soldierCreationRate = 0;
		color=c;
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
		return color;
	}
}
