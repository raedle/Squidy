package org.squidy.nodes.speechrecognition;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.squidy.nodes.*;


public class SpeechWindow extends JFrame {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 9208561146225274896L;

	static{
//		System.loadLibrary("/ext/speechrecognition/oojnidotnet");
		System.loadLibrary("/ext/speechrecognition/CSharpInJava");
	}
	
	NativeSR canva;
	JButton okButton;
	private String grammarFile;
	private double recoConf;
	private double hypoConf;
	private boolean dicationEnabled;
	private int babbleTimeOut;
	private SpeechRecognition speechNode;
	
	public SpeechWindow(SpeechRecognition sNode,String grammarFile,double recoConf, double hypoConf, boolean dicationEnabled, int babbleTimeout) {
		super("Speech Recognition Control");
		this.grammarFile = grammarFile;
		this.recoConf = recoConf;
		this.hypoConf = hypoConf;
		this.dicationEnabled = dicationEnabled;
		this.babbleTimeOut = babbleTimeout;
		this.speechNode = sNode;
		initialize();
	}
	
	public void initialize(){
		getContentPane().add(canva = new NativeSR(), BorderLayout.CENTER);
		canva.setParents(this.speechNode,this);
		//canva.NLoadGrammar(this.grammarFile);
		//System.out.println(canva.NLoadGrammar(this.grammarFile));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });
		pack();
		setSize(200, 200);
	}
	/*public void sendSRSettings()
	{
		//canva.loadGrammar(this.grammarFile,);
		canva.NLoadGrammar(this.grammarFile);
		canva.setRecoConf(this.recoConf);
		canva.setHypoConf(this.hypoConf);
		canva.setDicationEnabled(this.dicationEnabled);
		canva.setBabbleTimeOut(this.babbleTimeOut);
	}*/
}
