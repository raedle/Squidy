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


package org.squidy.nodes;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;


/**
 * <code>PowerpointControl</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 6:23:29 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: PowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "PowerpointControl")
@Processor(
	name = "PowerpointControl",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	description = "Empty Node to be filled...",
	types = { Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT },
	tags = { },
	status = Status.UNSTABLE
)
public class PowerpointControl extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	@XmlAttribute(name = "operating-system")
	@Property(
		name = "Operating System PowerPoint Application runs on"
	)
	@ComboBox(domainProvider = OperatingSystemProvider.class)
	private String operatingSystem = "osx";

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	@XmlAttribute(name = "language")
	@Property(
		name = "Language of PowerPoint Application"
	)
	@ComboBox(domainProvider = LanguageDomainProvider.class)
	private String language = "english";
	
	/**
	 * @return the language
	 */
	public final String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public final void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * <code>OperatingSystemProvider</code>.
	 * 
	 * <pre>
	 * Date: Sep 16, 2009
	 * Time: 5:35:36 PM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: PowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class OperatingSystemProvider implements DomainProvider {

		// Operating system values.
		private static final Object[] VALUES = new Object[]{
			new ComboBoxControl.ComboBoxItemWrapper("osx", "OS X"),
			new ComboBoxControl.ComboBoxItemWrapper("windows", "Windows")
		};
		
		/* (non-Javadoc)
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			return VALUES;
		}
	}
	
	/**
	 * <code>LanguageDomainProvider</code>.
	 * 
	 * <pre>
	 * Date: Aug 24, 2009
	 * Time: 6:17:14 AM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: PowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class LanguageDomainProvider implements DomainProvider {

		// Language values.
		private static final Object[] VALUES = new Object[]{ 
			new ComboBoxControl.ComboBoxItemWrapper("english", "English"),
			new ComboBoxControl.ComboBoxItemWrapper("german", "German")
		};
		
		/* (non-Javadoc)
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			return VALUES;
		}
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################
	
	// ################################################################################
	// BEGIN OF PROCESS
	// ################################################################################
	
	private boolean ignoreInput = false;
	private boolean penActivated = false;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		if (ignoreInput) {
			return null;
		}
		
		List<DataPosition2D> positions = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		
		if (positions.size() == 1 && penActivated) {
			DataPosition2D dataPosition2D = positions.get(0);
			
			if (dataPosition2D.hasAttribute(iPhone.TOUCHES_MOVED)) {
				DataButton buttonDown = new DataButton(PaintInPowerpoint.class, DataButton.BUTTON_1, true);
				publish(buttonDown);
			}
			else if (dataPosition2D.hasAttribute(iPhone.TOUCHES_ENDED)) {
				DataButton buttonUp = new DataButton(PaintInPowerpoint.class, DataButton.BUTTON_1, false);
				publish(buttonUp);
			}
		}
		if (positions.size() == 2) {
			boolean bothDown = true;
			for (DataPosition2D dataPosition2D : positions) {
				bothDown = dataPosition2D.hasAttribute(iPhone.TOUCHES_BEGAN);

				if (!bothDown) {
					break;
				}
			}

			if (bothDown) {
				
				ignoreInput = true;
				new Thread() {
					/* (non-Javadoc)
					 * @see java.lang.Thread#run()
					 */
					public void run() {
						try {
							sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ignoreInput = false;
					};
				}.start();
				
				int keyCode = getModeKeyEvent(penActivated);
				penActivated = !penActivated;
				
				DataButton buttonUp = new DataButton(PaintInPowerpoint.class, DataButton.BUTTON_1, false);
				publish(buttonUp);
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore
				}
				
				DataDigital ctrlDown = new DataDigital(PaintInPowerpoint.class, true);
				ctrlDown.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
				publish(ctrlDown);
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore
				}

				DataDigital keyDown = new DataDigital(PaintInPowerpoint.class, true);
				keyDown.setAttribute(Keyboard.KEY_EVENT, keyCode);
				publish(keyDown);
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore
				}

				DataDigital keyUp = new DataDigital(PaintInPowerpoint.class, false);
				keyUp.setAttribute(Keyboard.KEY_EVENT, keyCode);
				publish(keyUp);
				
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore
				}

				DataDigital ctrlUp = new DataDigital(PaintInPowerpoint.class, false);
				ctrlUp.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
				publish(ctrlUp);

				return null;
			}
		}
		
		return super.preProcess(dataContainer);
	}
	
	/**
	 * Uncomment method if processing of data string is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
	public IData process(DataString dataString) {
		
		if (dataString.hasAttribute(ShakeRecognizer.SHAKE_EVENT)) {
			int eraseKeyEvent = getEraseKeyEvent();
			
			DataDigital keyDown = new DataDigital(PaintInPowerpoint.class, true);
			keyDown.setAttribute(Keyboard.KEY_EVENT, eraseKeyEvent);
			publish(keyDown);
			
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				// ignore
			}

			DataDigital keyUp = new DataDigital(PaintInPowerpoint.class, false);
			keyUp.setAttribute(Keyboard.KEY_EVENT, eraseKeyEvent);
			publish(keyUp);
		}
		
		return dataString;
	}
	
	// ################################################################################
	// END OF PROCESS
	// ################################################################################
	
	/**
	 * @param isPenActivated
	 * @return
	 */
	private int getModeKeyEvent(boolean isPenActivated) {
		if (penActivated) {
			if ("english".equals(language)) {
				return KeyEvent.VK_A;
			}
			else if ("german".equals(language)) {
				return KeyEvent.VK_A;
			}
		}
		else {
			if ("english".equals(language)) {
				return KeyEvent.VK_P;
			}
			else if ("german".equals(language)) {
				return KeyEvent.VK_P;
			}
		}
		return -1;
	}
	
	/**
	 * @return
	 */
	private int getModeKeyModifier() {
		if ("osx".equals(operatingSystem)) {
			return KeyEvent.VK_META;
		}
		else if ("windows".equals(operatingSystem)) {
			return KeyEvent.VK_CONTROL;
		}
		return -1;
	}
	
	/**
	 * @return
	 */
	private int getEraseKeyEvent() {
		if ("english".equals(language)) {
			return KeyEvent.VK_E;
		}
		else if ("german".equals(language)) {
			return KeyEvent.VK_L;
		}
		return -1;
	}
}