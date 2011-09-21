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

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.squidy.nodes.DataRecorder;


@SuppressWarnings("serial")
public class RecorderGUI extends JFrame {

	private static final int LOG_LINE_LENGTH = 50;
	
	private DataRecorder recorder;
	
	private JLabel statusLabel;
	private JSlider positionSlider;
	private JLabel timeLabel;
	private JButton b_play, b_stop, b_pause, b_rec, b_fwd, b_open;
	private JLabel currentFileLabel;
	private final Dimension buttonSize = new Dimension(60,85);
	private final JFileChooser fileChooser = new JFileChooser();
	
	public RecorderGUI(String title, DataRecorder r) {
		super(title);
		recorder = r;
		
		try {
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.createControlPanel();
		this.addWindowStateListener(recorder);
	}
	
	private void createControlPanel(){
		GridBagConstraints gbc;

		//this.addWindowListener(parent);
		//controlPanel.setSize(400, 200);
		Container pane = this.getContentPane(); 
//		BackgroundPanel pane = new BackgroundPanel();
//		pane.setBackgroundImage(this.getClass().getResource("/images/plastic.png"));
//		pane.setBackground(new Color(100,100,100));
//		pane.setForeground(Color.LIGHT_GRAY);
		pane.setLayout(new GridBagLayout());
		//mainPane.add(pane);

		gbc = createGridBagConstraints(0, 0, 6, 1, false);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		statusLabel = new JLabel("Stopped");
		statusLabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		pane.add(statusLabel, gbc);

		gbc = createGridBagConstraints(0, 1 ,6, 1, false);
		positionSlider = new JSlider(0,1000,0);
		positionSlider.setMinorTickSpacing(1);
		positionSlider.setMajorTickSpacing(10);
		positionSlider.setEnabled(false);
		pane.add(positionSlider, gbc);
		
		gbc = createGridBagConstraints(0,2,6,1, false);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		timeLabel = new JLabel("00:00:00,000", JLabel.CENTER);
		pane.add(timeLabel, gbc);
		
		gbc = createGridBagConstraints(0, 3, 6, 1, false);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		currentFileLabel = new JLabel("Logfile: none loaded.");
		currentFileLabel.setUI(new MultiLineLabelUI());
		currentFileLabel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		currentFileLabel.setHorizontalAlignment(JLabel.CENTER);
		pane.add(currentFileLabel, gbc);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		
		gbc = createGridBagConstraints(0, 4, 1, 1, true);
		b_rec = createButton("REC", "/images/record.png");
		pane.add(b_rec, gbc);

		gbc = createGridBagConstraints(1,4,1,1, true);
		b_play = createButton("Play", "/images/media_play.png");
		pane.add(b_play, gbc);

		gbc = createGridBagConstraints(2, 4, 1, 1, true);
		b_fwd = createButton("Step", "/images/media_step_forward.png");
		pane.add(b_fwd, gbc);

		gbc = createGridBagConstraints(3, 4, 1, 1, true);
		b_stop = createButton("Stop", "/images/media_stop.png");
		pane.add(b_stop, gbc);

		gbc = createGridBagConstraints(4, 4, 1, 1, true);
		b_pause = createButton("Pause", "/images/media_pause.png");
		pane.add(b_pause, gbc);

		gbc = createGridBagConstraints(5, 4, 1, 1, true);
		b_open = createButton("Open", "/images/media_eject.png");
		pane.add(b_open, gbc);
		
		this.pack();
		this.setSize(this.getWidth(), this.getHeight()+80);

		if(fileChooser.getChoosableFileFilters().length == 1) {
			fileChooser.addChoosableFileFilter(new LogFileFilter());
		}
	}
	
	private JButton createButton(String label, String resourcePath){
		JButton b;
		Image img;
		ImageIcon icon;
		URL url =  this.getClass().getResource(resourcePath);
		b = new JButton(label);
		b.setVerticalTextPosition(SwingConstants.BOTTOM);
		b.setHorizontalTextPosition(SwingConstants.CENTER);
		b.setMargin(new Insets(0,0,0,0));
		if(url != null) {
			img = Toolkit.getDefaultToolkit().getImage(url);
			icon = new ImageIcon(img);
			b.setIcon(icon);
		}
		b.setFont(b.getFont().deriveFont(10.0f));
		b.setMinimumSize(buttonSize);
		b.setPreferredSize(buttonSize);
		b.setSize(buttonSize);
		b.setActionCommand(label.toLowerCase());
		b.setFocusPainted(false);
		b.setBackground(new Color(200,200,200));
		b.addActionListener(recorder);

		return b;
	}

	private GridBagConstraints createGridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, boolean isButton){
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridheight = gridheight;
		gbc.gridwidth = gridwidth;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		if(isButton) {
			gbc.anchor = GridBagConstraints.SOUTH;
			gbc.weighty = 100.0;
		}
		return gbc;
	}
	
	public void setStatusText(String text) {
		statusLabel.setText(text);
	}
	
	public void setFileLabelText(String filename, boolean isNew) {
		currentFileLabel.setText("Logfile: " + filename + (isNew?" (new)":""));
	}
	
	public void setTimeLabelText(String text) {
		timeLabel.setText(text);
	}
	
	public void setSliderPosition(int pos) {
		positionSlider.setValue(pos);
	}
	
	public File openFile(String dirToOpenIn) {
		fileChooser.setCurrentDirectory(new File(dirToOpenIn));
		int retVal = fileChooser.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			setFileLabelText(getMultiLineLabelForLogFile(file), false);
			//b_play.setEnabled(true);
			return file;
		}
		return null;
	}
	
	public String getMultiLineLabelForLogFile(File logFile) {
		String absPath = logFile.getAbsolutePath();
		String pathToLogFile = "";
		while(absPath.length() > LOG_LINE_LENGTH) {
			int lastSeparatorPos = absPath.substring(0, LOG_LINE_LENGTH).lastIndexOf(File.separator) + 1;
			pathToLogFile += absPath.substring(0, lastSeparatorPos);
			pathToLogFile += "\n";
			absPath = absPath.substring(lastSeparatorPos, absPath.length());
		}
		pathToLogFile += absPath;
		return pathToLogFile;
	}
	
	public class LogFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if(f.isDirectory()) {
				return true;
			}
			String extension;
			int dotPos = f.getName().lastIndexOf(".");
			if(dotPos == -1){
				return false;
			}
			extension = f.getName().substring(dotPos);
			if(extension.equalsIgnoreCase(recorder.getLogFileExtension())) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return "Squidy Log Files (" + recorder.getLogFileExtension() + ")";
		}

	}
	
	private class BackgroundPanel extends JPanel
    {
        private URL backgroundImage;
        private String backgroundRepeat = "repeat";
 
        public void setBackgroundImage(URL backgroundImage)
        {
             this.backgroundImage = backgroundImage;
        }
 
        public void setBackgroundRepeat(String backgroundRepeat)
        {
            this.backgroundRepeat = backgroundRepeat;
        }
 
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
 
            if (backgroundImage != null)
            {
                Graphics2D g2 = (Graphics2D)g.create();
                Insets inset = this.getInsets();
                ImageIcon image = new ImageIcon(backgroundImage);
                Rectangle clippingRegion = new Rectangle(this.getWidth() - (inset.left + inset.right), this.getHeight() - (inset.top + inset.bottom));
 
                g2.setClip(inset.left, inset.top, (int)clippingRegion.getWidth(), (int)clippingRegion.getHeight());
 
                int xRepeat = 0;
                int yRepeat = 0;
 
                if (backgroundRepeat == "repeat" || backgroundRepeat == "repeat-y")
                    yRepeat = (int)Math.ceil(clippingRegion.getHeight() / image.getIconHeight());
 
                if (backgroundRepeat == "repeat" || backgroundRepeat == "repeat-x")
                    xRepeat = (int)Math.ceil(clippingRegion.getWidth() / image.getIconWidth());
 
                for (int i = 0; i <= yRepeat; i++)
                {
                    for (int j = 0; j <= xRepeat; j++)
                    {
                        image.paintIcon(this, g2, j * image.getIconWidth() + inset.left, i * image.getIconHeight() + inset.top);
                    }
                }
                
                g2.dispose();
            }
        }
    }
}
