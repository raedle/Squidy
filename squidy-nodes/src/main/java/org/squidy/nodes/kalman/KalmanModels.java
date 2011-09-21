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


package org.squidy.nodes.kalman;

/**
 * <code>Kalman</code>.
 *
 * <pre>
 * Date: June 13, 2008
 * Time: 4:16:01 PM
 * </pre>
 *
 * @author Werner Koenig, werner.koenig@uni-konstanz.de, University of Konstanz
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: KalmanModels.java 772 2011-09-16 15:39:44Z raedle $
 */

public class KalmanModels {
	private int identifier = -1;
	private KalmanFilter modelStatic = null;
	private KalmanFilter modelDynamic = null;
	private long timestamp;
	private boolean initialized = false;
	private double last_x, last_y;
	
	public KalmanModels(int identifier, double x, double y){
		this.identifier = identifier;
		this.last_x = x;
		this.last_y = y;
		modelStatic = new KalmanFilter(x, y, 0, 0); 
		modelDynamic = new KalmanFilter(x, y, 0, 0); 
		timestamp = System.currentTimeMillis();
	}

	/**
	 * Compute minimal euclidian disctance between both predictions and current point
	 * @param x
	 * @param y
	 * @return
	 */
	public double getDistance(double x, double y, int mode){
		
		double distX = x - modelStatic.state_pre.get(0, 0);
		double distY = y - modelStatic.state_pre.get(2, 0);
		double distStatic = Math.sqrt(distX*distX+distY*distY);
		
		if(mode<2) return distStatic;
		
		distX = x - modelDynamic.state_pre.get(0, 0);
		distY = y - modelDynamic.state_pre.get(2, 0);
		double distDynamic = Math.sqrt(distX*distX+distY*distY);
		
		if(distStatic>distDynamic || mode==2){
			return distDynamic;
		}else{
			return distStatic;
		}
	}
	
	public int getIdentifier() {
		return identifier;
	}
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	public KalmanFilter getModelStatic() {
		return modelStatic;
	}
	public void setModelStatic(KalmanFilter modelStatic) {
		this.modelStatic = modelStatic;
	}
	
	public double getLast_x() {
		return last_x;
	}

	public void setLast_x(double last_x) {
		this.last_x = last_x;
	}

	public double getLast_y() {
		return last_y;
	}
	
	public void setLast_y(double last_y) {
		this.last_y = last_y;
	}

	public KalmanFilter getModelDynamic() {
		return modelDynamic;
	}
	public void setModelDynamic(KalmanFilter modelDynamic) {
		this.modelDynamic = modelDynamic;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
