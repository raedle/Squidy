/**
 * 
 */
package org.squidy.designer.component;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.omg.CORBA.Bounds;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.StrokeUtils;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>TemporaryNotification</code>.
 * 
 * <pre>
 * Date: Apr 28, 2010
 * Time: 11:43:50 AM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz
 *         .de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id$
 * @since 1.5.0
 */
public class TemporaryNotification extends PNode {

	/**
	 * Default generated serial version UID.
	 */
	private static final long serialVersionUID = -5804477899288137209L;

	private Composite alphaComposite = AlphaComposite.getInstance(
			AlphaComposite.SRC_OVER, 1.0f);

	private String message;

	private Shape shape;

	public TemporaryNotification(String message) {
		this.message = message;
		
		setBounds(10, 10, 260, 80);

		final PActivity flash = new PActivity(5000, 50) {

			protected void activityStep(long elapsedTime) {
				super.activityStep(elapsedTime);

				float step = (int) elapsedTime / 50;

				// System.out.println("STEP: " + step);

				float value = Math.abs((float) (step / 100) - 1.0f);
				// value = Math.min(step, 0.0f);
				// value = Math.max(step, 1.0f);

				// System.out.println("VALUE: " + value);

				alphaComposite = AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, value);

				repaintFrom(getBoundsReference(), TemporaryNotification.this);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see edu.umd.cs.piccolo.activities.PActivity#activityFinished()
			 */
			@Override
			protected void activityFinished() {
				super.activityFinished();

				removeFromParent();
			}
		};

		addPropertyChangeListener(PNode.PROPERTY_PARENT,
				new PropertyChangeListener() {

					public void propertyChange(PropertyChangeEvent evt) {
						addActivity(flash);
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paint(PPaintContext paintContext) {
		super.paint(paintContext);

		if (shape == null) {
			PBounds bounds = getBoundsReference();
			shape = new RoundRectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 15, 15);
		}
		
		Graphics2D g = paintContext.getGraphics();

		g.setComposite(alphaComposite);
		
		g.setStroke(StrokeUtils.getBasicStroke(3f));
		
		g.setPaint(Color.LIGHT_GRAY);
		g.fill(shape);

		g.setPaint(Color.GRAY);
		g.draw(shape);

		g.setPaint(Color.BLACK);
		g.setFont(g.getFont().deriveFont(12f));
		
		
		
		//draw message
		//final int maxWidth = (int)getBounds().getWidth() - 40;//does not work, since bounds change
		final int maxWidth = 220;
		final FontMetrics fm = g.getFontMetrics();
		if (fm.stringWidth(message) > maxWidth) {//message to long for one line
			//try to split message into several lines and draw separately
			// split at new line characters, first ...
			String[] split = message.split("\n");
			//draw '\n'-separated sections and split, if necessary
			int lineNumber = 0;
			for (String s : split)
				if (fm.stringWidth(s) <= maxWidth)//section fits into one line
					g.drawString(message, 20, 30 + 12 * lineNumber++);
				else {
					//split section into single words
					String[] words = s.split(" ");
					String textLine = "";
					for (int i = 0; i < words.length; ++i) {
						if (fm.stringWidth(textLine) > maxWidth) {
							//have to print, despite words being too long
							g.drawString(textLine, 20, 30 + 12 * lineNumber++);
							textLine = "";
						} else { //width of text line <= maxWidth
							//check if width of (text line + current word) < maxWidth
							final String nTextLine = textLine + " " + words[i];
							if (fm.stringWidth(nTextLine) <= maxWidth)
								textLine = nTextLine;//add word to text line
							else {
								//draw current text line ...
								g.drawString(textLine, 20, 30 + 12 * lineNumber++);
								//... and start a new line with the current word
								textLine = words[i];
							}
						}
					}
					//draw last text line
					g.drawString(textLine, 20, 30 + 12 * lineNumber++);
				}
		} else
			g.drawString(message, 20, 30);//message fits into one line
		
	}
}
