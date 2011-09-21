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
import java.awt.Button;
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
import org.squidy.manager.controls.ImagePanelControl;
import org.squidy.manager.controls.TextFieldControl;
import org.squidy.manager.plugin.Pluggable;
import org.squidy.manager.plugin.Plugin;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * <code>PatternPlugin</code>.
 * 
 * <pre>
 * Date: Apr 23, 2009
 * Time: 11:52:37 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: PatternPlugin.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@Plugin(name = "Pattern")
public class PatternPlugin implements Pluggable {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PatternPlugin.class);

	private ControlClient client;
	
	private String remoteAddress = "127.0.0.1";
	private int remotePort = 9999;
	private int patternWidth = 1920;
	private int patternHeight = 1200;
	private int patternColumns = 5;
	private int patternRows = 5;

	@Plugin.Interface
	public PNode getInterface() {
		

		

		PropertiesTable table = new PropertiesTable();

		
		TextFieldControl control = new TextFieldControl(remoteAddress);
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				remoteAddress = value;
				disposeControlClient();
			}
		});
		TableEntry<TextFieldControl> entry = new TableEntry<TextFieldControl>("Remote IP", "Description", control, "",
				"");
		table.addEntry(entry);

		control = new TextFieldControl(String.valueOf(remotePort));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				remotePort = Integer.parseInt(value);
				disposeControlClient();
			}
		});
		entry = new TableEntry<TextFieldControl>("Remote Port", "Description", control, "", "");
		table.addEntry(entry);

		control = new TextFieldControl(String.valueOf(patternWidth));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				patternWidth = Integer.parseInt(value);
			}
		});
		entry = new TableEntry<TextFieldControl>("Pattern Width", "Description", control, "", "");
		table.addEntry(entry);

		control = new TextFieldControl(String.valueOf(patternHeight));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				patternHeight = Integer.parseInt(value);
			}
		});
		entry = new TableEntry<TextFieldControl>("Pattern Height", "Description", control, "", "");
		table.addEntry(entry);

		control = new TextFieldControl(String.valueOf(patternColumns));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				patternColumns = Integer.parseInt(value);
			}
		});
		entry = new TableEntry<TextFieldControl>("Pattern Columns", "Description", control, "", "");
		table.addEntry(entry);

		control = new TextFieldControl(String.valueOf(patternRows));
		control.addPropertyUpdateListener(new PropertyUpdateListener<String>() {
			public void propertyUpdate(String value) {
				patternRows = Integer.parseInt(value);
			}
		});
		entry = new TableEntry<TextFieldControl>("Pattern Rows", "Description", control, "", "");
		table.addEntry(entry);
		
		

		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (final GraphicsDevice device : graphicsEnvironment.getScreenDevices()) {

			CheckBoxControl control2 = new CheckBoxControl(false);
			control2.addPropertyUpdateListener(new PropertyUpdateListener<Boolean>() {

				/**
				 * @param value
				 */
				public void propertyUpdate(Boolean value) {

					InetAddress address = null;
					try {
						address = InetAddress.getByName(remoteAddress);
					}
					catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						if (client == null) {
							client = new ControlClient(address, remotePort);
						}
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					SwitchableCommand command = new PatternScreen(device.getIDstring(), patternWidth, patternHeight,
							patternColumns - 1, patternRows - 1);
					command.setState(value ? Switch.ON : Switch.OFF);
					client.send(command);
					
					Designer.getInstance().requestFocus();
				}
			});
			TableEntry<CheckBoxControl> entry2 = new TableEntry<CheckBoxControl>("Device " + device.getIDstring(), "Description", control2,
					"", "");
			table.addEntry(entry2);
		}
		
		CheckBoxControl loadImageControl = new CheckBoxControl(false);	
		TableEntry<CheckBoxControl> loadImage = new TableEntry<CheckBoxControl>("Load Camera Image", "Description", loadImageControl,
			"", "");		
		table.addEntry(loadImage);	
		

	
		
		
	//	panelControl.setImage("C:\\projects\\Squidy\\squidy-2.0.0\\bayer.PNG");
		
		return table;
		
		
	}
	
	/**
	 * 
	 */
	private void disposeControlClient() {
		if (client != null) {
			client.close();
			client = null;
		}
	}
}
