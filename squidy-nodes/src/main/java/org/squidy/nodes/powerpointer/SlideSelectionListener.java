package org.squidy.nodes.powerpointer;

import java.util.EventListener;

/**
 * <code>SlideSelectionListener</code>.
 *
 * <pre>
 * Date: 07.05.2010
 * Time: 19:03:14
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: SlideSelectionListener.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
public interface SlideSelectionListener extends EventListener {
	public void slideSelected(int slideNumber);
}
