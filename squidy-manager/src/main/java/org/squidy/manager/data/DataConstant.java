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

import java.util.HashMap;
import java.util.Map;



/**
 * <code>DataConstant</code>.
 *
 * <pre>
 * Date: Jun 25, 2008
 * Time: 10:11:36 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>,
 *         University of Konstanz
 * @version $Id: DataConstant.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class DataConstant {
    
	public static final DataConstant IDENTIFIER = DataConstant.get(String.class, "IDENTIFIER");
	public static final DataConstant DEVICE_ID = DataConstant.get(Integer.class, "DEVICE_ID");
	public static final DataConstant GROUP_ID = DataConstant.get(Integer.class, "GROUP_ID");
	public static final DataConstant GROUP_DESCRIPTION = DataConstant.get(String.class, "GROUP_DESCRIPTION");
	public static final DataConstant FRAME_SEQUENCE_ID = DataConstant.get(Integer.class, "FRAME_SEQUENCE_ID");
	public static final DataConstant SESSION_ID = DataConstant.get(Integer.class, "SESSION_ID");
	public static final DataConstant RED_LED = DataConstant.get(Boolean.class, "RED_LED");
	public static final DataConstant GREEN_LED = DataConstant.get(Boolean.class, "GREEN_LED");
	public static final DataConstant TACTILE = DataConstant.get(Boolean.class, "TACTILE");
	public static final DataConstant TICK = DataConstant.get(Boolean.class, "TICK");
	public static final DataConstant CONTACT_STATE = DataConstant.get(String.class, "CONTACT_STATE"); //down/drag/up
	public static final DataConstant TIMEOUT = DataConstant.get(Boolean.class, "TIMEOUT");
	public static final DataConstant TARGETTYPE = DataConstant.get(String.class, "TARTETTYPE");
	
	//Laserpointer black
	public static final DataConstant LED_ID = DataConstant.get(Integer.class, "LED_ID");
	public static final DataConstant LED_COLOR = DataConstant.get(Byte.class, "LED_COLOR");
	
	// 3D 
	public static final DataConstant MAX_X = DataConstant.get(Double.class, "MAX_X");
	public static final DataConstant MAX_Y = DataConstant.get(Double.class, "MAX_Y");
	public static final DataConstant MAX_Z = DataConstant.get(Double.class, "MAX_Z");
	public static final DataConstant CenterOffset_X = DataConstant.get(Double.class, "CenterOffset_X");
	public static final DataConstant CenterOffset_Y = DataConstant.get(Double.class, "CenterOffset_Y");
	public static final DataConstant CenterOffset_Z = DataConstant.get(Double.class, "CenterOffset_Z");
	
	private static Map<String, DataConstant> DATA_CONSTANT_CACHE;
	
	
	
    private Class<?> type;
    private String name;
    
    /**
     * @param type
     * @param name
     */
    private DataConstant(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }
    
    /**
     * @param type
     * @param name
     * @return
     */
    public static DataConstant get(Class<?> type, String name) {
    	if (DATA_CONSTANT_CACHE == null) {
    		DATA_CONSTANT_CACHE = new HashMap<String, DataConstant>();
    	}
    	
    	if (DATA_CONSTANT_CACHE.containsKey(name)) {
    		DataConstant dataConstant = DATA_CONSTANT_CACHE.get(name);
    		if (dataConstant.getType().equals(type)) {
    			return dataConstant;
    		}
    	}
    	
    	DataConstant dataConstant = new DataConstant(type, name);
    	DATA_CONSTANT_CACHE.put(name, dataConstant);
    	
    	return dataConstant;
    }
    
    /**
     * @param type
     * @param namespace
     * @param name
     * @return
     */
    public static DataConstant get(Class<?> type, Class<?> namespace, String name) {
    	return new DataConstant(type, namespace.getSimpleName() + ":" + name);
    }
    
    /**
     * @return
     */
    public final Class<?> getType() {
    	return type;
    }

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DataConstant other = (DataConstant) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataConstant [name=" + name + ", type=" + type + "]";
	}
}
