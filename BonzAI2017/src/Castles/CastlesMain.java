
package Castles;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Castles.api.*;
import Castles.util.AIJar;
import bonzai.GameWrapper;
import bonzai.*;
import bonzai.gui.AffineMouseAdapter;
import bonzai.gui.BonzAIFrame;
import bonzai.Action;

/**
 * This class holds the actual gameplay itself and will render scores etc... 
 */
@SuppressWarnings("serial")
public class CastlesMain extends JPanel implements GameWrapper, Runnable, KeyListener {
	private AffineMouseAdapter affine;

	private int gameOverFrame = 0;
	private int current = 0;
	//FPS removed.  Simulation is now controlled by the the speed of the tween.
	//private int fps = 160;
	private boolean play = false;
	
	//These are used to get fluid animations into the simulation.
	private float tweenPercent = 0;
	private float tweenPercentChange = 0.01f;

	private Simulation simulation;

	/**
	 * Constructor
	 */
	public CastlesMain() {
		affine = new AffineMouseAdapter();
		addMouseListener(affine);
		addMouseMotionListener(affine);
		addMouseWheelListener(affine);

		this.simulation = null;
		enableAutoStart();
		//this.renderer = null;
	}

	@Override
	// TODO 2018: Set the version number here!!!!
	public String version() {
		return "1.0";
	}

	/**
	 * Return the max number of teams.
	 */
	@Override
	public int teams() {
		final int MAX_TEAMS = 6;
		return MAX_TEAMS;
	}

	/**
	 * Define the actual color values for each team and return the list of them.
	 */
	@Override
	public Color color(int team) {
		final Color[] colors = { 
				new Color(217,  51,   21), // Red
				new Color(68,   55,  142), // Blue
				new Color(238, 218,  102), // Yellow
				new Color(0,   173,   59), // Green
				new Color(236, 135,    0), // Orange
				new Color(207,  71,  207), // Purple-pink
		};
		return colors[team];
	}

	/**
	 * Return a jar, given a .java file.
	 */
	@Override
	public AIJar jar(File file) throws Exception {
		return new AIJar(file);
	}
	
	/**
	 * Return a new scenario that is generated by this thread.
	 */
	@Override
	public CastlesScenario scenario(File file) throws Exception {
		return new CastlesScenario(file, -1);	//Team id is -1 for the server.  It will be a valid number for AIClients
	}
	
	/**
	 * Start the thread/simulation
	 */
	@Override
	public void run(bonzai.Scenario scenario, List<bonzai.Jar> jars) {
		try {
			simulation = new Simulation(scenario, jars);
			simulation.start();
			current = 0;
		}
		catch(Exception e) {
			System.err.println("Oops, something must have went wrong");
			System.err.println(e);
		}
	}
	
	@Override
	public void setCurrentFrame(int frame) {
		if(simulation == null) { current = 0; }
		else {
			current = frame < simulation.availableFrames() ? frame : simulation.availableFrames();
		}
	}
	
	/**
	 * Given a color, get the AI name that belongs to that color.
	 */
	public String getTeamName(Castles.api.TeamColor color) {
		try{
			String name = simulation.jar(color).name();
			return name;
		}
		catch(NullPointerException e){
			return null;
		}
	}

	@Override
	public int getCurrentFrame() {
		return current;
	}

	@Override
	public int availableFrames() {
		if(simulation == null) { return 0; }
		return simulation.availableFrames();
	}

	@Override
	public int totalFrames() {
		if(simulation == null) { return 0; }
		return simulation.totalFrames();
	}

	@Override
	public int getFPS() {
		//Should be between 1 and 100
		return (int) (tweenPercentChange * 3000);
	}

	@Override
	public void setFPS(int fps) {
		//fps will be between 1 and 100
		this.tweenPercentChange = fps / 3000.0f;
	}

	@Override
	public void togglePlay() {
		play = !play;
	}

	@Override
	public JPanel view() {
		return this;
	}

	/**
	 * Undoes any zoom/panning that occured on this frame.
	 */
	@Override
	public void resetView() {
		affine.reset();
	}

	/**
	 * Run the game simulation by calling repaint()
	 */
	@Override
	public void run() {
		while(true) {
			this.repaint();
			try { Thread.sleep(1000 / 60); } catch(InterruptedException e) { }
		}
	}

	/**
	 * Paint the current frame of the game.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if(simulation != null) {
			
			//We want to tween (animate) between these two turns.
			Turn turn = simulation.turn(current);
			Turn nextTurn = simulation.turn(current+1);	//Returns turn(current) if out of bounds
			
			Action action = current > 0 ? simulation.action(current) : null;

			//If this is the first turn, reset the scale
			if (current == 0) {
				CastlesRenderer.setLastUL(new Point2D.Double());
				CastlesRenderer.setLastDR(new Point2D.Double());
				gameOverFrame = 0;
				CastlesRenderer.setPanel(this);
			}
			
			//THIS STUFF NEEDS TO STAY IN HERE. The way the game scales from mouse zoom
			//and panning is due to this.
			AffineTransform old = g2d.getTransform();
			g2d.translate(getWidth() / 2, getHeight() / 2);
			g2d.scale(getWidth() / 2, getWidth() / 2);
			affine.apply(g2d);
			
			//Render the actual game objects onto the screen.
			CastlesRenderer.render(g2d, turn, nextTurn, action, tweenPercent);

			g2d.setTransform(old);

			//Paint the scores
			renderScore(g2d, turn, getWidth(), getHeight());

			//Paint game over screen
			if (turn.gameOver()) {
				//We're at the end of the game!
				renderGameOver(g2d, turn, getWidth(), getHeight(), gameOverFrame);
				gameOverFrame++;
			} else {
				gameOverFrame = 0;
			}

			//See if there's a change in points
			int totalPoints = 0;
			for (Team t : turn.getAllTeams()) {
				totalPoints+=t.getScore();
			}
			for (Team t : nextTurn.getAllTeams()) {
				totalPoints-=t.getScore();
			}
			
			//Decide how much to update tween percent
			float tweenUpdate = (totalPoints == 0) ? tweenPercentChange : tweenPercentChange / 2;
			
			//Update tween percent
			tweenPercent = (play) ? tweenPercent + tweenUpdate : tweenPercent;

			//Update the current frame if we are done tweening
			if (tweenPercent >= 1.0f) {
				current = Math.min(current + (play ? 1 : 0), CastlesScenario.NUM_TURNS-1);
				tweenPercent = 0;
			} else if (tweenPercent < 0) {
				current = Math.max(current - (play ? 1 : 0), 0);
				tweenPercent = 1;
			}

			try { Thread.sleep(1000 / 60); } catch(InterruptedException e) { }
		}
	}

	public Simulation simulation() {
		return simulation;
	}
	
	/**
	 * Render our game over screen.
	 */
	private void renderGameOver(Graphics2D g, Turn turn, int screenWidth, int screenHeight, int frame) {
		//Here is the order of what happens (Note: 60 frames / second)
		/*First 1/2 second, score box falls from top of screen
		 * For the next 3 seconds, raise the score bars*/


		int startX = (int) (screenWidth * 0.1);
		int startY = (int)(screenHeight * 0.1);
		int width =  (int) (screenWidth * 0.8);
		int height = (int)(screenHeight * 0.8);

		//For the first 30 frames, make the scoreboard start falling
		if (frame < 30) {
			startY -= (30-frame) * (screenHeight/30);
		}

		//Begin by making a box that covers 80% of the width and height
		g.setColor(Color.BLACK);
		g.fillRect(startX,startY,width,height);
		g.setColor(Color.WHITE);
		g.drawRect(startX, startY, width, height);

		//Adjust the view of where we will actually draw stuff
		startX += (int) (width * 0.1);
		startY += (int)(height * 0.1);
		width =  (int) (width * 0.8);
		height = (int)(height * 0.8);

		//Offset for first 30 frames
		if (frame >= 30) {
			frame -= 30;
			if (frame > 180) { frame = 180; }

			//Sort the teams in descending order
			List<Team> oriTeams = turn.getAllTeams();
			List<Team> teams = new ArrayList<>(oriTeams);
			Collections.sort(teams, new Comparator<Team>() {
				public int compare(Team arg0, Team arg1) {
					return arg1.getScore() - arg0.getScore();
				}
			});
			
			//This stuff is required by the existing framework
			//A bit "hard-codey", but it works for now 
			HashMap<Team, Integer> rank = new HashMap<>();
			float highestScore;
			
			rank.put(teams.get(0), 1);
			for (int i = 1, r = 1; i < teams.size(); i++) {
				if (teams.get(i - 1).getScore() != teams.get(i).getScore()) r++;
				rank.put(teams.get(i), r);
			}
			
			highestScore = teams.get(0).getScore();

			//All scores are given in percentages of the best team's score
			float percent = frame / 180.0f;
			float highestVisibleScore = (percent * highestScore);

			//Avoid a divide by 0 error
			if (highestVisibleScore == 0) { highestVisibleScore = 1; }

			//Draw all the scores
			int xOffset = 0;
			for (Team t : turn.getAllTeams()) {

				int score = Math.min((int)highestVisibleScore, t.getScore());
				int scoreHeight = (int) (height * (score / highestScore));

				//Draw the bars!
				int barWidth = width/turn.getAllTeams().size();
				int yOffset = height - scoreHeight;

				g.setColor((Color) CastlesRenderer.getColors().get(t.getColor()));
				g.fillRect(startX + xOffset, startY+yOffset, barWidth, scoreHeight);
				String scoreString = (score == t.getScore()) ? getRank(rank.get(t)) + ": " + score : score + "";


				//Determine how big to draw text
				g.setFont(new Font("Arial", Font.PLAIN, 30));
				int scoreWidth = g.getFontMetrics().stringWidth(scoreString);	//Used for centering text

				//Too big?
				if (scoreWidth > barWidth) {
					g.setFont(new Font("Arial", Font.PLAIN, 18));
					scoreWidth = g.getFontMetrics().stringWidth(scoreString);
				}


				g.drawString(scoreString, startX+xOffset+((barWidth-scoreWidth)/2), startY+yOffset-5);

				//Draw team name
				String name = trim(getTeamName(t.getColor()), 20);
				g.setFont(new Font("Arial", Font.PLAIN, 20));
				int nameWidth = g.getFontMetrics().stringWidth(name);
				g.drawString(name, startX+xOffset+((barWidth-nameWidth)/2), startY + height + 25);
				//Center the name
				xOffset += barWidth;
			}

			//Give em 2 seconds to let it sink in
			//TODO MITCH This needs to be dynamic!  Not 300!  Otherwise winners will be reported too early when the automator runs
			if (gameOverFrame == 300) {
				System.out.printf("SCORES %d(%d) : %d(%d)\n", oriTeams.get(0).getScore(), turn.getTeamLossCount(0),
															  oriTeams.get(1).getScore(), turn.getTeamLossCount(1));
				System.out.println("RESULT " + getTeamID(teams, 0) + " " + getTeamID(teams, 1) + " " + getTeamID(teams, 2));
			}
		}
	}
	
	private static int getTeamID(List<Team> teams, int id) {
		if (teams.size() <= id) return -1;
		return teams.get(id).getID();
	}

	private static String getRank(Integer integer) {
		switch(integer) {
		case 1:
			return "1st";
		case 2:
			return "2nd";
		case 3:
			return "3rd";
		default:
			return integer + "th";
		}
	}

	/**
	 * Render the scores at the top of the screen 
	 */
	private void renderScore(Graphics2D g, Turn turn, int screenWidth, int screenHeight) {
		double barStart = 0;
		double barWidth = screenWidth;

		//Total Score
		float totalScore = 0;
		for (Team t : turn.getAllTeams()) {
			totalScore += t.getScore();
		}


		g.setStroke(new BasicStroke(2f));

		//Now draw each team's score
		double nameOffset = barStart;
		double offset = barStart;		//Where to begin drawing the score
		for (Team t : turn.getAllTeams()) {
			//Draw team names
			//Possible to get errors when not all jars for the map are selected.
			//Fixing with a try catch block for now.
			try{
				
				String teamName = getTeamName(t.getColor());

				double width = screenWidth / turn.getAllTeams().size();
				g.setColor((Color) CastlesRenderer.getColors().get(t.getColor()));
				g.setFont(new Font("arial",Font.PLAIN,16));
				g.fill(new Rectangle2D.Double(nameOffset, 0, width, 20));
				g.setColor(Color.WHITE);
				g.drawString(teamName, (int) (nameOffset + 10), 16);
				nameOffset += width;

				//Draw scores
				if (totalScore > 0) {
					width = (t.getScore() / totalScore) * barWidth;
					g.setColor((Color) CastlesRenderer.getColors().get(t.getColor()));
					//Draw rectangle
					g.fill(new Rectangle2D.Double(offset, 20, width, 10));
					g.setColor(Color.WHITE);
					g.draw(new Rectangle2D.Double(offset, 20, width, 10));
					offset+=width;
				}
			}
			catch (NullPointerException e){

			}
		}
	}

	/**
	 * This method enables whether the simulation should automatically start
	 * running when it is launched.
	 */
	public void enableAutoStart() {
		play = true;
		//this.tweenPercentChange = 1000;

		JFrame f;

		Container parent = this;
		while (true) {
			parent = parent.getParent();
			if(parent==null){
				return;
			}
			if (parent instanceof JFrame) {
				f = (JFrame)parent;
				break;
			}
		}

		//Full screen
		f.setExtendedState(f.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Arrays.toString(args));
		CastlesMain castles = new CastlesMain();
		new Thread(castles).start();
		BonzAIFrame.create("Castles!!!!", castles);
		Thread.sleep(1000);
		if (args.length == 8 && args[0] == "-run") {
			System.out.println("Called");
			String scenario = args[1];
			ArrayList<bonzai.Jar> ais = new ArrayList<>();
			for (int i = 2; i < args.length; i++) {
				ais.add(new AIJar(new File(args[i])));
			}
			castles.run(castles.scenario(new File(scenario)), ais);
			System.exit(0);
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}

	public String trim(String s, int length) {
		if (s.length() <= length) { return s;}
		return s.substring(0, length-3) + "...";
	}
}
