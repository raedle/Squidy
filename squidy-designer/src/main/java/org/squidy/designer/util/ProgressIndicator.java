/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

package org.squidy.designer.util;

import java.awt.Graphics2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ProgressIndicator</code>.
 * 
 * <pre>
 * Date: Jul 7, 2009
 * Time: 7:40:48 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ProgressIndicator.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ProgressIndicator extends Thread {

	private PNode node;
	private PNode indicator;
	private int prog = 0;
	private char[] progStrings = new char[]{'-', '\\', '|', '/'};
	private boolean inProgress = true;
	
	public ProgressIndicator(PNode node) {
		this.node = node;
		
		indicator = new PNode() {
			protected void paint(PPaintContext paintContext) {
				Graphics2D g = (Graphics2D) paintContext.getGraphics();
				
				g.drawRect(0, 0, 100, 100);
				g.drawString("PROGRESS: " + progStrings[prog], 10, 10);
				System.out.println("PROGRESS: " + progStrings[prog]);
			}
		};
		indicator.setBounds(10, 10, 10, 10);
		node.addChild(indicator);
		start();
	}
	
	public void done() {
		inProgress = false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (inProgress) {
			
			if (++prog >= 4) {
				prog = 0;
			}
			indicator.invalidatePaint();
			
			try {
				sleep(250);
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		node.removeChild(indicator);
	}
}
