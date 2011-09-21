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

import javax.swing.KeyStroke;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>MouseOffSimulator</code>.
 * 
 * <pre>
 * Date: Feb 12, 2008
 * Time: 1:42:21 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: FlipXY2D.java 451 2010-11-29 14:34:44Z raedle $
 */
@XmlType(name = "MouseOffSimulator")
@Processor(
	name = "MouseOffSimulator",
	types = { Processor.Type.FILTER },
	tags = { "flip", "vertical", "horizontal", "xy", "2D" }
)
public class MouseOffSimulator extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "keyID")
	@Property(
		name = "Key",
		description = "Key to be pressed"
	)
	@TextField
	private String keyStroke = "-";
	
	public final String getKeyStroke() {
		return keyStroke;
	}

	public final void setKeyStroke(String key) {
		this.keyStroke = key;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	
	
	/**
	 * @param dataPosition2D
	 * @return
	 */
	private boolean keyPressed;
	private boolean simClick;
	public IData process(DataPosition2D dataPosition2D) {
		if (keyPressed)
		{
			dataPosition2D.setX(1);
			dataPosition2D.setY(0);
		}
		return dataPosition2D;
	}
    public DataDigital process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	if (KeyStroke.getKeyStroke(this.keyStroke) == KeyStroke.getKeyStroke(key_event.intValue(),0))
    		{
        		if (dataDigital.getFlag())
        		{
        			if (keyPressed)
        				simClick = true;
        			else
        				simClick = false;
        			keyPressed = !keyPressed;
        		}
    		}
    	}
    	return dataDigital;
    }	
	
}
