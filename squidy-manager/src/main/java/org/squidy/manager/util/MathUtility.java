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

package org.squidy.manager.util;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;


public class MathUtility {

	public static final int X_AXIS = 1;
	public static final int Y_AXIS = 2;
	public static final int Z_AXIS = 3;

	private double[][] m6d = new double[3][3];
	private DataPosition6D pos6d;
	public MathUtility(){
		return;
	}
    
	public double[][] dataPosition6D2matrix(DataPosition6D obj6D){
//	     Matrix Index		   	 6D Index
//     0     1     2	          0   1   2
//	0	0,0   0,1   0,2        0  rxx rxy rxz
//	1   1,0	  1,1   1,2        1  ryx ryy ryz
//	2   2,0	  2,1	2,2	       2  rzx rzy rzz

		m6d[0][0] = obj6D.getM00();
		m6d[0][1] = obj6D.getM01();
		m6d[0][2] = obj6D.getM02();

		m6d[1][0] = obj6D.getM10();
		m6d[1][1] = obj6D.getM11();
		m6d[1][2] = obj6D.getM12();

		m6d[2][0] = obj6D.getM20();
		m6d[2][1] = obj6D.getM21();
		m6d[2][2] = obj6D.getM22();

		return m6d;
	}

	public DataPosition6D matrix2DataPosition6D(double[][] m6d){
		pos6d = new DataPosition6D(AbstractNode.class,
				0, 0, 0,
				0 ,0, 0, 0, 0, 0, 0, 0, 0,
				0, 0,0,0);

		pos6d.setM00(m6d[0][0]);
		pos6d.setM01(m6d[0][1]);
		pos6d.setM02(m6d[0][2]);

		pos6d.setM10(m6d[1][0]);
		pos6d.setM11(m6d[1][1]);
		pos6d.setM12(m6d[1][2]);

		pos6d.setM20(m6d[2][0]);
		pos6d.setM21(m6d[2][1]);
		pos6d.setM22(m6d[2][2]);

		return pos6d;
	}
//	rotates point p arount point o using the roationmatrix mRot
//	translateBack -> true = translate p back into original koordinate system after rotating it
//				  -> false = don't translate p back into orignial koordinate system after rotating it
	public DataPosition3D rotatePoint(DataPosition3D p, DataPosition3D o, DataPosition6D mRot, boolean translateBack, boolean transposeMatrix){
		double[] p1 = new double[3];
		double[] o1 = new double[3];
		double[] f1 = new double[3];
		p1[0] = p.getX();
		p1[1] = p.getY();
		p1[2] = p.getZ();
		o1[0] = o.getX();
		o1[1] = o.getY();
		o1[2] = o.getZ();
		if (transposeMatrix)
			f1 =  rotatePoint(p1, o1, transpose(dataPosition6D2matrix(mRot)),translateBack);
		else
			f1 =  rotatePoint(p1, o1, dataPosition6D2matrix(mRot),translateBack);
	    p.setX(f1[0]);
	    p.setY(f1[1]);
	    p.setZ(f1[2]);
	    return p;
	}
	public Point3d rotatePoint(Vector3d pv, Point3d ov, DataPosition6D mRot, boolean translateBack)
	{
		double out[] = new double[3];
		double p[] = {pv.x,pv.y,pv.z};
		double o[] = {ov.x,ov.y,ov.z};
		out = rotatePoint(p,o,dataPosition6D2matrix(mRot),translateBack);
		return new Point3d(out[0],out[1],out[2]);	
	}
	public Vector3d rotatePoint(Vector3d pv, Vector3d ov, double[][] mRot, boolean translateBack){
		double out[] = new double[3];
		double p[] = {pv.x,pv.y,pv.z};
		double o[] = {ov.x,ov.y,ov.z};
		out = rotatePoint(p,o,mRot,translateBack);
		return new Vector3d(out[0],out[1],out[2]);
	
	}
	public Vector3d rotatePoint(Vector3d pv, Vector3d ov, double[][] mRot, boolean translateBack,boolean transpose){
		if (transpose)
		   return rotatePoint(pv,ov,transpose(mRot),translateBack);
		else
		  return rotatePoint(pv,ov,mRot,translateBack);
	}
	public double[] rotatePoint(double[] p, double[] o, Matrix3d mRot, boolean translateBack){
		m6d[0][0] = mRot.m00;
		m6d[0][1] = mRot.m01;
		m6d[0][2] = mRot.m02;

		m6d[1][0] = mRot.m10;
		m6d[1][1] = mRot.m11;
		m6d[1][2] = mRot.m12;

		m6d[2][0] = mRot.m20;
		m6d[2][1] = mRot.m21;
		m6d[2][2] = mRot.m22;
		return rotatePoint(p, o, m6d, translateBack);
	}
	public DataPosition3D rotatePoint(DataPosition3D d3d, DataPosition6D d6d, double[][] mRot, boolean translateBack, boolean transposeMatrix) {
		double[] p = {d3d.getX(),d3d.getY(),d3d.getZ()};
		double[] o = {d6d.getX(),d6d.getY(),d6d.getZ()};
		double[] r;
		if (transposeMatrix)
			r =  rotatePoint(p,o,mRot,translateBack);
		else
			r =  rotatePoint(p,o,transpose(mRot),translateBack);
		d3d.setX(r[0]);
		d3d.setY(r[1]);
		d3d.setZ(r[2]);
		return d3d;
	}	
	public double[] rotatePoint(double[] p, double[] o, double[][] mRot, boolean translateBack){

//     Matrix Index
//	         0     1     2	   3
//		0	0,0   0,1   0,2   0,3
//		1   1,0	  1,1   1,2   1,3
//		2   2,0	  2,1	2,2	  2,3
//		3   3,0   3,1   3,2   3,3

		double[] p1 = new double[4];
		double[] p2 = new double[4];

//		1. Translate point p so that o becomes origin of coordinate system
//		Transformation Matrix, with o (xo, yo, zo)
//		       1    0    0    0
//		T =    0    1    0    0
//		       0    0    1    0
//		     -xo   -yo  -zo   1                                  1          0          0        0
//                                                               0          1          0        0
//                                                               0          0          1        0
//                                                           *  -o[0]      -o[1]      -o[2]     1
//	    Transformation:  p1 = p * T -> (p[0], p[1], p[2], 1)    p1[0]      p1[1]      p1[2]    p1[3]

		p1[0] = p[0] + (-1)*o[0];
		p1[1] = p[1] + (-1)*o[1];
		p1[2] = p[2] + (-1)*o[2];
		p1[3] = 1;

//		2. Rotate point around origin using rotationmatrix
//
//                                                          mRot[0][0]  mRot[0][1]  mRot[0][2]         0
//                                                          mRot[1][0]  mRot[1][1]  mRot[1][2]         0
//                                                          mRot[2][0]  mRot[2][1]  mRot[2][2]         0
//                                                      *       0            0             0           1
//Rotation: p2 = p1 * mRot -> (p1[0], p1[1], p1[2], p1[3])     p2[0]         p2[1]        p2[2]       p2[3]
// with p1[3] = 1:

		p2[0] = p1[0]*mRot[0][0] + p1[1]*mRot[1][0] + p1[2]*mRot[2][0];
		p2[1] = p1[0]*mRot[0][1] + p1[1]*mRot[1][1] + p1[2]*mRot[2][1];
		p2[2] = p1[0]*mRot[0][2] + p1[1]*mRot[1][2] + p1[2]*mRot[2][2];
		p2[3] = 1;

//		3. Translate point back
//		Transformation Matrix, with xo, yo, zo -> Values of point o
//	       1    0    0    0
//	T =    0    1    0    0
//	       0    0    1    0
//	      xo    yo   zo   1                                1          0          0        0
//                                                         0          1          0        0
//                                                         0          0          1        0
//                                                   *     o[0]       o[1]       o[2]     1
//Transformation: p2 * T -> (p2[0], p2[1], p2[2], p2[3])   p[0]       p[1]       p[2]     -
// with p2[3] = 1

//		if (p2[0]>0 || p2[1]>0 ){
//			System.out.println("Rotierter Punkt im positiven: "+p2[0]+"<-neg "+p2[1]+"<-neg "+p2[2]);
//		}

//		check if point should be translated back to a position in the original coordinate System ...
		if (translateBack == true){
//		... if so add the values of the origin to the rotated values
			p[0] = p2[0]+o[0];
			p[1] = p2[1]+o[1];
			p[2] = p2[2]+o[2];
		}
		else{
//		... if not return just the rotated values (refering to 0/0/0 as the origin)
			p[0] = p2[0];
			p[1] = p2[1];
			p[2] = p2[2];
		}

		return p;
	}

	
	public double[][] transpose(double[][] m ){
//	Matrix Index
//		       0,0 0,1 0,2
//		mT =   1,0 1,1 1,2
//	           2,0 2,1 2,2

//  Transponieren einer Matrix = Spiegeln an der Hauptdiagonale
//		-> switch columns/rows
//
//			 0,0 ... 0,n           0,0 ... m,0
//		A =   .   .   .       AT =  .   .   .
//           m,0 ... m,n           0,n ... m,n

		double[][] mT = new double[3][3];
//		1. column mT = 1. row m
		mT[0][0] = m[0][0];
		mT[1][0] = m[0][1];
		mT[2][0] = m[0][2];
//		2. column mT = 2. row m
		mT[0][1] = m[1][0];
		mT[1][1] = m[1][1];
		mT[2][1] = m[1][2];
//		3. column mT = 3. row m
		mT[0][2] = m[2][0];
		mT[1][2] = m[2][1];
		mT[2][2] = m[2][2];

		return mT;

	}

//  Distance between point (x1, y1, z1) and point (x2, y2, z2)
	
	
	public double euclidDist(double x1, double y1, double z1,
							 double x2, double y2, double z2){
		double dist = 0;
		dist = Math.sqrt(
				(x1 - x2)*(x1 - x2) +
				(y1 - y2)*(y1 - y2) +
				(z1 - z2)*(z1 - z2)
				);

		return dist;
	}

//  Distance between point (x1, y1, z1) and point (x2, y2, z2)
	public double euclidDist(DataPosition3D d1,DataPosition3D d2){
		double dist = 0;
		dist = Math.sqrt(
				(d1.getX() - d2.getX())*(d1.getX() - d2.getX()) +
				(d1.getY() - d2.getY())*(d1.getY() - d2.getY()) +
				(d1.getZ() - d2.getZ())*(d1.getZ() - d2.getZ())
				);

		return dist;
	}
//  Distance between point (x1, y1, z1) and point (x2, y2, z2)
	public double euclidDist(Point3d d1,Point3d d2){
		double dist = 0;
		dist = Math.sqrt(
				(d1.getX() - d2.getX())*(d1.getX() - d2.getX()) +
				(d1.getY() - d2.getY())*(d1.getY() - d2.getY()) +
				(d1.getZ() - d2.getZ())*(d1.getZ() - d2.getZ())
				);

		return dist;
	}	
//  Distance between point (x1, y1, z1 = 0) and point (x2, y2, z2 = 0)
	public double euclidDist2D(DataPosition3D d1,DataPosition3D d2){
		double dist = 0;
		dist = Math.sqrt(
				(d1.getX() - d2.getX())*(d1.getX() - d2.getX()) +
				(d1.getZ() - d2.getZ())*(d1.getZ() - d2.getZ())
				);

		return dist;
	}
//	Rotate matrix around rotationAxis using the specified angle positiv angle in clockwise direction
//	If axis of rotation is       Direction of positiv rotation is
//	  MathUtility.X_AXIS         y to z
//	  MathUtility.Y_AXIS         z to x
//	  MathUtility.Z_AXIS         x to y

	public double[][] rotateMatrix(int rotationAxis, double[][] m, double angle ){
		if (angle == 0){
			return m;
		}
		
		if (m == null)
		{
			m = new double[3][3];
			m[0][0]= 1;
			m[0][1]= 0;
			m[0][2]= 0;

			m[1][0]= 0;
			m[1][1]= 1;
			m[1][2]= 0;

			m[2][0]= 0;
			m[2][1]= 0;
			m[2][2]= 1;			
		}
		if (rotationAxis != X_AXIS && rotationAxis != Y_AXIS && rotationAxis != Z_AXIS){
			return m;
		}

//	     Matrix Index
//        0     1     2	   3
//	0	0,0   0,1   0,2   0,3
//	1   1,0	  1,1   1,2   1,3
//	2   2,0	  2,1	2,2	  2,3
//	3   3,0   3,1   3,2   3,3
		double[][] mT = new double[3][3];
		double[][] mNew = new double[3][3];

//		Transformationsmatrix setzen
		switch(rotationAxis){
		case(X_AXIS):
		{
			mT[0][0]= 1;
			mT[0][1]= 0;
			mT[0][2]= 0;

			mT[1][0]= 0;
			mT[1][1]= (double)Math.cos(angle);
			mT[1][2]= (double)Math.sin(angle);

			mT[2][0]= 0;
			mT[2][1]= (-1.0f)* (double)Math.sin(angle);
			mT[2][2]= (double)Math.cos(angle);

			break;
		}
		case(Y_AXIS):
		{
			mT[0][0]= (double)Math.cos(angle);
			mT[0][1]= 0;
			mT[0][2]= (-1.0f) * (double)Math.sin(angle);

			mT[1][0]= 0;
			mT[1][1]= 1;
			mT[1][2]= 0;

			mT[2][0]= (double)Math.sin(angle);
			mT[2][1]= 0;
			mT[2][2]= (double)Math.cos(angle);


			break;
		}
		case(Z_AXIS):{
			mT[0][0]= (double)Math.cos(angle);
			mT[0][1]= (double)Math.sin(angle);
			mT[0][2]= 0;

			mT[1][0]= (-1.0f)*(double)Math.sin(angle);
			mT[1][1]= (double)Math.cos(angle);
			mT[1][2]= 0;

			mT[2][0]= 0;
			mT[2][1]= 0;
			mT[2][2]= 1;

			break;
		}

		}

//		Neue Matrix berechnen ...
//	     					0,0   0,1   0,2
//	   						1,0	  1,1   1,2  <- mT
// 	 		  m				2,0	  2,1	2,2
//      	  |			*
//		0,0   0,1   0,2     0,0   0,1   0,2
//	    1,0	  1,1   1,2     1,0	  1,1   1,2  <-mN
//	    2,0	  2,1	2,2	    2,0	  2,1	2,2

		mNew[0][0] = m[0][0]*mT[0][0]+ m[0][1]*mT[1][0]+ m[0][2]*mT[2][0];
		mNew[0][1] = m[0][0]*mT[0][1]+ m[0][1]*mT[1][1]+ m[0][2]*mT[2][1];
		mNew[0][2] = m[0][0]*mT[0][2]+ m[0][1]*mT[1][2]+ m[0][2]*mT[2][2];

		mNew[1][0] = m[1][0]*mT[0][0]+ m[1][1]*mT[1][0]+ m[1][2]*mT[2][0];
		mNew[1][1] = m[1][0]*mT[0][1]+ m[1][1]*mT[1][1]+ m[1][2]*mT[2][1];
		mNew[1][2] = m[1][0]*mT[0][2]+ m[1][1]*mT[1][2]+ m[1][2]*mT[2][2];

		mNew[2][0] = m[2][0]*mT[0][0]+ m[2][1]*mT[1][0]+ m[2][2]*mT[2][0];
		mNew[2][1] = m[2][0]*mT[0][1]+ m[2][1]*mT[1][1]+ m[2][2]*mT[2][1];
		mNew[2][2] = m[2][0]*mT[0][2]+ m[2][1]*mT[1][2]+ m[2][2]*mT[2][2];

//      ... und zurückgeben
		return mNew;
	}

//	Rotates matrix m around all axes using the specified angles
//	-180 <= angleX <= +180
//	-90  <= angleY <= +90
//	-180 <= angleZ <= +180

	public double[][] rotateMatrix(double[][] m, double angleX, double angleY, double angleZ ){

//	     Matrix Index
//        0     1     2	   
//	0	0,0   0,1   0,2   
//	1   1,0	  1,1   1,2   
//	2   2,0	  2,1	2,2	  
		double[][] mT = new double[3][3];
		double[][] mNew = new double[3][3];
		if (m == null)
		{
			m = new double[3][3];
			m[0][0]= 1;
			m[0][1]= 0;
			m[0][2]= 0;

			m[1][0]= 0;
			m[1][1]= 1;
			m[1][2]= 0;

			m[2][0]= 0;
			m[2][1]= 0;
			m[2][2]= 1;			
		}
		
//		Transformationsmatrix setzen
	
		
		mT[0][0]= Math.cos(angleZ)*Math.cos(angleY);
		mT[0][1]= (-1.0)*Math.sin(angleZ)*Math.cos(angleY);
		mT[0][2]= Math.sin(angleY);

		mT[1][0]= Math.sin(angleZ)*Math.cos(angleX)+Math.cos(angleZ)*Math.sin(angleY)*Math.sin(angleX);
		mT[1][1]= Math.cos(angleZ)*Math.cos(angleX)-Math.sin(angleZ)*Math.sin(angleY)*Math.sin(angleX);
		mT[1][2]= (-1.0)*Math.cos(angleY)*Math.sin(angleX);

		mT[2][0]= Math.sin(angleZ)*Math.sin(angleX)-Math.cos(angleZ)*Math.sin(angleY)*Math.cos(angleX);
		mT[2][1]= Math.cos(angleZ)*Math.sin(angleX)+Math.sin(angleZ)*Math.sin(angleY)*Math.cos(angleX);
		mT[2][2]= Math.cos(angleY)*Math.cos(angleX);

//		Neue Matrix berechnen ...
//	     					0,0   0,1   0,2
//	   						1,0	  1,1   1,2  <- mT
// 	 		  m				2,0	  2,1	2,2
//      	  |			*
//		0,0   0,1   0,2     0,0   0,1   0,2
//	    1,0	  1,1   1,2     1,0	  1,1   1,2  <-mN
//	    2,0	  2,1	2,2	    2,0	  2,1	2,2

		mNew[0][0] = m[0][0]*mT[0][0]+ m[0][1]*mT[1][0]+ m[0][2]*mT[2][0];
		mNew[0][1] = m[0][0]*mT[0][1]+ m[0][1]*mT[1][1]+ m[0][2]*mT[2][1];
		mNew[0][2] = m[0][0]*mT[0][2]+ m[0][1]*mT[1][2]+ m[0][2]*mT[2][2];

		mNew[1][0] = m[1][0]*mT[0][0]+ m[1][1]*mT[1][0]+ m[1][2]*mT[2][0];
		mNew[1][1] = m[1][0]*mT[0][1]+ m[1][1]*mT[1][1]+ m[1][2]*mT[2][1];
		mNew[1][2] = m[1][0]*mT[0][2]+ m[1][1]*mT[1][2]+ m[1][2]*mT[2][2];

		mNew[2][0] = m[2][0]*mT[0][0]+ m[2][1]*mT[1][0]+ m[2][2]*mT[2][0];
		mNew[2][1] = m[2][0]*mT[0][1]+ m[2][1]*mT[1][1]+ m[2][2]*mT[2][1];
		mNew[2][2] = m[2][0]*mT[0][2]+ m[2][1]*mT[1][2]+ m[2][2]*mT[2][2];

//      ... und zurückgeben
		return mNew;
	}	
//	public static Matrix3d DataPosition6D2Matrix3d( DataPosition6D d6d)
//	{
//		 Matrix3d m3d = new Matrix3d();
//		 m3d.setM00(d6d.getM00());
//		 m3d.setM01(d6d.getM01());
//		 m3d.setM02(d6d.getM02());
//
//		 m3d.setM10(d6d.getM10());
//		 m3d.setM11(d6d.getM11());
//		 m3d.setM12(d6d.getM12());
//
//		 m3d.setM20(d6d.getM20());
//		 m3d.setM21(d6d.getM21());
//		 m3d.setM22(d6d.getM22());
//		 return m3d;
//	}
//	public static DataPosition6D Matrix3d2DataPosition6D (DataPosition6D d6d, Matrix3d m3d)
//	{
//		d6d.setM00(m3d.getM00());
//		d6d.setM01(m3d.getM01());
//		d6d.setM02(m3d.getM02());
//		
//		d6d.setM10(m3d.getM10());
//		d6d.setM11(m3d.getM11());
//		d6d.setM12(m3d.getM12());
//		
//		d6d.setM20(m3d.getM20());
//		d6d.setM21(m3d.getM21());
//		d6d.setM22(m3d.getM22());
//		return d6d;
//	}
}