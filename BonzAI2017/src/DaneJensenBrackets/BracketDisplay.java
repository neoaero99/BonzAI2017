package DaneJensenBrackets;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import bonzai.*;
import Castles.*;
import Castles.util.*;

import java.io.*;

public class BracketDisplay extends Component implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Match> matches = new ArrayList<>();

	public static void main(String[] args){
		if(args.length != 2){
			System.out.println("Usage: java -jar RunBracket.jar <bracket file> <ai dir>");
			System.exit(0);
		}
		String bfile = args[0];
		String aidir = args[1];
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		BracketDisplay b = new BracketDisplay();
		ArrayList<bonzai.Jar> ais = (ArrayList<bonzai.Jar>)b.getAIs(aidir);
		b.getMatches(ais, bfile);
		f.setTitle("Bonzai 2017 Brackets");
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		p.add(b, BorderLayout.CENTER);//makes my bracket display take up the entire screen
		f.add(p);
		//should be the LAST line of any UI code
		f.setVisible(true);
	}
	
	public java.util.List<bonzai.Jar> getAIs(String aiDir){
		ArrayList<bonzai.Jar> out = new ArrayList<>();
		File aid = new File(aiDir);
		String[] aiFiles = aid.list();
		for(String file: aiFiles){
			if(!file.contains(".jar")){
				continue;
			}
			try{
				out.add(new AIJar(new File(file)));
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
		return out;
	}
	
	public java.util.List<Match> getMatches(ArrayList<bonzai.Jar> ais, String bfile){
		ArrayList<Match> matches = new ArrayList<Match>();
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(bfile));
			boolean hasNext = true;
			while(hasNext){
				String line = r.readLine();
				System.out.println(line);
				if(line.contains("Bracket")){
					continue;
				}else if(line.equals("Not Placed")){
					
				}else{
					String t1 = r.readLine();
					String t2 = r.readLine();
					System.out.println(t1 + "\n" + t2);
					bonzai.Jar[] teams = new bonzai.Jar[2];
					for(int i = 0; i < ais.size(); i++){
						if(ais.get(i).name().equals(t1)){
							teams[0] = ais.get(i);
						}else if(ais.get(i).name().equals(t2)){
							teams[1] = ais.get(i);
						}else{
							//not nessisary to tell the loop to continue
							//but i thought it looked more readable
							continue;
						}
					}
					matches.add(new Match(teams[0], teams[1]));
				}
				
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.matches = matches;//make sure that the rest of the program has
							   //access to the matches list
		return matches;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//matches.get(0).drawMatch(g, 0, 0, 100, 50);
	}

	
	//I want the UI to be interactive, so we need some sort of listener
	//these are auto-magically called by a signal when one of these events
	//occurs, all you as a user have to do is handle the signal
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
