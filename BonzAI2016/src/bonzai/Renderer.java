package bonzai;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

// Various helper methods
public abstract class Renderer {
	private static Point2D.Double lastUL = new Point2D.Double();
	private static Point2D.Double lastDR = new Point2D.Double();
	
	/**
	 * Draw a string, then return its bounds
	 * @param g
	 * @param str
	 * @param color
	 * @return
	 */
	protected static Rectangle2D.Double drawText(Graphics2D g, String str, double x, double y, Color color) {
		int size = 18;
		double sized = size;
		
		Font font = new Font("arial", Font.BOLD,size);
		g.setFont(font);				// Why does g.setFont need to be called if we aren't drawing the text with g???
		
		int width = g.getFontMetrics().stringWidth(str);
		int height = g.getFontMetrics().getAscent() + g.getFontMetrics().getDescent();
		
		// Draw the text using a BufferedImage
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D imgg = img.createGraphics();
		imgg.setColor(color);
		imgg.fillRect(0, 0, width, height);
		imgg.setFont(font);
		imgg.setColor(Color.WHITE);
		imgg.drawString(str, 0, g.getFontMetrics().getAscent());
		
		// What do AffineTransforms do???
		AffineTransform at = new AffineTransform();
		at.translate(x + (width / 2 / sized), y - (height / size));
		at.scale(-1.0 / size, 1.0 / size);
		
		// Draw the image to the passed graphics instance
		g.drawImage(img, at, null);
		g.setColor(color);				// Why is this after the drawing occurred???
		
		// Return the bounding rectangle for the displayed Text
		return new Rectangle2D.Double(x - (width / sized), y, width / sized, height/ sized);
	}

	/**
	 * Creates a rectangle that can be drawn
	 * @param origin A point representing the center of the rectangle
	 * @param size A point representing the size of the rectangle (x = width, y = height)
	 * @return
	 */
	protected static Shape rectangle(Point2D.Double origin, Point2D size) {
		final double points[][] = {
				{ size.getX() / 2, size.getY() / 2 },
				{ -size.getX()/ 2, size.getY() / 2 },
				{ -size.getX()/ 2, -size.getY()/ 2 },
				{ size.getX() / 2, -size.getY()/ 2 },
				{ size.getX() / 2, size.getY() / 2 }
		};

		Path2D.Double rectangle = new Path2D.Double();
		rectangle.moveTo(origin.getX() + points[0][0], origin.getY() + points[0][1]);
		
		for (int k = 1; k < points.length; k++) {
			rectangle.lineTo(origin.getX() + points[k][0], origin.getY() + points[k][1]);
		}

		return rectangle;
	}

	/**
	 * Creates a hexagon that can be drawn.
	 * QUERY Does the returned hexagon require scaling?
	 * @param origin A point representing the center of the rectangle
	 * @return
	 */
	protected static Shape hexagon(Point2D.Double origin) {
		final double phi = 2 * Math.PI / 6;

		Path2D.Double hexagon = new Path2D.Double();
		hexagon.moveTo(origin.x + Math.cos(0), origin.y + Math.sin(0));
		
		for(int x = 1; x < 6; x += 1) {
			hexagon.lineTo(origin.x + Math.cos(phi * x), origin.y + Math.sin(phi * x));
		}

		return hexagon;
	}
	
	/**
	 * Creates a triangle that can be drawn
	 * @param origin A point representing the center of the triangle
	 * @return
	 */
	protected static Shape triangle(Point2D.Double origin) {
		Path2D.Double tri = new Path2D.Double();
		tri.moveTo(origin.x, origin.y);
		tri.lineTo(origin.x + 0.2, origin.y - 0.4);
		tri.lineTo(origin.x - 0.2, origin.y - 0.4);
		return tri;
	}

	/**
	 * Creates a five-pointed star that can be drawn
	 * @param origin A point representing the center of the star
	 * @return
	 */
	protected static Shape star(Point2D.Double origin) {
		final double points[][] = {
				{ -1.00, -0.15 }, { -0.25, -0.25 }, {  0.00, -0.90 }, 
				{  0.25, -0.25 }, {  1.00, -0.15 }, {  0.50,  0.25 },
				{  0.60,  0.90 }, {  0.00,  0.50 }, { -0.60,  0.90 },
				{ -0.50,  0.25 }, { -1.00, -0.15 }
		};

		Path2D star = new Path2D.Double();
		star.moveTo(origin.getX() + points[0][0], origin.getY() + points[0][1]);
		
		for (int k = 1; k < points.length; k++) {
			star.lineTo(origin.getX() + points[k][0], origin.getY() + points[k][1]);
		}

		return star;
	}	

	protected static Point2D.Double tween(Point2D.Double current, Point2D.Double next, float tween) {
		Point2D.Double between = new Point2D.Double();
		between.x = (current.x * (1-tween)) + (next.x * (tween));
		between.y = (current.y * (1-tween)) + (next.y * (tween));
		return between;
	}

	public static Point2D.Double getLastUL() {
		return lastUL;
	}

	public static void setLastUL(Point2D.Double lastUL) {
		Renderer.lastUL = lastUL;
	}

	public static Point2D.Double getLastDR() {
		return lastDR;
	}

	public static void setLastDR(Point2D.Double lastDR) {
		Renderer.lastDR = lastDR;
	}
}
