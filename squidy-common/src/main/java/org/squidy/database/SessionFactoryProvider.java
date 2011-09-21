/**
 * 
 */
package org.squidy.database;

/**
 * <code>SessionProviderFactory</code>.
 * 
 * <pre>
 * Date: Dec 10, 2010
 * Time: 11:48:02 AM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: SessionFactoryProvider.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class SessionFactoryProvider {

	private static SessionFactoryProvider factory;
	
	private static final SessionFactoryProvider get() {
		if (factory == null)
			factory = new SessionFactoryProvider();
		
		return factory;
	}
	
	public static final SessionFactory<? extends Session> getProvider() {
		return get().createProvider();
	}
	
	private SessionFactory<? extends Session> provider;
	
	public SessionFactory<? extends Session> createProvider() {
		if (provider == null)
//			provider = BaseXSessionProvider.get();
			provider = new LocalSessionProvider();
		
		return provider;
	}
}
