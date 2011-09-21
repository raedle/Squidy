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

package org.squidy.manager;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;

/**
 * <code>IProcessable</code>.
 * 
 * <pre>
 * Date: Feb 11, 2008
 * Time: 1:24:22 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: IProcessable.java 772 2011-09-16 15:39:44Z raedle $$
 */
public interface IProcessable<P extends IProcessable<?>> extends ILaunchable {

	/**
	 * Whether the processing is already processing.
	 * 
	 * @return True if the processable is processing data.
	 */
	public boolean isProcessing();

	/**
	 * Adds a sub processable to this processable.
	 * 
	 * @param processable
	 *            The sub processable.
	 */
	public void addSubProcessable(P processable);

	/**
	 * Removes a sub processable of the sub processable collection.
	 * 
	 * @param processable
	 *            The sub processable that should be removed of the sub
	 *            processable collection.
	 */
	public void removeSubProcessable(P processable);

	/**
	 * Processes the data container and returns the processed result of the data
	 * container.
	 * 
	 * @param dataContainer
	 *            The data container to be processed.
	 * @return The result of the process.
	 */
	public IDataContainer process(IDataContainer dataContainer);

	/**
	 * Publishs data objects to connected processables.
	 * 
	 * @param data
	 *            The data objects to be published.
	 */
	public void publish(IData... data);

	/**
	 * Publishs a data container to connected processables.
	 * 
	 * @param data
	 *            The data container to be published.
	 */
	public void publish(IDataContainer dataContainer);
}
