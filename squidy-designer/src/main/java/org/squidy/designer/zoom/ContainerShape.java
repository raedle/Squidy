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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.MVEL;
import org.squidy.Storable;
import org.squidy.database.RemoteUpdatable;
import org.squidy.database.RemoteUpdatablePool;
import org.squidy.database.RemoteUpdateUtil;
import org.squidy.designer.Designer;
import org.squidy.designer.component.button.ImageButton;
import org.squidy.designer.event.ZoomActionEvent;
import org.squidy.designer.event.ZoomActionListener;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.model.WorkspaceShape;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.manager.Manager;
import org.squidy.manager.model.Processable;
import org.squidy.manager.model.Processable.Action;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ContainerShape</code>.
 * 
 * <pre>
 * Date: Jul 14, 2009
 * Time: 12:59:46 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ContainerShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class ContainerShape<T extends VisualShape<?>, P extends Processable> extends ActionShape<T, P> implements RemoteUpdatable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7529185270166511409L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ContainerShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public ContainerShape() {
		// empty
	}
	
	@Override
	public void setId(String id) {
		super.setId(id);
		
		RemoteUpdatablePool.putRemoteUpdatable(this);
	}
	
	@XmlAttribute(name = "child-scale")
	private double childScale = 1.0;
	
	/**
	 * @return the childScale
	 */
	public double getChildScale() {
		return childScale;
	}

	/**
	 * @param childScale the childScale to set
	 */
	public void setChildScale(double childScale) {
		this.childScale = childScale;
	}
	
	@XmlAttribute(name = "grid-visible")
	private boolean gridVisible = true;

	/**
	 * @return the gridVisible
	 */
	public boolean isGridVisible() {
		return gridVisible;
	}

	/**
	 * @param gridVisible the gridVisible to set
	 */
	public void setGridVisible(boolean gridVisible) {
		this.gridVisible = gridVisible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomNavigationObject#afterUnmarshal(javax
	 * .xml.bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################
	
	// #############################################################################
	// BEGIN RemoteUpdatable
	// #############################################################################
	
	/* (non-Javadoc)
	 * @see org.squidy.database.RemoteUpdatable#serialize()
	 */
//	@Override
	public String serialize() {
		
//		"type=" + containerShape.getProcessable().getClass().getName() +
//		",processor=" + containerShape.getProcessable().getId() +
//		",shape=" + containerShape.getId() +
//		",layoutConstraintId=" + containerShape.getLayoutConstraint().getId() +
//		",parent=" + ((ContainerShape<?, ?>) containerShape.getParent()).getId() + 
		
		return RemoteUpdateUtil.createSerial(
				new String[]{ "processorType", "processorId", "layoutConstraint", "parentId"}, 
				new Object[]{ getProcessable().getClass().getName(), getProcessable().getId(), getLayoutConstraint().serialize(), ((ContainerShape<?, ?>) getParent()).getId() });
	}

	/* (non-Javadoc)
	 * @see org.squidy.database.RemoteUpdatable#deserialize(java.lang.String)
	 */
//	@Override
	public void deserialize(String serial) {
		RemoteUpdateUtil.applySerial(this, serial);
		String[] keyValues = serial.split(String.valueOf(SERIAL_DELIMITER));
		
		for (String keyValue : keyValues) {
			String[] kv = keyValue.split(String.valueOf(KEY_VALUE_DELIMITER));
			MVEL.setProperty(this, kv[0], kv[1]);
		}
		
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	// #############################################################################
	// END RemoteUpdatable
	// #############################################################################
	
	// #############################################################################
	// BEGIN Initializable
	// #############################################################################
	
//	private JComponentWrapper childScaleComponent;
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ActionShape#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
//		final JSlider childSizeSpinner = new JSlider(1, 100, (int) (getScale() * 100));
//		childSizeSpinner.setPaintLabels(true);
//		childSizeSpinner.setPaintTicks(true);
//		childSizeSpinner.setPaintTrack(true);
//		childSizeSpinner.setSnapToTicks(true);
//		childSizeSpinner.setBackground(Color.WHITE);
//		childScaleComponent = new JComponentWrapper(childSizeSpinner);
//		addChild(childScaleComponent);
////		childScaleComponent.setScale(0.1);
//		childScaleComponent.setOffset(1050, 15);
//		childSizeSpinner.addChangeListener(new ChangeListener() {
//			
//			/* (non-Javadoc)
//			 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
//			 */
//			public void stateChanged(ChangeEvent e) {
//				Object value = childSizeSpinner.getValue();
//				
//				for (Object o : getChildrenReference()) {
//					if (o instanceof ActionShape<?, ?>) {
//						ActionShape<?, ?> node = (ActionShape<?, ?>) o;
//						node.setScale(((Integer) value).doubleValue() / 100d);
//					}
//				}
//			}
//		});
	}
	
	// #############################################################################
	// END Initializable
	// #############################################################################

	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ActionShape#initializeLayout()
	 */
	@Override
	public void initializeLayout() {
		super.initializeLayout();
		
		final ImageButton startProcessing = new ImageButton(ActionShape.class
				.getResource("/images/24x24/media_play_green.png"), "Start");
		final ImageButton stopProcessing = new ImageButton(ActionShape.class
				.getResource("/images/24x24/media_stop_red.png"), "Stop");
		stopProcessing.setEnabled(false);

		startProcessing.addZoomActionListener(new ZoomActionListener() {

			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {

				new Thread() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Thread#run()
					 */
					@Override
					public void run() {
						if (!getProcessable().isProcessing()) {
							startProcessing.setEnabled(false);
							stopProcessing.setEnabled(true);
							invalidatePaint();
							ContainerShape.this.start();
							ContainerShape.this.doStart();
							
							Manager.get().notify(getProcessable(), Action.START);
						}
					}
				}.start();
			}
		});

		stopProcessing.addZoomActionListener(new ZoomActionListener() {

			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {
				new Thread() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Thread#run()
					 */
					@Override
					public void run() {
						// if (getProcessable().isProcessing()) {
						startProcessing.setEnabled(true);
						stopProcessing.setEnabled(false);
						invalidatePaint();
						ContainerShape.this.stop();
						ContainerShape.this.doStop();
						// }
						
						Manager.get().notify(getProcessable(), Action.STOP);
					}
				}.start();
			}
		});

		ImageButton delete = new ImageButton(ActionShape.class
				.getResource("/images/24x24/delete2.png"), "Delete");
		delete.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Pressed delete on " + getBreadcrumb());
				}

				int option = JOptionPane.showConfirmDialog(Designer
						.getInstance(), "Would you like to delete "
						+ getTitle() + "?", "Delete?",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, new ImageIcon(
								ActionShape.class
										.getResource("/images/delete.png")));

				if (option == JOptionPane.OK_OPTION) {
					ZoomShape<?> zoomShape = null;
					if (currentZoomState == ZoomState.ZOOM_IN
							&& getParent() instanceof ZoomShape<?>) {
						zoomShape = (ZoomShape<?>) getParent();
					}
					delete();
					if (zoomShape != null) {
						zoomShape.animateToCenterView(e.getCamera());
						
						// Set pipe shapes visible and pickable.
						for (Object child : zoomShape.getChildrenReference()) {
							if (child instanceof PipeShape) {
								ShapeUtils.setApparent((PipeShape) child, true);
							}
						}
					}
					
					Manager.get().notify(getProcessable(), Action.DELETE);
				}
			}
		});

		ImageButton duplicate = new ImageButton(ActionShape.class
				.getResource("/images/24x24/copy.png"), "Duplicate");
		duplicate.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Duplicate has been pressed for "
							+ getBreadcrumb());
				}
				
				Manager.get().notify(getProcessable(), Action.DUPLICATE);
			}
		});
		duplicate.setEnabled(false);

		ImageButton publish = new ImageButton(ActionShape.class
				.getResource("/images/24x24/export2.png"), "Publish");
		publish.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				if (LOG.isDebugEnabled()) {
					LOG
							.debug("Publish has been pressed for "
									+ getBreadcrumb());
				}

				Storable storable = ShapeUtils.getObjectInHierarchy(Storable.class, ContainerShape.this);
				if (storable != null) {
					storable.store();
				}
			}
		});
		publish.setEnabled(false);

		ImageButton update = new ImageButton(ActionShape.class
				.getResource("/images/24x24/import1.png"), "Update");
		update.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Update has been pressed for " + getBreadcrumb());
				}
			}
		});
		update.setEnabled(false);
		
		addPropertyChangeListener(Processable.PROPERTY_PROCESSING,
				new PropertyChangeListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * 
					 * 
					 * @seejava.beans.PropertyChangeListener#propertyChange(java.
					 * beans. PropertyChangeEvent)
					 */
					public void propertyChange(PropertyChangeEvent evt) {
						startProcessing.setEnabled(!(Boolean) evt.getNewValue());
						stopProcessing.setEnabled((Boolean) evt.getNewValue());
						
						if ((Boolean) evt.getNewValue())
							doStart();
						else
							doStop();
					}
				});

//		addPropertyChangeListener(Processable.PROPERTY_PROCESSING_STOP,
//				new PropertyChangeListener() {
//
//					/*
//					 * (non-Javadoc)
//					 * 
//					 * 
//					 * 
//					 * @seejava.beans.PropertyChangeListener#propertyChange(java.
//					 * beans. PropertyChangeEvent)
//					 */
//					public void propertyChange(PropertyChangeEvent evt) {
//						// startProcessing.setToggleState(ZoomToggle.RELEASED);
//						// stopProcessing.setToggleState(ZoomToggle.PRESSED);
//						startProcessing.setEnabled(true);
//						stopProcessing.setEnabled(false);
//						doStop();
//					}
//				});

		ShapeUtils.setApparent(startProcessing, false);
		ShapeUtils.setApparent(stopProcessing, false);
		if (!(this instanceof WorkspaceShape)) {
			ShapeUtils.setApparent(delete, false);
			ShapeUtils.setApparent(duplicate, false);
			ShapeUtils.setApparent(publish, false);
			ShapeUtils.setApparent(update, false);
		}
		
		addAction(startProcessing);
		addAction(stopProcessing);
		if (!(this instanceof WorkspaceShape)) {
			addAction(delete);
			addAction(duplicate);
			addAction(publish);
			addAction(update);
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#setScale(double)
	 */
	@Override
	public void setScale(double scale) {
		super.setScale(scale);
		
//		if (childScaleComponent != null) {
//			JSlider scaleSlider = (JSlider) childScaleComponent.getComponent();
//			scaleSlider.setValue((int) (scale * 100));
//		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.VisualShape#addVisualShape(org.squidy
	 * .designer.VisualShape)
	 */
	public void addVisualShape(T child) {
		super.addVisualShape(child);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ActionShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();
		
//		ShapeUtils.setApparent(childScaleComponent, true);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ActionShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();
		
//		ShapeUtils.setApparent(childScaleComponent, false);
	}
	
	private static Color ALPHA_GRAY_COLOR = new Color(120, 120, 120, 30);
	private static Stroke GRID_STROKE_THIN = new BasicStroke(1f);
	private static Stroke GRID_STROKE_THICK = new BasicStroke(2f);
	
	private static int GRIDS = 40;
	private static int GRID_SPACING = 5;
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.NavigationShape#paintShapeZoomedIn(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);
		
		if (gridVisible) {
			Graphics2D g = paintContext.getGraphics();
			PBounds bounds = getBoundsReference();
			double width = bounds.getWidth();
			double height = bounds.getHeight();

			g.setColor(ALPHA_GRAY_COLOR);
			double widthStep = width / (double) GRIDS;
			for (int i = 1; i <= GRIDS; i++) {
				g.setStroke((i % GRID_SPACING == 0) ? GRID_STROKE_THICK : GRID_STROKE_THIN);
				g.drawLine((int) widthStep * i, 0, (int) widthStep * i, (int) height);
			}
			double heightStep = height / (double) GRIDS;
			for (int i = 1; i < GRIDS - 1; i++) {
				g.setStroke((i % GRID_SPACING == 0) ? GRID_STROKE_THICK : GRID_STROKE_THIN);
				g.drawLine(0, (int) heightStep * i, (int) width, (int) heightStep * i);
			}
		}
	}
}
