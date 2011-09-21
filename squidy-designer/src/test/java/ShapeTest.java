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

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;

import org.junit.Ignore;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;

/**
 * 
 */

/**
 * <code>ShapeTest</code>.
 * 
 * <pre>
 * Date: Mar 14, 2009
 * Time: 10:26:03 PM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: ShapeTest.java 30 2009-09-18 22:23:35Z nitsche $
 * @since 1.0.0
 */
@Ignore
public class ShapeTest extends PFrame {

	public static void main(String[] args) {
		new ShapeTest();
	}
	
	private Shape arrowShape = new Polygon(new int[]{0, 50, 100, 50, 0, 50}, new int[]{0, 0, 75, 150, 150, 75}, 6);
	
	public ShapeTest() {
		
		PLayer layer = getCanvas().getLayer();
		
		PNode node = new PNode() {

			/* (non-Javadoc)
			 * @see edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
			 */
			@Override
			protected void paint(PPaintContext arg0) {
				super.paint(arg0);
				
				arg0.getGraphics().setColor(Color.RED);
				arg0.getGraphics().fill(arrowShape);
				arg0.getGraphics().setColor(Color.BLACK);				
				arg0.getGraphics().draw(arrowShape);
			}
		};
		
		node.setBounds(0, 0, 300, 300);
		node.setOffset(30, 50);
//		node.setScale(0.1);
		
		node.transformBy(PAffineTransform.getRotateInstance(Math.PI / 2, 50, 75));
		
		layer.addChild(node);
		
//		pack();
		setVisible(true);
	}
}
