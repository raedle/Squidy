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

package org.squidy.designer.components.code;

import java.awt.Color;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 * <code>SyntaxDocument</code>.
 * 
 * <pre>
 * Date: Mar 31, 2009
 * Time: 3:00:19 AM
 * </pre>
 * 
 * @version $Id: SyntaxDocument.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class SyntaxDocument extends DefaultStyledDocument {
	
	/**
	 * Generated serial version UID. 
	 */
	private static final long serialVersionUID = -9153528394075749586L;
	
	private Element rootElement;

	private boolean multiLineComment;
	private MutableAttributeSet normal;
	private MutableAttributeSet keyword;
	private MutableAttributeSet annotation;
	private MutableAttributeSet comment;
	private MutableAttributeSet quote;

	private HashSet<String> keywords;
	
	private TabSet tabSet = new TabSet(new TabStop[] {
			new TabStop(20),
			new TabStop(40),
			new TabStop(60),
			new TabStop(80),
			new TabStop(100),
			new TabStop(120),
			new TabStop(140),
			new TabStop(160),
			new TabStop(180),
			new TabStop(200),
			});

	public SyntaxDocument() {
		putProperty(PlainDocument.tabSizeAttribute, new Integer(0));
		putProperty(PlainDocument.tabSizeAttribute, 0);
		putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
		
		Style style = getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setTabSet(style, tabSet);
		
		rootElement = getDefaultRootElement();

		normal = new SimpleAttributeSet();
		StyleConstants.setForeground(normal, Color.black);

		comment = new SimpleAttributeSet();
		StyleConstants.setForeground(comment, new Color(0, 128, 0));
		StyleConstants.setItalic(comment, true);

		keyword = new SimpleAttributeSet();
		StyleConstants.setForeground(keyword, new Color(0x90, 0x3d, 0x7a));
		StyleConstants.setBold(keyword, true);

		annotation = new SimpleAttributeSet();
		StyleConstants.setForeground(annotation, new Color(0x64, 0x64, 0x74));
		
		quote = new SimpleAttributeSet();
		StyleConstants.setForeground(quote, Color.BLUE);

		keywords = new HashSet<String>();
		keywords.add("abstract");
		keywords.add("boolean");
		keywords.add("break");
		keywords.add("byte");
		keywords.add("byvalue");
		keywords.add("case");
		keywords.add("cast");
		keywords.add("catch");
		keywords.add("char");
		keywords.add("class");
		keywords.add("const");
		keywords.add("continue");
		keywords.add("default");
		keywords.add("do");
		keywords.add("double");
		keywords.add("else");
		keywords.add("extends");
		keywords.add("false");
		keywords.add("final");
		keywords.add("finally");
		keywords.add("float");
		keywords.add("for");
		keywords.add("future");
		keywords.add("generic");
		keywords.add("goto");
		keywords.add("if");
		keywords.add("implements");
		keywords.add("import");
		keywords.add("inner");
		keywords.add("instanceof");
		keywords.add("int");
		keywords.add("interface");
		keywords.add("long");
		keywords.add("native");
		keywords.add("new");
		keywords.add("null");
		keywords.add("operator");
		keywords.add("outer");
		keywords.add("package");
		keywords.add("private");
		keywords.add("protected");
		keywords.add("public");
		keywords.add("rest");
		keywords.add("return");
		keywords.add("short");
		keywords.add("static");
		keywords.add("super");
		keywords.add("switch");
		keywords.add("synchronized");
		keywords.add("this");
		keywords.add("throw");
		keywords.add("throws");
		keywords.add("transient");
		keywords.add("true");
		keywords.add("try");
		keywords.add("var");
		keywords.add("void");
		keywords.add("volatile");
		keywords.add("while");
	}
	
	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		if (str.equals("{"))
			str = addMatchingBrace(offset);

		super.insertString(offset, str, a);
		processChangedLines(offset, str.length());
	}

	/*
	 * Override to apply syntax highlighting after the document has been updated
	 */
	public void remove(int offset, int length) throws BadLocationException {
		super.remove(offset, length);
		processChangedLines(offset, 0);
	}

	/*
	 * Determine how many lines have been changed, then apply highlighting to
	 * each line
	 */
	public void processChangedLines(int offset, int length) throws BadLocationException {
		String content = getText(0, getLength());

		// The lines affected by the latest document update

		int startLine = rootElement.getElementIndex(offset);
		int endLine = rootElement.getElementIndex(offset + length);

		// Make sure all comment lines prior to the start line are commented
		// and determine if the start line is still in a multi line comment

		setMultiLineComment(commentLinesBefore(content, startLine));

		// Do the actual highlighting

		for (int i = startLine; i <= endLine; i++) {
			applyHighlighting(content, i);
		}

		// Resolve highlighting to the next end multi line delimiter

		if (isMultiLineComment())
			commentLinesAfter(content, endLine);
		else
			highlightLinesAfter(content, endLine);
	}

	/*
	 * Highlight lines when a multi line comment is still 'open' (ie. matching
	 * end delimiter has not yet been encountered)
	 */
	private boolean commentLinesBefore(String content, int line) {
		int offset = rootElement.getElement(line).getStartOffset();

		// Start of comment not found, nothing to do

		int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);

		if (startDelimiter < 0)
			return false;

		// Matching start/end of comment found, nothing to do

		int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);

		if (endDelimiter < offset & endDelimiter != -1)
			return false;

		// End of comment not found, highlight the lines

		setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, comment, false);
		return true;
	}

	/*
	 * Highlight comment lines to matching end delimiter
	 */
	private void commentLinesAfter(String content, int line) {
		int offset = rootElement.getElement(line).getEndOffset();

		// End of comment not found, nothing to do

		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (endDelimiter < 0)
			return;

		// Matching start/end of comment found, comment the lines

		int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);

		if (startDelimiter < 0 || startDelimiter <= offset) {
			setCharacterAttributes(offset, endDelimiter - offset + 1, comment, false);
		}
	}

	/*
	 * Highlight lines to start or end delimiter
	 */
	private void highlightLinesAfter(String content, int line) throws BadLocationException {
		int offset = rootElement.getElement(line).getEndOffset();

		// Start/End delimiter not found, nothing to do

		int startDelimiter = indexOf(content, getStartDelimiter(), offset);
		int endDelimiter = indexOf(content, getEndDelimiter(), offset);

		if (startDelimiter < 0)
			startDelimiter = content.length();

		if (endDelimiter < 0)
			endDelimiter = content.length();

		int delimiter = Math.min(startDelimiter, endDelimiter);

		if (delimiter < offset)
			return;

		// Start/End delimiter found, reapply highlighting

		int endLine = rootElement.getElementIndex(delimiter);

		for (int i = line + 1; i < endLine; i++) {
			Element branch = rootElement.getElement(i);
			Element leaf = getCharacterElement(branch.getStartOffset());
			AttributeSet as = leaf.getAttributes();

			if (as.isEqual(comment))
				applyHighlighting(content, i);
		}
	}

	/*
	 * Parse the line to determine the appropriate highlighting
	 */
	private void applyHighlighting(String content, int line) throws BadLocationException {
		int startOffset = rootElement.getElement(line).getStartOffset();
		int endOffset = rootElement.getElement(line).getEndOffset() - 1;

		int lineLength = endOffset - startOffset;
		int contentLength = content.length();

		if (endOffset >= contentLength)
			endOffset = contentLength - 1;

		// check for multi line comments
		// (always set the comment attribute for the entire line)

		if (endingMultiLineComment(content, startOffset, endOffset) || isMultiLineComment()
				|| startingMultiLineComment(content, startOffset, endOffset)) {
			setCharacterAttributes(startOffset, endOffset - startOffset + 1, comment, false);
			return;
		}

		// set normal attributes for the line

		setCharacterAttributes(startOffset, lineLength, normal, true);

		// check for single line comment

		int index = content.indexOf(getSingleLineDelimiter(), startOffset);

		if ((index > -1) && (index < endOffset)) {
			setCharacterAttributes(index, endOffset - index + 1, comment, false);
			endOffset = index - 1;
		}

		// check for tokens

		checkForTokens(content, startOffset, endOffset);
	}

	/*
	 * Does this line contain the start delimiter
	 */
	private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
			throws BadLocationException {
		int index = indexOf(content, getStartDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;
		else {
			setMultiLineComment(true);
			return true;
		}
	}

	/*
	 * Does this line contain the end delimiter
	 */
	private boolean endingMultiLineComment(String content, int startOffset, int endOffset) throws BadLocationException {
		int index = indexOf(content, getEndDelimiter(), startOffset);

		if ((index < 0) || (index > endOffset))
			return false;
		else {
			setMultiLineComment(false);
			return true;
		}
	}

	/*
	 * We have found a start delimiter and are still searching for the end
	 * delimiter
	 */
	private boolean isMultiLineComment() {
		return multiLineComment;
	}

	private void setMultiLineComment(boolean value) {
		multiLineComment = value;
	}

	/*
	 * Parse the line for tokens to highlight
	 */
	private void checkForTokens(String content, int startOffset, int endOffset) {
		while (startOffset <= endOffset) {
			// skip the delimiters to find the start of a new token

			while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
				if (startOffset < endOffset)
					startOffset++;
				else
					return;
			}

			// Extract and process the entire token

			if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1)))
				startOffset = getQuoteToken(content, startOffset, endOffset);
			else
				startOffset = getOtherToken(content, startOffset, endOffset);
		}
	}

	/*
	 *
	 */
	private int getQuoteToken(String content, int startOffset, int endOffset) {
		String quoteDelimiter = content.substring(startOffset, startOffset + 1);
		String escapeString = getEscapeString(quoteDelimiter);

		int index;
		int endOfQuote = startOffset;

		// skip over the escape quotes in this quote

		index = content.indexOf(escapeString, endOfQuote + 1);

		while ((index > -1) && (index < endOffset)) {
			endOfQuote = index + 1;
			index = content.indexOf(escapeString, endOfQuote);
		}

		// now find the matching delimiter

		index = content.indexOf(quoteDelimiter, endOfQuote + 1);

		if ((index < 0) || (index > endOffset))
			endOfQuote = endOffset;
		else
			endOfQuote = index;

		setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, quote, false);

		return endOfQuote + 1;
	}

	/*
	 *
	 */
	private int getOtherToken(String content, int startOffset, int endOffset) {
		int endOfToken = startOffset + 1;

		while (endOfToken <= endOffset) {
			if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
				break;

			endOfToken++;
		}

		String token = content.substring(startOffset, endOfToken);
		
		int lengthBefore = startOffset;
		StringTokenizer tokenizer = new StringTokenizer(token, ".");
		while (tokenizer.hasMoreTokens()) {
			String subToken = tokenizer.nextToken();
			
			if (isKeyword(subToken)) {
				setCharacterAttributes(lengthBefore, subToken.length(), keyword, false);
			}
			else if (isAnnotation(subToken)) {
				setCharacterAttributes(lengthBefore, subToken.length(), annotation, false);
			}
			
			lengthBefore += subToken.length() + ".".length();			
		}
		
		return endOfToken + 1;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int indexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.indexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index + 1;
		}

		return index;
	}

	/*
	 * Assume the needle will the found at the start/end of the line
	 */
	private int lastIndexOf(String content, String needle, int offset) {
		int index;

		while ((index = content.lastIndexOf(needle, offset)) != -1) {
			String text = getLine(content, index).trim();

			if (text.startsWith(needle) || text.endsWith(needle))
				break;
			else
				offset = index - 1;
		}

		return index;
	}

	private String getLine(String content, int offset) {
		int line = rootElement.getElementIndex(offset);
		Element lineElement = rootElement.getElement(line);
		int start = lineElement.getStartOffset();
		int end = lineElement.getEndOffset();
		return content.substring(start, end - 1);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isDelimiter(String character) {
		String operands = ";:{}()[]+-/%<=>!&|^~*";

		if (Character.isWhitespace(character.charAt(0)) || operands.indexOf(character) != -1)
			return true;
		else
			return false;
	}

	/*
	 * Override for other languages
	 */
	protected boolean isQuoteDelimiter(String character) {
		String quoteDelimiters = "\"'";

		if (quoteDelimiters.indexOf(character) < 0)
			return false;
		else
			return true;
	}

	/*
	 * Override for other languages
	 */
	protected boolean isKeyword(String token) {
		return keywords.contains(token);
	}

	/*
	 * Override for other languages
	 */
	protected boolean isAnnotation(String token) {
		return token.startsWith("@");
	}
	
	/*
	 * Override for other languages
	 */
	protected String getStartDelimiter() {
		return "/*";
	}

	/*
	 * Override for other languages
	 */
	protected String getEndDelimiter() {
		return "*/";
	}

	/*
	 * Override for other languages
	 */
	protected String getSingleLineDelimiter() {
		return "//";
	}

	/*
	 * Override for other languages
	 */
	protected String getEscapeString(String quoteDelimiter) {
		return "\\" + quoteDelimiter;
	}

	/*
	 *
	 */
	protected String addMatchingBrace(int offset) throws BadLocationException {
		StringBuffer whiteSpace = new StringBuffer();
		int line = rootElement.getElementIndex(offset);
		int i = rootElement.getElement(line).getStartOffset();

		while (true) {
			String temp = getText(i, 1);

			if (temp.equals(" ") || temp.equals("\t")) {
				whiteSpace.append(temp);
				i++;
			}
			else {
				break;
			}
		}

		return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
	}
}
