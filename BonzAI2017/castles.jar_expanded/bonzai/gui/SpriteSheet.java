package bonzai.gui;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * A simple spritesheet which loads file resources relative to the system
 * classloader.
 * <p>
 * Not intended for use with external resources.
 **/
public class SpriteSheet {
	/** The filename to load **/
	public final String filename;

	/** The width of a single tile **/
	public final int tileW;

	/** The height of a single tile **/
	public final int tileH;

	// The matrix of tiles
	private final BufferedImage[][] tiles;

	/**
	 * Load a file and generate a spritesheet given the width and height of an
	 * individual tile.
	 *
	 * @param filename
	 *            the file to load
	 * @param tileW
	 *            the width of a tile
	 * @param tileH
	 *            the height of a tile
	 **/
	public SpriteSheet(String filename, int tileW, int tileH) {
		this.filename = filename;
		this.tileW = tileW;
		this.tileH = tileH;

		try {
			BufferedImage image = ImageIO.read(ClassLoader
					.getSystemResource(filename));

			int xTiles = image.getWidth() / tileW;
			int yTiles = image.getHeight() / tileH;

			this.tiles = new BufferedImage[yTiles][xTiles];
			for (int y = 0; y < yTiles; y += 1) {
				for (int x = 0; x < xTiles; x += 1) {
					tiles[y][x] = image.getSubimage(x * tileW, y * tileH,
							tileW, tileH);
				}
			}
		} catch (Exception e) {
			throw new Error("Failed to load spritesheet: " + filename, e);
		}
	}

	/**
	 * Returns the tile given by the tile coordinate (x,y).
	 *
	 * @return the specified tile
	 **/
	public BufferedImage image(int x, int y) {
		return tiles[y][x];
	}
}
