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

package org.squidy.heuristics;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Ignore;
import org.squidy.manager.heuristics.Heuristic;
import org.squidy.manager.heuristics.Heuristics;
import org.squidy.manager.heuristics.HeuristicsHandler;
import org.squidy.manager.heuristics.Match;


/**
 * <code>HeuristicsLoader</code>.
 * 
 * <pre>
 * Date: Mar 28, 2009
 * Time: 2:42:03 PM
 * </pre>
 * 
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: HeuristicsLoader.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@Ignore
public class HeuristicsLoader {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		HeuristicsHandler handler = HeuristicsHandler.getHeuristicsHandler();

		// Heuristics heuristics = new Heuristics();
		// Heuristic heuristic = new Heuristic();
		// heuristic.setType(TestValve1.class.getName());
		// for (int i = 0; i < 5; i++) {
		// Match match = new Match();
		// match.setIndex(i);
		// match.setType(TestValve1.class.getName());
		// match.setProcessorType(Processor.Type.FILTER);
		// heuristic.getMatches().add(match);
		// }
		// heuristics.getHeuristics().add(heuristic);
		//		
		// handler.save(new FileOutputStream("./heuristics-save.xml"),
		// heuristics);

		InputStream is = new FileInputStream("./heuristics-save.xml");

		Heuristics heuristics = handler.load(is);

		for (Heuristic heuristic : heuristics.getHeuristics()) {
			System.out.println("TYPE: " + heuristic.getType());
			for (Match match : heuristic.getMatches()) {
				System.out.println(match.getIndex() + " | " + match.getType()
						+ " | " + match.getProcessorType());
			}
		}
	}
}
