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

import javax.swing.KeyStroke;

/**
 * <code>KeyStrokeMapping</code>.
 * 
 * <pre>
 * Date: Nov 13, 2008
 * Time: 1:02:32 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: KeyStrokeMapping.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class KeyStrokeMapping {

	private final char character;
	private final KeyStroke keyStroke;

	/**
	 * Creates a new </code>{@link KeyStrokeMapping}</code>.
	 * 
	 * @param character
	 *            the character corresponding to the intended <code>KeyStroke</code>.
	 * @param keyCode
	 *            the numeric key code for the intended <code>KeyStroke</code>.
	 * @param modifiers
	 *            the set of modifiers for the intended <code>KeyStroke</code>.
	 * @return the created <code>KeyStrokeMapping</code>.
	 */
	public static KeyStrokeMapping mapping(char character, int keyCode, int modifiers) {
		return new KeyStrokeMapping(character, keyCode, modifiers);
	}

	/**
	 * Creates a new </code>{@link KeyStrokeMapping}</code>.
	 * 
	 * @param character
	 *            the character corresponding to the intended <code>KeyStroke</code>.
	 * @param keyCode
	 *            the numeric key code for the intended <code>KeyStroke</code>.
	 * @param modifiers
	 *            the set of modifiers for the intended <code>KeyStroke</code>.
	 */
	public KeyStrokeMapping(char character, int keyCode, int modifiers) {
		this(character, KeyStroke.getKeyStroke(keyCode, modifiers));
	}

	/**
	 * Creates a new </code>{@link KeyStrokeMapping}</code>.
	 * 
	 * @param character
	 *            the character corresponding to the given <code>KeyStroke</code>.
	 * @param keyStroke
	 *            the <code>KeyStroke</code> corresponding to the given character.
	 */
	public KeyStrokeMapping(char character, KeyStroke keyStroke) {
		this.character = character;
		this.keyStroke = keyStroke;
	}

	/**
	 * Returns the character corresponding to this mapping's <code>{@link #keyStroke()}</code>.
	 * 
	 * @return the character corresponding to this mapping's <code>KeyStroke</code>.
	 */
	public char character() {
		return character;
	}

	/**
	 * Returns the <code>{@link KeyStroke}</code> corresponding to this mapping's
	 * <code>{@link #character()}</code>.
	 * 
	 * @return the <code>KeyStroke</code> corresponding to this mapping's character.
	 */
	public KeyStroke keyStroke() {
		return keyStroke;
	}

	/**
	 * Returns the <code>String</code> representation of this class.
	 * 
	 * @return the <code>String</code> representation of this class.
	 */
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(getClass().getSimpleName()).append("[");
		b.append("character='").append(character).append("',");
		b.append("keyStroke=").append(keyStroke).append("]");
		return b.toString();
	}
}
