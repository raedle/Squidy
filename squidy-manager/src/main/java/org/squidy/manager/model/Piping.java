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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlIDREF;

import org.squidy.SquidyException;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.IDataVisitorFactory;


/**
 * <code>Piping</code>.
 * 
 * <pre>
 * Date: Feb 23, 2009
 * Time: 12:09:07 AM
 * </pre>
 * 
 * @author
 * Roman R&amp;aumldle<br />
 * <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a><br />
 * Human-Computer Interaction Group<br />
 * University of Konstanz
 * 
 * @version $Id: Piping.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public abstract class Piping extends Processable {

	// #############################################################################
	// BEGIN JAXB
	// #############################################################################
	
	/**
	 * Default constructor required for JAXB.
	 */
	public Piping() {

	}
	
	@XmlElement(name = "pipe")
	@XmlElementWrapper(name = "pipes")
	private Collection<Pipe> pipes = new ArrayList<Pipe>();
	
	/**
	 * @return the pipes
	 */
	public final Collection<Pipe> getPipes() {
		return pipes;
	}

	/**
	 * @param pipes the pipes to set
	 */
	public final void setPipes(Collection<Pipe> pipes) {
		this.pipes = pipes;
	}

	@XmlIDREF
	@XmlElementWrapper(name = "outgoing-pipes")
	private Collection<Pipe> outgoingPipes = new ArrayList<Pipe>();

	/**
	 * @return the outgoingPipes
	 */
	public final Collection<Pipe> getOutgoingPipes() {
		return outgoingPipes;
	}

	/**
	 * @param outgoingPipes the outgoingPipes to set
	 */
	public final void setOutgoingPipes(Collection<Pipe> outgoingPipes) {
		this.outgoingPipes = outgoingPipes;
	}
	
	@XmlIDREF
	@XmlElementWrapper(name = "incoming-pipes")
	private Collection<Pipe> incomingPipes = new ArrayList<Pipe>();
	
	/**
	 * @return the incomingPipes
	 */
	public final Collection<Pipe> getIncomingPipes() {
		return incomingPipes;
	}

	/**
	 * @param incomingPipes the incomingPipes to set
	 */
	public final void setIncomingPipes(Collection<Pipe> incomingPipes) {
		this.incomingPipes = incomingPipes;
	}
	
	/**
	 * Adds a pipe to the pipes collection.
	 * @param pipe A pipe.
	 */
	private final void addPipe(Pipe pipe) {
		pipes.add(pipe);
		fireStatusChange(STATUS_PROCESSABLE_ADDED, null, pipe);
	}
	
	/**
	 * Removes a pipe from the pipes collection.
	 * @param pipe A pipe.
	 */
	private final void removePipe(Pipe pipe) {
		pipes.remove(pipe);
		fireStatusChange(STATUS_PROCESSABLE_DELETED, pipe, null);
	}
	
	/**
	 * @param pipe
	 */
	public final void addOutgoingPipe(Pipe pipe) {
		if (outgoingPipes.contains(pipe)) {
			throw new SquidyException("Pipe already exists.");
		}
		
		addPipe(pipe);
		outgoingPipes.add(pipe);
	}
	
	/**
	 * @param pipe
	 */
	public final void removeOutgoingPipe(Pipe pipe) {
		removePipe(pipe);
		outgoingPipes.remove(pipe);
	}
	
	/**
	 * @param pipe
	 */
	public final void addIncomingPipe(Pipe pipe) {
		// TODO [RR]: Fix me - if uncomment this creates 2 pipe objects when loading from file
//		pipes.add(pipe);
		incomingPipes.add(pipe);
	}
	
	/**
	 * @param pipe
	 */
	public final void removeIncomingPipe(Pipe pipe) {
		// TODO [RR]: Fix me - if uncomment this creates 2 pipe objects when loading from file
//		pipes.remove(pipe);
		incomingPipes.remove(pipe);
	}
	
	// #############################################################################
	// END JAXB
	// #############################################################################

	private Collection<IDataVisitorFactory> visitorFactories;
	
	/**
	 * @param visitor factory to add
	 * @return true if visitor factory has been added
	 */
	public boolean addDataVisitorFactory(IDataVisitorFactory factory) {
		if (factory == null) {
			return false;
		}
		if (visitorFactories == null) {
			visitorFactories = new ArrayList<IDataVisitorFactory>();
		} else if (visitorFactories.contains(factory)) {
			// prevent duplicates
			return false;
		}
		return visitorFactories.add(factory);
	}

	/**
	 * @param visitor factory to remove
	 * @return true if visitor factory has been removed
	 */
	public boolean removeDataVisitorFactory(IDataVisitorFactory factory) {
		return (visitorFactories != null) ? visitorFactories.remove(factory) : false;
	}
	

	/**
	 * create new visitors and attach them to all data objects 
	 * @param container of data objects
	 */
	protected void attachVisitors(IDataContainer container) {
		if (visitorFactories != null && container != null) {
			for (IDataVisitorFactory factory : visitorFactories) {
				for (IData data : container.getData()) {
					data.acceptVisitor(factory.createDataVisitor());
				}
			}
		}
	}

	/**
	 * notify all visitors that we are ready to receive their visit 
	 * @param container of data objects
	 */
	protected void notifyVisitors(IDataContainer container) {
		if (container != null) {
			for (IData data : container.getData()) {
				data.notifyVisitors(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Piping=[");
		sb.append("outgoingPipes=[").append(outgoingPipes).append("]");
		sb.append("incomingPipes=[").append(incomingPipes).append("]");
		sb.append("]");
		
		return sb.toString();
	}
}
