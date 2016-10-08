package castle;

import java.awt.Color;

public class Building {
	
	final int ID;						   // the ID of the building
	private final int defenseValue;        // how strong is the castle?
	private int soldiers;                  // the number of soldiers in the castle
	private int counter;                   // 
	private final int captureValue;        // number of turns to capture building
	private int soldierCreationRate;       // number of soldiers being produced (more details needed!)
	
	Color color = null;                    // not sure what this is for yet
	
	public Building(int newDefenseValue, int newCaptureValue, int newID){
		defenseValue = newDefenseValue;
		captureValue = newCaptureValue;
		ID = newID;
		soldierCreationRate = 0;
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
	
	
	
}
