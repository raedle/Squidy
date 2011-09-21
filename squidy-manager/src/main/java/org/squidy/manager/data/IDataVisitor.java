/**
 * 
 */
package org.squidy.manager.data;

import org.squidy.manager.IProcessable;

/**
 * <code>IDataVisitor</code>.
 * 
 * <pre>
 * Date: 11.10.2009
 * Time: 20:25:18
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
public interface IDataVisitor {
	public IDataVisitorFactory  getFactory();
	public void                 visit(IProcessable<?> processable, IData data);
	public String               serialize();
	public void                 deserialize(String serial);
}
