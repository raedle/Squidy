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

package org.squidy.manager.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <code>ClasspathScanner</code>. <p/> Date: Feb 8, 2008 Time: 11:29:37 AM
 * <p/>
 * 
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ClasspathScanner.java 772 2011-09-16 15:39:44Z raedle $$
 * 
 */
public abstract class ClasspathScanner {

//	private static final transient Log LOG = LogFactory.getLog(ClasspathScanner.class);

	protected static final String[] DEFAULT_PACKAGE_FILTER = { "de.ukn.hci" };

	private static Classpath classpath;

	private Set<String> locationFilters;

	private Set<String> packageFilters;

	private List<ClassMatcher> matchers = new ArrayList<ClassMatcher>();

	protected ClasspathScanner() {

	}

	protected final Classpath getClasspath() {
		synchronized (this) {
			if (classpath == null) {
				buildClasspath();
			}
		}

		return classpath;
	}

	protected final Set<String> getLocationFilters() {
		return this.locationFilters;
	}

	protected final void setLocationFilters(final Set<String> locationFilters) {
		this.locationFilters = locationFilters;
	}

	protected final Set<String> getPackageFilters() {
		return this.packageFilters;
	}

	protected final void setPackageFilters(final Set<String> packageFilters) {
		this.packageFilters = packageFilters;
	}

	public final void addMatcher(final ClassMatcher matcher) {
		matchers.add(matcher);
	}

	private static void buildClasspath() {
		classpath = new Classpath();

		// add libs
//		LOG.debug("Adding libraries");

		for (String libDir : getLibDirs()) {
			File dir = new File(libDir);

			if (dir.exists() && dir.isDirectory()) {
				for (File lib : dir.listFiles()) {
					classpath.addComponent(lib);
//					LOG.debug("Adding library: " + lib);
				}
			} else {
//				LOG.error("Library directory not found: " + dir);
			}
		}

		// add classes

//		LOG.debug("Adding class directories");

		for (String classesDir : getClassDirs()) {
			File dir = new File(classesDir);

			if (dir.exists() && dir.isDirectory()) {
				classpath.addComponent(dir);
//				LOG.debug("Adding class directory: " + dir);
			} else {
//				LOG.error("Class directory not found: " + dir);
			}
		}

	}

	private static String[] getLibDirs() {
		return new String[0];
	}

	private static String[] getClassDirs() {
		return new String[] { "" };
	}

	protected final boolean matchesAny(final String text, final Set<String> filters) {

		if (filters.size() == 0) {
			return true;
		}

		for (String filter : filters) {
			if (text.indexOf(filter) != -1) {
				return true;
			}
		}
		return false;
	}

	protected final boolean checkAllMatchers(final Class cl) {

		for (ClassMatcher matcher : matchers) {
			if (matcher.matches(cl)) {
				return true;
			}
		}
		return false;
	}

	protected static final Class loadClass(final ClassLoader loader, final String name) throws Exception {
		try {
			return loader.loadClass(name);
		} catch (Exception ex) {
			try {
				return Thread.currentThread().getContextClassLoader().loadClass(name);
			} catch (Exception e) {
				return Class.forName(name);
			}
		}
	}

	public interface ClassMatcher {
		boolean matches(Class cl);
	}

	public static final ClassMatcher DEFAULT_CLASSNAME_MATCHER = new ClassMatcher() {
		public boolean matches(final Class cl) {
			return false;
		}
	};
}

