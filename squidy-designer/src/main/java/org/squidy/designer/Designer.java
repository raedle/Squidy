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
package org.squidy.designer;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.basex.server.trigger.TriggerNotification;
import org.squidy.BaseXStorage;
import org.squidy.LocalJAXBStorage;
import org.squidy.SquidyException;
import org.squidy.Storage;
import org.squidy.StorageHandler;
import org.squidy.common.dynamiccode.HotDeployClassLoader;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.database.RemoteUpdatableSessionProvider;
import org.squidy.database.Session;
import org.squidy.database.SessionFactory;
import org.squidy.database.SessionFactoryProvider;
import org.squidy.designer.behavior.DesignerTrayIconBehavior;
import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.model.Data;
import org.squidy.designer.model.ModelViewHandler;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.model.PipelineShape;
import org.squidy.designer.model.WorkspaceShape;
import org.squidy.designer.prefs.PreferencesManager;
import org.squidy.designer.shape.LayoutConstraint;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.designer.zoom.ConnectorShape;
import org.squidy.designer.zoom.ContainerShape;
import org.squidy.manager.Manager;
import org.squidy.manager.heuristics.Heuristics;
import org.squidy.manager.heuristics.HeuristicsHandler;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.model.Pipeline;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;
import org.squidy.manager.model.Workspace;
import org.squidy.manager.util.PreferenceUtils;
import org.squidy.system.SystemCheckUp;
import org.squidy.system.SystemRequirements;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * <code>Prototype</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 2:30:05 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * @version $Id: Designer.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@SystemRequirements(majorVersion = '1', minorVersion = '6')
public class Designer extends PFrame implements StorageHandler {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 7924838172673313491L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Designer.class);

	public static final String WINDOW_SIZE = "DESINGER_WINDOW_SIZE";
	public static final String WINDOW_LOCATION = "DESINGER_WINDOW_LOCATION";

	static {
		HotDeployClassLoader classLoader = new HotDeployClassLoader(Thread
				.currentThread().getContextClassLoader());
		classLoader.addClassToDelegateList(AbstractNode.class.getName());
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// NativeInterface.open();
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				if (!SystemCheckUp.fullCheck(Designer.class)) {
					JOptionPane.showMessageDialog(null,
							"Please install appropriate Java version JDK 6+",
							"Wrong Java Version", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}

				Designer designer = Designer.getInstance();

				if (designer.initialized) {
					designer.requestFocus();
					return;
				}

				StorageMode storageMode = StorageMode.FILE;
				for (String arg : args) {

					if (arg.startsWith("--fullscreen") || arg.startsWith("-fs")) {
						String[] fullscreenModes = arg.split("=");
						if (fullscreenModes.length == 2) {
							designer.setFullScreenMode(Boolean
									.valueOf(fullscreenModes[1]));
						}
					} else if (arg.startsWith("--file") || arg.startsWith("-f")) {
						String[] file = arg.split("=");
						if (file.length == 2) {
							File workspace = new File(file[1]);

							if (workspace.exists()) {
								PreferencesManager
										.putFile(
												LocalJAXBStorage.DEFAULT_WORKSPACE_FILE,
												workspace);
							}
						}
					} else if (arg.startsWith("--storage-mode")
							|| arg.startsWith("-sm")) {
						String[] targetModes = arg.split("=");
						if (targetModes.length == 2) {
							storageMode = StorageMode.valueOf(targetModes[1]
									.toUpperCase());
						}
					} else if (arg.startsWith("--reset")
							|| arg.startsWith("-rs")) {
						String[] resets = arg.split("=");
						if ("workspace".equals(resets[1])) {
							PreferenceUtils
									.remove(LocalJAXBStorage.DEFAULT_WORKSPACE_FILE);
						}
					} else if (arg.startsWith("--reset-window")) {
						PreferencesManager.putDimension(WINDOW_SIZE,
								new Dimension(1228, 768));
						PreferencesManager.putPoint(WINDOW_LOCATION, new Point(
								100, 50));
					} else if (arg.startsWith("--license")
							|| arg.startsWith("-lc")) {
						String[] license = arg.split("=");
						designer.setLicense(license[1]);
					}
				}

				designer.setStorageMode(storageMode);

				designer.initialized = true;
				designer.setVisible0(true);
			}
		});

		// NativeInterface.runEventPump();

		Properties props = System.getProperties();
		System.out.println(props);
	}

	private enum StorageMode {
		FILE, BASEX, HIBERNATE, DB4O
	}

	// Singleton instance.
	private static Designer instance;

	/**
	 * @return
	 */
	public static Designer getInstance() {
		if (instance == null) {
			instance = new Designer();
		}
		return instance;
	}

	private String license = "";

	/**
	 * @return the license
	 */
	public String getLicense() {
		return license;
	}

	/**
	 * @param license
	 *            the license to set
	 */
	public void setLicense(String license) {
		this.license = license;
	}

	protected boolean initialized = false;

	private static final String VIEW_TITLE = "Squidy - Zoomable Design Environment for Multimodal User Interfaces";

	// The storage type.
	private Class<? extends Storage> storageType = LocalJAXBStorage.class;
	private Storage storage;
	private Timer timer;

	// All views on the model.
	private Collection<PFrame> views = new ArrayList<PFrame>();

	public static Heuristics heuristics;

	private Data data;

	private JCheckBoxMenuItem rendering = new JCheckBoxMenuItem(
			"Render Primitive Rectangle", true);

	// #############################################################################
	// BEGIN StorageHandler
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.Storage#getIdentifier()
	 */
	public String getIdentifier() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.Storage#isAutomatedStorageActive()
	 */
	public boolean isAutomatedStorageActive() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.Storage#store(org.squidy.designer.model.Data)
	 */
	public void store(Data data) {

		if (data == null) {
			return;
		}

		if (LOG.isDebugEnabled()) {
			// LOG.debug("Store data.");
		}

		if (storage == null) {
			
			// Stop automated storage timer.
			timer.cancel();
			
			storage = ReflectionUtil.createInstance(storageType);
			
			if (storage.isAutomatedStorageActive()) {
				initiateAutomatedStorage();
			}
		}
		storage.store(data);

		for (PFrame view : views) {
			view.setTitle(VIEW_TITLE + " :: " + storage.getIdentifier());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.Storage#restore()
	 */
	public Data restore() {
		if (storage == null) {
			storage = ReflectionUtil.createInstance(storageType);
		}
		Data data;
		try {
			data = storage.restore();
		} catch (SquidyException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Loading default pipeline.", e);
			}
			return createDefaultWorkspace();
		}

		for (PFrame view : views) {
			view.setTitle(VIEW_TITLE + " :: " + storage.getIdentifier());
		}

		return data;
	}

	// #############################################################################
	// END StorageHandler
	// #############################################################################

	/**
	 * 
	 */
	private Designer() {
		super("", false, new PSwingCanvas());
		
		try {
			setIconImage(ImageIO.read(Designer.class.getResource("/tray-icon.png")));
		} catch (Exception e1) {
			System.err.println("Could not find tray icon.");
		}
		
		views.add(this);
		initializeView(this);
		
		initializeSession();

		// getCanvas().setZoomEventHandler(null);

		// Doesn't work properly!
		// getCanvas().addInputEventListener(new PBasicInputEventHandler() {
		// @Override
		// public void mouseWheelRotated(PInputEvent event) {
		// super.mouseWheelRotated(event);
		//
		// System.out.println("ROT: " + event.getWheelRotation());
		//
		// PCamera camera = event.getCamera();
		// double cameraScale = camera.getScale();
		//
		// getCanvas().getCamera().scaleAboutPoint((double)
		// event.getWheelRotation() / 15 + 1, event.getPosition());
		// }
		//
		// @Override
		// public void mouseWheelRotatedByBlock(PInputEvent event) {
		// super.mouseWheelRotatedByBlock(event);
		//
		// System.out.println("ROT BLOCK");
		// }
		// });

		getCanvas().setPanEventHandler(null);

		try {
			heuristics = HeuristicsHandler.getHeuristicsHandler().load(
					Designer.class.getResourceAsStream("/heuristics.xml"));
		} catch (SquidyException e) {
			e.printStackTrace();
		}

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
				super.componentResized(e);
				zoomToZoomedShape(data);
			}
		});

		// Initiate the storage timer.
		initiateAutomatedStorage();

		// setSize(800, 600);
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// setLocation((int) (screenSize.getWidth() / 2) - getWidth() / 2, (int)
		// (screenSize.getHeight() / 2)
		// - getHeight() / 2);

		addComponentListener(new ComponentAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ComponentListener#componentResized(java.awt.event
			 * .ComponentEvent)
			 */
			public void componentResized(ComponentEvent e) {
				PreferencesManager.putDimension(WINDOW_SIZE,
						Designer.this.getSize());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ComponentAdapter#componentMoved(java.awt.event
			 * .ComponentEvent)
			 */
			@Override
			public void componentMoved(ComponentEvent e) {
				PreferencesManager.putPoint(WINDOW_LOCATION,
						Designer.this.getLocation());
			}
		});

		initMenuBar();

		Dimension size = PreferencesManager.getDimension(WINDOW_SIZE,
				new Dimension(1228, 768));
		setSize(size);

		Point location = PreferencesManager.getPoint(WINDOW_LOCATION,
				new Point(100, 50));
		setLocation(location);
		
		// Enables the tray icon behavior for the designer window.
		new DesignerTrayIconBehavior(this).enable();
	}
	
	/**
	 * This method does ignore calls, which cause visibility of designer window
	 * although the window has not completed launching.
	 */
	@Override
	public void setVisible(boolean b) {
		// ignore
	}

	/**
	 * The set visible redirects visible calls to set window visible.
	 * 
	 * @param b
	 *            Whether the window is visible or not.
	 */
	public void setVisible0(boolean b) {
		super.setVisible(b);
	}

	/**
	 * Initializes menu bar.
	 */
	private void initMenuBar() {

		JMenuBar menuBar = new JMenuBar();

		JMenu workspace = new JMenu("Workspace");
		workspace.add(new AbstractAction("Open from...") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileHidingEnabled(true);
				fileChooser.setFileFilter(new FileFilter() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * javax.swing.filechooser.FileFilter#accept(java.io.File)
					 */
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".sdy");
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see javax.swing.filechooser.FileFilter#getDescription()
					 */
					@Override
					public String getDescription() {
						return "Squidy Workspace";
					}
				});
				int option = fileChooser.showOpenDialog(Designer.this);

				if (option == JFileChooser.APPROVE_OPTION) {

					// Stop replacing workspace if currently running.
					if (data != null) {
						data.getWorkspace().stop();
					}

					File workspaceFile = fileChooser.getSelectedFile();

					if (storage instanceof LocalJAXBStorage) {
						((LocalJAXBStorage) storage)
								.setWorkspaceFile(workspaceFile);
						load();
					} else {
						try {
							data = ModelViewHandler.getModelViewHandler().load(
									new FileInputStream(workspaceFile));

							WorkspaceShape workspace = data.getWorkspaceShape();
							workspace.setModel(data);
							workspace.setStorageHandler(Designer.this);
							workspace.initialize();
							LayoutConstraint lc = workspace
									.getLayoutConstraint();
							workspace.setScale(lc.getScale());
							workspace.setOffset(lc.getX(), lc.getY());

							getCanvas().getLayer().addChild(workspace);

							zoomToZoomedShape(data);
						} catch (SquidyException e1) {
							e1.printStackTrace();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		workspace.add(new AbstractAction("Export as...") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.ActionListener#actionPerformed(java.awt.event.
			 * ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileHidingEnabled(true);
				fileChooser.setFileFilter(new FileFilter() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * javax.swing.filechooser.FileFilter#accept(java.io.File)
					 */
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".sdy");
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see javax.swing.filechooser.FileFilter#getDescription()
					 */
					@Override
					public String getDescription() {
						return "Squidy Workspace";
					}
				});

				if (storage instanceof LocalJAXBStorage) {
					fileChooser.setSelectedFile(((LocalJAXBStorage) storage)
							.getWorkspaceFile());
				}

				int option = fileChooser.showSaveDialog(Designer.this);

				if (option == JFileChooser.APPROVE_OPTION) {

					File workspaceFile = fileChooser.getSelectedFile();

					try {
						ModelViewHandler.getModelViewHandler().save(
								new FileOutputStream(workspaceFile), data);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		JMenu options = new JMenu("Options");
		rendering.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				VisualShape.setRenderPrimitiveRect(rendering.isSelected());
				data.setRenderPrimitiveRect(rendering.isSelected());
				storage.store(data);
				repaint();
			}
		});
		options.add(rendering);
		
		JMenu storage = new JMenu("Storage");

		ButtonGroup group = new ButtonGroup();

		final JRadioButtonMenuItem storageLocalJAXB = new JRadioButtonMenuItem("Local JAXB");
		storageLocalJAXB.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (storageLocalJAXB.isSelected()) {
					setStorageMode(StorageMode.FILE);
				}
			}
		});
		storage.add(storageLocalJAXB);
		group.add(storageLocalJAXB);
		
		final JRadioButtonMenuItem storageBaseX = new JRadioButtonMenuItem("BaseX");
		storageBaseX.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (storageBaseX.isSelected()) {
					setStorageMode(StorageMode.BASEX);
				}
			}
		});
		storageBaseX.setSelected(storageType.equals(BaseXStorage.class));
		
		storage.add(storageBaseX);
		group.add(storageBaseX);
		options.add(storage);

		menuBar.add(workspace);
		menuBar.add(options);

		setJMenuBar(menuBar);
	}

	/**
	 * @param data
	 */
	private void zoomToZoomedShape(Data data) {
		if (data != null) {

			if (data.getZoomedShape() != null) {
				data.getZoomedShape().animateToCenterView(
						getCanvas().getCamera());
				return;
			}

			PBounds zoomedBounds = data.getZoomedBounds();
			if (zoomedBounds != null) {
				getCanvas().getCamera().animateViewToCenterBounds(zoomedBounds,
						true, 500);
			} else if (data.getWorkspaceShape() != null) {
				data.getWorkspaceShape().animateToCenterView(
						getCanvas().getCamera());
			}
		}
	}

	/**
	 * @param storageMode
	 */
	public void setStorageMode(StorageMode storageMode) {

		// Unset current storage.
		storage = null;
		
		switch (storageMode) {
		case FILE:
			storageType = LocalJAXBStorage.class;
			saveAndUnload();
			load();
			break;
		case BASEX:
			storageType = BaseXStorage.class;
			saveAndUnload();
			load();
			break;
		case HIBERNATE:
			break;
		case DB4O:
			break;
		}
	}

	/**
	 * 
	 */
	public void saveAndUnload() {
		// TODO [RR]: If MVC will be used use controller instead of shape.
		if (data != null) {
			store(data);
			data.getWorkspaceShape().delete();
		}
	}

	/**
	 * 
	 */
	public void load() {
		data = restore();

		if (data == null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Couldn't load workspace. Loading default workspace instead (-Ddefault.workspace=<location>)");
			}

			String defaultWorkspace = System.getProperty("default.workspace");
			if (defaultWorkspace != null) {

				if (LOG.isInfoEnabled()) {
					LOG.info("Loading default workspace at " + defaultWorkspace);
				}

				InputStream inputStream = Designer.class
						.getResourceAsStream(defaultWorkspace);
				data = ModelViewHandler.getModelViewHandler().load(inputStream);
			} else {
				data = createDefaultWorkspace();
			}
		}
		
		if (data.getWorkspace() == null || data.getWorkspaceShape() == null)
			data = createDefaultWorkspace();

		// Initialize data.
		Manager.get(data);
		
		rendering.setSelected(data.isRenderPrimitiveRect());

		WorkspaceShape workspace = data.getWorkspaceShape();
		workspace.setModel(data);
		workspace.setStorageHandler(this);
		workspace.initialize();
		LayoutConstraint lc = workspace.getLayoutConstraint();
		workspace.setScale(lc.getScale());
		workspace.setOffset(lc.getX(), lc.getY());

		getCanvas().getLayer().addChild(workspace);

		zoomToZoomedShape(data);
	}

	/**
	 * @param view
	 */
	private void initializeView(PFrame view) {
		view.setTitle(VIEW_TITLE);
		view.setDefaultCloseOperation(PFrame.DO_NOTHING_ON_CLOSE);

		view.addWindowListener(new WindowAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt
			 * .event.WindowEvent)
			 */
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);

				// Remove view.
				PFrame view = (PFrame) e.getWindow();
				views.remove(view);
				view.dispose();

				if (LOG.isDebugEnabled()) {
					LOG.debug("Closing view. View count " + views.size());
				}

				// Store data if last view has been closed.
				if (views.size() == 0) {
					store(data);
					System.exit(0);
				}
			}
		});

		view.getCanvas().addInputEventListener(new PBasicInputEventHandler() {
			@Override
			public void mouseClicked(PInputEvent event) {
				super.mouseClicked(event);

				final PCamera camera = event.getCamera();

				if (!event.isHandled() && event.isLeftMouseButton()
						&& event.getClickCount() == 2) {

					Rectangle bounds = getCanvas().getBounds();
					bounds.setBounds((int) (bounds.getX() - 30),
							((int) bounds.getY() - 30),
							((int) bounds.getWidth() + 30),
							((int) bounds.getHeight() + 30));

					camera.animateViewToCenterBounds(bounds, true, 1000);

					// Set all children of current node as draggable.
					for (Object child : getCanvas().getLayer()
							.getChildrenReference()) {
						if (child instanceof Draggable) {
							((Draggable) child).setDraggable(true);
						}
					}
				}
			}
		});

		view.getCanvas().getCamera()
				.addInputEventListener(new PBasicInputEventHandler() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseClicked
					 * (edu.umd.cs.piccolo.event.PInputEvent)
					 */
					@Override
					public void mouseClicked(PInputEvent event) {
						super.mouseClicked(event);

						if (event.isRightMouseButton()
								&& event.getClickCount() > 1) {

							PLayer layer = getCanvas().getLayer();

							PCamera camera = new PCamera();
							camera.addLayer(layer);
							layer.getRoot().addChild(camera);

							PCanvas canvas = new PSwingCanvas();
							canvas.setCamera(camera);

							PFrame view = new PFrame("", false, canvas);
							views.add(view);
							initializeView(view);
							// view.setVisible(true);

							if (LOG.isDebugEnabled()) {
								LOG.debug("Created view. View count "
										+ views.size());
							}
						}
					}
				});
	}

	/**
	 * 
	 */
	private void initiateAutomatedStorage() {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				store(data);
			}
		}, 30000, 30000);
	}

	/**
	 * 
	 */
	private Data createDefaultWorkspace() {
		data = new Data();

		// WORKSPACE
		WorkspaceShape workspaceShape = new WorkspaceShape("HCI Group");
		Workspace workspace = new Workspace();
		workspaceShape.setProcessable(workspace);

		// LAYOUTING WORKSPACE
		getCanvas().getLayer().addChild(workspaceShape);
		workspaceShape.offset(50, 50);
		workspaceShape.scale(1.0);

		// ADDING WORKSPACE
		data.setWorkspaceShape(workspaceShape);
		data.setWorkspace(workspace);
		workspaceShape.setModel(data);
		workspaceShape.setStorageHandler(this);

		// PROJECT #1
		PipelineShape pipelineShape1 = new PipelineShape("Media Room");
		Pipeline pipeline0 = new Pipeline();
		pipelineShape1.setProcessable(pipeline0);

		// LAYOUTING PROJECT #1
		LayoutConstraint lc = pipelineShape1.getLayoutConstraint();
		lc.setX(40);
		lc.setY(40);
		lc.setScale(0.1);

		// ADDING PROJECT #1
		workspaceShape.addVisualShape(pipelineShape1);
		workspace.addSubProcessable(pipeline0);

		// // PIPELINE #1
		// PipelineShape pipelineShape2 = new PipelineShape("iPhone to TUIO");
		// Pipeline pipeline1 = pipelineShape2.getProcessable();
		//
		// // LAYOUTING PIPELINE #1
		// LayoutConstraint lc1 = pipelineShape2.getLayoutConstraint();
		// lc1.setX(40);
		// lc1.setY(40);
		// lc1.setScale(0.1);
		//
		// // ADDING PIPELINE #1
		// pipelineShape1.addVisualShape(pipelineShape2);
		// pipeline0.addSubProcessable(pipeline1);
		//
		// // PIPELINE #2
		// PipelineShape pipelineShape3 = new PipelineShape("Pen to TUIO");
		// Pipeline pipeline2 = pipelineShape3.getProcessable();
		//
		// // LAYOUTING PIPELINE #2
		// LayoutConstraint lc2 = pipelineShape3.getLayoutConstraint();
		// lc2.setX(90);
		// lc2.setY(60);
		// lc2.setScale(0.1);
		//
		// // ADDING PIPELINE #2
		// pipelineShape1.addVisualShape(pipelineShape3);
		// pipeline0.addSubProcessable(pipeline2);
		//
		// // PIPELINE #3
		// PipelineShape pipelineShape4 = new
		// PipelineShape("Laserpointer to TUIO");
		// Pipeline pipeline3 = pipelineShape4.getProcessable();
		//
		// // LAYOUTING PIPELINE #2
		// LayoutConstraint lc3 = pipelineShape4.getLayoutConstraint();
		// lc3.setX(50);
		// lc3.setY(80);
		// lc3.setScale(0.1);
		//
		// // ADDING PIPELINE #2
		// pipelineShape1.addVisualShape(pipelineShape4);
		// pipeline0.addSubProcessable(pipeline3);
		//
		// LayerDemoShape layerDemoShape = new LayerDemoShape();
		// LayoutConstraint lc4 = layerDemoShape.getLayoutConstraint();
		// lc4.setX(20);
		// lc4.setY(20);
		// lc4.setScale(0.1);
		// pipelineShape1.addVisualShape(layerDemoShape);

		// // VALVE #1
		// ValveShape zoomValve1 = new ValveShape(new TestValve());
		// Valve valve1 = zoomValve1.getValve();
		//
		// // LAYOUTING VALVE #1
		// LayoutConstraint lc3 = zoomValve1.getLayoutConstraint();
		// lc3.setX(40);
		// lc3.setY(40);
		// lc3.setScale(0.1);
		//
		// // ADDING VALVE #1
		// zoomPipeline1.addVisualShape(zoomValve1);
		// pipeline1.addValve(valve1);
		//
		// // VALVE #2
		// ValveShape zoomValve2 = new ValveShape(new TestValve());
		// Valve valve2 = zoomValve2.getValve();
		//
		// // LAYOUTING VAVLE #2
		// LayoutConstraint lc4 = zoomValve2.getLayoutConstraint();
		// lc4.setX(50.78);
		// lc4.setY(63.23);
		// lc4.setScale(0.1);
		//
		// // ADDING VALVE #2
		// zoomPipeline1.addVisualShape(zoomValve2);
		// pipeline1.addValve(valve2);
		//
		// // ADDING VALVE #3
		// ValveShape zoomValve3 = new ValveShape(new TestValve());
		// Valve valve3 = zoomValve3.getValve();
		//
		// // LAYOUTING VALVE #3
		// LayoutConstraint lc5 = zoomValve3.getLayoutConstraint();
		// lc5.setX(40);
		// lc5.setY(40);
		// lc5.setScale(0.1);
		//
		// // ADDING VALVE #3
		// zoomPipeline2.addVisualShape(zoomValve3);
		// pipeline2.addValve(valve3);
		//
		// // VALVE #4
		// ValveShape zoomValve4 = new ValveShape(new TestValve());
		// Valve valve4 = zoomValve4.getValve();
		//
		// // LAYOUTING VALVE #4
		// LayoutConstraint lc6 = zoomValve4.getLayoutConstraint();
		// lc6.setX(50.78);
		// lc6.setY(63.23);
		// lc6.setScale(0.1);
		//
		// // ADDING VALVE #4
		// zoomPipeline2.addVisualShape(zoomValve4);
		// pipeline2.addValve(valve4);

		// KnowledgeBase knowledgeBase = new KnowledgeBase();
		// getCanvas().getLayer().addChild(knowledgeBase);
		// knowledgeBase.offset(300, 200);
		// knowledgeBase.setMode(KnowledgeBase.Mode.INPUT);
		// // knowledgeBase.scale(0.5);

		// #############################################
		// JWebBrowser - SWT Component
		// #############################################

		// try {
		// URL url = new URL("http://en.wikipedia.org/wiki/Kalman_filter");
		// BufferedInputStream in = new BufferedInputStream(url.openStream());
		// StringBuilder sb = new StringBuilder();
		//
		// byte[] buffer = new byte[8192];
		// int length = 0;
		//
		// while ((length = in.read(buffer)) != -1) {
		// sb.append(new String(buffer, 0, length));
		// }
		//
		// System.out.println(sb.toString());
		//
		// JWebBrowser browser = new JWebBrowser();
		// browser.navigate("http://www.google.de");
		// browser.setPreferredSize(new Dimension(400, 400));
		// browser.setSize(new Dimension(400, 400));
		// browser.setMaximumSize(new Dimension(200, 200));
		// // browser.setHTMLContent(sb.toString());
		// PSwing swing = new PSwing(browser);
		//
		// swing.setOffset(400, 50);
		// getCanvas().getLayer().addChild(swing);
		// }
		// catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// #############################################

		// #############################################
		// JEditorPane and HTMLEditorKit
		// #############################################

		// try {
		// // URL url = new URL("http://en.wikipedia.org/wiki/Kalman_filter");
		// URL url = new
		// URL("http://www.access-programmers.co.uk/forums/showthread.php?t=94345");
		// BufferedInputStream in = new BufferedInputStream(url.openStream());
		//
		// // StringBuilder sb = new StringBuilder();
		// //
		// // byte[] buffer = new byte[8192];
		// // int length = 0;
		// //
		// // while ((length = in.read(buffer)) != -1) {
		// // sb.append(new String(buffer, 0, length));
		// // }
		// //
		// // System.out.println(sb.toString());
		//
		// // InputStreamReader reader = new InputStreamReader(in);
		//
		// HTMLEditorKit kit = new HTMLEditorKit();
		// JEditorPane pane = new JEditorPane();
		// pane.setEditorKit(kit);
		// pane.read(in, kit.createDefaultDocument());
		//
		// // // Create empty HTMLDocument to read into
		// // HTMLEditorKit htmlKit = new HTMLEditorKit();
		// // HTMLDocument htmlDoc = (HTMLDocument)
		// htmlKit.createDefaultDocument();
		// // // Create parser (javax.swing.text.html.parser.ParserDelegator)
		// // HTMLEditorKit.Parser parser = new ParserDelegator();
		// // // Get parser callback from document
		// // HTMLEditorKit.ParserCallback callback = htmlDoc.getReader(0);
		// // // Load it (true means to ignore character set)
		// // parser.parse(reader, callback, true);
		// // // Replace document
		// // pane.setDocument(htmlDoc);
		// // System.out.println("Loaded");
		//
		// pane.setPreferredSize(new Dimension(200, 200));
		// pane.setSize(new Dimension(200, 200));
		// pane.setMaximumSize(new Dimension(200, 200));
		//
		// PSwing swing = new PSwing(pane);
		// swing.setOffset(400, 50);
		//
		// getCanvas().getLayer().addChild(swing);
		//
		// // HTMLNode node = new HTMLNode(sb.toString());
		// // node.setOffset(400, 10);
		// // getCanvas().getLayer().addChild(node);
		// }
		// catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// #############################################

		return data;
	}
	
	private void initializeSession() {
		try {
			Session session = SessionFactoryProvider.getProvider().getSession();
			session.createTrigger("add");
			session.attachTrigger("add", new TriggerNotification() {
				
				// type=org.squidy.designer.model.PipeShape,shape=<id>,pipe=<id>,source=<id>,target=<id>
				public void update(String data) {
					SessionFactory<? extends Session> provider = SessionFactoryProvider.getProvider();
					
					if (provider instanceof RemoteUpdatableSessionProvider<?>)
						((RemoteUpdatableSessionProvider<? extends Session>) provider).setIgnoreUpdateRemote(true);
					
					String[] addStr = data.split(",");
					
					String typeStr = addStr[0].split("=")[1];
					Class<?> type = ReflectionUtil.loadClass(typeStr);
					
					if (PipeShape.class.isAssignableFrom(type)) {
						String id = addStr[1].split("=")[1];
						String pipeId = addStr[2].split("=")[1];
						String sourceId = addStr[3].split("=")[1];
						String targetId = addStr[4].split("=")[1];
						
						ConnectorShape<?, ?> source = (ConnectorShape<?, ?>) ShapeUtils.getShapeWithId(Designer.getInstance().data.getWorkspaceShape(), sourceId);
						ConnectorShape<?, ?> target = (ConnectorShape<?, ?>) ShapeUtils.getShapeWithId(Designer.getInstance().data.getWorkspaceShape(), targetId);
						
						PipeShape shape = PipeShape.create(source, target, id, pipeId);
						
						VisualShape<VisualShape<?>> parentShape;
						if (!source.getParent().equals(target.getParent())) {
							if (target.getParent().equals(source)) {
								parentShape = (VisualShape<VisualShape<?>>) source;
							}
							else {
								parentShape = (VisualShape<VisualShape<?>>) target;
							}
						}
						else {
							parentShape = (VisualShape<VisualShape<?>>) source.getParent();
						}
						
						parentShape.addVisualShape(shape);
						shape.invalidateFullBounds();
						
						parentShape.repaint();
					}
					else if (Processable.class.isAssignableFrom(type)) {
						// type=<processor_type>,processor=<id>,shape=<id>,layoutConstraintId=<id>,parent=<id>,x=<id>,y=<id>
						String processorId = addStr[1].split("=")[1];
						String id = addStr[2].split("=")[1];
						String layoutConstraintId = addStr[3].split("=")[1];
						String parentId = addStr[4].split("=")[1];
						double x = Double.parseDouble(addStr[5].split("=")[1]);
						double y = Double.parseDouble(addStr[6].split("=")[1]);
						
						Processable processable = ReflectionUtil.createInstance((Class<Processable>) type);
						processable.setId(processorId);
						
						ContainerShape parentShape = (ContainerShape<?, ?>) ShapeUtils.getShapeWithId(Designer.getInstance().data.getWorkspaceShape(), parentId);
						ActionShape<?, ?> shape = ShapeUtils.getActionShape(processable);
						shape.setId(id);
						shape.getLayoutConstraint().setId(layoutConstraintId);
						
						((ContainerShape<?, Piping>) shape).setProcessable((Piping) processable);
						((ContainerShape<?, Piping>) parentShape).getProcessable().addSubProcessable(processable);
						parentShape.addVisualShape(shape);
						
//						shape.setOffset(x, y);
						
						shape.setDraggable(true);
					}
					
					if (provider instanceof RemoteUpdatableSessionProvider<?>)
						((RemoteUpdatableSessionProvider<? extends Session>) provider).setIgnoreUpdateRemote(false);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void add(VisualShape<?> shape) {
		try {
			if (shape instanceof PipeShape) {
				PipeShape pipeShape = (PipeShape) shape;
				// type=org.squidy.designer.model.PipeShape,shape=<id>,pipe=<id>,source=<id>,target=<id>
				SessionFactoryProvider.getProvider().getSession().trigger("1 to 1", "add",
						"type=" + pipeShape.getClass().getName() +
						",shape=" + pipeShape.getId() + 
						",pipe=" + pipeShape.getPipe().getId() + 
						",source=" + pipeShape.getSource().getId() + 
						",target=" + pipeShape.getTarget().getId());
			}
			else if (shape instanceof ContainerShape<?, ?>) {
				ContainerShape<?, ?> containerShape = (ContainerShape<?, ?>) shape;
				// type=<processor_type>,processor=<id>,shape=<id>,layoutConstraintId=<id>,parent=<id>,x=<id>,y=<id>
				SessionFactoryProvider.getProvider().getSession().trigger("1 to 1", "add", "type=" + containerShape.getProcessable().getClass().getName() +
						",processor=" + containerShape.getProcessable().getId() +
						",shape=" + containerShape.getId() +
						",layoutConstraintId=" + containerShape.getLayoutConstraint().getId() +
						",parent=" + ((ContainerShape<?, ?>) containerShape.getParent()).getId() + 
						",x=" + containerShape.getOffset().getX() + 
						",y=" + containerShape.getOffset().getY());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
