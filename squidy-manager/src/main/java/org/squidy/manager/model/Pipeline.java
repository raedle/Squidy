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

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.manager.ProcessException;
import org.squidy.manager.VisualRepresentation;
import org.squidy.manager.VisualRepresentation.Type;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;


/**
 * <code>Pipeline</code>.
 * 
 * <pre>
 * Date: Feb 15, 2008
 * Time: 11:04:31 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: Pipeline.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
@XmlType(name = "Pipeline")
@Processor(name = "Pipeline", tags = { "pipeline", "container" }, description = "Empty Pipeline to be filled...", types = { Processor.Type.LOGIC })
@VisualRepresentation(type = Type.PIPELINE)
public class Pipeline extends Piping {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(Pipeline.class);

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################

	/**
	 * Default constructor required for JAXB.
	 */
	public Pipeline() {
		// empty
	}

//	@XmlIDREF
//	@XmlElementWrapper(name = "inner-outgoing-pipes")
//	private Collection<Pipe> innerOutgoingPipes = new ArrayList<Pipe>();
//
//	/**
//	 * @return the innerOutgoingPipes
//	 */
//	public final Collection<Pipe> getInnerOutgoingPipes() {
//		return innerOutgoingPipes;
//	}
//
//	/**
//	 * @param innerOutgoingPipes
//	 *            the innerOutgoingPipes to set
//	 */
//	public final void setInnerOutgoingPipes(Collection<Pipe> innerOutgoingPipes) {
//		this.innerOutgoingPipes = innerOutgoingPipes;
//	}
//
//	@XmlIDREF
//	@XmlElementWrapper(name = "inner-incoming-pipes")
//	private Collection<Pipe> innerIncomingPipes = new ArrayList<Pipe>();
//
//	/**
//	 * @return the innerIncomingPipes
//	 */
//	public final Collection<Pipe> getInnerIncomingPipes() {
//		return innerIncomingPipes;
//	}
//
//	/**
//	 * @param innerIncomingPipes
//	 *            the innerIncomingPipes to set
//	 */
//	public final void setInnerIncomingPipes(Collection<Pipe> innerIncomingPipes) {
//		this.innerIncomingPipes = innerIncomingPipes;
//	}

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
		// if (LOG.isDebugEnabled()) {
		// LOG.debug("Processing container in pipeline... " + dataContainer);
		// }

		Collection<Pipe> pipes = getOutgoingPipes();
		int size = pipes.size();

//		System.out.println("SIZE: " + size);

		for (Pipe pipe : pipes) {

			Processable processable = pipe.getTarget();
			// System.out.println(pipe.getSource().getClass().getSimpleName() +
			// " | " + pipe.getTarget().getClass().getSimpleName());
			if (processable.getParent().equals(this)) {
				IDataContainer container = dataContainer;

				// TODO [RR]: Improve performance here. No cloning needed if
				// other pipes are on the same level as the pipe and thus just
				// one parent pipeline exists.
				if (size > 1) {
					container = dataContainer.getClone();
				}

				pipe.process(container);
				if (container == null) {
					continue;
				}

				processable.process(container);
			}
//			else {
//				System.out
//						.println("PROCESSABLE IS ON THE SAME LEVEL AS THE PIPE");
//			}
		}

		return null;
	}

//	public IDataContainer process2(IDataContainer dataContainer) {
//
//		Collection<Pipe> pipes = getOutgoingPipes();
//		int size = pipes.size();
//		for (Pipe pipe : pipes) {
//
//			IDataContainer container = dataContainer;
//			if (size > 1) {
//				container = dataContainer.getClone();
//			}
//
//			container = pipe.process(container);
//			if (container == null) {
//				continue;
//			}
//
//			attachVisitors(container);
//			notifyVisitors(container);
//
//			Processable processable = pipe.getTarget();
//
//			// A processable is inside of a pipeline.
//			if (processable instanceof Pipeline
//					&& processable.equals(pipe.getSource().getParent())) {
//				((Pipeline) processable).publishToOutside(container);
//			} else {
//				processable.process(container);
//			}
//		}
//
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.Processable#delete()
	 */
	@Override
	public void delete() throws ProcessException {
		// This hack avoids concurrent modification exceptions while deleting
		// pipes.
		Collection<Pipe> pipesToDelete = new ArrayList<Pipe>();

		for (Pipe pipe : getPipes()) {
			pipesToDelete.add(pipe);
		}

		// TODO [RR]: Hack for -> Fix me - if uncomment this creates 2 pipe
		// objects when loading from file
		for (Pipe pipe : getIncomingPipes()) {
			pipesToDelete.add(pipe);
		}

		for (Pipe pipe : pipesToDelete) {
			pipe.delete();
		}

		super.delete();
	}

	/**
	 * @param dataContainer
	 */
	public void publishToOutside(IDataContainer dataContainer) {

//		System.out.println("PUBLISH TO OUTSIDE");

		Collection<Pipe> pipes = getOutgoingPipes();
		int size = pipes.size();
		for (Pipe pipe : pipes) {

			Processable processable = pipe.getTarget();
			if (!processable.getParent().equals(this)) {

//				System.out.println("TARGET: " + processable.getClass().getSimpleName());
				
				IDataContainer container = dataContainer;
				if (size > 1) {
					container = dataContainer.getClone();
				}

				container = pipe.process(container);
				if (container == null) {
					continue;
				}

				attachVisitors(container);
				notifyVisitors(container);

				processable.process(container);
			}
		}
	}

	// public IDataContainer processOutgoing(IDataContainer dataContainer) {
	//		
	// Collection<Pipe> pipes = getIncomingPipes();
	// int size = pipes.size();
	// for (Pipe pipe : pipes) {
	// IDataContainer container = dataContainer;
	// if (size > 1) {
	// container = dataContainer.getClone();
	// }
	//
	// // container = pipe.process(container);
	// if (container == null) {
	// continue;
	// }
	//
	// // attachVisitors(container);
	// // notifyVisitors(container);
	//
	// Processable processable = pipe.getTarget();
	// System.out.println(pipe.getSource().getClass().getSimpleName() + " | " +
	// pipe.getTarget().getClass().getSimpleName());
	// if (!this.equals(processable)) {
	// processable.process(container);
	// }
	// }
	//		
	// return null;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.IProcessable#publish(org.squidy.manager
	 * .data.IData[])
	 */
	public void publish(IData... data) {
		throw new UnsupportedOperationException("Publishing data through pipeline is not allowed. Use publishToOutside instead.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.squidy.manager.IProcessable#publish(org.squidy.manager
	 * .data.IDataContainer)
	 */
	public void publish(IDataContainer dataContainer) {
		throw new UnsupportedOperationException("Publishing data through pipeline is not allowed. Use publishToOutside instead.");
	}

	// #############################################################################
	// END IProcessable
	// #############################################################################
}
