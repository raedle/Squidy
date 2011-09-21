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

package org.squidy.manager.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.dynamiccode.DynamicCodeClassLoader;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.data.Processor;
import org.squidy.manager.model.Data;
import org.squidy.manager.model.Node;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Pipeline;
import org.squidy.manager.model.Workspace;
import org.squidy.manager.scanner.PackageScanner;


/**
 * <code>ModelHandler</code>.
 * 
 * <pre>
 * Date: Feb 16, 2008
 * Time: 11:09:44 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>,, University of Konstanz
 * @version $Id: ModelHandler.java 772 2011-09-16 15:39:44Z raedle $$
 * 
 */
public class ModelHandler {

    // Logger to log info, error, debug,... messages.
    private static final Log LOG = LogFactory.getLog(ModelHandler.class);

    private final static Class[] CLASS_NAMES = PackageScanner
	    .findAllClassesWithAnnotation(Processor.class);

    private static ModelHandler INSTANCE;

    /**
     * Singleton pattern.
     * 
     * @return Single instance of diagram model parser.
     */
    public static ModelHandler getModelHandler() {
	if (INSTANCE == null) {
	    INSTANCE = new ModelHandler();
	}
	return INSTANCE;
    }

    /**
     * Use singleton pattern.
     */
    protected ModelHandler() {

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

	ClassLoader classLoader = DynamicCodeClassLoader.DYNAMIC_CODE;
	return context = JAXBContext.newInstance(ReflectionUtil
		.loadContextClasses(Thread.currentThread()
			.getContextClassLoader(), PackageScanner
			.findAllClassNamesWithAnnotation(Processor.class),
			Data.class, Workspace.class, Pipeline.class,
			Node.class, Pipe.class));
    }

    public static void resetJAXBContext(Class<?> type) {
	// TODO [RR]: This is a DIRTY hack!
	// PackageScanner.CLASS_CACHE.clear();

	if (!PackageScanner.CLASS_CACHE.contains(type)) {
	    PackageScanner.CLASS_CACHE.add(type);
	}

	String[] classes = PackageScanner
		.findAllClassNamesWithAnnotation(Processor.class);
	List<String> only = new ArrayList<String>();

	for (String className : classes) {
	    if (!type.getName().equals(className)) {
		only.add(className);
		System.out.println("IN: " + className);
	    } else {
		System.out.println("REMOVED: " + className);
	    }
	}

	try {
	    context = null;
	    ClassLoader classLoader = DynamicCodeClassLoader.DYNAMIC_CODE;
	    context = JAXBContext.newInstance(ReflectionUtil
		    .loadContextClasses(classLoader, only
			    .toArray(new String[0]), type, Data.class,
			    Workspace.class, Pipeline.class, Node.class,
			    Pipe.class));
	} catch (JAXBException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Parses a given input stream (should contain model and diagram code as xml
     * structure) and returns it as an object structure.
     * 
     * @param inputStream
     *            The input stream should contain model and diagram structure.
     * @return The parsed XMI document containing model and diagram in object
     *         representation.
     */
    @SuppressWarnings("restriction")
    public Data load(InputStream inputStream) {
	try {
	    Unmarshaller unmarshaller = getContext().createUnmarshaller();
	    unmarshaller.setEventHandler(new ValidationEventHandler() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.xml.bind.ValidationEventHandler#handleEvent(javax.xml
		 * .bind.ValidationEvent)
		 */
		public boolean handleEvent(ValidationEvent e) {
		    // Ignore this as workspace-shape is located in not
		    // referenced squidy-designer module
		    if (e
			    .getMessage()
			    .startsWith(
				    "unexpected element (uri:\"\", local:\"workspace-shape\")")) {
			return true;
		    }

		    ValidationEventLocator locator = e.getLocator();
		    if (LOG.isErrorEnabled()) {
			LOG.error("Error while reading input stream: \""
				+ e.getMessage() + "\" [line="
				+ locator.getLineNumber() + ", column="
				+ locator.getColumnNumber() + "]"
				+ e.getLinkedException());
		    }
		    return true;
		}
	    });

	    return (Data) unmarshaller.unmarshal(inputStream);
	} catch (JAXBException e) {
	    if (LOG.isErrorEnabled()) {
		LOG.error(e.getMessage(), e);
	    }
	    return null;
	}
    }
}
