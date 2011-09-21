package org.squidy.nodes.reactivision.remote;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class GridPoint extends FloatPoint {
	
	private static int infoDisplayMode = 0;
	
	private static BufferedImage image =
		new BufferedImage(5, 5, BufferedImage.TYPE_4BYTE_ABGR);
	
	static {
		for (int x = 0; x < image.getWidth(); ++x)
			for (int y = 0; y < image.getHeight(); ++y)
				image.setRGB(x, y, -256);
	}
	
	public static void toggleID() {
		if (infoDisplayMode +1 < 3)
			++infoDisplayMode;
		else
			infoDisplayMode = 0;
	}
	
	private char[][] ids = new char[3][];
	
	public boolean isSet = true;
	public boolean positionInterpolated = false;
	
	public GridPoint(){
		this(0,0);
	}
	
	public GridPoint(int x, int y) {
		super(x, y);
		ids[0] = null;
		ids[1] = new char[2];
		ids[1][0] = 'x';
		ids[1][1] = 'x';
		ids[2] = null;
	}
	
	/**
	 * @return the distance between this GridPoint and a point with the specified
	 * coordinates.
	 */
	public double distanceFrom(int x, int y) {
		return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}
	
	/**
	 * Draws this GridPoint.
	 * @param g the surface onto which this GridPoint will be drawn.
	 */
	public void draw(Graphics2D g) {
		g.drawImage(image, (int)x - 2, (int)y - 2, null);
		if (ids[infoDisplayMode] != null)
			g.drawChars(ids[infoDisplayMode], 0, ids[infoDisplayMode].length, (int)x+3, (int)y+13);
	}
	
	/**
	 * @return <code>true</code>, if a mousePressed event at the given coordinates
	 * is be within this GridPoints grabbing area.
	 */
	public boolean grabbed(int x, int y) {
		//based on Manhattan distance
		if (Math.abs(this.x - x) > 2)
			return false;
		if (Math.abs(this.y - y) > 2)
			return false;
		return true;
	}
	
	/**
	 * Rotates this GridPoint clockwise around the passed Point;
	 */
	public void rotateClockwise(final Point pivot) {
		//translate
		final float xDiff = pivot.x - x;
		final float yDiff = pivot.y - y;
		x = pivot.x + yDiff;
		y = pivot.y - xDiff;
	}
	
	public void setID(int gridPointID) {
		//point ID number
		final int one = gridPointID / 10;
		final int two = gridPointID % 10;
		ids[1][0] = String.valueOf(one).charAt(0);
		ids[1][1] = String.valueOf(two).charAt(0);
		
		//grid position
		final int x = gridPointID % 9 + 1;
		final int y = gridPointID / 9 + 1;
		ids[2] = new char[6];
		ids[2][0] = '[';
		ids[2][1] = String.valueOf(y).charAt(0);
		ids[2][2] = ']';
		ids[2][3] = '[';
		ids[2][4] = String.valueOf(x).charAt(0);
		ids[2][5] = ']';
	}
	
	public void setPosition(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public String toString() {
		return "[" + x + "|" + y + "]";
	}
}

