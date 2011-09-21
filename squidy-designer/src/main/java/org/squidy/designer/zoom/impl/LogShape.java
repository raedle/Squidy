/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

package org.squidy.designer.zoom.impl;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JTextArea;

import org.squidy.designer.component.CropScroll;
import org.squidy.designer.component.button.ImageButton;
import org.squidy.designer.component.button.VisualButton;
import org.squidy.designer.event.ZoomActionEvent;
import org.squidy.designer.event.ZoomActionListener;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ImageShape;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomLog</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 2:50:34 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: LogShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class LogShape extends ImageShape {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -19131656327220868L;
	
	private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();
	
	private JTextArea textArea;
	private JComponentWrapper log;
	
	private CropScroll logScroll;

	/**
	 * 
	 */
	public LogShape() {
		super("Log", LogShape.class.getResource("/images/scroll_information.png"));
		
		textArea = new JTextArea();
		textArea.setColumns(80);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		log = new JComponentWrapper(textArea);
		
		logScroll = new CropScroll(log, new Dimension(1000, 780), 0.2);
		addChild(logScroll);
		logScroll.setOffset(getBoundsReference().getCenterX() - logScroll.getBoundsReference().getCenterX(), 160);
		
		// Add clear log action to action bar.
		ImageButton clearLogAction = new ImageButton(ImageButton.class.getResource("/images/24x24/scroll_delete.png"), "Clear");
		clearLogAction.addZoomActionListener(new ZoomActionListener() {
			
			/* (non-Javadoc)
			 * @see org.squidy.designer.event.ZoomActionListener#actionPerformed(org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				textArea.setText("");
			}
		});
		addAction(clearLogAction);
		ShapeUtils.setApparent(clearLogAction, false);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();
		
		ShapeUtils.setApparent(logScroll, true);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		ShapeUtils.setApparent(logScroll, false);
		
		super.layoutSemanticsZoomedOut();
	}
	
	private static Font fontLog = internalFont.deriveFont(50f);

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.NavigationShape#paintShapeZoomedIn(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);
		
		Graphics2D g = paintContext.getGraphics();
		
		PBounds bounds = getBoundsReference();
		int x = (int) bounds.getX();
		
		g.setFont(fontLog);
		g.drawString("Log", (int) (x + 50), 140);
	}
	
	/**
	 * @param info
	 */
	public void logInfo(String info) {
		log(info);
	}
	
	/**
	 * @param e
	 */
	public void logError(Throwable e) {
		StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		
		log(sw.toString());
	}
	
	/**
	 * @param s
	 */
	private void log(String s) {
		StringBuilder sb = new StringBuilder();
		
		if (textArea.getText().length() > 0) {
			sb.append(textArea.getText());
			sb.append(System.getProperty("line.separator"));
		}
		
		sb.append(DATE_FORMAT.format(new Date())).append(": ");
//		sb.append(System.getProperty("line.separator"));
		sb.append(s);
		
		textArea.setText(sb.toString());
	}
	
	public void clearLog() {
		textArea.setText("");
	}
}
