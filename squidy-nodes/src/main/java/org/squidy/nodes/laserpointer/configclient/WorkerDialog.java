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

package org.squidy.nodes.laserpointer.configclient;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jdesktop.swingworker.SwingWorker;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

@SuppressWarnings("serial")
public class WorkerDialog extends JDialog implements ActionListener {
	private SwingWorker worker;
	private Component parent;
	private Action cancelAction;
	
	// data
	private String title;
	private String message = "";
	
	private Timer timer;
	private int count = 0;
	
	private JLabel messageLabel;
	
	public WorkerDialog(String title, Dialog parent, SwingWorker worker) throws HeadlessException {
		super();
		setModal(true);
		this.parent = parent;
		startWorkerDialog(title, worker);
	}
	
	private void startWorkerDialog(String title, SwingWorker worker) {
		this.title = title;
		this.worker = worker;
		this.setContentPane(buildPanel());
		
		// dialog props
		setLocationRelativeTo(parent);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		pack();

		worker.execute();
		timer = new Timer(200,this);
		timer.start();
		
		setVisible(true);
	}
	
	private JPanel buildPanel() {
		initComponents();
		// JGoodies layout
		FormLayout layout = new FormLayout(
                /* columns */ 	"max(100dlu;pref)",
                /* rows    */ 	"pref, 3dlu, max(10dlu;pref), 3dlu, pref"
                );
		PanelBuilder builder = new PanelBuilder(layout);
        builder.setDefaultDialogBorder();
        CellConstraints cc = new CellConstraints();
        builder.addTitle(title,cc.xy(1, 1));
        builder.addSeparator("",cc.xy(1, 2));
        builder.add(messageLabel,cc.xy(1, 3));
        builder.add(new JButton(cancelAction),cc.xy(1, 5));
        return builder.getPanel();
	}

	private void initComponents() {
		messageLabel = new JLabel(message);
		cancelAction = new CancelAction();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(worker.isDone()) {
			this.setVisible(false);
			message = "";
			count = 0;
		} else {
			message = message + ".";
			messageLabel.setText(message);
			if(count==10) {
				message = "";
				count = 0;
			}
			count++;
		}
		
	}

	private class CancelAction extends AbstractAction {
		public CancelAction() {
			super("Cancel");
		}
		public void actionPerformed(ActionEvent e) {
			worker.cancel(true);
		}
	}
}
