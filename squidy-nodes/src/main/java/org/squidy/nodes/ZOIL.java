/**
 * 
 */
package org.squidy.nodes;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.bridge.BridgeCallback;
import org.squidy.manager.bridge.CSharpBridge;
import org.squidy.manager.controls.FileChooser;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>ZOIL</code>.
 *
 * <pre>
 * Date: 12.05.2010
 * Time: 10:23:36
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ZOIL.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
@XmlType(name = "ZOIL")
@Processor(
	name = "ZOIL",
	types = { Processor.Type.OUTPUT },
	tags = { "zoil" },
	status = Status.UNSTABLE
)
public class ZOIL extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "executable-path")
	@Property(
		name = "Executable path"
	)
	@FileChooser
	private String executablePath = null;
	
	/**
	 * @return the executablePath
	 */
	public final String getExecutablePath() {
		return executablePath;
	}

	/**
	 * @param executablePath the executablePath to set
	 */
	public final void setExecutablePath(String executablePath) {
		this.executablePath = executablePath;
	}

	@XmlAttribute(name = "bridge-host")
	@Property(
		name = "Bridge host"
	)
	@TextField
	private String bridgeHost = "127.0.0.1";
	
	/**
	 * @return the bridgeHost
	 */
	public final String getBridgeHost() {
		return bridgeHost;
	}

	/**
	 * @param bridgeHost the bridgeHost to set
	 */
	public final void setBridgeHost(String bridgeHost) {
		this.bridgeHost = bridgeHost;
	}

	@XmlAttribute(name = "bridge-port")
	@Property(
		name = "Bridge port"
	)
	@TextField
	private int bridgePort = 3537;

	/**
	 * @return the bridgePort
	 */
	public final int getBridgePort() {
		return bridgePort;
	}

	/**
	 * @param bridgePort the bridgePort to set
	 */
	public final void setBridgePort(int bridgePort) {
		this.bridgePort = bridgePort;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	private CSharpBridge bridge;
	
	public ZOIL() {
		bridge = new CSharpBridge();
		bridge.setCallback(new BridgeCallback() {
			
			public void opened() {
				if (!isProcessing()) {
					start();
				}
			}
			
			public void closed() {
				if (isProcessing()) {
					stop();
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		try {
			bridge.setExecutablePath(executablePath);
			bridge.setHost(bridgeHost);
			bridge.setPort(bridgePort);
			bridge.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		
		try {
			bridge.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.onStop();
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {

		try {
			bridge.publish(dataContainer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.preProcess(dataContainer);
	}
}
