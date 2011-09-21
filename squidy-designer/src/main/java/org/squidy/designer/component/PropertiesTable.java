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

import java.awt.Insets;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.squidy.designer.shape.VisualShape;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * {@link _PTable}
 * 
 * @author Cheazy
 * 
 */
public class PropertiesTable extends VisualShape<VisualShape<?>> {

	private static final long serialVersionUID = -4550536065863504695L;

	private static final Insets insets = new Insets(10, 0, 10, 0);
	
	// #############################################################################
	// BEGIN PropertyChangeSupport
	// #############################################################################

	/**
	 * @see this{@link #firePropertyChange(int, String, Object, Object)}
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		super.firePropertyChange(-1, propertyName, oldValue, newValue);
	}

	// #############################################################################
	// END PropertyChangeSupport
	// #############################################################################

	// Contains all table entries grouped by table entry group.
	private List<GroupedTableEntries> groupedTableEntries = new ArrayList<GroupedTableEntries>();
	
	/**
	 * 
	 */
	public PropertiesTable() {
		super();
		
		GroupedTableEntries defaultGroupedTableEntries = new GroupedTableEntries("");
		groupedTableEntries.add(defaultGroupedTableEntries);
		addChild(defaultGroupedTableEntries);

		// Catch Pan Event while e.g. sliding on components
		addInputEventListener(new PBasicInputEventHandler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * edu.umd.cs.piccolo.event.PBasicInputEventHandler#processEvent
			 * (edu.umd.cs.piccolo.event.PInputEvent, int)
			 */
			@Override
			public void processEvent(PInputEvent event, int type) {
				super.processEvent(event, type);
				
				if (event.getClickCount() != 2) {
					event.setHandled(true);
				}
			}
		});
	}

	/**
	 * @param tableEntry
	 */
	public void addEntry(TableEntry<?> tableEntry) {
		addEntryToGroup(tableEntry, "");
	}
	
	/**
	 * @param tableEntry
	 * @param groupName
	 */
	public void addEntryToGroup(TableEntry<?> tableEntry, String groupName) {
		
		GroupedTableEntries groupedTableEntry = null;
		for (GroupedTableEntries tmpGroupedTableEntry : groupedTableEntries) {
			if (groupName.equals(tmpGroupedTableEntry.getGroupName())) {
				groupedTableEntry = tmpGroupedTableEntry;
				break;
			}
		}
		
		if (groupedTableEntry == null) {
			groupedTableEntry = new GroupedTableEntries(groupName);
			groupedTableEntries.add(groupedTableEntry);
			
			addChild(groupedTableEntry);
		}
		groupedTableEntry.addTableEntry(tableEntry);
		
		double offsetY = 0;
		GroupedTableEntries previousGroupedTableEntries = null;
		for (GroupedTableEntries tmpGroupedTableEntry : groupedTableEntries) {
			
			if (previousGroupedTableEntries == null) {
				previousGroupedTableEntries = tmpGroupedTableEntry;
				continue;
			}
			
			Point2D offset = previousGroupedTableEntries.getOffset();
			offsetY = offset.getY() + previousGroupedTableEntries.getFullBoundsReference().getHeight() + insets.top + insets.bottom;
			
			tmpGroupedTableEntry.setOffset(0, offsetY);
			
			previousGroupedTableEntries = tmpGroupedTableEntry;
		}
		
//		invalidateFullBounds();
		
		firePropertyChange(-1, CropScroll.CROP_SCROLLER_UPDATE, false, true);
	}

	/**
	 * 
	 */
	public void clearEntries() {
		groupedTableEntries.clear();
		removeAllChildren();
	}
	
//	/* (non-Javadoc)
//	 * @see edu.umd.cs.piccolo.PNode#addInputEventListener(edu.umd.cs.piccolo.event.PInputEventListener)
//	 */
//	@Override
//	public void addInputEventListener(PInputEventListener listener) {
//		super.addInputEventListener(listener);
//	}
	
//	/* (non-Javadoc)
//	 * @see org.squidy.designer.shape.VisualShape#paintShape(edu.umd.cs.piccolo.util.PPaintContext)
//	 */
//	@Override
//	protected void paintShape(PPaintContext paintContext) {
//		super.paintShape(paintContext);
//		
//		Graphics2D g = paintContext.getGraphics();
//		
//		g.setColor(Color.YELLOW);
//		g.draw(parentToLocal(getBoundsReference()));
//	}
}