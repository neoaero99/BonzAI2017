package Castles.Objects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComponent;
import Castles.util.graph.Node;
import bonzai.*;

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
	
	private static final long serialVersionUID = 3166707557130028703L;
	// The shared sprite for the soldier
	public static BufferedImage sprite;
	// The combat value this unit has
	public int value;
	// The radius used in collision
	public float radius;
	
	/**
	 * So, I replaced your position reference to a node (which can be either
	 * a vertex or edge), so that a soldier can reference the graph directly.
	 * Also, all path generations return a linked list of edges, so I
	 * modified your given_path as such.
	 * 		- Joshua
	 */
	
	// The position of the soldier on the map (on screen)
	public Node position;
	// An array list that holds the IDs of all nodes in the solider's current
	public ArrayList<String> given_path;
	// The current status of the soldier (always defaults to standby)
	public SoldierState state;
	
	public Team leader;
	
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
	public Soldier(Node base_position) {
		value = 1;
		radius = 0f;
		position = base_position;
		given_path = null;
		
		state = SoldierState.STANDBY;
	}

}
