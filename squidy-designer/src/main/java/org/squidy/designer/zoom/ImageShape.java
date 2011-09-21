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

import java.net.URL;

import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.manager.ProcessException;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>ImageShape</code>.
 * 
 * <pre>
 * Date: Jul 15, 2009
 * Time: 9:24:49 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ImageShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ImageShape extends ActionShape<VisualShape<?>, Processable> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7556550314240418265L;

	private PImage image;
	
	/**
	 * @param title
	 * @param imageUrl
	 */
	public ImageShape(String title, URL imageUrl) {
		setTitle(title);
		
		image = new PImage(imageUrl);
		image.setScale(10);
		image.setPickable(false);
		image.setChildrenPickable(false);
		addChild(image);
		
		PBounds bounds = getBoundsReference();
		PBounds imageBounds = image.getBoundsReference();
		image.offset(bounds.getCenterX() - (imageBounds.getCenterX() * image.getScale()), 100);
	}
	
	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		// ignore
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		// ignore
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		// ignore
	}

	// #############################################################################
	// END ILaunchable
	// #############################################################################

	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();
		
		ShapeUtils.setApparent(image, false);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		ShapeUtils.setApparent(image, true);
		
		super.layoutSemanticsZoomedOut();
	}
}
