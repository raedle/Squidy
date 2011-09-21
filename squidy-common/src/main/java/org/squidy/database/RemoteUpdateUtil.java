/**
 * 
 */
package org.squidy.database;

import org.mvel2.MVEL;

/**
 * <code>RemoteUpdateUtil</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 3:35:10 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: RemoteUpdateUtil.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class RemoteUpdateUtil {

	public static String createSerial(String[] keys, Object[] values) {
		assert keys.length == values.length : "count of keys are not equals count of values";
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			sb.append(keys[i]).append(RemoteUpdatable.KEY_VALUE_DELIMITER).append(values[i]);
			
			if (i < keys.length - 1)
				sb.append(RemoteUpdatable.SERIAL_DELIMITER);
		}
		
		return sb.toString();
	}
	
	public static void applySerial(Object obj, String serial) {
		String[] keyValues = serial.split(String.valueOf(RemoteUpdatable.SERIAL_DELIMITER));
		
		for (String keyValue : keyValues) {
			String[] kv = keyValue.split(String.valueOf(RemoteUpdatable.KEY_VALUE_DELIMITER));
			MVEL.setProperty(obj, kv[0], kv[1]);
		}
	}
}
