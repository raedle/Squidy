package org.squidy.nodes.speechrecognition;

import java.awt.Canvas;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.squidy.nodes.*;


public class NativeSR extends Canvas {
	/**
	 * 
	 */
	//private static final long serialVersionUID = 2586762717367712276L;
	
	protected void finalize() throws Throwable {
		super.finalize();
	}

	public void addNotify() {
		super.addNotify();
		ref = create();
		System.out.println("r1 "+ref);
	}

	public void removeNotify() {
		dispose(ref);
		super.removeNotify();
	}
	
	public int ref = 0;
	private static SpeechRecognition speechNode;
	private static SpeechWindow sWindow;
	
	private static String JGrammarFile;
	private static double JRecoConf;
	private static double JHypoConf;
	private static int JBabbleTimeout;
	private static boolean JDictationEnable;
	
	
	native int create();
	native void dispose(int ref);
	
	native String loadGrammar(String s, int peer);
	native String setRecoConf(double d);
	native String setHypoConf(double d);
	native String setDicationEnabled(boolean b);
	native String setBabbleTimeOut(int i);

	
	
	public void setParents(SpeechRecognition sNode,SpeechWindow speechWindow)
	{
		speechNode = sNode;
		sWindow = speechWindow;
		JGrammarFile = sNode.getGrammarFile();
		JRecoConf = sNode.getRecoConf();
		JHypoConf = sNode.getHypoConf();
		JBabbleTimeout = sNode.getBabbleTimeout();
		JDictationEnable = sNode.getDictationEnabled();
		
	}
	
	public static void JPrintLine(String in)
	{
		//speechNode.getData(in);
		System.out.println("SR " + in);
	}
	public static void JSpeechRecognized(String in)
	{
		System.out.println("JSpeechRecognized "+in);
	}
	public static void JSpeechHypotesized(String in)
	{
		System.out.println("JSpeechHypotesized "+in);
	}	
	public static void JSpeechRecognitionRejected(String in)
	{
//		System.out.println("JSpeechRecognitionRejected "+in);
	}
	public static void JAudioStateChanged(String in)
	{
//		System.out.println("JAudioStateChanged "+in);
	}
	public static void JRecognitionComplete(String in)
	{
//		System.out.println("JRecognitionComplete "+in);
	}	
	public static void JSpeechDeteced(String in)
	{
//		System.out.println("JSpeechDeteced "+in);
	}	
	public static void JRecognizeComplete(String in)
	{
//		System.out.println("JRecognizeComplete "+in);
	}	
	public static void JAudioSignalProblem(String in)
	{
		//System.out.println("JAudioSignalProblem "+in);
	}	
	public static void JGrammarLoaded(String in)
	{
		//System.out.println("JGrammarLoaded "+in);
	}	
	public static void JIsListening(String in)
	{
		//System.out.println("JIsListening "+in);
	}	
	
}