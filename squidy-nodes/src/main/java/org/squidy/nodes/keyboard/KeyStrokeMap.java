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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.KeyStroke;

import static java.awt.event.InputEvent.SHIFT_MASK;
import static java.awt.event.KeyEvent.CHAR_UNDEFINED;

/**
 * <code>KeyStrokeMap</code>.
 * 
 * <pre>
 * Date: Nov 13, 2008
 * Time: 1:07:07 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: KeyStrokeMap.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class KeyStrokeMap {

	private static final String ENGLISH = "en";

	private static final Map<Character, KeyStroke> CHAR_TO_KEY_STROKE = new HashMap<Character, KeyStroke>();
	private static final Map<KeyStroke, Character> KEY_STROKE_TO_CHAR = new HashMap<KeyStroke, Character>();

	static {
		initialize();
	}

	private static void initialize() {
		Locale locale = Locale.getDefault();
		if (locale.getLanguage().equals(ENGLISH)) {
			addKeyStrokesFrom(new KeyStrokeMappingProvider_de());
			return;
		}
		addKeyStrokesFrom(new DefaultKeyStrokeMappingProvider());
	}

	/**
	 * Adds the collection of <code>{@link KeyStrokeMapping}</code>s from the given
	 * <code>{@link KeyStrokeMappingProvider}</code> to this map.
	 * 
	 * @param provider
	 *            the given <code>KeyStrokeMappingProvider</code>.
	 */
	public static synchronized void addKeyStrokesFrom(KeyStrokeMappingProvider provider) {
		for (KeyStrokeMapping entry : provider.keyStrokeMappings())
			add(entry.character(), entry.keyStroke());
	}

	private static void add(Character character, KeyStroke keyStroke) {
		CHAR_TO_KEY_STROKE.put(character, keyStroke);
		KEY_STROKE_TO_CHAR.put(keyStroke, character);
	}

	/**
	 * Removes all the character-<code>{@link KeyStroke}</code> mappings.
	 */
	public static synchronized void clearKeyStrokes() {
		CHAR_TO_KEY_STROKE.clear();
		KEY_STROKE_TO_CHAR.clear();
	}

	/**
	 * Returns the <code>{@link KeyStroke}</code> corresponding to the given character, as best we
	 * can guess it, or <code>null</code> if we don't know how to generate it.
	 * 
	 * @param character
	 *            the given character.
	 * @return the key code-based <code>KeyStroke</code> corresponding to the given character, or
	 *         <code>null</code> if we cannot generate it.
	 */
	public static KeyStroke keyStrokeFor(char character) {
		return CHAR_TO_KEY_STROKE.get(character);
	}

	/**
	 * Given a <code>{@link KeyStroke}</code>, returns the equivalent character. Key strokes are
	 * defined properly for US keyboards only. To contribute your own, please add them using the
	 * method <code>{@link #addKeyStrokesFrom(KeyStrokeMappingProvider)}</code>.
	 * 
	 * @param keyStroke
	 *            the given <code>KeyStroke</code>.
	 * @return KeyEvent.VK_UNDEFINED if the result is unknown.
	 */
	public static char charFor(KeyStroke keyStroke) {
		Character character = KEY_STROKE_TO_CHAR.get(keyStroke);
		if (character == null) {
			// Try again, but strip all modifiers but shift
			int mask = keyStroke.getModifiers() & ~SHIFT_MASK;
			character = KEY_STROKE_TO_CHAR.get(KeyStroke.getKeyStroke(keyStroke.getKeyCode(), mask));
			if (character == null)
				return CHAR_UNDEFINED;
		}
		return character.charValue();
	}

	private KeyStrokeMap() {
	}
}
