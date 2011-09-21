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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.squidy.manager.ManagerException;
import org.squidy.manager.data.DataConstant;


/**
 * <code>DataUtility</code>.
 *
 * <pre>
 * Date: Jun 13, 2008
 * Time: 8:45:27 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: CloneUtility.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class CloneUtility {

	@SuppressWarnings("unchecked")
	public static <T> T getDeepClone(T clonable) {
		
            if (clonable == null) {
                return null;
            }

		if (clonable instanceof Map) {
			return (T) getDeepClone((Map) clonable);
		}
		else if (clonable instanceof DataConstant) {
			return (T) getDeepClone((DataConstant) clonable);
		}
		else if (clonable instanceof String) {
			return (T) getDeepClone((String) clonable);
		}
		else if (clonable instanceof Double) {
			return (T) getDeepClone((Double) clonable);
		}
		else if (clonable instanceof Float) {
			return (T) getDeepClone((Float) clonable);
		}
		else if (clonable instanceof Integer) {
			return (T) getDeepClone((Integer) clonable);
		}
		else if (clonable instanceof Long) {
			return (T) getDeepClone((Long) clonable);
		}
		else if (clonable instanceof Boolean) {
			return (T) getDeepClone((Boolean) clonable);
		}
		else if (clonable instanceof Vector) {
			return (T) getDeepClone((Vector) clonable);
		}
		else if (clonable instanceof Byte) {
			return (T) getDeepClone((Byte) clonable);
		}
		else {
			throw new ManagerException("Clone of " + clonable.getClass() + " currently not supported.");
		}
	}
	
	public static DataConstant getDeepClone(DataConstant clonable) {
		return DataConstant.get(clonable.getType(), clonable.getName());
	}
	
	public static String getDeepClone(String clonable) {
		return new String(clonable);
	}
	
	public static Double getDeepClone(Double clonable) {
		return new Double(clonable);
	}
	
	public static Float getDeepClone(Float clonable) {
		return new Float(clonable);
	}
	
	public static Integer getDeepClone(Integer clonable) {
		return new Integer(clonable);
	}
	
	public static Long getDeepClone(Long clonable) {
		return new Long(clonable);
	}
	
	public static Boolean getDeepClone(Boolean clonable) {
		return new Boolean(clonable);
	}
	
	public static Vector<?> getDeepClone(Vector<?> clonable) {
		return (Vector<?>) clonable.clone();
	}
	
	public static Byte getDeepClone(Byte clonable) {
		return new Byte(clonable);
	}
	
	public synchronized static Map<Object, Object> getDeepClone(Map<Object, Object> clonable) {

//		Map<Object, Object> clone = Collections.synchronizedMap(new HashMap<Object, Object>());
		Map<Object, Object> clone = new ConcurrentHashMap<Object, Object>();
		
		for (Object key : clonable.keySet()) {
			
			Object keyClone = getDeepClone(key);
			Object valueClone = getDeepClone(clonable.get(key));
			
			clone.put(keyClone, valueClone);
		}
		
		return clone;
	}
}
