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


package org.squidy.nodes.plugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.Designer;
import org.squidy.designer.component.PropertiesTable;
import org.squidy.designer.component.TableEntry;
import org.squidy.manager.PropertyUpdateListener;
import org.squidy.manager.commander.ControlClient;
import org.squidy.manager.commander.ControlServerContext;
import org.squidy.manager.commander.command.ICommand;
import org.squidy.manager.commander.command.SwitchableCommand;
import org.squidy.manager.commander.command.utility.Switch;
import org.squidy.manager.controls.CheckBoxControl;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.TextFieldControl;
import org.squidy.manager.data.Property;
import org.squidy.manager.plugin.Pluggable;
import org.squidy.manager.plugin.Plugin;

import edu.umd.cs.piccolo.PNode;
/**
 * <code>PatternPlugin</code>.
 * 
 * <pre>
 * Date: Jul 05, 2009
 * Time: 11:52:37 PM
 * </pre>
 * 
 * 
 * @author Toni Schmidt <a
 *         href="mailto:toni.schmidt@uni-konstanz.de">toni.schmidt
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: CameraConfigPlugin.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@Plugin(name = "Camera Configuration")
public class CameraConfigPlugin implements Pluggable {
	private static final Log LOG = LogFactory.getLog(PatternPlugin.class);
	private ControlClient client;
	private String test = "test";
	
	@XmlAttribute(name = "cameras")
	@Property(name = "Cameras", group = "Camera Configuration", description = "Cameras to be attached.")
	@TextField
	private int cameras = 1;

	/**
	 * @return the cameras
	 */
	public final int getCameras() {
		return cameras;
	}
	
		
	@Plugin.Interface
	public PNode getInterface() {
		
		PropertiesTable table = new PropertiesTable();
		
		TextFieldControl control = new TextFieldControl(Integer.toString(getCameras()));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				test = value;
				disposeControlClient();
			}
		});		
		TableEntry<TextFieldControl> entry = new TableEntry<TextFieldControl>("Remote IP", "Description", control, "",
		"");
		table.addEntry(entry);
		
		return table;
	}
	private void disposeControlClient() {
		if (client != null) {
			client.close();
			client = null;
		}
	}	

}
