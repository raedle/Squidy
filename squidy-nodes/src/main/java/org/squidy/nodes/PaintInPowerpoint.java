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
 * <code>PaintInPowerpoint</code>.
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
 * @version $Id: PaintInPowerpoint.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "PaintInPowerpoint")
@Processor(
	name = "PaintInPowerpoint",
	icon = "/org/squidy/nodes/image/48x48/funnel.png",
	description = "[32] But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure. [33] On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains",
	types = { Processor.Type.INPUT, Processor.Type.FILTER, Processor.Type.OUTPUT },
	tags = { },
	status = Status.UNSTABLE
)
public class PaintInPowerpoint extends AbstractNode {

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
	 * @version $Id: PaintInPowerpoint.java 772 2011-09-16 15:39:44Z raedle $
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
	 * @version $Id: PaintInPowerpoint.java 772 2011-09-16 15:39:44Z raedle $
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
	
	private boolean penActivated = false;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#beforeDataContainerProcessing(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
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
