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

package org.squidy.designer.zoom.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RectangularShape;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.component.button.ImageButton;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.event.ZoomActionEvent;
import org.squidy.designer.event.ZoomActionListener;
import org.squidy.designer.model.PipeShape;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.visualization.PlotContext;
import org.squidy.designer.shape.visualization.Visualization;
import org.squidy.designer.shape.visualization.impl.ImagePlot;
import org.squidy.designer.shape.visualization.impl.ScatterPlot;
import org.squidy.designer.shape.visualization.impl.ThermoPlot;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.manager.ProcessException;
import org.squidy.manager.ProcessingFeedback;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DefaultDataContainer;
import org.squidy.manager.model.Processable;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * <code>ZoomVisualization</code>.
 * 
 * <pre>
 * Date: Feb 18, 2009
 * Time: 6:09:39 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: VisualizationShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class VisualizationShape extends ActionShape<VisualShape<?>, Processable> implements ProcessingFeedback {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -649926873755554231L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(VisualizationShape.class);

	// class DataWrapper {
	//
	// long timestamp;
	// IData[] data;
	//		
	// DataWrapper(long timestamp, IData... data) {
	// this.timestamp = timestamp;
	// this.data = data;
	// }
	// }

	// #############################################################################
	// BEGIN ProcessingFeedback
	// #############################################################################

	// private final Queue<DataWrapper> DATA_QUEUE = new
	// ConcurrentLinkedQueue<DataWrapper>();
	private boolean active = false;
	private final Queue<IDataContainer> DATA_QUEUE = new ConcurrentLinkedQueue<IDataContainer>();
	private int frameCount = 0;
	private int currentFPS = 0;

	private final Map<Class<? extends IData>, IData> LAST_DATA_CACHE = new HashMap<Class<? extends IData>, IData>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.ProcessingFeedback#feedback(org.squidy
	 * .manager.data.IData)
	 */
	public void feedback(IData... data) {

		if (!active) {
			return;
		}

		frameCount++;

		if (freeze) {
			return;
		}

		// if (DATA_QUEUE.size() > 500) {
		// DATA_QUEUE.poll();
		// }

		for (IData d : data) {
			LAST_DATA_CACHE.put(d.getClass(), d);
		}

		DATA_QUEUE.add(new DefaultDataContainer(data));
	}

	// #############################################################################
	// END ProcessingFeedback
	// #############################################################################

	// #############################################################################
	// BEGIN ILaunchable
	// #############################################################################

	// #############################################################################
	// END ILaunchable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#delete()
	 */
	public void delete() throws ProcessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		throw new UnsupportedOperationException();
	}

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	private final PAffineTransform TRANSLATION = new PAffineTransform();

	private static final int SCALING = 10;
	private static final double SCALE_TRANSFORM_SCALING = 1.0 / (double) SCALING;
	private static final PAffineTransform SCALE_TRANSFROM = new PAffineTransform();

	static {
		SCALE_TRANSFROM.setScale(SCALE_TRANSFORM_SCALING);
	}

	private int visualizationIndex = 0;
	private Visualization[] visualizations = new Visualization[] { new ScatterPlot(), new ThermoPlot(), new ImagePlot() };
	// private Visualization visualization = new ThermoPlot();

	private Timer timer = new Timer();
	private TimerTask repaintTask;
	private TimerTask frameCountTask;

	private PSwing timeSpinner;
	private long sampleTime = 2500;

	private PImage image;

	private RectangularShape shape;

	private boolean freeze = false;

	private PipeShape pipeShape;

	/**
	 * @param pipeShape
	 */
	public VisualizationShape(PipeShape pipeShape) {
		setBounds(Constants.DEFAULT_CIRCLE_BOUNDS);
		setTitle("Visualization");
		// setShowNavigation(false);
		 setShowTitle(false);

		this.pipeShape = pipeShape;

//		addInputEventListener(new PBasicInputEventHandler() {
//			@Override
//			public void mouseClicked(PInputEvent event) {
//
//				if (event.isRightMouseButton() && event.getClickCount() == 2) {
//					if (!event.isHandled()) {
//						if (++visualizationIndex == visualizations.length) {
//							visualizationIndex = 0;
//						}
//						invalidatePaint();
//
//						event.setHandled(true);
//					}
//				}
//			}
//		});

		initSemanticImage();
		initTimeSpinner();

		PBounds bounds = getBoundsReference();
		PBounds imageBounds = image.getBoundsReference();
		image.setOffset(bounds.getCenterX() - (imageBounds.getCenterX() * image.getScale()), bounds.getCenterY()
				- (imageBounds.getCenterY() * image.getScale()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ActionShape#initializeLayout()
	 */
	@Override
	public void initializeLayout() {
		super.initializeLayout();
		setShowActionsOnMouseOver(false);

		final ImageButton runAction = new ImageButton(VisualizationShape.class
				.getResource("/images/24x24/stopwatch_run.png"), "Run");
		final ImageButton pauseAction = new ImageButton(VisualizationShape.class
				.getResource("/images/24x24/stopwatch_pause.png"), "Pause");

		runAction.setEnabled(false);
		runAction.addZoomActionListener(new ZoomActionListener() {

			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {
				freeze = false;
				runAction.setEnabled(false);
				pauseAction.setEnabled(true);
			}
		});
		addAction(runAction);

		pauseAction.addZoomActionListener(new ZoomActionListener() {

			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {
				freeze = true;
				runAction.setEnabled(true);
				pauseAction.setEnabled(false);
			}
		});
		addAction(pauseAction);
		
		final ImageButton scatterPlotAction = new ImageButton(VisualizationShape.class
				.getResource("/images/24x24/text_code_colored.png"), "Scatter");
		final ImageButton thermoPlotAction = new ImageButton(VisualizationShape.class
				.getResource("/images/24x24/text_rich_colored.png"), "Thermo");
		
		scatterPlotAction.setEnabled(false);
		scatterPlotAction.addZoomActionListener(new ZoomActionListener() {
			
			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {
				visualizationIndex = 0;
				scatterPlotAction.setEnabled(false);
				thermoPlotAction.setEnabled(true);
			}
		});
		addAction(scatterPlotAction);
		
		thermoPlotAction.addZoomActionListener(new ZoomActionListener() {
			
			/**
			 * @param e
			 */
			public void actionPerformed(ZoomActionEvent e) {
				visualizationIndex = 1;
				scatterPlotAction.setEnabled(true);
				thermoPlotAction.setEnabled(false);
			}
		});
		addAction(thermoPlotAction);
	}

	/**
	 * 
	 */
	private final void initSemanticImage() {
		image = new PImage(VisualizationShape.class.getResource("/images/visualization.png"));
		image.setScale(10);
		image.setPickable(false);
		image.setChildrenPickable(false);
		addChild(image);
	}

	/**
	 * 
	 */
	private final void initTimeSpinner() {
		PBounds bounds = getBoundsReference();

		final JSpinner spinner = new JSpinner(new SpinnerNumberModel(sampleTime, 1000, 10000, 500));
		spinner.addChangeListener(new ChangeListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event
			 * .ChangeEvent)
			 */
			public void stateChanged(ChangeEvent e) {
				sampleTime = ((Double) spinner.getValue()).longValue();
				invalidatePaint();
			}
		});

		timeSpinner = new JComponentWrapper(spinner);
		timeSpinner.setOffset((bounds.getWidth() * 0.25) - (timeSpinner.getWidth() / 2),
				bounds.getHeight() * 0.75 + 0.25);
		addChild(timeSpinner);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.PNode#pickAfterChildren(edu.umd.cs.piccolo.util.PPickPath
	 * )
	 */
	@Override
	protected boolean pickAfterChildren(PPickPath pickPath) {
		PBounds pickBounds = pickPath.getPickBounds();
		return getZoomedInShape().intersects(pickBounds);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomObject#getShape()
	 */
	@Override
	protected Shape getZoomedInShape() {
		if (shape == null) {
			shape = new Ellipse2D.Double();
			shape.setFrame(getBoundsReference());
		}

		return shape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomObject#getShapeZoomedOut()
	 */
	@Override
	protected Shape getZoomedOutShape() {
		return getZoomedInShape();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		active = true;

		repaintTask = new TimerTask() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {

				if (freeze) {
					return;
				}

				if (DATA_QUEUE.size() > 0) {
					repaint();
				}

				// TODO [RR]: Optimization
				// PBounds bounds = getBoundsReference();
				// bounds = new PBounds(bounds.getWidth() / 4,
				// bounds.getHeight() / 4, bounds.getWidth() / 2,
				// bounds.getHeight() / 2);
				//				
				// repaintFrom(bounds, getParent());
			}
		};

		timer.scheduleAtFixedRate(repaintTask, 0, 50);

		frameCountTask = new TimerTask() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				currentFPS = frameCount;
				frameCount = 0;
				repaint();
			}
		};
		timer.schedule(frameCountTask, 0, 1000);

		ShapeUtils.setApparent(image, false);
		ShapeUtils.setApparent(timeSpinner, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		if (repaintTask != null) {
			repaintTask.cancel();
			repaintTask = null;
			frameCountTask.cancel();
			frameCountTask = null;
		}

		active = false;
		DATA_QUEUE.clear();
		currentFPS = 0;
		frameCount = 0;

		ShapeUtils.setApparent(image, true);
		ShapeUtils.setApparent(timeSpinner, false);
	}

	private static Font fontLables = internalFont.deriveFont(3f);
	private static Font fontSmall = internalFont.deriveFont(1.8f);
	private static Font fontFPS = internalFont.deriveFont(25f);
	private static Font fontHeadline = internalFont.deriveFont(32f);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomObject#paintShapeZoomedIn(edu.umd
	 * .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = getBoundsReference();

//		PBounds cameraBounds = getBoundsReference();// paintContext.getCamera().getFullBoundsReference();
//		double cameraX = cameraBounds.getX() - 20;
//		double cameraY = cameraBounds.getY();
//		double cameraWidth = cameraBounds.getWidth() + 40;
//		double cameraHeight = cameraBounds.getHeight();

//		g.setColor(Color.WHITE);
//		g.fillRect((int) cameraX, (int) cameraY, (int) cameraWidth, (int) cameraHeight);
//
//		g.setColor(Color.BLACK);
//		g.fillRect((int) cameraX, (int) (cameraY + cameraHeight / 4), (int) cameraWidth, (int) (cameraHeight / 2));

		g.setFont(fontLables);

		// Paint node labels.
		paintNodeLabels(paintContext);

		super.paintShapeZoomedIn(paintContext);

		// Paint headline.
		if (!isShowNavigation()) {
			paintHeadline(paintContext);
		}

		g.setFont(fontSmall);

		// Translate the current graphics position to 1/4 width and 1/4 height
		// of this shape.
		TRANSLATION.setToTranslation((int) (bounds.getWidth() / 4), (int) (bounds.getHeight() / 4));
		paintContext.pushTransform(TRANSLATION);

		// Scale up visualization are because of int constraints to avoid
		// mathematical round failure.
		int maxWidth = (int) ((bounds.getWidth() / 2) * 10);
		int maxHeight = (int) ((bounds.getHeight() / 2) * 10);

		paintContext.pushTransform(SCALE_TRANSFROM);

		PlotContext plotContext = new PlotContext(paintContext, maxWidth, maxHeight, SCALE_TRANSFORM_SCALING,
				sampleTime);

		long currentTime = System.currentTimeMillis();
		for (IDataContainer dataContainer : DATA_QUEUE) {
			if ((currentTime - dataContainer.getTimestamp()) >= sampleTime) {
				DATA_QUEUE.remove(dataContainer);
				continue;
			}

			// Delegates plot of data to visualization.
			visualizations[visualizationIndex].plotData(plotContext, dataContainer);
		}

		// Paints the grid.
		visualizations[visualizationIndex].paintContextual(paintContext, plotContext);

		paintContext.popTransform(SCALE_TRANSFROM);

		// Pop transform translation.
		paintContext.popTransform(TRANSLATION);

		g.setFont(fontFPS);
		g.drawString("FPS: " + currentFPS, (int) (bounds.getWidth() / 2 - 50), (int) (bounds.getHeight() - 100));
		
		g.setFont(fontHeadline.deriveFont(18f));
		g.setColor(Color.RED);
		g.fillOval((int) (bounds.getWidth() - 115), (int) (bounds.getHeight() / 2 - 20), 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("x-value", (int) (bounds.getWidth() - 100), (int) (bounds.getHeight() / 2 - 10));
		
		g.setColor(Color.BLUE);
		g.fillOval((int) (bounds.getWidth() - 115), (int) (bounds.getHeight() / 2 + 20), 10, 10);
		g.setColor(Color.BLACK);
		g.drawString("y-value", (int) (bounds.getWidth() - 100), (int) (bounds.getHeight() / 2 + 30));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomShape#paintShapeZoomedOut(edu.umd
	 * .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedOut(PPaintContext paintContext) {

//		Graphics2D g = paintContext.getGraphics();
//		g.setStroke(StrokeUtils.getBasicStroke(5f));
		
//		g.draw(getBoundsReference());

		super.paintShapeZoomedOut(paintContext);
	}

	/**
	 * @param paintContext
	 */
	protected void paintHeadline(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();

		g.setFont(fontHeadline);
		FontMetrics fm = g.getFontMetrics();

		PBounds bounds = getBoundsReference();
		double x = bounds.getX();
		double width = bounds.getWidth();

		int titleWidth = FontUtils.getWidthOfText(fm, getTitle());
		g.drawString(getTitle(), (int) (x + width / 2) - (titleWidth / 2), 100);

		int breadcrumbWidth = FontUtils.getWidthOfText(fm, getBreadcrumb());
		g.drawString(getBreadcrumb(), (int) (x + width / 2) - (breadcrumbWidth / 2), 100 + fm.getHeight());
	}

	private PAffineTransform rotation270 = new PAffineTransform();
	private PAffineTransform rotation90 = new PAffineTransform();

	/**
	 * @param paintContext
	 */
	protected void paintNodeLabels(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();
		
		g.setFont(g.getFont().deriveFont(18f));
		FontMetrics fm = g.getFontMetrics();

		PBounds bounds = getBoundsReference();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		
		String inputName = pipeShape.getSource().getTitle();
		rotation270.setToRotation(Math.toRadians(270));
		paintContext.pushTransform(rotation270);
		g.setColor(Color.WHITE);
		g.drawString(inputName, (int) -((height / 2) + (FontUtils.getWidthOfText(fm, inputName) / 2)), -25);
		paintContext.popTransform(rotation270);

		String outputName = pipeShape.getTarget().getTitle();
		rotation90.setToRotation(Math.toRadians(90));
		paintContext.pushTransform(rotation90);
		g.setColor(Color.WHITE);
		g.drawString(outputName, (int) ((height / 2) - (FontUtils.getWidthOfText(fm, outputName) / 2)),
				(int) (-width - 30));
		paintContext.popTransform(rotation90);
	}

	// /**
	// * Paint the grid of the visualization.
	// *
	// * @param paintContext Paint context.
	// */
	// private void paintGrid(PPaintContext paintContext) {
	//		
	// Graphics2D g = paintContext.getGraphics();
	//		
	// PBounds bounds = getBoundsReference();
	//		
	// g.setColor(Color.BLACK);
	// // System.out.println(defaultFont);
	// g.setFont(defaultFont.deriveFont(1.8f));
	//		
	// int timeStep = (int) sampleTime / 500;
	// int gridStepX = (int) ((bounds.getWidth() / 2) / timeStep);
	// int gridStepY = (int) ((bounds.getHeight() / 2) / 10);
	//		
	// // Paint axes.
	// g.setStroke(new BasicStroke(0.5f));
	// g.drawLine(0, 0, 0, (int) (bounds.getHeight() / 2));
	// g.drawLine(0, (int) (bounds.getHeight() / 2), (int) (bounds.getWidth() /
	// 2), (int) (bounds.getHeight() / 2));
	//		
	// g.setStroke(new BasicStroke(0.1f));
	//		
	// FontMetrics fm = g.getFontMetrics();
	//		
	// // Properties for vertical and horizontal lines.
	// int requiredWidth = (int) (bounds.getWidth() / 2);
	// int requiredHeight = (int) (bounds.getHeight() / 2);
	//		
	// String xLabel = "Time (ms)";
	// g.drawString(xLabel, requiredWidth / 2 - fm.stringWidth(xLabel),
	// requiredHeight + 5);
	//		
	// PAffineTransform rotation = new PAffineTransform();
	// rotation.rotate(Math.toRadians(-90));
	// paintContext.pushTransform(rotation);
	// String yLabel = "Value";
	// g.drawString(yLabel, - (requiredHeight / 2) - (fm.stringWidth(yLabel) /
	// 2), - 5);
	// paintContext.popTransform(rotation);
	//		
	// // Paint vertical lines.
	// int timeAxes = (int) sampleTime;
	// for (int xShift = 0; xShift <= requiredWidth; xShift += gridStepX) {
	// g.drawLine(xShift, 0, xShift, (int) (bounds.getHeight() / 2));
	//			
	// if (xShift > 0) {
	// String xLabels = timeAxes > 0 ? "-" + timeAxes : "" + timeAxes;
	// g.drawString(xLabels, xShift - (fm.stringWidth(xLabels) / 2), (int)
	// (bounds.getHeight() / 2) + 2);
	// }
	// timeAxes -= 500;
	// }
	//		
	// // Paint horizontal lines.
	// double i = 0;
	// for (int yShift = requiredHeight; yShift >= 0; yShift -= gridStepY) {
	// String yLabels = DECIMAL_FORMAT.format(i);
	//			
	// g.drawLine(0, yShift, requiredWidth, yShift);
	// g.drawString(yLabels, - fm.stringWidth(yLabels) - 1, yShift);
	// i += 0.1;
	// }
	// }

	// #############################################################################
	// END INTERNAL
	// #############################################################################
}
