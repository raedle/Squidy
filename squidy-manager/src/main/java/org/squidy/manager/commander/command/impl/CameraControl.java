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

package org.squidy.manager.commander.command.impl;

import org.squidy.manager.commander.ControlServerContext;
import org.squidy.manager.commander.command.ICommand;
import org.squidy.manager.commander.command.SwitchableCommand;

import com.phidgets.AdvancedServoPhidget;
import com.phidgets.PhidgetException;


/**
 * <code>CameraControl</code>.
 *
 * <pre>
 * Date: Jan 21, 2009
 * Time: 4:53:50 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: CameraControl.java 772 2011-09-16 15:39:44Z raedle $
 * @since 2.0
 */
public class CameraControl extends SwitchableCommand {

	public static final String KEY_ADVANCED_SERVO_PHIDGET = "advanced.servo.phidget";
		
	/* (non-Javadoc)
	 * @see org.squidy.control.command.SwitchableCommand#off(org.squidy.control.ControlServerContext)
	 */
	@Override
	public ICommand off(ControlServerContext context) {
		
		try {
			AdvancedServoPhidget advancedServoPhidget = new AdvancedServoPhidget();
			
			advancedServoPhidget.openAny();
			
			advancedServoPhidget.waitForAttachment(10000);
			
//			advancedServoPhidget.setMotorOn(0, true);
			advancedServoPhidget.setEngaged(0, true);
			advancedServoPhidget.setSpeedRampingOn(0, true);
			advancedServoPhidget.setAcceleration(0, 50);
			advancedServoPhidget.setPosition(0, 55);
			
//			advancedServoPhidget.setEngaged(0, false);
//			advancedServoPhidget.setMotorOn(0, false);
			
			advancedServoPhidget.close();
		}
		catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.control.command.SwitchableCommand#on(org.squidy.control.ControlServerContext)
	 */
	@Override
	public ICommand on(ControlServerContext context) {
		
		//context.putObject(KEY_ADVANCED_SERVO_PHIDGET, value)
		
		try {
			AdvancedServoPhidget advancedServoPhidget = new AdvancedServoPhidget();
			
			advancedServoPhidget.openAny();
			
			advancedServoPhidget.waitForAttachment(10000);
			
			advancedServoPhidget.setEngaged(0, true);
//			advancedServoPhidget.setMotorOn(0, true);
			advancedServoPhidget.setSpeedRampingOn(0, true);
			advancedServoPhidget.setAcceleration(0, 50);
			advancedServoPhidget.setPosition(0, 100);
			
//			advancedServoPhidget.setMotorOn(0, false);
//			advancedServoPhidget.setEngaged(0, false);
			
			advancedServoPhidget.close();
		}
		catch (PhidgetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
