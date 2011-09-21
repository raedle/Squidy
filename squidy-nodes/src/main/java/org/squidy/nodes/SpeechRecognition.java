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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.speechrecognition.*;



/**
 * <code>Speechrecognition</code>.
 * 
 * <pre>
 * Date: April 12, 2010
 * Time: 7:33:04 AM
 * </pre>
 * 
 * @author Simon Faeh, simon.faeh@uni-konstanz.de, University of Konstanz

 */
@XmlType(name = "Speechrecognition")
@Processor(
		name = "Speechrecognition",
		types = { Processor.Type.OUTPUT },
		icon = "/org/squidy/nodes/image/48x48/speech.png",
		tags = {"Speech", "recognition","textinput"}
)
public class SpeechRecognition extends AbstractNode {

	static{
//		System.loadLibrary("/ext/speechrecognition/oojnidotnet");
		System.loadLibrary("/ext/speechrecognition/CSharpInJava");
	}
	private boolean isRunning = false;

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "grammar-file")
	@Property(
		name = "Grammar-File",
		description = "Path to the Grammar-File"
	)
	@TextField
	private String grammarFile = "D:\\Development\\Nipper\\Nipper-Server\\Grammar\\cmnrules.grxml";

	/**
	 * @return the grammarFile
	 */
	public String getGrammarFile() {
		return grammarFile;
	}

	/**
	 * @param Grammarfile
	 *            the Grammarfile to set
	 */
	public void setGrammarFile(String file) {
		if(isRunning)
			stop();
		this.grammarFile = file;
		//if(isRunning)
		//	start();
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "reco-confidence")
	@Property(
		name = "Recognitionconfidence",
		description = "Level for the Recognitionconfidence"
	)
	@TextField
	private double recoConf = 0.3;

	/**
	 * @return the Recognitionconfidence
	 */
	public final double getRecoConf() {
		return recoConf;
	}

	/**
	 * @param Recognitionconfidence
	 *            the Recognitionconfidence to set
	 */
	public final void setRecoConf(double reco) {
		if(isRunning)
			stop();		
		this.recoConf = reco;
	}
	
	// ################################################################################
	
	@XmlAttribute(name = "hypo-confidence")
	@Property(
		name = "Hypothesis-Confidence",
		description = "Level of the Hypothesis-Confidence"
	)
	@TextField
	private double hypoConf = 0.3;

	/**
	 * @return the Hypothesis-Confidence
	 */
	public final double getHypoConf() {
		return hypoConf;
	}
	
	/**
	 * @param Hypothesis-Confidence
	 *            the Hypothesis-Confidence to set
	 */
	public final void setHypoConf(double hypo) {
		if(isRunning)
			stop();		
		this.hypoConf = hypo;
	}
	
    // ################################################################################
	
	@XmlAttribute(name = "babbleTimeout")
	@Property(
		name = "Babble Timeout",
		description = "Timeout for Speechoverflow"
	)
	@TextField
	private int babbleTimeout = 12;

	/**
	 * @return the Babble Timeout
	 */
	public final int getBabbleTimeout() {
		return babbleTimeout;
	}

	/**
	 * @param Babble Timeout
	 *            the Babble Timeout to set
	 */
	public final void setBabbleTimeout(int babble) {
		if(isRunning)
			stop();		
		this.babbleTimeout = babble;
	}

    // ################################################################################
	
	@XmlAttribute(name = "dictation-enabled")
	@Property(name = "Enable Dictation", description = "Enable Dication for Textinput")
	@CheckBox
	private boolean dictationEnabled = false;

	public boolean getDictationEnabled() {
		return dictationEnabled;
	}

	public void setDictationEnabled(boolean dict) {
		this.dictationEnabled = dict;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStart()
	 */
	
	//private SpeechWindow speechWindow;
	private SpeechWindow speechRecognitionFrame;
	public final void onStart() {
		speechRecognitionFrame = new SpeechWindow(this,this.grammarFile, this.recoConf, this.hypoConf, this.dictationEnabled, this.babbleTimeout);
		speechRecognitionFrame.setVisible(true);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.ReflectionProcessable#onStop()
	 */
	public final void onStop() {
       speechRecognitionFrame.setVisible(false);
	}
	
	public void getData(String data)
	{
		System.out.println("CSharp Frame Println:  "+data);
	}
}
