package DaneJensenBrackets;

import java.awt.*;

public class Match extends Component{
	private bonzai.Jar[] teams = new bonzai.Jar[2];
	private int winningTeam = -1;
	
	public Match(bonzai.Jar team1, bonzai.Jar team2){
		teams[0] = team1;
		teams[1] = team2;
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
		/*       =======================
		 *       |Start Match          |
		 *    _  =======================
		 *   |X| ManticoreAI  (score)   
		 *    =  ======================= 
		 *   |X| TurtleAI     (score)   
		 *    =  ======================= 
		 * 
		 * Rough drawing of what it will look like when all is done,
		 * the start match is not to scale with the ai names
		 * 
		 */
		g.setColor(Color.WHITE);
		//draw the start match button
		int bY = (ysize * 1) / 5;
		g.drawRect(xsize/10 + x, y + y, xsize * 9 / 10, bY);//makes a box at the top of the match
		//auto continue buttons
		g.drawRect(x, y + bY, xsize/10, bY*2);
		g.drawRect(x, y + bY*3, xsize/10, bY*2);
		//lines to go under team names
		g.setColor(Color.BLACK);
		g.drawLine(x, y + bY, x + xsize, y + bY);
		g.drawLine(x, y + 3 * bY, x + xsize, y + 3*bY);
		
	}
	
	/**
	 * runs the match 
	 * 
	 * @return the team that won or -1 if their was an error
	 */
	public int runMatch(){
		
		try{
			//java -jar castles.jar -run [scenarios] <ai/null> <ai/null> <ai/null> <ai/null> <ai/null> <ai/null>
			String[] args = new String[11];
			
			
		}catch(Exception e){
			//print message to stderr
			//doesn't crash this thread
			e.printStackTrace(System.err);
		}
		
		return winningTeam;
	}

}
