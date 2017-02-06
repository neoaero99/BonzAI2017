package Castles.Objects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import Castles.api.Color;

/**
 * Class:	Soldier.java
 * Ver:		0.01
 * 
 * @author David Mohrhardt
 *
 * Description:
 * This is the basic soldier class.  What this classes intent is to create
 * a drawable soldier that contains a reference to the soldier image, a value
 * that indicates the soldiers raw power, a position to draw said soldier at
 * and an animator to animate the soldier as he moves.  It will contain methods
 * that allow it to be drawn to the screen, allow it to move around, allow the AI
 * to send it to other places on the fly.
 * 
 * Any changes to this class need to be documented below.  If you change anything
 * in this class please leave your name, when you made the changes, and what you
 * changed like so:
 * 
 * Change Author: 	XXXXXXXX
 * Date:			MM/DD/YY
 * Change Descriptions:
 * 	- This is where you indicate the change and why
 * 
 * And increment the version number please.
 */

/**
 * @TODO
 * 
 * Inumeration for states of the soldier:
 * 
 * Team Color Flag
 * 
 * Sprite
 * 
 * Vertex Path List
 * 
 */

public class Soldier extends JComponent {
	
	// The radius used in collision
	public final static float radius;
	
	// The shared sprite for the soldier
	private static BufferedImage sprite;
	
	private static final long serialVersionUID = 3166707557130028703L;
	
	private Color leaderColor;
	// The combat value this unit has
	private int value;
	// The current status of the soldier (always defaults to standby)
	private SoldierState state;
	
	// An array list that holds the IDs of all nodes in the solider's current
	private ArrayList<String> given_path;
	// The ID of the position where the soldier is on the map
	private String posID;
	
	static {
		radius = 0f;
	}
	
	/**
	 * Method:	Soldier(VectorND base_position)
	 * Returns:	Void
	 * Takes:	VectorND base_position
	 * 				An <X,Y> position so that soldiers spawn in the right
	 * 				place.
	 * 
	 * Description:
	 * Creates a soldier at a given position.  Can be called by the castles.
	 * 
	 * @author David Mohrhardt
	 */
	public Soldier(Color teamColor, int iniVal, String posID) {
		leaderColor = teamColor;
		value = iniVal;
		state = SoldierState.STANDBY;
		
		this.posID = posID;
		given_path = new ArrayList<String>();
	}
	
	public static void quickSort(List<Soldier> s){
		if(s.size()<=1){
			return;
		}
		int part=0;
		int value=s.get(0).value;
		for(int i=1;i<s.size();i++){
			if(s.get(i).value>value){
				part++;
				Soldier temp=s.get(i);
				s.remove(i);
				s.add(0, temp);
			}
		}
		quickSort(s.subList(0, part));
		quickSort(s.subList(part+1, s.size()));
	}
	
	public Color getLeaderColor() {
		return leaderColor;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = Math.max(0, value);
	}

	public SoldierState getState() {
		return state;
	}

	public void setState(SoldierState state) {
		this.state = state;
	}
	
	public void setPath(ArrayList<String> path) {
		given_path = path;
	}
	
	public ArrayList<String> getPath() {
		return given_path;
	}
	
	/**
	 * 
	 * @return	The ID of the previous position of the soldier, or null if the
	 * 			soldier did not move
	 */
	public String updatePositionID() {
		if (state == SoldierState.MOVING && given_path.size() > 1) {
			String oldID = given_path.remove(0);
			posID = given_path.get(0);
			return oldID;
		}
		
		return null;
	}

	public String getPositionID() {
		return posID;
	}
	
	public Soldier copy(){
		Soldier temp = new Soldier(leaderColor, value, posID);
		
		if (given_path != null) {
			temp.given_path = new ArrayList<String>(given_path);
		}
		
		temp.state = state;
		
		return temp;
	}
	
	@Override
	public String toString() {
		return String.format("leader:%s state:%s val:%d pos:%s",
				leaderColor, state, value, posID);
	}
}
