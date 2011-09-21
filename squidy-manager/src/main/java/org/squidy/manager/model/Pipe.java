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

package org.squidy.manager.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.ProcessingFeedback;
import org.squidy.manager.ProcessingFeedbackable;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.util.DataUtility;


/**
 * <code>Pipe</code>.
 * 
 * <pre>
 * Date: Feb 14, 2008
 * Time: 5:51:29 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: Pipe.java 772 2011-09-16 15:39:44Z raedle $$
 */
@XmlType(name = "Pipe")
public class Pipe extends Processable implements ProcessingFeedbackable {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Pipe.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * 
	 */
	public Pipe() {
		// empty
	}

	@XmlIDREF
	@XmlAttribute(name = "source")
	public Processable source;

	/**
	 * @return
	 */
	public Processable getSource() {
		return source;
	}

	/**
	 * @param source
	 */
	public void setSource(Processable source) {
		this.source = source;
	}

	@XmlIDREF
	@XmlAttribute(name = "target")
	public Processable target;

	public Processable getTarget() {
		return target;
	}

	public void setTarget(Processable target) {
		this.target = target;
	}

	@XmlElement(name = "input-type")
	@XmlElementWrapper(name = "input-types")
	private Collection<Class<? extends IData>> inputTypes = new HashSet<Class<? extends IData>>();

	/**
	 * @return the inputTypes
	 */
	public final Collection<Class<? extends IData>> getInputTypes() {
		return inputTypes;
	}

	/**
	 * @param inputTypes
	 *            the inputTypes to set
	 */
	public final void setInputTypes(Collection<Class<? extends IData>> inputTypes) {
		this.inputTypes = inputTypes;
	}

	@XmlElement(name = "output-type")
	@XmlElementWrapper(name = "output-types")
	private Collection<Class<? extends IData>> outputTypes = new HashSet<Class<? extends IData>>();

	/**
	 * @return the outputTypes
	 */
	public final Collection<Class<? extends IData>> getOutputTypes() {
		return outputTypes;
	}

	/**
	 * @param outputTypes
	 *            the outputTypes to set
	 */
	public final void setOutputTypes(Collection<Class<? extends IData>> outputTypes) {
		this.outputTypes = outputTypes;
	}

	// #############################################################################
	// END JAXB
	// #############################################################################

	// #############################################################################
	// BEGIN IProcessable
	// #############################################################################

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.IProcessable#process(org.squidy.manager
	 * .data.IDataContainer)
	 */
	public IDataContainer process(IDataContainer dataContainer) {

		List<IData> incomingData = new ArrayList<IData>(2);
		
		for (IData data : dataContainer.getData()) {
			
			// Ignore empty input types.
			if (inputTypes.size() == 0) {
				break;
			}
			
			// Ignore if input types doesn't contain current data class.
			if (!inputTypes.contains(data.getClass())) {
				continue;
			}
			
			incomingData.add(data);
		}
		
		// Processing feedback.
		if (processingFeedback != null) {
			for (ProcessingFeedback feedback : processingFeedback) {
				// Need to clone data, otherwise following filter's processing could have side effects on visualization.
				feedback.feedback(DataUtility.getClones(incomingData.toArray(new IData[incomingData.size()])));
			}
		}
		
		List<IData> outgoingData = new ArrayList<IData>(2);
		
		for (IData data : incomingData) {
			
			// Ignore empty output types.
			if (outputTypes.size() == 0) {
				break;
			}
			
			// Ignore if output types doesn't contain current data class.
			if (!outputTypes.contains(data.getClass())) {
				continue;
			}
			
			outgoingData.add(data);
		}
		
		// Ignore if return data size is empty.
		if (outgoingData.size() <= 0) {
			return null;
		}
		
		dataContainer.setData(outgoingData.toArray(new IData[outgoingData.size()]));
		
		return dataContainer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.IProcessable#publish(org.squidy.manager
	 * .data.IData[])
	 */
	public void publish(IData... data) {
		throw new UnsupportedOperationException("Publishing data inside a pipe is currently not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.IProcessable#publish(org.squidy.manager
	 * .data.IDataContainer)
	 */
	public void publish(IDataContainer dataContainer) {
		throw new UnsupportedOperationException("Publishing data inside a pipe is currently not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#start()
	 */
	public void start() throws ProcessException {
		throw new UnsupportedOperationException("Starting a pipe is currently not supported");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.ILaunchable#stop()
	 */
	public void stop() throws ProcessException {
		throw new UnsupportedOperationException("Stopping a pipe is currently not supported");
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.Processable#delete()
	 */
	@Override
	public void delete() throws ProcessException {
		
		if (source instanceof Piping) {
			((Piping) source).removeOutgoingPipe(this);
		}
		
		if (target instanceof Piping) {
			((Piping) target).removeIncomingPipe(this);
		}
		
		super.delete();
	}

	// #############################################################################
	// END IProcessable
	// #############################################################################

	// #############################################################################
	// BEGIN ProcessingFeedback
	// #############################################################################

	private Collection<ProcessingFeedback> processingFeedback;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.ProcessingFeedbackable#setProcessingFeedback
	 * (org.squidy.manager.ProcessingFeedback)
	 */
	public void addProcessingFeedback(ProcessingFeedback feedback) {
		if (feedback == null) {
			return;
		}
		if (processingFeedback == null) {
			processingFeedback = new ArrayList<ProcessingFeedback>();
		} else if (processingFeedback.contains(feedback)) {
			// prevent duplicates
			return;
		}
		processingFeedback.add(feedback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.ProcessingFeedbackable#removeProcessingFeedback
	 * (org.squidy.manager.ProcessingFeedback)
	 */
	public void removeProcessingFeedback(ProcessingFeedback feedback) {
		if (processingFeedback != null && feedback != null) {
			processingFeedback.remove(feedback);
		}
	}

	// #############################################################################
	// END ProcessingFeedback
	// #############################################################################

	// #############################################################################
	// BEGIN INTERNAL
	// #############################################################################
	
	// #############################################################################
	// END INTERNAL
	// #############################################################################

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pipe other = (Pipe) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}
}
