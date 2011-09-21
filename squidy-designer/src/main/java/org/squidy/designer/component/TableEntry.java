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

package org.squidy.designer.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JSlider;

import org.squidy.designer.model.NodeShape;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.zoom.impl.InformationShape;
import org.squidy.manager.IBasicControl;
import org.squidy.manager.controls.ComboBoxControl;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class TableEntry<C extends IBasicControl<?, ? extends JComponent>> extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -3501810293934427374L;
	
	private C control;
	private String valueName;
	private String postfix;
	private String prefix;
	
	boolean hasError = false;
	
	private static final Insets insets = new Insets(5, 5, 5, 5);

	/**
	 * @param name
	 * @param description
	 * @param control
	 * @param prefix
	 * @param postfix
	 */
	public TableEntry(String name, String description, C control, String prefix, String postfix) {
		this.control = control;
		this.valueName = name;
		this.postfix = postfix;
		this.prefix = prefix;
		
		JComponent component = control.getComponent();
		Dimension dimension = component.getPreferredSize();
		
		setBounds(0, 0, 990, insets.top + dimension.getHeight() + insets.bottom);
		
		prepareInformation(description);
		prepareControl(control);
	}
	
	/**
	 * @param information
	 */
	private void prepareInformation(String description) {
		PBounds bounds = getBoundsReference();
		
		InformationShape information = new InformationShape(valueName, description);
		addChild(information);
		information.setScale(0.02);
		
		double offsetY = bounds.getCenterY() - information.localToParent(information.getBounds()).getCenterY();
		information.setOffset(10 + 10, offsetY);
	}
	
	/**
	 * @param control
	 */
	private void prepareControl(C control) {
		JComponent component = control.getComponent();
		
		final JComponentWrapper componentWrapper = new JComponentWrapper(component);
		addChild(componentWrapper);
		
		// --- Take this out as soon as Piccolo Library sets Environment
		// automatically
		if (control instanceof ComboBoxControl) {
			final ComboBoxControl box = (ComboBoxControl) control;
			box.getComponent().addPropertyChangeListener(new PropertyChangeListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * java.beans.PropertyChangeListener#propertyChange(java.beans
				 * .PropertyChangeEvent)
				 */
				public void propertyChange(PropertyChangeEvent evt) {

					if ("ancestor".equals(evt.getPropertyName())) {
						Container c = box.getComponent().getParent();
						while (c != null && !(c instanceof PSwingCanvas)) {
							c = c.getParent();
						}
						
						if (c instanceof PSwingCanvas) {
							box.getComponent().setEnvironment(componentWrapper, (PSwingCanvas) c);
						}
					}
				}
			});
		}
		
		control.addPropertyChangeListener(NodeShape.PROPERTY_BINDING_OK, new PropertyChangeListener() {

			/* (non-Javadoc)
			 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				hasError = false;
				invalidatePaint();
			}
		});
		
		control.addPropertyChangeListener(NodeShape.PROPERTY_BINDING_EXCEPTION, new PropertyChangeListener() {

			/* (non-Javadoc)
			 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				hasError = true;
				invalidatePaint();
			}
			
		});
		
		Font font = internalFont.deriveFont(18.0f);
		
		double componentOffsetX = component.getPreferredSize().getWidth() + insets.right;
		if (component instanceof JSlider) {
			componentOffsetX += FontUtils.getWidthOfText(component.getFontMetrics(font), String.valueOf(((JSlider) component).getMaximum())) + insets.left + insets.right;
		}
		if (postfix != null) {
			componentOffsetX += FontUtils.getWidthOfText(component.getFontMetrics(font), postfix);
		}
		
		PBounds bounds = getBoundsReference();
		componentWrapper.setOffset(bounds.getWidth() - componentOffsetX - 10, insets.top);
	}
	
	/**
	 * @param paintContext
	 */
	@Override
	protected void paintShape(PPaintContext paintContext) {
		super.paintShape(paintContext);
		
		Graphics2D g = paintContext.getGraphics();
		
		g.setFont(internalFont.deriveFont(12.0f));
		
		PBounds bounds = getBoundsReference();
		double x = bounds.getX() + 10;
		double y = bounds.getY();
		double width = bounds.getWidth() - 20;
		double height = bounds.getHeight();
		
		g.setStroke(new BasicStroke(1.5f));
//		g.drawRoundRect(x, y, width / 2, height, 15, 15);
//		g.drawRoundRect(x + width / 2, y, width / 2, height, 15, 15);
		
		g.setColor(hasError ? Color.RED : Color.WHITE);
		if (isRenderPrimitiveRect())
			g.fillRect((int) x, (int) y, (int) width, (int) height);
		else
			g.fillRoundRect((int) x, (int) y, (int) width, (int) height, 15, 15);
		g.setColor(Color.BLACK);

		if (isRenderPrimitiveRect())
			g.drawRect((int) x, (int) y, (int) width, (int) height);
		else
			g.drawRoundRect((int) x, (int) y, (int) width, (int) height, 15, 15);
		g.drawLine((int)  (x + width / 2), (int) y, (int) (x + width / 2), (int) (y + height));
		
		FontMetrics fm = g.getFontMetrics();
		double centerFont = fm.getAscent() / (double) 2 - fm.getDescent() / 2;
//		double centerFont = 0;
		g.setColor(Color.BLACK);
		g.drawString(valueName, (int) (x + 45), (int) ((y + (height / 2) + centerFont)));
		
		int postfixWidth = FontUtils.getWidthOfText(fm, postfix);
		g.drawString(postfix, (int) (x + width - postfixWidth - insets.right), (int) (y + (height / 2) + centerFont));
		
		JComponent component = control.getComponent();
		if (component instanceof JSlider) {
			String value = String.valueOf(((JSlider) component).getValue());
			
			int fontWidth = FontUtils.getWidthOfText(fm, value) + insets.right;
			
			g.drawString(value, (int) (x + width - postfixWidth - insets.right - fontWidth), (int) (y + (height / 2) + centerFont));
		}
	}
}