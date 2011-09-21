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

package org.squidy.common.dynamiccode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>HotDeployClassLoader</code>.
 * 
 * <pre>
 * Date: Aug 21, 2009
 * Time: 12:32:25 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 *         @uni-konstanz.de</a> Human-Computer Interaction Group University of
 *         Konstanz
 * 
 * @version $Id: HotDeployClassLoader.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class HotDeployClassLoader extends ClassLoader {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(HotDeployClassLoader.class);
	
	private Map<String, ClassHolder> classTimeStamps;
	private List<String> classesToDelegate;

	private Map<String, RuntimeClassLoader> runtimeClassLoaders;

	/**
	 * @param classLoader
	 */
	public HotDeployClassLoader(ClassLoader classLoader) {
		super(classLoader);

		classTimeStamps = new HashMap<String, ClassHolder>();
		classesToDelegate = new ArrayList<String>();
		
		runtimeClassLoaders = new HashMap<String, RuntimeClassLoader>();
	}

	/**
	 * @param name
	 */
	public void addClassToDelegateList(String name) {
		classesToDelegate.add(name);
	}

	/**
	 * @param name
	 * @return
	 */
	protected boolean isClassInDelagationList(String name) {
		return classesToDelegate.contains(name);
	}

	/* (non-Javadoc)
	 * @see java.lang.ClassLoader#findClass(java.lang.String)
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (!isClassAvailable(name)) {
//			if (LOG.isDebugEnabled()) {
//				LOG.debug("Needs loading " + name);
//			}

			if (!runtimeClassLoaders.containsKey(name)) {
				createRuntimeClassLoader(name);
			}
			return runtimeClassLoaders.get(name).loadClass(name);
		}

		if (isNewerVersionAvailable(name)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Newer version required for " + name);
			}

			classTimeStamps.remove(name);

			createRuntimeClassLoader(name);
			return runtimeClassLoaders.get(name).loadClass(name);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Version is still up-to-date " + name);
		}
		return classTimeStamps.get(name).getClazz();
	}
	
	/**
	 * 
	 */
	private void createRuntimeClassLoader(String name) {
		runtimeClassLoaders.put(name, new RuntimeClassLoader(this));
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean isClassAvailable(String name) {
		return classTimeStamps.containsKey(name);
	}

	/**
	 * @param name
	 * @return
	 */
	private boolean isNewerVersionAvailable(String name) {
		File file = new File(DynamicCodeClassLoader.DYNAMIC_CODE_REPOSITORY, name.replace(".", "/") + ".class");
		return file.lastModified() > classTimeStamps.get(name).getTimeStamp();
	}

//	/* (non-Javadoc)
//	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
//	 */
//	@Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException {
//		return loadClass(name, false);
//	}
	
	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		return (isClassInDelagationList(name)) ? getParent().loadClass(name) : findClass(name);
	}

	/**
	 * <code>ClassHolder</code>.
	 * 
	 * <pre>
	 * Date: Aug 21, 2009
	 * Time: 12:33:08 PM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: HotDeployClassLoader.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	static class ClassHolder {
		private Class<?> clazz;
		private long timeStamp;

		public ClassHolder(Class<?> clazz, long timeStamp) {
			this.clazz = clazz;
			this.timeStamp = timeStamp;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

	}

	/**
	 * <code>RuntimeClassLoader</code>.
	 * 
	 * <pre>
	 * Date: Aug 21, 2009
	 * Time: 12:33:12 PM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: HotDeployClassLoader.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private class RuntimeClassLoader extends ClassLoader {
		
		/**
		 * @param classLoader
		 */
		public RuntimeClassLoader(ClassLoader classLoader) {
			super(classLoader);
		}

		/* (non-Javadoc)
		 * @see java.lang.ClassLoader#findClass(java.lang.String)
		 */
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return loadClass(name);
		}

		/* (non-Javadoc)
		 * @see java.lang.ClassLoader#loadClass(java.lang.String)
		 */
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (isClassInDelagationList(name)) {
				return getParent().loadClass(name);
			}
			byte[] classBytes = null;
			try {
				classBytes = getBytes(name.replace(".", "/") + ".class");
			}
			catch (IOException e) {
				return findSystemClass(name);
			}

			Class<?> clazz = defineClass(name, classBytes, 0, classBytes.length);
			File file = new File(DynamicCodeClassLoader.DYNAMIC_CODE_REPOSITORY, name.replace(".", "/") + ".class");
			classTimeStamps.put(name, new ClassHolder(clazz, file.lastModified()));
			return clazz;
		}

		/**
		 * @param filename
		 * @return
		 * @throws IOException
		 */
		private byte[] getBytes(String filename) throws IOException {
			File file = new File(DynamicCodeClassLoader.DYNAMIC_CODE_REPOSITORY, filename);
			long len = file.length();
			byte raw[] = new byte[(int) len];
			FileInputStream fin = new FileInputStream(file);
			int r = fin.read(raw);
			if (r != len)
				throw new IOException("Can't read all, " + r + " != " + len);
			fin.close();
			return raw;
		}
	}
}
