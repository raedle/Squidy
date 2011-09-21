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
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.designer.component.CropScroll;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.NavigationShape;
import org.squidy.manager.plugin.Pluggable;
import org.squidy.manager.plugin.Plugin;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>PluginShape</code>.
 * 
 * <pre>
 * Date: Apr 18, 2009
 * Time: 9:27:49 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a> Human-Computer Interaction Group University of
 *         Konstanz
 * 
 * @version $Id: PluginShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class PluginShape extends NavigationShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8686534714015237415L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PluginShape.class);

	private final Map<Plugin.Event, Set<Method>> EVENT_TO_METHOD = new HashMap<Plugin.Event, Set<Method>>();

	private Pluggable pluggable;

	private CropScroll cropScroll;
	private PImage image;

	/**
	 * 
	 */
	public PluginShape(Pluggable pluggable) {
		this.pluggable = pluggable;

		image = new PImage();
		image.setPickable(false);
		image.setChildrenPickable(false);
		addChild(image);

		buildPlugin(pluggable);
	}

	/**
	 * @param pluggable
	 */
	private void buildPlugin(Pluggable pluggable) {

		Class<? extends Pluggable> pluggableType = pluggable.getClass();

		Plugin plugin = pluggableType.getAnnotation(Plugin.class);

		setTitle(plugin.name());
		image.setImage(Toolkit.getDefaultToolkit().getImage(ImageUtils.getPluginIconURL(plugin)));
		PBounds bounds = getBoundsReference();
		PBounds imageBounds = image.getBoundsReference();
		image.offset(bounds.getCenterX() - imageBounds.getCenterX(), 10);

		PNode node = null;

		Method[] methods = pluggableType.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Plugin.Interface.class)) {
				node = ReflectionUtil.<PNode>callMethod(method, pluggable);
			}
			else if (method.isAnnotationPresent(Plugin.Logic.class)) {
				Plugin.Logic logic = method.getAnnotation(Plugin.Logic.class);

				for (Plugin.Event event : logic.events()) {
					Set<Method> eventMethods = EVENT_TO_METHOD.get(event);
					if (eventMethods == null) {
						eventMethods = new HashSet<Method>();
						EVENT_TO_METHOD.put(event, eventMethods);
					}
					eventMethods.add(method);
				}
			}
		}

		cropScroll = new CropScroll(node, new Dimension(90, 78), 0.3);
		addChild(cropScroll);
		cropScroll.setOffset(getBoundsReference().getCenterX() - cropScroll.getBoundsReference().getCenterX(), 15);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		ShapeUtils.setApparent(image, false);

		if (cropScroll != null) {
			ShapeUtils.setApparent(cropScroll, true);
		}

		// Call methods that are annotated with a zoom in event.
		if (EVENT_TO_METHOD.containsKey(Plugin.Event.ZOOM_IN)) {
			for (Method method : EVENT_TO_METHOD.get(Plugin.Event.ZOOM_IN)) {
				ReflectionUtil.callMethod(method, pluggable);
				invalidateFullBounds();
			}
		}
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

		if (cropScroll != null) {
			ShapeUtils.setApparent(cropScroll, false);
		}

		// Call methods that are annotated with a zoom out event.
		if (EVENT_TO_METHOD.containsKey(Plugin.Event.ZOOM_OUT)) {
			for (Method method : EVENT_TO_METHOD.get(Plugin.Event.ZOOM_OUT)) {
				ReflectionUtil.callMethod(method, pluggable);
			}
		}
	}
}
