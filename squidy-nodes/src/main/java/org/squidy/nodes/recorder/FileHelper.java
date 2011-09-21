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

package org.squidy.nodes.recorder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.squidy.nodes.DataRecorder;
import org.squidy.nodes.recorder.LoggingObjectFactory.LoggingObject;


public class FileHelper {
	
	private DataRecorder recorder;
	private File currentLogFolder;
	private File currentLogFile;
	private OutputStream logOutStream;
	private BufferedReader logInStream;
	private RingBuffer<LoggingObject> buffer;
	private BufferLoader loader;
	private int bufferSize = 1000;
	private String tempFileExtension = ".tmp";
	
	public FileHelper(DataRecorder p) {
		recorder = p;
	}
	
	public void terminate() {
		if(loader != null) {
			loader.terminate();
		}
		buffer = null;
		closeLogInput();
		closeLogOutput();
	}
	
	// ##################################
	// Input methods for reading logfiles
	// ##################################
	
	public BufferedReader getInputReader() {
		if(logInStream == null) {
			logInStream = createLogInput(recorder.getCurrentLogFile());
		}
		return logInStream;
	}
	
	private BufferedReader createLogInput(File file) {
		if(file == null) {
			return null;
		}
		try {
			if(logInStream != null) {
				logInStream.close(); 
			}
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			return new BufferedReader(new InputStreamReader(in));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void closeLogInput() {
		if (logInStream != null) {
			try {
				logInStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} /* TODO finally {
				logInStream = createLogInput();
				//positionSlider.setValue(0);
				//timeLabel.setText("00:00:00 / " + currentFileDurationString);
				//TODO: Position is 0 again, set all counters back
			}*/

		}
	}
	
	public DataPlayer getPlayer(boolean ignorePause) {
		if((logInStream = createLogInput(recorder.getCurrentLogFile())) == null) {
			return null;
		}
		if(buffer != null) {
			//TODO buffer.empty();
			buffer = null;
		}
		buffer = new RingBuffer<LoggingObject>(bufferSize);
		if(loader != null) {
			loader.terminate();
			loader = null;
		}
		loader = new BufferLoader(buffer, logInStream);
		loader.start();
		return new DataPlayer(recorder, buffer, ignorePause);
	}
	
	// ###################################
	// Output methods for writing logfiles
	// ###################################
	
	public void startRecord(String folder, String filename, String extension) {
		if(logOutStream != null) {
			closeLogOutput();
		}
		logOutStream = createLogOutput(folder, filename, extension);
	}
	
	private OutputStream createLogOutput(String loggingFolder, String filename, String logFileExtension) {
		if (currentLogFolder == null) {
			java.util.Date theDate = new java.util.Date();
			java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
			String subfolder = dateFormatter.format(theDate);
			
			currentLogFolder = new File(loggingFolder, subfolder);
			currentLogFolder.mkdirs();
		}
		java.util.Date theDate = new java.util.Date();
		java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("HH-mm-ss");
		String dateExtension = dateFormatter.format(theDate);
		currentLogFile = new File(currentLogFolder, filename + "_" + dateExtension + tempFileExtension);
		
		try {
			return new BufferedOutputStream(new FileOutputStream(currentLogFile));
		} catch (FileNotFoundException e) {
			recorder.publishFailure(e);
		}
		return null;
	}

	private void closeLogOutput(){
		if (logOutStream != null) {
			try {
				logOutStream.flush();
				logOutStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logOutStream = null;
		}
	}

	public void write(byte[] byteArray) {
		if(logOutStream != null) {
			try {
				logOutStream.write(byteArray);
			} catch (IOException e) {
				recorder.publishFailure(new Throwable("DataRecorder: Could not write to OutputStream for Logfile", e));
			}
		}
		else {
			recorder.publishFailure(new Exception("No outputstream available in DataRecorder"));
		}
	}

	public void writeHeader(long recordTime, long totalPauseTime) {
		//This method is called when recording has stopped
		//It will prepend a "file header" containing information
		//about the log runtime and other stuff to the logfile
		closeLogOutput();
		BufferedReader in = createLogInput(currentLogFile);
		String name = currentLogFile.getName().replace(tempFileExtension, recorder.getLogFileExtension());
		File outFile = new File(currentLogFolder, name);
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(outFile));
		} catch (FileNotFoundException e) {
			recorder.publishFailure(new Throwable("DataRecorder: Could not create OutputStream for Logfile", e));
		}
		String header = "HEADER," + recordTime + "," + totalPauseTime + System.getProperty("line.separator");
		try {
			out.write(header.getBytes());
			String line;
			while((line = in.readLine()) != null) {
				out.write(line.getBytes());
				out.write(System.getProperty("line.separator").getBytes());	
			}
		} catch (IOException e) {
			recorder.publishFailure(new Throwable("DataRecorder: Could not write to OutputStream for Logfile", e));
		}
		try {
			in.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//copy header and logfile into new file, delete temp file
		
		//TODO replace tempFile with new file
		recorder.setCurrentLogFile(outFile);
		if(!currentLogFile.delete()) {
			recorder.publishFailure(new Throwable("Could not delete DataRecorder temp file"));
		}
	}

	/**
	 * Returns the file which is currently logged to.
	 * @return currentLogFile - the file currently logged to.
	 */
	public File getCurrentLogFile() {
		return currentLogFile;
	}
}
