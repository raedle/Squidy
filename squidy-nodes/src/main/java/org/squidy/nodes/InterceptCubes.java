package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>Intercept Display</code>
 * 
 * @author Stephanie Foehrenbach
 *         Workaround until OptiTrack Coordinate System can be placed in bottom 
 *         right corner of Cubes (currently not possible due to lack of hardware,
 *         otherwise use interceptDisplay and change the width/height adjustable of the 
 *         OptiTrack Tracking Node)
 *         
 * @version $Id: InterceptCubes.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
@XmlType(name = "InterceptCubs")
@Processor(
	name = "Intercept Cubes",
	tags = { "cubes", "intercept", "display", "6dof" },
	types = { Processor.Type.FILTER },
	status = Status.UNSTABLE
)
public class InterceptCubes extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "left-down-x")
	@Property(
		name = "Left down X",
		description = "Left down x-value of cubes in relation to OptiTrack Coordinate System in mm."
	)
	@TextField
	private float leftDownX = 1000;

	/**
	 * @return the leftDownX
	 */
	public final float getLeftDownX() {
		return leftDownX;
	}

	/**
	 * @param width the leftDownX to set
	 */
	public final void setLeftDownX(float leftDownX) {
		this.leftDownX = leftDownX;
	}

	@XmlAttribute(name = "left-down-y")
	@Property(
		name = "Left down Y",
		description = "Left down y-value of cubes in relation to OptiTrack Coordinate System in mm."
	)
	@TextField
	private float leftDownY = 1200;

	/**
	 * @return the leftDownX
	 */
	public final float getLeftDownY() {
		return leftDownY;
	}

	/**
	 * @param width the leftDownY to set
	 */
	public final void setLeftDownY(float leftDownY) {
		this.leftDownY = leftDownY;
	}

	@XmlAttribute(name = "left-down-z")
	@Property(
		name = "Left down Z",
		description = "Left down Z-value of cubes in relation to OptiTrack Coordinate System in mm."
	)
	@TextField
	private float leftDownZ = -60;

	/**
	 * @return the leftDownZ
	 */
	public final float getLeftDownZ() {
		return leftDownZ;
	}

	/**
	 * @param width the leftDownZ to set
	 */
	public final void setLeftDownZ(float leftDownZ) {
		this.leftDownZ = leftDownZ;
	}


	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	
	private double a1, a2, a3, ba1, ba2, ba3, d, x, y;

	/**
	 * {@inheritDoc}
	 */
	public synchronized IData process(DataPosition6D dataPosition6D) {

		// Target position
		a1 = dataPosition6D.getX();
		a2 = dataPosition6D.getY();
		a3 = dataPosition6D.getZ();

		// Point (0,0,z) is rotated using the rotation matrix of the target
		// by using (0,0,<any value>) the result can be used imidiatly as a
		// direction vector

		// direction vector
		ba1 = dataPosition6D.getM02() * dataPosition6D.getZ();
		ba2 = dataPosition6D.getM12() * dataPosition6D.getZ();
		ba3 = dataPosition6D.getM22() * dataPosition6D.getZ();

		// This is a shortcut for:
		//
		// 1. rotate Point b=(0,0,1) using the rotation matrix of the Target
		// giving point b1
		// Rotation Matrix * b
		// 2. translate b1 using the target position a1
		//
		// b1 = pos6d.rxz * pos6d.z + pos6d.x;
		// b2 = pos6d.ryz * pos6d.z + pos6d.y;
		// b3 = pos6d.rzz * pos6d.z + pos6d.z;
		//		
		// 3. use b1 and a1 to calculate the direction vector that
		// intersects
		// both points
		// ba1 = b1 - a1;
		// ba2 = b2 - a2;
		// ba3 = b3 - a3;

		// interception directection vector with x-y-plane
		d = (-1) * (a3 / ba3);
		x = a1 + d * ba1;
		y = a2 + d * ba2;

		// Workaround: simulate, that cubes are positioned at the optiTrack origin
		x = x - leftDownX;
		y = y - leftDownY;
		// switch x Value
		x = x * (-1);
		
		
		// normalise x, y as it is expected for 2D Objects
		// @ TODO: user dataPosition6D maxX attributs to normalize
		//x = norm(x, dataPosition6D.maxX);
		//y = norm(y, dataPosition6D.maxY);
		
		if(x==-1||y==-1) return null;

		return new DataPosition2D(InterceptCubes.class, x, y);
	}

//	private double norm(double val, double bound) {
//		double tmp = val / bound;
//		tmp = (tmp > 1.0) ? 1.0 : tmp;
//		tmp = (tmp < 0.0) ? 0.0 : tmp;
//		return tmp;
//	}
	
	private double norm(double val, double bound) {
		double tmp = val / bound;
		tmp = (tmp > 1.0) ? -1.0 : tmp;
		tmp = (tmp < 0.0) ? -1.0 : tmp;
		return tmp;
	}

}
