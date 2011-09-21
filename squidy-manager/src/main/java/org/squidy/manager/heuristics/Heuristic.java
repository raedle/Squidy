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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * <code>Heuristic</code>.
 * 
 * <pre>
 * Date: Mar 28, 2009
 * Time: 2:40:20 PM
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
 * @version $Id: Heuristic.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class Heuristic {

	@XmlAttribute(name = "type")
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

	@XmlElement(name = "match")
	private Collection<Match> matches = new ArrayList<Match>();

	/**
	 * @return the matches
	 */
	public final Collection<Match> getMatches() {
		return matches;
	}

	/**
	 * @param matches the matches to set
	 */
	public final void setMatches(Collection<Match> matches) {
		this.matches = matches;
	}
}
