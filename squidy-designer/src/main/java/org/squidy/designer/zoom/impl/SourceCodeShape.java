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

package org.squidy.designer.zoom.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.designer.component.CropScroll;
import org.squidy.designer.component.button.ImageButton;
import org.squidy.designer.components.code.SyntaxDocument;
import org.squidy.designer.event.ZoomActionEvent;
import org.squidy.designer.event.ZoomActionListener;
import org.squidy.designer.model.NodeShape;
import org.squidy.designer.piccolo.JComponentWrapper;
import org.squidy.designer.util.FontUtils;
import org.squidy.designer.util.ShapeUtils;
import org.squidy.designer.zoom.ImageShape;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * <code>ZoomSourceCode</code>.
 * 
 * <pre>
 * Date: Feb 19, 2009
 * Time: 2:50:26 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: SourceCodeShape.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class SourceCodeShape extends ImageShape {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 9169948617106975478L;

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(SourceCodeShape.class);

	private static final int CODE_PANE_WIDTH = 800;

	private NodeShape nodeShape;

	private URL sourceCodeURL;

	/**
	 * @return the sourceCodeURL
	 */
	public final URL getSourceCodeURL() {
		return sourceCodeURL;
	}

	/**
	 * @param sourceCodeURL
	 *            the sourceCodeURL to set
	 */
	public final void setSourceCodeURL(URL sourceCodeURL) {
		URL oldSourceCodeURL = this.sourceCodeURL;
		this.sourceCodeURL = sourceCodeURL;

		// Do re-initialize codePane if a new source code URL has been set and
		// only if
		// codePane has been initialized after zooming in.
		if (!oldSourceCodeURL.equals(sourceCodeURL)) {
			if (codePane != null) {
				try {
					FileInputStream fis = new FileInputStream(sourceCodeURL.getPath());
					codePane.read(fis, null);
				}
				catch (FileNotFoundException e) {
					nodeShape.publishFailure(e);
				}
				catch (IOException e) {
					nodeShape.publishFailure(e);
				}

				// Reset calculations -> used in paint method.
				sourceName = null;
				className = null;
			}
		}
	}

	private CropScroll cropScroll;

	private JEditorPane codePane;
	private boolean dirty = false;

	private ImageButton saveSourceCodeAction;
	private ImageButton revertAction;

	private String originSourceCode;

	/**
	 * @param sourceCodeURL
	 */
	public SourceCodeShape(NodeShape valveShape, URL sourceCodeURL) {
		super("Source Code", SourceCodeShape.class.getResource("/images/text_code_java.png"));

		this.nodeShape = valveShape;
		this.sourceCodeURL = sourceCodeURL;

		// Add save source code to action bar.
		saveSourceCodeAction = new ImageButton(ImageButton.class.getResource("/images/24x24/disk_blue.png"), "Save");
		saveSourceCodeAction.setEnabled(false);
		saveSourceCodeAction.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				rebuildValveIfDirty();
			}
		});
		addAction(saveSourceCodeAction);
		ShapeUtils.setApparent(saveSourceCodeAction, false);

		// Add revert action to action bar.
		revertAction = new ImageButton(ImageButton.class.getResource("/images/24x24/undo.png"), "Revert");
		revertAction.setEnabled(false);
		revertAction.addZoomActionListener(new ZoomActionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.squidy.designer.event.ZoomActionListener#actionPerformed
			 * (org.squidy.designer.event.ZoomActionEvent)
			 */
			public void actionPerformed(ZoomActionEvent e) {
				if (codePane != null && originSourceCode != null) {
					codePane.setText(originSourceCode);
				}
				markClean();
			}
		});
		addAction(revertAction);
		ShapeUtils.setApparent(revertAction, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#zoomBegan()
	 */
	@Override
	protected void zoomBegan() {
		super.zoomBegan();

		if (cropScroll == null) {
			new Thread() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Thread#run()
				 */
				public void run() {
					initPane();
				};
			}.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.shape.ZoomShape#zoomEnded()
	 */
	@Override
	protected void zoomEnded() {
		super.zoomEnded();

		ShapeUtils.setApparent(cropScroll, true);
	}

	private void initPane() {
		// Add Zoomable Component
		new Thread(new Runnable() {
			public void run() {
				PNode codePane = JComponentWrapper.create(createCodePane(sourceCodeURL));
				cropScroll = new CropScroll(codePane, new Dimension(1000, 780), 0.2);
				cropScroll.setOffset(getBoundsReference().getCenterX() - cropScroll.getBoundsReference().getCenterX(),
						160);
				addChild(cropScroll);
				// ShapeUtils.setApparent(cropScroll, false);
			}
		}).start();
	}

	private JEditorPane createCodePane(URL sourceCodeURL) {

		codePane = new JEditorPane() {
			@Override
			protected void processComponentKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {
					System.out.println("Code completion");

					int caretPosition = codePane.getCaretPosition();

					String code = codePane.getText();
					switch (code.charAt(caretPosition - 1)) {
					case '.':
						int pseudoCaret = caretPosition - 1;
						StringBuilder word = new StringBuilder();
						for (char c = code.charAt(--pseudoCaret); !isEndOfWord(c); c = code.charAt(--pseudoCaret)) {
							word.append(c);
						}

						word = word.reverse();

						System.out.println("WORD: " + word);

						// Class<?> type =
						// ReflectionUtil.loadClass(word.toString());
						//						
						// System.out.println("TYPE: " + type);

						JPopupMenu menu = new JPopupMenu("sdaf");
						Point p = codePane.getCaret().getMagicCaretPosition();
						System.out.println("CARET POS: " + p);
						// Point p = codePane.get

						// menu.setPreferredSize(new Dimension(200, 200));
						menu.setLocation(30, 30);
						menu.add("test");

						codePane.add(menu);

						// System.out.println(p);

						// codePane.get

						menu.show(codePane, p.x, p.y);

						break;
					}
				}

				super.processComponentKeyEvent(e);
			}

			/**
			 * @param c
			 * @return
			 */
			private boolean isEndOfWord(char c) {
				return c == ' ' || c == '\n' || c == '\r' || c == '\t';
			}
		};

		EditorKit editorKit = new StyledEditorKit() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 7024886168909204806L;

			public Document createDefaultDocument() {
				return new SyntaxDocument();
			}
		};

		codePane.setEditorKitForContentType("text/java", editorKit);
		codePane.setContentType("text/java");

		try {
			FileInputStream fis = new FileInputStream(sourceCodeURL.getPath());
			codePane.read(fis, null);
			originSourceCode = codePane.getText();

			computeHeightOfCodePane();
			codePane.setAutoscrolls(true);
		}
		catch (Exception e) {
			codePane.setText("File not found!");
		}

		codePane.requestFocus();
		codePane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		codePane.addKeyListener(new KeyAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);

				switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_SPACE:
					computeHeightOfCodePane();
					break;
				}

				markDirty();
			}
		});

		// final JPopupMenu menu = new JPopupMenu();
		//
		// JMenuItem i = new JMenuItem("Option 1");
		// JMenuItem i2 = new JMenuItem("Option 2");
		// menu.add(i);
		// menu.add(i2);
		// edit.add(menu);
		// getComponent().addKeyListener(new KeyAdapter() {
		//		
		// public void keyTyped(KeyEvent e) {
		// if(e.getModifiers() == 2 && e.getKeyChar() == ' ') {
		// Point popupLoc = edit.getCaret().getMagicCaretPosition();
		// System.out.println(popupLoc);
		// menu.setLocation(new
		// Point((int)popupLoc.getX(),(int)popupLoc.getY()));
		// menu.setVisible(true);
		// }
		//		
		// }
		// });

		return codePane;
	}

	/**
	 * 
	 */
	void markDirty() {
		dirty = true;
		saveSourceCodeAction.setEnabled(true);
		revertAction.setEnabled(true);
	}

	/**
	 * 
	 */
	void markClean() {
		dirty = false;
		saveSourceCodeAction.setEnabled(false);
		revertAction.setEnabled(false);

		computeHeightOfCodePane();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedIn()
	 */
	@Override
	protected void layoutSemanticsZoomedIn() {
		super.layoutSemanticsZoomedIn();

		ShapeUtils.setApparent(cropScroll, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.designer.zoom.ZoomShape#layoutSemanticsZoomedOut()
	 */
	@Override
	protected void layoutSemanticsZoomedOut() {
		if (cropScroll != null) {
			ShapeUtils.setApparent(cropScroll, false);

			rebuildValveIfDirty();

			// Clear code pane and its crop scroll
			cropScroll.removeFromParent();
			codePane = null;
			cropScroll = null;
		}

		super.layoutSemanticsZoomedOut();
	}

	/**
	 * 
	 */
	private void rebuildValveIfDirty() {

		if (!dirty) {
			return;
		}

		nodeShape.persistCode(new File(sourceCodeURL.getFile()), codePane.getText());

		markClean();
	}

	/**
	 * 
	 */
	void computeHeightOfCodePane() {
		if (codePane != null) {
			int lineCount = FontUtils.getLineCount(codePane.getText(), CODE_PANE_WIDTH);
			FontMetrics fm = codePane.getFontMetrics(codePane.getFont());
			codePane.setPreferredSize(new Dimension(CODE_PANE_WIDTH, lineCount * fm.getHeight()));
		}
	}

	private static Font fontName = internalFont.deriveFont(50f);
	private static Font fontSource = internalFont.deriveFont(15f);

	private String sourceName;
	private int sourceNameX;

	private String className;
	private int classNameX;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.designer.zoom.ZoomNavigationShape#paintShapeZoomedIn
	 * (edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	protected void paintShapeZoomedIn(PPaintContext paintContext) {
		super.paintShapeZoomedIn(paintContext);

		Graphics2D g = paintContext.getGraphics();

		PBounds bounds = getBoundsReference();

		Class<?> type = nodeShape.getProcessable().getClass();

		String typeName = type.getSimpleName();

		g.setFont(fontName);
		g.drawString(typeName, (int) (bounds.x + 50), 140);

		g.setFont(fontSource);

		// Calculate sourceName string if not done yet.
//		if (sourceName == null) {
//			sourceName = FontUtils.createCroppedLabelIfNecessary(g.getFontMetrics(), "Source: "
//					+ sourceCodeURL.toString(), (int) (bounds.width));
//			sourceNameX = (int) (bounds.x + bounds.width - FontUtils.getWidthOfText(g.getFontMetrics(), sourceName) - 20);
//		}
//		g.drawString(sourceName, sourceNameX, 90);

		// Calculate className string if not done yet.
		if (className == null) {
			className = FontUtils.createCroppedLabelIfNecessary(g.getFontMetrics(), "Class: " + type.getName(),
					(int) (bounds.width * 0.7));
			classNameX = (int) (bounds.x + bounds.width - FontUtils.getWidthOfText(g.getFontMetrics(), className) - 20);
		}
		g.drawString(className, classNameX, 110);
	}
}
