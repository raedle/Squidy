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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>AdvancedPowerpointControl</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 6:23:29 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: AdvancedPowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "AdvancedPowerpointControl")
@Processor(
	name = "AdvancedPowerpointControl",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	types = { Processor.Type.FILTER },
	tags = {},
	status = Status.UNSTABLE
)
public class AdvancedPowerpointControl extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################

	@XmlAttribute(name = "operating-system")
	@Property(name = "Operating System PowerPoint Application runs on")
	@ComboBox(domainProvider = OperatingSystemProvider.class)
	private String operatingSystem = "osx";

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	@XmlAttribute(name = "language")
	@Property(name = "Language of PowerPoint Application")
	@ComboBox(domainProvider = LanguageDomainProvider.class)
	private String language = "english";

	/**
	 * @return the language
	 */
	public final String getLanguage() {
		return language;
	}

	/**
	 * @param language
	 *            the language to set
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
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of
	 *                      Konstanz
	 * 
	 * @version $Id: AdvancedPowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class OperatingSystemProvider implements DomainProvider {

		// Operating system values.
		private static final Object[] VALUES = new Object[] {
				new ComboBoxControl.ComboBoxItemWrapper("osx", "OS X"),
				new ComboBoxControl.ComboBoxItemWrapper("windows", "Windows") };

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.squidy.manager.data.domainprovider.DomainProvider#getValues
		 * ()
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
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of
	 *                      Konstanz
	 * 
	 * @version $Id: AdvancedPowerpointControl.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class LanguageDomainProvider implements DomainProvider {

		// Language values.
		private static final Object[] VALUES = new Object[] {
				new ComboBoxControl.ComboBoxItemWrapper("english", "English"),
				new ComboBoxControl.ComboBoxItemWrapper("german", "German") };

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.squidy.manager.data.domainprovider.DomainProvider#getValues
		 * ()
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

	public IData process(DataButton dataButton) {

		int keyCode = -1;
		switch (dataButton.getButtonType()) {
		case 0:
			keyCode = getModeKeyEvent(false);
			publish(new DataButton(AdvancedPowerpointControl.class,
					DataButton.BUTTON_1, false));
			break;
		case 1:
			keyCode = getModeKeyEvent(true);
			publish(new DataButton(AdvancedPowerpointControl.class,
					DataButton.BUTTON_1, true));
			break;
		case 2:
			break;
		default:
			break;
		}

		DataDigital ctrlDown = new DataDigital(AdvancedPowerpointControl.class, true);
		ctrlDown.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
		publish(ctrlDown);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}

		DataDigital keyDown = new DataDigital(AdvancedPowerpointControl.class, true);
		keyDown.setAttribute(Keyboard.KEY_EVENT, keyCode);
		publish(keyDown);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}

		DataDigital keyUp = new DataDigital(AdvancedPowerpointControl.class, false);
		keyUp.setAttribute(Keyboard.KEY_EVENT, keyCode);
		publish(keyUp);

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore
		}

		DataDigital ctrlUp = new DataDigital(AdvancedPowerpointControl.class, false);
		ctrlUp.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
		publish(ctrlUp);

		return null;
	}

	// ################################################################################
	// END OF PROCESS
	// ################################################################################

	/**
	 * @param isPenActivated
	 * @return
	 */
	private int getModeKeyEvent(boolean isPenActivated) {
		if (isPenActivated) {
			if ("english".equals(language)) {
				return KeyEvent.VK_A;
			} else if ("german".equals(language)) {
				return KeyEvent.VK_A;
			}
		} else {
			if ("english".equals(language)) {
				return KeyEvent.VK_P;
			} else if ("german".equals(language)) {
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
		} else if ("windows".equals(operatingSystem)) {
			return KeyEvent.VK_CONTROL;
		}
		return -1;
	}

	/**
	 * @reCopyOfPowerpointControlturn
	 */
	private int getEraseKeyEvent() {
		if ("english".equals(language)) {
			return KeyEvent.VK_E;
		} else if ("german".equals(language)) {
			return KeyEvent.VK_L;
		}
		return -1;
	}
}