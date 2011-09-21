/**
 * 
 */
package org.squidy.manager.data.impl;

import org.squidy.manager.data.IDataVisitor;
import org.squidy.manager.data.IDataVisitorFactory;

/**
 * <code>DataTestVisitorFactory</code>.
 * 
 * <pre>
 * Date: 11.10.2009
 * Time: 22:06:19
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
public class DataTestVisitorFactory implements IDataVisitorFactory {

	/* 
	 * create a new visitor
	 */
	public IDataVisitor createDataVisitor() {
		return new DataTestVisitor(this);
	}

	/* 
	 * custom deserialization for remote transport if required 
	 */
	public void deserialize(String serial) {
	}

	/* 
	 * custom serialization for remote transport if required 
	 */
	public String serialize() {
		return null;
	}

}
