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

package org.squidy.designer;

import org.squidy.core.SplashWindow;

/**
 * <code>DesignerWithSplash</code>.
 *
 * <pre>
 * Date: Nov 19, 2008
 * Time: 1:23:57 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DesignerWithSplash.java 776 2011-09-18 21:34:48Z raedle $
 * @since 1.1.0
 */
public final class DesignerWithSplash {
	
    /**
     * Shows the splash screen, launches the application and then disposes
     * the splash screen.
     * @param args the command line arguments
     */
    public void show(String[] args) {
    	System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Squidy Interaction Library");
        SplashWindow.splash(DesignerWithSplash.class.getResource("/org/squidy/nodes/splash.png"));
        SplashWindow.invokeMain("org.squidy.designer.Designer", args);
        SplashWindow.disposeSplash();
    }
}
