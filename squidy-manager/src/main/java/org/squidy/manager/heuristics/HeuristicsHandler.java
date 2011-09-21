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

package org.squidy.manager.heuristics;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.manager.parser.DefaultNamespacePrefixMapper;


/**
 * <code>HeuristicsHandler</code>.
 * 
 * <pre>
 * Date: Mar 28, 2009
 * Time: 2:12:14 PM
 * </pre>
 * 
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: HeuristicsHandler.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class HeuristicsHandler {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(HeuristicsHandler.class);

	private static HeuristicsHandler INSTANCE;

	/**
	 * Singleton pattern.
	 * 
	 * @return Single instance of diagram model parser.
	 */
	public static HeuristicsHandler getHeuristicsHandler() {
		if (INSTANCE == null) {
			INSTANCE = new HeuristicsHandler();
		}
		return INSTANCE;
	}

	/**
	 * Use singleton pattern.
	 */
	protected HeuristicsHandler() {
		
	}

	private static JAXBContext context;

	/**
	 * Initializes the JAXB context with all required classes.
	 * 
	 * @return The initialized JAXB context.
	 * @throws JAXBException
	 *             The exception will be thrown if initialization of the JAXB
	 *             context has been failed.
	 */
	protected JAXBContext getContext() throws JAXBException {
		if (context != null) {
			return context;
		}
		
		return context = JAXBContext.newInstance(Heuristics.class, Heuristic.class, Match.class);
	}

	/**
	 * @param inputStream
	 * @return
	 * @throws SquidyException
	 */
	public Heuristics load(InputStream inputStream) throws SquidyException {
		try {
			Unmarshaller unmarshaller = getContext().createUnmarshaller();
			return (Heuristics) unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SquidyException("Could not unmarshal input stream.", e);
		}
	}

	/**
	 * @param outputStream
	 * @param xmi
	 */
	public void save(OutputStream outputStream, Heuristics data) {
		try {
			Marshaller marshaller = getContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new DefaultNamespacePrefixMapper());
			marshaller.marshal(data, outputStream);
		} catch (JAXBException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
	}
}
