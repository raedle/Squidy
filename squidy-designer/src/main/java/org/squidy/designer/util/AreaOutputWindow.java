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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;


/**
 * <code>AreaOutputWindow</code>.
 * 
 * <pre>
 * Date: Aug 20, 2009
 * Time: 2:13:34 AM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: AreaOutputWindow.java 772 2011-09-16 15:39:44Z raedle $
 */
public class AreaOutputWindow extends JFrame {

	private static AreaOutputWindow instance = null;
	private static boolean running = false;
	private AreaDisplay areaDisplay = null;
	private Dimension display = null;
	private Dimension frame = null;
	private Long dataFlow = Long.MIN_VALUE;


	private AreaOutputWindow() {
		super("Squidy Area Display");
		running = true;
		initInputWindow();
	}

	public static AreaOutputWindow getInstance() {
		if (instance == null) {
			instance = new AreaOutputWindow();
		}
		return instance;
	}
	
	private void initInputWindow() {
		
		addComponentListener(new ComponentAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ComponentAdapter#componentResized(java.awt.event
			 * .ComponentEvent)
			 */
			@Override
			public void componentResized(ComponentEvent e) {
				frame = new Dimension(getHeight(), getWidth());
			}
		});
		
		// Send empty is alive in here!
		Thread t = new Thread() {
			
			@Override
			public void run() {
				while (running) {
					
					try {
						Thread.sleep(16);
					} catch (InterruptedException e) {
						
					}
					
					synchronized (dataFlow) {
						if (dataFlow > (System.currentTimeMillis() - 1000)) {
							
							if(areaDisplay!=null){
								areaDisplay.repaint(); 
							}
						}
					}
				}
			}
		};
		t.start();
		
		display = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new Dimension(display.width/4, display.height/4);
		setBounds(100, 100,frame.width,frame.height);
		getContentPane().setBackground(Color.WHITE);
		
		setLayout(new BorderLayout());

		areaDisplay = new AreaDisplay();
		areaDisplay.setBackground(Color.WHITE);
		add(areaDisplay, BorderLayout.CENTER);

		setVisible(true);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * 
	 *      org.squidy.manager.data.IDataContainer
	 */
	public void drawDataContainer(IDataContainer dataContainer) {

		synchronized (dataFlow) {
			dataFlow = System.currentTimeMillis();
		}

		// Extract data position 2d of current data container.
		List<DataPosition2D> dataPositions2D = DataUtility.getDataOfType(DataPosition2D.class, dataContainer);
		
		areaDisplay.setPositions(dataPositions2D);

	}
	
	public void closeWindow(){
		running = false;
		if (instance != null) {
			setVisible(false);
			dispose();
			instance = null;
		}
	}
	

	@SuppressWarnings("serial")
	class AreaDisplay extends JComponent {

		private ConcurrentLinkedQueue<DataPosition2D> pos2Ds;

		public void setPositions(List<DataPosition2D> pos2DList) {
			if(pos2Ds==null)
				pos2Ds = new ConcurrentLinkedQueue<DataPosition2D>();
			pos2Ds.addAll(pos2DList);
		}

		@Override
		protected void paintComponent(Graphics g) {
//			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			DataPosition2D pos = null;

			// g2d.setStroke(new BasicStroke(5f));

			if (pos2Ds != null) {
				g2d.setColor(Color.RED);
				
				while (!pos2Ds.isEmpty()) {

					pos = pos2Ds.poll();
					
					int x = (int) (pos.getX() * getWidth());
					int y = (int) (pos.getY() * getHeight());

//					Stroke stroke = g2d.getStroke();
					g2d.setStroke(new BasicStroke(2f));
					g2d.drawOval(x, y, 10, 10);
					g2d.drawRect(0, 0, getWidth(), getHeight());
//					g2d.setStroke(stroke);
					
//					Integer ID = (Integer) pos.getAttribute(DataConstant.SESSION_ID);
//					
//					if (ID != null) {
//						g2d.drawString(String.valueOf(ID), x, y);
//					}
				}
				pos2Ds = null;
			}
		}
	}
}
