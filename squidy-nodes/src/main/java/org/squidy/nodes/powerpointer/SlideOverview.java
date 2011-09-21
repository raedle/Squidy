/**
 * 
 */
package org.squidy.nodes.powerpointer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.StrokeUtils;


/**
 * <code>DisplayDomainProvider</code>.
 *
 * <pre>
 * Date: May 07, 2010
 * Time: 3:42:50 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: SlideOverview.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
public class SlideOverview extends Window {

	public static void main(String[] args) {
		SlideOverview slideOverview = new SlideOverview();
		slideOverview.setVisible(true);
		slideOverview.setSize(new Dimension(800, 600));
		slideOverview.setMaximumSize(new Dimension(800, 600));
		slideOverview.setPreferredSize(new Dimension(800, 600));
//		slideOverview.pack();
	}
	
	/**
	 * Default generated serial version UID.
	 */
	private static final long serialVersionUID = -965450405880371747L;
	
	private final EventListenerList listeners = new EventListenerList();
	
	private File ppt;
	
	public SlideOverview() {
		this(new File("/Users/raedle/Desktop/Test3/SquidyWorkshop.ppt"));
	}
	
	public SlideOverview(final File ppt) {
		super(null);
//		super(800, 400);
		
		this.ppt = ppt;
		
		setSize(new Dimension(1000, 600));
		setPreferredSize(new Dimension(1000, 600));
		setMaximumSize(new Dimension(1000, 600));
		setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		setAlwaysOnTop(true);
		
		augmentWindowWithComponents();
		
		setLocation(50, 50);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.component.TransparentWindow#augmentWindowWithComponents()
	 */
	protected void augmentWindowWithComponents() {
		SwingUtilities.invokeLater(new Runnable() {

			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					FileInputStream inputStream = new FileInputStream(ppt);
					SlideShow ppt = new SlideShow(inputStream);
					inputStream.close();
					
					Dimension pageSize = ppt.getPageSize();
					
					for (Slide slide : ppt.getSlides()) {
					    System.out.println("process slide");
						SlideComponent slideComponent = new SlideComponent(slide, pageSize.width, pageSize.height);
						add(slideComponent);
						System.out.println("slide processed");
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				pack();
			}
		});
	}
	
	public void addSlideSelectionListener(SlideSelectionListener listener) {
		listeners.add(SlideSelectionListener.class, listener);
	}
	
	protected class SlideComponent extends JComponent {

		/**
		 * Default generated serial version UID.
		 */
		private static final long serialVersionUID = 1175616566716704778L;
		
		private Slide slide;
		
		private BufferedImage slideImage;
		private boolean slideDrawn = false;
		
		private static final float SCALE_FACTOR = 0.2f;
		
		private JLabel loadingLabel;
		
		private String slideLabel;
		
		private boolean hovered = false;
		
		public SlideComponent(final Slide slide, final int width, final int height) {
			this.slide = slide;
			
			slideLabel = "Slide " + slide.getSlideNumber();
			
			setLayout(new BorderLayout());
			setSize(new Dimension((int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR)));
			setPreferredSize(new Dimension((int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR)));
//			setBounds(0, 0, (int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR));
			
			try {
				loadingLabel = new JLabel(new ImageIcon(ImageUtils.loadImageFromClasspath("/org/squidy/nodes/image/loader.gif")));
				add(loadingLabel, BorderLayout.CENTER);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			Thread createImage = new Thread() {
//
//				/* (non-Javadoc)
//				 * @see java.lang.Runnable#run()
//				 */
//				public void run() {
					slideImage = new BufferedImage((int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR), BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = slideImage.createGraphics();
					
					AffineTransform transform = AffineTransform.getScaleInstance(SCALE_FACTOR, SCALE_FACTOR);
					
					graphics.setTransform(transform);
					
					//clear the drawing area
					graphics.setPaint(Color.WHITE);
					graphics.fill(new Rectangle2D.Float(0, 0, (int) (width * SCALE_FACTOR), (int) (height * SCALE_FACTOR)));

					//render
					try {
						slide.draw(graphics);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println( Runtime.getRuntime().freeMemory() + " free out of " + Runtime.getRuntime().totalMemory() );
					}
					
					graphics.dispose();
					
					slideDrawn = true;
					
					remove(loadingLabel);
					repaint();
//				}
//			};
//			createImage.setPriority(Thread.MIN_PRIORITY);
//			createImage.start();
			
			addMouseListener(new MouseAdapter() {

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					
					hovered = true;
					repaint();
				}

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					
					hovered = false;
					repaint();
				}

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					
//					for (SlideSelectionListener listener : listeners.getListeners(SlideSelectionListener.class)) {
//						listener.slideSelected(slide.getSlideNumber());
//					}
				}

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
				 */
				@Override
				public void mousePressed(MouseEvent e) {
					super.mousePressed(e);
					
					for (SlideSelectionListener listener : listeners.getListeners(SlideSelectionListener.class)) {
						listener.slideSelected(slide.getSlideNumber());
					}
				}
			});
		}
		
		/* (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override
		protected void paintComponent(Graphics g) {
			
			Graphics2D g2d = (Graphics2D) g;
			
			if (slideDrawn && slideImage != null) {
				g.drawImage(slideImage, 0, 0, null);
			}
			else {
				g2d.setColor(Color.WHITE);
				g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.draw(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
			}
			
			if (hovered) {
				Stroke defaultStroke = g2d.getStroke();
				g2d.setStroke(StrokeUtils.getBasicStroke(3.0f));
				g2d.setColor(Color.BLUE);
				g2d.draw(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
				g2d.setStroke(defaultStroke);
			}
			
			System.out.println("PAINT WIDTH: " + getX() + ", "+  getY() + ":" + slide.getSlideNumber());
			
			g.setFont(g.getFont().deriveFont(Font.BOLD));
			int slideLabelWidth = FontUtils.getWidthOfText(g.getFontMetrics(), slideLabel);
			g.drawString(slideLabel, getWidth() / 2 - slideLabelWidth / 2, getHeight() - 10);

			super.paintComponent(g);
		}
	}
}
