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

package org.squidy.nodes.keyboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.KeyStroke;

import static java.awt.event.KeyEvent.*;

/**
 * <code>DefaultKeyStrokeMappingProvider</code>.
 * 
 * <pre>
 * Date: Nov 13, 2008
 * Time: 1:08:12 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: DefaultKeyStrokeMappingProvider.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class DefaultKeyStrokeMappingProvider implements KeyStrokeMappingProvider {

	/**
	 * Returns the default mapping of characters and <code>{@link KeyStroke}</code>s. This provider
	 * will only return the mappings for following keys:
	 * <ul>
	 * <li>Escape</li>
	 * <li>Backspace</li>
	 * <li>Delete</li>
	 * <li>Enter</li>
	 * </ul>
	 * 
	 * @return the default mapping of characters and <code>KeyStroke</code>s
	 */
	public Collection<KeyStrokeMapping> keyStrokeMappings() {
		List<KeyStrokeMapping> mappings = new ArrayList<KeyStrokeMapping>();
		mappings.add(new KeyStrokeMapping('\b', VK_BACK_SPACE, NO_MASK));
		mappings.add(new KeyStrokeMapping('', VK_DELETE, NO_MASK));
		mappings.add(new KeyStrokeMapping('', VK_ESCAPE, NO_MASK));
		mappings.add(new KeyStrokeMapping('\n', VK_ENTER, NO_MASK));
		mappings.add(new KeyStrokeMapping('\r', VK_ENTER, NO_MASK));
		return mappings;
	}
}
