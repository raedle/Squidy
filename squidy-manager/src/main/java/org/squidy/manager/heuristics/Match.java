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

package org.squidy.manager.heuristics;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.Processor;


/**
 * <code>Match</code>.
 * 
 * <pre>
 * Date: Mar 28, 2009
 * Time: 2:36:24 PM
 * </pre>
 * 
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: Match.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Match")
public class Match {

	@XmlAttribute(name = "index", required = true)
	private int index;
	
	/**
	 * @return the index
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public final void setIndex(int index) {
		this.index = index;
	}

	@XmlAttribute(name = "type", required = true)
	private String type;
	
	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}

	@XmlAttribute(name = "processor-type", required = true)
	private Processor.Type processorType;

	/**
	 * @return the processorType
	 */
	public final Processor.Type getProcessorType() {
		return processorType;
	}

	/**
	 * @param processorType the processorType to set
	 */
	public final void setProcessorType(Processor.Type processorType) {
		this.processorType = processorType;
	}
}
