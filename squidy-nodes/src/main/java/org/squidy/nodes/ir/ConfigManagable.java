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

package org.squidy.nodes.ir;

import org.squidy.manager.data.IData;
import org.squidy.nodes.tracking.config.ConfigNotifier;
import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Configuration;


/**
 * <code>ConfigManagable</code>.
 *
 * <pre>
 * Date: May 23, 2008
 * Time: 5:31:07 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ConfigManagable.java 772 2011-09-16 15:39:44Z raedle $  * @since 1.0.0  * @since 1.0.0
 * @since 1.0
 */
public interface ConfigManagable {
	public String getRemoteAddress();
	public int getRemotePort();
	public void attachNotifier(ConfigNotifier configNotifier);
	public void detachNotifier(ConfigNotifier configNotifier);
	public void notifyUpdateConfig();
	public Configuration getConfig();
	public Camera getCalibrationCamera();
	public boolean isConfigXmlReadOnly();
	public void publish(IData... data);
	public boolean isShowWhite();
}
