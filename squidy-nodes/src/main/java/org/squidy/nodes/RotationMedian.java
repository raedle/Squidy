/**
 *
 */
package org.squidy.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>RotationMedian</code>.
 *
 * <pre>
 * Date: Dec 7, 2009
 * Time: 12:16:54 PM
 * </pre>
 *
 *
 * @author
 * Roman RŠdle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 *
 * @version $Id: RotationMedian.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 *
 * TODO [RR]: Clear list if token has been lifted.
 */
@XmlType(name = "Rotation Median")
@Processor(
	name = "Rotation Median",
	icon = "/org/squidy/nodes/image/48x48/rotationmedian.png",
	types = { Processor.Type.FILTER },
	tags = { "reactivision", "fiducial", "id", "token", "rotation", "median" },
	status = Status.UNSTABLE
)
public class RotationMedian extends AbstractNode {

	// Log to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(RotationMedian.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@Property(
		name = "History size",
		description = "Size of values kept as history values."
	)
	@TextField
	private int historySize = 10;

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private Map<Integer, List<DataPosition2D>> lastValues;

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		lastValues = new HashMap<Integer, List<DataPosition2D>>();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();

		lastValues.clear();
		lastValues = null;
	}

	/**
	 * @param dataPosition2D
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {

		if (!dataPosition2D.hasAttribute(TUIO.ANGLE_A)) {
			return dataPosition2D;
		}

		if (dataPosition2D.hasAttribute(TUIO.FIDUCIAL_ID)) {
			int fiducialId = (Integer) dataPosition2D.getAttribute(TUIO.FIDUCIAL_ID);

			List<DataPosition2D> lastPositions = lastValues.get(fiducialId);
			if (lastPositions == null) {
				lastPositions = new ArrayList<DataPosition2D>();
				lastValues.put(fiducialId, lastPositions);
			}

			if (lastPositions.size() >= historySize) {
				lastPositions.remove(0);
			}

			lastPositions.add(dataPosition2D);

			List<DataPosition2D> medianCollection = new ArrayList<DataPosition2D>();
			medianCollection.addAll(lastPositions);

			// Sorts last rotations collection
			Collections.sort(medianCollection, new Comparator<DataPosition2D>() {

				public int compare(DataPosition2D o1, DataPosition2D o2) {
					Float v1 = (Float) o1.getAttribute(TUIO.ANGLE_A);
					Float v2 = (Float) o2.getAttribute(TUIO.ANGLE_A);

					return v1.compareTo(v2);
				}
			});

			return medianCollection.get(medianCollection.size() / 2);
		}

		return dataPosition2D;
	}
}
