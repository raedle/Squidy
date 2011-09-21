/**
 * 
 */
package org.squidy.nodes;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.domainprovider.impl.GraphicsDeviceDomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.model.adapter.GraphicsDeviceAdapter;
import org.squidy.nodes.powerpointer.EdgeListener;
import org.squidy.nodes.powerpointer.PieMenuWindow;
import org.squidy.nodes.powerpointer.SlideOverview;
import org.squidy.nodes.powerpointer.SlideSelectionListener;


/**
 * <code>Powerpointer</code>.
 * 
 * <pre>
 * Date: Apr 30, 2010
 * Time: 3:50:50 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz
 *         .de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id: Powerpointer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
@XmlType(name = "Powerpointer")
@Processor(
	name = "Powerpointer",
	icon = "/org/squidy/nodes/image/48x48/powerpointer.png",
	types = { Processor.Type.OUTPUT,
	Processor.Type.FILTER },
	tags = { "laser", "pointer", "Powerpoint", "presentation" },
	status = Status.UNSTABLE
)
public class Powerpointer extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	@XmlAttribute(name = "presentation-device")
	@Property(
		name = "Determines on which device the presentation is held."
	)
	@ComboBox(domainProvider = GraphicsDeviceDomainProvider.class)
	@XmlJavaTypeAdapter(GraphicsDeviceAdapter.class)
	private GraphicsDevice presentationDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	/**
	 * @return the presentationDevice
	 */
	public GraphicsDevice getPresentationDevice() {
		return presentationDevice;
	}

	/**
	 * @param presentationDevice the presentationDevice to set
	 */
	public void setPresentationDevice(GraphicsDevice presentationDevice) {
		this.presentationDevice = presentationDevice;
		
		presentationBounds = presentationDevice.getDefaultConfiguration().getBounds();
	}

	@XmlAttribute(name = "operating-system")
	@Property(
		name = "Operating System PowerPoint Application runs on"
	)
	@ComboBox(domainProvider = OperatingSystemProvider.class)
	private String operatingSystem = "osx";

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	@XmlAttribute(name = "language")
	@Property(
		name = "Language of PowerPoint Application"
	)
	@ComboBox(domainProvider = LanguageDomainProvider.class)
	private String language = "english";
	
	/**
	 * @return the language
	 */
	public final String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public final void setLanguage(String language) {
		this.language = language;
	}
	
	@XmlAttribute(name = "left-button")
	@Property(
		name = "Set left button action"
	)
	@ComboBox(domainProvider = ActionProvider.class)
	private String leftButtonAction = ActionProvider.SLIDE_PREVIOUS;
	
	/**
	 * @return
	 */
	public String getLeftButtonAction() {
		return leftButtonAction;
	}

	/**
	 * @param leftButtonAction
	 */
	public void setLeftButtonAction(String leftButtonAction) {
		this.leftButtonAction = leftButtonAction;
	}
	
	@XmlAttribute(name = "right-button")
	@Property(
		name = "Set right button action"
	)
	@ComboBox(domainProvider = ActionProvider.class)
	private String rightButtonAction = ActionProvider.SLIDE_NEXT;

	/**
	 * @return the rightButtonAction
	 */
	public String getRightButtonAction() {
		return rightButtonAction;
	}

	/**
	 * @param rightButtonAction the rightButtonAction to set
	 */
	public void setRightButtonAction(String rightButtonAction) {
		this.rightButtonAction = rightButtonAction;
	}
	
	@XmlAttribute(name = "circle-button")
	@Property(
		name = "Set circle button action"
	)
	@ComboBox(domainProvider = ActionProvider.class)
	private String circleButtonAction = ActionProvider.DRAWING_MODE;

	/**
	 * @return the circleButtonAction
	 */
	public String getCircleButtonAction() {
		return circleButtonAction;
	}

	/**
	 * @param circleButtonAction the circleButtonAction to set
	 */
	public void setCircleButtonAction(String circleButtonAction) {
		this.circleButtonAction = circleButtonAction;
	}
	
	@XmlAttribute(name = "shake")
	@Property(
		name = "Set shake action"
	)
	@ComboBox(domainProvider = ActionProvider.class)
	private String shakeAction = ActionProvider.SHOW_PIE_MENU;

	/**
	 * @return the shakeAction
	 */
	public String getShakeAction() {
		return shakeAction;
	}

	/**
	 * @param shakeAction the shakeAction to set
	 */
	public void setShakeAction(String shakeAction) {
		this.shakeAction = shakeAction;
	}
	
	@XmlAttribute(name = "pie-menu-diameter")
	@Property(
		name = "Pie menu diameter",
		description = "The diameter of the pie menu when it is in open state.",
		suffix = "Pixel"
	)
	@Slider(
		minimumValue = 100,
		maximumValue = 1100,
		majorTicks = 250,
		minorTicks = 50,
		showLabels = true,
		showTicks = true,
		snapToTicks = true
	)
	private int pieMenuDiameter = 500;
	
	/**
	 * @return the pieMenuDiameter
	 */
	public final int getPieMenuDiameter() {
		return pieMenuDiameter;
	}

	/**
	 * @param pieMenuDiameter the pieMenuDiameter to set
	 */
	public final void setPieMenuDiameter(int pieMenuDiameter) {
		this.pieMenuDiameter = pieMenuDiameter;
	}

	@XmlAttribute(name = "pie-menu-threshold")
	@Property(
		name = "Pie menu time threshold",
		description = "The time threshold that defines after which the pie menu stays hidden.",
		suffix = "ms"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 2000,
		majorTicks = 1000,
		minorTicks = 250,
		showLabels = true,
		showTicks = true
	)
	private int pieMenuThreshold = 250;

	public int getPieMenuThreshold() {
		return pieMenuThreshold;
	}

	public void setPieMenuThreshold(int pieMenuThreshold) {
		this.pieMenuThreshold = pieMenuThreshold;
	}
	
	@XmlAttribute(name = "robot-sleep-interval")
	@Property(
		name = "Robot sleep interval",
		description = "The time interval which the robot will put to sleep after each command.",
		suffix = "ms"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 1000,
		minorTicks = 125,
		majorTicks = 500,
		showLabels = true,
		showTicks = true
	)
	private int robotSleepInterval = 50;

	public int getRobotSleepInterval() {
		return robotSleepInterval;
	}

	public void setRobotSleepInterval(int robotSleepInterval) {
		this.robotSleepInterval = robotSleepInterval;
	}

	/**
	 * <code>OperatingSystemProvider</code>.
	 * 
	 * <pre>
	 * Date: Sep 16, 2009
	 * Time: 5:35:36 PM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: Powerpointer.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class OperatingSystemProvider implements DomainProvider {

		// Operating system values.
		private static final Object[] VALUES = new Object[]{
			new ComboBoxControl.ComboBoxItemWrapper("osx", "OS X"),
			new ComboBoxControl.ComboBoxItemWrapper("windows", "Windows")
		};
		
		/* (non-Javadoc)
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			return VALUES;
		}
	}
	
	/**
	 * <code>LanguageDomainProvider</code>.
	 * 
	 * <pre>
	 * Date: Aug 24, 2009
	 * Time: 6:17:14 AM
	 * </pre>
	 * 
	 * 
	 * @author
	 * Roman RŠdle
	 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * 
	 * @version $Id: Powerpointer.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	public static class LanguageDomainProvider implements DomainProvider {

		// Language values.
		private static final Object[] VALUES = new Object[]{ 
			new ComboBoxControl.ComboBoxItemWrapper("english", "English"),
			new ComboBoxControl.ComboBoxItemWrapper("german", "German")
		};
		
		/* (non-Javadoc)
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			return VALUES;
		}
	}
	
	public static class ActionProvider implements DomainProvider {

		public static final String SHOW_PIE_MENU = "SWITCH_MOUSE_PRESENTATION";
		public static final String SLIDE_PREVIOUS = "SLIDE_PREVIOUS";
		public static final String SLIDE_NEXT = "SLIDE_NEXT";
		public static final String DRAWING_MODE = "DRAWING_MODE";
		public static final String DELETE_DRAWINGS = "DELETE_DRAWINGS";
		
		// Language values.
		private static final Object[] VALUES = new Object[]{ 
			new ComboBoxControl.ComboBoxItemWrapper(SHOW_PIE_MENU, "Show Pie Menu"),
			new ComboBoxControl.ComboBoxItemWrapper(SLIDE_PREVIOUS, "Previous Slide"),
			new ComboBoxControl.ComboBoxItemWrapper(SLIDE_NEXT, "Next Slide"),
			new ComboBoxControl.ComboBoxItemWrapper(DRAWING_MODE, "Drawing Mode"),
			new ComboBoxControl.ComboBoxItemWrapper(DELETE_DRAWINGS, "Delete Drawings")
		};
		
		public Object[] getValues() {
			return VALUES;
		}
		
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################
	
	// ################################################################################
	// BEGIN OF ILauchable
	// ################################################################################

	private File powerpointPresentation;
	
	private SlideOverview slideOverview;
	
//	private Window test;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		
//		final File file = new File("/Users/raedle/Desktop/Test/BW-Fit.ppt");
		
		new Thread() {
			
			public void run() {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {

					/* (non-Javadoc)
					 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
					 */
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(".ppt") || file.isDirectory();
					}

					/* (non-Javadoc)
					 * @see javax.swing.filechooser.FileFilter#getDescription()
					 */
					@Override
					public String getDescription() {
						return "Powerpoint Slides (*.ppt)";
					}
				});
				int option = fileChooser.showOpenDialog(null);
				
				if (option == JFileChooser.APPROVE_OPTION) {
					slideOverview = new SlideOverview(fileChooser.getSelectedFile());
					
					Rectangle bounds = presentationDevice.getDefaultConfiguration().getBounds();
					int x = (int) bounds.getCenterX() - slideOverview.getWidth() / 2;
					int y = (int) bounds.getCenterY() - slideOverview.getHeight() / 2;
					
					slideOverview.setLocation(x, y);
					
					slideOverview.addMouseListener(new MouseAdapter() {

						/* (non-Javadoc)
						 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
						 */
						@Override
						public void mouseExited(MouseEvent e) {
							super.mouseExited(e);
							
//							slideOverview.setVisible(false);
						}
					});
					slideOverview.addSlideSelectionListener(new SlideSelectionListener() {
						
						public void slideSelected(int slideNumber) {
							
							slideOverview.setVisible(false);
							
							String strNumber = "" + slideNumber;
							
							List<IData> data = new ArrayList<IData>();
							for (int i = 0, j = 0; i < strNumber.length(); i++, j += 2) {
								KeyStroke keyStroke = KeyStroke.getKeyStroke(strNumber.charAt(i), 0);
								
								DataDigital keyDown = new DataDigital(Powerpointer.class, true);
								keyDown.setAttribute(Keyboard.KEY_EVENT, keyStroke.getKeyCode());
								data.add(keyDown);
								
								DataDigital keyUp = new DataDigital(Powerpointer.class, false);
								keyUp.setAttribute(Keyboard.KEY_EVENT, keyStroke.getKeyCode());
								data.add(keyUp);
								
								System.out.println("KEYSTROKE: " + keyStroke.getKeyCode());
							}
							DataDigital keyDown = new DataDigital(Powerpointer.class, true);
							keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_ENTER);
							data.add(keyDown);
							
							DataDigital keyUp = new DataDigital(Powerpointer.class, false);
							keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_ENTER);
							data.add(keyUp);
								
							publish(data);
						}
					});
					slideOverview.setBackground(Color.LIGHT_GRAY);
				}
			}
		}.start();
		
//		test = new JWindow();
//		test.setVisible(true);
//		test.setAlwaysOnTop(true);
//		try {
//			BufferedImage image = ImageUtils.loadImageFromClasspath("/org/squidy/nodes/image/16x16/presentation_chart.png");
//			
////			AWTUtilities.setWindowShape(test, ImageUtils.getShapeOfImage(image));
////			AWTUtilities.setComponentMixingCutoutShape(test, ImageUtils.getShapeOfImage(image));
////			AWTUtilities.setWindowOpacity(test, 0.5f);
//			
//			JLabel label = new JLabel(new ImageIcon(image));
//			test.setLayout(new BorderLayout());
//			test.add(label, BorderLayout.CENTER);
////			label.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		test.setSize(new Dimension(16, 16));
//		test.setPreferredSize(new Dimension(16, 16));
		
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		//		test.setVisible(false);
		//		test.dispose();
		
		presentationBounds = null;
		if (slideOverview != null) {
			slideOverview.setVisible(false);
			slideOverview.dispose();
		}
		super.onStop();
	}
	
	// ################################################################################
	// END OF ILauchable
	// ################################################################################
	
	private boolean powerpointerMode = true;

	private boolean drawingsActive = false;
	private boolean alreadeShaken = false;
	
	private long lastTimeOfDataPosition2D = System.currentTimeMillis();
	private boolean presenterMode = true;
	
	private Rectangle presentationBounds;
	
	private boolean hasLostPresentationFocus = false;
	
	public IData process(DataPosition2D dataPosition2D) {
		
		lastTimeOfDataPosition2D = System.currentTimeMillis();
		
//		return drawingsActive ? dataPosition2D : null;
		
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point p = pointerInfo.getLocation();
//		test.setLocation((int) p.getX() + 10, (int) p.getY() + 15);
//		test.setLocation((int) p.getX() - 24, (int) p.getY() - 24);
		
//		if (bounds.contains(p)) {
//			publishNotification("Presentation Mode");
//			System.out.println("Presentation Mode");
		if (presentationBounds == null) {
			presentationBounds = presentationDevice.getDefaultConfiguration().getBounds();
		}
		presenterMode = presentationBounds.contains(p);
//		}
//		else {
//			publishNotification("Pointing Mode");
//			System.out.println("Pointing Mode");
//		}
		
		return dataPosition2D;
	}
	
	public IData process(DataButton dataButton) {
		
		if (!presenterMode && Math.abs(System.currentTimeMillis() - lastTimeOfDataPosition2D) < 50) {
			hasLostPresentationFocus = true;
			return dataButton;
		}
		
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		Point p = pointerInfo.getLocation();
		if (slideOverview != null && slideOverview.isVisible() && slideOverview.getBounds().contains(p)) {
			return dataButton;
		}

		if (hasLostPresentationFocus) {
			hasLostPresentationFocus = false;
			publish(new DataPosition2D(Powerpointer.class, 0.99, 0.99));
			publish(new DataButton(Powerpointer.class, DataButton.BUTTON_1, true));
			publish(new DataButton(Powerpointer.class, DataButton.BUTTON_1, false));
		}
		
//		System.out.println("PRES MODE");
//		return null;
		
		switch (dataButton.getButtonType()) {
		// Left button on laser pointer.
		case DataButton.BUTTON_1:
			if (powerpointerMode || leftButtonAction.equals(ActionProvider.SHOW_PIE_MENU)) {
				performAction(dataButton.getFlag(), leftButtonAction);
				return null;
			}
			break;
			// Right button on laser pointer.
		case DataButton.BUTTON_3:
			if (powerpointerMode || rightButtonAction.equals(ActionProvider.SHOW_PIE_MENU)) {
				performAction(dataButton.getFlag(), rightButtonAction);
				return null;
			}
			break;
			// Circle button on laser pointer. -> draw mode
		case DataButton.BUTTON_2:
			if (powerpointerMode || circleButtonAction.equals(ActionProvider.SHOW_PIE_MENU)) {
				performAction(dataButton.getFlag(), circleButtonAction);
				return null;
			}
			break;
		}
		return dataButton;
	}
	
	private boolean pieMenuToBeActivated = false;
	private PieMenuWindow pieMenuWindow = null;
	
	private void performAction(boolean flag, String action) {
		if (ActionProvider.SLIDE_NEXT.equals(action)) {
			if (flag) {
				DataDigital keyDown = new DataDigital(Powerpointer.class, true);
				keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
				publish(keyDown);
				
				DataDigital keyUp = new DataDigital(Powerpointer.class, false);
				keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_RIGHT);
				publish(keyUp);
			}
		}
		else if (ActionProvider.SLIDE_PREVIOUS.equals(action)) {
			if (flag) {
				DataDigital keyDown = new DataDigital(Powerpointer.class, true);
				keyDown.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
				publish(keyDown);
				
				DataDigital keyUp = new DataDigital(Powerpointer.class, false);
				keyUp.setAttribute(Keyboard.KEY_EVENT, KeyEvent.VK_LEFT);
				publish(keyUp);
			}
		}
		else if (ActionProvider.SHOW_PIE_MENU.equals(action)) {
			pieMenuToBeActivated = flag;
			
			if (pieMenuToBeActivated) {
				new Thread() {

					@Override
					public void run() {
						int time = pieMenuThreshold;
						
						while (time > 0 && pieMenuToBeActivated) {
							try {
								sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							time -= 10;
						}
						
						if (!pieMenuToBeActivated && pieMenuWindow == null) {
							pieMenuWindow = new PieMenuWindow(pieMenuDiameter);
							pieMenuWindow.addEdgeListener(new EdgeListener() {
								
								public void exitOnEdge(final int edge) {
									pieMenuWindow = null;
									
									SwingUtilities.invokeLater(new Runnable() {

										/* (non-Javadoc)
										 * @see java.lang.Runnable#run()
										 */
										public void run() {
											
											if (edge == EDGE_BOTTOM) {
												deleteDrawings();
												return;
											}
											else if (edge == EDGE_TOP) {
												if (slideOverview != null) {
													slideOverview.setVisible(true);
												}
												return;
											}
											
//											DataDigital dataDigital = new DataDigital(Powerpointer.class, true);
////											dataDigital.setAttribute(DataConstant.LED_ID, 3);
//											
//											DataDigital dataDigital2 = new DataDigital(Powerpointer.class, true);
//											dataDigital2.setAttribute(DataConstant.LED_ID, 2);
//											
//											switch (edge) {
//											case EDGE_TOP:
//												System.out.println("RED");
////												dataDigital.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_RED);
//												dataDigital2.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_RED);
//												break;
//											case EDGE_RIGHT:
//												System.out.println("BLUE");
////												dataDigital.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_BLUE);
//												dataDigital2.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_BLUE);
//												break;
//											case EDGE_BOTTOM:
//												System.out.println("GREEN");
////												dataDigital.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_GREEN);
//												dataDigital2.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_GREEN);
//												break;
//											case EDGE_LEFT:
//												System.out.println("YELLOW");
////												dataDigital.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_YELLOW);
//												dataDigital2.setAttribute(DataConstant.LED_COLOR, Laserpointer.LED_COLOR_YELLOW);
//												break;
//											}
//											
////											publish(dataDigital, dataDigital2);
//											
//											publish(dataDigital2);
										}
									});
								}
							});
							pieMenuWindow.setVisible(true);
						}
						else if (pieMenuWindow != null) {
							Toolkit.getDefaultToolkit().beep();
						}
						else {
							activateDrawings(true);
						}
					}
				}.start();
			}

			if (!flag) {
				activateDrawings(false);
			}
		}
		else if (ActionProvider.DELETE_DRAWINGS.equals(action)) {
			if (flag) {
				deleteDrawings();
			}
		}
		else if (ActionProvider.DRAWING_MODE.equals(action)) {
			activateDrawings(flag);
		}
	}
	
	/**
	 * Uncomment method if processing of data string is desired.
	 * 
	 * @param dataToken The data token object.
	 * @return Any desired data object of interface IData.
	 */
	public IData process(DataString dataString) {
		
		if ((powerpointerMode || shakeAction.equals(ActionProvider.SHOW_PIE_MENU)) && dataString.hasAttribute(ShakeRecognizer.SHAKE_EVENT)) {
			performAction(alreadeShaken = !alreadeShaken, shakeAction);
		}
		
		return dataString;
	}
	
	private void activateDrawings(boolean activatePen) {
		drawingsActive = activatePen;
		
//		System.out.println("ACTIVE: " + drawingsActive);
		
		int keyCode = getModeKeyEvent(drawingsActive);
		
		DataDigital ctrlDown = new DataDigital(Powerpointer.class, true);
		ctrlDown.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
		publish(ctrlDown);
		
		try {
			Thread.sleep(robotSleepInterval);
		}
		catch (InterruptedException e) {
			// ignore
		}

		DataDigital keyDown = new DataDigital(Powerpointer.class, true);
		keyDown.setAttribute(Keyboard.KEY_EVENT, keyCode);
		publish(keyDown);
		
		try {
			Thread.sleep(robotSleepInterval);
		}
		catch (InterruptedException e) {
			// ignore
		}

		DataDigital keyUp = new DataDigital(Powerpointer.class, false);
		keyUp.setAttribute(Keyboard.KEY_EVENT, keyCode);
		publish(keyUp);
		
		try {
			Thread.sleep(robotSleepInterval);
		}
		catch (InterruptedException e) {
			// ignore
		}

		DataDigital ctrlUp = new DataDigital(Powerpointer.class, false);
		ctrlUp.setAttribute(Keyboard.KEY_EVENT, getModeKeyModifier());
		publish(ctrlUp);

		try {
			Thread.sleep(robotSleepInterval);
		}
		catch (InterruptedException e) {
			// ignore
		}
		
		DataButton mouse = new DataButton(Powerpointer.class, DataButton.BUTTON_1, activatePen);
		publish(mouse);
	}
	
	private void deleteDrawings() {
		int eraseKeyEvent = getEraseKeyEvent();
		
		DataDigital keyDown = new DataDigital(Powerpointer.class, true);
		keyDown.setAttribute(Keyboard.KEY_EVENT, eraseKeyEvent);
		publish(keyDown);
		
//		try {
//			Thread.sleep(robotSleepInterval);
//		}
//		catch (InterruptedException e) {
//			// ignore
//		}

		DataDigital keyUp = new DataDigital(Powerpointer.class, false);
		keyUp.setAttribute(Keyboard.KEY_EVENT, eraseKeyEvent);
		publish(keyUp);
	}
	
	/**
	 * @param activatePen
	 * @return
	 */
	private int getModeKeyEvent(boolean activatePen) {
		if (activatePen) {
			if ("german".equals(language)) {
				return KeyEvent.VK_P;
			}
			return KeyEvent.VK_P;
		}
		else {
			if ("german".equals(language)) {
				return KeyEvent.VK_A;
			}
			return KeyEvent.VK_A;
		}
	}
	
	/**
	 * @return
	 */
	private int getModeKeyModifier() {
		if ("osx".equals(operatingSystem)) {
			return KeyEvent.VK_META;
		}
		else if ("windows".equals(operatingSystem)) {
			return KeyEvent.VK_CONTROL;
		}
		return -1;
	}
	
	/**
	 * @return
	 */
	private int getEraseKeyEvent() {
		if ("german".equals(language)) {
			return KeyEvent.VK_L;
		}
		return KeyEvent.VK_E;
	}
}