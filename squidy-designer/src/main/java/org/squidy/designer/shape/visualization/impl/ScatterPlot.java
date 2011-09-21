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

package org.squidy.designer.shape.visualization.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.visualization.PlotContext;
import org.squidy.designer.shape.visualization.Visualization;
import org.squidy.designer.util.StrokeUtils;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataAnalog;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataKey;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.data.impl.DataString;

import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ScatterPlot</code>.
 * 
 * <pre>
 * Date: May 30, 2009
 * Time: 4:50:18 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: ScatterPlot.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ScatterPlot implements Visualization {

	// Affine transform scales sx to 0.5 and sy to 0.5.
	private static final PAffineTransform SCALE_TRANSFORM = new PAffineTransform();
	static {
		SCALE_TRANSFORM.setToScale(0.5, 0.5);
	}
	
	// Affine transform rotates graphics -45 degrees.
	private static final PAffineTransform ROTATION_TRANSFORM = new PAffineTransform();
	private static final double ROTATION_RADIANS = Math.toRadians(-45);
	static {
		ROTATION_TRANSFORM.setToRotation(ROTATION_RADIANS);
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	
	/**
	 * @param plotContext
	 * @param dataContainter
	 */
	public void plotData(PlotContext plotContext, IDataContainer dataContainer) {
		
		Graphics2D g = plotContext.getGraphics();
		int width = plotContext.getWidth();
		int height = plotContext.getHeight();
		
		long sampleTime = plotContext.getSampleTime();
		
		long currentTime = System.currentTimeMillis();
		double normalizedWidth = Math.abs(((currentTime - dataContainer.getTimestamp()) / (double) sampleTime) - 1);
		double xPosition = width * normalizedWidth;
		
		for (IData data : dataContainer.getData()) {
			if (data.getClass().equals(DataPosition2D.class)) {
				plot(g, (DataPosition2D) data, height, xPosition, Color.RED, Color.BLUE);
			}
			else if (data.getClass().equals(DataPosition3D.class)) {
				plot(g, (DataPosition3D) data, height, xPosition, Color.MAGENTA, Color.CYAN, Color.GRAY);
			}
			else if (data.getClass().equals(DataPosition6D.class)) {
				plot(g, (DataPosition3D) data, height, xPosition, Color.MAGENTA, Color.CYAN, Color.GRAY);
			}
			else if (data.getClass().equals(DataAnalog.class)) {
				plot(g, (DataAnalog) data, height, xPosition);
			}
			else if (data.getClass().equals(DataDigital.class)) {
				plot(plotContext, (DataDigital) data, height, xPosition);
			}
			else if (data.getClass().equals(DataButton.class)) {
				plot(plotContext, (DataDigital) data, height, xPosition);
			}
			else if (data.getClass().equals(DataKey.class)) {
				plot(plotContext, (DataDigital) data, height, xPosition);
			}
			else if (data.getClass().equals(DataString.class)) {
				plot(plotContext, (DataString) data, height, xPosition);
			}
			else if (data.getClass().equals(DataInertial.class)) {
				plot(g, (DataInertial) data, height, xPosition);
			}
		}
	}
	
	/**
	 * @param g
	 * @param dataPosition2D
	 * @param height
	 */
	private void plot(Graphics2D g, DataPosition2D dataPosition2D, int height, double xPosition, Color xColor, Color yColor) {

		// Draw x-position of DataPosition2D.
		g.setColor(xColor);
		int dataPosition2DXPosY = (int) (Math.abs(dataPosition2D.getX() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition2DXPosY, 50, 50);

		// Draw y-position of DataPosition2D.
		g.setColor(yColor);
		int dataPosition2DYPosY = (int) (Math.abs(dataPosition2D.getY() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition2DYPosY, 50, 50);
	}
	
	/**
	 * @param g
	 * @param dataPosition2D
	 * @param height
	 */
	private void plot(Graphics2D g, DataPosition3D dataPosition3D, int height, double xPosition, Color xColor, Color yColor, Color zColor) {

		// Draw x-position of DataPosition2D.
		g.setColor(xColor);
		int dataPosition3DXPosY = (int) (Math.abs(dataPosition3D.getX() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition3DXPosY, 50, 50);

		// Draw y-position of DataPosition2D.
		g.setColor(yColor);
		int dataPosition3DYPosY = (int) (Math.abs(dataPosition3D.getY() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition3DYPosY, 50, 50);
		
		// Draw z-position of DataPosition2D.
		g.setColor(zColor);
		int dataPosition3DZPosY = (int) (Math.abs(dataPosition3D.getZ() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition3DZPosY, 50, 50);
	}
	
	/**
	 * @param g
	 * @param dataAnalog
	 * @param height
	 */
	private void plot(Graphics2D g, DataAnalog dataAnalog, int height, double xPosition) {

		// Draw value of DataAnalog.
		g.setColor(Color.GREEN);
		int dataPosition2DXPosY = (int) (Math.abs(dataAnalog.getValue() - 1.0) * height);
		g.fillOval((int) xPosition, dataPosition2DXPosY, 50, 50);
	}
	
	/**
	 * @param g
	 * @param dataInertial
	 * @param height
	 * @param xPosition
	 */
	private void plot(Graphics2D g, DataInertial dataInertial, int height, double xPosition) {

		// Draw x-value of dataInertial.
		g.setColor(Color.YELLOW);
		int dataInertialXPosY = (int) (Math.abs(dataInertial.getX() - 1.0) * height);
		g.fillOval((int) xPosition, dataInertialXPosY, 50, 50);

		// Draw y-value of dataInertial.
		g.setColor(Color.MAGENTA);
		int dataInertialYPosY = (int) (Math.abs(dataInertial.getY() - 1.0) * height);
		g.fillOval((int) xPosition, dataInertialYPosY, 50, 50);
		
		// Draw z-value of dataInertial.
		g.setColor(Color.CYAN);
		int dataInertialZPosY = (int) (Math.abs(dataInertial.getZ() - 1.0) * height);
		g.fillOval((int) xPosition, dataInertialZPosY, 50, 50);
		
		// Draw vector length of dataInertial.
		g.setColor(Color.GRAY);
		int dataInertialAbs = (int) (Math.abs(dataInertial.getAbsoluteValue() - 1.0) * height);
		g.fillOval((int) xPosition, dataInertialAbs, 50, 50);
	}
	
	private static final String VISUALIZATION_NAME = "Scatter Plot";
	private static final Font FONT = VisualShape.internalFont.deriveFont(180f);
	
	private static String LABEL_X_AXIS;
	private static int LABEL_X_AXIS_X;
	private static int LABEL_X_AXIS_Y;
	
	private static String LABEL_Y_AXIS;
	private static int LABEL_Y_AXIS_X;
	private static int LABEL_Y_AXIS_Y;
	
	private static final PAffineTransform ROTATION_90_COUNTER = new PAffineTransform();
	static {
		ROTATION_90_COUNTER.rotate(Math.toRadians(-90));
	}
	
	
	/* (non-Javadoc)
	 * @see org.squidy.designer.shape.visualization.Visualization#paintContextual(edu.umd.cs.piccolo.util.PPaintContext, org.squidy.designer.shape.visualization.PlotContext)
	 */
	public void paintContextual(PPaintContext paintContext, PlotContext plotContext) {
		Graphics2D g = paintContext.getGraphics();
		long sampleTime = plotContext.getSampleTime();
		
		g.setFont(FONT);
		g.setColor(Color.BLACK);
		g.drawString(VISUALIZATION_NAME, 0, 0 - 100);
		
		int width = plotContext.getWidth();
		int height = plotContext.getHeight();
		
		int timeStep = (int) sampleTime / 500;
		int gridStepX = (int) (width / timeStep);
		int gridStepY = (int) (height / 10);
		
		// Paint axes.
		g.setStroke(StrokeUtils.getBasicStroke(25f));
		g.drawLine(0, 0, 0, (int) height);
		g.drawLine(0, (int) height, (int) width, (int) height);
		
		FontMetrics fm = g.getFontMetrics();
		
		// Properties for vertical and horizontal lines.
		int requiredWidth = (int) width;
		int requiredHeight = (int) height;
		
		if (LABEL_X_AXIS == null) {
			LABEL_X_AXIS = "Time (ms)";
			LABEL_X_AXIS_X = requiredWidth / 2 - g.getFontMetrics().stringWidth(LABEL_X_AXIS);
			LABEL_X_AXIS_Y = requiredHeight + 5 * 100;
		}
		g.drawString(LABEL_X_AXIS, LABEL_X_AXIS_X, LABEL_X_AXIS_Y);
		
		paintContext.pushTransform(ROTATION_90_COUNTER);
		if (LABEL_Y_AXIS == null) {
			LABEL_Y_AXIS = "Value";
			LABEL_Y_AXIS_X = -(requiredHeight / 2) - (g.getFontMetrics().stringWidth(LABEL_Y_AXIS) / 2);
			LABEL_Y_AXIS_Y = -5 * 100;
		}
		g.drawString(LABEL_Y_AXIS, LABEL_Y_AXIS_X, LABEL_Y_AXIS_Y);
		paintContext.popTransform(ROTATION_90_COUNTER);
		
		g.setStroke(StrokeUtils.getBasicStroke(5.5f));
		// Paint vertical lines.
		int timeAxes = (int) sampleTime;
		for (int xShift = 0; xShift <= requiredWidth; xShift += gridStepX) {
			g.drawLine(xShift, 0, xShift, (int) height);
			
			if (xShift > 0) {
				String xLabels = timeAxes > 0 ? "-" + timeAxes : "" + timeAxes;
				g.drawString(xLabels, xShift - (fm.stringWidth(xLabels) / 2), (int) height + 2 * 100);
			}
			timeAxes -= 500;
		}
		
		// Paint horizontal lines.
		double i = 0;
		for (int yShift = requiredHeight; yShift >= 0; yShift -= gridStepY) {
			String yLabels = DECIMAL_FORMAT.format(i);
			
			g.drawLine(0, yShift, requiredWidth, yShift);
			g.drawString(yLabels, - fm.stringWidth(yLabels) - 1 * 100, yShift);
			i += 0.1;
		}
	}
	
	/**
	 * @param g
	 * @param dataPosition2D
	 * @param height
	 */
	private void plot(PlotContext plotContext, DataDigital dataDigital, int height, double xPosition) {
		
		Graphics2D g = plotContext.getGraphics();
		
		g.setStroke(new BasicStroke(30f));
		g.setFont(g.getFont().deriveFont(1.8f * 100));

		g.setColor(Color.YELLOW);
		g.drawLine((int) xPosition, (int) height, (int) xPosition, 0);

		String label = "" + dataDigital.getFlag();
		if (dataDigital instanceof DataButton) {
			label += ", buttonType=" + ((DataButton) dataDigital).getButtonType();
		}
		
		double oppositeLeg = Math.abs(Math.sin(ROTATION_RADIANS) * xPosition);
		double adjustment = Math.cos(ROTATION_RADIANS) * xPosition - xPosition;
		
		// Push rotation transform.
		plotContext.pushTransform(ROTATION_TRANSFORM);
		
		// Push backwards translation transform.
		PAffineTransform translateBackwards = new PAffineTransform();
		translateBackwards.setToTranslation(adjustment + 0.5, oppositeLeg - 0.5);
		plotContext.pushTransform(translateBackwards);
		
		g.setColor(Color.BLACK);
		g.drawString(label, (int) xPosition, 0);

		// Pop backwards translation transform.
		plotContext.popTransform(translateBackwards);
		
		// Pop rotation transform.
		plotContext.popTransform(ROTATION_TRANSFORM);
	}
	
	/**
	 * @param g
	 * @param dataPosition2D
	 * @param height
	 */
	private void plot(PlotContext plotContext, DataString dataString, int height, double xPosition) {
		
		Graphics2D g = plotContext.getGraphics();
		
		g.setStroke(new BasicStroke(30f));
		g.setFont(g.getFont().deriveFont(1.8f * 100));

		g.setColor(Color.GREEN);
		g.drawLine((int) xPosition, (int) height, (int) xPosition, 0);

//		String label = "" + dataDigital.getFlag();
//		if (dataDigital instanceof DataButton) {
//			label += ", buttonType=" + ((DataButton) dataDigital).getButtonType();
//		}
		
		double oppositeLeg = Math.abs(Math.sin(ROTATION_RADIANS) * xPosition);
		double adjustment = Math.cos(ROTATION_RADIANS) * xPosition - xPosition;
		
		// Push rotation transform.
		plotContext.pushTransform(ROTATION_TRANSFORM);
		
		// Push backwards translation transform.
		PAffineTransform translateBackwards = new PAffineTransform();
		translateBackwards.setToTranslation(adjustment + 0.5, oppositeLeg - 0.5);
		plotContext.pushTransform(translateBackwards);
		
		g.setColor(Color.BLACK);
		g.drawString(dataString.getData(), (int) xPosition, 0);

		// Pop backwards translation transform.
		plotContext.popTransform(translateBackwards);
		
		// Pop rotation transform.
		plotContext.popTransform(ROTATION_TRANSFORM);
	}
}
