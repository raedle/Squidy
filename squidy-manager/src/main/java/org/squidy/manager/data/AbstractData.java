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

package org.squidy.manager.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.IProcessable;
import org.squidy.manager.util.CoreUtility;
import org.squidy.manager.util.TimeUtility;


/**
 * <code>AbstractData</code>.
 * 
 * <pre>
 * Date: Feb 13, 2008
 * Time: 6:47:28 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,,
 *         University of Konstanz
 * @version $Id: AbstractData.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 * 
 * TODO: [RR] serialize and deserialize attributes!!!
 */
public abstract class AbstractData<T extends AbstractData> implements IData<T> {

	private static final Log LOG = LogFactory.getLog(AbstractData.class);

	/**
	 * The default constructor is required to deserialize data types.
	 */
	public AbstractData() {
		// empty
	}

	// The attributes map allows to store any kind of attribute to this specific
	// data type.
	protected Map<DataConstant, Object> attributes;

	/**
	 * @param dataConstant
	 * @param value
	 */
	public void setAttribute(DataConstant dataConstant, Object value) {
		if (attributes == null) {
//			attributes = Collections.synchronizedMap(new HashMap<DataConstant, Object>());
			attributes = new ConcurrentHashMap<DataConstant, Object>();
		}

		// Security check.
		if (value == null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Trying to set attribute " + dataConstant + " on " + getClass().getSimpleName()
						+ " with null value.");
			}
			return;
		}

		// Security check that prevents values of unsupported type.
		if (!dataConstant.getType().isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Required type " + dataConstant.getType().getName()
					+ " of DataConstant doesn't match value type " + value.getClass().getName());
		}

		attributes.put(dataConstant, value);
	}

	/**
	 * @param dataConstant
	 * @return
	 */
	public Object getAttribute(DataConstant dataConstant) {
		if (attributes == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Attributes hash map is null.");
			}
			return null;
		}

		return attributes.get(dataConstant);
	}
	
	public boolean hasAttribute(DataConstant dataConstant) {
		if (attributes == null) {
			return false;
		}
		return attributes.containsKey(dataConstant);
	}
	
	protected T original;

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#getOriginal()
	 */
	public T getOriginal() {
		return original;
	}
	
	protected Collection<T> clones;
	
	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#getClones()
	 */
	public T[] getClones() {
		return (T[]) clones.toArray();
	}

	/**
	 * Adds a cloned data object to the original data object.
	 * 
	 * @param clone The cloned data object.
	 */
	protected void addClone(T clone) {
		if (clones == null)
			clones = Collections.synchronizedList(new ArrayList<T>());
		
		clones.add(clone);
	}
	
	protected boolean killed = false;
	
	/**
	 * Indicates whether this data object should be removed from processing
	 * or not.
	 * 
	 * @return Whether this data object was marked as "to be killed" for processing.
	 */
	public boolean isKilled() {
		return killed;
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#killAll()
	 */
	public void killAll() {
		if (this.original != null) {
			((AbstractData) this.original).killAll();
			return;
		}
		
		if (clones != null && clones.size() > 0)
			for (IData clone : clones) {
				((AbstractData) clone).killed = true;
			}
		
		killed = true;
	}

	// Date time format used in toString().
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss S a");

	protected long timestamp = 0;

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	// The processable class produced this data object.
	protected Class<? extends IProcessable<?>> source;

	/**
	 * @return the source
	 */
	public Class<? extends IProcessable<?>> getSource() {
		return source;
	}

	/**
	 * @param source
	 */
	public AbstractData(Class<? extends IProcessable<?>> source) {
		super();
		this.source = source;

		// Set timestamp of data instantiation.
		timestamp = System.currentTimeMillis();
	}

	// all visitors assigned to this object
	private Collection<IDataVisitor> visitors;

	/**
	 * @param visitor to add
	 * @return true if visitor has been added
	 */
	public boolean acceptVisitor(IDataVisitor visitor) {
		if (visitor == null) {
			return false;
		}
		if (visitors == null) {
			visitors = new ArrayList<IDataVisitor>();
		} else if (visitors.contains(visitor)) {
			// prevent duplicates
			return false;
		}
		return visitors.add(visitor);
	}

	/**
	 * @param visitor to remove
	 * @return true if visitor has been removed
	 */
	public boolean dismissVisitor(IDataVisitor visitor) {
		return (visitors != null) ? visitors.remove(visitor) : false;
	}

	/**
	 * notify all visitors that we are ready to receive their visit 
	 */
	public void notifyVisitors(IProcessable<?> processable) {
		if (visitors != null && processable != null) {
			for (IDataVisitor visitor : visitors) {
				visitor.visit(processable, this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.squidy.manager.data.IData#deserialize(java.lang.Object[])
	 */
	public void deserialize(Object[] serial) {
		source = ReflectionUtil.loadClass((String) serial[0]);
		timestamp = TimeUtility.getTimestamp((String) serial[1]);
		
		attributes = CoreUtility.getAttributesOfSerial((String) serial[2]);
//		visitors = CoreUtility.getVisitorsOfSerial((String) serial[3]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.squidy.manager.data.IData#serialize()
	 */
	public Object[] serialize() {
		
		String attributesSerial = CoreUtility.getSerialOfAttributes(attributes);
//		String visitorsSerial = CoreUtility.getSerialOfVisitors(visitors);

		return new Object[] { source.getName(), String.valueOf(timestamp), attributesSerial };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[class=").append(getClass().getSimpleName()).append("]");
		sb.append("[timestamp=").append(DATE_TIME_FORMAT.format(new Date(timestamp))).append("]");
		if (attributes != null) {
			sb.append("[attributes={");
			StringBuilder attributesBuilder = new StringBuilder();
			for (DataConstant constant : attributes.keySet()) {
				attributesBuilder.append(constant.getName()).append("=").append(attributes.get(constant)).append(", ");
			}
			sb.append(attributesBuilder.substring(0, attributesBuilder.length() - 2));
			sb.append("}]");
		}
		return sb.toString();
	}
}
