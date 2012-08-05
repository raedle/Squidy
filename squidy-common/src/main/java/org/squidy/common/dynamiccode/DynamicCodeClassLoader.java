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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;


/**
 * <code>DynamicCode</code>.
 * 
 * <pre>
 * Date: Mar 30, 2009
 * Time: 7:19:03 PM
 * </pre>
 * 
 * @author Roman Rädle <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 *         Human-Computer Interaction Group University of Konstanz
 * @version $Id: DynamicCodeClassLoader.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DynamicCodeClassLoader extends ClassLoader {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(DynamicCodeClassLoader.class);

	private String compileClasspath;

	private File compilerOutput = DYNAMIC_CODE_REPOSITORY;

	private ClassLoader parentClassLoader;

	private ArrayList<SourceDir> sourceDirs = new ArrayList<SourceDir>();

	// class name => LoadedClass
	private HashMap<String, LoadedClass> loadedClasses = new HashMap<String, LoadedClass>();

	// public static final String DYNAMIC_CODE_REPOSITORY_NAME =
	// "DynamicCodeRepository";
	public static final File DYNAMIC_CODE_REPOSITORY;
	static {
		DYNAMIC_CODE_REPOSITORY = new File("target/classes");
	}

	public static final DynamicCodeClassLoader DYNAMIC_CODE;
	static {

		if (LOG.isInfoEnabled()) {
			LOG.info("Using context class loader: " + Thread.currentThread().getContextClassLoader());
		}

		DYNAMIC_CODE = new DynamicCodeClassLoader(Thread.currentThread().getContextClassLoader());

		/*
		 * // TODO: JFileChooser crashes with exception: ClassCircularityError JFileChooser sourceDirChooser = new
		 * JFileChooser(); sourceDirChooser.setDialogTitle("Choose Source Directory");
		 * sourceDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); int option =
		 * sourceDirChooser.showOpenDialog(null); if (option == JFileChooser.APPROVE_OPTION) { File sourceDirectory =
		 * sourceDirChooser.getSelectedFile(); if (sourceDirectory.isDirectory()) {
		 * DYNAMIC_CODE.addSourceDir(sourceDirectory); } else { DYNAMIC_CODE.addSourceDir(new File("src/main/java")); }
		 * } else { DYNAMIC_CODE.addSourceDir(new File("src/main/java")); }
		 */

		Thread.currentThread().setContextClassLoader(DYNAMIC_CODE);
		// DYNAMIC_CODE.addSourceDir(new File("extension"));
		// DYNAMIC_CODE.addSourceDir(new
		// File("../squidy-extension-basic-1.1.0-SNAPSHOT/src/main/java"));
	}

	public DynamicCodeClassLoader() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public DynamicCodeClassLoader(ClassLoader parentClassLoader) {
		this(extractClasspath(parentClassLoader), parentClassLoader);
	}

	/**
	 * @param compileClasspath used to compile dynamic classes
	 * @param parentClassLoader the parent of the class loader that loads all the dynamic classes
	 */
	public DynamicCodeClassLoader(String compileClasspath, ClassLoader parentClassLoader) {
// super(new URL[] { DYNAMIC_CODE_REPOSITORY_URL });

		if (LOG.isDebugEnabled()) {
			LOG.debug("Classpath: " + compileClasspath);
		}

		this.compileClasspath = compileClasspath;
		this.parentClassLoader = parentClassLoader;
	}

	/**
	 * Add a directory that contains the source of dynamic java code.
	 * 
	 * @param srcDir
	 * @return true if the add is successful
	 */
	public boolean addSourceDir(File srcDir) {

		try {
			srcDir = srcDir.getCanonicalFile();
		}
		catch (IOException e) {
			// ignore
		}

		synchronized (sourceDirs) {

			// check existence
			for (int i = 0; i < sourceDirs.size(); i++) {
				SourceDir src = (SourceDir) sourceDirs.get(i);
				if (src.srcDir.equals(srcDir)) {
					return false;
				}
			}

			// add new
			SourceDir src = new SourceDir(srcDir);
			sourceDirs.add(src);

			if (LOG.isInfoEnabled()) {
				LOG.info("Added source directory: " + srcDir);
			}
		}

		return true;
	}

	/**
	 * Returns the up-to-date dynamic class by name.
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException if source file not found or compilation error
	 */
	public Class<?> loadClass(String className) throws ClassNotFoundException {

		LoadedClass loadedClass = null;
		synchronized (loadedClasses) {
			loadedClass = (LoadedClass) loadedClasses.get(className);
		}

		// first access of a class
		if (loadedClass == null) {

			String resource = className.replace('.', '/') + ".java";
			SourceDir src = locateResource(resource);
			if (src == null) {
				try {
					return parentClassLoader.loadClass(className);
				}
				catch (Exception e) {
					throw new ClassNotFoundException("DynamicCode class not found " + className);
				}
			}

			synchronized (this) {

				// compile and load class
				loadedClass = new LoadedClass(className, src);

				synchronized (loadedClasses) {
					loadedClasses.put(className, loadedClass);
				}
			}

			return loadedClass.loadedClass;
		}

		// subsequent access
		if (loadedClass.isChanged()) {
			// unload and load again
			unload(loadedClass.srcDir);
			return loadClass(className);
		}

		return loadedClass.loadedClass;
	}

	private SourceDir locateResource(String resource) {
		for (int i = 0; i < sourceDirs.size(); i++) {
			SourceDir src = (SourceDir) sourceDirs.get(i);
			if (new File(src.srcDir, resource).exists()) {
				return src;
			}
		}
		return null;
	}

	private void unload(SourceDir src) {
		// clear loaded classes
		synchronized (loadedClasses) {
			for (Iterator iter = loadedClasses.values().iterator(); iter.hasNext();) {
				LoadedClass loadedClass = (LoadedClass) iter.next();
				if (loadedClass.srcDir == src) {
					iter.remove();
				}
			}
		}

		// create new class loader
		src.recreateClassLoader();
	}

	/**
	 * Get a resource from added source directories.
	 * 
	 * @param resource
	 * @return the resource URL, or null if resource not found
	 */
	public URL getResource(String resource) {
		try {

			SourceDir src = locateResource(resource);
			return src == null ? null : new File(src.srcDir, resource).toURL();

		}
		catch (MalformedURLException e) {
			// should not happen
			return null;
		}
	}

	/**
	 * Get a resource stream from added source directories.
	 * 
	 * @param resource
	 * @return the resource stream, or null if resource not found
	 */
	public InputStream getResourceAsStream(String resource) {
		try {

			SourceDir src = locateResource(resource);
			return src == null ? null : new FileInputStream(new File(src.srcDir, resource));

		}
		catch (FileNotFoundException e) {
			// should not happen
			return null;
		}
	}

	/**
	 * <code>SourceDir</code>.
	 * 
	 * <pre>
	 * Date: Aug 20, 2009
	 * Time: 9:36:53 PM
	 * </pre>
	 * 
	 * @author Roman RŠdle <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
	 * @version $Id: DynamicCodeClassLoader.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private class SourceDir {
		File srcDir;

		File binDir;

		Javac javac;

		URLClassLoader classLoader;

		SourceDir(File srcDir) {
			this.srcDir = srcDir;

			// Make directories for hot deployment compiler output
			if (!compilerOutput.mkdirs()) {
				throw new SquidyException("Could not make directories for hot deployment compiler output");
			}
			
			this.binDir = compilerOutput;// new
			
			// Make directories for hot deployment binaries
			if (!binDir.mkdirs()) {
				throw new SquidyException("Could not make directories for hot deployment binaries");
			}

			// prepare compiler
			this.javac = new Javac(compileClasspath, binDir.getAbsolutePath());

			// class loader
			recreateClassLoader();
		}

		/**
		 * Recreate class loader.
		 */
		void recreateClassLoader() {
			classLoader = (URLClassLoader) AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
				public ClassLoader run() {
					try {
						return new URLClassLoader(new URL[] { binDir.toURL() }, parentClassLoader);
					}
					catch (MalformedURLException e) {
						e.printStackTrace();
					}
					catch (SecurityException e) {
						e.printStackTrace();
					}
					return null;
				}
			});
		}
	}

	/**
	 * <code>LoadedClass</code>.
	 * 
	 * <pre>
	 * Date: Aug 20, 2009
	 * Time: 9:37:28 PM
	 * </pre>
	 * 
	 * @author Roman RŠdle <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
	 * @version $Id: DynamicCodeClassLoader.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private static class LoadedClass {
		String className;

		SourceDir srcDir;

		File srcFile;

		File binFile;

		Class loadedClass;

		long lastModified;

		LoadedClass(String className, SourceDir src) {
			this.className = className;
			this.srcDir = src;

			String path = className.replace('.', '/');
			this.srcFile = new File(src.srcDir, path + ".java");
			this.binFile = new File(src.binDir, path + ".class");

			compileAndLoadClass();
		}

		boolean isChanged() {
			return srcFile.lastModified() != lastModified;
		}

		void compileAndLoadClass() {

			if (loadedClass != null) {
				return; // class already loaded
			}

// System.out.println("SRC: " + srcFile);

			// compile, if required
			String error = null;
			if (binFile.lastModified() < srcFile.lastModified()) {
				error = srcDir.javac.compile(new File[] { srcFile });
			}

			if (error != null) {
				throw new RuntimeException("Failed to compile " + srcFile.getAbsolutePath() + ". Error: " + error);
			}

			try {
				// load class
				loadedClass = srcDir.classLoader.loadClass(className);

				// load class success, remember timestamp
				lastModified = srcFile.lastModified();

			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException("Failed to load class " + srcFile.getAbsolutePath());
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("Initialized " + loadedClass);
			}
		}
	}

	/**
	 * Extracts a classpath string from a given class loader. Recognizes only URLClassLoader.
	 */
	private static String extractClasspath(ClassLoader cl) {
		StringBuffer buf = new StringBuffer();

		while (cl != null) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("Trying to extract classpath of class loader: " + cl.getClass().getName());
			}

			if (cl instanceof URLClassLoader) {
				URL urls[] = ((URLClassLoader) cl).getURLs();
				for (int i = 0; i < urls.length; i++) {
					if (buf.length() > 0) {
						buf.append(File.pathSeparatorChar);
					}

					String path = urls[i].getFile();
					if (path.startsWith("/C:/") || path.startsWith("/c:/")) {
						path = path.substring(1, path.length());
						path = path.replace('/', '\\');
						path = path.replace("%20", " ");
					}
					buf.append(path);
				}
			}
			cl = cl.getParent();
		}

		return buf.toString();
	}
}
