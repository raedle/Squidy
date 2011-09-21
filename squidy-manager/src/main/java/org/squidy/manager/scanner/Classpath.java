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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>Classpath</code>.
 * <p/>
 * Date: Feb 8, 2008
 * Time: 11:37:53 AM
 * <p/>
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: Classpath.java 772 2011-09-16 15:39:44Z raedle $$
 *
 */
public final class Classpath {

    private List<File> elements = new ArrayList<File>();

    public Classpath() {
    }

    public Classpath(final String initial) {
        addClasspath(initial);
    }

    public boolean addComponent(final String component) {
        if ((component != null) && (component.length() > 0)) {
            try {
                File f = new File(component);
                if (f.exists()) {
                    File key = f.getCanonicalFile();
                    if (!elements.contains(key)) {
                        elements.add(key);
                        return true;
                    }
                }
            } catch (IOException e) {
                // ignored
            }
        }
        return false;
    }

    public boolean addComponent(final File component) {
        if (component != null) {
            try {
                if (component.exists()) {
                    File key = component.getCanonicalFile();
                    if (!elements.contains(key)) {
                        elements.add(key);
                        return true;
                    }
                }
            } catch (IOException e) {
                // ignored
            }
        }
        return false;
    }

    public boolean addClasspath(final String s) {
        boolean added = false;
        if (s != null) {
            StringTokenizer t = new StringTokenizer(s, File.pathSeparator);
            while (t.hasMoreTokens()) {
                added |= addComponent(t.nextToken());
            }
        }
        return added;
    }

    public String toString() {
        StringBuffer cp = new StringBuffer(1024);
        int cnt = elements.size();
        if (cnt >= 1) {
            cp.append(((elements.get(0))).getPath());
        }
        for (int i = 1; i < cnt; i++) {
            cp.append(File.pathSeparatorChar);
            cp.append(((elements.get(i))).getPath());
        }
        return cp.toString();
    }

    public ClassLoader getClassLoader() {
        int cnt = elements.size();
        URL[] urls = new URL[cnt];
        for (int i = 0; i < cnt; i++) {
            try {
                urls[i] = ((elements.get(i))).toURL();
            } catch (MalformedURLException e) {
                // ignored
            }
        }

        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = Classpath.class.getClassLoader();
        }
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        return new Loader(urls, parent);
    }

    private class Loader extends URLClassLoader {

        private String name;

        Loader(final URL[] urls, final ClassLoader parent) {
            super(urls, parent);
            name = "Loader" + Arrays.asList(urls);
        }

        public String toString() {
            return name;
        }
    }
}
