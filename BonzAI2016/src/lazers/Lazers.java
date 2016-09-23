package lazers;

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

import bonzai.GameWrapper;
import bonzai.Team;
import bonzai.gui.AffineMouseAdapter;
import bonzai.gui.BonzAIFrame;
import lazers.api.Turn;
import lazers.Simulation;
import bonzai.Action;

@SuppressWarnings("serial")
public class Lazers extends JPanel implements GameWrapper, Runnable, KeyListener {
	private AffineMouseAdapter affine;

	private int gameOverFrame = 0;
	private int current = 0;
	//FPS removed.  Simulation is now controlled by the the speed of the tween.
	//private int fps = 160;
	private boolean play = false;

	private float tweenPercent = 0;
	private float tweenPercentChange = 0.1f;
	
	private Simulation simulation;
	
	/**
	 * 
	 */
	public Lazers() {
		affine = new AffineMouseAdapter();
		addMouseListener(affine);
		addMouseMotionListener(affine);
		addMouseWheelListener(affine);

		this.simulation = null;
		//this.renderer = null;
	}

	@Override
	public String version() {
		return "0.7.6.5";
	}

	@Override
	public int teams() {
		final int MAX_TEAMS = 6;
		return MAX_TEAMS;
	}

	@Override
	public Color color(int team) {
		final Color[] colors = { 
				new Color(217,  51,   21), // Red
				new Color(238, 218,  102), // Yellow
				new Color(68,   55,  142), // Blue
				new Color(0,   173,   59), // Green
				new Color(236, 135,    0), // Orange
				new Color(207,  71,  207), // Purple-pink
		};
		return colors[team];
	}

	@Override
	public Jar jar(File file) throws Exception {
		return new Jar(file);
	}

	@Override
	public LazersScenario scenario(File file) throws Exception {
		return new LazersScenario(file);
	}

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
	
	public String getTeamName(lazers.api.Color color) {
		return simulation.jar(color).name();
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
		return (int) (tweenPercentChange * 100);
	}

	@Override
	public void setFPS(int fps) {
		this.tweenPercentChange = fps / 100.0f;
	}

	@Override
	public void togglePlay() {
		play = !play;
	}

	@Override
	public JPanel view() {
		return this;
	}

	@Override
	public void resetView() {
		affine.reset();
	}

	@Override
	public void run() {
		while(true) {
			this.repaint();
			try { Thread.sleep(1000 / 60); } catch(InterruptedException e) { }
		}
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;

		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		if(simulation != null) {

			Turn turn = simulation.turn(current);

			Action action = current > 0 ? simulation.action(current) : null;

			//If this is the first turn, reset the scale
			if (current == 0) {
				LazersRenderer.setLastUL(new Point2D.Double());
				LazersRenderer.setLastDR(new Point2D.Double());
				gameOverFrame = 0;
				LazersRenderer.setPanel(this);
			}

			AffineTransform old = g2d.getTransform();
			g2d.translate(getWidth() / 2, getHeight() / 2);
			g2d.scale(getWidth() / 2, getWidth() / 2);
			affine.apply(g2d);

			LazersRenderer.render(g2d, turn, action, tweenPercent);

			g2d.setTransform(old);

			//Paint the score here
			renderScore(g2d, turn, getWidth(), getHeight());

			//Paint game over if it is
			if (turn == simulation.turn(LazersScenario.NUM_TURNS-1)) {
				//We're at the end of the game!
				renderGameOver(g2d,turn,getWidth(),getHeight(),gameOverFrame);
				gameOverFrame++;
			} else {
				gameOverFrame = 0;
			}

			//Update tween percent
			tweenPercent = (play) ? tweenPercent + tweenPercentChange : tweenPercent;

			//Update the current frame if we are done tweening
			if (tweenPercent >= 1.0f) {
				current = Math.min(current + (play ? 1 : 0), LazersScenario.NUM_TURNS-1);
				tweenPercent = 0;
			}



			try { Thread.sleep(1000 / 60); } catch(InterruptedException e) { }
		}
	}
	
	public Simulation simulation() {
		return simulation;
	}

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


			//YES!  I know this is bad.  We are bubble sorting teams
			//to find their rank.  And yes, we are indeed doing this EVERY FRAME!
			//However, this runs on my laptop, so it should run on everything.

			//Create a hash map that maps teams to their rank
			HashMap<Team, Integer> rank = new HashMap<Team,Integer>();
			Collection<Team> teams = turn.getTeams();
			//Find the largest, then add it
			int place = 1;
			while (!teams.isEmpty()){
				Team nextBest = teams.iterator().next();
				for (Team t : teams) {
					if (t.getScore() > nextBest.getScore()) { nextBest = t; }
				}
				rank.put(nextBest,place);
				place++;
				teams.remove(nextBest);
			}

			//Find the highest score, and remember this as a benchmark
			int highestScore = 0;
			
			ArrayList<Team> allTeams = new ArrayList<Team>();
			for (Team t : turn.getTeams()) {
				allTeams.add(t);
			}
			
			int winner = 0;
			for (int i = 0; i < allTeams.size(); i++) {
				if (allTeams.get(i).getScore() > highestScore) {
					highestScore = allTeams.get(i).getScore();
					winner = i;
				}
			}
			
			
			//Sorry guys, hard code time!
			int second = -1;
			for (int i = 0; i < allTeams.size(); i++) {
				if (i == winner) { continue; }
				if (second < 0 || allTeams.get(i).getScore() > allTeams.get(second).getScore()) {
					second = i;
				}
			}
			
			//Sorry guys, hard code time!
			int third = -1;
			for (int i = 0; i < allTeams.size(); i++) {
				if (i == winner || i == second) { continue; }
				if (third < 0 || allTeams.get(i).getScore() > allTeams.get(third).getScore()) {
					third = i;
				}
			}
			
			

			//All scores are given in percentages of the best team's score
			float percent = frame / 180.0f;
			float highestVisibleScore = (percent * highestScore);

			//Avoid a divide by 0 error
			if (highestVisibleScore == 0) { highestVisibleScore = 1; }

			//Draw all the scores
			int xOffset = 0;
			for (Team t : turn.getTeams()) {
				int score = Math.min((int)highestVisibleScore,t.getScore());
				int scoreHeight = (int) (height * (score / (float)highestScore));

				//Draw the bars!
				int barWidth = width/turn.getTeams().size();
				int yOffset = height - scoreHeight;

				g.setColor(LazersRenderer.getColors().get(t.getColor()));
				g.fillRect(startX + xOffset, startY+yOffset, barWidth, scoreHeight);
				String scoreString = (score == t.getScore()) ? getRank(rank.get(t)) + ": " + score : score + "";


				//Determine how big to draw text
				g.setFont(new Font("Arial",Font.PLAIN,30));
				int scoreWidth = g.getFontMetrics().stringWidth(scoreString);	//Used for centering text

				//Too big?
				if (scoreWidth > barWidth) {
					g.setFont(new Font("Arial",Font.PLAIN,18));
					scoreWidth = g.getFontMetrics().stringWidth(scoreString);
				}


				g.drawString(scoreString, startX+xOffset+((barWidth-scoreWidth)/2), startY+yOffset-5);

				//Draw team name
				String name = trim(getTeamName(t.getColor()), 20);
				g.setFont(new Font("arial",Font.PLAIN, 20));
				int nameWidth = g.getFontMetrics().stringWidth(name);
				g.drawString(name, startX+xOffset+((barWidth-nameWidth)/2), startY + height + 25);
				//Center the name
				xOffset += barWidth;
			}
			
			//Give em 2 seconds to let it sink in
			//TODO MITCH This needs to be dynamic!  Not 300!  Otherwise winners will be reported too early when the automator runs
			if (gameOverFrame == 300) {
				System.out.println("RESULT " + winner + " " + second + " " + third);
			}
		}

	}

	private static String getRank(int integer) {
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

	private void renderScore(Graphics2D g, Turn turn, int screenWidth, int screenHeight) {
		double barStart = 0;
		double barWidth = screenWidth;

		//Total Score
		float totalScore = 0;
		for (Team t : turn.getTeams()) {
			totalScore += t.getScore();
		}


		g.setStroke(new BasicStroke(2f));

		//Now draw each team's score
		double nameOffset = barStart;
		double offset = barStart;		//Where to begin drawing the score
		for (Team t : turn.getTeams()) {
			
			//Draw team names
			String teamName = getTeamName(t.getColor());
			double width = screenWidth / turn.getTeams().size();
			g.setColor(LazersRenderer.getColors().get(t.getColor()));
			g.setFont(new Font("arial",Font.PLAIN,16));
			g.fill(new Rectangle2D.Double(nameOffset, 0, width, 20));
			g.setColor(Color.WHITE);
			g.drawString(teamName, (int) (nameOffset + 10), 16);
			nameOffset += width;
			
			//Draw scores
			if (totalScore > 0) {
				width = (t.getScore() / totalScore) * barWidth;
				g.setColor(LazersRenderer.getColors().get(t.getColor()));
				//Draw rectangle
				g.fill(new Rectangle2D.Double(offset, 20, width, 10));
				g.setColor(Color.WHITE);
				g.draw(new Rectangle2D.Double(offset, 20, width, 10));
				offset+=width;
			}
		}
	}
	
	public void enableAutoStart() {
		play = true;
		this.tweenPercentChange = 1000;
		
		JFrame f;
		
		Container parent = this;
		while (true) {
			parent = parent.getParent();
			if (parent instanceof JFrame) {
				f = (JFrame)parent;
				break;
			}
		}
		
		//Full screen
		f.setExtendedState(f.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	}


	/**
	 *
	 **/
	public static void main(String[] args) throws Exception{
		Lazers snowbound = new Lazers();
		new Thread(snowbound).start();
		BonzAIFrame.create("Snowbound", snowbound);
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
