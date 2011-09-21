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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.squidy.designer.shape.VisualShape;

import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>GroupedTableEntries</code>.
 * 
 * <pre>
 * Date: Apr 24, 2009
 * Time: 11:16:51 AM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: GroupedTableEntries.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class GroupedTableEntries extends VisualShape<VisualShape<?>> {
	
	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 5961786272704156613L;
	
	private static final Color BACKGROUND_COLOR = new Color(122, 122, 122, 30);
	
	private static final Insets insets = new Insets(2, 0, 2, 0);
	
	private PText group;
	
	private String groupName;
	
	/**
	 * @return the groupName
	 */
	public final String getGroupName() {
		return groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public final void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	private List<TableEntry<?>> tableEntries = new ArrayList<TableEntry<?>>();
	
	/**
	 * @return the tableEntries
	 */
	public final List<TableEntry<?>> getTableEntries() {
		return tableEntries;
	}
	
	/**
	 * @param tableEntry
	 */
	public final void addTableEntry(TableEntry<?> tableEntry) {
		
		double offsetY = group.getBoundsReference().getHeight() + insets.top + insets.bottom;
		if (tableEntries.size() > 0) {
			TableEntry<?> previousTableEntry = tableEntries.get(tableEntries.size() - 1);
			Point2D offset = previousTableEntry.getOffset();
			offsetY = offset.getY() + previousTableEntry.getFullBoundsReference().getHeight();
			
			offsetY += insets.top + insets.bottom;
		}

		tableEntry.setOffset(0, offsetY);
		
		addChild(tableEntry);
		tableEntries.add(tableEntry);
		
		refreshFullBounds = true;
//		invalidateFullBounds();
	}
	
	private boolean refreshFullBounds = false;
	
	/**
	 * @param groupName
	 */
	public GroupedTableEntries(String groupName) {
		this.groupName = groupName;
		
//		addPropertyChangeListener(PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
//
//			/* (non-Javadoc)
//			 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
//			 */
//			public void propertyChange(PropertyChangeEvent evt) {
//				PBounds fullBounds = (PBounds) evt.getNewValue();
//				
//				Rectangle2D bounds = parentToLocal((PBounds) fullBounds.clone());
//				setBounds(bounds);
//			}
//		});
		
		group = new PText("".equals(groupName) ? "Common" : groupName);
		group.setFont(internalFont.deriveFont(20f));
		group.setOffset(15, 0);
		addChild(group);
	}
	
//	/* (non-Javadoc)
//	 * @see edu.umd.cs.piccolo.PNode#computeFullBounds(edu.umd.cs.piccolo.util.PBounds)
//	 */
//	@Override
//	public PBounds computeFullBounds(PBounds dstBounds) {
//		PBounds fullBounds = super.computeFullBounds(dstBounds);
//		
////		if (refreshFullBounds) {
////			refreshFullBounds = false;
////			return new PBounds(fullBounds.getX(), fullBounds.getY(), fullBounds.getWidth(), fullBounds.getHeight() + 10);
////		}
//		return fullBounds;
//	}
}
