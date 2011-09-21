/**
 * 
 */
package org.squidy.manager.bridge;

import java.io.IOException;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;


/**
 * <code>Bridge</code>.
 *
 * <pre>
 * Date: 10.05.2010
 * Time: 16:58:00
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public interface Bridge {
	public void open() throws IOException;
	public void close() throws IOException;
	public void setCallback(BridgeCallback callback);
	public void publish(IData... data) throws IOException;
	public void publish(IDataContainer dataContainer) throws IOException;
}
