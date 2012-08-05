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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>InputWindow</code>.
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
 * @version $Id: InputWindow.java 772 2011-09-16 15:39:44Z raedle $
 */
public class InputWindow extends JFrame {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 1655494690912997446L;
	
	private static InputWindow instance = null;
	private List<AbstractNode> registeredMouse = new ArrayList<AbstractNode>();
	private List<AbstractNode> registeredKeyboard = new ArrayList<AbstractNode>();
	
	public static final DataConstant CLICK_COUNT = DataConstant.get(Integer.class, "CLICK_COUNT");

	private InputWindow() {
		super("Squidy Input Window");
		initInputWindow();
	}

	public static InputWindow getInstance() {
		if (instance == null) {
			instance = new InputWindow();
		}
		return instance;
	}
	
	/**
	 * Register mouse listener 
	 * @param node
	 */
	public void registerMouseListener(AbstractNode node){
		if(!registeredMouse.contains(node))
		registeredMouse.add(node);
	}
	
	/**
	 * Unregister mouse listener
	 * @param node
	 */
	public void removeMouseListener(AbstractNode node){
		registeredMouse.remove(node);
		if(registeredMouse.size()==0 && registeredKeyboard.size()==0){
			closeWindow();
		}
	}
	
	/**
	 * Register key listener
	 * @param node
	 */
	public void registerKeyListener(AbstractNode node){
		if(!registeredKeyboard.contains(node))
		registeredKeyboard.add(node);
	}
	
	/**
	 * Unregister key listener
	 * @param node
	 */
	public void removeKeyListener(AbstractNode node){
		registeredKeyboard.remove(node);
		if(registeredMouse.size()==0 && registeredKeyboard.size()==0){
			closeWindow();
		}
	}

	private void initInputWindow() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(100, 100, dimension.width / 4, dimension.height / 4);
		getContentPane().setBackground(Color.WHITE);
		JTextField text = new JTextField("Squidy Input Window: Move mouse or press a key to generate input...");
		text.setEditable(false);
	    text.addKeyListener(new KeyAdapter(){

		    /**
		     * Invoked when a key has been pressed.
		     */
		    public void keyPressed(KeyEvent e) {
		    	pushSampleKey(e.getKeyCode(), true);
		    }

		    /**
		     * Invoked when a key has been released.
		     */
		    public void keyReleased(KeyEvent e) {
		    	pushSampleKey(e.getKeyCode(), false);
		    }
		});
	    getContentPane().add(text);

		addWindowListener(new WindowAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent
			 * )
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosed(e);
			}
		});
		
		text.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					pushSampleButton(1, true, e.getClickCount());
				} else {
					if (SwingUtilities.isMiddleMouseButton(e)) {
						pushSampleButton(2, true, e.getClickCount());
					} else {
						if (SwingUtilities.isRightMouseButton(e)) {
							pushSampleButton(3, true, e.getClickCount());
						}
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					pushSampleButton(1, false, e.getClickCount());
				} else {
					if (SwingUtilities.isMiddleMouseButton(e)) {
						pushSampleButton(2, false, e.getClickCount());
					} else {
						if (SwingUtilities.isRightMouseButton(e)) {
							pushSampleButton(3, false, e.getClickCount());
						}
					}
				}
			}
		});

		text.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				double x = ((double) e.getPoint().x)
						/ (double) getWidth();
				double y = ((double) e.getPoint().y)
						/ (double) getHeight();
				pushSamplePos2D(x, y);
			}

			public void mouseDragged(MouseEvent e) {
				pushSamplePos2D(((double) e.getPoint().x)
						/ (double) getWidth(), ((double) e.getPoint().y)
						/ (double) getHeight());
			}
		});

		text.addMouseWheelListener(new MouseWheelListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event
			 * .MouseWheelEvent)
			 */
			public void mouseWheelMoved(MouseWheelEvent e) {
				// currentWheelRotation += e.getWheelRotation();
				// System.out.println("CUR: " + currentWheelRotation);
				// e.consume();
			}
		});
		
		setVisible(true);
	}
	
	private void closeWindow(){
		if (instance != null) {
			setVisible(false);
			dispose();
			instance = null;
		}
	}
	
	private void pushSamplePos2D(double x, double y) {
		DataPosition2D pos = null;
		AbstractNode node = null;
		for(ListIterator<AbstractNode> it = registeredMouse.listIterator(); it.hasNext();){
			node = it.next();
	    	pos = new DataPosition2D(node.getClass(), x, y);
			node.publish(pos);
		}
	}

	private void pushSampleButton(int type, boolean flag, int clickCount) {
		DataButton button = null;
		AbstractNode node = null;
		for(ListIterator<AbstractNode> it = registeredMouse.listIterator(); it.hasNext();){
	    	node = it.next();
	    	button = new DataButton(node.getClass(), type, flag);
	    	button.setAttribute(CLICK_COUNT, clickCount);
			node.publish(button);
	    }
	}
	
	private void pushSampleKey(int type, boolean flag) {
		DataDigital digital = null;
		AbstractNode node = null;
		List<IData> datas = null; 
		for(ListIterator<AbstractNode> it = registeredKeyboard.listIterator(); it.hasNext();){
	    	node = it.next();
	    	datas = new ArrayList<IData>();
	    	digital = new DataDigital(node.getClass(), flag);
	    	digital.setAttribute(DataConstant.get(Integer.class, "KEY_EVENT"), type);
	    	datas.add(digital);
	    	node.publish(datas);
	    }
	}
}
