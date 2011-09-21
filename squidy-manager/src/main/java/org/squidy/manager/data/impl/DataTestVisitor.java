/**
 * 
 */
package org.squidy.manager.data.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.IProcessable;
import org.squidy.manager.data.AbstractDataVisitor;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataVisitorFactory;


/**
 * <code>DataTestVisitor</code>.
 * 
 * <pre>
 * Date: 11.10.2009
 * Time: 22:02:22
 * </pre>
 * 
 * 
 * @author
 * Toni Zeitler
 * <a href="mailto:anton.zeitler@campus.lmu.de">anton.zeitler@campus.lmu.de</a>
 * Media Informatics Group
 * University of Munich (LMU)
 * 
 * @version $Id$
 * @since 2.0.0
 */
public class DataTestVisitor extends AbstractDataVisitor {

	private static final Log LOG = LogFactory.getLog(DataTestVisitor.class);

	public DataTestVisitor(IDataVisitorFactory factory) {
		super(factory);
	}

	public void visit(IProcessable<?> processable, IData data) {
		if (data instanceof DataPosition2D) {
			DataPosition2D d2d = (DataPosition2D) data;
			LOG.debug(processable.getClass().getName() + ": x = " + String.format("%.3f", d2d.x) + " y = " + String.format("%.3f", d2d.y));
		}
	}

}
