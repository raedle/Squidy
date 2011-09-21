/**
 * 
 */
package org.squidy.database;

import junit.framework.Assert;

import org.basex.server.ClientSession;
import org.junit.Before;
import org.junit.Test;
import org.squidy.database.BaseXSessionProvider;

/**
 * <code>BaseXSessionProviderTest</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 2:10:18 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: BaseXSessionProviderTest.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class BaseXSessionProviderTest {

	@Before
	public void setUp() {
		System.setProperty("host", "localhost");
		System.setProperty("port", "1985");
		System.setProperty("user", "admin");
		System.setProperty("pw", "admin");
		System.setProperty("db", "empty");
	}
	
	@Test
	public void provider() {
		BaseXSessionProvider provider = BaseXSessionProvider.get();
		
		Assert.assertNotNull("Provider was not initialized correctly.", provider);
		Assert.assertEquals("localhost", provider.getHost());
		Assert.assertEquals(1985, provider.getPort());
		Assert.assertEquals("admin", provider.getUser());
		Assert.assertEquals("empty", provider.getDatabase());
		
		Assert.assertEquals(provider, BaseXSessionProvider.get());
	}
	
	@Test
	public void session() {
		BaseXSessionProvider provider = BaseXSessionProvider.get();
		
		ClientSession session = provider.getSession();
		Assert.assertEquals(session, provider.getSession());
		Assert.assertNotSame(session, provider.createSession());
		
		provider.closeSession();
		
		Assert.assertNotSame(session, provider.getSession());
	}
}
