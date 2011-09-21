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

import edu.umd.cs.piccolox.pswing.PComboBox;

public class ComboBoxControl extends AbstractBasicControl<Object, PComboBox>{

	/**
	 * @param values
	 */
	public ComboBoxControl(Object[] values) {
		super(new PComboBox(values));
		
//		ComboBoxUI comboBoxUI = ReflectionUtility.createInstance("apple.laf.CUIAquaComboBox");
//		getComponent().setUI(comboBoxUI);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#getValue()
	 */
	public Object getValue() {//
		throw new UnsupportedOperationException("Getting value of ComboBoxControl is currenty not supported.");
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public Object valueFromString(String value) {
		throw new UnsupportedOperationException("Transforming a string to value is currenty not supported.");
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#setValueWithoutPropertyUpdate(java.lang.Object)
	 */
	public void setValue(Object value) {
		final PComboBox component = getComponent();
		component.setEditable(true);
		for (int i = 0; i < component.getItemCount(); ++i) {
			Object item = component.getItemAt(i);
			if (item instanceof ComboBoxItemWrapper) {
				final ComboBoxItemWrapper wrapper = (ComboBoxItemWrapper)item;
				if (wrapper.value.equals(value)) {
					component.setSelectedIndex(i);
					break;
				}
			}
		}
		//throw new UnsupportedOperationException("Setting value for ComboBoxControl is currenty not supported.");
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#reconcileComponent()
	 */
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
				if (ItemEvent.SELECTED == e.getStateChange()) {

					Object value = e.getItem();
					if (value instanceof ComboBoxControl.ComboBoxItemWrapper) {
						ComboBoxControl.ComboBoxItemWrapper wrapper = (ComboBoxControl.ComboBoxItemWrapper) value;
						value = wrapper.getValue();
					}

					firePropertyUpdateEvent(value);
				}
			}
		});
	}



	/**
	 * <code>ComboBoxItemWrapper</code>.
	 * 
	 * <pre>
	 * Date: Mar 25, 2009
	 * Time: 12:27:51 AM
	 * </pre>
	 * 
	 * @author <pre>
	 * Roman R&amp;aumldle
	 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * 
	 * @version $Id: ComboBoxControl.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class ComboBoxItemWrapper {

		private Object value;
		private String friendlyName;
		
		/**
		 * @param value
		 * @param friendlyName
		 */
		public ComboBoxItemWrapper(Object value, String friendlyName) {
			this.value = value;
			this.friendlyName = friendlyName;
		}
		
		/**
		 * @return
		 */
		public Object getValue() {
			return value;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return friendlyName;
		}
	}
}
