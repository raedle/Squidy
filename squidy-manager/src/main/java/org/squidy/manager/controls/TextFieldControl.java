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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

public class TextFieldControl extends AbstractBasicControl<String, JTextField> {

	/**
	 * @param value
	 */
	public TextFieldControl(String value) {
		super(new JTextField(value));
		getComponent().setPreferredSize(new Dimension(200, 20));
		getComponent().setHorizontalAlignment(JTextField.RIGHT);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#getValue()
	 */
	public String getValue() {
		return ((JTextField) getComponent()).getText();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#setValueWithoutPropertyUpdate(java.lang.Object)
	 */
	public void setValue(String value) {
		((JTextField) getComponent()).setText(value);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public String valueFromString(String value) {
		return value;
	}

	@Override
	protected void reconcileComponent() {
		getComponent().addActionListener(new ActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event
			 * .ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				getComponent().setFocusable(false);
				getComponent().setFocusable(true);
			}
		});
		
		getComponent().addFocusListener(new FocusAdapter() {

			/* (non-Javadoc)
			 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				firePropertyUpdateEvent(getComponent().getText());
			}
		});
	}
}
