/**
 * 
 */
package org.squidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.BaseXServer;
import org.basex.core.BaseXException;
import org.basex.server.ClientSession;
import org.squidy.database.BaseXSessionProvider;
import org.squidy.designer.model.Data;
import org.squidy.designer.model.ModelViewHandler;


/**
 * <code>BaseXStorage</code>.
 * 
 * <pre>
 * Date: Dec 1, 2010
 * Time: 11:54:01 AM
 * </pre>
 * 
 * 
 * @author Roman RŠdle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: BaseXStorage.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
public class BaseXStorage implements Storage {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(BaseXStorage.class);
	
	private static BaseXServer server;
	
	/* (non-Javadoc)
	 * @see org.squidy.Storage#getIdentifier()
	 */
	public String getIdentifier() {
		return "BaseX <" + System.getProperty("db") + ">";
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.Storage#isAutomatedStorageActive()
	 */
	public boolean isAutomatedStorageActive() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.squidy.Storage#store(org.squidy.designer.model.Data)
	 */
	public void store(Data data) {
//		System.out.println("STORE DATA");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		ModelViewHandler.getModelViewHandler().save(out, data);
		
		String xml = out.toString();
		xml = xml.substring(55, xml.length()).trim();
		
		try {
			BaseXSessionProvider.get().getSession().execute("xquery declare namespace common='http://hci.uni-konstanz.de/squidy/common'; replace node /common:Data with " + xml);
		} catch (BaseXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.Storage#restore()
	 */
	public Data restore() {
		try {
			return ModelViewHandler.getModelViewHandler().load(queryWorkspace());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private InputStream queryWorkspace() throws Exception {

//		int option = JOptionPane.showOptionDialog(Designer.getInstance(), "Choose Database", "Choice", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, val2, val2[0]);
//		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//		workspace = val2[option].replaceAll("\"", "").trim();
//		
//		session.execute(new XQuery("declare namespace common='http://hci.uni-konstanz.de/squidy/common';"
//						+ "for $x in /common:workspaces/*[@name='" + workspace + "']"
//						+ "return $x"), out);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n".getBytes());
//		session.execute(new XQuery("declare namespace common='http://hci.uni-konstanz.de/squidy/common';" +
//				"declare namespace manager='http://hci.uni-konstanz.de/squidy/manager';" +
//				"declare namespace designer='http://hci.uni-konstanz.de/squidy/designer';" +
//				"declare namespace basic='http://hci.uni-konstanz.de/squidy/extension/basic';" +
//				"/*"), out);
		
		BaseXSessionProvider.get().getSession().execute("xquery /*", out);
		
//		WorkspaceConnectionService.get().getSession().execute(new XQuery("/*"), out);
		
//		System.out.println("ANSWER: " + out.toString());
		
		return new ByteArrayInputStream(out.toByteArray());
	}
}
