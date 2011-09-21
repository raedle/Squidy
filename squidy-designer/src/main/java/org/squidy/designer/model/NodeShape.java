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

package org.squidy.designer.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.MVEL;
import org.squidy.SquidyException;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.database.RemoteUpdateUtil;
import org.squidy.designer.Designer;
import org.squidy.designer.DesignerPreferences;
import org.squidy.designer.component.CropScroll;
import org.squidy.designer.component.PropertiesTable;
import org.squidy.designer.component.TableEntry;
import org.squidy.designer.dragndrop.Draggable;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.ImageUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.util.SourceCodeUtils;
import org.squidy.designer.zoom.Connectable;
import org.squidy.designer.zoom.ConnectorShape;
import org.squidy.designer.zoom.impl.InformationShape;
import org.squidy.designer.zoom.impl.LogShape;
import org.squidy.designer.zoom.impl.PluginsShape;
import org.squidy.designer.zoom.impl.SourceCodeShape;
import org.squidy.manager.IBasicControl;
import org.squidy.manager.Manager;
import org.squidy.manager.ProcessException;
import org.squidy.manager.PropertyUpdateListener;
import org.squidy.manager.controls.AbstractBasicControl;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.FileChooser;
import org.squidy.manager.controls.Gauge;
import org.squidy.manager.controls.ImagePanel;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.Spinner;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.model.Node;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;
import org.squidy.manager.plugin.Pluggable;
import org.squidy.manager.util.ControlUtils;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomValve</code>.
 * 
 * <pre>
 * Date: Feb 20, 2009
 * Time: 11:57:48 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: NodeShape.java 776 2011-09-18 21:34:48Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "NodeShape")
public class NodeShape extends ConnectorShape<ZoomShape<?>, Piping> implements Draggable, Connectable {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -7029278385238730453L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(NodeShape.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public NodeShape() {
		super();
		setGridVisible(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#afterUnmarshal(javax.xml
	 * .bind.Unmarshaller, java.lang.Object)
	 */
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		super.afterUnmarshal(unmarshaller, parent);
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	// #############################################################################
	// BEGIN RemoteUpdatable
	// #############################################################################
	
//	/* (non-Javadoc)
//	 * @see org.squidy.designer.zoom.ContainerShape#serialize()
//	 */
//	@Override
//	public String serialize() {
//		String serial = super.serialize();
//		
//		Class<? extends Piping> type = getProcessable().getClass();
//		for (Field field : type.getFields()) {
//			if (field.isAnnotationPresent(Property.class)) {
//				field.setAccessible(true);
//				String name = field.getName();
//				Object value = field.get(getProcessable());
//			}
//		}
//		
//		return serial;
//	}
	
	// #############################################################################
	// END RemoteUpdatable
	// #############################################################################
	
	// #############################################################################
	// BEGIN Initializable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.Initializable#initialize()
	 */
	public void initialize() {
		super.initialize();

		if (getProcessable() == null) {
			if (LOG.isErrorEnabled()) {
				LOG
						.error("Could not initialize ValveShape correctly. A processable instance is missing. Using error content instead.");
			}
			createContentForErrorProne();

			return;
		}

		Processor processor = null;
		processor = getProcessable().getClass().getAnnotation(Processor.class);

		if (processor == null) {
			if (LOG.isErrorEnabled()) {
				LOG
						.error("Could not initialize ValveShape correctly. A @Processor annotation is missing. Using error content instead.");
			}
			createContentForErrorProne();

			return;
		}
		
		setStable(processor.status().equals(Processor.Status.STABLE) ? true : false);

		createContent(processor);

		// TODO [RR]: Use arrow key to navigate to "next" connected nodes.
		// addInputEventListener(new PBasicInputEventHandler() {
		//			
		// });
	}

	// #############################################################################
	// END Initializable
	// #############################################################################

	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		getProcessable().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		getProcessable().stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		if (getProcessable() != null) {
			if (getProcessable().isProcessing()) {
				stop();
			}

			getProcessable().delete();
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleting processable shape...");
		}

		// TODO [RR]: Remove ZoomShape

		removeFromParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ActionShape#publishFailure(java.lang.
	 * Exception)
	 */
	@Override
	public void publishFailure(Throwable e) {
		super.publishFailure(e);

		log.logError(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ActionShape#resolveFailure()
	 */
	@Override
	public void resolveFailure() {
		super.resolveFailure();

		log.clearLog();
	}

	// #############################################################################
	// END ILaunchable
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	public static final String PROPERTY_BINDING_OK = "PROPERTY_BINDING_OK";
	public static final String PROPERTY_BINDING_EXCEPTION = "PROPERTY_BINDING_EXCEPTION";

	private PImage image;
	private InformationShape information;
	private SourceCodeShape sourceCode;
	private LogShape log;
	private PluginsShape plugins;
	private PropertiesTable propertiesTable;
	private CropScroll scroll;
	
	private boolean stable = true;

	public void setStable(boolean stable) {
		this.stable = stable;
	}

	// private Versioning versioning;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.NavigationShape#changeTitle(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	protected boolean changeTitle(String oldTitle, String newTitle) {

		try {

			URL sourceCodeUrl = SourceCodeUtils.getSourceCode(getProcessable());
			
			// Check if title changed.
			File sourceCodeFile;
			if (!oldTitle.equals(newTitle)) {
				sourceCodeFile = new File(sourceCodeUrl.getFile().replace(oldTitle, newTitle));
				
				// Check if class already exists at url.
				if (sourceCodeFile.exists()) {
					JOptionPane.showMessageDialog(Designer.getInstance(), "Class " + newTitle + " already exists at\n" + sourceCodeFile.toString() + "\nPlease choose a different name.",
							"Class already exists", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
			else {
				sourceCodeFile = new File(sourceCodeUrl.getFile());
			}

			// Proof if newTitle matches a Java convenient naming.
			if (!SourceCodeUtils.isJavaConvenientNaming(newTitle)) {
				JOptionPane.showMessageDialog(Designer.getInstance(), "Please choose a Java convenient naming.",
						"Java naming error", JOptionPane.ERROR_MESSAGE);
				return false;
			}

			StringBuilder sb = new StringBuilder();
			try {
				FileReader reader = new FileReader(sourceCodeUrl.getFile());
				char[] buffer = new char[8096];
				int len;
				while ((len = reader.read(buffer)) != -1) {
					sb.append(buffer, 0, len);
				}
			}
			catch (FileNotFoundException e) {
				publishFailure(e);
			}
			catch (IOException e) {
				publishFailure(e);
			}

			String newSourceCode = sb.toString().replace(oldTitle, newTitle);

			persistCode(sourceCodeFile, newTitle, newSourceCode);
		}
		catch (MalformedURLException e) {
			publishFailure(e);

			return false;
		}

		return super.changeTitle(oldTitle, newTitle);
	}

	/**
	 * @param file
	 * @param content
	 */
	public void persistCode(File file, String content) {
		Node node = (Node) getProcessable();

		persistCode(file, node.getClass().getSimpleName(), content);
	}

	/**
	 * @param file
	 * @param content
	 */
	public void persistCode(File file, String newName, String content) {

		// Flag if something failed while persisting node.
		boolean failed = false;

		try {
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			failed = true;
			publishFailure(e);
		}

		Class<? extends Node> valveType;
		try {
			Node node = (Node) getProcessable();

			valveType = ReflectionUtil.<Node> loadClass(node.getClass().getPackage().getName() + "." + newName);

			node = ReflectionUtil.createInstance(valveType);

			failed = rebuildWithValve(node);
		}
		catch (Exception e) {
			failed = true;
			publishFailure(e);
		}

		// Set url to new source code.
		try {
			sourceCode.setSourceCodeURL(file.toURL());
		}
		catch (MalformedURLException e) {
			failed = true;
			publishFailure(e);
		}

		// Resolve failure if everything went fine.
		if (!failed) {
			resolveFailure();
		}
	}

	/**
	 * @param processor
	 */
	private final void createContent(Processor processor) {
		setTitle(processor.name());
		setTitleGap(30);

		String description = processor.description();
		information = new InformationShape(processor.name(), description);
		information.setScale(0.1);

		// Retrieve url to source code.
		URL sourceUrl = null;
		try {
			sourceUrl = SourceCodeUtils.getSourceCode(getProcessable());
		}
		catch (MalformedURLException e) {
			publishFailure(e);
		}

		sourceCode = new SourceCodeShape(this, sourceUrl);
		sourceCode.setScale(0.1);

		log = new LogShape();
		log.setScale(0.1);

		addChild(information);
		addChild(sourceCode);
		addChild(log);

		// Builds property table and binds properties to components.
		buildPropertiesTable();
		scroll = new CropScroll(propertiesTable, new Dimension(1000, 620), 0.2);
		addChild(scroll);

		// versioning = new Versioning(propertiesTable);
		// versioning.setBounds(0, 0, 100, 10);
		// addChild(versioning);

		if (processor.plugins().length > 0) {
			createPlugins(processor.plugins());
		}
		
		try {
			image = new PImage(ImageUtils.getProcessorIconURL(processor));
		} catch (SquidyException e) {
			publishFailure(e);
			image = new PImage();
		}
		image.setPickable(false);
		image.setChildrenPickable(false);
		addChild(image);
	}

	/**
	 * 
	 */
	private final void createContentForErrorProne() {
		image = new PImage(NodeShape.class.getResource("/org/squidy/nodes/image/48x48/warning.png"));
		image.setPickable(false);
		image.setChildrenPickable(false);
		addChild(image);
	}

	/**
	 * @param processable
	 */
	public boolean rebuildWithValve(Node node) {
		boolean failed = false;
		
		ModelViewHandler.resetJAXBContext(node.getClass());

		boolean processing = getProcessable().isProcessing();

		Piping oldProcessable = getProcessable();
		Processable parentProcessable = oldProcessable.getParent();

		if (processing) {
			oldProcessable.stop();
		}

		for (Pipe pipe : oldProcessable.getOutgoingPipes()) {
			pipe.setSource(node);
		}

		for (Pipe pipe : oldProcessable.getIncomingPipes()) {
			pipe.setTarget(node);
		}

		node.setPipes(oldProcessable.getPipes());
		node.setOutgoingPipes(oldProcessable.getOutgoingPipes());
		node.setIncomingPipes(oldProcessable.getIncomingPipes());

		parentProcessable.removeSubProcessable(oldProcessable);
		parentProcessable.addSubProcessable(node);
		setProcessable(node);
		
		// Mapping previous property values to new nodes properties.
		ReflectionUtil.mapFieldsWithAnnotation(Property.class, oldProcessable, node);

		Processor processor = node.getClass().getAnnotation(Processor.class);
		
		// Catch exception if image could not be found by image utils.
		try {
			image.setImage(ImageUtils.getProcessorIconURL(processor).getPath());
		} catch (SquidyException e) {
			failed = true;
			publishFailure(e);
		}
		
		information.setInformationSource(processor.description());
//		setTitle(processor.name());

		if (processing) {
			node.start();
		}

		buildPropertiesForPropertiesTable();
		
		return failed;
	}

	/**
	 * @param pluginTypes
	 */
	private void createPlugins(Class<? extends Pluggable>[] pluginTypes) {
		plugins = new PluginsShape(pluginTypes);
		plugins.setScale(0.1);
		addChild(plugins);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#layoutChildren()
	 */
	@Override
	protected void layoutChildren() {
		super.layoutChildren();

		if (getChildBoundsInvalid()) {
			PBounds bounds = getBoundsReference();

			double x = bounds.getX();
			double y = bounds.getY();
			double height = bounds.getHeight();
			double centerX = bounds.getCenterX();

			ShapeUtils.setOffset(information, x + 50, y + 110);
			ShapeUtils.setOffset(sourceCode, x + 220, y + 110);
			ShapeUtils.setOffset(log, x + 400, y + 110);
			ShapeUtils.setOffset(plugins, x + 680, y + 110);

			if (scroll != null) {
				ShapeUtils.setOffset(scroll, centerX - scroll.getBoundsReference().getCenterX(), y + 320);
			}

			// if (versioning != null) {
			// ShapeUtils.setOffset(versioning, centerX -
			// scroll.getBoundsReference().getCenterX(), y + height
			// - versioning.getHeight() - 5);
			// }
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#zoomBegan()
	 */
	@Override
	protected void zoomBegan() {
		image.setVisible(false);
		ShapeUtils.setRenderPrimitive(information, true);
		ShapeUtils.setRenderPrimitive(sourceCode, true);
		ShapeUtils.setRenderPrimitive(log, true);
		ShapeUtils.setRenderPrimitive(scroll, true);
		// ShapeUtils.setRenderPrimitive(versioning, true);

		super.zoomBegan();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#zoomEnded()
	 */
	protected void zoomEnded() {
		super.zoomEnded();

		image.setVisible(true);
		ShapeUtils.setRenderPrimitive(information, false);
		ShapeUtils.setRenderPrimitive(sourceCode, false);
		ShapeUtils.setRenderPrimitive(log, false);
		ShapeUtils.setRenderPrimitive(scroll, false);
		// ShapeUtils.setRenderPrimitive(versioning, false);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#getZoomedOutDrawPaint()
	 */
	@Override
	protected Paint getZoomedOutDrawPaint() {
		return stable ? super.getZoomedOutDrawPaint() : DesignerPreferences.STATUS_UNSTABLE;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#getZoomedInDrawPaint()
	 */
	@Override
	protected Paint getZoomedInDrawPaint() {
		return stable ? super.getZoomedInDrawPaint() : DesignerPreferences.STATUS_UNSTABLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		ShapeUtils.setScale(image, 0.4);
		ShapeUtils.setOffset(image, 460, 10);

		ShapeUtils.setApparent(information, true);
		ShapeUtils.setApparent(sourceCode, true);
		ShapeUtils.setApparent(log, true);
		ShapeUtils.setApparent(plugins, true);
		ShapeUtils.setApparent(scroll, true);
		// ShapeUtils.setApparent(versioning, true);

		ShapeUtils.setApparent(getInputPort(), false);
		// ShapeUtils.setScale(getInputPort(), 0.5);
		// ShapeUtils.setOffset(getInputPort(), 0.0, 0.0);
		ShapeUtils.setApparent(getOutputPort(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		ShapeUtils.setScale(image, 10);
		PBounds bounds = getBoundsReference();

		double y = bounds.getY();
		double centerX = bounds.getCenterX();

		if (image != null) {
			ShapeUtils
					.setOffset(image, centerX - (image.getBoundsReference().getCenterX() * image.getScale()), y + 150);
		}

		ShapeUtils.setApparent(information, false);
		ShapeUtils.setApparent(sourceCode, false);
		ShapeUtils.setApparent(log, false);
		ShapeUtils.setApparent(plugins, false);
		ShapeUtils.setApparent(scroll, false);
		// ShapeUtils.setApparent(versioning, false);

		ShapeUtils.setApparent(getInputPort(), true);
		ShapeUtils.setApparent(getOutputPort(), true);

		// Set pipe shapes visible and pickable.
		for (Object child : getParent().getChildrenReference()) {
			if (child instanceof PipeShape) {
				ShapeUtils.setApparent((PipeShape) child, true);
			}
		}
	}

	private static Font fontProperties = internalFont.deriveFont(45f);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ConnectorShape#paintShapeZoomedIn(edu
	 * .umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = getBoundsReference();
		int x = (int) bounds.getX();
		int y = (int) bounds.getY();

		g.setFont(fontProperties);
		g.drawString("Properties", x + 50, y + 300);
	}

	/**
	 * 
	 */
	private void buildPropertiesTable() {
		propertiesTable = new PropertiesTable();
		// propertiesTable.setBounds(0, 0, 600, 300);

		buildPropertiesForPropertiesTable();
	}

	/**
	 * 
	 */
	private void buildPropertiesForPropertiesTable() {
		propertiesTable.clearEntries();

		Field[] fields = ReflectionUtil.getFieldsInObjectHierarchy(getProcessable().getClass());
		for (Field field : fields) {

			if (field.isAnnotationPresent(Property.class)) {

				final String fieldName = field.getName();
				Object value = MVEL.getProperty(field.getName(), getProcessable());
				final AbstractBasicControl<Object, ?> control;
				if (field.isAnnotationPresent(TextField.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(TextField.class), value);
				}
				else if (field.isAnnotationPresent(CheckBox.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(CheckBox.class), value);
				}
				else if (field.isAnnotationPresent(ComboBox.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(ComboBox.class), value);
				}
				else if (field.isAnnotationPresent(Slider.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(Slider.class), value);
				}
				else if (field.isAnnotationPresent(Spinner.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(Spinner.class), value);
				}
				else if (field.isAnnotationPresent(ImagePanel.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(ImagePanel.class), value);
				}
				else if (field.isAnnotationPresent(Gauge.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(Gauge.class), value);
				}
				else if (field.isAnnotationPresent(FileChooser.class)) {
					control = (AbstractBasicControl<Object, ?>) ControlUtils.createControl(field
							.getAnnotation(FileChooser.class), value);
				}
				else {
					throw new SquidyException("Couldn't add property " + fieldName
							+ " to properties table because of a not existing control annotation.");
				}

				Property property = field.getAnnotation(Property.class);

				String name = property.name();
				String description = property.description();
				String prefix = property.prefix();
				String suffix = property.suffix();

				// Add property change listener to receive processing updates.
				getProcessable().addStatusChangeListener(fieldName, new PropertyChangeListener() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * java.beans.PropertyChangeListener#propertyChange(java
					 * .beans.PropertyChangeEvent)
					 */
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							control.setValueWithoutPropertyUpdate(evt.getNewValue());
						} catch (Exception e) {
							control.setValueWithoutPropertyUpdate(control.valueFromString(evt.getNewValue().toString()));
						}
						control.getComponent().repaint();

						propertiesTable.firePropertyChange(CropScroll.CROP_SCROLLER_UPDATE, null, null);
					}
				});

				// Add property update change listener to inform the processable
				// about UI update events.
				control.addPropertyUpdateListener(new PropertyUpdateListener<Object>() {

					/*
					 * (non-Javadoc)
					 * 
					 * @seede.ukn.hci.squidy.designer.components.
					 * PropertyUpdateListener #propertyUpdate(java.lang.Object)
					 */
					public void propertyUpdate(Object value) {
						try {
							// Notify manager about property changes.
							Manager.get().propertyChanged(getProcessable(), fieldName, value);

							control.firePropertyChange(PROPERTY_BINDING_OK, null, null);
						}
						catch (Exception e) {
							control.firePropertyChange(PROPERTY_BINDING_EXCEPTION, null, null);
							publishFailure(new SquidyException(
									"Could not set property "
											+ fieldName
											+ ". Please check setter of this property for any Exceptions such as NullPointerException.",
									e));
						}

						propertiesTable.firePropertyChange(CropScroll.CROP_SCROLLER_UPDATE, null, null);
					}
				});

				TableEntry entry = new TableEntry<IBasicControl<?, ?>>(name, description, control, prefix, suffix);
				if (field.isAnnotationPresent(ImagePanel.class)) {
					entry.addInputEventListener(new PBasicInputEventHandler() {
						@Override
						public void mouseClicked(PInputEvent event) {
							control.customPInputEvent(event);
						}
					});
				}
				propertiesTable.addEntryToGroup(entry, property.group());
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
