/**
 * 
 */
package org.squidy.performance.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Ignore;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>GaugeNode</code>.
 *
 * <pre>
 * Date: Jul 22, 2010
 * Time: 2:32:47 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id$
 * @since 1.5.0
 */
@Ignore
public class GaugeNode extends AbstractNode {

	private GaugingCallback gaugingCallback;
	
	/**
	 * @param gaugingCallback
	 */
	public void setGaugingCallback(GaugingCallback gaugingCallback) {
		this.gaugingCallback = gaugingCallback;
	}

	private Timer timer;
	private int frameCount;
	
	private List<Integer> fpsList = new ArrayList<Integer>();
	
	private synchronized void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}
	
	/**
	 * @return
	 */
	public int getMeasuringPoints() {
		return fpsList.size();
	}
	
	/**
	 * @return
	 */
	public int getMeanAverageFPS() {
		int totalFPS = 0;
		for (int fps : fpsList) {
			totalFPS += fps;
		}
		return totalFPS / fpsList.size();
	}
	
	/**
	 * @return
	 */
	public int getMedianFPS() {
		List<Integer> tmpFPSList = new ArrayList<Integer>(fpsList);
		Collections.sort(tmpFPSList);
		
		int medianFPS = 0;
		int midPoint = tmpFPSList.size() / 2;
		if (tmpFPSList.size() % 2 == 0) {
			int v1 = tmpFPSList.get(midPoint);
			int v2 = tmpFPSList.get(midPoint - 1);
			medianFPS = (v1 + v2) / 2;
		}
		else {
			medianFPS = tmpFPSList.get(midPoint);
		}
		return medianFPS;
	}
	
	/**
	 * @return
	 */
	public Integer[] getValues() {
		return fpsList.toArray(new Integer[0]);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		
		frameCount = 0;
		
		// Clear previous results.
		fpsList.clear();
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			/* (non-Javadoc)
			 * @see java.util.TimerTask#run()
			 */
			@Override
			public void run() {
				fpsList.add(frameCount);
				setFrameCount(0);
				
				if (gaugingCallback != null && gaugingCallback.checkForAchievement(fpsList.size())) {
					gaugingCallback.cyclesAchieved();
				}
			}
		}, 1000, 1000);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		
		timer.cancel();
		timer = null;
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.model.AbstractNode#checkProcessingPrivileges(org.squidy.manager.data.IDataContainer)
	 */
	@Override
	public boolean checkProcessingPrivileges(IDataContainer dataContainer) {
		return isProcessing();
	}
	
	/**
	 * @return
	 */
	public IData process(DataPosition2D dataPosition2D) {
		setFrameCount(++frameCount);
		return dataPosition2D;
	}
	
	public static interface GaugingCallback {
		public void cyclesAchieved();
		public boolean checkForAchievement(int cylces);
	}
}
