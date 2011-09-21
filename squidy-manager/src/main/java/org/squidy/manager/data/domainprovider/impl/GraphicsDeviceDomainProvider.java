package org.squidy.manager.data.domainprovider.impl;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.mvel2.MVEL;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.domainprovider.DomainProvider;


/**
 * <code>DisplayDomainProvider</code>.
 *
 * <pre>
 * Date: May 06, 2010
 * Time: 7:42:50 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public class GraphicsDeviceDomainProvider implements DomainProvider {

	private static final ComboBoxItemWrapper[] DEVICES;
	static {
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
		
		DEVICES = new ComboBoxItemWrapper[graphicsDevices.length];
		for (int i = 0; i < graphicsDevices.length; i++) {
			String screenName;
			try {
				screenName = "Screen " + (Integer) MVEL.getProperty("screen", graphicsDevices[i]);
			} catch (Exception e) {
				screenName = graphicsDevices[i].getIDstring();
			}
			DEVICES[i] = new ComboBoxItemWrapper(graphicsDevices[i], screenName);
		}
	}
	
	public Object[] getValues() {
		return DEVICES;
	}
}
