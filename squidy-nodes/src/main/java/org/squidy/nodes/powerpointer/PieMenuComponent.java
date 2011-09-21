/**
 * 
 */
package org.squidy.nodes.powerpointer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

/**
 * @author raedle
 *
 */
public class PieMenuComponent extends JComponent {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 7453217419590215948L;

	private final EventListenerList listeners = new EventListenerList();
	
	private static final AlphaComposite COMPOSITE_ALPHA = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f);
	private static final AlphaComposite COMPOSITE_ALPHA_PIECES = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f);
	private AffineTransform translateTransform = AffineTransform.getTranslateInstance(0, 0);
	private Paint knopPaint;
	private Paint piePaint;
	private Stroke border = new BasicStroke(2f);
	private Stroke piecesBorder1 = new BasicStroke(1f);
	private Stroke piecesBorder2 = new BasicStroke(3f);
	private Stroke piecesBorder3 = new BasicStroke(5f);
	
	private Ellipse2D pieMenuInner;
	private Ellipse2D pieMenuOuter;
	
	private int diameter;
	
	private boolean openPieMenu = false;
	private double openRatio = 0.0;
	private Thread pieMenuOpener;
	
	public PieMenuComponent(final int diameter) {
		setBounds(0, 0, diameter, diameter);
		
		this.diameter = diameter;
		
		int oneThird = diameter / 3;
		
		knopPaint = new GradientPaint(oneThird + oneThird / 10, oneThird + oneThird / 10, Color.WHITE, oneThird * 2, oneThird * 2, Color.DARK_GRAY);
		piePaint = new GradientPaint(oneThird / 5, oneThird / 5, Color.LIGHT_GRAY, diameter, diameter, Color.GRAY);
		
		pieMenuInner = new Ellipse2D.Double(oneThird, oneThird, oneThird, oneThird);
		pieMenuOuter = new Ellipse2D.Double(0, 0, diameter, diameter);
		
		pieMenuOpener = new Thread() {

			@Override
			public void run() {
				super.run();
				
				double sizeHalf = (double) diameter / (double) 2;
				double trans = 0;
				for (openRatio = 0.0; openRatio <= 1.0; openRatio += 0.05) {
					trans = sizeHalf - (sizeHalf * openRatio);
					translateTransform.setTransform(openRatio, 0, 0, openRatio, trans, trans);
					
					try {
						SwingUtilities.invokeAndWait(new Runnable() {

							/* (non-Javadoc)
							 * @see java.lang.Runnable#run()
							 */
							public void run() {
								repaint();
							}
						});
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
//				BufferedImage img = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_RGB);
//				Graphics2D g2d = img.createGraphics();
//				g2d.setColor(Color.WHITE);
//				g2d.fillRect(0, 0, diameter, diameter);
//				paintAll(g2d);
//				g2d.dispose();
//				try {
//                    ImageIO.write(img, "png", new File("/Users/raedle/Desktop/c5_pie-menu.png"));
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
			}
		};
		
		addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseMoved(MouseEvent e) {
				
				if (openRatio < 0.5) {
					return;
				}
				
				if (!contains(e.getPoint())) {
					Container parent = getParent();
					while (!(parent instanceof JWindow) && parent != null) {
						parent = parent.getParent();
					}
					
					if (parent instanceof JWindow) {
						((JWindow) parent).setVisible(false);
						((JWindow) parent).dispose();
					}
					
					Point p = e.getPoint();
					
					int x = getX();
					int y = getY();
					
					int width = getWidth();
					int height = getHeight();
					
					Point[] edges = new Point[4];
					edges[EdgeListener.EDGE_TOP] = new Point(x + width / 2, y);
					edges[EdgeListener.EDGE_RIGHT] = new Point(x + width, y + height / 2);
					edges[EdgeListener.EDGE_BOTTOM] = new Point(x + width / 2, y + height);
					edges[EdgeListener.EDGE_LEFT] = new Point(x, y + height / 2);
					
					// Calculate distance from exit point to edge points and the minimal distance
					// indicates the leave point.
					double edgeDistances[] = new double[4];
					edgeDistances[EdgeListener.EDGE_TOP] = edges[EdgeListener.EDGE_TOP].distance(p);
					edgeDistances[EdgeListener.EDGE_RIGHT] = edges[EdgeListener.EDGE_RIGHT].distance(p);
					edgeDistances[EdgeListener.EDGE_BOTTOM] = edges[EdgeListener.EDGE_BOTTOM].distance(p);
					edgeDistances[EdgeListener.EDGE_LEFT] = edges[EdgeListener.EDGE_LEFT].distance(p);
					
					int edge = 0;
					for (int i = 0; i < 4; i++) {
						if (edgeDistances[edge] > edgeDistances[i]) {
							edge = i;
						}
					}
					
					EdgeListener[] edgeListeners = listeners.getListeners(EdgeListener.class);
					for (EdgeListener listener : edgeListeners) {
						listener.exitOnEdge(edge);
					}
				}
			}
			
			public void mouseDragged(MouseEvent e) {
				// empty
			}
		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				
//				System.out.println("Point: " + e.getPoint());
				
				Container parent = getParent();
				while (!(parent instanceof JWindow) && parent != null) {
					parent = parent.getParent();
				}
				
				if (parent instanceof JWindow) {
					((JWindow) parent).setVisible(false);
					((JWindow) parent).dispose();
				}
				
				Point p = e.getPoint();
				
				int x = getX();
				int y = getY();
				
				int width = getWidth();
				int height = getHeight();
				
				Point[] edges = new Point[4];
				edges[EdgeListener.EDGE_TOP] = new Point(x + width / 2, y);
				edges[EdgeListener.EDGE_RIGHT] = new Point(x + width, y + height / 2);
				edges[EdgeListener.EDGE_BOTTOM] = new Point(x + width / 2, y + height);
				edges[EdgeListener.EDGE_LEFT] = new Point(x, y + height / 2);
				
				// Calculate distance from exit point to edge points and the minimal distance
				// indicates the leave point.
				double edgeDistances[] = new double[4];
				edgeDistances[EdgeListener.EDGE_TOP] = edges[EdgeListener.EDGE_TOP].distance(p);
				edgeDistances[EdgeListener.EDGE_RIGHT] = edges[EdgeListener.EDGE_RIGHT].distance(p);
				edgeDistances[EdgeListener.EDGE_BOTTOM] = edges[EdgeListener.EDGE_BOTTOM].distance(p);
				edgeDistances[EdgeListener.EDGE_LEFT] = edges[EdgeListener.EDGE_LEFT].distance(p);
				
				int edge = 0;
				for (int i = 0; i < 4; i++) {
					if (edgeDistances[edge] > edgeDistances[i]) {
						edge = i;
					}
				}
				
				EdgeListener[] edgeListeners = listeners.getListeners(EdgeListener.class);
				for (EdgeListener listener : edgeListeners) {
					listener.exitOnEdge(edge);
				}
			}
		});
	}
	
	public void addEdgeListener(EdgeListener edgeListener) {
		listeners.add(EdgeListener.class, edgeListener);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (!openPieMenu) {
			openPieMenu = true;
			pieMenuOpener.start();
			return;
		}
		
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform saveTransform = g2d.getTransform();
		g2d.setTransform(translateTransform);
		
		Composite originalComposite = g2d.getComposite();
		
//		g.setColor(Color.LIGHT_GRAY);
//		g2d.draw(pieMenu);
//		g2d.setComposite(COMPOSITE_ALPHA);
//		g.setColor(Color.LIGHT_GRAY);
//		g2d.fill(pieMenu);
		
		Shape defaultClip = g2d.getClip();
//		g2d.clip(pieMenuInner);
		g2d.setComposite(COMPOSITE_ALPHA);
		g.setColor(Color.LIGHT_GRAY);
		Paint defaultPaint = g2d.getPaint();
		g2d.setPaint(piePaint);
		g2d.fill(pieMenuOuter);
		
		Stroke defaultStroke = g2d.getStroke();
		g.setColor(Color.GRAY);
		g2d.setStroke(border);
		g2d.draw(pieMenuOuter);
		
		g2d.setComposite(COMPOSITE_ALPHA_PIECES);
		
		g2d.clip(pieMenuOuter);
		g.setColor(Color.GRAY);
		g2d.setStroke(piecesBorder3);
		g2d.drawLine(0, 0, diameter, diameter);
		g2d.drawLine(diameter, 0, 0, diameter);

		g.setColor(Color.GRAY);
		g2d.setStroke(piecesBorder2);
		g2d.drawLine(0, 0, diameter, diameter);
		g2d.drawLine(diameter, 0, 0, diameter);

		g.setColor(Color.GRAY);
		g2d.setStroke(piecesBorder1);
		g2d.drawLine(0, 0, diameter, diameter);
		g2d.drawLine(diameter, 0, 0, diameter);
		
		g2d.setComposite(originalComposite);
		
		g2d.setColor(Color.GRAY);
//		g2d.setXORMode(Color.MAGENTA);
		
		g2d.setPaint(knopPaint);
		g2d.fill(pieMenuInner);
		g2d.setPaint(defaultPaint);
//		g2d.setColor(Color.GRAY);
//		g2d.draw(pieMenuInner);
		
		g2d.setClip(defaultClip);
		
		g2d.setStroke(defaultStroke);
		g2d.setColor(Color.BLACK);
		g2d.setFont(g2d.getFont().deriveFont((float) diameter / (float) 14).deriveFont(Font.BOLD));
			
		int width1 = g2d.getFontMetrics().stringWidth("Slide Overview");
		g.drawString("Slide Overview", diameter / 2 - width1 / 2, 115);
			
//		int width2 = g2d.getFontMetrics().stringWidth("Erase Drawings");
//		g.drawString("Erase Drawings", SIZE / 2 - width2 / 2, SIZE - 80);
			
		int width3 = g2d.getFontMetrics().stringWidth("Erase Drawings");
		g.drawString("Erase Drawings", diameter / 2 - width3 / 2, diameter - 80);
			
//		int width4 = g2d.getFontMetrics().stringWidth("Erase Drawings");
//		g.drawString("Erase Drawings", SIZE / 2 - width4 / 2, SIZE - 80);
		
		g2d.setTransform(saveTransform);
	}
}
