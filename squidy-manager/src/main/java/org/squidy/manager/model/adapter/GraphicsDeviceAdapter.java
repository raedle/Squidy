package org.squidy.manager.model.adapter;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * <code>GraphicsDeviceAdapter</code>.
 * 
 * <pre>
 * Date: May 06, 2010
 * Time: 9:58:05 PM
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
 * @since 1.5.0
 */
public class GraphicsDeviceAdapter extends XmlAdapter<String, GraphicsDevice> {

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(GraphicsDevice device) throws Exception {
		if (device == null)
			return "";
		
//		if (device instanceof D3DGraphicsDevice) {
//			((D3DGraphicsDevice) device).getScreen();
//		}
		
		return device.getIDstring();
	}

	/* (non-Javadoc)
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public GraphicsDevice unmarshal(String v) throws Exception {
		GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		for (GraphicsDevice graphicsDevice : graphicsDevices) {
			if (v.equals(graphicsDevice.getIDstring())) {
				return graphicsDevice;
			}
		}
		return null;
	}
}
