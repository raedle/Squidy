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

package org.squidy.performance.pipeline.kalman;

import org.junit.Ignore;

import Jama.Matrix;

@Ignore
public class KalmanFilter {
	public Matrix state_pre, state_post, transition_matrix,error_cov_pre,error_cov_post,process_noise_cov,measurement_matrix,measurement_noise_cov,gain,zk,tmp,tmp2,tmp3;
	
	public KalmanFilter(double start_x, double start_y, double start_vx, double start_vy){
		
		state_pre = new Matrix(4,1); //Xk-1
		state_post = new Matrix(4,1); //X
		state_post.set(0, 0, start_x); 
		state_post.set(1, 0, start_y);
		state_post.set(2, 0, start_vx);
		state_post.set(3, 0, start_vy);
		
		transition_matrix = new Matrix(4,4); //A
		
//		B = new Matrix(4,);
//		u = new Matrix();
		
		error_cov_pre = new Matrix(4,4); //Pk-1
		error_cov_post = Matrix.identity(4, 4); //P
		process_noise_cov = new Matrix(4,4); //Q
		
		measurement_matrix = new Matrix(2,4); //H
		measurement_matrix.set(0, 0, 1);
		measurement_matrix.set(1, 2, 1);
		
		measurement_noise_cov = new Matrix(2,2); //R
		
		gain = new Matrix(4,2); //K
		
		zk = new Matrix(2,1);
		
	}
	
	public void predict(){
		//normally: xk= A*xk_old + B*u
		//no control used:
		//xk= A*xk_old
		state_pre = transition_matrix.times(state_post);
		
		//P = A*P_old*trans(A) + Q
		error_cov_pre = ((transition_matrix.times(error_cov_post)).times(transition_matrix.transpose())).plus(process_noise_cov);

	}
	
	public void correct(double x, double y){
		zk.set(0, 0, x);
		zk.set(1, 0, y);
		
		//Kk=P'k•HT•(H•P'k•HT+R)-1
		tmp = ((measurement_matrix.times(error_cov_pre)).times(measurement_matrix.transpose())).plus(measurement_noise_cov);
		gain = (error_cov_pre.times(measurement_matrix.transpose())).times(tmp.inverse());
		
		//xk=x'k+Kk•(zk-H•x'k)
		tmp2 = zk.minus((measurement_matrix.times(state_pre)));
		state_post = state_pre.plus((gain.times(tmp2)));
		
		//Pk=(I-Kk•H)•P'k = P'k-Kk•H•P'k
		tmp3 = (gain.times(measurement_matrix)).times(error_cov_pre);
		error_cov_post = error_cov_pre.minus(tmp3);
	}
}
