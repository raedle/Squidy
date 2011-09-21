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
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.squidy.SquidyException;
import org.squidy.common.license.LicenseUtil;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.designer.Designer;
import org.squidy.designer.constant.Constants;
import org.squidy.designer.shape.VisualShape;
import org.squidy.designer.shape.ZoomShape;
import org.squidy.designer.zoom.ActionShape;
import org.squidy.manager.data.Processor;
import org.squidy.manager.heuristics.Heuristic;
import org.squidy.manager.heuristics.Match;
import org.squidy.manager.model.EmptyNode;
import org.squidy.manager.model.Pipeline;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Processable;
import org.squidy.manager.scanner.PackageScanner;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>KnowledgeBase</code>.
 * 
 * <pre>
 * Date: Feb 15, 2009
 * Time: 5:26:49 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: AdvancedKnowledgeBase.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class AdvancedKnowledgeBase<T extends ZoomShape<VisualShape<?>>> extends PNode {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -8765164642810105737L;

	public static final String KNOWLEDGE_BASE_DUMMY_TYPE = "org.squidy.extension.dynamic.DummyValve";
	
	private Processor.Type mode = Processor.Type.INPUT;

	// Contains current knowledge base items.
	private final List<PNode> CURRENT_KNOWLEDGE_BASE_ITEMS = new ArrayList<PNode>();

	private List<RepositoryItem<T>> AVAILABLE_INPUTS = new ArrayList<RepositoryItem<T>>();
	private List<RepositoryItem<T>> AVAILABLE_FILTERS = new ArrayList<RepositoryItem<T>>();
	private List<RepositoryItem<T>> AVAILABLE_OUTPUTS = new ArrayList<RepositoryItem<T>>();

	// The required amount of knowledge base item within the knowledge base.
	private static final int REQUIRED_KNOWLEDGE_BASE_ITEM_SIZE = 8;

	private boolean isWorkspace = false;
	
	public AdvancedKnowledgeBase(boolean isWorkspace) {
		this.isWorkspace = isWorkspace;
		
		setBounds(Constants.DEFAULT_KNOWLEDGE_BASE_BOUNDS);

		if (!isWorkspace) {
			String[] inputTypes = PackageScanner.findAllClassesWithProcessorAndType(Processor.Type.INPUT);
			String[] filterTypes = PackageScanner.findAllClassesWithProcessorAndType(Processor.Type.FILTER);
			String[] outputTypes = PackageScanner.findAllClassesWithProcessorAndType(Processor.Type.OUTPUT);
	
			// Create available knowledge base items for each Processor.Type.
			createItems(AVAILABLE_INPUTS, inputTypes);
			createItems(AVAILABLE_FILTERS, filterTypes);
			createItems(AVAILABLE_OUTPUTS, outputTypes);
	
			// Prepare knowledge base without previous node.
			prepareKnowledgeBase(null);
		}
		else {
			CURRENT_KNOWLEDGE_BASE_ITEMS.add(createItem(Pipeline.class));
			arrange(CURRENT_KNOWLEDGE_BASE_ITEMS);
			invalidatePaint();
		}
	}

	/**
	 * @param itemContainer
	 * @param itemClass
	 */
	private void createItems(List<RepositoryItem<T>> itemContainer, String[] itemClasses) {
		
		String[] licenseTypes = Designer.getInstance().getLicense().split(",");
		for (String processorType : itemClasses) {
			
//			// TODO [RR]: Set nodes deprecqted if annotation is present
//			if (type.isAnnotationPresent(Deprecated.class)) {
//				// Deprecated deprecated = type.getAnnotation(Deprecated.class);
//				// zoomDevice.setDeprecated(true);
//			}
			
			Class<? extends Processable> processableClass = ReflectionUtil.loadClass(processorType);
			for (String licenseType : licenseTypes) {
				if (LicenseUtil.isObtainingLicense(processableClass, licenseType)) {
					itemContainer.add(createItem(processableClass));
					break;
				}
			}
		}
	}

	/**
	 * @param type
	 */
	private void prepareKnowledgeBase(String type) {

		if (Designer.heuristics == null)
			return;
		
		// Remove all existing items.
		CURRENT_KNOWLEDGE_BASE_ITEMS.clear();

		// Get heuristic for type.
		Heuristic heuristic;
		if (type == null) {
			heuristic = Designer.heuristics.getDefaultHeuristic();
		}
		else {
			heuristic = Designer.heuristics.getHeuristicForType(type);

			if (heuristic == null) {
				heuristic = Designer.heuristics.getDefaultHeuristic();
			}
		}

		// Create sections for input, filter and output.
		List<RepositoryItem<T>> sectionInput = new ArrayList<RepositoryItem<T>>();
		List<RepositoryItem<T>> sectionFilter = new ArrayList<RepositoryItem<T>>();
		List<RepositoryItem<T>> sectionOutput = new ArrayList<RepositoryItem<T>>();

		Processor.Type majorProcessorType = null;
		for (Match match : heuristic.getMatches()) {

			// Set major processor type -> used to fill up section if sum of
			// section count is less than required.
			if (majorProcessorType == null) {
				majorProcessorType = match.getProcessorType();
			}

			if (!checkKnowledgeBaseItemBalance(sectionInput, sectionFilter, sectionOutput)) {
				break;
			}

			addMatch(match, sectionInput, sectionFilter, sectionOutput);
		}

		// Check each section for minimum size.
		checkKnowledgeBaseSectionForMinimumSize(sectionInput, AVAILABLE_INPUTS);
		checkKnowledgeBaseSectionForMinimumSize(sectionFilter, AVAILABLE_FILTERS);
		checkKnowledgeBaseSectionForMinimumSize(sectionOutput, AVAILABLE_OUTPUTS);

		if (checkKnowledgeBaseItemBalance(sectionInput, sectionFilter, sectionOutput)) {
			int missing = REQUIRED_KNOWLEDGE_BASE_ITEM_SIZE
					- (sectionInput.size() + sectionFilter.size() + sectionOutput.size());
			if (majorProcessorType != null) {
				switch (majorProcessorType) {
				case INPUT:
					fillUpKnowledgeBaseSection(sectionInput, AVAILABLE_INPUTS, missing);
					break;
				case FILTER:
					fillUpKnowledgeBaseSection(sectionFilter, AVAILABLE_FILTERS, missing);
					break;
				case OUTPUT:
					fillUpKnowledgeBaseSection(sectionOutput, AVAILABLE_OUTPUTS, missing);
					break;
				}
			}
		}
		
		CURRENT_KNOWLEDGE_BASE_ITEMS.addAll(sectionInput);
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(new Separator());
		CURRENT_KNOWLEDGE_BASE_ITEMS.addAll(sectionFilter);
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(new Separator());
		CURRENT_KNOWLEDGE_BASE_ITEMS.addAll(sectionOutput);
		
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(new Separator());
		
		// TODO [RR]: THIS IS A HACK!!!
//		CURRENT_KNOWLEDGE_BASE_ITEMS.add(createItem(KNOWLEDGE_BASE_DUMMY_TYPE));
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(createItem(EmptyNode.class));
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(createItem(Pipeline.class));
		NodeRepositoryShape<?> knowledgeBaseNew = new NodeRepositoryShape<ZoomShape<VisualShape<?>>>();
		knowledgeBaseNew.setScale(0.075);
		CURRENT_KNOWLEDGE_BASE_ITEMS.add(knowledgeBaseNew);
		
		arrange(CURRENT_KNOWLEDGE_BASE_ITEMS);

		invalidatePaint();
	}

	/**
	 * @param nodes
	 */
	private void arrange(List<? extends PNode> nodes) {
		removeAllChildren();

		double widthAggregate = 0;

		for (PNode node : nodes) {
			addChild(node);
			node.setOffset(((widthAggregate + 185) * node.getScale()), 200 * node.getScale());
			widthAggregate += node.getWidth() + 185;
		}
	}

	/**
	 * Checks knowledge base for section item balance.
	 * 
	 * @param sections
	 * @return
	 */
	private boolean checkKnowledgeBaseItemBalance(List<RepositoryItem<T>>... sections) {
		int itemCount = 0;
		int emptySectionCount = 0;
		for (List<RepositoryItem<T>> section : sections) {
			if (section.isEmpty()) {
				emptySectionCount++;
			}
			itemCount += section.size();
		}

		// Check if 2 sections are empty and item count has already required
		// items size - 2 items.
		if (emptySectionCount == 2 && itemCount >= REQUIRED_KNOWLEDGE_BASE_ITEM_SIZE - 2) {
			return false;
		}

		return itemCount < REQUIRED_KNOWLEDGE_BASE_ITEM_SIZE;
	}

	/**
	 * @param section
	 * @param availableNodes
	 * @param amount
	 */
	private void fillUpKnowledgeBaseSection(List<RepositoryItem<T>> section,
			List<RepositoryItem<T>> availableNodes, int amount) {
		for (RepositoryItem<T> item : availableNodes) {
			if (!section.contains(item)) {
				section.add(item);
				amount--;
			}

			if (amount == 0) {
				break;
			}
		}
	}

	/**
	 * @param section
	 * @param availableNodes
	 */
	private void checkKnowledgeBaseSectionForMinimumSize(List<RepositoryItem<T>> section,
			List<RepositoryItem<T>> availableNodes) {
		if (section.size() == 0 && !availableNodes.isEmpty()) {
			section.add(availableNodes.get(0));
		}
	}

	/**
	 * @param match
	 * @param sectionInput
	 * @param sectionFilter
	 * @param sectionOutput
	 */
	private void addMatch(Match match, List<RepositoryItem<T>> sectionInput,
			List<RepositoryItem<T>> sectionFilter, List<RepositoryItem<T>> sectionOutput) {
		
		Class<? extends Processable> processableClass = ReflectionUtil.loadClass(match.getType());
		RepositoryItem<T> item = createItem(processableClass);

		switch (match.getProcessorType()) {
		case INPUT:
			sectionInput.add(item);
			break;
		case FILTER:
			sectionFilter.add(item);
			break;
		case OUTPUT:
			sectionOutput.add(item);
			break;
		}
	}

	/**
	 * @param type
	 * @return
	 */
	private RepositoryItem<T> createItem(Class<? extends Processable> processableClass) throws SquidyException {
		RepositoryItem<T> item = new RepositoryItem<T>(processableClass);
		item.scale(0.075);

		if (!isWorkspace) {
			item.addPropertyChangeListener(RepositoryItem.ITEM_SELECTION, new PropertyChangeListener() {
	
				public void propertyChange(PropertyChangeEvent evt) {
	
					ActionShape<?, Piping> actionShape = (ActionShape<?, Piping>) evt.getNewValue();
					Processable processable = actionShape.getProcessable();
	
					String type = processable.getClass().getName();
	
					switch (mode) {
					case INPUT:
						prepareKnowledgeBase(type);
						break;
					case FILTER:
						prepareKnowledgeBase(type);
						break;
					case OUTPUT:
						prepareKnowledgeBase(type);
						break;
					}
				}
			});
		}

		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
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

		// TODO: use global setting
		boolean isRenderPrimitiveRect = true;

		g.setColor(Constants.Color.COLOR_SHAPE_BACKGROUND);
//		g.setColor(Color.WHITE);
		if (isRenderPrimitiveRect)
			g.fillRect(x, y, width, height);
		else
			g.fillRoundRect(x, y, width, height, 25, 25);
		
		g.setColor(Color.BLACK);
		if (isRenderPrimitiveRect)
			g.drawRect(x, y, width, height);
		else
			g.drawRoundRect(x, y, width, height, 25, 25);
	}

	/**
	 * <code>Separator</code>.
	 * 
	 * <pre>
	 * Date: Mar 27, 2009
	 * Time: 6:52:15 PM
	 * </pre>
	 * 
	 * @author <pre>
	 * Roman R&amp;aumldle
	 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
	 * Human-Computer Interaction Group
	 * University of Konstanz
	 * </pre>
	 * 
	 * @version $Id: AdvancedKnowledgeBase.java 772 2011-09-16 15:39:44Z raedle $
	 * @since 1.0.0
	 */
	private class Separator extends PNode {

		private Separator() {
			setBounds(0, 0, 100, 1200);
			setScale(0.075);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
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

			// TODO: use global setting
			boolean isRenderPrimitiveRect = true;

			g.setColor(Color.BLACK);
			for (int i = -50; i < height - 50; i += 150) {
				if (isRenderPrimitiveRect)
					g.fillRect(x, y + i, 50, 100);
				else
					g.fillOval(x, y + i, 50, 100);
			}

			// g.drawRoundRect(x, y, width, height, 5, 5);
		}
	}
}
