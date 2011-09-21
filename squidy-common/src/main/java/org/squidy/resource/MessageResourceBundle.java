/**
 * 
 */
package org.squidy.resource;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * <code>MessageResourceBundle</code>.
 *
 * <pre>
 * Date: Aug 9, 2010
 * Time: 9:32:06 AM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: MessageResourceBundle.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
public class MessageResourceBundle {

	protected ResourceBundle bundle;
	
	private static MessageResourceBundle instance;
	
	public static MessageResourceBundle getBundle(String baseName) {
		if (instance == null) {
			instance = new MessageResourceBundle(baseName);
		}
		return instance;
	}
	
	private MessageResourceBundle(String baseName) {
		bundle = ResourceBundle.getBundle(baseName);
	}
	
	public String getMessage(String key, Object... arguments) {
		return MessageFormat.format(bundle.getString(key), arguments);
	}
}
