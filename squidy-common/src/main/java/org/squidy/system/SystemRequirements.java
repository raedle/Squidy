/**
 * 
 */
package org.squidy.system;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <code>SystemRequirement</code>.
 * 
 * <pre>
 * Date: Apr 16, 2010
 * Time: 7:36:00 PM
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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemRequirements {
	char majorVersion() default '0';
	char minorVersion() default '0';
	boolean screenMenuBar() default true;
}
