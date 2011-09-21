/**
 * 
 */
package org.squidy.nodes;

import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataAnalog;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;


/**
 * @author mahsajenabi
 *
 */
@XmlType(name = "Compass")
@Processor(
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	name = "Compass",
	tags = {"compass", "magnetic", "heading" },
	status = Status.UNSTABLE
)
public class Compass extends AbstractNode {
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		double magneticHeading = 0;
		double y = 0;
		
		List<DataAnalog> dataAnalogs = DataUtility.getDataOfType(DataAnalog.class, dataContainer);
		
		for (DataAnalog dataAnalog : dataAnalogs) {
			if (dataAnalog.hasAttribute(iPhone.HEADING_MAGNETIC)) {
				magneticHeading = dataAnalog.getValue();
			}
			else if (dataAnalog.hasAttribute(iPhone.HEADING_Y)) {
				y = dataAnalog.getValue();
			}
		}
		
		publish(new DataPosition2D(Compass.class, magneticHeading,y));
		
		return super.preProcess(dataContainer);
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#postProcess(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public IDataContainer postProcess(IDataContainer dataContainer) {
		return super.postProcess(dataContainer);
	}

	public IData process(DataAnalog dataAnalog) {
		
		if (dataAnalog.hasAttribute(iPhone.HEADING_MAGNETIC)) {
			System.out.println("HEADING_MAGNETIC HAS VALUE: " + dataAnalog.getValue());
		}
		
		return dataAnalog;
	}
}
