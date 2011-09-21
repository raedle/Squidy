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

import java.awt.Color;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderControl<N extends Number> extends AbstractBasicControl<N, JSlider> {
	
	/**
	 * @param value
	 * @param minimumValue
	 * @param maximumValue
	 */
	public SliderControl(N value, N minimumValue, N maximumValue) {
		super(new JSlider((Integer) minimumValue, (Integer) maximumValue, (Integer) value));
		getComponent().setBackground(Color.WHITE);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#getValue()
	 */
	public N getValue() {
		return (N) (Integer) getComponent().getValue();
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#setValueWithoutPropertyUpdate(java.lang.Object)
	 */
	public void setValue(N value) {
		getComponent().setValue((Integer) value);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public N valueFromString(String value) {
		return (N) Integer.valueOf(value);
	}

	@Override
	protected void reconcileComponent() {
		getComponent().addChangeListener(new ChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javax.swing.event.ChangeListener#stateChanged(javax.swing
			 * .event.ChangeEvent)
			 */
			public void stateChanged(ChangeEvent e) {
				if (!getComponent().getValueIsAdjusting()) {
					firePropertyUpdateEvent((N) (Integer) getComponent().getValue());
				}
			}
		});
	}
}
