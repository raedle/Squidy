package org.squidy.nodes.reactivision.remote;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.squidy.nodes.ReacTIVision;
import org.squidy.nodes.reactivision.remote.control.ControlServer;
import org.squidy.nodes.reactivision.remote.image.ImageServer;



public class CalibrationWindow extends JFrame {
	
	private static final long serialVersionUID = -5039303428320924913L;
	JScrollPane scrollPane;
	private CalibrationArea calibrationArea;
	private ReacTIVision reacTIVisionInstance;
	
	public CalibrationWindow(final ReacTIVision callingNode, final ControlServer controlServer, final ImageServer imageServer) {
		
		reacTIVisionInstance = callingNode;
		calibrationArea = new CalibrationArea(controlServer, imageServer);
		JScrollPane scrollPane = new JScrollPane( calibrationArea ); 
		
		
		//set start size
		Insets insets = getInsets();
		Dimension windowDim = calibrationArea.getPreferredSize();
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		
		if (windowDim.height > screenDim.height - 50)
			windowDim.height = screenDim.height - 50;
		if (windowDim.width > screenDim.width)
			windowDim.width = screenDim.width;
		setSize(windowDim.width + insets.left + insets.right,
				windowDim.height + insets.top + insets.bottom);
		
		add( scrollPane );
		
		final WindowAdapter exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controlServer.stopCameraFeed();
				reacTIVisionInstance.refreshGridCalibration(ReacTIVision.MODE_OFF);
			}
		};
		addWindowListener(exitListener);
	}	
}
