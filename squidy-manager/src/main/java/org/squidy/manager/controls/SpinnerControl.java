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

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinnerControl extends AbstractBasicControl<Number, JSpinner> {

	/**
	 * @param value
	 */
	public SpinnerControl(Number value, Comparable<? extends Number> minumumValue, Comparable<? extends Number> maximumValue, Number stepSize) {
		super(new JSpinner(new SpinnerNumberModel(value, minumumValue, maximumValue, stepSize)));
		getComponent().setBackground(Color.WHITE);
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.components.basiccontrols.IBasicControl#getValue()
	 */
	public Number getValue() {
		return (Number) ((JSpinner) getComponent()).getValue();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#setValueWithoutPropertyUpdate(java.lang.Object)
	 */
	public void setValue(Number value) {
		((JSpinner) getComponent()).setValue(value);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public Number valueFromString(String value) {
		return Integer.valueOf(value);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#reconcileComponent()
	 */
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
				firePropertyUpdateEvent((Number) getComponent().getValue());
			}
		});
	}
}
