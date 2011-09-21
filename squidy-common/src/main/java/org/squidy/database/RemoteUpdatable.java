/**
 * 
 */
package org.squidy.database;

/**
 * <code>RemoteUpdatable</code>.
 * 
 * <pre>
 * Date: Dec 6, 2010
 * Time: 8:03:14 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: RemoteUpdatable.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public interface RemoteUpdatable {
	
	public static final char KEY_VALUE_DELIMITER = '=';
	public static final char KEY_VALUE_PAIR_DELIMITER = '\n';
	public static final char SERIAL_DELIMITER = ',';
	
	String getId();
	String serialize();
	void deserialize(String serial);
}
