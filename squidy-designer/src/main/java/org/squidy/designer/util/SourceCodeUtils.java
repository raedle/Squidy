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

package org.squidy.designer.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.dynamiccode.DynamicCodeClassLoader;
import org.squidy.designer.model.NodeShape;
import org.squidy.manager.model.Processable;


/**
 * <code>SourceCodeUtils</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 11:48:51 AM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: SourceCodeUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class SourceCodeUtils {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(SourceCodeUtils.class);
	
	/**
	 * Returns true if the name is convenient to Java naming.
	 * 
	 * @param name The name that should be proofed by conventions.
	 * @return Whether the name is Java convenient or not.
	 */
	public static boolean isJavaConvenientNaming(String name) {
		char firstChar = name.charAt(0);
		
		// Name's first char must be in-between A and Z (including A and Z).
		if (!(firstChar >= 'A' && firstChar <= 'Z')) {
			return false;
		}
		
		// Name must not contain white-spaces.
		if (name.contains(" ")) {
			return false;
		}
		
		// TODO [RR]: Currently no proof for special characters like '$%&...'.
		
		return true;
	}
	
	/**
	 * @param processable
	 * @return
	 */
	public static URL getSourceCode(Processable processable) throws MalformedURLException {
		
		String processableName = processable.getClass().getName();
		processableName = processableName.replace('.', '/');

		URL sourceUrl = SourceCodeUtils.class.getClassLoader().getResource(processableName + ".java");
		if (sourceUrl == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Could not find source in dynamic code repository.");
			}
			sourceUrl = NodeShape.class.getResource(processableName + ".java");
		}
		
		if (sourceUrl == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Could not find source in classpath.");
			}
			
			File sourceFile = new File("src/main/java/" + processableName + ".java");
			
			// TODO [RR]: Change this path to a dynamic path.
			if (sourceFile == null || !sourceFile.exists()) {
				sourceFile = new File("src/main/java/" + processableName + ".java");
			}
			
			sourceUrl = sourceFile.toURL();
		}
		
		return sourceUrl;
	}
}
