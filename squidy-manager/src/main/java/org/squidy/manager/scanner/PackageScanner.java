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
import java.io.FilenameFilter;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.common.dynamiccode.DynamicCodeClassLoader;
import org.squidy.manager.data.Processor;


/**
 * <code>PackageScanner</code>.
 * 
 * <pre>
 *  Date: Feb 8, 2008
 *  Time: 12:29:40 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PackageScanner.java 772 2011-09-16 15:39:44Z raedle $$
 * 
 *          $Id: PackageScanner.java 772 2011-09-16 15:39:44Z raedle $
 */
public class PackageScanner {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PackageScanner.class);

	public static final Collection<Class<?>> CLASS_CACHE = new HashSet<Class<?>>();

	/**
	 * @param type
	 * @return
	 */
	public static String[] findAllClassesWithProcessorAndType(Processor.Type type) {
		
		if (CLASS_CACHE.size() == 0) {
			Class<?>[] classes = findAllClassesWithAnnotation(Processor.class);

			for (Class<?> foundType : classes) {
				CLASS_CACHE.add(foundType);
			}
		}

		List<String> returnTypes = new ArrayList<String>();

		for (Class<?> clazz : CLASS_CACHE) {
			Processor processor = (Processor) clazz.getAnnotation(Processor.class);
			for (Processor.Type ofType : processor.types()) {
				if (ofType.equals(type)) {
					returnTypes.add(clazz.getName());
				}
			}
		}

		Collections.sort(returnTypes, new Comparator<String>() {

			public int compare(String o1, String o2) {
				String type1 = o1.substring(o1.lastIndexOf('.') + 1, o1.length());
				String type2 = o2.substring(o2.lastIndexOf('.') + 1, o2.length());

				return type1.compareTo(type2);
			}
		});

		return returnTypes.toArray(new String[0]);
	}

	/**
	 * @param annotationType
	 * @return
	 */
	public static String[] findAllClassNamesWithAnnotation(Class<? extends Annotation> annotationType) {
		
		Class<?>[] foundTypes = findAllClassesWithAnnotation(annotationType);
		
		List<String> foundTypeNames = new ArrayList<String>();
		for (Class<?> foundType : foundTypes) {
			if (!foundTypeNames.contains(foundType.getName())) {
				foundTypeNames.add(foundType.getName());
			}
		}
		
		return foundTypeNames.toArray(new String[0]);
	}

	/**
	 * @param <T>
	 * @param annotationType
	 * @return
	 */
	public static <T> Class<T>[] findAllClassesWithAnnotation(Class<? extends Annotation> annotationType)
			throws SquidyException {

		List<String> foundClassNames = new ArrayList<String>();

		String packagePrefix = "org.squidy";

		String thePackagePattern = packagePrefix.replace('.', '/') + "/[/\\w]*\\.class";
		String classPath = System.getProperty("sun.boot.class.path") + System.getProperty("path.separator", ";")
				+ System.getProperty("java.class.path");

		URL[] classPathUrls = getClasspathUrls(classPath);

		for (URL url : classPathUrls) {
			File resource = urlToFile(url);

			// if (System.getProperty("os.name").contains("Windows") &&
			// resource.startsWith("/")) {
			// resource = resource.substring(1, resource.length());
			// }

			// resource = resource.replace('/', File.separatorChar);

			if (resource.getName().endsWith(".jar")) {
				foundClassNames.addAll(findAllClassesInJarContainedBy(thePackagePattern, resource));
			}
			else { // Url is Directory
				findAllClassesInDirectoryContainedBy0(foundClassNames, packagePrefix, resource);
			}
		}

		List<Class<T>> types = new ArrayList<Class<T>>();

		for (String className : foundClassNames) {
			try {
				
				Class<T> type = null;
				try {
					type = (Class<T>) PackageScanner.class.getClassLoader().loadClass(className);
				}
				catch (Exception e) {
					
//					if (LOG.isErrorEnabled()) {
//						LOG.error("Could not load class on first sight: " + e.getMessage());
//					}
					
					type = (Class<T>) PackageScanner.class.getClassLoader().loadClass(className);
				}

				if (type.isAnnotationPresent(annotationType)) {
					types.add(type);
				}
			}
			catch (ClassNotFoundException e) {
//				throw new SquidyException(e);
				if (LOG.isErrorEnabled()) {
					LOG.error("Could not load class " + e.getMessage(), e);
				}
			}
		}

		return types.toArray(new Class[types.size()]);
	}

	public static String[] findAllClassesContainedBy(String... packages) {
		List<String> classes = new ArrayList<String>();

		for (String thePackage : packages) {
			String thePackagePattern = thePackage.replace('.', '/') + "/[_$\\w]*.class";
			String classPath = System.getProperty("sun.boot.class.path") + System.getProperty("path.separator", ";")
					+ System.getProperty("java.class.path");
			URL[] classPathUrls = getClasspathUrls(classPath);

			for (URL url : classPathUrls) {
				File resource = urlToFile(url);

				// if (System.getProperty("os.name").contains("Windows") &&
				// resource.startsWith("/")) {
				// resource = resource.substring(1, resource.length());
				// }

				// resource = resource.replace('/', File.separatorChar);

				if (resource.getName().endsWith(".jar")) {
					classes.addAll(findAllClassesInJarContainedBy(thePackagePattern, resource));
				}
				else { // Url is Directory
					classes.addAll(findAllClassesInDirectoryContainedBy(thePackage, resource));
				}
			}
		}

		Collections.sort(classes);

		return classes.toArray(new String[0]);
	}

	/**
	 * @param classes
	 * @param thePackage
	 * @param file
	 */
	private static void findAllClassesInDirectoryContainedBy0(List<String> classes, String thePackage, File file) {
		try {
			if (!file.exists()) {
				return;
			}

			String thePackagePattern = thePackage.replace('.', '/');
			File thePackageFolder = new File(file, thePackagePattern);
			if (!thePackageFolder.exists()) {
				return;
			}

			String[] fileNames = thePackageFolder.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return (name.endsWith(".java") && !name.endsWith("DummyValve.java"))
							|| (name.endsWith(".class") && name.indexOf('$') == -1 && !name
									.equals("package-info.class"));
				}
			});

			String[] packageNames = thePackageFolder.list(new FilenameFilter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.io.FilenameFilter#accept(java.io.File,
				 * java.lang.String)
				 */
				public boolean accept(File dir, String name) {
					return dir.isDirectory();
				}
			});

			for (String packageName : packageNames) {
				findAllClassesInDirectoryContainedBy0(classes, thePackage + "." + packageName, file);
			}

			for (int i = 0; i < fileNames.length; i++) {
				if (fileNames[i].endsWith(".java")) {
					String fileName = fileNames[i].substring(0, fileNames[i].lastIndexOf(".java"));
					fileNames[i] = thePackage + "." + fileName.replace(".java", "");
				}
				else if (fileNames[i].endsWith(".class")) {
					String fileName = fileNames[i].substring(0, fileNames[i].lastIndexOf(".class"));
					fileNames[i] = thePackage + "." + fileName.replace(".class", "");
				}
			}

			// Avoid duplicates.
			for (String fileName : fileNames) {
				if (!classes.contains(fileName)) {
					classes.add(fileName);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param thePackage
	 * @param file
	 * @return
	 */
	private static Collection<? extends String> findAllClassesInDirectoryContainedBy(String thePackage, File file) {
		List<String> classes = new ArrayList<String>();
		try {
			if (!file.exists()) {
				return classes;
			}
			String thePackagePattern = thePackage.replace('.', '/');
			File thePackageFolder = new File(file, thePackagePattern);
			if (!thePackageFolder.exists()) {
				return classes;
			}

			String[] fileNames = thePackageFolder.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".class") && name.indexOf('$') == -1 && !name.equals("package-info.class");
				}
			});

			for (int i = 0; i < fileNames.length; i++) {
				String fileName = fileNames[i].substring(0, fileNames[i].lastIndexOf(".class"));
				fileNames[i] = thePackage + "." + fileName.replace(".class", "");
			}

			return Arrays.asList(fileNames);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * @param thePackagePattern
	 * @param file
	 * @return
	 */
	private static Collection<? extends String> findAllClassesInJarContainedBy(String thePackagePattern, File file) {
		List<String> classes = new ArrayList<String>();

		try {
			if (!file.exists()) {
				// if (LOG.isDebugEnabled()) {
				// LOG.debug("Jar file does not exist. Skipping " + file);
				// }
				return classes;
			}

			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if (jarEntry.getName().matches(thePackagePattern)) {
					String fileName = jarEntry.getName().substring(0, jarEntry.getName().lastIndexOf(".class"));

					fileName = fileName.replace('/', '.');
					classes.add(fileName);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}

	/**
	 * 
	 * @param classPath
	 * @return
	 */
	private static URL[] getClasspathUrls(String classPath) {
		String[] classPaths = classPath.split(System.getProperty("path.separator", ";"));
		URL[] classPathUrls = new URL[classPaths.length + 1];
		for (int i = 0; i < classPaths.length; i++) {
			String url = classPaths[i];
			try {
				classPathUrls[i] = new File(url).toURI().toURL();
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			classPathUrls[classPathUrls.length - 1] = DynamicCodeClassLoader.DYNAMIC_CODE_REPOSITORY.toURL();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return classPathUrls;
	}

	/**
	 * TODO: out-source me to a FileUtility or FileUtils helper class.
	 * 
	 * @param url
	 * @return
	 */
	private static File urlToFile(URL url) {
		URI uri;
		try {
			// this is the step that can fail, and so
			// it should be this step that should be fixed
			uri = url.toURI();
		}
		catch (URISyntaxException e) {
			// OK if we are here, then obviously the URL did
			// not comply with RFC 2396. This can only
			// happen if we have illegal unescaped characters.
			// If we have one unescaped character, then
			// the only automated fix we can apply, is to assume
			// all characters are unescaped.
			// If we want to construct a URI from unescaped
			// characters, then we have to use the component
			// constructors:
			try {
				uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url
						.getQuery(), url.getRef());
			}
			catch (URISyntaxException e1) {
				// The URL is broken beyond automatic repair
				throw new IllegalArgumentException("broken URL: " + url);
			}

		}
		return new File(uri);
	}
}
