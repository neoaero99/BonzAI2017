package Castles;

import java.awt.*;
import java.util.List;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.imageio.ImageIO;

import Castles.Game;
import Castles.Objects.*;
import Castles.api.ShoutAction;
import Castles.api.TeamColor;
//import Castles.api.Color;
import Castles.api.Turn;
import Castles.util.graph.SegEdge;
import DavidMohrhardt.animator.Animator;
import bonzai.Action;
import bonzai.Position;
import bonzai.Renderer;
import bonzai.Team;

/**
 * Handle rendering the actual game objects on the screen. 
 */
@SuppressWarnings("unused")
public class CastlesRenderer extends Renderer {
	private static int soldierUpdateCount;
	// The scale value for rendering position images
	static final float posImgSF = 0.015f;
	static HashMap<String,BufferedImage> backgroundImages = new HashMap<String,BufferedImage>();
	static BufferedImage backgroundImage;	//The current background image

	//TODO 2017: Set up your art assets by setting them up here
	//These data structures hold the art assets for each game object.
//**************************************************************************************************


	static File[] selectorFiles = {
				new File("art/turn/turn_red.png"),
				new File("art/turn/turn_blue.png"),
				new File("art/turn/turn_yellow.png"),
				new File("art/turn/turn_green.png"),
				new File("art/turn/turn_orange.png"),
				new File("art/turn/turn_purple.png")
			};
	
	static File[] rallyPointFiles = new File[] {
				new File("art/sprites/gray_node.png"),
				new File("art/sprites/red_node.png"),
				new File("art/sprites/blue_node.png")
			};
	
	static File villageFile = new File("art/sprites/village.png");
	static File castleFile = new File("art/sprites/castle.png");

//	
//***************************************************************************************************

	//These data structures hold the actual images that get pulled from the above files
	private static final Map<TeamColor, Color> colors = new HashMap<>();
	private static Map<TeamColor, BufferedImage> selectorImages = new HashMap<>();
//	private static Map<Castles.api.Color, BufferedImage> castleImages = new HashMap<>();
//	private static BufferedImage[] targetImages = new BufferedImage[targetFiles.length];
	private static BufferedImage[] rallyPointImages;
	private static BufferedImage rallyPointImage,villageImage,castleImage;
	private static boolean imagesLoaded = false;

	public CastlesRenderer() {
		soldierUpdateCount = 0;
		loadImages();
	}
	
	public static void loadImages(){
		if(!imagesLoaded){
			imagesLoaded = true;
			colors.put(TeamColor.RED,    new Color(217,  51,   21)); // Red
			colors.put(TeamColor.YELLOW, new Color(238, 218,  102)); // Yellow
			colors.put(TeamColor.BLUE,   new Color(68,   55,  142)); // Blue
			colors.put(TeamColor.GREEN,  new Color(0,   173,   59)); // Green
			colors.put(TeamColor.ORANGE, new Color(236, 135,    0)); // Orange
			colors.put(TeamColor.PURPLE, new Color(207,  71,  207)); // Purple

			getBackgroundImages();
			try { 
				//Read the images into the data structures, given the file names defined above.
				//TODO load our sprites here
//				//load our png's
				selectorImages = loadIntoMap(selectorFiles);
				castleImage = ImageIO.read(castleFile);
				villageImage = ImageIO.read(villageFile);
				rallyPointImages = new BufferedImage[rallyPointFiles.length];
				
				for (int idx = 0; idx < rallyPointFiles.length; ++idx) {
					rallyPointImages[idx] = ImageIO.read(rallyPointFiles[idx]);
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}else{
			return;
		}
	}

	/**
	 * Render a preview of the map, for displaying inside of the launcher.
	 **/
	public static void render(Graphics2D g, CastlesMap map) {
		List<Castles.api.TeamColor> colors = new LinkedList<Castles.api.TeamColor>(getColors().keySet());
		Game game = new Game(0, 2, map, colors);
		render(g, game.turn(0), game.turn(0), null, 1);

	}

	private static void getBackgroundImages() {
		String path = "art/maps/";
		File temp = new File(path);//opens the map directory
		String[] files = temp.list(); //gets all files in the directory
		for(int i = 0; i < files.length; i++){
				
				int dotIdx = files[i].indexOf('.');
				String name = files[i].substring(0, dotIdx);
				
				try {
					backgroundImages.put(name, ImageIO.read(new File(path+files[i])));
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		}
		
	}

	/**
	 * sets the backGround image to the image connected to the supplied
	 * theme
	 * 
	 * @param theme the map name being set
	 */
	public static void setBackground(String theme) {
		//Set the background image
		backgroundImage = backgroundImages.get(theme);
		if (backgroundImage == null) {backgroundImage = backgroundImages.get("desert");}
	}


	/**
	 * Render a turn within the game window.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param turn - the turn to render
	 * @param nextTurn - Used for tweening
	 * @param action
	 * @param tweenPercent
	 */
	
	//TODO 2017: Change this code to render your game objects. The tweening stuff 
	public static void render(Graphics2D g, Turn turn, Turn nextTurn, Action action, float tweenPercent) {
		//Before we do anything, move everything so it is centered
		AffineTransform oldTransform = g.getTransform();

		float halfWidth = (turn.getMapWidth()+2) / 2f;
		float height = (turn.getMapHeight()+2);
		float halfHeight = height / 2f;
		g.scale(1/height, 1/height);

		//Add 1 because we actually go from size -1 to width + 1
		g.translate(-halfWidth + (1f), halfHeight-(1f));

		/* We can split each turn rendering into 2 parts:
		 * Part 1: Moving (Eg rotaters rotate)
		 * Part 2: Reaction to moving (eg lazers grow)
		 * Part 3: Discovery turn (reacts to lazers moving)
		 * 		   Note: Part 3 ONLY occurs when there is a change of points!
		 * Part 1 tween goes from 0 to 100%, then part 2 tween goes from 0 to 100% after
		 */

		//If there is a change in points, there are 3 parts (points can only go up!)
		int totalPoints = 0;
		for (Team t : turn.getAllTeams()) {
			totalPoints+=t.getScore();
		}
		for (Team t : nextTurn.getAllTeams()) {
			totalPoints-=t.getScore();
		}

		int numberOfParts = (totalPoints == 0) ? 2 : 4;

		float part1Tween = tweenPercent * numberOfParts;
		part1Tween = (part1Tween > 1) ? 1 : part1Tween;

		float part2Tween = (tweenPercent * numberOfParts) - 1;
		part2Tween = (part2Tween < 0) ? 0 : part2Tween;
		part2Tween = (part2Tween > 1) ? 1 : part2Tween;

		//Part 3 tween last twice as long as the first 2
		float part3Tween = (tweenPercent * numberOfParts) - 2;
		part3Tween /= 2;
		part3Tween = (part3Tween < 0) ? 0 : part3Tween;

		//Calculate a tween that follows a bell curve (0% -> 100% -> 0%)
		float bellTween = getBellCurve(part1Tween);

		//Calculate a smooth tween (0%-100%)
		float smoothTween = getSmoothCurve(part1Tween);

		//TODO 2017: This is where you call your custom render methods. Our versions
		//of these methods have been left for you at the bottom of this file.
		turn.renderMap(g);
		renderShoutActions(g, turn, nextTurn, smoothTween);
		g.setTransform(oldTransform);
	}

	public static void renderPaths(Graphics2D g, CastlesMap map) {
		Stroke origin = g.getStroke();
		ArrayList<SegEdge> paths = map.edgeList();
		
		g.setStroke(new BasicStroke(0.05f));
		
		//iterate over all the paths
		for(SegEdge p: paths){
			//get the rally points on the edge
			RallyPoint r1 = map.getPosition(p.first.ID);
			RallyPoint r2 = map.getPosition(p.second.ID);
			
			//get the x,y coords of the two rally points
			int x1 = r1.getPosition().getX();
			int y1 = r1.getPosition().getY();
			int x2 = r2.getPosition().getX();
			int y2 = r2.getPosition().getY();
			
			g.setColor(Color.PINK);
			g.drawLine(x1, y1 - map.getHeight(), x2, y2 - map.getHeight());
		}
		
		g.setStroke(origin);
	}
	
	
	public static void renderSoldiers(Graphics2D g, CastlesMap map) {
		++soldierUpdateCount;
		ArrayList<Soldier>[] soldierList;
		soldierList = map.getSoldiers();
		
		for (ArrayList<Soldier> soldier: soldierList){
			for(Soldier newSoldier: soldier){
				Animator anim = newSoldier.getAnimator();
				BufferedImage image;
				
				if (soldierUpdateCount > 100) {
					soldierUpdateCount = 0;
					image = anim.getFrameAtIndex("March", anim.getCurrentFrameIndex());
					
				} else {
					image = anim.getFrameAtIndex("March", anim.getCurrentFrameIndex());
				}
				
				RallyPoint r = map.getPosition(newSoldier.getPositionID());
				int sIdx = r.onPoint.indexOf(newSoldier);
				
				if (sIdx >= 0 && sIdx < 4) { // Only render the first four soldiers on a point
					Position rp = r.getPosition();
					int halfPIW = (int)(posImgSF * castleImage.getWidth() / 2.0),
						halfPIH = (int)(posImgSF * castleImage.getHeight() / 2.0),
						px, py;
					
					if (sIdx == 0) {
						// Render at the top-left corner of the position
						px = rp.getX() - halfPIW;
						py = rp.getY() - halfPIH;
						
					} else if (sIdx == 1) {
						// Render at the top-right corner of the position
						px = rp.getX() + halfPIW;
						py = rp.getY() - halfPIH;
						
					} else if (sIdx == 2) {
						// Render at the bottom-left corner of the position
						px = rp.getX() - halfPIW;
						py = rp.getY() + halfPIH;
						
					} else {
						// Render at the bottom-right corner of the position
						px = rp.getX() + halfPIW;
						py = rp.getY() + halfPIH;
					}
					
					float soldierImgSF = 0.015f;
					double halfImgWidth = soldierImgSF * image.getWidth() / 2.0,
							halfImgHeight = soldierImgSF * image.getHeight() / 2.0;
					
					//g.translate(-halfImgWidth, -halfImgHeight);
					drawToScale(g, map, image, px, py, 0, soldierImgSF, 0);
					//g.translate(halfImgWidth, halfImgHeight);
					
					drawText(g, Integer.toString(newSoldier.getValue()), px + halfImgWidth / 2f, py + halfImgHeight / 2f - map.getHeight(), Color.BLACK, Color.WHITE, 0.5f);
				}
			}
		}
	}
	
	public static void renderBuildings(Graphics2D g, CastlesMap map) {
		ArrayList<RallyPoint> nodes = map.getAllPositions();
		//Animator animate = new Animator("art/sprite_sheets/nodes.png", "art/sprite_sheets/nodes.ssc");
		
		for(RallyPoint r : nodes) {
			Position p = r.getPosition();
			/*
			String ownerTeam = "Neutral";
			
			if (r instanceof Building) {
				Building b = (Building)r;
				
				if (b.getTeamColor() == Castles.api.Color.RED) {
					ownerTeam = "Red";
					
				} else if (b.getTeamColor() == Castles.api.Color.YELLOW) {
					ownerTeam = "Blue";
				}
			}
			
			drawToScale(g, animate.getFrameAtIndex(ownerTeam, 0), r.getPosition().getX(), r.getPosition().getY(), 0, 1.5f*posImgSF, 0);
			*/
			
			if (r instanceof Building) {
				Building b = (Building)r;
				
				// Draw team color under buildings controlled by an AI
				if (b.getTeamColor() != null) {
					int teamIdx = b.getTeamColor().ordinal() + 1;
					
					g.translate(0.2, 0.75);
					drawToScale(g, map, rallyPointImages[teamIdx],p.getX(),p.getY(),0,2f*posImgSF/3f,0);
					g.translate(-0.2, -0.75);
				}
				
				// Draw the image for the position based on its building type
				if (b.type == PType.BASE || b.type == PType.CASTLE) {
					drawToScale(g, map, castleImage,p.getX(),p.getY(),0,posImgSF,0);
					
				} else {
					drawToScale(g, map, villageImage,p.getX(),p.getY(),0,posImgSF,0);
				}
				
			} else {
				// Position is a rally point
				drawToScale(g, map, rallyPointImages[0],p.getX(),p.getY(),0,2f*posImgSF/3f,0);
			}	
		}
	}

	private static void renderShoutActions(Graphics2D g, Turn turn, Turn nextTurn, float smoothTween) {
		int fontSize = 20;
		float fontScale = fontSize / 2.f;
		
		for (Team t : turn.getAllTeams()) {
			Action a = turn.getActionFor(t.getColor());
			
			if (a instanceof ShoutAction) {
				Position pos = turn.getRanOccupiedPos( t.getColor() );
				
				if (pos == null) {
					pos = new Position(turn.getMapWidth() / 2, turn.getMapHeight() / 2);
				}
				
				String message = ((ShoutAction)a).getMessage();
	
				g.setFont(new Font("Arial", Font.PLAIN, fontSize));
	
				RoundRectangle2D.Float bubble = new RoundRectangle2D.Float();
	
				int length = message.length();
				length += (length < 9 ? 9 - length : 0);
	
				bubble.width = (g.getFontMetrics().stringWidth(message) + length) / 20.f;
				float offsetX = bubble.width / 2 / fontScale;
	
				bubble.height = g.getFontMetrics().getHeight() * 3 / 40.f;
				bubble.archeight = .4f;
				bubble.arcwidth = .4f;
	
				bubble.x = pos.getX() - offsetX;
				bubble.y = pos.getY() - turn.getMapHeight();
	
				g.setColor(new Color(245, 245, 245, 200));
				g.fill(bubble);

				drawText(g, message, bubble.getCenterX(), bubble.getCenterY() , Color.BLACK, new Color(0, 0, 0, 0),1.0f);
			}
		}
	}

	private static float getBellCurve(float percent) {
		return (float)((1-Math.cos(percent * Math.PI * 2))/2);
	}

	private static float getSmoothCurve(float percent) {
		return (float)((1-Math.cos(percent * Math.PI))/2);
	}

	/**
	 * Draw the background image onto the screen.
	 * 
	 * @param g - the graphics object to draw onto
	 */
	public static void renderBackground(Graphics2D g, CastlesMap map) {
		setBackground(map.getField("theme"));
		AffineTransform original = g.getTransform();

		int mapWidth = map.getWidth();
		int mapHeight = map.getHeight();

		//Step 1 is scale the image so it takes up 1 tile width (same 
		float scaleWidth = 1.0f/backgroundImage.getWidth();
		float scaleHeight = 1.0f/backgroundImage.getHeight();

		//Remember that transforms happen in reverse!
		AffineTransform at = new AffineTransform();
		
		at.translate(0, -mapHeight);
		at.scale(scaleWidth * mapWidth, scaleHeight * mapHeight);
		g.transform(at);

		g.drawImage(backgroundImage,0,0,null);

		//restore original transform
		g.setTransform(original);
	}
	
	/**
	 * This is here in case we need to perform any math
	 * to draw into the correct grid coordinates. In our case,
	 * our grid position {2, 0} might need to be drawn at (200, 0)
	 * to account for the width of the grid space.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param text - the text to draw
	 * @param p - the position of the object we want to draw text over
	 * @param tweenPercent - the percentage to tween the text
	 */
	private static void drawTextOnGrid(Graphics2D g, String text, Position p, Color textColor, float size) {

		//These constants will determine where to draw the text within
		//the grid square. If the grid square is 100x100, then these
		//constants should never be larger than (100-textHeight) or
		//(100-textWidth)
		int pixelConstantHorizontal = 25;
		int pixelConstantVertical = 25;

		drawText(g, text, p.getX(), -p.getY(), textColor, new Color(0, 0, 0, 0), size);
	}

	/**
	 * 
	 * Draws a BufferedImage within a grid based on the size of the background image.
	 * 
	 * @param g - graphics object
	 * @param img - img to draw
	 * @param x - x position in grid to draw
	 * @param y - y position in grid to draw
	 * @param rotation - rotation of object
	 */
	private static void drawToScale(Graphics2D g, CastlesMap map, BufferedImage img, int x, int y, float rotation, float scaleFactor, float alpha) {

		// Scale the dimensions of the image
		int gx = (int)(scaleFactor * img.getWidth());
		int gy = (int)(scaleFactor * img.getHeight());
		
		AffineTransform origin = g.getTransform();
		
		g.translate(-gx / 2.0, -gy / 2.0);
		g.drawImage(img, x, y - map.getHeight(), gx, gy, null);
		g.translate(gx / 2.0, gy / 2.0);
		
		g.setTransform(origin);
	}

	// TODO Tweak transformation (possibly add in custom transform functions?)
	private static Point2D toPoint2D(Position p) {
		return new Point2D.Float(p.getX(), p.getY());
	}

	public static Map<Castles.api.TeamColor, Color> getColors() {
		return colors;
	}

	/**
	 * Loads a set of PNG's into a hashmap.
	 * @return h - a Hashmap containing the loaded BufferedImages
	 * @param arr - a String array containing paths to the PNG files to load
	 * @throws IOException
	 */
	public static Map<Castles.api.TeamColor, BufferedImage> loadIntoMap(File[] arr) throws IOException{
		Map<Castles.api.TeamColor, BufferedImage> h = new HashMap<>();
		BufferedImage image;
		
		for(int i = 0; i < arr.length; i++){
			image = ImageIO.read(arr[i]);
			h.put(Castles.api.TeamColor.values()[i], image);
		}
		
		return h;
	}
//	private static void renderPaths
	
	
	
//	/**
//	 * Draw each emitter onto the screen.
//	 * 
//	 * @param g - the graphics object to draw onto
//	 * @param turn - the current turn we are trying to draw
//	 * @param nextTurn - the next turn we will need to draw (for tweening)
//	 * @param tweenPercent - the percentage to tween
//	 */
//	private static void renderEmitters(Graphics2D g, Turn turn, Turn nextTurn, float bellTween, float smoothTween) {
//		for (Emitter e : turn.getAllEmitters()) {
//			//Get a reference to the next turn's Emitter
//			Emitter nextE = (Emitter)nextTurn.getUtil().updateEntity(e);
//			float scale = 1;
//			if (e.getRotation() != nextE.getRotation()) {
//				scale = 1 + bellTween;	//Draw twice as big if it is about to move
//			}
//			Position pos = e.getPosition();
//			float rotation = tweenFloat((float)Math.toRadians(e.getRotation()),(float)Math.toRadians(nextE.getRotation()),smoothTween);
//
//			drawToScale(g, emitterImages.get(e.getTeam().getColor()), pos.getX(), pos.getY(), rotation, scale,1);
//			drawTextOnGrid(g, e.getID() + "", e.getPosition(), Color.white, 0.49f);
//		}
//	}
//
//	private static float tweenFloat(float start, float end, float tween) {
//		return (start*(1-tween)) + (end*(tween));
//	}
//
//
//	/**
//	 * Draw each repeater onto the screen.
//	 * 
//	 * @param g - the graphics object to draw onto
//	 * @param turn - the current turn we are trying to draw
//	 * @param nextTurn - the next turn we will need to draw (for tweening)
//	 * @param tweenPercent - the percentage to tween
//	 */
//	private static void renderRepeaters(Graphics2D g, Turn turn, Turn nextTurn, float bellTween, float smoothTween) {
//		for (Repeater r : turn.getAllRepeaters()) {
//
//			//Get a reference to the next turn's Repeater
//			Repeater nextR = (Repeater)nextTurn.getUtil().updateEntity(r);
//
//			float scale = 1;
//			if (r.getRotation() != nextR.getRotation()) {
//				scale = 1 + bellTween;	//Draw twice as big if it is about to move
//			}
//
//			Position pos = r.getPosition();
//			float rotation = tweenFloat((float)Math.toRadians(r.getRotation()), (float)Math.toRadians(nextR.getRotation()), smoothTween);
//
//			drawToScale(g, repeaterImage, pos.getX(), pos.getY(), rotation, scale,1);
//
//			// Draw cooldown to the grid
//			if (r.getCooldown() > 0) {
//				drawTextOnGrid(g, "" + r.getCooldown(), pos, Color.WHITE,1.0f);
//			}
//			
//			//Draw the id on top (zoom in a lot!)
//			drawTextOnGrid(g, r.getID() + "", pos, Color.white, 0.49f);
//		}
//	}
//
//	private static void renderRepeaterCircles(Graphics2D g, Turn turn, Turn nextTurn, float bellTween, float smoothTween) {
//		for (Repeater r : turn.getAllRepeaters()) {
//			
//			
//			//Do one for each turn (current and next)
//			Team owner = r.getOwner();
//			
//			//Do one for each turn (current and next)
//			Repeater nextR = (Repeater)nextTurn.getMap().getEntity(r.getID());
//			Team nextOwner = nextR.getOwner();
//			
//			
//			if (owner != null) {
//				BufferedImage ownerImage = selectorImages.get(owner.getColor());
//				float scale = 1-smoothTween;
//				//If owners haven't changed
//				if (nextOwner != null && owner.getID() == nextOwner.getID()) {
//					scale = 1;
//				}
//				drawToScale(g, ownerImage, r.getPosition().getX(), r.getPosition().getY(), 0, scale, 1);
//			}
//			
//			if (nextOwner != null) {
//				BufferedImage ownerImage = selectorImages.get(nextOwner.getColor());
//				float scale = smoothTween;
//				//If owners haven't changed
//				if (owner != null && owner.getID() == nextOwner.getID()) {
//					scale = 0;
//				}
//				drawToScale(g, ownerImage, r.getPosition().getX(), r.getPosition().getY(), 0, scale, 1);
//			}
//		}
//	}
//	
//	/**
//	 * Draw the targets onto the screen
//	 *
//	 * @param g - the graphics object to draw onto
//	 * @param turn - a Turn object representing the current turn to draw
//	 * @param tweenPercent - the amount to tween between this turn and the previous turn
//	 */
//	private static void renderTargets(Graphics2D g, Turn turn, Turn nextTurn, float part2BellTween, float part2SmoothTween) {
//		int i = 0;
//		for(Target t : turn.getAllTargets()) {
//			Target nextTarget = (Target)nextTurn.getMap().getEntity(t.getID());
//			Position pos = t.getPosition();
//
//			drawToScale(g, targetImages[(targetStart+i)%targetImages.length], pos.getX(), pos.getY(), 0,1,1);
//			i += 1;
//
//
//			//Draw clouds over undiscovered things
//			float alpha = 1 - part2SmoothTween;
//			if (!nextTarget.isDiscovered()) { alpha = 1; }
//			if (t.isDiscovered()) { alpha = 0; }
//			int numberOfPloofs = 3;
//			for (int p = 0; p < numberOfPloofs; p++) {
//				float angle = ((float)(Math.PI*2) / numberOfPloofs) * p;
//				float distance = 0.2f;
//				float xOffset = (float)Math.cos(angle)*distance;
//				float yOffset = (float)Math.sin(angle)*distance;
//				drawToScale(g, cloudImage, xOffset+pos.getX(), yOffset+pos.getY(), 0, 1,alpha);
//			}
//
//			//If this has been discovered this turn
//			if (t.getPointValue() != nextTarget.getPointValue()) {
//				alpha = 1-(float)Math.pow(part2SmoothTween,6);
//				numberOfPloofs = 10;
//				for (int p = 0; p < numberOfPloofs; p++) {
//					float angle = ((float)(Math.PI*2) / numberOfPloofs) * p;
//					angle += part2SmoothTween * (float)Math.PI / 4;
//					float distance = (part2SmoothTween * 2.0f);
//					float xOffset = (float)Math.cos(angle)*distance;
//					float yOffset = (float)Math.sin(angle)*distance;
//					float rotation = 0;//(float)((Math.PI) * part2SmoothTween) + angle;
//					drawToScale(g, cloudImage, xOffset+pos.getX(), yOffset+pos.getY(), rotation, part2SmoothTween*2,alpha);
//				}
//				drawToScale(g,discoveryImage,pos.getX(),pos.getY(),0,4 * part2SmoothTween, alpha);
//			}
//			
//			drawTextOnGrid(g, t.getID() + "", pos, Color.black, 0.49f);
//		}
//	}
//
//	/**
//	 * Draw the walls onto the screen
//	 *
//	 * @param g - the graphics object to draw onto
//	 * @param turn - a Turn object representing the current turn to draw
//	 * @param tweenPercent - the amount to tween between this turn and the previous turn
//	 */
//	private static void renderWalls(Graphics2D g, Turn turn, Turn nextTurn) {
//		for (Wall w : turn.getAllWalls()) {
//			drawToScale(g, wallImage, w.getPosition().getX(), w.getPosition().getY(), 0, 1,1);
//			drawTextOnGrid(g, w.getID() + "", w.getPosition(), Color.white, 0.49f);
//		}
//	}
//
//	private static Line2D.Float getLazerLine(Position p1, float rotation, Point2D p2) {
//		rotation = (float)Math.toRadians(rotation);
//		float pX = (float)(p1.getX() + (1000 * Math.cos(rotation)));				// Get the line to the edge of the screen
//		float pY = (float)(p1.getY() + (1000 * Math.sin(rotation)));				// Given the current rotation
//
//		if (p2 != null) {
//			pX = (float)p2.getX();
//			pY = (float)p2.getY();
//		}
//
//		return new Line2D.Float(p1.getX(), -p1.getY(), pX, -pY);
//	}
//
//	/**
//	 * Draw the lazers onto the screen, including the "bounces"
//	 * 
//	 * @param g - the graphics object to draw onto
//	 * @param turn - a Turn object representing the current turn to draw
//	 * @param tweenPercent - the amount to tween between this turn and the previous turn
//	 */
//	private static void renderLazers(Graphics2D g, Turn turn, Turn nextTurn, float smoothTween, float part2SmoothTween) {
//		//Render a source from each emitter
//		for (Emitter e : turn.getAllEmitters()) {
//			Rotatable r = e;
//			g.setColor(getColors().get(e.getTeam().getColor()));
//			g.setStroke(new BasicStroke(0.11f));
//
//			HashSet<Rotatable> visited = new HashSet<>();
//
//			//Store a list of all lines that will be drawn
//			//This is used for tweening
//			boolean linesHaveChanged = false;	//Set to true after we hit the repeater that moved
//			List<Line2D.Float> unchangingLines = new LinkedList<Line2D.Float>();
//			List<Line2D.Float> newLines = new LinkedList<Line2D.Float>();
//
//			do {
//				visited.add(r);
//				Rotatable nextR = (Rotatable)nextTurn.getUtil().updateEntity(r);
//				float rotation = tweenFloat(r.getRotation(),nextR.getRotation(),smoothTween);
//
//				//If the team that owns this rotater is not for this color, don't draw lazers
//				if (nextR.getOwner() == null || nextR.getOwner().getID() != e.getTeam().getID()) {
//					//System.out.printf("Not drawing lazer for team %d because repeater %d's owner is %s\n",e.getTeam().getID(),r.getID(),r.getOwner());
//					continue;
//				}
//
//				//Decide if this needs to be redrawn or not
//				if (r.getRotation() != nextR.getRotation()) {
//					linesHaveChanged = true;
//				}
//
//				//Clone the rotatable object so we can modify its rotation and get the point of collision
//				Rotatable clone;
//				if (r instanceof Emitter) {
//					clone = new Emitter(((Emitter) r).getTeam(), rotation, r.getPosition(), r.getID(), turn.getMap());
//				} else {
//					clone = new Repeater(rotation, r.getPosition(),((Repeater)r).getCooldown(), r.getID(), null, r.getOwner(), turn.getMap());
//				}
//
//				//Get the target first...
//				Positionable target = Rotatable.calculateTarget(clone);
//
//				//Get the actual collision point
//				Point2D point = (target!=null) ? clone.isColliding(target) : null;
//
//				if (linesHaveChanged) {
//					newLines.add(getLazerLine(r.getPosition(), rotation, point));
//				} else {
//					unchangingLines.add(getLazerLine(r.getPosition(), rotation, point));
//				}
//				r = (target instanceof Repeater) ? (Rotatable)target : null;
//			} while (r != null && !visited.contains(r));
//
//			//Draw all beams that aren't changing
//			for (Line2D.Float line : unchangingLines) {
//				drawLazerBeam(g,line,e.getTeam().getColor());
//			}
//
//
//			//Now that we've calculated the lines, lets draw them based on the tween
//			//Calculate the total length of the line
//			float totalDistance = 0;
//			for (Line2D line : newLines) {
//				totalDistance += lineLength(line);
//			}
//
//			//How much distance should we draw
//			float drawDistance = totalDistance * part2SmoothTween;
//
//			//A counter of the lines we've drawn
//			float currentDistance = 0;
//
//			//Now draw the first tween percent of each line
//			for (Line2D.Float line : newLines) {
//				float lineLength = lineLength(line);
//
//				//Draw it regularly if we're still less than draw distance
//				if (currentDistance+lineLength < drawDistance) {
//					drawLazerBeam(g,line,e.getTeam().getColor());
//				} else {
//					//What fraction of the line do we need to draw such that
//					//currentDistance = drawDistnace?
//					//(lineLength * percent)+currentDistance = drawDistance
//					//Some math reveals:
//					//percent = (drawDistance - currentDistance) / lineLength
//					float percent = (drawDistance - currentDistance) / lineLength;
//					drawLazerBeam(g,partOfLine(line,percent),e.getTeam().getColor());
//
//					//No need to draw any more lines
//					break;
//				}
//
//				currentDistance += lineLength;
//			}
//
//
//		}
//	}
//
//	private static void drawLazerBeam(Graphics2D g, Line2D.Float line, lazers.api.Color color) {
//		g.draw(line);
//
//		BufferedImage img = lazerImages.get(color);
//
//		AffineTransform original = g.getTransform();
//
//		AffineTransform af = new AffineTransform();
//		float length = lineLength(line);
//		//Adjust the scale to be 1 unit
//		float scale = 1f/img.getWidth();
//		double rotation = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
//
//		float beamWidthScale = 0.4f;
//
//		af.translate(line.x1, line.y1);
//		af.rotate(rotation);
//		af.translate(0, -0.5f);
//		af.scale(length*scale, scale);
//
//		g.transform(af);
//		g.drawImage(img,0,0,null);
//
//		g.setTransform(original);
//	}
//
//	private static float lineLength(Line2D line) {
//		double deltaX = line.getX2() - line.getX1();
//		double deltaY = line.getY2() - line.getY1();
//		return (float)Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
//	}
//
//	private static Line2D.Float partOfLine(Line2D.Float line,float percent) {
//		float xOffset = (line.x2-line.x1) * percent;
//		float yOffset = (line.y2-line.y1) * percent;
//		return new Line2D.Float(line.x1,line.y1,line.x1+xOffset,line.y1+yOffset);
//	}
//}line.x1);
//
//		float beamWidthScale = 0.4f;
//
//		af.translate(line.x1, line.y1);
//		af.rotate(rotation);
//		af.translate(0, -0.5f);
//		af.scale(length*scale, scale);
//
//		g.transform(af);
//		g.drawImage(img,0,0,null);
//
//		g.setTransform(original);
//	}
//
//	private static float lineLength(Line2D line) {
//		double deltaX = line.getX2() - line.getX1();
//		double deltaY = line.getY2() - line.getY1();
//		return (float)Math.sqrt((deltaX*deltaX) + (deltaY*deltaY));
//	}
//
//	private static Line2D.Float partOfLine(Line2D.Float line,float percent) {
//		float xOffset = (line.x2-line.x1) * percent;
//		float yOffset = (line.y2-line.y1) * percent;
//		return new Line2D.Float(line.x1,line.y1,line.x1+xOffset,line.y1+yOffset);
//	}
}