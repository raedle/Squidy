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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>Heuristics</code>.
 * 
 * <pre>
 * Date: Mar 28, 2009
 * Time: 2:09:21 PM
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
 * @version $Id: Heuristics.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlRootElement(name = "heuristics")
public class Heuristics {
	
	@XmlElement(name = "default-heuristic")
	private Heuristic defaultHeuristic;

	/**
	 * @return the defaultHeuristic
	 */
	public final Heuristic getDefaultHeuristic() {
		return defaultHeuristic;
	}

	/**
	 * @param defaultHeuristic the defaultHeuristic to set
	 */
	public final void setDefaultHeuristic(Heuristic defaultHeuristic) {
		this.defaultHeuristic = defaultHeuristic;
	}

	@XmlElement(name = "heuristic")
	private Collection<Heuristic> heuristics = new ArrayList<Heuristic>();

	/**
	 * @return
	 */
	public final Collection<Heuristic> getHeuristics() {
		return heuristics;
	}

	/**
	 * @param heuristics
	 */
	public final void setHeuristics(Collection<Heuristic> heuristics) {
		this.heuristics = heuristics;
	}
	
	/**
	 * @param type
	 * @return
	 */
	public final Heuristic getHeuristicForType(String type) {
		for (Heuristic heuristic : heuristics) {
			if (heuristic.getType().equals(type)) {
				return heuristic;
			}
		}
		return null;
	}
}
