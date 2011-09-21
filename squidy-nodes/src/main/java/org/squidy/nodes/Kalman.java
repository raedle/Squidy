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


package org.squidy.nodes;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.kalman.KalmanFilter;
import org.squidy.nodes.kalman.KalmanModels;

import Jama.Matrix;

/**
 * <code>Kalman</code>.
 * 
 * <pre>
 * Date: Feb 11, 2008
 * Time: 1:16:01 PM
 * </pre>
 * 
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: Kalman.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 * 
 * TODO Werner: Kalman for multipoint (distance function)
 */
@XmlType(name = "Kalman")
@Processor(
	name = "Kalman",
	icon = "/org/squidy/nodes/image/48x48/kalman.png",
	description = "/org/squidy/nodes/html/Kalman.html",
//	description = "http://en.wikipedia.org/wiki/Kalman_filter",
	types = { Processor.Type.FILTER },
	tags = { "Kalman", "prediction", "movement", "touch", "finger", "path integration", "identification" }
)
public class Kalman extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "mode")
	@Property(
		name = "Mode"
	)
	@ComboBox(domainProvider = ModeDomainProvider.class)
	private int mode = MODE_POINT;

	/**
	 * @return
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param mode
	 */
	public void setMode(int mode) {
		this.mode = mode;

		resetFilters();
	}
	
	// ################################################################################

	@XmlAttribute(name = "frame-rate")
	@Property(
		name = "Frame rate",
		description = "The frame rate the Kalman is processing data.",
		suffix = "fps"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 10,
		snapToTicks = true
	)
	private int frameRate = 100;

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
		resetFilters();
	}
	
	// ################################################################################

	@XmlAttribute(name = "m-noise")
	@Property(
		name = "M-Noise",
		group = "Noise"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 1000,
		minorTicks = 100,
		majorTicks = 250,
		showTicks = true,
		showLabels = true
	)
	private int mNoise = 80;

	public int getMNoise() {
		return mNoise;
	}

	public void setMNoise(int mNoise) {
		this.mNoise = mNoise;

		resetFilters();
	}
	
	// ################################################################################

	@XmlAttribute(name = "p-noise")
	@Property(
		name = "P-Noise",
		group = "Noise"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 40000,
		minorTicks = 5000,
		majorTicks = 20000,
		showTicks = true,
		showLabels = true
	)
	private int pNoise = 30000;

	public int getPNoise() {
		return pNoise;
	}

	public void setPNoise(int pNoise) {
		this.pNoise = pNoise;

		resetFilters();
	}
	
	// ################################################################################

	@XmlAttribute(name = "pv-noise")
	@Property(
		name = "PV-Noise",
		group = "Noise"
	)
	@TextField
	private double pvNoise = 80;

	public double getPvNoise() {
		return pvNoise;
	}

	public void setPvNoise(double pvNoise) {
		this.pvNoise = pvNoise;

		resetFilters();
	}
	
	// ################################################################################

	@XmlAttribute(name = "maximum-distance-single-point")
	@Property(
		name = "Maximum distance (single point)",
		description = "Repeated single touches within the bounds determined by the " +
				"maximum distance receive the previously assigned identifier.",
		suffix = "\u0025"
	)
	@Slider(
		minimumValue = 0,
		maximumValue = 100,
		minorTicks = 10,
		majorTicks = 25,
		showTicks = true,
		showLabels = true
	)
	private int maximumDistanceSinglePoint = 50;

	/**
	 * @return
	 */
	public int getMaximumDistanceSinglePoint() {
		return maximumDistanceSinglePoint;
	}

	/**
	 * @param maximumDistanceSinglePoint
	 */
	public void setMaximumDistanceSinglePoint(int maximumDistanceSinglePoint) {
		this.maximumDistanceSinglePoint = maximumDistanceSinglePoint;
	}
	
	// ################################################################################

	@XmlAttribute(name = "maximum-distance-multi-point")
	@Property(
		name = "Maximum distance (multi point)",
		description = "Repeated multi touches within the bounds determined by the " +
				"maximum distance receive the previously assigned identifier.",
		suffix = "\u0025"
	)
	@Slider(
			minimumValue = 0,
			maximumValue = 100,
			minorTicks = 10,
			majorTicks = 25,
			showTicks = true,
			showLabels = true
		)
	private int maximumDistanceMultiPoint = 4;

	public int getMaximumDistanceMultiPoint() {
		return maximumDistanceMultiPoint;
	}

	public void setMaximumDistanceMultiPoint(int maximumDistanceMultiPoint) {
		this.maximumDistanceMultiPoint = maximumDistanceMultiPoint;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "maximum-timeout")
	@Property(
		name = "Maximum timeout",
		description = "The maximum timeout until position cache gets cleared.",
		suffix = "ms"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 5000,
		showLabels = true,
		showTicks = true,
		majorTicks = 2500,
		minorTicks = 1000
//		snapToTicks = true
	)
	private int maximumTimeout = 150;

	public int getMaximumTimeout() {
		return maximumTimeout;
	}

	public void setMaximumTimeout(int maximumTimeout) {
		this.maximumTimeout = maximumTimeout;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "smoothing-subpixels")
//	@Property(name = "Smoothing Subpixels", description = "Minimizes jittering caused by real2int casting or subpixel movements")
//	@CheckBox
	private boolean smoothingSubpixels = false;

	public boolean isSmoothingSubpixels() {
		return smoothingSubpixels;
	}

	public void setSmoothingSubpixels(boolean trackFingers) {
		this.smoothingSubpixels = trackFingers;
	}
	
// ################################################################################
	
	@XmlAttribute(name = "smoothing-range-horizontal")
	@Property(
		name = "Maximum Smoothing Range - Horizontal",
		description = "The maximum horizontal range in one-tenth of a percentage of display area for applying the smoothing.",
		suffix = "\u2030"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 1000,
		showLabels = true,
		showTicks = true,
		majorTicks = 250,
		minorTicks = 100
//		snapToTicks = true
	)
	private int smoothingRangeHorizontal = 1;

	public int getSmoothingRangeHorizontal() {
		return smoothingRangeHorizontal;
	}

	public void setSmoothingRangeHorizontal(int smoothingRangeHorizontal) {
		this.smoothingRangeHorizontal = smoothingRangeHorizontal;
	}
	
// ################################################################################
	
	@XmlAttribute(name = "smoothing-range-vertical")
	@Property(
		name = "Maximum Smoothing Range - Vertical",
		description = "The maximum vertical range in one-tenth of a percentage of display area for applying the smoothing.",
		suffix = "\u2030"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 1000,
		showLabels = true,
		showTicks = true,
		majorTicks = 250,
		minorTicks = 100
//		snapToTicks = true
	)
	private int smoothingRangeVertical = 1;

	public int getSmoothingRangeVertical() {
		return smoothingRangeVertical;
	}

	public void setSmoothingRangeVertical(int smoothingRangeVertical) {
		this.smoothingRangeVertical = smoothingRangeVertical;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################

	public static class ModeDomainProvider implements DomainProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(MODE_OFF, "Off");
			values[1] = new ComboBoxItemWrapper(MODE_POINT, "Static Model");
			values[2] = new ComboBoxItemWrapper(MODE_POINT_VELOCITY, "Dynamic Model");
			values[3] = new ComboBoxItemWrapper(MODE_MULTI_WEIGHTED, "Weighted Combination");
			values[4] = new ComboBoxItemWrapper(MODE_MULIT_CHOICE, "Best Choice (XOR)");

			return values;
		}
	}

	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################

	private int numPointsFrame = 0;
	
	/**
	 * Processing data types of type <code>DataPosition2D</code> and lower hierarchy. Each data
	 * object of a container that matches one of these types will be processed by this method.
	 * Forwarding the calculated value is possible in two ways.
	 * <ul>
	 *   <li>Return that data object.</li>
	 *   <li>Publish that data object along with new created once.</li>
	 * </ul>
	 * @see DataPosition2D
	 * @see ReflectionProcessable#publish(java.util.Collection)
	 * @see ReflectionProcessable#publish(org.squidy.manager.data.IData...)
	 * @see ReflectionProcessable#publish(IDataContainer)
	 */
	@Processor.Process(type = DataPosition2D.class)
	public DataPosition2D process(DataPosition2D dataPosition2D) {
		if (mode < 0) return dataPosition2D;

		KalmanModels models = identifyFilter(dataPosition2D);
		
		return processFilter(models, dataPosition2D);
	}

	/**
	 * Processing the data position 2d and validate that position against the Kalman models.
	 */
	private DataPosition2D processFilter(KalmanModels kalmanModels, DataPosition2D dataPosition2D) {
		
		KalmanFilter kalmanStatic = kalmanModels.getModelStatic();
		KalmanFilter kalmanDynamic = kalmanModels.getModelDynamic();

		if (!kalmanModels.isInitialized() || mode==0) {
			// The very first measurement point
			kalmanStatic.state_post.set(0, 0, dataPosition2D.getX());
			kalmanStatic.state_post.set(2, 0, dataPosition2D.getY());
			kalmanStatic.state_post.set(1, 0, 0);
			kalmanStatic.state_post.set(3, 0, 0);
			
			if(mode==0){
				kalmanStatic.state_pre.set(0, 0, dataPosition2D.getX());
				kalmanStatic.state_pre.set(2, 0, dataPosition2D.getY());
			}

			// Predict
			if(mode!=0)kalmanStatic.predict();

			if (mode >= 3) {
				kalmanDynamic.state_post.set(0, 0, dataPosition2D.getX());
				kalmanDynamic.state_post.set(2, 0, dataPosition2D.getY());
				kalmanDynamic.state_post.set(1, 0, 0);
				kalmanDynamic.state_post.set(3, 0, 0);

				// Predict
				kalmanDynamic.predict();
			}

			kalmanModels.setInitialized(true);
		}

		// Correct
		if(mode!=0)kalmanStatic.correct(dataPosition2D.getX(), dataPosition2D.getY());

		if (mode >= 3) {

			double mNoiseScaled = mNoise > 0 ? mNoise / (double) 100000 : 0;
			
			// Compute model likelihoods
			// 1st model:
			double c1_x = kalmanStatic.error_cov_pre.get(0, 0) + mNoiseScaled;
			double c1_y = kalmanStatic.error_cov_pre.get(2, 2) + mNoiseScaled;
			double rez_x = dataPosition2D.getX() - kalmanStatic.state_pre.get(0, 0);
			double rez_y = dataPosition2D.getY() - kalmanStatic.state_pre.get(2, 0);
			double f1_x = Math.exp(-rez_x * rez_x / (c1_x * 2.0)) / Math.sqrt(2 * Math.PI * c1_x);
			double f1_y = Math.exp(-rez_y * rez_y / (c1_y * 2.0)) / Math.sqrt(2 * Math.PI * c1_y);

			// 2d model:
			double c2_x = kalmanDynamic.error_cov_pre.get(0, 0) + mNoiseScaled;
			double c2_y = kalmanDynamic.error_cov_pre.get(2, 2) + mNoiseScaled;
			rez_x = dataPosition2D.getX() - kalmanDynamic.state_pre.get(0, 0);
			rez_y = dataPosition2D.getY() - kalmanDynamic.state_pre.get(2, 0);
			double f2_x = Math.exp(-rez_x * rez_x / (c2_x * 2.0)) / Math.sqrt(2 * Math.PI * c2_x);
			double f2_y = Math.exp(-rez_y * rez_y / (c2_y * 2.0)) / Math.sqrt(2 * Math.PI * c2_y);

			// weights
			double w_x, w_y;
			if ((f1_x + f2_x) < 0.00001) {
				w_x = 0;
			}
			else {
				w_x = f1_x / (f1_x + f2_x);
			}

			if ((f1_y + f2_y) < 0.00001) {
				w_y = 0;
			}
			else {
				w_y = f1_y / (f1_y + f2_y);
			}

			if (mode == 4) {
				w_x = (w_x > 0.5) ? 1 : 0;
				w_y = (w_y > 0.5) ? 1 : 0;
			}

			// Correct
			kalmanDynamic.correct(dataPosition2D.getX(), dataPosition2D.getY());

			// take the average
			dataPosition2D.setX(w_x * kalmanStatic.state_post.get(0, 0) + (1 - w_x) * kalmanDynamic.state_post.get(0, 0));
			dataPosition2D.setY(w_y * kalmanStatic.state_post.get(2, 0) + (1 - w_y) * kalmanDynamic.state_post.get(2, 0));

			kalmanDynamic.state_post.set(0, 0, dataPosition2D.getX());
			kalmanDynamic.state_post.set(2, 0, dataPosition2D.getY());

			kalmanStatic.state_post.set(0, 0, dataPosition2D.getX());
			kalmanStatic.state_post.set(2, 0, dataPosition2D.getY());

			// Predict
			kalmanDynamic.predict();

		}
		else {
			dataPosition2D.setX(kalmanStatic.state_post.get(0, 0));
			dataPosition2D.setY(kalmanStatic.state_post.get(2, 0));

			// Predict
			if(mode!=0)kalmanStatic.predict();
		}
		
		dataPosition2D.setAttribute(DataConstant.SESSION_ID, kalmanModels.getIdentifier());
//		System.out.println(models.getIdentifier());
//		dataPosition2D.setIdentifier(dataPosition2D.getIdentifier());
		
		double tmp_x = dataPosition2D.getX();
		double tmp_y = dataPosition2D.getY();
		
		// smooth subpixel movements
		if(isSmoothingSubpixels()){
			if(Math.abs(dataPosition2D.getX()-kalmanModels.getLast_x())<=getSmoothingRangeHorizontal()/1000) dataPosition2D.setX(kalmanModels.getLast_x());
			if(Math.abs(dataPosition2D.getY()-kalmanModels.getLast_y())<=getSmoothingRangeVertical()/1000) dataPosition2D.setY(kalmanModels.getLast_y());
		}
		
		kalmanModels.setLast_x(tmp_x);
		kalmanModels.setLast_y(tmp_y);
		
//		System.out.println(dataPosition2D.toString());
		return dataPosition2D;
	}
	
	public static final int MODE_OFF = 0;
	public static final int MODE_POINT = 1;
	public static final int MODE_POINT_VELOCITY = 2;
	public static final int MODE_MULTI_WEIGHTED = 3;
	public static final int MODE_MULIT_CHOICE = 4;

	private static int counter = 0;

	private ArrayList<KalmanModels> filterInstances = null;

	private boolean reset = false;
	private boolean started = false;
	private DataPosition2D lastPos2D = null;
	
	// private double c1_x, c1_y, rez_x, rez_y, f1_x, f1_y, c2_x, c2_y, f2_x,
	// f2_y, w_x, w_y;

	private static int getNewID() {
		if (counter < 1000) {
			return counter++;
		}
		else {
			return counter = 0;
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	@Override
	public void onStart() {
		resetFilters();
	}

	private void resetFilters() {
		filterInstances = new ArrayList<KalmanModels>();
	}
	
//	public final IDataContainer process(IDataContainer dataContainer) {
//		System.out.println(getDataQueue().size());
//		return super.process(dataContainer);
//	}
	
	@Override
	public IDataContainer preProcess(
			IDataContainer dataContainer) {
		
		numPointsFrame = dataContainer.getData().length;
		
		return super.preProcess(dataContainer);
	}

	private KalmanModels identifyFilter(DataPosition2D data2d) {
        if (filterInstances.size() == 0) {
            KalmanModels models = new KalmanModels(getNewID(), data2d.getX(), data2d.getY());
            initModels(models);
            filterInstances.add(models);
            return models;
        }
        else {
			long currTime = System.currentTimeMillis();
			KalmanModels nearestModels = null;
			double minDist = Double.MAX_VALUE;
			double currDist;
			Iterator<KalmanModels> iterator = filterInstances.iterator();
			while (iterator.hasNext()) {
				KalmanModels models = iterator.next();
				// remove old models
				//System.out.println(currTime - models.getTimestamp());
				if (currTime - models.getTimestamp() > maximumTimeout) {
					iterator.remove();
					continue;
				}
				currDist = models.getDistance(data2d.getX(), data2d.getY(), mode);
				if (currDist < minDist) {
					minDist = currDist;
					nearestModels = models;
				}
			}		
			double distance = maximumDistanceMultiPoint / (double) 100;
			if(numPointsFrame<=1) distance = maximumDistanceSinglePoint / (double) 100;
			if (minDist < distance) {
				nearestModels.setTimestamp(currTime);
				return nearestModels;
			}
			else {
				KalmanModels models = new KalmanModels(getNewID(), data2d.getX(), data2d.getY());
				initModels(models);
				filterInstances.add(models);
				return models;
			}
        }
    }


	private void initModels(KalmanModels models) {
		double dt;
		if (mode == 1) {
			dt = 0;
		}
		else {
			dt = 1.0 / frameRate;
		}

		KalmanFilter kalmanStatic = models.getModelStatic();
		KalmanFilter kalmanDynamic = models.getModelDynamic();

		Matrix A = new Matrix(4, 4); // transition_matrix
		A.set(0, 0, 1);
		A.set(0, 1, dt);
		A.set(1, 1, 1);
		A.set(2, 2, 1);
		A.set(2, 3, dt);
		A.set(3, 3, 1);
		kalmanStatic.transition_matrix = A;

		double pNoiseScaled = pNoise > 0 ? pNoise / (double) 100000000 : 0;
		
		Matrix Q = new Matrix(4, 4); // process_noise_cov
		if (dt != 0) {
			Q.set(0, 0, dt * dt * dt * pvNoise);
			Q.set(1, 0, dt * dt * pvNoise);
			Q.set(0, 1, dt * dt * pvNoise);
			Q.set(1, 1, dt * pvNoise);
			Q.set(2, 2, dt * dt * dt * pvNoise);
			Q.set(3, 2, dt * dt * pvNoise);
			Q.set(2, 3, dt * dt * pvNoise);
			Q.set(3, 3, dt * pvNoise);
		}
		else {
			Q.set(0, 0, pNoiseScaled);
			Q.set(1, 1, pNoiseScaled);
			Q.set(2, 2, pNoiseScaled);
			Q.set(3, 3, pNoiseScaled);
		}
		kalmanStatic.process_noise_cov = Q;

		double mNoiseScaled = mNoise > 0 ? mNoise / (double) 100000 : 0;
		
		Matrix R = new Matrix(2, 2); // measurement_noise_cov
		R.set(0, 0, mNoiseScaled);
		R.set(1, 1, mNoiseScaled);
		kalmanStatic.measurement_noise_cov = R;

		if (mode >= 3) { // 2d p-model{

			kalmanDynamic.transition_matrix = Matrix.identity(4, 4); // transition_matrix

			Matrix Q2 = new Matrix(4, 4); // process_noise_cov
			Q2.set(0, 0, pNoiseScaled);
			Q2.set(1, 1, pNoiseScaled);
			Q2.set(2, 2, pNoiseScaled);
			Q2.set(3, 3, pNoiseScaled);
			kalmanDynamic.process_noise_cov = Q2;

			Matrix R2 = new Matrix(2, 2); // measurement_noise_cov
			R2.set(0, 0, mNoiseScaled);
			R2.set(1, 1, mNoiseScaled);
			kalmanDynamic.measurement_noise_cov = R2;
		}

	}
}
