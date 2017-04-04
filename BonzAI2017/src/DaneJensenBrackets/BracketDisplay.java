package DaneJensenBrackets;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import bonzai.*;
import Castles.*;

public class BracketDisplay extends Component{

	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("Usage: java -jar RunBracket.jar <bracket file> <ai dir>");
		}
		String bfile = args[0];
		String aidir = args[1];
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		//read in bfile and parse into matches
	}
	
}
