/**
 * 
 */
package org.squidy.database;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>RemoteUpdatablePool</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 4:19:01 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: RemoteUpdatablePool.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class RemoteUpdatablePool {

	private static RemoteUpdatablePool pool;
	
	private static final RemoteUpdatablePool get() {
		if (pool == null)
			pool = new RemoteUpdatablePool();
		
		return pool;
	}
	
	public static final RemoteUpdatable getRemoteUpdatable(/*Class<RemoteUpdatable> type, */String id) {
		RemoteUpdatablePool pool = get();
		
		return pool.remoteUpdatables.get(id);
	}
	
	public static final void putRemoteUpdatable(RemoteUpdatable remoteUpdatable) {
		RemoteUpdatablePool.get().remoteUpdatables.put(remoteUpdatable.getId(), remoteUpdatable);
	}
	
	private final Map<String, RemoteUpdatable> remoteUpdatables = new HashMap<String, RemoteUpdatable>();
}
