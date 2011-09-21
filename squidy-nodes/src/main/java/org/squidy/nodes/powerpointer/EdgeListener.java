/**
 * 
 */
package org.squidy.nodes.powerpointer;

import java.util.EventListener;

/**
 * @author raedle
 *
 */
public interface EdgeListener extends EventListener {
	
	public static final int EDGE_TOP = 0;
	public static final int EDGE_RIGHT = 1;
	public static final int EDGE_BOTTOM = 2;
	public static final int EDGE_LEFT = 3;
	
	public void exitOnEdge(int edge);
}
