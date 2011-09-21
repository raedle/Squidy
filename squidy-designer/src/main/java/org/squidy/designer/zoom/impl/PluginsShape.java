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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ImageShape;
import org.squidy.manager.plugin.Pluggable;


/**
 * <code>PluginsShape</code>.
 * 
 * <pre>
 * Date: Apr 18, 2009
 * Time: 9:27:49 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: PluginsShape.java 776 2011-09-18 21:34:48Z raedle $
 * @since 1.0.0
 */
public class PluginsShape extends ImageShape {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8686534714015237415L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PluginsShape.class);
	
	/**
	 * 
	 */
	public PluginsShape(Class<? extends Pluggable>[] pluginTypes) {
		super("Plugins", PluginsShape.class.getResource("/org/squidy/nodes/image/48x48/plugins.png"));
		setTitle("Plugins");
		
		buildPlugins(pluginTypes);
	}
	
	/**
	 * @param pluginTypes
	 */
	private void buildPlugins(Class<? extends Pluggable>[] pluginTypes) {
		
		int offsetX = 5;
		for (Class<? extends Pluggable> pluginType : pluginTypes) {
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Building plugin of type " + pluginType.getName());
			}
			
			Pluggable plugable = ReflectionUtil.createInstance(pluginType);
			
			PluginShape pluginShape = new PluginShape(plugable);
			pluginShape.setOffset(offsetX, 15);
			pluginShape.setScale(0.1);
			addChild(pluginShape);
			
			offsetX += (pluginShape.getBounds().getWidth() * pluginShape.getScale()) + 5;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();
		
		for (Object child : getChildrenReference()) {
			if (child instanceof PluginShape) {
				ShapeUtils.setApparent((PluginShape) child, true);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();
		
		for (Object child : getChildrenReference()) {
			if (child instanceof PluginShape) {
				ShapeUtils.setApparent((PluginShape) child, false);
			}
		}
	}
}
