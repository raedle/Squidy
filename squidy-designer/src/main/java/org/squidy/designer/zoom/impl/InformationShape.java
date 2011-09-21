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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.component.CropScroll;
import org.squidy.designer.components.pdf.PDFPane;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ImageShape;
import org.xhtmlrenderer.simple.XHTMLPanel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomInformation</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 2:50:43 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: InformationShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class InformationShape extends ImageShape {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -1338857118835318578L;

	// Logger to info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(InformationShape.class);

	private CropScroll cropScroll;

	private URL url;
	private String name;

	private String informationSource;

	/**
	 * @return the informationSource
	 */
	public final String getInformationSource() {
		return informationSource;
	}

	/**
	 * @param informationSource
	 *            the informationSource to set
	 */
	public final void setInformationSource(String informationSource) {
		this.informationSource = informationSource;
	}

	public InformationShape(String name, String information) {
		super("Information", InformationShape.class
				.getResource("/images/information.png"));

		this.name = name;
		this.informationSource = information;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#zoomBegan()
	 */
	@Override
	protected void zoomBegan() {
		super.zoomBegan();

		if (cropScroll == null) {
			initPane();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		ShapeUtils.setApparent(cropScroll, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		ShapeUtils.setApparent(cropScroll, false);

		if (cropScroll != null) {
			cropScroll.removeFromParent();
			cropScroll = null;
		}
		super.layoutSemanticsZoomedOut();
	}

	private static Font fontName = internalFont.deriveFont(50f);
	private static Font fontSource = internalFont.deriveFont(30f);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomShape#paintShapeZoomedIn(edu.umd.
	 * cs.piccolo.util.PPaintContext) TODO [RR]: Improve speed of rendering.
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);

		if (informationSource != null) {
			Graphics2D g = paintContext.getGraphics();

			PBounds bounds = getBoundsReference();
			double x = bounds.getX();
			double width = bounds.getWidth();

			String source = "Source: " + informationSource;

			g.setFont(fontName);
			g.drawString(name, (int) (x + 50), 230);

			g.setFont(fontSource);
			source = FontUtils.createCroppedLabelIfNecessary(
					g.getFontMetrics(), source, (int) width);
			g.drawString(source, (int) (x + width - FontUtils.getWidthOfText(g
					.getFontMetrics(), source)) - 20, 130);
		}
	}

	private void initPane() {
		// Add Zoomable Component
		new Thread(new Runnable() {
			public void run() {

				// ProgressIndicator indicator = new
				// ProgressIndicator(InformationShape.this);

				if (informationSource == null || "".equals(informationSource)) {
					return;
				}

				try {
					if (informationSource.endsWith(".pdf")) {
						url = InformationShape.class
								.getResource(informationSource);
					} else if (informationSource.endsWith(".html")) {
						try {
							url = new URL(informationSource);
						} catch (Exception e) {
							url = InformationShape.class
									.getResource(informationSource);
						}
					} else {
						url = new URL(informationSource);
					}
				} catch (Exception e) {
					// do nothing
				}

				PNode cropNode;
				// PDF
				if (informationSource.endsWith(".pdf")) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Display information as PDF.");
					}
					cropNode = new PDFPane(url.getFile());
				}
				// HTML
				else {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Display information as HTML.");
					}

					if (informationSource.startsWith("http") || informationSource.endsWith(".html")) {

						XHTMLPanel xhtmlPanel = new XHTMLPanel();
						try {
							xhtmlPanel.setDocument(url.toURI().toString());
						} catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						xhtmlPanel.setPreferredSize(new Dimension(800, 800));
//						xhtmlPanel.addPropertyChangeListener("preferredSize", new PropertyChangeListener() {
//							
//							public void propertyChange(PropertyChangeEvent evt) {
//								cropScroll.updateScroller();
//								cropNode.
//							}
//						});
						cropNode = JComponentWrapper.create(xhtmlPanel);
					} else {
						JEditorPane editorPane = new JEditorPane();
						editorPane.setFont(internalFont.deriveFont(10f));

						FontMetrics fm = editorPane.getFontMetrics(editorPane
								.getFont());
						int editorWidth = 400;
						editorPane.setPreferredSize(new Dimension(editorWidth,
								FontUtils.getLineCount(informationSource,
										editorWidth)
										* fm.getHeight()));

						cropNode = JComponentWrapper.create(editorPane);
						editorPane.setEditable(false);

						editorPane.setText(informationSource);
					}

					// Prepare HTML Kit
					// HTMLParser editorKit = new HTMLParser();
					// HTMLParserCallback callback = new
					// HTMLParserCallback();
					// getComponentEditorPane().setEditorKit(editorKit);
					// //Open connection
					// InputStreamReader reader = new
					// InputStreamReader(url.openStream());
					// //Start parse process
					// editorKit.getParser().parse(reader, callback, true);
					// Wait until parsing process has finished
					// try {
					// Thread.sleep(2000);
					// }
					// catch (InterruptedException e) {
					// if (LOG.isErrorEnabled()) {
					// LOG.error("Error in " +
					// InformationShape.class.getName() + ".", e);
					// }
					// }
				}

				cropScroll = new CropScroll(cropNode, new Dimension(1000, 700),
						0.2);
				cropScroll.setOffset(getBoundsReference().getCenterX()
						- cropScroll.getBoundsReference().getCenterX(), 250);
				addChild(cropScroll);
				invalidateFullBounds();
				invalidateLayout();
				invalidatePaint();

				// indicator.done();
			}
		}).start();
	}
}
