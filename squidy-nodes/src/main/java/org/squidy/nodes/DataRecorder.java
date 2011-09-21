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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.data.impl.DefaultDataContainer;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.recorder.DataPlayer;
import org.squidy.nodes.recorder.FileHelper;
import org.squidy.nodes.recorder.LoggingObjectFactory;
import org.squidy.nodes.recorder.RecorderGUI;
import org.squidy.nodes.recorder.LoggingObjectFactory.LoggingObject;


/**
 * @author Markus Nitsche
 * @author Mario Ganzeboom (MaGaM) - contributed added functionality and bug fixes
 */
@XmlType(name = "DataRecorder")
@Processor(
		name = "Data Recorder",
		icon = "/org/squidy/nodes/image/48x48/recorder.png",
		description = "/org/squidy/nodes/html/DataRecorder.html",
		types = { Processor.Type.FILTER },
		tags = { "recorder", "player", "log", "logging" }
)
public class DataRecorder extends AbstractNode implements ActionListener, WindowStateListener {

	//GUI
	private RecorderGUI controlPanel;
	
	//File handling
	//private RingBuffer<IData[]> buffer = new RingBuffer<IData[]>(100);
	private FileHelper fileHelper = new FileHelper(this);
	private DataPlayer player;
	private File currentLogFile = null;
	
	//Replay Mode
	public static final int MODE_STOP = 0;
	public static final int MODE_PLAY = 1;
	public static final int MODE_RECORD = 2;
	public static final int MODE_PAUSE_PLAY = 3;
	public static final int MODE_PAUSE_RECORD = 4;
	public static final int MODE_SINGLESTEP = 5;
	private int mode = MODE_STOP;
	
	private long recordStartTime = -1;
	private long lastPauseBegan = -1;
	private long totalPauseTime = 0;
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################
	
	@XmlAttribute(name = "replay-mode")
	@Property(
			name = "Replay mode",
			description = "Default mode does ignore pauses in the log file which were created by pressing 'pause'. With pause does include all pauses."
	)
	@ComboBox(domainProvider = ReplayModeDomainProvider.class)
	private int replayMode = 0;

	public int getReplayMode() {
		return replayMode;
	}

	public void setReplayMode(int replayMode) {
		if(player != null) {
			player.setIgnorePause(replayMode == REPLAYMODE_DEFAULT);
		}
		this.replayMode = replayMode;
	}
	
	
	@XmlAttribute(name = "keep-timestamp")
	@Property(name = "Keep original timestamp", description = "If set, the original timestamp from the logfile is used. Otherwise it will be replaced by the current timestamp")
	@CheckBox
	private boolean keepTimestamp = false;

	public boolean isKeepTimestamp() {
		return keepTimestamp;
	}

	public void setKeepTimestamp(boolean keep) {
		this.keepTimestamp = keep;
	}
	
	//#################################################################

	@XmlAttribute(name = "value-separator")
	@Property(
			name = "Logfile values separator",
			description = "The seperator which will be used to seperate the single values of an object in the logfile"
	)
	@TextField
	private String valueSeparator = ",";

	
	public String getValueSeparator() {
		return valueSeparator;
	}

	public void setValueSeparator(String separator) {
		this.valueSeparator = separator;
		LoggingObjectFactory.getInstance().setValueSeparator(separator);
	}
	//#################################################################

	@XmlAttribute(name = "object-separator")
	@Property(
			name = "Logfile obejcts separator",
			description = "The seperator which will be used to seperate the single object of a container in the logfile"
	)
	@TextField
	private String objectSeparator = "::";

	
	public String getObjectSeparator() {
		return objectSeparator;
	}

	public void setObjectSeparator(String objectSeparator) {
		this.objectSeparator = objectSeparator;
		LoggingObjectFactory.getInstance().setObjectSeparator(objectSeparator);
	}
	//#################################################################
	@XmlAttribute(name = "logging-folder")
	@Property(
			name = "Logging folder",
			description = "The path to the log folder."
	)
	@TextField
	private String loggingFolder = "log";

	
	public String getLoggingFolder() {
		return loggingFolder;
	}

	public void setLoggingFolder(String loggingFolder) {

		if (!this.loggingFolder.equals(loggingFolder)) {
			//logStream = createLogOutput();
			//fileCounter = 0;
		}

		this.loggingFolder = loggingFolder;
	}
	
	//#################################################################
	
	@XmlAttribute(name = "logfilename")
	@Property(
			name = "Name of Logfile",
			description = "The name of the logfile(s) which will be created by the recorder."
	)
	@TextField
	private String filename = "SquidyLog";

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	//#################################################################
	
	@XmlAttribute(name = "logfileextension")
	@Property(
			name = "Logfile Extension",
			description = "File extension of Squidy Logfiles."
	)
	@TextField
	
	private String logFileExtension = ".sdl";

	public String getLogFileExtension() {
		return logFileExtension;
	}

	public void setLogFileExtension(String logFileExtension) {
		this.logFileExtension = logFileExtension;
	}

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	

	@Override
	public void onStart() {
		super.onStart();
		if(controlPanel == null) {
			controlPanel = new RecorderGUI("Data Recorder", this);
		}
		if(fileHelper == null) {
			fileHelper = new FileHelper(this);
		}
		controlPanel.setVisible(true);
		LoggingObjectFactory.getInstance().setObjectSeparator(objectSeparator);
		LoggingObjectFactory.getInstance().setValueSeparator(valueSeparator);
	}

	@Override
	public void onStop() {
		super.onStop();
		fileHelper.terminate();
		controlPanel.setVisible(false);
		controlPanel = null;
		fileHelper = null;
		currentLogFile = null;
		player = null;
	}

	public IDataContainer preProcess(IDataContainer dataContainer) {
		
		if(mode != MODE_RECORD) {
			return super.preProcess(dataContainer);
		}
		LoggingObject o = LoggingObjectFactory.getInstance().getLoggingObject(System.currentTimeMillis(), dataContainer, LoggingObject.TYPE_DATA);
		fileHelper.write(o.serialize());
		
		return super.preProcess(dataContainer);
	}
		
	public void publishData(IData i) {
		publish(i);
	}
	
	public void publishData(List<IData> datas) {
		publish(datas);
	}
	
	private void setMode(int newMode, long time) {
		if (newMode == MODE_PLAY) {
			if(mode == MODE_RECORD || mode == MODE_PLAY) {
				return;
			}
			if(mode == MODE_PAUSE_PLAY) {
				player.proceed();
				mode = MODE_PLAY;
				controlPanel.setStatusText("Playing");
				return;
			}
			if(mode == MODE_SINGLESTEP) {
				player.proceed();
				mode = MODE_PLAY;
				controlPanel.setStatusText("Playing");
				return;
			}
			if(currentLogFile == null) {
				if(!selectInputFile()) {
					return;
				}
			}
			if(player != null) {
				player.stopPlayer();
				player = null;
			}
			player = fileHelper.getPlayer(replayMode == REPLAYMODE_DEFAULT);
			player.start();
			mode = MODE_PLAY;
			controlPanel.setStatusText("Playing");
		}
		else if (newMode == MODE_PAUSE_PLAY) {
			player.pausePlayer();
			mode = MODE_PAUSE_PLAY;
			controlPanel.setStatusText("Paused playback");
		}
		else if(newMode == MODE_RECORD) {
			if(mode == MODE_PAUSE_RECORD) {
				writePause(time, false);
			}
			else {
				fileHelper.startRecord(loggingFolder, filename, logFileExtension);
				File curLogFile = fileHelper.getCurrentLogFile();
				String fileName = controlPanel.getMultiLineLabelForLogFile(curLogFile);
				fileName = fileName.replace(".tmp", getLogFileExtension());
				controlPanel.setFileLabelText(fileName, true);
				recordStartTime = time;
				//write a null logging object, such that the duration of the file is correct
				LoggingObject o = LoggingObjectFactory.getInstance().getLoggingObject(time, null, LoggingObject.TYPE_NULL);
				fileHelper.write(o.serialize());
			}
			mode = MODE_RECORD;
			controlPanel.setStatusText("Recording");
		}
		else if (newMode == MODE_PAUSE_RECORD) {
			writePause(time, true);
			mode = MODE_PAUSE_RECORD;
			controlPanel.setStatusText("Paused recording");
		}
		else if(newMode == MODE_STOP) {
			if(mode == MODE_RECORD) {
				//write a Null Logging Object at end of file
				LoggingObject o = LoggingObjectFactory.getInstance().getLoggingObject(time, null, LoggingObject.TYPE_NULL);
				fileHelper.write(o.serialize());
				
				//write Logfile header
				long recordTime = time - recordStartTime;
				fileHelper.writeHeader(recordTime, totalPauseTime);
				controlPanel.setStatusText("Stopped recording");
				//TODO: open recorded file directly
			}
			if(mode == MODE_PLAY) {
				player.stopPlayer();
				player = null;
				controlPanel.setStatusText("Stopped playback");
				controlPanel.setTimeLabelText("00:00:00,000");
				controlPanel.setSliderPosition(0);
			}
			mode = MODE_STOP;
		}
		else if (newMode == MODE_SINGLESTEP && (mode == MODE_PLAY || mode == MODE_PAUSE_PLAY || mode == MODE_SINGLESTEP)){
			player.stepForward();
			mode = MODE_SINGLESTEP;
		}
	}
	
	private void writePause(long time, boolean pauseBegins) {
		if(pauseBegins)
			lastPauseBegan = time;
		
		if(!pauseBegins && lastPauseBegan != -1) {
			totalPauseTime += time - lastPauseBegan;
			lastPauseBegan = -1;
		}
		
		LoggingObject l = LoggingObjectFactory.getInstance().getLoggingObject(time, null, LoggingObject.TYPE_PAUSE);
		fileHelper.write(l.serialize());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand().toLowerCase();
		if(action.equals("rec")){
			setMode(MODE_RECORD, e.getWhen());
		} else if  (action.equals("stop")) {
			setMode(MODE_STOP, e.getWhen());
		} else if  (action.equals("play")) {	
			setMode(MODE_PLAY, e.getWhen());
		} else if  (action.equals("pause")) {
			if(mode == MODE_PLAY) {
				setMode(MODE_PAUSE_PLAY, e.getWhen());
			} else if (mode == MODE_RECORD) {
				setMode(MODE_PAUSE_RECORD, e.getWhen());
			} else if (mode == MODE_PAUSE_PLAY) {
				setMode(MODE_PLAY, e.getWhen());
			} else if (mode == MODE_PAUSE_RECORD) {
				setMode(MODE_RECORD, e.getWhen());
			}
		} else if  (action.equals("open")) {
			selectInputFile();
		} else if  (action.equals("step")) {
			setMode(MODE_SINGLESTEP, e.getWhen());
		}
	}
	
	private boolean selectInputFile() {
		File logfile = controlPanel.openFile(getLoggingFolder());
		if(logfile != null) {
			currentLogFile = logfile;
			return true;
		}
		return false;
	}
	
	// ################################################################################
	// BEGIN OF DOMAIN PROVIDERS
	// ################################################################################
	
	public static final int REPLAYMODE_DEFAULT = 0;
	public static final int REPLAYMODE_WITHPAUSE= 1;
	
	public static class ReplayModeDomainProvider implements DomainProvider {
	
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.squidy.manager.data.domainprovider.DomainProvider#getValues()
		 */
		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[2];
			values[0] = new ComboBoxItemWrapper(REPLAYMODE_DEFAULT, "Default");
			values[1] = new ComboBoxItemWrapper(REPLAYMODE_WITHPAUSE, "With pauses");
	
			return values;
		}
	}

	
	// ################################################################################
	// END OF DOMAIN PROVIDERS
	// ################################################################################
	
	
	// ###############################################################################
	// BEGIN OF WINDOW STATE LISTENER METHODS
	// ###############################################################################
	
	public void windowStateChanged(WindowEvent e) {
		//TODO
		if(e.getID() == WindowEvent.WINDOW_CLOSING) {
			
		}
		else if (e.getID() == WindowEvent.WINDOW_CLOSED) {
			
		}
	}

	public File getCurrentLogFile() {
		return currentLogFile;
	}

	public void setCurrentLogFile(File currentLogFile) {
		this.currentLogFile = currentLogFile;
	}

	public void playerHasfinished() {
		fileHelper.terminate();
		setMode(MODE_STOP, System.currentTimeMillis());
	}

	public void doPublish(IDataContainer dataContainer) {
		if(!isKeepTimestamp()){
			dataContainer.setTimestamp(System.currentTimeMillis());
		}
		publish(dataContainer);
	}

	public void updateGUI(float percentagePlayed, long timeStamp) {
		controlPanel.setSliderPosition((int) (percentagePlayed*1000.0f));
		controlPanel.setTimeLabelText(new SimpleDateFormat("HH:mm:ss,SSS").format(new Date(timeStamp)));
	}
	
	// ###############################################################################
	// END OF WINDOW STATE LISTENER METHODS
	// ###############################################################################
}
