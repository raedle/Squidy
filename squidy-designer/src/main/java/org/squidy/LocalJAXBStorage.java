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

package org.squidy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.designer.Designer;
import org.squidy.designer.dragndrop.FileDrop;
import org.squidy.designer.model.Data;
import org.squidy.designer.model.ModelViewHandler;
import org.squidy.manager.util.PreferenceUtils;


/**
 * <code>LocalJAXBStorage</code>.
 * 
 * <pre>
 * Date: Apr 3, 2009
 * Time: 3:30:15 AM
 * </pre>
 * 
 * 
 * @author Roman Rädle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle
 * @uni-konstanz.de</a> Human-Computer Interaction Group University of Konstanz
 * 
 * @version $Id: LocalJAXBStorage.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class LocalJAXBStorage implements Storage {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(LocalJAXBStorage.class);
	
	// Default name of Squidy workspace file.
	public static final String DEFAULT_WORKSPACE_FILE = "squidy-workspace.sdy";
	
	private boolean storeInProgress = false;
	
	/**
	 * 
	 */
	public LocalJAXBStorage() {
		
		new FileDrop(Designer.getInstance(), new FileDrop.Listener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.dragndrop.FileDrop.Listener#filesDropped
			 * (java.io.File[])
			 */
			public void filesDropped(File[] files) {

				if (files.length > 1) {
					throw new SquidyException("Only one file could be dropped to designer.");
				}

				Designer designer = Designer.getInstance();
				designer.saveAndUnload();
				setWorkspaceFile(files[0]);
				designer.load();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.Storage#getIdentifier()
	 */
	public String getIdentifier() {
		File file = getWorkspaceFile();
		if (file != null && file.exists()) {
			return file.getAbsolutePath();
		}
		
		return "";
	}

	/* (non-Javadoc)
	 * @see org.squidy.Storage#isAutomatedStorageActive()
	 */
	public boolean isAutomatedStorageActive() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.squidy.Storage#restore()
	 */
	public Data restore() {
		
		File file = getWorkspaceFile();
//		if (file == null || !file.exists()) {
//			file = getWorkspaceFileOfOpenDialog();
//		}
		
		if (file != null) {
		
			ModelViewHandler handler = ModelViewHandler.getModelViewHandler();
			try {
				return handler.load(new FileInputStream(file));
			}
			catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Could not restore data of file " + file.getAbsolutePath());
				}
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.squidy.Storage#store(org.squidy.designer.model.Data)
	 */
	public synchronized void store(Data data) {
		
		if (storeInProgress) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Store is in progress. System is may waiting for user input.");
			}
			return;
		}
		
		File file = getWorkspaceFile();
		if (file == null) {
			file = getWorkspaceFileOfSaveDialog();
		}
		
		if (file == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Could not store data. No workspace file has been selected.");
			}
			return;
			
//			String path = PreferenceUtils.get().get(DEFAULT_WORKSPACE_FILE);
//			if (path != null) {
//				file = new File(path);
//			}
		}
		
		ModelViewHandler handler = ModelViewHandler.getModelViewHandler();
		try {
			File hiddenFile = new File(file.getParent(), "." + file.getName());
			OutputStream fileOutputStream = new FileOutputStream(hiddenFile);
			handler.save(fileOutputStream, data);
			
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
			
			File backupFile = new File(file.getParent(), file.getName() + ".bak");
			if (backupFile.exists()) {
				backupFile.delete();
			}
			
			file.renameTo(backupFile);
			hiddenFile.renameTo(file);
			
			if (backupFile.exists()) {
				backupFile.delete();
			}
		}
		catch (FileNotFoundException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * @return
	 */
	public File getWorkspaceFile() {
		return PreferenceUtils.getFile(DEFAULT_WORKSPACE_FILE);
	}

	/**
	 * @param file
	 */
	public void setWorkspaceFile(File file) {
		PreferenceUtils.putFile(DEFAULT_WORKSPACE_FILE, file);
	}
	
	/**
	 * @return
	 */
	private File getWorkspaceFileOfOpenDialog() {
		JFileChooser fileChooser = new JFileChooser();
		
		int option = fileChooser.showOpenDialog(Designer.getInstance());
		
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			setWorkspaceFile(file);
			return file;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	private File getWorkspaceFileOfSaveDialog() {
		storeInProgress = true;
		try {
			JFileChooser fileChooser = new JFileChooser();
			
			int option = fileChooser.showSaveDialog(Designer.getInstance());
			
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				setWorkspaceFile(file);
				return file;
			}
			return null;
		}
		finally {
			storeInProgress = false;
		}
	}
}