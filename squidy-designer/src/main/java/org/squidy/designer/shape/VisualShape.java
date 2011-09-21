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

package org.squidy.designer.shape;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.Initializable;
import org.squidy.designer.component.TemporaryNotification;
import org.squidy.designer.constant.DebugConstants;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>VisualShape</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 1:42:43 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: VisualShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class VisualShape<T extends VisualShape<?>> extends PNode implements Initializable, PropertyChangeListener {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -3046580620271103161L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(VisualShape.class);

	// Used font within visual shape hierarchy.
	public static Font defaultFont;
	public static Font internalFont;

	// Loading default font in static context.
	static {
		try {
			defaultFont = new Font("Arial", Font.BOLD, 2);
//			InputStream is = VisualShape.class.getResourceAsStream("/font/Discognate.ttf");
//			InputStream is = VisualShape.class.getResourceAsStream("/font/Discognate-Light.ttf");
//			internalFont = Font.createFont(Font.TRUETYPE_FONT, is);
			internalFont = new Font("Arial", Font.PLAIN, 2);
		}
		catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e);
			}
		}
	}

	/**
	 * The property name that identifies a initializing done.
	 */
	public static final String PROPERTY_INITIALZED = "initialized";
	public static final int PROPERTY_CODE_INITIALIZED = 1 << 11;

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor is required for JAXB.
	 */
	public VisualShape() {
		id = UUID.randomUUID().toString();

		initializeInternalComponents();
		initializeShapeListeners();
	}
	
	@XmlID
	@XmlAttribute(name = "id")
	private String id;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}
	
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "layout-constraint")
	private LayoutConstraint layoutConstraint = new LayoutConstraint();

	/**
	 * @return the layoutConstraint
	 */
	public final LayoutConstraint getLayoutConstraint() {
		return layoutConstraint;
	}

	/**
	 * @param layoutConstraint
	 *            the layoutConstraint to set
	 */
	public final void setLayoutConstraint(LayoutConstraint layoutConstraint) {
		this.layoutConstraint = layoutConstraint;
	}

	@XmlElement(name = "child")
	@XmlElementWrapper(name = "children")
	private Collection<T> children = new ArrayList<T>();

	/**
	 * @return the children
	 */
	public final Collection<T> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public final void setChildren(Collection<T> children) {
		this.children = children;
	}

	/**
	 * @param child
	 */
	public void addVisualShape(T child) {
		if (!children.contains(child)) {
			children.add(child);
		}

		LayoutConstraint layoutConstraint = child.getLayoutConstraint();
		child.setOffset(layoutConstraint.getX(), layoutConstraint.getY());
		child.setScale(layoutConstraint.getScale());

		addChild(child);
	}

	/**
	 * @param child
	 */
	public final void removeVisualShape(T child) {
		children.remove(child);
		removeChild(child);
	}
	
//	public void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
//		
//		layoutConstraint.addPropertyChangeListener(new PropertyChangeListener() {
//			
//			public void propertyChange(PropertyChangeEvent e) {
//				System.out.println(layoutConstraint);
//				
//				VisualShape.super.setOffset(layoutConstraint.getX(), layoutConstraint.getY());
//				VisualShape.super.setScale(layoutConstraint.getScale());
//			}
//		});
//	}
	
	public void propertyChange(PropertyChangeEvent e) {
//		System.out.println(layoutConstraint);
		
		super.setOffset(layoutConstraint.getX(), layoutConstraint.getY());
		super.setScale(layoutConstraint.getScale());
		repaint();
	}

	/**
	 * This method is called after all the properties (except IDREF) are
	 * unmarshalled for this object, but before this object is set to the parent
	 * object.
	 * 
	 * @param unmarshaller
	 * @param parent
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		
		for (T child : children) {
			LayoutConstraint layoutConstraint = child.getLayoutConstraint();
			addChild(child);
			// addVisualShape(child);
			child.setOffset(layoutConstraint.getX(), layoutConstraint.getY());
			child.setScale(layoutConstraint.getScale());
		}
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	private boolean draggable = false;

	/**
	 * @return the draggable
	 */
	public final boolean isDraggable() {
		return draggable;
	}

	/**
	 * @param draggable
	 *            the draggable to set
	 */
	public final void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}
	
	private static boolean renderPrimitive = false;

	/**
	 * @return the renderPrimitive
	 */
	public static boolean isRenderPrimitive() {
		return renderPrimitive;
	}

	/**
	 * @param renderPrimitive the renderPrimitive to set
	 */
	public static void setRenderPrimitive(boolean renderPrimitive) {
		VisualShape.renderPrimitive = renderPrimitive;
		renderingHintsSet = false;
	}

	private static boolean renderPrimitiveRect = true;

	/**
	 * @return the renderPrimitiveRect
	 */
	public static boolean isRenderPrimitiveRect() {
		return renderPrimitiveRect;
	}

	/**
	 * @param renderPrimitiveRect the renderPrimitiveRect to set
	 */
	public static void setRenderPrimitiveRect(boolean renderPrimitiveRect) {
		VisualShape.renderPrimitiveRect = renderPrimitiveRect;
	}
	
	public void initialize() {
		getLayoutConstraint().afterUnmarshal(null, this);
	}

	protected void initializeInternalComponents() {
	}
	
	protected void initializeShapeListeners() {
//		getLayoutConstraint().afterUnmarshal(null, this);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#removeFromParent()
	 */
	@Override
	public void removeFromParent() {
		if (getParent() instanceof VisualShape) {
			((VisualShape<VisualShape<T>>) getParent()).removeVisualShape(this);
		}

		super.removeFromParent();
	}
	
	public void publishNotification(TemporaryNotification notification) {
		PNode parent = getParent();
		while (parent != null && !(parent instanceof PLayer)) {
			parent = parent.getParent();
		}
		
		if (parent != null) {
			((PLayer) parent).getCamera(0).addChild(notification);
		}
	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#setOffset(double, double)
	 */
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x, y);
		
		layoutConstraint.setX(x);
		layoutConstraint.setY(y);
		
		// Do not uncomment this -> cycling updates between two or more squidy clients.
//		Designer.getInstance().propertyChanged(id, "layoutConstraint", layoutConstraint);
	}
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#repaintFrom(edu.umd.cs.piccolo.util.PBounds, edu.umd.cs.piccolo.PNode)
	 */
	@Override
	public void repaintFrom(PBounds localBounds, PNode childOrThis) {
		
		localBounds.add(localBounds.getMinX() - 20, localBounds.getMinY() - 20);
		localBounds.add(localBounds.getMaxX() + 20, localBounds.getMaxY() + 20);
		
		super.repaintFrom(localBounds, childOrThis);
	}
	
	private static boolean renderingHintsSet = false;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected final void paint(PPaintContext paintContext) {
		super.paint(paintContext);

		Graphics2D g = paintContext.getGraphics();

		// Set default font.
		g.setFont(internalFont);

//		if (!renderingHintsSet) {
			if (isRenderPrimitive()) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
				g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			else {
				// Use anti aliasing -> May slow down performance.
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
//			renderingHintsSet = true;
//		}
		
		// Paint the shapes visual representation.
		paintShape(paintContext);

		// Allows visual debugging if enabled.
		if (DebugConstants.ENABLED) {
			paintDebug(paintContext);
		}
	}

	/**
	 * Allows sub-classing the visual shape and paint individual shapes, forms,
	 * etc. onto the scene.
	 * 
	 * @param paintContext
	 *            The paint context contains the Graphics2D object to draw on,
	 *            scale factor and further information that are required to
	 *            paint the current scene.
	 */
	protected void paintShape(PPaintContext paintContext) {
	}

	/**
	 * Allows subclasses to debug bounds, coordinates, etc. visually. This
	 * method won't be called in paint process if Debug.ENABLED flag is turned
	 * off.
	 * 
	 * @param paintContext
	 *            The paint context contains the Graphics2D object to draw on,
	 *            scale factor and further information that are required to
	 *            paint the current scene.
	 * @see DebugConstants#ENABLED
	 */
	protected void paintDebug(PPaintContext paintContext) {
	}
}
