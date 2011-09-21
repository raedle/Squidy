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

package org.squidy.manager.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.Authenticator.RequestorType;

/**
 * <code>Plugin</code>.
 * 
 * <pre>
 * Date: Apr 21, 2009
 * Time: 10:37:37 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: Plugin.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

	String name();

	String icon() default "";

	String smallIcon() default "";

	/**
	 * <code>Interface</code>.
	 * 
	 * <pre>
	 * Date: Apr 22, 2009
	 * Time: 4:16:55 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 *         @uni-konstanz.de</a> Human-Computer Interaction Group University
	 *         of Konstanz
	 * 
	 * @version $Id: Plugin.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	@Target( { ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Interface {
		int index() default 0;
	}

	/**
	 * <code>Logic</code>.
	 * 
	 * <pre>
	 * Date: Apr 22, 2009
	 * Time: 4:16:53 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 *         @uni-konstanz.de</a> Human-Computer Interaction Group University
	 *         of Konstanz
	 * 
	 * @version $Id: Plugin.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	@Target( { ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Logic {
		Event[] events() default {};
	}

	/**
	 * <code>Event</code>.
	 * 
	 * <pre>
	 * Date: Apr 22, 2009
	 * Time: 4:16:29 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 *         @uni-konstanz.de</a> Human-Computer Interaction Group University
	 *         of Konstanz
	 * 
	 * @version $Id: Plugin.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	enum Event {
		ZOOM_IN, ZOOM_OUT
	}
}
