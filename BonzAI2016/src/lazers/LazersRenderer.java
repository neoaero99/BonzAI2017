package lazers;

import java.awt.*;
import java.awt.Color;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageTranscoder;
import javax.swing.JPanel;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

import bonzai.Action;
import bonzai.Position;
import bonzai.Rotatable;
import lazers.api.*;

public class LazersRenderer extends bonzai.Renderer {
	
	private static JPanel panel;
	
	private static int mapHeight;
	private static int mapWidth; 
	
	static BufferedImage backgroundImage;
	
	//Temporary things to hold fileNames. Need a better way to get these in
	static String backgroundFileName = "art/maps/desert_small.png";
	
	static String[] targetFileNames = {"art/sprites/target_cropcircle1.svg",
									   "art/sprites/target_cropcircle2.svg",
									   "art/sprites/target_henge.svg",
									   "art/sprites/target_nazca1.svg",
									   "art/sprites/target_nazca2.svg"};
	
	static String repeaterFileName = "art/sprites/repeater.svg";
	
	//Need different images for each color.
	static String[] lazerFileNames = {"art/sprites/laser_red.svg",
									  "art/sprites/laser_yellow.svg", 
									  "art/sprites/laser_blue.svg",
									  "art/sprites/laser_green.svg",
									  "art/sprites/laser_orange.svg",
									  "art/sprites/laser_purple.svg"};
	
	static File[] emitterFileNames = {//new File("art\\sprites\\fox_red.svg"), 
										//new File("art\\sprites\\fox_yellow.svg"), 
										//new File("art\\sprites\\fox_blue.svg"),
										//new File("art\\sprites\\fox_green.svg"),
										//new File("art\\sprites\\fox_orange.svg"),
										new File("art\\sprites\\fox_purple.svg")};
	
	private static final Map<lazers.api.Color, Color> colors = new HashMap<>();
	private static Map<lazers.api.Color, Document> emitterImages = new HashMap<>();
	private static Map<lazers.api.Color, Document> lazerImages = new HashMap<>();
	private static Map<lazers.api.Color, Document> targetImages = new HashMap<>();
	
	
	static {
		colors.put(lazers.api.Color.RED,    new Color(217,  51,   21)); // Red
		colors.put(lazers.api.Color.YELLOW, new Color(238, 218,  102)); // Yellow
		colors.put(lazers.api.Color.BLUE,   new Color(68,   55,  142)); // Blue
		colors.put(lazers.api.Color.GREEN,  new Color(0,   173,   59)); // Green
		colors.put(lazers.api.Color.ORANGE, new Color(236, 135,    0)); // Orange
		colors.put(lazers.api.Color.PURPLE, new Color(207,  71,  207)); // Purple
		
		try { 
			//TODO: Figure out how to get filenames from runtime
			//backgroundImage = ImageIO.read(new File(backgroundFileName));
			//targetImage = ImageIO.read(new File(targetFileName));
			//repeaterImage = ImageIO.read(new File(repeaterFileName));
			
	// I don't know which branch has the correct version - Grayson
			//Load emitter and lazer images into the maps.
			//The array indices are based on color, and should match the teamID's given to each team.
			for (int i = 0; i < 6; i++) {
				File fileToRead = new File(emitterFileNames[i]);
				System.out.println("File = " + fileToRead);
				
				BufferedImage imgToStore = ImageIO.read(fileToRead);
				System.out.println("Img = " + imgToStore);
				
				emitterImages.put(lazers.api.Color.values()[i], imgToStore);
				//lazerImages.put(lazers.api.Color.values()[i], ImageIO.read(new File(lazerFileNames[i])));
			
			//load our SVG's
			//targetImages = SVGLoad(targetFileNames);
			emitterImages = SVGLoad(emitterFileNames);
			//lazerImages = SVGLoad(lazerFileNames);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Render a preview of the map, for displaying the launcher.
	 **/
	public static void render(Graphics2D g, LazersMap map) {
		// TODO This
	}
	
	
	/**
	 * Render a turn within the game window.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param turn - the turn to render
	 * @param nextTurn - 
	 * @param action
	 * @param tweenPercent
	 */
	public static void render(Graphics2D g, Turn turn, Action action, float tweenPercent) {
		//Draw the background first over top of all the old stuff.
		renderBackground(g);
		renderEmitters(g, turn, tweenPercent);
		//renderRepeaters(g, turn, tweenPercent);
		//renderTargets(g, turn, tweenPercent);
		//renderLazers(g, turn, tweenPercent);
		//renderWalls(g, turn, tweenPercent);
		//TODO: Figure out how this is done. It is literally mystifying
		//renderAction(g, turn, turn.actor(), nextTurn.current(turn.actor()), action, tweenPercent);
	}
	
//	private static void renderTargets(Graphics2D g, Turn turn, float tweenPercent) {
//		//TODO: implement filter if not hit to remove inner if statement
//		for(Target t : turn.getTargets()) {
//			//Only draw targets that have not been hit.
//			if (!t.isHit()) {
//				drawOnGrid(g, targetImage, t.getPosition(), tweenPercent);
//			}
//		}
//	}
	
	private static void renderWalls(Graphics2D g, Turn turn, float tweenPercent) {
		for (Wall w : turn.getWalls()) {
			Position[] ps = w.getPositions();
			// Transform ps to match the grid
			//g.setColor(Color.BLACK);
			//g.setStroke(new BasicStroke(.1f));
			g.draw(new Line2D.Double(toPoint2D(ps[0]), toPoint2D(ps[1])));
			// If we use a Image we can move some logic to drawOnGrid
		}
	}
	
	private static void renderLazers(Graphics2D g, Turn turn, float tweenPercent) {
		//TODO: This
		
		//Scale image vertically to proper distance
		//Rotate image to point in proper direction
		//Apply to proper grid areas.
	}

	/**
	 * Draw each repeater onto the screen.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param turn - the current turn we are trying to draw
	 * @param nextTurn - the next turn we will need to draw (for tweening)
	 * @param tweenPercent - the percentage to tween
	 */
//	private static void renderRepeaters(Graphics2D g, Turn turn, float tweenPercent) {		
//		for (Repeater r : turn.getRepeaters()) {
//
//			BufferedImage img = rotateImage(r, repeaterImage);
//			drawOnGrid(g, repeaterImage, r.getPosition(), tweenPercent);
//			
//			//Convert int cooldown to a string
//			String cd = ((Integer) r.getCooldown()).toString();
//			
//			//Draw the cooldown to the grid.
//			drawTextOnGrid(g, cd, r.getPosition(), tweenPercent);
//		}
//	}

	/**
	 * Draw each emitter onto the screen.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param turn - the current turn we are trying to draw
	 * @param nextTurn - the next turn we will need to draw (for tweening)
	 * @param tweenPercent - the percentage to tween
	 */
	private static void renderEmitters(Graphics2D g, Turn turn, float tweenPercent) {
		BufferedImage img;
		Document d;
		for (Emitter e : turn.getEmitters()) {
			//img = rotateImage(e, emitterImages.get(e.getTeam().getColor()));
			d = emitterImages.get(e.getTeam().getColor()); 
			img = transcodeSVG(d);
			//Draw the emitter to the grid.
			//drawOnGrid(g, img, e.getPosition(), tweenPercent);
			g.drawImage(img, 0, 0, null);
		}
	}
	
	public static void renderBackground(Graphics2D g) {
		AffineTransform original = g.getTransform();
		
		//Scale the image from X pixels down to 1.0 (100% screen size)
		AffineTransform at = new AffineTransform();
		float scale = 12.0f / backgroundImage.getWidth();
		at.scale(scale,scale);
		g.transform(at);
		
		g.drawImage(backgroundImage,0,0,null);
		
		//restore original transform
		g.setTransform(original);
	}
	
	//private static void renderAction(Graphics2D g, Turn turn, Unit actor, Unit nextActor, Action action, float tweenPercent) {
		
	//}
	
	
	/** ~~~~HELPER METHODS~~~~ **/
	
	/**
	 * This is here in case we need to perform any math
	 * to draw into the correct grid coordinates. In our case,
	 * our grid position {2, 0} might need to be drawn at (200, 0)
	 * to account for the width of the grid space.
	 * 
	 * @param g - the graphics object to draw onto
	 * @param im - the image to draw
	 * @param p - the position of the object we want to draw 
	 * @param tweenPercent - the percentage to tween the image
	 */
	private static void drawOnGrid(Graphics2D g, Image im, Position p, float tweenPercent) {
		//Figure out current window size
		int height = panel.getHeight();
		int width = panel.getWidth();
		
		//Tiles are always squares.
		int tileSize;
		int xMargin = 0;
		int yMargin = 0;
		
		if (height < width) {
			tileSize = height/mapHeight;
			xMargin = (width - mapWidth)/2;
		} else {
			tileSize = width/mapWidth;
			yMargin = (height - mapHeight)/2; 
		}
	
		//Scale image based on tilesize
		//Draw the map
		g.drawImage(im, tileSize, tileSize, (p.getX()*tileSize)+xMargin, (p.getY()*tileSize)+yMargin, null);
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
	private static void drawTextOnGrid(Graphics2D g, String text, Position p, float tweenPercent) {
		
		//These constants will determine where to draw the text within
		//the grid square. If the grid square is 100x100, then these
		//constants should never be larger than (100-textHeight) or
		//(100-textWidth)
		int pixelConstantHorizontal = 25;
		int pixelConstantVertical = 25;
		
		g.drawChars(text.toCharArray(), 0, text.length(), p.getX()+pixelConstantHorizontal, p.getY()+pixelConstantVertical);
	}
	
	/**
	 * Returns a rotated image for a Rotatable object.
	 * 
	 * @param r - the rotatable object to rotate
	 * @param img - the image to rotate
	 * @return - the rotated image
	 */
	//TODO: May be buggy based on the anchor points - do they need to be the grid center?
	private static BufferedImage rotateImage(Rotatable r, BufferedImage img) {
		double rotation = Math.toRadians(r.getRotation());
		//Create a transform that will rotate our image.
		AffineTransform tx = AffineTransform.getRotateInstance(rotation, r.getPosition().getX(), r.getPosition().getY());
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		//op.filter() is responsible for rotating the image
		return op.filter(img, null);
	}
	
	// TODO Tweak transformation (possibly add in custom transform functions?)
	private static Point2D toPoint2D(Position p) {
		return new Point2D.Float(p.getX(), p.getY());
	}
	
	public static Map<lazers.api.Color, Color> getColors() {
		return colors;
	}
	
	public static void setPanel(JPanel p) {
		panel = p;
	}
	
	public static void setMapSize(int x, int y) {
		mapWidth = x;
		mapHeight = y;
	}
	
	/**
	 * Get a Graphics2D for your svg, today!
	 * @param d Document created from an SVG file that can be used by Batik
	 * @return a Graphics2D of your desired SVG file
	 */
	public static Graphics2D getSVGGraphic(Document d){
		return new SVGGraphics2D(d);
	}
	
	/**
	 * Loads a set of SVG's into a hashmap .
	 * @return h - a Hashmap containing the SVG Document objects
	 * @param arr - a String array containing paths to the SVG files to load
	 * @throws IOException
	 */
	public static Map<lazers.api.Color, Document> SVGLoad(File[] arr) throws IOException{
		Map<lazers.api.Color, Document> h = new HashMap<>();
		String parser = XMLResourceDescriptor.getXMLParserClassName();
	    SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		for(int i = 0; i < arr.length; i++){
		    Document d = f.createDocument(arr[i].toURI().toString());
			h.put(lazers.api.Color.values()[i], d);
		}
		
		return h;
	}
	
	public static BufferedImage transcodeSVG(Document d) {
		// Create a PNG transcoder
		PNGTranscoder transcoder = new PNGTranscoder();
		
		
		// Create the transcoder input
		TranscoderInput input;
		input = new TranscoderInput(d);
		// Create the transcoder output
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		TranscoderOutput output = new TranscoderOutput(ostream);
		try {
			// Transform the svg document into a PNG image
			transcoder.transcode(input, output);
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			
			return ImageIO.read(istream);
		
		} catch (Exception e) {
			System.err.println("Error transcoding image");
			e.printStackTrace();
		}
		
		return null;
	}
	
}
