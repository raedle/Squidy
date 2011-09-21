package org.squidy.nodes.reactivision.remote;

import static java.lang.Math.PI;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.event.MouseInputAdapter;

import org.squidy.nodes.reactivision.remote.control.ControlServer;
import org.squidy.nodes.reactivision.remote.image.ImageServer;



public final class CalibrationArea extends JPanel implements Scrollable {
	
	private enum CalibrationMode {
		STANDARD_CALIBRATION,
		QUICK_CALIBRATION,
		DISABLED;
	}
	
	private static final long serialVersionUID = 8461995937722552323L;
	
	private ControlServer controlServer;
	private ImageServer imageServer;
	private static final int manualCalibrationBorderWidth = 196;
	private static final int autoCalibrationBorderWidth = 32;
	private JPopupMenu popUpMenu;
	private CalibrationMode mode = CalibrationMode.STANDARD_CALIBRATION;
	/**
	 * Contains GridPoints in absolute pixel coordinates
	 */
	private GridPoint[] gridPoints = new GridPoint[63];//9*7
	/**
	 * Index of the next GridPoint to be placed during quick calibration.
	 */
	private int calibrationGridPointIndex;
	/**
	 * in ReacTIVision coordinates (default position = 0, individual deviation
	 * from that point in 1/8 camera image width increments).
	 * Use during receiving and sending the grid from the ReacTIVision client.
	 */
	private float[] grid;
	/**
	 * The horizontal grid "lines".
	 */
	private CatmullRomSpline[] horizontalSplines;
	/**
	 * The vertical grid "lines".
	 */
	private CatmullRomSpline[] verticalSplines;
	/**
	 * The pixel distance between two adjacent default GridPoint positions - 1.
	 */
	private int boxWidth;
	/**
	 * Reference to the currently grabbed GridPoint. Is <code>null</code> if no
	 * GridPoint is grabbed.
	 */
	private GridPoint grabbedGridPoint;
	
	/**
	 * If <code>true</code>, the location of the next MouseClickedEvent will be used
	 * as a pivot to rotate all GridPoints.
	 */
	private boolean rotateGridPoints = false;
	
	//Strokes
	final static float dash1[] = {5.0f};
	
	final static BasicStroke dashed = new BasicStroke(1.0f, 
            BasicStroke.CAP_BUTT, 
            BasicStroke.JOIN_MITER, 
            10.0f, dash1, 0.0f);
	final Stroke fullStroke = new BasicStroke(/*(float)1.5*/);
	
	private JMenuItem rotateClockwise;
	
	private JMenuItem invertGridPoints;
	
	private JMenuItem applyChanges;
	
	public CalibrationArea(ControlServer controlServer, ImageServer imageServer) {
		this.controlServer = controlServer;
		this.imageServer = imageServer;
		imageServer.setCalibrationArea(this);
		setOpaque( false );
		setFocusable(true);
		
		BufferedImage firstImage = imageServer.getImage();
		setPreferredSize( new Dimension(firstImage.getWidth() + 2 * getBorderWidth(),
				firstImage.getHeight() + 2 * getBorderWidth()));
		
		grid = controlServer.getGrid();
		
		controlServer.startCameraFeed();
		
		if (grid == null) {
			//makes no sense to continue
			mode = CalibrationMode.DISABLED;
			return;
		}
		
		this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		
		boxWidth = firstImage.getWidth() / 8;
		initGridPoints();
		initGridLines();
		addPopUpMenu();
		addEventListeners();
	}
	
	private void addEventListeners() {
		final MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {

			@Override
			public void mouseClicked(MouseEvent m) {
				if (m.getButton() == MouseEvent.BUTTON3) {
					rotateGridPoints = false;
					//show applyChanges only if all GridPoints are set
					if (gridPoints[gridPoints.length-1].isSet) {
						popUpMenu.add(rotateClockwise);
						popUpMenu.add(invertGridPoints);
						popUpMenu.add(applyChanges);
					} else {
						popUpMenu.remove(rotateClockwise);
						popUpMenu.remove(invertGridPoints);
						popUpMenu.remove(applyChanges);
					}
					popUpMenu.show(m.getComponent(), m.getX(), m.getY());
				}
				else if (m.getButton() == MouseEvent.BUTTON1) {
					if (rotateGridPoints) {
						final Point pivot = m.getPoint();
						for (int i = 0; i < gridPoints.length; ++i)
							gridPoints[i].rotateClockwise(pivot);
						rotateGridPoints = false;
						repaint();
					} else if (mode == CalibrationMode.QUICK_CALIBRATION) {
						final Point p = new Point(m.getX(), m.getY());
						if (!pointCollision(p)) {
							gridPoints[calibrationGridPointIndex].setPosition(p);
							gridPoints[calibrationGridPointIndex].isSet = true;
							if (++calibrationGridPointIndex >= gridPoints.length)
								mode = CalibrationMode.STANDARD_CALIBRATION;
							repaint();
						}
					}
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent m) {
				if (grabbedGridPoint != null) {
					grabbedGridPoint.setPosition(m.getPoint());
					repaint();
				}	
			}
			
			@Override
			public void mousePressed(MouseEvent m) {
				//check if point has been grabbed
				for (int i = 0; i < gridPoints.length; ++i) {
					if (!gridPoints[i].isSet)
						continue;
					if (gridPoints[i].grabbed(m.getX(), m.getY())) {
						grabbedGridPoint = gridPoints[i];
						break;
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent m) {
				if (grabbedGridPoint != null) {
					grabbedGridPoint = null;
					repaint();
				}
			}
		};
		this.addMouseListener(mouseInputAdapter);
		this.addMouseMotionListener(mouseInputAdapter);
		
		final KeyAdapter keyListener = new KeyAdapter() {
			
			private boolean control = false;
			
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_CONTROL)
					control = true;
				if (k.getKeyCode() == KeyEvent.VK_Z && control
						&& mode == CalibrationMode.QUICK_CALIBRATION) {
					--calibrationGridPointIndex;
					if (calibrationGridPointIndex < 0)
						calibrationGridPointIndex = 0;
					gridPoints[calibrationGridPointIndex].isSet = false;
					repaint();
				}
				if (k.getKeyCode() == KeyEvent.VK_SPACE) {
					GridPoint.toggleID();
					repaint();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_CONTROL)
					control = false;
			}
			
		};
		this.addKeyListener(keyListener);
	}
	
	private void addPopUpMenu() {
		popUpMenu = new JPopupMenu(); 
		JMenuItem quickCalibration = new JMenuItem("Start quick calibration");
		quickCalibration.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				mode = CalibrationMode.QUICK_CALIBRATION;
				for (int i = 0; i < gridPoints.length; ++i)
					gridPoints[i].isSet = false;
				calibrationGridPointIndex = 0;
				repaint();
			}
		});
		popUpMenu.add( quickCalibration );
		
		JMenuItem resetGrid = new JMenuItem("Reset Grid");
		resetGrid.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				mode = CalibrationMode.STANDARD_CALIBRATION;
				calibrationGridPointIndex = 0;
				for (int i = 0; i < gridPoints.length; ++i) {
					gridPoints[i].setPosition(getDefaultGridPointPosition(i));
					gridPoints[i].isSet = true;
				}
				repaint();
			}
		});
		popUpMenu.add( resetGrid );
		
		rotateClockwise = new JMenuItem("Rotate clockwise");
		rotateClockwise.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				rotateGridPoints = true;
			}
		});
		//do not add the above JMenuItem here
		
		invertGridPoints = new JMenuItem("Invert grid points");
		invertGridPoints.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				for (int i = 0; i < gridPoints.length / 2; ++i) {
					final float tempX = gridPoints[i].x;
					final float tempY = gridPoints[i].y;
					gridPoints[i].x = gridPoints[gridPoints.length - 1 - i].x;
					gridPoints[i].y = gridPoints[gridPoints.length - 1 - i].y;
					gridPoints[gridPoints.length - 1 - i].x = tempX;
					gridPoints[gridPoints.length - 1 - i].y = tempY;
				}
				repaint();
			}
		});
		//do not add the above JMenuItem here
		
		applyChanges = new JMenuItem("Apply Changes");
		applyChanges.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				//transform GridPoint coordinates to ReacTIVision coordinates
				//and store in grid
				for (int i = 0; i < gridPoints.length; ++i) {
					final Point defaultPosition = getDefaultGridPointPosition(i);
					grid[2*i]   = ((float)(gridPoints[i].x - defaultPosition.x)) / boxWidth;
					grid[2*i+1] = ((float)(gridPoints[i].y - defaultPosition.y)) / boxWidth;
				}
				if (!controlServer.setGrid(grid)) {
//					ReacTIVision.showErrorPopUp("Could not apply grid changes.");
				}
			}
		});
		//do not add the above JMenuItem here
	}
	
	/**
	 * Returns <code>true</code>, if the the four GridPoints surrounding the
	 * passed coordinates are all set. This method works correctly only on
	 * undistorted relative grid coordinates, i.e. it has to be used before interpolation.
	 * 
	 * @param x range: 0 to 8
	 * @param y range: 0 to 6
	 * @return see description
	 */
	private boolean boxIsSet(double x, double y) {
		if (x < 0 || x > 8 || y < 0 || y > 6)
			return false;
		
		int ceilX = (int)ceil(x);
		int ceilY = (int)ceil(y);
		
		if (!gridPoints[9 * (ceilY - 1) + ceilX - 1].isSet)
			return false;
		if (!gridPoints[9 * (ceilY - 1) + ceilX].isSet)
			return false;
		if (!gridPoints[9 * ceilY + ceilX - 1].isSet)
			return false;
		if (!gridPoints[9 * ceilY+ ceilX].isSet)
			return false;
		return true;
	}

	private double catmullRomSpline(double x,double v1,double v2,double v3,double v4)
	{
		double c1,c2,c3,c4;

		c1 =  1.0*v2;
		c2 = -0.5*v1 +  0.5*v3;
		c3 =  1.0*v1 + -2.5*v2 +  2.0*v3 + -0.5*v4;
		c4 = -0.5*v1 +  1.5*v2 + -1.5*v3 +  0.5*v4;

		return (((c4*x + c3)*x +c2)*x + c1);
	}
	
	private int getBorderWidth() {
		return manualCalibrationBorderWidth + autoCalibrationBorderWidth;
	}
	
	
	
	/**
	 * Returns the default absolute pixel coordinates of the specified GridPoint.
	 * @param gridPointID the position of a GridPoint in gridPoints, which corresponds
	 * to the points default order when traversing the grid from left to right and top
	 * to bottom.
	 * @return a <code>Point</code> with the requested coordinates
	 */
	private Point getDefaultGridPointPosition(int gridPointID) {
		int yBoxes = (62 - gridPointID) / 9;
		int xBoxes = gridPointID % 9;
		return new Point(boxWidth * xBoxes + getBorderWidth(),
				boxWidth * (6 - yBoxes) + getBorderWidth());
	}
	
	private FloatPoint getInterpolated(float x, float y)
	{
		FloatPoint point = new FloatPoint();
		
		x = 8 - x;
		y = 6 - y;
		
		if( x>=9 ) return getInterpolatedY(9,y);
		if( y>=7 ) return getInterpolatedX(x,7);

		// x
		int x_floor = (int)x;
		float x_offset = x - x_floor;

		FloatPoint x1 = getInterpolatedY(x_floor,y);
		FloatPoint x2 = getInterpolatedY(x_floor+1,y);
		point.x = (x1.x * (1-x_offset)) + (x2.x * x_offset);

		// y
		int y_floor = (int)y;
		float y_offset = y - y_floor;

		FloatPoint y1 = getInterpolatedX(x,y_floor);
		FloatPoint y2 = getInterpolatedX(x,y_floor+1);
		point.y = (y1.y * (1-y_offset)) + (y2.y * y_offset);
		
		return point;
	}
	
	private FloatPoint getInterpolatedX( float x, int y )
	{
		int x_floor = (int)x;
		float x_offset = x - x_floor;

		FloatPoint v1;
		try {
			if( x_floor<=0 ) v1 = new FloatPoint(gridPoints[ (y*9) + x_floor ]);
			else v1 = new FloatPoint(gridPoints[ (y*9) + x_floor-1 ]);
		} catch (IndexOutOfBoundsException e) {
			v1 = new FloatPoint(0,0);
		}
		FloatPoint v2;

		try {
			v2 = new FloatPoint(gridPoints[ (y*9) + x_floor ]);
		} catch (IndexOutOfBoundsException e) {
			v2 = new FloatPoint(0,0);
		}
		
		FloatPoint v3;
		try {
			v3 = new FloatPoint(gridPoints[ (y*9) + x_floor+1 ]);
		} catch (IndexOutOfBoundsException e) {
			v3 = new FloatPoint(0,0);
		}

		FloatPoint v4;
		try {
			if( x_floor>=7 ) v4 = new FloatPoint(gridPoints[ (y*9) + x_floor+1 ]);
			else v4 = new FloatPoint(gridPoints[ (y*9) + x_floor+2 ]);
		} catch (IndexOutOfBoundsException e) {
			v4 = new FloatPoint(0,0);
		}

		FloatPoint point = new FloatPoint(
				(float)catmullRomSpline( x_offset, v1.x, v2.x, v3.x, v4.x ),
				(float)catmullRomSpline( x_offset, v1.y, v2.y, v3.y, v4.y ));

		return point;
	}
	
	private FloatPoint getInterpolatedY( int x, float y )
	{
		int y_floor = (int)y;
		float y_offset = y - y_floor;

		FloatPoint v1;
		if( y_floor<=0 ) v1 = new FloatPoint(gridPoints[ (y_floor*9) + x ]);
		else v1 = new FloatPoint(gridPoints[ ((y_floor-1) * 9) + x ]);

		FloatPoint v2 = new FloatPoint(gridPoints[ (y_floor * 9) + x ]);
		
		FloatPoint v3;
		try {
			v3 = new FloatPoint(gridPoints[ ((y_floor+1) * 9) + x ]);
		} catch (IndexOutOfBoundsException e) {
			v3 = new FloatPoint(0,0);
		}

		FloatPoint v4;
		try {
			if( y_floor>=5 ) v4 = new FloatPoint(gridPoints[ ((y_floor+1) * 9) + x ]);
			else v4 = new FloatPoint(gridPoints[ ((y_floor+2) * 9) + x ]);
		} catch (IndexOutOfBoundsException e) {
			v4 = new FloatPoint(0,0);
		}

		FloatPoint point = new FloatPoint(
				(float)catmullRomSpline( y_offset, v1.x, v2.x, v3.x, v4.x ),
				(float)catmullRomSpline( y_offset, v1.y, v2.y, v3.y, v4.y ));

		return point;
	}
	
	//############### Listeners and subcomponents ###############
	
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(imageServer.getImage().getWidth(),
				imageServer.getImage().getHeight());
	}
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 1;
	}
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 1;
	}
	
	//############### implement Scrollable interface ################# 
	
	//################## calibration methods #################### 
	/**
	 * Initializes the grid's horizontal and vertical splines.
	 * Must not be called prior to <code>initGridPoints()</code>.
	 */
	private void initGridLines() {
		horizontalSplines = new CatmullRomSpline[7];
		for (int i = 0; i < 7; ++i) {
			horizontalSplines[i] = new CatmullRomSpline();
			for (int j = 0; j < 9; ++j)
				horizontalSplines[i].addControlPoint(gridPoints[9*i + j]);
		}
		verticalSplines = new CatmullRomSpline[9];
		for (int i = 0; i < 9; ++i) {
			verticalSplines[i] = new CatmullRomSpline();
			for (int j = 0; j < 7; ++j)
				verticalSplines[i].addControlPoint(gridPoints[i + 9*j]);
		}
	}

	/**
	 * Uses the information in <code>grid</code> to create corresponding GridPoints
	 * and store them in <code>gridPoints</code>. 
	 */
	private void initGridPoints() {
		for (int i = 0; i < gridPoints.length; ++i) {
			gridPoints[i] = new GridPoint(
					(int)(getDefaultGridPointPosition(i).x + grid[2*i] * boxWidth),
					(int)(getDefaultGridPointPosition(i).y + grid[2*i+1] * boxWidth));
			gridPoints[i].setID(i);
		}
	}
	
	private void translatePosition(FloatPoint p) {
		//translate position to relative coordinates
		p.x -= (manualCalibrationBorderWidth + autoCalibrationBorderWidth);
		p.x /= boxWidth;
		p.y -= (manualCalibrationBorderWidth + autoCalibrationBorderWidth);
		p.y /= boxWidth;
		
		//interpolate
		p.set(getInterpolated(p.x, p.y));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		//draw area borders
		g2.setStroke(dashed);
		g2.draw(new Rectangle2D.Double(0, 0,
				getPreferredSize().width - 1,
				getPreferredSize().height - 1));
			
		g2.draw(new Rectangle2D.Double(
				autoCalibrationBorderWidth,
				autoCalibrationBorderWidth,
				getPreferredSize().width -  2 * autoCalibrationBorderWidth - 1,
				getPreferredSize().height - 2 * autoCalibrationBorderWidth - 1));
		
		//draw camera image (if possible)
		g.drawImage(imageServer.getImage(), getBorderWidth(), getBorderWidth(), null);
		
		if (mode != CalibrationMode.DISABLED) {
			
			//draw extrapolated position of next point
			if (mode == CalibrationMode.QUICK_CALIBRATION && calibrationGridPointIndex > 1
					&& calibrationGridPointIndex < 63) {
				
				g2.setColor(Color.GREEN);
				g2.setStroke(fullStroke);
				
				final GridPoint[] leftPoints = leftPoints(calibrationGridPointIndex);
				if (leftPoints.length > 1) {
					
					FloatPoint f = FloatPointExtrapolator.extrapolate(
						leftPoints[leftPoints.length - 2],
						leftPoints[leftPoints.length - 1]);
				
					g2.drawLine((int)(f.x - 10), (int)f.y, (int)(f.x + 10), (int)f.y);
					g2.drawLine((int)f.x, (int)(f.y - 10), (int)f.x, (int)(f.y + 10));
				}//TODO
				
				g2.setColor(Color.RED);
				
				final GridPoint[] abovePoints = abovePoints(calibrationGridPointIndex);
				if (abovePoints.length > 1) {
					
					FloatPoint f = FloatPointExtrapolator.extrapolate(
						abovePoints[abovePoints.length - 2],
						abovePoints[abovePoints.length - 1]);
				
					g2.drawLine((int)(f.x - 10), (int)f.y, (int)(f.x + 10), (int)f.y);
					g2.drawLine((int)f.x, (int)(f.y - 10), (int)f.x, (int)(f.y + 10));
				}//TODO
			}
			
			//draw grid lines
			g2.setColor(Color.YELLOW);
			g2.setStroke(fullStroke);
			for (int i = 0; i < 7; ++i)
				horizontalSplines[i].draw(g2);
			for (int i = 0; i < 9; ++i)
				verticalSplines[i].draw(g2);
			
			//draw grid points
			for (int i = 0; i < gridPoints.length; ++i)
				if (gridPoints[i].isSet)
					gridPoints[i].draw(g2);
				else
					break;
			
			//*** draw ellipsoids ***
			
			final Point center = new Point(
					getBorderWidth() + 4 * boxWidth,
					getBorderWidth() + 3 * boxWidth); 
			
			int steps = 30;
			int[] x = new int[steps + 1];
			int[] y = new int[steps + 1];
			boolean[] boxIsSet = new boolean[steps + 1];
			
			final FloatPoint p = new FloatPoint();
			
			//inner circle
			for (int i = 0; i <= steps; ++i) {
				final double angle = i*2*PI/steps;
				
				p.x = (float)(center.x - boxWidth * cos(angle));
				p.y = (float)(center.y + boxWidth * sin(angle));
				
				translatePosition(p);
				
				x[i] = (int)p.x;
				y[i] = (int)p.y;
				
				boxIsSet[i] = boxIsSet(4 + cos(angle) * 0.99, 3 - sin(angle) * 0.99);
			}
			for (int i = 0; i < steps; ++i)
				if (boxIsSet[i] && boxIsSet[i+1])
					g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
			
			
			//middle circle
			steps = 60;
			x = new int[steps + 1];
			y = new int[steps + 1];
			boxIsSet = new boolean[steps + 1];
			
			for (int i = 0; i <= steps; ++i) {
				final double angle = i*2*PI/steps;
				
				p.x = (float)(center.x - 2 * boxWidth * cos(angle));
				p.y = (float)(center.y + 2 * boxWidth * sin(angle));
				
				translatePosition(p);
				
				x[i] = (int)p.x;
				y[i] = (int)p.y;
				
				boxIsSet[i] = boxIsSet(4 + cos(angle) * 1.99, 3 - sin(angle) * 1.99);
			}
			for (int i = 0; i < steps; ++i)
				if (boxIsSet[i] && boxIsSet[i+1])
					g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
			
			//outer circle
			steps = 120;
			x = new int[steps + 1];
			y = new int[steps + 1];
			boxIsSet = new boolean[steps + 1];
			
			for (int i = 0; i <= steps; ++i) {
				final double angle = i*2*PI/steps;
				
				p.x = (float)(center.x - 3 * boxWidth * cos(angle));
				p.y = (float)(center.y + 3 * boxWidth * sin(angle));
				
				translatePosition(p);
				
				x[i] = (int)p.x;
				y[i] = (int)p.y;
				boxIsSet[i] = boxIsSet(4 + cos(angle) * 2.99, 3 - sin(angle) * 2.99);
			}
			for (int i = 0; i < steps; ++i)
				if (boxIsSet[i] && boxIsSet[i+1])
					g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
			
			//ellipse
			steps = 150;
			x = new int[steps + 1];
			y = new int[steps + 1];
			boxIsSet = new boolean[steps + 1];
			
			for (int i = 0; i <= steps; ++i) {
				final double angle = i*2*PI/steps;
				
				p.x = (float)(center.x - 4 * boxWidth * cos(angle));
				p.y = (float)(center.y + 3 * boxWidth * sin(angle));
				
				translatePosition(p);
				
				x[i] = (int)p.x;
				y[i] = (int)p.y;
				
				boxIsSet[i] = boxIsSet(4 + cos(angle) * 3.99, 3 - sin(angle) * 2.99);
			}
			for (int i = 0; i < steps; ++i)
				if (boxIsSet[i] && boxIsSet[i+1])
					g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
		}
	}
	
	/**
	 * Returns an array containing all GridPoints which are part of the same horizontal
	 * spline and which are located to the left of the point identified by its location
	 * in {@link #gridPoints}.
	 * <p>
	 * If no such GridPoints exist, an array of lenth zero is returned.
	 */
	private GridPoint[] leftPoints(int gridPointIndex) {
		final GridPoint[] points = new GridPoint[gridPointIndex % 9];
		for (int i = 0; i < points.length; ++i)
			points[i] = gridPoints[gridPointIndex - points.length + i];
		return points;
	}
	
	/**
	 * This method works similar to {@link #leftPoints(int)}, just the GridPoints
	 * above the spedified GridPoint in the same vertical spline are returned.
	 */
	private GridPoint[] abovePoints(int gridPointIndex) {
		final GridPoint[] points = new GridPoint[gridPointIndex / 9];
		for (int i = 0; i < points.length; ++i)
			points[i] = gridPoints[(gridPointIndex % 9) + i * 9];
		return points;
	}
	
	/**
	 * Checks whether a GridPoint at location p would be too close to any existing
	 * GridPoint.
	 * @param p the point to check
	 * @return <code>true</code>, if the distance to all GridPoints in the
	 * CalibrationArea is large enough, else <code>false</code>
	 */
	private boolean pointCollision(Point p) {
		final int minDistance = 8;
		for (int i = 0; i <= gridPoints.length; ++i) {
			if (!gridPoints[i].isSet)
				return false;
			if (gridPoints[i].distanceFrom(p.x, p.y) < minDistance)
				return true;
		}
		return false;
	}
}
