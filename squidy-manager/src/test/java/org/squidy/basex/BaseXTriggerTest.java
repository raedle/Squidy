/**
 * 
 */
package org.squidy.basex;

import static org.basex.core.Text.ADMIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import org.basex.BaseXServer;
import org.basex.core.BaseXException;
import org.basex.server.ClientSession;
import org.basex.server.EventNotifier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.squidy.manager.Manager;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.model.Processable;


/**
 * <code>BaseXTriggerTest</code>.
 * 
 * <pre>
 * Date: Dec 3, 2010
 * Time: 12:15:34 PM
 * </pre>
 * 
 * 
 * @author Roman R�dle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: BaseXTriggerTest.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 * 
 */
public final class BaseXTriggerTest {

	/** BaseX server. */
	private static BaseXServer server;
	/** Client session. */
	private ClientSession mc;
	/** Control client sessions. */
	private ClientSession[] ccs = new ClientSession[4];
	
	/** Starts the server. 
	 * @throws IOException */
	@BeforeClass
	public static void start() throws IOException {
		server = new BaseXServer("-z");
	}

	/** Starts all sessions. */
	@Before
	public void startSession() {
		try {
			mc = new ClientSession(server.context, ADMIN, ADMIN);
			for (int i = 0; i < ccs.length; i++) {
				ccs[i] = new ClientSession(server.context, ADMIN, ADMIN);
			}
		} catch (final IOException ex) {
			fail(ex.toString());
		}
	}

	/** Stops all sessions. */
	@After
	public void stopSession() {
		try {
			mc.close();
			for (ClientSession s : ccs)
				s.close();
		} catch (final IOException ex) {
			fail(ex.toString());
		}
	}

	/** Stops the server. 
	 * @throws IOException */
//	@AfterClass
	public static void stop() throws IOException {
		server.stop();
	}

	/**
	 * Runs a query command and retrieves the result as string.
	 * 
	 * @throws BaseXException
	 *             command exception
	 */
	@Test
	public void command() throws IOException {
		
		final Processable p = new Processable() {
			
			public void publish(IDataContainer dataContainer) {
				
			}
			
			public void publish(IData... data) {
				
			}
			
			public IDataContainer process(IDataContainer dataContainer) {
				return null;
			}
		};

		// Create a trigger.
		mc.execute("create event " + Manager.TRIGGER_PROPERTY);

		// Attach half of the clients to the trigger.
		for (int i = ccs.length / 2; i < ccs.length; i++) {
			ccs[i].watch(Manager.TRIGGER_PROPERTY, new EventNotifier() {
				
				@Override
				public void notify(final String value) {
					assertEquals("processable=" + p.getId() + ",myProperty=myValue", new String(value));
				}
			});
		}

		// Release a trigger.
		Manager.get().propertyChanged(p, "myProperty", "myValue");

		// Detach all clients attached to trigger beforehand.
		for (int i = ccs.length / 2; i < ccs.length; i++) {
			ccs[i].unwatch(Manager.TRIGGER_PROPERTY);
		}

		// Drop a trigger.
		mc.execute("drop event " + Manager.TRIGGER_PROPERTY);
	}
}
