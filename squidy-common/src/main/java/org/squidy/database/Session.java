/**
 * 
 */
package org.squidy.database;

import org.basex.server.trigger.TriggerNotification;


/**
 * <code>Session</code>.
 * 
 * <pre>
 * Date: Dec 10, 2010
 * Time: 11:47:24 AM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: Session.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public interface Session {
	String execute(String command) throws Exception;
	void createTrigger(String name) throws Exception;
	void dropTrigger(String name) throws Exception;
	void attachTrigger(String name, TriggerNotification notification) throws Exception;
	void detachTrigger(String name) throws Exception;
	void trigger(String query, String name, String notification) throws Exception;
}
