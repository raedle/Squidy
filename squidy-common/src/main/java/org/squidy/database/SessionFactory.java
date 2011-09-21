/**
 * 
 */
package org.squidy.database;

/**
 * <code>SessionProvider</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 1:55:43 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: SessionFactory.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public interface SessionFactory<T extends Session> {
	T createSession();
	T getSession();
	void closeSession();
	void closeSession(T session);
}
