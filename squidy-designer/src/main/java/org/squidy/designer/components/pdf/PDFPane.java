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

package org.squidy.designer.components.pdf;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PagePanel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class PDFPane extends PNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2806101138711805130L;

	// Logger to info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(PDFPane.class);

	private PagePanel[] pdfComponents;
	private int pageHeight;
	private int pageWidth;

	public PDFPane(String pdfFileLoc) {
		super();
		try {
			// load a pdf from a byte buffer
			File file = new File(pdfFileLoc);
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			PDFFile pdffile = new PDFFile(buf);
			pdfComponents = new PagePanel[pdffile.getNumPages()];
			pageHeight = (int)pdffile.getPage(0).getHeight()+3;
			pageWidth = (int)pdffile.getPage(0).getWidth()+1;
			
			for (int i = 0; i < pdfComponents.length; i++) {
				PagePanel p = new PagePanel();
				p.setBackground(Color.BLACK);
				p.setPreferredSize(new Dimension(pageWidth,pageHeight));
				PSwing ps = new PSwing(p);
				
				ps.setOffset(0,i*pageHeight);
				addChild(ps);
				p.showPage(pdffile.getPage(i));
			}
		} catch (IOException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error("Error in " + PDFPane.class.getName() + ".", e);
			}
			System.out.println(e.getMessage());
		}
	}
}
