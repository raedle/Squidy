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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.squidy.util.ExceptionUtilities;


/**
 * <code>ExceptionDialog</code>.
 *
 * <pre>
 * Date: Nov 3, 2008
 * Time: 12:11:22 AM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: ExceptionDialog.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class ExceptionDialog extends JDialog {

	private Submission submission;
	
	private Exception exception;
	
	public ExceptionDialog(Frame owner, Exception exception, final Submission submission) {
		super(owner, true);
		
		this.exception = exception;
		this.submission = submission;
		
		String stackTrace = ExceptionUtilities.getStackTrace(exception);
		
		JTextArea exceptionDetail = new JTextArea(stackTrace);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				ExceptionDialog.this.setVisible(false);
				ExceptionDialog.this.dispose();
			}
		});
		
//		JButton detail = new JButton("Detail");
		
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e) {
				
				submission.submit();
				
				ExceptionDialog.this.setVisible(false);
				ExceptionDialog.this.dispose();
			}
		});
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(5, 5, 5, 5);
		
		add(new JScrollPane(exceptionDetail), c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		add(close, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		
		add(submit, c);
		
		setSize(new Dimension(500, 300));
		setPreferredSize(new Dimension(500, 300));
		
		int x = owner.getX();
		int y = owner.getY();
		int width = owner.getWidth();
		int height = owner.getHeight();
		
		setLocation(x + ((width - getWidth()) / 2), y + ((height - getHeight()) / 2));
	}
	
	public static void showExceptionDialog(Frame owner, Exception exception, Submission submission) {
		ExceptionDialog dialog = new ExceptionDialog(owner, exception, submission);
		
		dialog.setVisible(true);
	}
}
