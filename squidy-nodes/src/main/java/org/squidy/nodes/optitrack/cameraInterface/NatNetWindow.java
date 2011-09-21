package org.squidy.nodes.optitrack.cameraInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

import org.squidy.nodes.*;
import org.squidy.nodes.optitrack.TTStreaming;


public class NatNetWindow extends JFrame {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 9208561146225274896L;


	static{
		System.loadLibrary("/ext/optitrack/oojnidotnet");
		System.loadLibrary("/ext/optitrack/NatNetStreaming");
	}
	
	NativeNatNet canva;

	private TTStreaming ttStreaming;
	private Point3d dimensions;
	public NatNetWindow(TTStreaming ttStreaming, Point3d dimensions) {
		super("NatNetStreaming");
		this.ttStreaming = ttStreaming;
		this.dimensions = dimensions;
		initialize();
	}
	public void stop()
	{
		canva.stop();
		this.dispose();
	}	
	public void initialize(){
		getContentPane().add(canva = new NativeNatNet(), BorderLayout.CENTER);
		canva.setParents(this.ttStreaming,this, dimensions);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
		pack();
		setSize(200, 200);
	}
	
}
