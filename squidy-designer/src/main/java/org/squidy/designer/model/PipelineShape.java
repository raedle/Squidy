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

import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.modularity.NodeBased;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.designer.zoom.Connectable;
import org.squidy.designer.zoom.ConnectorShape;
import org.squidy.designer.zoom.ZoomState;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.Processor;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.nodes.PImage;

/**
 * <code>ZoomPipeline</code>.
 * 
 * <pre>
 * Date: Feb 2, 2009
 * Time: 12:49:49 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PipelineShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
@XmlType(name = "PipelineShape")
public class PipelineShape extends ConnectorShape<VisualShape<?>, Piping> implements Draggable, Connectable, NodeBased {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -2895306183015570552L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PipelineShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public PipelineShape() {
		super();
		setTitle("Pipeline");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#afterUnmarshal(javax.xml
	 * .bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}

	// #############################################################################
	// END JAXB
	// #############################################################################
	
	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		getProcessable().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		getProcessable().stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		if (getProcessable() != null) {
			if (getProcessable().isProcessing()) {
				stop();
			}

			getProcessable().delete();
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting processable shape...");
		}

		// TODO [RR]: Remove ZoomShape

		removeFromParent();
	}

	// #############################################################################
	// END ILaunchable
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################
	
	private PImage[] inputValves;

	private PImage[] outputValves;

	public PipelineShape(String headline) {
		super();
		setTitle(headline);
	}

	/**
	 * 
	 */
	private void prepareZoomOutRepresentation() {
		int inputValveCounter = 0;
		int outputValveCounter = 0;
		for (Object child : getChildrenReference()) {
			
			if (inputValveCounter > (inputValves.length - 1) || outputValveCounter > (outputValves.length - 1)) {
				break;
			}
			
			if (child instanceof NodeShape) {
				Processable processable = ((NodeShape) child).getProcessable();

				if (processable != null && !(processable instanceof AbstractNode)) {
					Processor processor = processable.getClass().getAnnotation(Processor.class);
					Image image = Toolkit.getDefaultToolkit().getImage(ImageUtils.getProcessorIconURL(processor));
					for (Processor.Type type : processor.types()) {

						switch (type) {
						case INPUT:
							inputValves[inputValveCounter].setImage(image);
							ShapeUtils.setApparent(inputValves[inputValveCounter],
									currentZoomState == ZoomState.ZOOM_OUT);
							inputValveCounter++;
							break;
						case OUTPUT:
							outputValves[outputValveCounter].setImage(image);
							ShapeUtils.setApparent(outputValves[outputValveCounter],
									currentZoomState == ZoomState.ZOOM_OUT);
							outputValveCounter++;
							break;
						}
					}
				}
			}
		}

		// Set unused input valve slots as not apparent.
		for (; inputValveCounter < inputValves.length; inputValveCounter++) {
			ShapeUtils.setApparent(inputValves[inputValveCounter], false);
		}

		// Set unused output valve slots as not apparent.
		for (; outputValveCounter < outputValves.length; outputValveCounter++) {
			ShapeUtils.setApparent(outputValves[outputValveCounter], false);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.VisualShape#initializeInternalComponents()
	 */
	@Override
	protected void initializeInternalComponents() {
		super.initializeInternalComponents();
		
		inputValves = new PImage[] { new PImage(), new PImage(), new PImage() };
		outputValves = new PImage[] { new PImage(), new PImage(), new PImage() };
		
		inputValves[0].setPickable(false);
		inputValves[0].setOffset(44, 10);
		inputValves[1].setPickable(false);
		inputValves[1].setOffset(44, 30);
		inputValves[2].setPickable(false);
		inputValves[2].setOffset(44, 50);
		outputValves[0].setPickable(false);
		outputValves[0].setOffset(70, 10);
		outputValves[1].setPickable(false);
		outputValves[1].setOffset(70, 30);
		outputValves[2].setPickable(false);
		outputValves[2].setOffset(70, 50);

		addChild(inputValves[0]);
		addChild(inputValves[1]);
		addChild(inputValves[2]);
		addChild(outputValves[0]);
		addChild(outputValves[1]);
		addChild(outputValves[2]);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.VisualShape#initializeShapeListeners()
	 */
	@Override
	protected void initializeShapeListeners() {
		super.initializeShapeListeners();
		
		// Listener that prepares the zoom out representation of the pipeline shape.
		addPropertyChangeListener(new PropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
			 * PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				prepareZoomOutRepresentation();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		// Set input valve slots as not apparent.
		for (PImage image : inputValves) {
			ShapeUtils.setApparent(image, false);
		}

		// Set output valve slots as not apparent.
		for (PImage image : outputValves) {
			ShapeUtils.setApparent(image, false);
		}

		// Set all <code>ZoomValve</code> children apparent.
		for (Object child : getChildrenReference()) {
			if (child instanceof ActionShape<?, ?>) {
				ShapeUtils.setApparent((ActionShape<?, ?>) child, true);
			}
			else if (child instanceof PipeShape) {
				ShapeUtils.setApparent((PipeShape) child, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		// Set input valve slots apparent.
		for (PImage image : inputValves) {
			ShapeUtils.setApparent(image, true);
		}

		// Set output valve slots apparent.
		for (PImage image : outputValves) {
			ShapeUtils.setApparent(image, true);
		}

		// Set all <code>ZoomValve</code> children as not apparent.
		for (Object child : getChildrenReference()) {
			if (child instanceof ActionShape<?, ?>) {
				ShapeUtils.setApparent((ActionShape<?, ?>) child, false);
			}
			else if (child instanceof PipeShape) {
				ShapeUtils.setApparent((PipeShape) child, false);
			}
		}
	}
	
	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
