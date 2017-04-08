package DaneJensenBrackets;

import java.awt.*;

public class Match{
	protected bonzai.Jar[] teams = new bonzai.Jar[2];
	private int winningTeam = -1;
	//font style, font type and font size
	public static final Font matchFont = new Font("Times New Roman", Font.BOLD, 14);
	private Match[] depends = new Match[2];
	
	
	public Match(bonzai.Jar team1, bonzai.Jar team2){
		teams[0] = team1;
		teams[1] = team2;
		if(teams[0] == null || teams[1] == null){
			throw new IllegalArgumentException("A team is null");
		}
	}
	
	public Match(bonzai.Jar team1){
		teams[0] = team1;
	}
	
	public Match(Match m1, Match m2){
		depends[0] = m1;
		depends[1] = m2;
	}
	
	public Match(Match m1, bonzai.Jar team1){
		this.teams[0] = team1;
		this.depends[1] = m1;
	}
	
	public void updateTeams(){
		if(depends[0] != null && teams[0] == null){
			if(depends[0].getWinningTeam() != -1){
				teams[0] = depends[0].teams[depends[0].getWinningTeam()];
			}
		}
		if(depends[1] != null && teams[1] == null){
			if(depends[1].getWinningTeam() != -1){
				teams[1] = depends[1].teams[depends[1].getWinningTeam()];
			}
		}
	}
	
	/**
	 * Draws the match onto the bracket, all it needs to do is draw the start match
	 * button the 2 buttons to auto move a team on, the teams names and the scores of
	 * said teams, or 0 for each if the teams haven't played yet.
	 * 
	 * The inputed x,y coordinates are expected to be the upper right hand 
	 * coordinates of the match,
	 * 
	 * 
	 * @param g			The graphics Object for the panel that will be drawing the Match
	 * @param x			The x value added to the initial position of the Object
	 * @param x			the y value added to the initial position of the Object
	 * @param xsize		How big it can be in the x direction
	 * @param ysize		How big it can be in the y direction		
	 */
	public void drawMatch(Graphics g, int x, int y, int xsize, int ysize){
		/*
		 *        =======================
		 *       |Start Match          |
		 *    _  =======================
		 *   |X| ManticoreAI  (score)   
		 *    =  ======================= 
		 *   |X| TurtleAI     (score)   
		 *    =  ======================= 
		 * 
		 * Rough drawing of what it will look like when all is done,
		 * the start match is not to scale with the ai names
		 */
		updateTeams();
		if(teams[0] == null && teams[1] == null){
			drawEmptyMatch(g,x,y,xsize,ysize);
			return;
		}
		g.drawRect(x, y, xsize, ysize);
		g.setColor(Color.WHITE);
		//draw the start match button
		int bY = (ysize * 1) / 2;
		//g.fillRect(xsize/10 + x, y, xsize * 9 / 10, bY);//makes a box at the top of the match
		//auto continue buttons
		//g.fillRect(x, y + bY, xsize/10, bY);
		//g.fillRect(x, y + bY*2, xsize/10, bY);
		//lines to go under team names
		g.setColor(Color.BLACK);
		g.drawLine(x, y + 2 * bY, x + xsize, y + 2 * bY);
		g.drawLine(x, y + 1 * bY, x + xsize, y + 1*bY);
		//the input for strings is strange because it takes the bottom left corner
		//or where the string is supposed to be
		g.setFont(matchFont);
		if(teams[0] != null){
			if(winningTeam == 0){
				g.setColor(Color.GREEN);
				g.fillRect(x, y, xsize, bY);
				g.setColor(Color.black);
			}
			g.drawString(teams[0].name(),x + 5, bY*1 + y - 2);
		}
		if(teams[1] != null){
			if(winningTeam == 1){
				g.setColor(Color.GREEN);
				g.fillRect(x, y + bY, xsize, bY);
				g.setColor(Color.black);
			}
			g.drawString(teams[1].name(),x + 5, bY*2 + y - 2);
		}
		//g.drawString("Start Match", x + xsize * 3 /10, y+bY-2);
		
	}
	
	/**
	 * Draws the match onto the bracket, all it needs to do is draw the start match
	 * button the 2 buttons to auto move a team on, the teams names and the scores of
	 * said teams, or 0 for each if the teams haven't played yet.
	 * 
	 * The inputed x,y coordinates are expected to be the upper right hand 
	 * coordinates of the match,
	 * 
	 * 
	 * @param g			The graphics Object for the panel that will be drawing the Match
	 * @param x			The x value added to the initial position of the Object
	 * @param x			the y value added to the initial position of the Object
	 * @param xsize		How big it can be in the x direction
	 * @param ysize		How big it can be in the y direction		
	 * @param matchFont The font that will be displayed in the match object
	 */
	public void drawMatch(Graphics g, int x, int y, int xsize, int ysize, Font matchFont, String top){
		/*
		 *        =======================
		 *       |Start Match          |
		 *    _  =======================
		 *   |X| ManticoreAI  (score)   
		 *    =  ======================= 
		 *   |X| TurtleAI     (score)   
		 *    =  ======================= 
		 * 
		 * Rough drawing of what it will look like when all is done,
		 * the start match is not to scale with the ai names
		 */
		updateTeams();
		if(teams[0] == null && teams[1] == null){
			drawEmptyMatch(g,x,y,xsize,ysize);
			return;
		}
		g.drawRect(x, y, xsize, ysize);
		g.setColor(Color.WHITE);
		//draw the start match button
		int bY = (ysize * 1) / 3;
		g.fillRect(xsize/10 + x, y, xsize * 9 / 10, bY);//makes a box at the top of the match
		//auto continue buttons
		g.fillRect(x, y + bY, xsize/10, bY);
		g.fillRect(x, y + bY*2, xsize/10, bY);
		//lines to go under team names
		g.setColor(Color.BLACK);
		g.drawLine(x, y + 3 * bY, x + xsize, y + 3 * bY);
		g.drawLine(x, y + 2 * bY, x + xsize, y + 2*bY);
		//the input for strings is strange because it takes the bottom left corner
		//or where the string is supposed to be
		g.setFont(matchFont);
		if(teams[0] != null){
			if(winningTeam == 0){
				g.setColor(Color.GREEN);
				g.fillRect(xsize/10 + x, y+bY, xsize*9/10, bY);
				g.setColor(Color.black);
			}
			g.drawString(teams[0].name(),x + xsize/10 , bY*2 + y - 2);
		}
		if(teams[1] != null){
			if(winningTeam == 1){
				g.setColor(Color.GREEN);
				g.fillRect(xsize/10 + x, y+bY*2, xsize*9/10, bY);
				g.setColor(Color.black);
			}
			g.drawString(teams[1].name(),x + xsize/10 , bY*3 + y - 2);
		}
		g.drawString(top, x + xsize * 3 /10, y+bY-2);
		
	}
	
	public int getWinningTeam(){
		return winningTeam;
	}
	
	public Match[] getMatchDependencies(){
		return depends;
	}
	
	public void setMatchDependancy(Match m1){
		depends[1] = m1;
	}
	
	public void setWinningTeam(int team){
		winningTeam = team;
	}
	
	public boolean hasTwoTeams(){
		int i =0;
		if(teams[0] != null)i+=1;
		if(teams[1] != null)i+=1;
		return i == 2;
	}
	
	public static void drawEmptyMatch(Graphics g, int x, int y, int xsize, int ysize){
		g.drawRect(x, y, xsize, ysize);
		g.setColor(Color.WHITE);
		//draw the start match button
		int bY = (ysize * 1) / 2;
		//auto continue buttons
		g.fillRect(x, y, xsize/10, bY);
		g.fillRect(x, y + bY, xsize/10, bY);
		//lines to go under team names
		g.setColor(Color.BLACK);
		g.drawLine(x, y + 2 * bY, x + xsize, y + 2 * bY);
		g.drawLine(x, y + 1 * bY, x + xsize, y + 1*bY);
		//the input for strings is strange because it takes the bottom left corner
		//or where the string is supposed to be
		g.setFont(matchFont);
		//g.drawString("Start Match", x + xsize * 3 /10, y + bY-2);
	}

}
