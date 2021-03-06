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

package org.squidy;

import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RepaintManager;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

/**
 * Demonstrates the use of PSwing in a Piccolo application.
 */

public class PSwingExample extends PFrame {

    public PSwingExample() {
        this( new PSwingCanvas() );
    }

    public PSwingExample( PCanvas aCanvas ) {
        super( "PSwingExample", false, aCanvas );
    }

    public void initialize() {
        PSwingCanvas pswingCanvas = (PSwingCanvas)getCanvas();
        PLayer l = pswingCanvas.getLayer();

//        JSlider js = new JSlider( 0, 100 );
//        js.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                System.out.println( "e = " + e );
//            }
//        } );
//        js.setBorder( BorderFactory.createTitledBorder( "Test JSlider" ) );
//        PSwing pSwing = new PSwing( js );
        
        JTable table = new JTable(new Object[][]{{"a1", "b1"}, {"a2", "b2"}}, new Object[]{"a", "b"});
        JScrollPane scrollPane = new JScrollPane(table);
        
        PSwing pSwing = new PSwing(scrollPane) {
        	
        	/**
		     * Renders to a buffered image, then draws that image to the
		     * drawing surface associated with g2 (usually the screen).
		     *
		     * @param g2 graphics context for rendering the JComponent
		     */
		    public void paint( Graphics2D g2 ) {
//		    	super.paint(g2);
		    	JComponent component = getComponent();
		        if( component.getBounds().isEmpty() ) {
		            // The component has not been initialized yet.
		            return;
		        }

		        PSwingRepaintManager manager = (PSwingRepaintManager)RepaintManager.currentManager( component );
		        manager.lockRepaint( component );

//		        Graphics2D bufferedGraphics = null;
//		        if( !isBufferValid() ) {
//		            // Get the graphics context associated with a new buffered image.
//		            // Use TYPE_INT_ARGB_PRE so that transparent components look good on Windows.
//		            buffer = new BufferedImage( component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
//		            bufferedGraphics = buffer.createGraphics();
//		        }
//		        else {
//		            // Use the graphics context associated with the existing buffered image
//		            bufferedGraphics = buffer.createGraphics();
//		            // Clear the buffered image to prevent artifacts on Macintosh
//		            bufferedGraphics.setBackground( BUFFER_BACKGROUND_COLOR );
//		            bufferedGraphics.clearRect( 0, 0, component.getWidth(), component.getHeight() );
//		        }
		//
//		      // Start with the rendering hints from the provided graphics context
//		        bufferedGraphics.setRenderingHints( g2.getRenderingHints() );
		//
//		      // PSwing sometimes causes JComponent text to render with "..." when fractional font metrics are enabled.  These are now always disabled for the offscreen buffer.
//		        bufferedGraphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );

		        // Draw the component to the buffer
		        component.paint( g2 );

		        // Draw the buffer to g2's associated drawing surface
//		        g2.drawRenderedImage( buffer, IDENTITY_TRANSFORM );

		        manager.unlockRepaint( component );
		    }
        };
        pSwing.translate( 100, 100 );
        l.addChild( pSwing );

        pswingCanvas.setPanEventHandler( null );
    }

    public static void main( String[] args ) {
        new PSwingExample();
    }
}
