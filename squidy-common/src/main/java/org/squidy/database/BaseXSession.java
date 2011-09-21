/**
 * 
 */
package org.squidy.database;

import java.io.IOException;

import org.basex.server.ClientSession;

/**
 * <code>BaseXSession</code>.
 * 
 * <pre>
 * Date: Dec 10, 2010
 * Time: 11:53:06 AM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: BaseXSession.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class BaseXSession extends ClientSession implements Session {

	public BaseXSession(String host, int port, String user, String pw) throws IOException {
		super(host, port, user, pw);
	}
}
