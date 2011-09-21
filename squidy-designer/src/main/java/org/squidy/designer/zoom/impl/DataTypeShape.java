/**
 * Squidy Interaction Library is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. Squidy Interaction Library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Squidy Interaction Library. If not, see <http://www.gnu.org/licenses/>. 2009 Human-Computer
 * Interaction Group, University of Konstanz. <http://hci.uni-konstanz.de> Please contact info@squidy-lib.de or visit
 * our website <http://www.squidy-lib.de> for further information.
 */
package org.squidy.designer.zoom.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.manager.data.DataType;
import org.squidy.manager.data.IData;
import org.squidy.manager.util.DataUtility;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The <code>DataTypeShape</code> constitutes a data filter for the generic data types available in Squidy. If the data
 * type filter is zoomed out it will give a brief overview, which data type is selected. At this zoom level data types
 * within a single hierarchy will be combined into a single representation. If the users zooms into the filter all data
 * types are displayed corresponding to their hierarchy. The user then can select or deselect single data types by
 * clicking on the corresponding data type shape or select and deselect all data types at once by clicking on the IData
 * data type (the topmost generic data type - interface).
 * 
 * <pre>
 * Date: Mar 14, 2009
 * Time: 9:44:13 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz .de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * @version $Id: DataTypeShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DataTypeShape extends ZoomShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 138128836520420673L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(DataTypeShape.class);

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################

	// A cache to keep already used instances of color in memory to avoid
	// frequently creations of color instances.
	private static final Map<Class<? extends IData>, Color> COLOR_CACHE = new HashMap<Class<? extends IData>, Color>();

	// A single arrow used to build a concatenated arrow view.
	private static Shape arrowShape = new Polygon(new int[] { 0, 500, 1000, 500, 0, 500 }, new int[] { 0, 0, 750, 1500,
			1500, 750 }, 6);

	// A rounded rectangle shape available indicates each data type.
	private static Shape roundedRectangleShape = new RoundRectangle2D.Double(0, 0, 450, 120, 120, 120);

	private static final Stroke STROKE_ARROW_BOLD = new BasicStroke(50f);
	private static final Stroke STROKE_ARROW_THIN = new BasicStroke(5f);
	private static final Stroke STROKE_SHAPE = new BasicStroke(4f);
	private static final Stroke STROKE_SHAPE_ITEM = new BasicStroke(5f);
	private static Font fontDataType = internalFont.deriveFont(50f);

	private Collection<Class<? extends IData>> dataTypes;

	/**
	 * A data type shape with the data types given as parameter selected otherwise the data types are deselected
	 * automatically.
	 * 
	 * @param dataTypes The data types that will be set as selected for this data type shape.
	 */
	public DataTypeShape(Collection<Class<? extends IData>> dataTypes) {
		this.dataTypes = dataTypes;

		setBounds(0, 0, 2000, 1500);

		buildDataHierarchy();
	}

	/**
	 * TODO [RR]: Needs refactoring for automatically computes visual hierarchy.
	 */
	private void buildDataHierarchy() {

		PBounds bounds = getBoundsReference();
		Rectangle2D rectBounds = roundedRectangleShape.getBounds2D();

		DataItemShape iData = new DataItemShape(IData.class, true);
		iData.setBounds(rectBounds);
		iData.setOffset(bounds.getCenterX() - rectBounds.getCenterX(), 0);
		addChild(iData);

		Class<? extends IData>[] typesFirstRow = DataUtility.DATA_FIRST_LEVEL;
		int amount = typesFirstRow.length;
		double spacing = (bounds.getWidth() - (amount * rectBounds.getWidth())) / (amount - 1);
		for (int i = 0; i < amount; i++) {
			boolean selected = dataTypes.contains(typesFirstRow[i]);
			DataItemShape item = new DataItemShape(typesFirstRow[i], selected);
			item.setBounds(rectBounds);
			item.setOffset((i * rectBounds.getWidth()) + (i * spacing), rectBounds.getHeight() + 200);
			addChild(item);
		}

		Class<? extends IData>[] typesSecondRow = DataUtility.DATA_SECOND_LEVEL;
		amount = typesSecondRow.length;
		spacing = (bounds.getWidth() - (amount * rectBounds.getWidth())) / (amount - 1);
		for (int i = 0; i < amount; i++) {
			boolean selected = dataTypes.contains(typesSecondRow[i]);
			DataItemShape item = new DataItemShape(typesSecondRow[i], selected);
			item.setBounds(rectBounds);
			item.setOffset((i * rectBounds.getWidth()) + (i * spacing), (2 * rectBounds.getHeight()) + (2 * 200));
			addChild(item);
		}

		amount = 4;
		spacing = (bounds.getWidth() - (amount * rectBounds.getWidth())) / (amount - 1);
		boolean selected = dataTypes.contains(DataUtility.DATA_THIRD_LEVEL[0]);
		DataItemShape item = new DataItemShape(DataUtility.DATA_THIRD_LEVEL[0], selected);
		item.setBounds(rectBounds);
		item.setOffset((1 * rectBounds.getWidth()) + (1 * spacing), (3 * rectBounds.getHeight()) + (3 * 200));
		addChild(item);
	}

	/*
	 * (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		for (Object child : getChildrenReference()) {
			if (child instanceof DataItemShape) {
				ShapeUtils.setApparent((DataItemShape) child, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		for (Object child : getChildrenReference()) {
			if (child instanceof DataItemShape) {
				ShapeUtils.setApparent((DataItemShape) child, false);
			}
		}
	}

	private PAffineTransform translation = new PAffineTransform(AffineTransform.getTranslateInstance(0, -200));

	/*
	 * (non-Javadoc)
	 * @see org.squidy.designer.shape.ZoomShape#paintShape(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		// paintContext.pushTransform(translation);
		super.paintShape(paintContext);
		// paintContext.popTransform(translation);
	}

	/*
	 * (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#paintShapeZoomedIn(edu.umd. cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();

		paintContext.pushTransform(translation);

		PBounds bounds = getBoundsReference();
		// Rectangle rectBounds = roundedRectangleShape.getBounds();

		int x = (int) bounds.x - 3;
		int y = (int) bounds.y - 3;
		int width = (int) bounds.width + 6;
		int height = (int) bounds.height;

		g.setColor(Color.WHITE);
		g.fillRoundRect(x - 40, y, width + 80, height, 825, 825);
		g.setStroke(STROKE_SHAPE);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x - 40, y, width + 80, height, 825, 825);

		paintContext.popTransform(translation);

		// 1st vertical
		g.drawLine(1000, 100, 1000, 220);

		// 1st horizontal
		g.drawLine(230, 220, 1780, 220);

		// 2nd (left) vertical
		g.drawLine(230, 220, 230, 700);
		// 2nd (left-middle) vertical
		g.drawLine(740, 220, 740, 960);
		// 2d (right-middle) vertical
		g.drawLine(1260, 220, 1260, 700);
		// 2nd (right) vertical
		g.drawLine(1780, 220, 1780, 700);
	}

	private PAffineTransform translationArrow = new PAffineTransform();

	private Collection<Class<? extends IData>> alreadyPaintedHighLevelDataTypes = new HashSet<Class<? extends IData>>();

	/*
	 * (non-Javadoc)
	 * @see org.squidy.designer.zoom.ZoomShape#paintShapeZoomedOut(edu.umd .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedOut(PPaintContext paintContext) {
		Graphics2D g = paintContext.getGraphics();

		if (dataTypes != null && dataTypes.size() > 0) {
			int i = 0;

			alreadyPaintedHighLevelDataTypes.clear();
			for (Class<? extends IData> type : dataTypes) {

				if (type.isAssignableFrom(IData.class)) {
					continue;
				}

				Class<? extends IData> highLevelDataType = DataUtility.getHighLevelDataType(type);

				if (alreadyPaintedHighLevelDataTypes.contains(highLevelDataType)) {
					continue;
				}

				Color color;
				if (!COLOR_CACHE.containsKey(highLevelDataType)) {
					DataType dataType = highLevelDataType.getAnnotation(DataType.class);
					int[] colorScheme = dataType.color();
					color = new Color(colorScheme[0], colorScheme[1], colorScheme[2], colorScheme[3]);
					COLOR_CACHE.put(highLevelDataType, color);
				}
				else {
					color = COLOR_CACHE.get(highLevelDataType);
				}

				translationArrow.setToTranslation(i * 750, 0);
				paintContext.pushTransform(translationArrow);

				g.setColor(color);
				g.fill(arrowShape);

				g.setStroke(STROKE_ARROW_BOLD);
				g.setColor(Color.BLACK);
				g.draw(arrowShape);

				paintContext.popTransform(translationArrow);

				alreadyPaintedHighLevelDataTypes.add(highLevelDataType);

				i++;
			}
		}
		else {
			drawNoDataType(g);
		}

		// g.setColor(Color.BLACK);
		// g.setStroke(StrokeUtils.getBasicStroke(105f));
		// g.draw(getGlobalBoundsZoomedIn());
	}

	/**
	 * Draws an empty data type filter. This will be drawn if all data types are unselected.
	 * 
	 * @param g The graphics device to draw on.
	 */
	void drawNoDataType(Graphics2D g) {
		g.setStroke(STROKE_ARROW_THIN);
		g.setColor(Color.BLACK);
		g.draw(arrowShape);
	}

	// #############################################################################
	// END INTERNAL
	// #############################################################################

	/**
	 * <code>DataItemShape</code>.
	 * 
	 * <pre>
	 * Date: Mar 25, 2009
	 * Time: 11:23:11 AM
	 * </pre>
	 * 
	 * @author <pre>
	 * Roman R&amp;aumldle
	 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * @version $Id: DataTypeShape.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private class DataItemShape extends PNode {

		/**
		 * Generated serial version UID.
		 */
		private static final long serialVersionUID = -8622708993706228441L;

		private Class<? extends IData> type;
		private String typeName;
		private Color color;
		private boolean selected;

		/**
		 * @param type
		 * @param isSelected
		 */
		public DataItemShape(final Class<? extends IData> type, final boolean selected) {

			// Security check.
			if (!type.isAnnotationPresent(DataType.class)) {
				String typeName = type.getName();
				String dataTypeName = DataType.class.getName();
				throw new SquidyException("Data type " + typeName + " is missing " + dataTypeName + " annotation.");
			}

			DataType dataType = type.getAnnotation(DataType.class);
			int[] colorScheme = dataType.color();

			// Color scheme for data type
			color = new Color(colorScheme[0], colorScheme[1], colorScheme[2], colorScheme[3]);
			this.type = type;
			this.typeName = type.getSimpleName();
			this.selected = selected;

			// Initializes the selection and deselection listener
			initializeSelectionListener();
		}

		/**
		 * Initializes the selection and deselection listener. If the component receives a mouse pressed event it
		 * deselects the component if it was selected previously and vice versa.
		 */
		void initializeSelectionListener() {
			addInputEventListener(new PBasicInputEventHandler() {

				/*
				 * (non-Javadoc)
				 * @see
				 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd.cs.piccolo.event.PInputEvent)
				 */
				@Override
				public void mousePressed(PInputEvent event) {
					super.mousePressed(event);

					if (!event.isHandled()) {
						setSelected(!selected);
						if (type.isAssignableFrom(IData.class)) {
							for (Object o : getParent().getChildrenReference()) {
								if (!o.equals(DataItemShape.this) && o instanceof DataItemShape) {
									DataItemShape dataItemShape = (DataItemShape) o;
									dataItemShape.setSelected(selected);
								}
							}
						}
						event.setHandled(true);
						invalidatePaint();
					}
				}
			});
		}

		/**
		 * @param selected
		 */
		void setSelected(boolean selected) {
			this.selected = selected;
			if (selected) {
				dataTypes.add(type);
			}
			else {
				dataTypes.remove(type);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
		 */
		@Override
		protected void paint(PPaintContext paintContext) {
			super.paint(paintContext);

			Graphics2D g = paintContext.getGraphics();

			PBounds bounds = getBoundsReference();

			int x = (int) bounds.getX();
			int y = (int) bounds.getY();
			int width = (int) bounds.getWidth();
			int height = (int) bounds.getHeight();

			g.setColor(selected ? color : Color.WHITE);
			if (isRenderPrimitiveRect())
				g.fillRect(x, y, width, height);
			else
				g.fillRoundRect(x, y, width, height, 120, 120);

			g.setStroke(STROKE_SHAPE_ITEM);
			g.setColor(Color.BLACK);
			if (isRenderPrimitiveRect())
				g.drawRect(x, y, width, height);
			else
				g.drawRoundRect(x, y, width, height, 120, 120);

			g.setFont(fontDataType);

			FontMetrics fm = g.getFontMetrics();
			int typeNameWidth = fm.stringWidth(typeName);
			g.drawString(typeName, (int) (bounds.getCenterX() - (typeNameWidth / 2)), (int) (bounds.getCenterY() + fm
					.getDescent()));
		}
	}
}
