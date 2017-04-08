package DaneJensenBrackets;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import bonzai.*;
import Castles.*;
import Castles.util.*;
import java.io.*;
//i didn't want warnings for importing things that i was never using
@SuppressWarnings("unused")
public class BracketDisplay extends Component implements MouseListener,Runnable{
	/*
	 * TODO
	 * 1. print pre-matches (or empty matches because I like consistency)
	 * 2. fill in the rest of the brackets with match dependencies
	 * 3. test test test test test test test test test test test
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Match> matches = new ArrayList<>();
	private ArrayList<Match> preMatches = new ArrayList<>();
	private int n = 0; //the number of matches
	private int x=0,y=0;
	public boolean end = false;
	private int frame = 0;
	private int logn = 0;
	private int matchN = 0;
	private Font highlightF = new Font("Times New Roman", Font.BOLD, 40);

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
		f.setSize(1400, 900);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		p.add(b, BorderLayout.CENTER);//makes my bracket display take up the entire screen
		p.addMouseListener(b);
		f.add(p);
		//should be the LAST line of any UI code
		f.setVisible(true);
		b.run();
	}
	
	public BracketDisplay(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.x = (int)screenSize.getWidth();
		this.y = (int)screenSize.getHeight();
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
			int qual = 0;
			n = 0;//the number of matches
			while(hasNext){
				String line = r.readLine();
				//System.out.println(line);
				if(line.contains("Bracket")){
					continue;
				}else if(line.equals("Not Placed")){
					//System.out.println("Making " + qual + " Extra Match(es)");
					//add extra matches (should add the rest of the ais)
					for(int i = 0; i < qual; i++){
						String t1 = r.readLine();
						String t2 = r.readLine();
						//System.out.println(t1 + "\n" + t2);
						bonzai.Jar[] teams = new bonzai.Jar[2];
						for(int j = 0; j < ais.size(); j++){
							if(ais.get(j).name().equals(t1)){
								teams[0] = ais.get(j);
							}else if(ais.get(j).name().equals(t2)){
								teams[1] = ais.get(j);
							}else{
								//not nessisary to tell the loop to continue
								//but i thought it looked more readable
								continue;
							}
						}
						preMatches.add(new Match(teams[0], teams[1]));
					}
					break;
				}else{
					n++;
					String t1 = r.readLine();
					String t2 = r.readLine();
					boolean sb = false;
					if(t1.contains("L")){
						try{
							String[] parts = t1.split("L");
							if(parts[0].length() == 0){//string starts with L
								//surrounded by try/catch so that if someone just
								//named their ai L* it wouldn't cause the
								//program to crash
								int temp = Integer.parseInt(parts[1]);
								sb = true;
								String tempS = t1;
								t1 = t2;
								t2 = tempS;
								qual++;
							}
						}catch(Exception e){
							
						}
					}
					
					if(!sb && t2.contains("L")){
						try{
							String[] parts = t2.split("L");
							if(parts[0].length() == 0){//string starts with L
								//surrounded by try/catch so that if someone just
								//named their ai L* it wouldn't cause the
								//program to crash
								int temp = Integer.parseInt(parts[1]);
								sb = true;
								qual++;
							}
						}catch(Exception e){
							
						}
					}
					
					//System.out.println(t1 + "\n" + t2);
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
					if(sb){
						matches.add(new Match(teams[0]));
					}else{
						matches.add(new Match(teams[0], teams[1]));
					}
					
				}
				
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.matches = matches;//make sure that the rest of the program has
							   //access to the matches list
		for(int i =0,j=0; i < n; i++){
			if(!matches.get(i).hasTwoTeams()){
				matches.get(i).setMatchDependancy(preMatches.get(j++));
			}
		}
		logn = 0;
		while(Math.pow(2, logn) < n){
			logn++;
		}
		if(n < ais.size()){
			logn++;
		}
		
		//fill da brackets
		int index = 0;
		for(int i = logn-1; i > 0; i--){
			int num = (int)Math.pow(2, i);
			for(int j = 0; j < num; j+=2){
				matches.add(new Match(matches.get(j+index),matches.get(j+1+index)));
			}
			index += num;
		}
		
		return matches;
	}
	
	@Override
	public void paint(Graphics g){
		//make animator to scroll brackets
		super.paint(g);
		
		int xoffset = 30;
		int xpadding = 50;
		int ypadding = 10;
		int matchxsize = 120;
		int matchysize = 40;
		int yOffFrame = -1;
		
		if((frame*yOffFrame+y)<0){
			frame = 0;
		}
		if(n * (matchysize + ypadding) >= y){
			y = n * (matchysize + ypadding);
		}else{
			yOffFrame = 0;
		}
		
		if(matchxsize * logn >= Toolkit.getDefaultToolkit().getScreenSize().width - 500){
			x = matchxsize * logn + 500;
		}else{
			x = Toolkit.getDefaultToolkit().getScreenSize().width;
		}
		
		//print brackets
		int m = 1;
		for(int j = 0; j <logn; j++){
			int i = (int)Math.pow(2,j);
			int numB = i*2;
			int xs = x-(xpadding*(j))-xoffset-matchxsize*(j+1);
			for(; i > 0; i--){
				Match[] dep = matches.get(matches.size()-m).getMatchDependencies();
				if(dep == null) break;
				int[] deps = new int[2];
				if(dep[0]!=null)deps[0]++;
				if(dep[1]!=null)deps[1]++;
				int ys = (y/numB) * (2*i-1) + yOffFrame*frame + 5;
				int inter = (y/numB)/2;
				if(deps[0] == 1 || deps[1] == 1){
					g.drawLine(xs, ys, xs-xpadding/2, ys);
				}
				if(deps[1] == 1){
					g.drawLine(xs-xpadding/2, ys, xs-xpadding/2, ys + inter);
					g.drawLine(xs-xpadding/2, ys + inter, xs-xpadding, ys + inter);
				}
				if(deps[0] == 1){
					g.drawLine(xs-xpadding/2, ys, xs-xpadding/2, ys - inter);
					g.drawLine(xs-xpadding/2, ys - inter, xs-xpadding, ys - inter);
				}
				m++;
			}
		}
		int index = matches.size();
		//yay matches
		for(int j = 0; j <= logn; j++){
			int i = (int)Math.pow(2,j);
			int numB = i*2;
			int xs = x - (xpadding*(j)) - xoffset - matchxsize*(j+1);
			if(i == n){
				for(;i>0;i--){
					int ys = (y/numB) * (2*i-1) + (-matchysize/2 + yOffFrame * frame);
					matches.get(i-1).drawMatch(g,xs, ys, matchxsize, matchysize);
					Match[] temp = matches.get(i-1).getMatchDependencies();
					if(temp[0] != null){
						temp[0].drawMatch(g, xs-xpadding-matchxsize , ys - y/(numB*2), matchxsize, matchysize);
					}
						
					if(temp[1] != null){
						temp[1].drawMatch(g, xs-xpadding-matchxsize , ys + y/(numB*2), matchxsize, matchysize);
					}
					
				}
				break;
			}else{
				for(;i>0;i--){
					int ys = (y/numB) * (2*i-1) + (-matchysize/2 + yOffFrame * frame);
					matches.get(index - i).drawMatch(g,xs, ys, matchxsize, matchysize);
				}
				index -= i;
			}
		}
		
		//creates a separate scope with no logic attached
		{
			//print current match
			int xs = x - (xpadding*(logn+1)) - xoffset - matchxsize*(logn+2);
			//print next match
			//g.drawLine(xs, 0, xs, y);
			xs = 0 + xoffset;
			int xsize = 400;
			int ysize = 160;
			int height = Toolkit.getDefaultToolkit().getScreenSize().height;
			int padding = height/8;
			//draws the current match
			if(matchN < preMatches.size()){
				preMatches.get(matchN).drawMatch(g, xs, padding, xsize, ysize, highlightF, "Current Match");
			}else{
				if(!(matchN-preMatches.size() >= matches.size())){
					matches.get(matchN-preMatches.size()).drawMatch(g, xs, padding, xsize, ysize, highlightF, "Current Match");
				}
			}
			padding += padding;
			padding += ysize;
			//draws the next match
			if((matchN+1) < preMatches.size()){
				preMatches.get(matchN).drawMatch(g, xs, padding, xsize, ysize, highlightF, "Next Match");
			}else{
				if(!(matchN+1-preMatches.size() >= matches.size())){
					matches.get((matchN+1)-preMatches.size()).drawMatch(g, xs, padding, xsize, ysize, highlightF, "Next Match");
				}
			}
			
		}
	}

	
	//I want the UI to be interactive, so we need some sort of listener
	//these are auto-magically called by a signal when one of these events
	//occurs, all you as a user have to do is handle the signal
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		//System.out.println("Click: "+x+","+y);
		int minx = 0 + 30;//minx
		int miny = Toolkit.getDefaultToolkit().getScreenSize().height/8 + 40;//miny
		int maxx = minx + 160/3;
		int maxy = miny + 320/3;
		//the only 2 elements that are clickable are the current and next match
		//so those are the only 2 that I am going to check
		
		//check if mouse was pressed in any of the boxes that can be clicked
		//in the right x region
		if(minx < x && maxx > x){
			//System.out.println("In the correct X region");
			//check for the y region
			if(miny < y && maxy > y){
				//current match
				System.out.println("Clicked in the current match area");
				int interval = maxy-miny;
				System.out.println("" + interval + "   " + y);
				if(y <= miny + interval/2){
					if(matchN < preMatches.size()){
						preMatches.get(matchN).setWinningTeam(0);
					}else{
						matches.get(matchN-preMatches.size()).setWinningTeam(0);
					}
				}else{
					if(matchN < preMatches.size()){
						preMatches.get(matchN).setWinningTeam(1);
					}else{
						matches.get(matchN-preMatches.size()).setWinningTeam(1);
					}
				}
				matchN++;
			}
			miny += 160;
			miny += Toolkit.getDefaultToolkit().getScreenSize().height/8;
			maxy = miny + 320/3;
			if(miny < y && maxy > y){
				//next match
				//System.out.println("YOU CLICKED THE WRONG BUTTON!!!");
			}
		}else{
			//System.out.println("Not in Range "+minx+" to "+maxx);
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void run() {
		//repaints the brackets every 20 milliseconds
		//or roughly 50 fps
		while(!end){
			this.repaint();
			frame++;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
