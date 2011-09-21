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

package test;

import javax.swing.SwingUtilities;

import org.junit.Ignore;
import org.squidy.designer.knowledgebase.NodeRepositoryShape;

import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

@Ignore
@SuppressWarnings("serial")
public class Test extends PFrame {

	public Test() {
		super("Test", false, new PSwingCanvas());
		
		SwingUtilities.invokeLater(new Runnable() {
		
			public void run() {
				PSwingCanvas canvas = (PSwingCanvas) getCanvas();
				canvas.setPanEventHandler(null);
				NodeRepositoryShape n = new NodeRepositoryShape();
				canvas.getLayer().addChild(n);
			}
		});
	}

	public static void main(String[] args) {
		new Test();
	}
}
