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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.SquidyException;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.AbstractData;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.impl.DefaultDataContainer;


/**
 * <code>AbstractValve</code>.
 * 
 * <pre>
 * Date: Feb 22, 2009
 * Time: 11:54:49 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz
 *         .de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id: AbstractNode.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class AbstractNode extends Node {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(AbstractNode.class);

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	// @XmlAttribute(name = "active")
	// @Property(
	// name = "Active",
	// group = "Activation",
	// description =
	// "Whether the processable is active or not. If it's not active the data points will be send through the processable without any process execution."
	// )
	// @CheckBox
	// protected boolean active = true;
	//
	// /**
	// * @return the active
	// */
	// public final boolean isActive() {
	// return active;
	// }
	//
	// /**
	// * @param active
	// * the active to set
	// */
	// public final void setActive(boolean active) {
	// this.active = active;
	// }

	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private final Map<Class<? extends IData>, Method> PROCESS_CACHE = new HashMap<Class<? extends IData>, Method>();

	/* For autonumbering anonymous threads. */
	private static int threadInitNumber;

	private static synchronized int nextThreadNum() {
		return threadInitNumber++;
	}

	// Allows processable to wait for data and notify if data has been sent.
	private Object lock = new Object();

	// The process thread.
	private Thread processingThread;

//	private final ConcurrentLinkedQueue<IDataContainer> dataQueue = new ConcurrentLinkedQueue<IDataContainer>();
	private final Queue<IDataContainer> dataQueue = new LinkedList<IDataContainer>();

	public final Queue<IDataContainer> getDataQueue() {
		return dataQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.Processable#start()
	 */
	public final void start() throws ProcessException {

		// If it is already processing just return.
		if (isProcessing()) {
			return;
		}

		super.start();

		try {
			onStart();
		} catch (Throwable e) {

			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}

			publishFailure(e);
			processing = false;
			return;
		}

		resolveFailure();

		processingThread = new Thread(new Runnable() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				while (processing) {
					try {
						synchronized (lock) {
							while (processing && dataQueue.isEmpty()) {
								lock.wait();
							}
						
							if (processing) {
	
								IDataContainer dataContainer;
		//						while (!dataQueue.isEmpty()) {
									dataContainer = dataQueue.poll();
									
									if (dataContainer == null) {
										throw new SquidyException("Empty data container has been queued");
									}
									
									dataContainer = preProcess(dataContainer);
		
									if (dataContainer != null) {
										dataContainer = processInternal(dataContainer);
									}
		
									if (dataContainer != null) {
										dataContainer = postProcess(dataContainer);
									}
		
									if (dataContainer != null) {
										publish(dataContainer);
									}
									
		//							synchronized (lock) {
		//								lock.notify();
		//							}
							}
						}
					} catch (InterruptedException e) {
						if (processing) {
							throw new ProcessException(e.getMessage(), e);
						}
					}
				}
			}
		}, getId() + "_" + nextThreadNum());
		processingThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.model.Processable#stop()
	 */
	public final void stop() throws ProcessException {

		// If it is not processing just return.
		if (!processing) {
			return;
		}

		super.stop();

		try {
			onStop();
		} catch (Exception e) {
			publishFailure(e);
			// throw new ProcessException(e.getMessage(), e);
		} finally {
			synchronized (lock) {
				// Empty the data queue.
				dataQueue.clear();
				
				lock.notify();
			}
			
			if (processingThread != null)
				try {
					processingThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.model.Processable#delete()
	 */
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
	 * 
	 */
	public void onStart() {
		// empty
	}

	/**
	 * 
	 */
	public void onStop() {
		// empty
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.IProcessable#process(org.squidy.manager.data.IDataContainer)
	 */
	public final IDataContainer process(IDataContainer dataContainer) {

		if (!checkProcessingPrivileges(dataContainer)) {
			return null;
		}
		
		if (!processing) {
			publish(dataContainer);
			return null;
		}
		
		// Add data container to processing queue.
//		dataQueue.add(dataContainer);

		synchronized (lock) {
			
//			// Locking of the data queue is not necessary because the queue is already thread-safe.
			// Add data container to processing queue.
			dataQueue.add(dataContainer);
			
			// Notify the processing thread.
			lock.notify();
			
//			try {
//				lock.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
			
		return null;
	}
	
	/**
	 * @param dataContainer
	 * @return
	 */
	public boolean checkProcessingPrivileges(IDataContainer dataContainer) {
		return true;
	}

	/**
	 * @param dataContainer
	 * @return
	 */
	private IDataContainer processInternal(IDataContainer dataContainer)
			throws ProcessException {

		IData[] datas = dataContainer.getData();

		List<IData> processedData = new ArrayList<IData>(datas.length);

		for (IData data : datas) {
			// Do no process killed data objects.
			if (((AbstractData) data).isKilled())
				continue;
			
			Method method = getProcessMethod(data.getClass());

			// assert method != null : "Could not process data type " +
			// data.getClass().getName() + " for class " + me.getName();
			if (method == null) {
				// TODO [RR]: Comment me if context filtering is not possible!!!
				processedData.add(data);
				continue;
			}

			// Process data object.
			try {
				Object o = method.invoke(this, data);

				if (o != null) {
					// TODO [RR]: Bubble this exception for usage at frontend.
					try {
						processedData.add((IData) o);
					} catch (ProcessException e) {
						if (LOG.isErrorEnabled()) {
							LOG.error(e.getMessage(), e);
						}
					}
				}
			} catch (IllegalArgumentException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			} catch (IllegalAccessException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			} catch (InvocationTargetException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

		if (processedData.size() <= 0) {
			return null;
		}

		// Set new data to keep data container's meta information.
		dataContainer.setData(processedData.toArray(new IData[processedData.size()]));

		return dataContainer;
	}

	/**
	 * @param dataContainer
	 * @return
	 */
	public IDataContainer preProcess(final IDataContainer dataContainer) {
		return dataContainer;
	}

	/**
	 * @param dataContainer
	 * @return
	 */
	public IDataContainer postProcess(final IDataContainer dataContainer) {
		return dataContainer;
	}

	/**
	 * Publishes a data container to all outgoing connected processables.
	 * 
	 * @param dataContainer
	 *            The data container that gets published.
	 */
	public final void publish(final IDataContainer dataContainer) {
		Collection<Pipe> pipes = getOutgoingPipes();
		int size = pipes.size();
		for (Pipe pipe : pipes) {

			IDataContainer container = dataContainer;
			if (size > 1) {
				container = dataContainer.getClone();
			}

			container = pipe.process(container);
			if (container == null) {
				continue;
			}

//			attachVisitors(container);
//			notifyVisitors(container);

			Processable processable = pipe.getTarget();
			// A processable is inside of a pipeline.
			if (processable instanceof Pipeline && processable.equals(pipe.getSource().getParent())) {
				((Pipeline) processable).publishToOutside(container);
			}
			else {
				processable.process(container);
			}
		}
	}

	/**
	 * Publishes data objects to all outgoing connected processables.
	 * 
	 * @param data
	 *            The data objects that get published.
	 * @see ReflectionProcessable#publish(IDataContainer)
	 */
	public final void publish(final IData... data) {
		if (data == null || data.length < 1) {
			throw new ProcessException(
					"Publishing an empty collection of data.");
		}

		publish(new DefaultDataContainer(data));
	}

	/**
	 * Publishes data objects to all outgoing connected processables.
	 * 
	 * @param data
	 *            The data objects that get published.
	 * @see ReflectionProcessable#publish(IData...)
	 */
	public final void publish(Collection<? extends IData> data) {
		publish(new DefaultDataContainer(data.toArray(new IData[data.size()])));
	}

	/**
	 * @param type
	 * @return
	 */
	private Method getProcessMethod(Class<? extends IData> type) {

		// Return cached method if available.
		if (PROCESS_CACHE.containsKey(type)) {
			return PROCESS_CACHE.get(type);
		}

		Class<?> me = getClass();

		Method method = null;
		while (method == null && !AbstractNode.class.equals(me)) {
			Class<?> dataType = type;

			do {
				try {
					method = me.getDeclaredMethod("process", dataType);
				} catch (SecurityException e) {
					// do nothing
				} catch (NoSuchMethodException e) {
					// do nothing
				}

				if (method != null) {
					// Add method to cache to avoid further re-runs for this
					// data type.
					PROCESS_CACHE.put(type, method);
					return method;
				}
				dataType = dataType.getSuperclass();

				// Hierarchical upper data class (interface).
				if (dataType == null) {
					break;
				}

				// Set base type if abstract data class has been reached.
				if (dataType.isAssignableFrom(AbstractData.class)) {
					dataType = IData.class;
				}
			} while (method == null);

			me = me.getSuperclass();
		}

		// Add empty method to cache to avoid further re-runs for this data
		// type.
		PROCESS_CACHE.put(type, null);
		return null;
	}
}
