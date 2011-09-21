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

package org.squidy.manager.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

public class CheckBoxControl extends AbstractBasicControl<Boolean, JCheckBox> {

	/**
	 * @param value
	 */
	public CheckBoxControl(Boolean value) {
		super(new JCheckBox());
		((JCheckBox) getComponent()).setSelected(value);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#getValue()
	 */
	public Boolean getValue() {
		return ((JCheckBox) getComponent()).isSelected();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#setValueWithoutPropertyUpdate(java.lang.Object)
	 */
	public void setValue(Boolean value) {
		((JCheckBox) getComponent()).setSelected(value);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public Boolean valueFromString(String value) {
		return Boolean.valueOf(value);
	}

	@Override
	protected void reconcileComponent() {
		getComponent().addItemListener(new ItemListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ItemListener#itemStateChanged(java.awt.event
			 * .ItemEvent)
			 */
			public void itemStateChanged(ItemEvent e) {
				firePropertyUpdateEvent((Boolean) getComponent().isSelected());
			}
		});
	}
}
