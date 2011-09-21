/**
 * 
 */
package org.squidy.manager.controls;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <code>FileChooser</code>.
 * 
 * <pre>
 * Date: Feb 14, 2010
 * Time: 1:00:09 AM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id$
 * @since 1.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FileChooser {
	String title() default "Choose";
}
