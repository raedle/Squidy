/**
 * 
 */
package org.squidy.system;

/**
 * <code>SystemCheckUp</code>.
 * 
 * <pre>
 * Date: Apr 16, 2010
 * Time: 7:31:29 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id$
 * @since 1.1.0
 */
public class SystemCheckUp {

	public static boolean fullCheck(Class<?> check) {
		
		if (check.isAnnotationPresent(SystemRequirements.class)) {
			SystemRequirements requirement = check.getAnnotation(SystemRequirements.class);
			
			String version = System.getProperty("java.version");
			char major = version.charAt(0);
			char minor = version.charAt(2);
			
			if (major < requirement.majorVersion() || minor < requirement.minorVersion())
				return false;
			
			if (requirement.screenMenuBar() && "Mac OS X".equals(System.getProperty("os.name")))
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			
			return true;
		}
		throw new UnsupportedOperationException("Cannot check system requirements. @" + SystemRequirements.class.getSimpleName() + " is not set.");
	}
}
