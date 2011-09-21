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

package org.squidy.manager.util;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * <code>PreferenceManager</code>.
 *
 * <pre>
 * Date: Apr 22, 2008
 * Time: 1:03:42 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: PreferenceUtils.java 772 2011-09-16 15:39:44Z raedle $$
 *
 * $Id: PreferenceUtils.java 772 2011-09-16 15:39:44Z raedle $
 */
public class PreferenceUtils {
	
	// The last recent pipeline.
	public static final String LAST_RECENT_PIPELINE = "last.recent.pipeline";

	protected Preferences prefs;
	
	private PreferenceUtils() {
		prefs = Preferences.userRoot().node("Squidy Designer");
	}
	
	private static PreferenceUtils instance;
	
	public static PreferenceUtils get() {
		if (instance == null) {
			instance = new PreferenceUtils();
		}
		return instance;
	}
	
	public Preferences getPreferences() {
		assert prefs != null : "Preferences node is required in " + getClass().getSimpleName();
		return prefs;
	}
	
	public static void put(String key, String value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.put(key, value);
	}
	
	public static void remove(String key) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.remove(key);
	}
	
	public static String get(String key) {
		return get(key, null);
	}
	
	public static String get(String key, String defaultValues) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		return prefs.get(key, defaultValues);
	}
	
	public static void putInt(String key, Integer value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.putInt(key, value);
	}
	
	public static Integer getInt(String key) {
		return getInt(key, -1);
	}
	
	public static Integer getInt(String key, Integer defaultValue) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		return prefs.getInt(key, defaultValue);
	}
	
	public static void putBoolean(String key, Boolean value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.putBoolean(key, value);
	}
	
	public static Boolean getBoolean(String key) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		return prefs.getBoolean(key, false);
	}
	
	public static void putFile(String key, File value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.put(key, value.getAbsolutePath());
	}
	
	public static File getFile(String key) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		
		String absolutePath = prefs.get(key, null);
		if (absolutePath != null) {
			File file = new File(absolutePath);
			return file.exists() ? file : null;
		}
		return null;
	}
	
	public static void putPoint(String key, Point value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.put(key, value.x + ":" + value.y);
	}
	
	public static Point getPoint(String key) {
		return getPoint(key, null);
	}
	
	public static Point getPoint(String key, Point defaultValue) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		
		String dimension = prefs.get(key, null);
		if (dimension != null) {
			String[] pointValues = dimension.split(":");
			int x = Integer.parseInt(pointValues[0]);
			int y = Integer.parseInt(pointValues[1]);
			return new Point(x, y);
		}
		return defaultValue;
	}
	
	public static void putDimension(String key, Dimension value) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		prefs.put(key, value.width + ":" + value.height);
	}
	
	public static Dimension getDimension(String key) {
		return getDimension(key, null);
	}
	
	public static Dimension getDimension(String key, Dimension defaultValue) {
		Preferences prefs = PreferenceUtils.get().getPreferences();
		
		String dimension = prefs.get(key, null);
		if (dimension != null) {
			String[] dimensionValues = dimension.split(":");
			int width = Integer.parseInt(dimensionValues[0]);
			int height = Integer.parseInt(dimensionValues[1]);
			return new Dimension(width, height);
		}
		return defaultValue;
	}
}

