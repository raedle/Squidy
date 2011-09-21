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

package org.squidy.manager.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.squidy.manager.model.Node;
import org.squidy.manager.plugin.Pluggable;


/**
 * <code>Output</code>.
 * <p/>
 * Date: Feb 2, 2008 Time: 3:37:22 AM
 * <p/>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: Processor.java 772 2011-09-16 15:39:44Z raedle $$
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Processor {

	String name();

	String icon() default "";

	// String smallIcon() default "";
	String description() default "";

	String[] tags();

	/**
	 * A set of plugins that are required to utilize the processor.
	 * @return
	 */
	Class<? extends Pluggable>[] plugins() default { };

	/**
	 * @return
	 */
	Type[] types();
	
	Status status() default Status.STABLE;

	/**
	 * 
	 * @return
	 */
	Class<? extends Node>[] requires() default { };

	// Throughput[] throughputs() default {};

	/**
	 * <code>Process</code>.
	 * 
	 * <pre>
	 * Date: Apr 23, 2009
	 * Time: 11:47:19 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of
	 *                      Konstanz
	 * 
	 * @version $Id: Processor.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public @interface Process {
		Class<? extends IData> type();
	}

	/**
	 * <code>Type</code>.
	 * 
	 * <pre>
	 * Date: Apr 23, 2009
	 * Time: 11:46:58 PM
	 * </pre>
	 * 
	 * 
	 * @author Roman RŠdle <a
	 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
	 * @uni-konstanz.de</a> Human-Computer Interaction Group University of
	 *                      Konstanz
	 * 
	 * @version $Id: Processor.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	enum Type {
		INPUT, OUTPUT, FILTER, LOGIC
	}
	
	enum Status {
		STABLE, UNSTABLE
	}

	/**
	 * <code>TypeAdapter</code>.
	 * 
	 * <pre>
	 * Date: Mar 28, 2009
	 * Time: 2:48:15 PM
	 * </pre>
	 * 
	 * 
	 * @author <pre>
	 * Roman R&amp;aumldle
	 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * 
	 * @version $Id: Processor.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	class TypeAdapter extends XmlAdapter<String, Processor.Type> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object
		 * )
		 */
		@Override
		public String marshal(Type type) throws Exception {
			return type.toString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang
		 * .Object)
		 */
		@Override
		public Type unmarshal(String value) throws Exception {
			return Type.valueOf(value);
		}
	}
}
