/**
 * 
 */
package org.squidy.designer.model;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * <code>PBoundsAdapter</code>.
 * 
 * <pre>
 * Date: Jan 19, 2010
 * Time: 5:12:05 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id$
 * @since 1.1.0
 */
public class PBoundsAdapter extends XmlAdapter<String, PBounds> {

	private static final String BOUND_VALUE_DELIMITER = ",";
	
	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(PBounds v) throws Exception {
		if (v == null)
			return null;
		return v.x + BOUND_VALUE_DELIMITER + v.y + BOUND_VALUE_DELIMITER + v.width + BOUND_VALUE_DELIMITER + v.height;
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public PBounds unmarshal(String v) throws Exception {
		String[] bounds = v.split(BOUND_VALUE_DELIMITER);
		
		double x = Double.parseDouble(bounds[0]);
		double y = Double.parseDouble(bounds[1]);
		double width = Double.parseDouble(bounds[2]);
		double height = Double.parseDouble(bounds[3]);
		
		return new PBounds(x, y, width, height);
	}
}
