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

package org.squidy.designer.knowledgebase;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.database.RemoteUpdatable;
import org.squidy.database.RemoteUpdatableSessionProvider;
import org.squidy.database.Session;
import org.squidy.database.SessionFactory;
import org.squidy.database.SessionFactoryProvider;
import org.squidy.designer.Designer;
import org.squidy.designer.DesignerPreferences;
import org.squidy.designer.component.CropScroll;
import org.squidy.designer.components.pdf.PDFPane;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.designer.zoom.ContainerShape;
import org.squidy.designer.zoom.TitledShape;
import org.squidy.designer.zoom.impl.InformationShape;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolo.util.PStack;

/**
 * <code>KnowledgeBaseItem</code>.
 * 
 * <pre>
 * Date: Feb 15, 2009
 * Time: 7:20:34 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: RepositoryItem.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class RepositoryItem<T extends ZoomShape<?>> extends TitledShape<T> implements Instantiable<Processable> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 687637552092351418L;

	// Logger to info, error, debug,... messages.
	private static Log LOG = LogFactory.getLog(RepositoryItem.class);

	public static final String ITEM_SELECTION = "ITEM_SELECTION";

	private Class<? extends Processable> processableClass;
	
	private boolean stable = true;

	/**
	 * @return the processableClass
	 */
	public Class<? extends Processable> getProcessableClass() {
		return processableClass;
	}

	private PImage image;

	private CropScroll cropScroll;
	private String information;
	
	/**
	 * @param goalDirectedZoom
	 */
	public RepositoryItem(Class<? extends Processable> processable) throws SquidyException {
		setBounds(Constants.DEFAULT_NODE_BOUNDS);

		setTitle("N/A");

		this.processableClass = processable;

		if (processable.isAnnotationPresent(Processor.class)) {
			Processor processor = processable.getAnnotation(Processor.class);
			setTitle(processor.name());
			stable = processor.status().equals(Status.STABLE);
			information = processor.description();
			try {
				image = new PImage(ImageUtils.getProcessorIconURL(processor));
			} catch (Exception e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		if (image != null) {
			image.setScale(10);
			image.setPickable(false);
			image.setChildrenPickable(false);
			addChild(image);
		}
		addInputEventListener(new KnowledgeBaseEventHandler());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.knowledgebase.Instantiable#getInstance()
	 */
	public Processable getInstance() {
		return ReflectionUtil.createInstance(processableClass);
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

		//ShapeUtils.setApparent(image, false);
		ShapeUtils.setApparent(cropScroll, true);
		
		image.setScale(1.0);
		image.setOffset(40, 40);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		ShapeUtils.setApparent(image, true);
		ShapeUtils.setApparent(cropScroll, false);
		
		if (cropScroll != null) {
			cropScroll.removeFromParent();
			cropScroll = null;
		}
		PBounds bounds = getBoundsReference();
		PBounds imageBounds = image.getBoundsReference();
		image.setScale(10);
		image.offset(bounds.getCenterX() - (imageBounds.getCenterX() * image.getScale()), 150);
	}
	
	private static Font fontName = internalFont.deriveFont(50f);
	private static Font fontSource = internalFont.deriveFont(30f);
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#getZoomedOutDrawPaint()
	 */
	@Override
	protected Paint getZoomedOutDrawPaint() {
		return stable ? super.getZoomedOutDrawPaint() : DesignerPreferences.STATUS_UNSTABLE;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#paintShapeZoomedIn(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);
		
		Graphics2D g = paintContext.getGraphics();
		
		String information = "N/A";
		if (information != null) {

			PBounds bounds = getBoundsReference();
			double x = bounds.getX();
			double width = bounds.getWidth();

			String source = "Source: " + information;

			g.setFont(fontName);
			g.drawString(getProcessableClass().getSimpleName(), (int) (x + 100), 80);

			g.setFont(fontSource);
			g.drawString(source,
					(int) (x + width - FontUtils.getWidthOfText(g.getFontMetrics(), source)) - 20, 130);
		}
	}
	
	private void initPane() {
		// Add Zoomable Component
		new Thread(new Runnable() {
			public void run() {

				// ProgressIndicator indicator = new
				// ProgressIndicator(InformationShape.this);

				if (information == null || "".equals(information)) {
					return;
				}
				
				URL url = null;
				try {
					try {
						if (information.endsWith(".pdf")) {
							url = InformationShape.class.getResource(information);
						}
						else if (information.endsWith(".html")) {
							try {
								url = new URL(information);
							}
							catch (Exception e) {
								url = InformationShape.class.getResource(information);
							}
						}
						else {
							url = new URL(information);
						}
					}
					catch (Exception e) {
						// do nothing
					}

					PNode cropNode;
					// PDF
					if (information.endsWith(".pdf")) {
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

						JEditorPane editorPane = new JEditorPane();
						editorPane.setFont(internalFont.deriveFont(10f));

						FontMetrics fm = editorPane.getFontMetrics(editorPane.getFont());
						int editorWidth = 400;
						editorPane.setPreferredSize(new Dimension(editorWidth, FontUtils.getLineCount(information,
								editorWidth)
								* fm.getHeight()));

						cropNode = JComponentWrapper.create(editorPane);
						editorPane.setEditable(false);

						if (information.endsWith(".html")) {
							HTMLEditorKit editorKit = new HTMLEditorKit();
							editorPane.setEditorKit(editorKit);
							editorPane.setPage(url);
							editorPane.setPreferredSize(new Dimension(800, 2000));
						}
						else {
							editorPane.setText(information);
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

					cropScroll = new CropScroll(cropNode, new Dimension(1000, 700), 0.2);
					cropScroll.setOffset(getBoundsReference().getCenterX()
							- cropScroll.getBoundsReference().getCenterX(), 250);
					addChild(cropScroll);
					invalidateFullBounds();
					invalidateLayout();
					invalidatePaint();
				}
				catch (MalformedURLException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error("Could not parse URL from input string: " + e.getMessage() + " in "
								+ RepositoryItem.class.getName() + ".\nInput was: " + information);
					}
				}
				catch (IOException e) {
					if (LOG.isErrorEnabled()) {
						LOG.error("Could not create HTMLPane in " + RepositoryItem.class.getName(), e);
					}
				}

				// indicator.done();
			}
		}).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((processableClass == null) ? 0 : processableClass.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryItem<?> other = (RepositoryItem<?>) obj;
		if (processableClass == null) {
			if (other.processableClass != null)
				return false;
		}
		else if (!processableClass.equals(other.processableClass))
			return false;
		return true;
	}

	/**
	 * <code>KnowledgeBaseEventHandler</code>.
	 * 
	 * <pre>
	 * Date: Jul 9, 2009
	 * Time: 4:15:27 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of
	 *                      Konstanz
	 * 
	 * @version $Id: RepositoryItem.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	class KnowledgeBaseEventHandler extends PBasicInputEventHandler {

		private VisualShape<?> instance;

		private PNode originator;
		private PNode parent;

		private PropertyChangeListener prop;

		private Stack<PNode> poppedNodes = new Stack<PNode>();
		private Stack<PAffineTransform> poppedTransforms = new Stack<PAffineTransform>();

		/* (non-Javadoc)
		 * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseDragged(edu.umd.cs.piccolo.event.PInputEvent)
		 */
		@Override
		public void mouseDragged(final PInputEvent event) {
			super.mouseDragged(event);
			
			try {
				originator = getOriginator(event);
			}
			catch (SquidyException e) {
				return;
			}

			Processable processable = getInstance();

			instance = ShapeUtils.getActionShape(processable);

			parent = getParent();
			while (parent != null && !(parent instanceof ContainerShape<?, ?>)) {
				parent = parent.getParent();
			}

			if (parent != null && parent instanceof ContainerShape<?, ?>) {

				if (originator.getParent().getParent().getParent() instanceof NodeRepositoryShape<?>) {
					dragZoomedIntoKnowledgeBase(event);

					// Zoom out animation.
					if (parent != null && parent instanceof ZoomShape<?>) {
						((ZoomShape<?>) parent).animateToCenterView(event.getCamera());
					}
				}
				else {
					((ContainerShape) parent).addVisualShape(instance);

					PBounds bounds = originator.getBounds();
					double minX = bounds.getMinX();
					double minY = bounds.getMinY();

					instance.setVisible(true);
					originator.localToGlobal(bounds);
					instance.globalToLocal(bounds);

					double offsetX = (bounds.getMinX() - minX) * instance.getScale();
					double offsetY = (bounds.getMinY() - minY) * instance.getScale();

					instance.setOffset(offsetX, offsetY);
					
					if (instance instanceof RemoteUpdatable) {
						SessionFactory<? extends Session> factory = SessionFactoryProvider.getProvider();
						if (factory instanceof RemoteUpdatableSessionProvider)
							((RemoteUpdatableSessionProvider<? extends Session>) factory).updateRemote((RemoteUpdatable) instance);
					}
						
					Designer.getInstance().add(instance);
				}

				// Pick path -> traversed by event handler.
				PPickPath path = event.getPath();

				// Pop nodes and transforms until required type is reached.
				popNodesAndTransformUntilType(path, AdvancedKnowledgeBase.class);

				// Insert new node instance and its transform.
				path.pushNode(instance);
				path.pushTransform(instance.getTransform());

				if (originator.getParent().getParent().getParent() instanceof NodeRepositoryShape<?>) {
					// Push back popped nodes and popped transforms.
					pushBackNodesAndTransforms(path);
				}

				((ActionShape<?, Piping>) instance).setProcessable((Piping) processable);
				((ActionShape<?, Piping>) parent).getProcessable().addSubProcessable(processable);
			}

			instance.setDraggable(true);

			firePropertyChange(-1, ITEM_SELECTION, null, instance);
		}

		/**
		 * @param event
		 */
		private PNode getOriginator(PInputEvent event) throws SquidyException {
			PNode node = event.getPickedNode();
			while (node != null && !(node instanceof Instantiable<?>)) {
				node = node.getParent();
			}

			if (node == null) {
				throw new SquidyException("Required instatiable not found.");
			}

			return node;
		}

		

		/**
		 * @param path
		 * @param type
		 */
		private void popNodesAndTransformUntilType(PPickPath path, Class<?> type) {
			// Clear previously popped nodes and popped transforms.
			poppedNodes.clear();
			poppedTransforms.clear();

			boolean poppedTopNode = false;

			PStack stack = path.getNodeStackReference();
			PNode stackNode = (PNode) stack.peek();
			while (!type.isAssignableFrom(stackNode.getClass())) {

				// Pop node and its transform.
				PNode poppedNode = (PNode) stack.pop();
				PAffineTransform transform = stackNode.getTransformReference(false);
				path.popTransform(transform);

				if (poppedTopNode) {

					// Push node to storage stack.
					poppedNodes.push(poppedNode);

					// Push transform to storage stack.
					poppedTransforms.push(transform);
				}
				else {
					poppedTopNode = true;
				}

				// Process with next node.
				stackNode = (PNode) stack.peek();
			}
		}

		/**
		 * @param path
		 */
		private void pushBackNodesAndTransforms(PPickPath path) {
			for (PNode node : poppedNodes) {
				path.pushNode(node);
			}

			for (PAffineTransform transform : poppedTransforms) {
				path.pushTransform(transform);
			}
		}

		private PBasicInputEventHandler dragHandler;
		private PNode dragNode;

		/**
		 * @param event
		 */
		private void dragZoomedIntoKnowledgeBase(final PInputEvent event) {
			Point2D p = originator.getOffset();

			Point2D p1 = originator.getParent().getOffset();
			p.setLocation(p.getX() + p1.getX(), p.getY() + p1.getY());

			p1 = originator.getParent().getParent().getOffset();
			p.setLocation(p.getX() + p1.getX(), p.getY() + p1.getY());

			instance.setOffset(p);

			dragNode = createDragNode(originator);
			dragNode.removeFromParent();
			event.getCamera().addChild(dragNode);
			dragNode.setScale(0.02);
			dragNode.setOffset(event.getPositionRelativeTo(event.getCamera()));

			dragHandler = new PBasicInputEventHandler() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#
				 * processEvent(edu.umd.cs.piccolo.event.PInputEvent, int)
				 */
				@Override
				public void processEvent(PInputEvent event, int type) {
					super.processEvent(event, type);

					switch (type) {
					case MouseEvent.MOUSE_DRAGGED:
						dragNode.setOffset(event.getPositionRelativeTo(event.getCamera()));
						break;
					case MouseEvent.MOUSE_RELEASED:
						if (event.getCamera().getChildrenReference().contains(dragNode)) {
							event.getCamera().removeChild(dragNode);
						}

						if (parent instanceof ContainerShape<?, ?>) {
							((ContainerShape<VisualShape<?>, ?>) parent).addVisualShape(instance);
							Point2D pos = event.getPositionRelativeTo(parent);
							instance.setOffset(pos);
						}

						event.getTopCamera().removeInputEventListener(dragHandler);

						break;
					}
				}
			};
			event.getTopCamera().addInputEventListener(dragHandler);
		}
	}

	private PNode createDragNode(PNode originator) {

		if (originator instanceof RepositoryItem<?>) {
			return new RepositoryItem(((RepositoryItem<?>) originator).getProcessableClass());
		}

		return new PNode();
	}
}