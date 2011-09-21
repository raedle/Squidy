/**
 * 
 */
package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.optitrack.utils.TrackingConstant;


/**
 * <code>TUIO</code>.
 *
 * <pre>
 * Date: July 09, 2010
 * Time: 3:12:59 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
@XmlType(name = "Augment2TUIO")
@Processor(
	name = "Augment2TUIO",
	types = { Processor.Type.FILTER },
	tags = { "tuio", "augment" }
)
public class Augment2TUIO extends AbstractNode {

	// ################################################################################
	// BEGIN OF PROPERTIES
	// ################################################################################
	
	@XmlAttribute(name = "type")
	@Property(
		name = "Type"
	)
	@ComboBox(domainProvider = TypeDomainProvider.class)
	private Type type = Type.TYPE_2D_CURSOR;
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public static class TypeDomainProvider implements DomainProvider {

		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[2];
			values[0] = new ComboBoxItemWrapper(Type.TYPE_2D_CURSOR, "2Dcur");
			values[1] = new ComboBoxItemWrapper(Type.TYPE_2D_OBJECT, "2Dobj");
			return values;
		}
	}
	
	// ################################################################################
	// END OF PROPERTIES
	// ################################################################################
	
	enum Type {
		TYPE_2D_CURSOR, TYPE_2D_OBJECT
	}
	
	enum ObjectState {
		ADD("add"), UPDATE("update"), REMOVE("remove");
		
		private String type;
		
		ObjectState(String type) {
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
	}
	
	private DataPosition2D lastPosition2D = null;
	private boolean added = false;
	
	private ObjectState objectState = null;
	
	private int frameId = 0;
	private int getSessionId() {
		if (frameId > Integer.MAX_VALUE) {
			frameId = 0;
		}
		return ++frameId;
	}
	
	public IData process(DataButton dataButton) {
		if (dataButton.getFlag()) {
			objectState = ObjectState.ADD;
			added = true;
			if (lastPosition2D != null) {
				return process(lastPosition2D);
			}
		}
		else {
			objectState = ObjectState.REMOVE;
			if (lastPosition2D != null) {
				return process(lastPosition2D);
			}
			added = false;
		}
		return dataButton;
	}
	
	public IData process(DataPosition2D dataPosition2D) {
		if (Type.TYPE_2D_CURSOR.equals(type)) {
			dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS, "/tuio/2Dcur"); // cur
			dataPosition2D.setAttribute(TUIO.SESSION_ID, Integer.valueOf(dataPosition2D.getAttribute(DataConstant.GROUP_ID).toString()));
			dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID, getSessionId());
			dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_X, 0f);
			dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_Y, 0f);
			dataPosition2D.setAttribute(TUIO.MOTION_ACCELERATION, 0f);	
		}
		else if (Type.TYPE_2D_OBJECT.equals(type)) {
			lastPosition2D = dataPosition2D.getClone();
			if (!added) {
				return null;
			}
			if (objectState != null && lastPosition2D != null) {
				dataPosition2D.setAttribute(TUIO.ORIGIN_ADDRESS, "/tuio/2Dobj"); // cur
				dataPosition2D.setAttribute(TUIO.OBJECT_STATE, objectState
						.getType());
				dataPosition2D.setAttribute(DataConstant.SESSION_ID,
						getSessionId());
				dataPosition2D.setAttribute(DataConstant.FRAME_SEQUENCE_ID,
						getSessionId());
				dataPosition2D.setAttribute(TUIO.FIDUCIAL_ID, 1); // Token ID
				dataPosition2D.setAttribute(TUIO.ANGLE_A, 0f);
				dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_X, 0f);
				dataPosition2D.setAttribute(TUIO.MOVEMENT_VECTOR_Y, 0f);
				dataPosition2D.setAttribute(TUIO.ROTATION_VECTOR_A, 0f);
				dataPosition2D.setAttribute(TUIO.MOTION_ACCELERATION, 0f);
				dataPosition2D.setAttribute(TUIO.ROTATION_ACCELERATION, 0f);

				switch (objectState) {
				case ADD:
					objectState = ObjectState.UPDATE;
					break;
				case REMOVE:
					objectState = null;
					break;
				}
			}
		}
		return dataPosition2D;
	}
}
