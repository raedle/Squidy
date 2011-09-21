/**
 * 
 */
package org.squidy.database;

import org.basex.server.trigger.TriggerNotification;

/**
 * <code>LocalSession</code>.
 * 
 * <pre>
 * Date: Dec 10, 2010
 * Time: 12:10:11 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: LocalSession.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class LocalSession implements Session {

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#execute(java.lang.String)
	 */
	public String execute(String command) throws Exception {
		return "";
	}

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#createTrigger(java.lang.String)
	 */
	public void createTrigger(String name) throws Exception {

	}

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#dropTrigger(java.lang.String)
	 */
	public void dropTrigger(String name) throws Exception {

	}

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#attachTrigger(java.lang.String, org.basex.server.trigger.TriggerNotification)
	 */
	public void attachTrigger(String name, TriggerNotification notification)
			throws Exception {

	}

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#detachTrigger(java.lang.String)
	 */
	public void detachTrigger(String name) throws Exception {

	}

	/* (non-Javadoc)
	 * @see org.squidy.database.Session#trigger(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void trigger(String query, String name, String notification)
			throws Exception {

	}
}
