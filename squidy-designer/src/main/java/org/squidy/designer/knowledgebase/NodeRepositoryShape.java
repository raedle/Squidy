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

package org.squidy.designer.knowledgebase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;

import org.squidy.designer.component.CropScroll;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.util.StrokeUtils;
import org.squidy.designer.zoom.NavigationShape;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

public class NodeRepositoryShape<T extends ZoomShape<VisualShape<?>>> extends NavigationShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 3228592695522981994L;

	PImage baseIcon;

	private CropScroll cropScroll;

	private PSwing searchField;
	private String searchString;
	private List<RepositoryItem<T>> NOT_MATCHING_ITEMS = new ArrayList<RepositoryItem<T>>();

	public NodeRepositoryShape() {
		setTitle("Knowledge Base");

		baseIcon = new PImage(NodeRepositoryShape.class.getResource("/org/squidy/nodes/image/48x48/data-table.png"));
		baseIcon.setScale(7);
		baseIcon.setOffset(80, 400);
		addChild(baseIcon);

		final NodeTile<?> nodeTile = new NodeTile<RepositoryItem<ZoomShape<?>>>();
		cropScroll = new CropScroll(nodeTile, new Dimension(900, 700), 0.3);
		cropScroll.setOffset(getBoundsReference().getCenterX() - cropScroll.getBoundsReference().getCenterX(), 250);
		addChild(cropScroll);

		final String searchHint = "<search for item>";

		// TODO - use JComponentWrapper instead
		final JTextField searchBox = new JTextField(searchHint);
		searchBox.setForeground(Color.LIGHT_GRAY);
		searchBox.setPreferredSize(new Dimension(150, 20));
		searchBox.setFont(internalFont.deriveFont(12f).deriveFont(Font.ITALIC, 12f));
		searchBox.addFocusListener(new FocusAdapter() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.FocusAdapter#focusGained(java.awt.event.FocusEvent)
			 */
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				
				if (searchHint.equals(searchBox.getText())) {
					searchBox.setText("");
				}
				searchBox.setForeground(Color.BLACK);
			}
			
			/* (non-Javadoc)
			 * @see java.awt.event.FocusAdapter#focusLost(java.awt.event.FocusEvent)
			 */
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				
				if ("".equals(searchBox.getText())) {
					searchBox.setText(searchHint);
					searchBox.setForeground(Color.LIGHT_GRAY);
				}
			}
		});
		searchBox.addKeyListener(new KeyAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);

				searchString = searchBox.getText().toLowerCase();

				nodeTile.filterTiles(searchString);

				invalidatePaint();
			}
		});

		searchField = new JComponentWrapper(searchBox) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -9158482136889456638L;

			@Override
			public void paint(PPaintContext arg0) {
				getComponent().paint(arg0.getGraphics());
			}
		};
		searchField.setScale(2);
		searchField.setOffset(100, 120);
		addChild(searchField);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.shape.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		ShapeUtils.setApparent(baseIcon, false);
		ShapeUtils.setApparent(cropScroll, true);
		ShapeUtils.setApparent(searchField, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.shape.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		super.layoutSemanticsZoomedOut();

		ShapeUtils.setApparent(baseIcon, true);
		ShapeUtils.setApparent(cropScroll, false);
		ShapeUtils.setApparent(searchField, false);
	}
	
	private Shape shape1;
	private Shape shape2;
	private Shape shape3;
	private Shape shape4;
	
	private Paint gradientPaint1;
	private Paint gradientPaint2;
	
	private static Font knowledgeBaseFont = internalFont.deriveFont(150f);

	private static final String NODE_BASE_NAME1 = "Node";
	private static final String NODE_BASE_NAME2 = "Repository";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.TitledShape#paintShapeZoomedOut(edu.umd
	 * .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedOut(PPaintContext paintContext) {

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = getBoundsReference();

		double x = bounds.x;
		double y = bounds.y;
		double width = bounds.width;
		double height = bounds.height;

		if (shape1 == null) {
			shape1 = new RoundRectangle2D.Double(x, y, width, height, width, height / 3);
		}
		g.draw(shape1);

		if (gradientPaint1 == null) {
			gradientPaint1 = new GradientPaint(0, 0, Color.WHITE, (int) Constants.DEFAULT_NODE_BOUNDS
					.getWidth() / 2, 0, Color.LIGHT_GRAY);
		}
		g.setPaint(gradientPaint1);

		if (shape2 == null) {
			shape2 = new RoundRectangle2D.Double(x + 1, y + 1, width - 2, height - 2, width, height / 3); 
		}
		g.fill(shape2);

		g.setColor(Color.BLACK);
	
		if (shape3 == null) {
			shape3 = new Ellipse2D.Double(x, y, width, height / 3);
		}
		g.draw(shape3);

		if (gradientPaint2 == null) {
			gradientPaint2 = new GradientPaint(0, 0, Color.WHITE, (int) Constants.DEFAULT_NODE_BOUNDS.getWidth() / 2, 0,
				Color.GRAY);
		}
		g.setPaint(gradientPaint2);

		if (shape4 == null) {
			shape4 = new Ellipse2D.Double(x + 1, y + 1, width - 2, height / 3 - 2);
		}
		g.fill(shape4);

		g.setColor(Color.BLACK);
		g.setFont(knowledgeBaseFont);
		g.drawString(NODE_BASE_NAME1, 520, 600);
		g.drawString(NODE_BASE_NAME2, 440, 800);
	}
	
	private Shape shape5;

	private static Color color1 = Color.DARK_GRAY.brighter();
	private static Color color2 = Color.LIGHT_GRAY;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.shape.ZoomShape#paintAfterChildren(edu.umd
	 * .cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintAfterChildren(PPaintContext arg0) {
		Graphics2D g = arg0.getGraphics();
		g.setStroke(StrokeUtils.getBasicStroke(0.5f));

		for (RepositoryItem<T> item : NOT_MATCHING_ITEMS) {
			
			if (shape5 == null) {
				shape5 = new RoundRectangle2D.Double(item.getXOffset(), item.getYOffset(), 14, 10, 2, 2);
			}
			
			g.setColor(color1);
			g.draw(shape5);
			g.setColor(color2);
			g.fill(shape5);
		}
	}
}
