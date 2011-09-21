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


package org.squidy.nodes;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>OpenCV</code>.
 * 
 * <pre>
 * Date: Jun 12, 2009
 * Time: 5:24:59 PM
 * </pre>
 * 
 * 
 * @author Roman Raedle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: OpenCV.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "OpenCV")
@Processor(
	name = "OpenCV",
	icon = "/org/squidy/nodes/image/48x48/camera.png",
	description = "/org/squidy/nodes/html/OpenCV.html",
	types = { Processor.Type.INPUT },
	tags = {},
	status = Status.UNSTABLE
)
public class OpenCV extends AbstractNode {

	/*
	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(OpenCV.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "framerate")
	@Property(name = "Framerate", group = "Framerate")
	@Slider(minimumValue = 1, maximumValue = 160)
	private int framerate = 10;

	/**
	 * @return the framerate
	 *
	public int getFramerate() {
		return framerate;
	}

	/**
	 * @param framerate
	 *            the framerate to set
	 *
	public void setFramerate(int framerate) {
		this.framerate = framerate;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private hypermedia.video.OpenCV cv;

	// the input video stream image
	Image frame = null;
	// list of all face detected area
	Rectangle[] squares = new Rectangle[0];
	Blob[] blobs = new Blob[0];

	private JFrame window;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 *
	@Override
	public void onStart() throws ProcessException {
		super.onStart();

		cv = new hypermedia.video.OpenCV();
		cv.capture(320, 240);
		cv.cascade(hypermedia.video.OpenCV.CASCADE_FRONTALFACE_ALT);

		window = new JFrame("OpenCV") {

			/**
			 * Draw video frame and each detected faces area.
			 *
			public void paint(Graphics g) {

				// draw image
				g.drawImage(frame, 0, 0, null);

				// draw squares
				g.setColor(Color.RED);
				for (Rectangle rect : squares)
					g.drawRect(rect.x, rect.y, rect.width, rect.height);
				
				g.setColor(Color.WHITE);
			    // draw blob results
			    for( int i=0; i<blobs.length; i++ ) {
			        for( int j=0; j<blobs[i].points.length; j++ ) {
			            g.drawRect( blobs[i].points[j].x, blobs[i].points[j].y, 1, 1 );
			        }
			    }
			}
		};
		window.setBounds(100, 100, 320, 240);
		window.setBackground(Color.BLACK);
		window.setVisible(true);

		new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 *
			@Override
			public void run() {

				while (isProcessing()) {
					try {
						sleep(1000 / framerate);

						// grab image from video stream
						cv.read();
						
//						cv.threshold(80);
						
						// find blobs
//					    blobs = cv.blobs( 10, 20, 100, true, hypermedia.video.OpenCV.MAX_VERTICES*4 );

//					    publish(new DataBlob(OpenCV.class, "OpenCV Pixels", cv.pixels()));
						
						// create a new image from cv pixels data
						MemoryImageSource mis = new MemoryImageSource(cv.width, cv.height, cv.pixels(), 0, cv.width);
						frame = window.createImage(mis);

						// detect faces
						squares = cv.detect(1.2f, 2, hypermedia.video.OpenCV.HAAR_DO_CANNY_PRUNING, 20, 20);
						
						// of course, repaint
						window.repaint();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 *
	@Override
	public void onStop() throws ProcessException {
		super.onStop();

		cv.stop();
		cv = null;
		
		window.dispose();
		window = null;
	}
	*/
}
