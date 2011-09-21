/**
 * 
 */
package org.squidy.nodes.powerpointer;

import java.util.EventListener;

import org.squidy.designer.component.TransparentWindow;


/**
 * @author raedle
 *
 */
public class PieMenuWindow extends TransparentWindow {
	
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -5487973037490300035L;
	
	private PieMenuComponent pieMenu;
	
	public PieMenuWindow(int diameter) {
		super(diameter, diameter);
		
		setAlwaysOnTop(true);
		centerToPointer();
		
		disposeOnMouseExit(true);
		
		pieMenu = new PieMenuComponent(diameter);
		add(pieMenu);
	}

	public void addEdgeListener(EdgeListener listener) {
		pieMenu.addEdgeListener(listener);
	}
}
