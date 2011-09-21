/**
 * 
 */
package org.squidy.designer.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JWindow;

/**
 * <code>TransparentWindow</code>.
 *
 * <pre>
 * Date: 07.05.2010
 * Time: 15:49:00
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
public abstract class TransparentWindow extends JWindow {

	protected TransparentBackground backgroundComponent = new TransparentBackground();
	
	private MouseListener disposeOnMouseExitListener;
	
	public TransparentWindow(int width, int height) {
		Dimension size = new Dimension(width, height);
		setSize(size);
		setPreferredSize(size);
		
		backgroundComponent.setBounds(0, 0, width, height);
		super.add(backgroundComponent);
	}
	
	public void disposeOnMouseExit(final boolean disposeOnMouseExit) {
		if (disposeOnMouseExit && disposeOnMouseExitListener == null) {
			disposeOnMouseExitListener = new MouseAdapter() {

				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
				 */
				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);

					if (disposeOnMouseExit) {
						setVisible(false);
						dispose();
					}
				}
			};
			addMouseListener(disposeOnMouseExitListener);
		}
		else if (!disposeOnMouseExit && disposeOnMouseExitListener == null) {
			removeMouseListener(disposeOnMouseExitListener);
			disposeOnMouseExitListener = null;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#add(java.awt.Component)
	 */
	@Override
	public Component add(Component component) {
		return backgroundComponent.add(component);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JWindow#setLayout(java.awt.LayoutManager)
	 */
	@Override
	public void setLayout(LayoutManager manager) {
		if (backgroundComponent != null) {
			backgroundComponent.setLayout(manager);
		}
		else {
			super.setLayout(manager);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setLocation(int, int)
	 */
	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		
		if (backgroundComponent != null) {
            backgroundComponent.updateBackground();
        }
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#setLocation(java.awt.Point)
	 */
	@Override
	public void setLocation(Point p) {
		super.setLocation(p);
		
		backgroundComponent.updateBackground();
	}
	
	public void centerToPointer() {
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point p = pointerInfo.getLocation();
		setLocation((int) p.getX() - (getWidth() / 2), (int) p.getY() - (getHeight() / 2));
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#pack()
	 */
	@Override
	public void pack() {
		super.pack();
		backgroundComponent.updateBackground();
	}
}
