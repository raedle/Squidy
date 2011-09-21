/**
 * 
 */
package org.squidy.manager.bridge;

/**
 * <code>BridgeCallback</code>.
 *
 * <pre>
 * Date: 30.09.2010
 * Time: 13:10:18
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public interface BridgeCallback {
	void opened();
	void closed();
}
