/**
 * 
 */
package org.squidy.manager.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * <code>FileChooserControl</code>.
 * 
 * <pre>
 * Date: Feb 14, 2010
 * Time: 12:33:03 AM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id$
 * @since 1.0.2
 */
public class FileChooserControl extends AbstractBasicControl<String, JPanel> {

	public FileChooserControl(String value, String label) {
		super(new ChooserPanel(value, label));
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#getValue()
	 */
	public String getValue() {
		return ((ChooserPanel) getComponent()).getTextField().getText();
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#setValue(java.lang.Object)
	 */
	public void setValue(String value) {
		((ChooserPanel) getComponent()).getTextField().setText(value);
	}
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.IBasicControl#valueFromString(java.lang.String)
	 */
//	@Override
	public String valueFromString(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.controls.AbstractBasicControl#reconcileComponent()
	 */
	@Override
	protected void reconcileComponent() {
		((ChooserPanel) getComponent()).setFileChooserControl(this);
	}
	
	static class ChooserPanel extends JPanel {
		
		/**
		 * Generated serial version UID.
		 */
		private static final long serialVersionUID = -3454497591286752926L;

		private FileChooserControl fileChooserControl;
		
		/**
		 * @param fileChooserControl the fileChooserControl to set
		 */
		public void setFileChooserControl(FileChooserControl fileChooserControl) {
			this.fileChooserControl = fileChooserControl;
		}

		private JTextField textField;
		
		/**
		 * @return the textField
		 */
		public JTextField getTextField() {
			return textField;
		}

		ChooserPanel(String value, String label) {
			super(new BorderLayout());
		
			textField = new JTextField(value);
			textField.setPreferredSize(new Dimension(200, 20));
			add(textField, BorderLayout.CENTER);
			
			JButton choose = new JButton(label);
			choose.addActionListener(new ActionListener() {
	
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					
					int option = fileChooser.showOpenDialog(null);
					if (option == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						
						textField.setText(file.getAbsolutePath());
						fileChooserControl.firePropertyUpdateEvent(file.getAbsolutePath());
					}
				}
			});
			add(choose, BorderLayout.EAST);
		}
	}
}
