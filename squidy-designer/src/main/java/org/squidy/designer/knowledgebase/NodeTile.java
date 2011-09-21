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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.squidy.common.license.LicenseUtil;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.designer.Designer;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.manager.data.Processor;
import org.squidy.manager.model.Processable;
import org.squidy.manager.scanner.PackageScanner;


/**
 * <code>NodeTile</code>.
 * 
 * <pre>
 * Date: Apr 30, 2009
 * Time: 8:24:57 PM
 * </pre>
 * 
 * 
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * 
 * @version $Id: NodeTile.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class NodeTile<T extends ZoomShape<?>> extends VisualShape<VisualShape<?>> {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 5839796889723869435L;

	private static final int TILES_IN_A_ROW = 6;
	private static final double TILE_GAP_HORIZONTAL = 20;
	private static final double TILE_GAP_VERTICAL = 20;
	
	private List<RepositoryItem<T>> knowledgeBaseItems = new ArrayList<RepositoryItem<T>>();
	
	public NodeTile() {
		String[] nodeTypeNames = PackageScanner.findAllClassNamesWithAnnotation(Processor.class);
//		Arrays.sort(nodeTypeNames, new Comparator<String>() {
//			public int compare(String o1, String o2) {
//				return o1.toLowerCase().compareTo(o2.toLowerCase());
//			};
//		});

		String[] licenseTypes = Designer.getInstance().getLicense().split(",");
		for (String processorType : nodeTypeNames) {
			
			Class<? extends Processable> processableClass = ReflectionUtil.loadClass(processorType);
			for (String licenseType : licenseTypes) {
				if (LicenseUtil.isObtainingLicense(processableClass, licenseType)) {
					RepositoryItem<T> knowledgeBaseItem = create(processableClass);
					addChild(knowledgeBaseItem);
					knowledgeBaseItems.add(knowledgeBaseItem);
					break;
				}
			}
		}
		
		// Sort knowledge base items by its processable class not regarding case sensitive.
		Collections.sort(knowledgeBaseItems, new Comparator<RepositoryItem<T>>() {
			public int compare(RepositoryItem<T> o1, RepositoryItem<T> o2) {
				return o1.getProcessableClass().getSimpleName().toLowerCase().compareTo(o2.getProcessableClass().getSimpleName().toLowerCase());
			}
		});
		
		arrageItems();
	}
	
	private RepositoryItem<T> create(Class<? extends Processable> processableClass) {
		RepositoryItem<T> item = new RepositoryItem<T>(processableClass);
		item.scale(0.1);
		return item;
	}
	
	private void arrageItems() {

//		Collections.sort(knowledgeBaseItems, new Comparator<KnowledgeBaseItem<T>>() {
//			public int compare(KnowledgeBaseItem<T> o1, KnowledgeBaseItem<T> o2) {
//				return o1.getProcessableClass().getSimpleName().toLowerCase().compareTo(o2.getProcessableClass().getSimpleName().toLowerCase());
//			}
//		});
		
		int column = 0;
		int row = 0;
		for (RepositoryItem<T> knowledgeBaseItem : knowledgeBaseItems) {
			
			if (!knowledgeBaseItem.getVisible()) {
				continue;
			}
			
			if  (column >= TILES_IN_A_ROW) {
				column = 0;
				row++;
			}
			
			double scale = knowledgeBaseItem.getScale();
			double x = column * (knowledgeBaseItem.getWidth() * scale) + column * TILE_GAP_HORIZONTAL;
			double y = row * (knowledgeBaseItem.getHeight() * scale) + row * TILE_GAP_VERTICAL;
			
			knowledgeBaseItem.setOffset(x, y);
			
			column++;
		}
	}
	
	public void filterTiles(String query) {
		
		// Simple case with empty query.
		if ("".equals(query)) {
			for (RepositoryItem<?> knowledgeBaseItem : knowledgeBaseItems) {
				knowledgeBaseItem.setVisible(true);
			}
			arrageItems();
			return;
		}
		
//		System.out.println("FILTER tags() OF @Processor -> IFilter interface should help.");
		
		for (RepositoryItem<?> knowledgeBaseItem : knowledgeBaseItems) {
			knowledgeBaseItem.setVisible(false);
			
			Processor processor = knowledgeBaseItem.getProcessableClass().getAnnotation(Processor.class);
			String[] tags = processor.tags();
			for (String tag : tags) {
				if (tag.toLowerCase().contains(query.trim().toLowerCase())) {
					knowledgeBaseItem.setVisible(true);
					break;
				}
			}
		}
		arrageItems();
	}
}
