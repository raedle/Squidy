/**
 * 
 */
package org.squidy.database;

import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.BaseXServer;
import org.basex.core.BaseXException;
import org.basex.server.ClientSession;
import org.basex.server.EventNotifier;
import org.squidy.Namespaces;


/**
 * <code>BaseXSessionProvider</code>.
 * 
 * <pre>
 * Date: Dec 7, 2010
 * Time: 1:57:27 PM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: BaseXSessionProvider.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 *
 */
public class BaseXSessionProvider implements RemoteUpdatableSessionProvider<BaseXSession>, EventNotifier {

	// A logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(BaseXSessionProvider.class);
	
	private static BaseXSessionProvider provider;
	
	public static BaseXSessionProvider get() {
		if (provider == null)
			provider = new BaseXSessionProvider();
		
		return provider;
	}
	
	private String host;
	
	public final String getHost() {
		return host;
	}
	
	private int port;
	
	public final int getPort() {
		return port;
	}
	
	private String user;
	
	public final String getUser() {
		return user;
	}
	
	private String pw;
	
	private String db;
	
	public final String getDatabase() {
		return db;
	}
	
	private BaseXSession session;
	
	/**
	 * 
	 */
	public BaseXSessionProvider() {
		host = System.getProperty("host", "127.0.0.1");
		port = Integer.parseInt(System.getProperty("port", "1984"));
		user = System.getProperty("user", "admin");
		pw = System.getProperty("pw", "admin");
		db = System.getProperty("db", "squidy");
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.database.SessionProvider#getSession()
	 */
	public BaseXSession getSession() {
		if (session != null)
			return session;
		
		session = createSession();
		return session;
	}

	/* (non-Javadoc)
	 * @see org.squidy.database.SessionProvider#createSession()
	 */
	public BaseXSession createSession() {
		BaseXSession session = null;
		try {
			session = new BaseXSession(host, port, user, pw);
		} catch (ConnectException e) {
			if ("Connection refused".equals(e.getMessage())) {
				if (LOG.isWarnEnabled())
					LOG.warn("Could not connect to database " + db + " [host=" + host + ",port=" + port + ",user=" + user + "][cause=" + e.getMessage() + "]");
				
				try {
					startServer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return createSession();
			}
		} catch (IOException e) {
			if (LOG.isErrorEnabled())
				LOG.error(e);
		}
		
		if (session != null)
			try {
				String res = session.execute("open " + db);
				if (LOG.isInfoEnabled())
					LOG.info(res);
				
				if (!res.isEmpty()) {
					session.execute("create db " + db + " <common:Data xmlns:common='" + Namespaces.NAMESPACE_PREFIX_COMMON + "' />");
					session.execute("open " + db);
				}
			} catch (IOException e) {
				if (LOG.isErrorEnabled())
					LOG.error(e);
			}
			
		if (session != null) {
			try {
				if (!containsTrigger(session))
					session.execute("create event " + REMOTE_UPDATE_TRIGGER_NAME);
			} catch (IOException e) {
				if (LOG.isErrorEnabled())
					LOG.error("Failed to create trigger " + REMOTE_UPDATE_TRIGGER_NAME, e);
			}
			
			try {
				session.watch(REMOTE_UPDATE_TRIGGER_NAME, this);
			} catch (IOException e) {
				if (LOG.isErrorEnabled())
					LOG.error("Failed to attach trigger " + REMOTE_UPDATE_TRIGGER_NAME, e);
			}
		}
		
		return session;
	}
	
	private boolean containsTrigger(ClientSession session) throws IOException {
		String triggers = session.execute("show events");
		
		if (!triggers.isEmpty())
			for (String trigger : triggers.split("\n")) {
				if (trigger.equals(REMOTE_UPDATE_TRIGGER_NAME))
					return true;
			}
		return false;
	}

	private void startServer() throws IOException {
		final BaseXServer server = new BaseXServer("-p " + port);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				super.run();
				try {
					server.stop();
				} catch (IOException e) {
					if (LOG.isErrorEnabled())
						LOG.error("Could not stop server", e);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.squidy.database.SessionProvider#closeSession()
	 */
	public void closeSession() {
		closeSession(session);
		session = null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.database.SessionProvider#closeSession(java.lang.Object)
	 */
	public void closeSession(BaseXSession session) {
		if (session != null) {
			try {
				session.close();
			} catch (IOException e) {
				if (LOG.isErrorEnabled())
					LOG.error("Could not close session.", e);
			}
			session = null;
		}
	}
	
	// ################################################################################
	// BEGIN REMOTE UPDATE
	// ################################################################################
	
	private boolean ignoreUpdateRemote = false;
	
	public void setIgnoreUpdateRemote(boolean ignore) {
		this.ignoreUpdateRemote = ignore;
	}

	public void updateRemote(RemoteUpdatable update) {
		String type = update.getClass().getName();
		String id = update.getId();
		String serial = update.serialize();
		
		try {
			if (!ignoreUpdateRemote)
					getSession().execute("db:event(" + REMOTE_UPDATE_TRIGGER_NAME + ", type" + RemoteUpdatable.KEY_VALUE_DELIMITER + type + RemoteUpdatable.KEY_VALUE_PAIR_DELIMITER + "id" + RemoteUpdatable.KEY_VALUE_DELIMITER + id + RemoteUpdatable.KEY_VALUE_PAIR_DELIMITER + "serial" + RemoteUpdatable.KEY_VALUE_DELIMITER + serial + ")");
		} catch (IOException e) {
			if (LOG.isErrorEnabled())
				LOG.error(e);
		}
	}

	public void notify(final String value) {
		Properties props = new Properties();
		try {
			props.load(new StringReader(value));
			
			String type = props.getProperty("type");
			String id = props.getProperty("id");
			if (id != null) {
				RemoteUpdatable up = RemoteUpdatablePool.getRemoteUpdatable(id);
				
				if (up != null) {
					ignoreUpdateRemote = true;
					up.deserialize(props.getProperty("serial"));
					ignoreUpdateRemote = false;
				}
				else if ("org.squidy.designer.model.NodeShape".equals(type)) {
					System.out.println(props.getProperty("serial"));
					
//					else if (Processable.class.isAssignableFrom(type)) {
//						// type=<processor_type>,processor=<id>,shape=<id>,layoutConstraintId=<id>,parent=<id>,x=<id>,y=<id>
//						String processorId = addStr[1].split("=")[1];
//						String id = addStr[2].split("=")[1];
//						String layoutConstraintId = addStr[3].split("=")[1];
//						String parentId = addStr[4].split("=")[1];
//						double x = Double.parseDouble(addStr[5].split("=")[1]);
//						double y = Double.parseDouble(addStr[6].split("=")[1]);
//						
//						Processable processable = ReflectionUtil.createInstance((Class<Processable>) type);
//						processable.setId(processorId);
//						
//						ContainerShape parentShape = (ContainerShape<?, ?>) ShapeUtils.getShapeWithId(Designer.getInstance().data.getWorkspaceShape(), parentId);
//						ActionShape<?, ?> shape = ShapeUtils.getActionShape(processable);
//						shape.setId(id);
//						shape.getLayoutConstraint().setId(layoutConstraintId);
//						
//						((ContainerShape<?, Piping>) shape).setProcessable((Piping) processable);
//						((ContainerShape<?, Piping>) parentShape).getProcessable().addSubProcessable(processable);
//						parentShape.addVisualShape(shape);
//						
////						shape.setOffset(x, y);
//						
//						shape.setDraggable(true);
//					}
				}
			}
		} catch (IOException e) {
			if (LOG.isErrorEnabled())
				LOG.error(e);
		}
	}
	
	// ################################################################################
	// END REMOTE UPDATE
	// ################################################################################
}
