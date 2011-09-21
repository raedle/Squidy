/**
 * 
 */
package org.squidy.manager.data;

/**
 * <code>IDataVisitorFactory</code>.
 * 
 * <pre>
 * Date: 11.10.2009
 * Time: 20:56:08
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
public interface IDataVisitorFactory {
	public IDataVisitor createDataVisitor();
	public String       serialize();
	public void         deserialize(String serial);
}
