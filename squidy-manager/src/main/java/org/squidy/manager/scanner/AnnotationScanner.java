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
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <code>AnnotationScanner</code>.
 * <p/>
 * Date: Feb 8, 2008
 * Time: 11:33:13 AM
 * <p/>
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: AnnotationScanner.java 772 2011-09-16 15:39:44Z raedle $$
 *
 */
public final class AnnotationScanner extends ClasspathScanner {

//    private static final Log LOG = LogFactory.getLog(AnnotationsScanner.class);

    public AnnotationScanner() {
    }

    public Map<Class, List<String>> getAnnotations(final Class<? extends Annotation> annotation,
            final Set<String> locationFilters, final Set<String> packageFilters) {

        setLocationFilters(locationFilters != null ? locationFilters : Collections.EMPTY_SET);
        setPackageFilters(packageFilters != null ? packageFilters : new HashSet<String>(Arrays
                .asList(DEFAULT_PACKAGE_FILTER)));

        Map<Class, List<String>> class2Annotation = new HashMap();

        Set<String> locationPatterns = new HashSet<String>();
        for (String locationFilter : getLocationFilters()) {
            locationPatterns.add(locationFilter.replace("*", ""));
        }

        Set<String> packagePatterns = new HashSet<String>();
        for (String packageFilter : getPackageFilters()) {
            packagePatterns.add(packageFilter.replace("*", "").replace(".", File.separator));
        }

        ClassLoader loader = getClasspath().getClassLoader();

        // If it's not a URLClassLoader, we can't deal with it!
        if (!(loader instanceof URLClassLoader)) {
//            LOG.error("The current ClassLoader is not castable to a URLClassLoader. ClassLoader is of type ["
//                    + loader.getClass().getName() + "]. Cannot scan ClassLoader for annotations of "
//                    + annotation.getClass().getName() + ".");
        } else {

            URLClassLoader urlLoader = (URLClassLoader) loader;
            URL[] urls = urlLoader.getURLs();
            for (URL url : urls) {
                String path = url.getFile();
                File location = new File(path);

                // Only process the URL if it matches one of our filter strings
                if (matchesAny(path, locationPatterns)) {
//                    LOG.trace("Checking URL '" + url + "' for annotation of " + annotation);
                    if (location.isDirectory()) {
                        class2Annotation.putAll(getAnnotationsInDirectory(loader, annotation, null, location,
                                packagePatterns));
                    }
                }
            }
        }

        return class2Annotation;
    }

    private Map<Class, List<String>> getAnnotationsInDirectory(final ClassLoader loader,
            final Class<? extends Annotation> annotation, final String parent, final File location,
            final Set<String> packagePatterns) {
        Map<Class, List<String>> class2Annotation = new HashMap();
        File[] files = location.listFiles();
        StringBuilder builder = null;

        for (File file : files) {
            builder = new StringBuilder(100);
            builder.append(parent).append(File.separator).append(file.getName());
            String packageOrClass = (parent == null ? file.getName() : builder.toString());

            if (file.isDirectory()) {
                class2Annotation.putAll(getAnnotationsInDirectory(loader, annotation, packageOrClass, file,
                        packagePatterns));
            } else if (file.getName().endsWith(".class")) {
                if (matchesAny(packageOrClass, packagePatterns)) {
                    addIfAnnotationPresent(loader, class2Annotation, annotation, packageOrClass);
                }
            }
        }

        return class2Annotation;
    }

    private void addIfAnnotationPresent(final ClassLoader loader, final Map<Class, List<String>> impls,
            final Class<? extends Annotation> iface, final String name) {
        try {
//            LOG.trace("Checking to see if class '" + name + "' has annotation " + iface);
            String externalName = name.substring(0, name.indexOf('.')).replace(File.separatorChar, '.');

            Class type = loadClass(loader, externalName);
            boolean matched = checkAllMatchers(type);
            if (type.isAnnotationPresent(iface) || matched) {
//                LOG.trace("Found: " + type);
                Annotation annotation = type.getAnnotation(iface);

                List<String> permissions = impls.get(type);
                if (permissions == null) {
                    permissions = new ArrayList();
                    impls.put(type, permissions);
                }

                String permission = null;

                // if we have an annotation use the given path otherwise the
                // package name
//                if (annotation instanceof Authorized) {
//                    permission = ((Authorized) annotation).permission();
//                } else {
//                    permission = type.getPackage().getName();
//                }
//
//                if (permission != null && permission.length() > 0) {
//                    permissions.add(permission);
//                }
            }
        } catch (Throwable t) {
//            LOG.warn("Could not examine class '" + name + "'. Reason: " + t.getMessage());
        }
    }
}
