package org.squidy.nodes.optitrack.multicursor;

import java.util.ArrayList;
import java.util.List;
import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent.KeyBinding;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.optitrack.RigidBody.RBIDDomainProvider;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;


/*<code>Merger2D</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simo Faeh, < href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de</>, University f Konstanz
* 
* @version $Id: OptitrackPrint.java 373 2010-08-08 12:06:21Z raedle $
*/
@XmlType(name = "Merger2D")
@Processor(
	name = "Merger2D",
	icon = "/org/squidy/nodes/image/48x48/merge2D.png",
	description = "Merges DataPosition2D from different Pipelines to a single DataContainer",
	types = { Processor.Type.FILTER },
	tags = { "merger", "dataposition2d" }
)

public class Merger2D extends AbstractNode {
	

	private int currentGroupID = 0;
	private List<DataPosition2D> d2dList;
	public void process(DataPosition2D d2d)
	{
		if (d2dList == null)
			d2dList = new ArrayList<DataPosition2D>();
		if (d2d.hasAttribute(DataConstant.GROUP_ID))
		{
			if (TrackingUtility.getAttributesBoolean(d2d, TrackingConstant.MERGEDIRECTLY) == true)
			{
				d2dList.add(d2d);
				publish(d2dList);
				d2dList = new ArrayList<DataPosition2D>();
			}
			else
			{
				if (Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString()) != currentGroupID)
				{
					currentGroupID = Integer.valueOf(d2d.getAttribute(DataConstant.GROUP_ID).toString());
					publish(d2dList);
					d2dList = new ArrayList<DataPosition2D>();
					d2dList.add(d2d);
				}else
				{
					d2dList.add(d2d);
				}
			}
		}
	}
	
}
