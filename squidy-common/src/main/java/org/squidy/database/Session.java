/**
 * 
 */
package org.squidy.database;

import java.io.IOException;

import org.basex.server.EventNotifier;


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
	String execute(String command) throws IOException;
}
