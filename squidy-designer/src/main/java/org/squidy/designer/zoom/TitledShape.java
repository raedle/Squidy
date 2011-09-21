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

package org.squidy.designer.zoom;

import java.awt.Graphics2D;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.DrawableString;
import org.squidy.designer.util.DrawableString.AlignmentV;

import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>TitledZoomShape</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 4:41:42 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: TitledShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class TitledShape<T extends VisualShape<?>> extends ZoomShape<T> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 272335530878460462L;

	// Logger to log info, error, debug,... messages
	private static final Log LOG = LogFactory.getLog(TitledShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public TitledShape() {
		super();
		title2.setFont(internalFont.deriveFont(150f));
		title2.setAlignmentV(AlignmentV.TOP);
		title2.setOffsetY((int)getHeight() - 100);
		title2.setBounds(getBoundsReference().getBounds());
	}

	/**
	 * The title shown at zoomed out state. {@link ZoomState#ZOOM_OUT}
	 * {@link ZoomShape#currentZoomState}
	 * 
	 * @link this
	 *       {@link #paintShapeZoomedOut(edu.umd.cs.piccolo.util.PPaintContext)}
	 */
	@XmlAttribute(name = "title")
	private String title;

	// TODO: remove old title, rename to title, serialize
	private DrawableString title2 = new DrawableString();
	
	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
		//return title2.getFullString();
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
		this.title2.set(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomShape#afterUnmarshal(javax.xml.bind
	 * .Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
		
		// Sets title of shape after unmarshalling
		title2.set(title);
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	/**
	 * 
	 */
	protected void invalidateTitle() {
		title2.invalidate();
	}
	
	private boolean showTitle = true;

	/**
	 * @return the showTitle
	 */
	public final boolean isShowTitle() {
		return showTitle;
	}

	/**
	 * @param showTitle the showTitle to set
	 */
	public final void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	/**
	 * @param title
	 */
	public TitledShape(String title) {
		this();
		this.title = title;
		this.title2.set(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomShape#paintShapeZoomedOut(edu.umd
	 * .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedOut(PPaintContext paintContext) {
		super.paintShapeZoomedOut(paintContext);

		if (showTitle && title2.get() != null && paintContext.getCamera() != null) {
			title2.draw(paintContext.getGraphics(), paintContext.getCamera().getViewScale());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TitledShape [title=" + title + "]";
	}
	
	// #############################################################################
	// END INTERAL
	// #############################################################################
}
