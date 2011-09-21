package org.squidy.designer.behavior;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.squidy.designer.Designer;
import org.squidy.designer.prefs.PreferencesManager;


/**
 * <code>DesignerTrayIconBehavior</code>.
 * 
 * <pre>
 * Date: Oct 25, 2010
 * Time: 2:30:05 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * @version $Id: Designer.java 426 2010-09-30 13:17:46Z raedle $
 * @since 1.0.0
 */
public class DesignerTrayIconBehavior implements Behavior {

	private static final String TRAY_ICON_ACTIVATED_PROPERTY = "TRAY_ICON_ACTIVATED_PROPERTY";
	
	private boolean trayIconActivated = false;
	
	private Designer designer;
	
	private TrayIcon trayIcon;
	
	public DesignerTrayIconBehavior(Designer designer) {
		this.designer = designer;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.behavior.Behavior#enable()
	 */
	public void enable() {
		if (SystemTray.isSupported()) {
			initializeSystemTray();
			
			if (PreferencesManager.getBoolean(TRAY_ICON_ACTIVATED_PROPERTY)) {
				minimizeToSystemTray();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.designer.behavior.Behavior#disable()
	 */
	public void disable() {
		
	}
	
	protected void initializeSystemTray() {
		SystemTray systemTray = SystemTray.getSystemTray();
		
		URL imageUrl = Designer.class.getResource("/tray-icon.png");
		
		PopupMenu popup = new PopupMenu();
	    MenuItem resetWindowMenuItem = new MenuItem("Reset Window Location");
	    resetWindowMenuItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Point location = new Point(0, 0);
		    	PreferencesManager.putPoint(Designer.WINDOW_LOCATION, location);
		    	designer.setLocation(location);
	    	}
	    });
	    popup.add(resetWindowMenuItem);
	    
	    popup.addSeparator();
	    
	    MenuItem exitMenuItem = new MenuItem("Exit");
	    exitMenuItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    popup.add(exitMenuItem);
		
	    try {
			trayIcon = new TrayIcon(ImageIO.read(imageUrl), "Squidy Interaction Library", popup);
			trayIcon.setImageAutoSize(true);
			
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					
					if (e.getClickCount() == 2) {
						designer.setVisible0(true);
						designer.requestFocus();
						designer.toFront();
						PreferencesManager.putBoolean(TRAY_ICON_ACTIVATED_PROPERTY, false);
					}
				}
			});
			
//		    trayIcon.addActionListener(actionListener);
//		    trayIcon.addMouseListener(mouseListener);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
	        if (trayIcon != null) {
				systemTray.add(trayIcon);
			}
	    } catch (AWTException e) {
	        System.err.println("TrayIcon could not be added.");
	    }
	    
	    designer.addWindowListener(new WindowAdapter() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.WindowAdapter#windowIconified(java.awt.event.WindowEvent)
			 */
			public void windowIconified(WindowEvent e) {
				super.windowIconified(e);
				
				minimizeToSystemTray();
			}
		});
	}
	
	/**
	 * Minimizes the designer window to the system tray.
	 */
	private void minimizeToSystemTray() {
		designer.setVisible0(false);
		
	    trayIcon.displayMessage("Squidy minimized", 
	            "Squidy has been minimized to system tray.",
	            TrayIcon.MessageType.INFO);
	    
	    PreferencesManager.putBoolean(TRAY_ICON_ACTIVATED_PROPERTY, true);
	}
}
