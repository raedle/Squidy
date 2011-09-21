/**
 * 
 */
package org.squidy.manager.data;


/**
 * <code>AbstractDataVisitor</code>.
 * 
 * <pre>
 * Date: 11.10.2009
 * Time: 23:46:49
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
public abstract class AbstractDataVisitor implements IDataVisitor {

	private IDataVisitorFactory factory;
	
	public AbstractDataVisitor(IDataVisitorFactory factory) {
		super();
		this.factory = factory;
	}

	/*
	 * @return factory which produced this visitor
	 */
	public IDataVisitorFactory getFactory() {
		return factory;
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
