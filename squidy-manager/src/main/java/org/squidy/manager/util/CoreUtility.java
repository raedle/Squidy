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

package org.squidy.manager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataVisitor;
import org.squidy.manager.data.IDataVisitorFactory;


/**
 * <code>NumberUtility</code>.
 *
 * <pre>
 * Date: Aug 18, 2008
 * Time: 11:58:18 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: CoreUtility.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class CoreUtility {

	/**
	 * @param attributes
	 * @return
	 */
	public static String getSerialOfAttributes(Map<DataConstant, Object> attributes) {
		
		if (attributes == null) {
			return "";
		}

		StringBuilder serial = new StringBuilder("");
		for (DataConstant key : attributes.keySet()) {
			String name = key.getName();
			Class<?> type = key.getType();
			
			Object value = attributes.get(key);
			
			serial.append(name).append(":");
			serial.append(type.getName()).append(":");
			serial.append(String.valueOf(value)).append(";");
		}
		
		return serial.substring(0, serial.length() - 1);
	}
	
	/**
	 * @param serial
	 * @return
	 */
	public static Map<DataConstant, Object> getAttributesOfSerial(String serial) {
		
		// Simple case. Attributes are empty.
		if ("".equals(serial)) {
			return null;
		}
		
//		Map<DataConstant, Object> attributes = Collections.synchronizedMap(new HashMap<DataConstant, Object>());
		Map<DataConstant, Object> attributes = new ConcurrentHashMap<DataConstant, Object>();
		
		StringTokenizer attributeTokens = new StringTokenizer(serial, ";");
		while (attributeTokens.hasMoreTokens()) {
			String[] parts = attributeTokens.nextToken().split(":");
			
			// An attribute has to have 3 parts exactly.
			if (parts.length < 3) {
				continue;
			}
			
			String name = parts[0];
			Class<?> type = ReflectionUtil.loadClass(parts[1]);
			
			Object value;
			if (Integer.class.isAssignableFrom(type)) {
				value = Integer.parseInt(parts[2]);
			}
			else if (Double.class.isAssignableFrom(type)) {
				value = Double.parseDouble(parts[2]);
			}
			else if (Float.class.isAssignableFrom(type)) {
				value = Float.parseFloat(parts[2]);
			}
			else if (Long.class.isAssignableFrom(type)) {
				value = Long.parseLong(parts[2]);
			}
			else if (Byte.class.isAssignableFrom(type)) {
				value = Byte.parseByte(parts[2]);
			}
			else if (Short.class.isAssignableFrom(type)) {
				value = Short.parseShort(parts[2]);
			}
			else if (Boolean.class.isAssignableFrom(type)) {
				value = Boolean.parseBoolean(parts[2]);
			}
			else {
				value = parts[2];
			}
			
//			Object value = type.cast(parts[2]);
			
			attributes.put(DataConstant.get(type, name), value);
		}
		
		return attributes;
	}
	
	/**
	 * @param visitors
	 * @return serialized string
	 */
	public static String getSerialOfVisitors(Collection<IDataVisitor> visitors) {
		
		if (visitors == null) {
			return "";
		}
			
		StringBuilder serial = new StringBuilder("");
		for (IDataVisitor visitor : visitors) {
			serial.append(visitor.getFactory().getClass().getName()).append(":");
			serial.append(visitor.getFactory().serialize()).append(":");
			serial.append(visitor.serialize()).append(";");
		}
		
		return serial.substring(0, serial.length() - 1);
	}
	
	/**
	 * @param serial
	 * @return list from serialized string
	 */
	public static Collection<IDataVisitor> getVisitorsOfSerial(String serial) {
		
		if ("".equals(serial)) {
			return null;
		}
		
		Collection<IDataVisitor> visitors = new ArrayList<IDataVisitor>();
		
		StringTokenizer attributeTokens = new StringTokenizer(serial, ";");
		while (attributeTokens.hasMoreTokens()) {
			String[] parts = attributeTokens.nextToken().split(":");
			
			IDataVisitorFactory factory = ReflectionUtil.createInstance(parts[0]);
			if (factory != null) {
				factory.deserialize(parts[1]);
				IDataVisitor visitor = factory.createDataVisitor();
				if (visitor != null) {
					visitor.deserialize(parts[2]);
					visitors.add(visitor);
				}
			}
		}
		
		return visitors;
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean getBoolean(String value) {
		return Boolean.valueOf(value);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static int getInteger(String value) {
		return Integer.valueOf(value);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static double getDouble(String value) {
		return Double.parseDouble(value);
	}
}
