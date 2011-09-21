/**
 * 
 */
package org.squidy.database;

/**
 * <code>RemoteUpdatableSessionProvider</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 3:05:40 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: RemoteUpdatableSessionProvider.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public interface RemoteUpdatableSessionProvider<T extends Session> extends SessionFactory<T> {
	public static final String REMOTE_UPDATE_TRIGGER_NAME = "remoteUpdate";
	
	void setIgnoreUpdateRemote(boolean ignore);
	void updateRemote(RemoteUpdatable update);
}
