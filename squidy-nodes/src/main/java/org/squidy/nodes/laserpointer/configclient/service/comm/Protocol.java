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

package org.squidy.nodes.laserpointer.configclient.service.comm;

public class Protocol {
	public static final String PROTOCOL = "protocol";
	
	public static final String ERROR = "error";
	public static final String UPDATE = "update";
	public static final String CAM_REFRESH = "refresh"; // refresh camera info
	public static final String CAM_STARTCALIBRATION = "start_calibration";
	public static final String CAM_STARTTRACKING = "start_tracking";
	public static final String CAM_STOPTRACKING = "stop_tracking";
}
