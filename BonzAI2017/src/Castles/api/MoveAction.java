package Castles.api;

import java.util.ArrayList;

import javax.swing.text.html.parser.Element;

import Castles.Objects.*;
import Castles.util.graph.Vertex;
import bonzai.Action;

/**
 * The main action for AI's to take in the castles game for BonzAI 2017
 * 
 * 
 * Usage: Create a MoveAction at the beginning of an AI's Turn </br>
 * 		  Have AI add all the soldier movements it would like </br>
 * 		  to take in its turn.</br>
 * 		  Return the MoveAction at the end of the AI's Turn</br>
 * 
 * 
 * @author Dane Jensen
 * @author Team Secret (2016 - 2017)
 */
public class MoveAction implements Action{
	private String srcID, destID;
	private int splitAmount;
	
	/**
	 * 
	 * 
	 * @param sID
	 * @param dID
	 * @param splitAmt
	 */
	public MoveAction(String sID, String dID, int splitAmt) {
		srcID = sID;
		destID = dID;
		splitAmount = splitAmt;
	}
	
	/**
	 * @return
	 */
	public String getSrcID() {
		return srcID;
	}

	/**
	 * @return
	 */
	public String getDestID() {
		return destID;
	}

	/**
	 * @return
	 */
	public int getSplitAmount() {
		return splitAmount;
	}

	@Override
	public String toString(){
		return String.format("MOVE %s %d %s", srcID, splitAmount, destID);
	}
	
}
