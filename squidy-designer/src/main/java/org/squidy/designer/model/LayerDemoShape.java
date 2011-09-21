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

package org.squidy.designer.model;

import javax.xml.bind.annotation.XmlType;

import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.NavigationShape;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>LayerDemoShape</code>.
 * 
 * <pre>
 * Date: May 11, 2009
 * Time: 9:21:16 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: LayerDemoShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "LayerDemoShape")
public class LayerDemoShape extends NavigationShape<ZoomShape<?>> implements Draggable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 7809717133012425921L;

	private PImage questionAndAnswer;
	private PImage layerDemoPicture;
	
	public LayerDemoShape() {
		setTitle("Squidy Layer Architecture");
		
		PBounds bounds = getBoundsReference();
		
		questionAndAnswer = new PImage(LayerDemoShape.class.getResource("/question-and-answer.png"));
		PBounds imageBounds2 = questionAndAnswer.getBoundsReference();
		questionAndAnswer.setOffset(bounds.getCenterX() - (imageBounds2.getCenterX() * questionAndAnswer.getScale()), bounds.getCenterY() - (imageBounds2.getCenterY() * questionAndAnswer.getScale()) - 10);
		addChild(questionAndAnswer);
		
		layerDemoPicture = new PImage(LayerDemoShape.class.getResource("/layer-demo-picture.png"));
		layerDemoPicture.setScale(0.11);
		
		PBounds imageBounds = layerDemoPicture.getBoundsReference();
		layerDemoPicture.setOffset(bounds.getCenterX() - (imageBounds.getCenterX() * layerDemoPicture.getScale()), 5 + bounds.getCenterY() - (imageBounds.getCenterY() * layerDemoPicture.getScale()));
		addChild(layerDemoPicture);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();
		
		ShapeUtils.setApparent(layerDemoPicture, true);
		ShapeUtils.setApparent(questionAndAnswer, false);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();
		
		ShapeUtils.setApparent(layerDemoPicture, false);
		ShapeUtils.setApparent(questionAndAnswer, true);
	}

	
}
