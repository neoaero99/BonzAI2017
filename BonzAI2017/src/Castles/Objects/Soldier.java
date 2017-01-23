package Castles.Objects;

import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.JComponent;

import Castles.util.VectorND;
import Castles.util.graph.Node;
import Castles.util.graph.SegEdge;
import Castles.util.graph.WeightedEdge;
import Castles.util.graph.WeightedGraph;
import Castles.util.linkedlist.DualLinkList;
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
	
	// The shared sprite for the soldier
	public static BufferedImage sprite;
	// The combat value this unit has
	public int value = 1;
	// The radius used in collision
	public float radius = 0f;
	
	/**
	 * So, I replaced your position reference to a node (which can be either
	 * a vertex or edge), so that a soldier can reference the graph directly.
	 * Also, all path generations return a linked list of edges, so I
	 * modified your given_path as such.
	 * 		- Joshua
	 */
	
	// The position of the soldier on the map (on screen)
	public Node<RallyPoint> position;
	// An array list that is the current path (in nodes on the graph)
	// First element is the starting point, last element is the end point
	public DualLinkList<String> given_path;
	// the position in give_path
	private int pathPosition;
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
	public Soldier(Node<RallyPoint> base_position) {
		position = base_position;
		state = SoldierState.STANDBY;
		pathPosition=0;
	}
	public void gotoNext(WeightedGraph<RallyPoint,Integer> graph){
		pathPosition++;
		String ID=given_path.atIndex(pathPosition).getElement();
		//for()
		
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
	public static void main(String[] args){
		ArrayList<Soldier> test=new ArrayList<Soldier>();
		for(int i=0;i<10;i++){
			int temp=(int) (Math.random()*10);
			Soldier x=new Soldier(null);
			x.value=temp;
			test.add(x);
		}
		Soldier.quickSort(test);
		for(int i=0;i<10;i++){
			System.out.println(test.get(i).value);
		}
	}
	public Soldier copy(){
		Soldier temp =new Soldier(position);
		temp.given_path=given_path;
		temp.leader=leader;
		temp.pathPosition=pathPosition;
		temp.value=value;
		temp.sprite=sprite;
		temp.radius=radius;
		temp.state=state;
		return temp;
		
	}
}
