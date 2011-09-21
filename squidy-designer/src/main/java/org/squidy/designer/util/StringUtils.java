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

package org.squidy.designer.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code>StringUtils</code>.
 * 
 * <pre>
 * Date: Mar 31, 2009
 * Time: 3:33:06 AM
 * </pre>
 * 
 * 
 * @author Roman R&amp;aumldle <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman
 *         .Raedle@uni-konstanz.de</a> Human-Computer Interaction Group
 *         University of Konstanz
 * 
 * @version $Id: StringUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class StringUtils {

	/**
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String slurp(InputStream in) throws IOException {
		StringBuilder out = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	/**
	 * @param input
	 * @param keyValueSet
	 * @return
	 */
	public static String replaceVariables(String input, Hashtable<String, String> keyValueSet) {

		Pattern pattern = Pattern.compile("(\\$\\{.*?\\})");

		String value = keyValueSet.get("${type_name}");
		
		Matcher matcher = pattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, value);
		}
		matcher.appendTail(sb);

		return sb.toString();
	}
}
