/**
 * 
 */
package org.squidy.database;

/**
 * <code>LocalSessionProvider</code>.
 * 
 * <pre>
 * Date: Dec 10, 2010
 * Time: 11:46:49 AM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: LocalSessionProvider.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class LocalSessionProvider implements RemoteUpdatableSessionProvider<LocalSession> {

	private LocalSession session;
	
	public LocalSession createSession() {
		return new LocalSession();
	}

	public LocalSession getSession() {
		if (session == null)
			session = new LocalSession();
		return session;
	}

	public void closeSession() {
		
	}

	public void closeSession(LocalSession session) {
		
	}

	public void updateRemote(RemoteUpdatable update) {
		
	}

	public void setIgnoreUpdateRemote(boolean ignore) {
		
	}
}
