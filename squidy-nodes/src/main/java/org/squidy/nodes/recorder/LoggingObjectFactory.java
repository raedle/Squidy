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

package org.squidy.nodes.recorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DefaultDataContainer;


public class LoggingObjectFactory {
	private static LoggingObjectFactory uniqueInstance;
	
	private String valueSeparator;
	private String objectSeparator;
	
	private LoggingObjectFactory() {
		
	}
	
	public String getValueSeparator() {
		return valueSeparator;
	}

	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	public String getObjectSeparator() {
		return objectSeparator;
	}

	public void setObjectSeparator(String objectSeparator) {
		this.objectSeparator = objectSeparator;
	}
	
	public static LoggingObjectFactory getInstance() {
		if(uniqueInstance == null){
			uniqueInstance = new LoggingObjectFactory();
		}
		return uniqueInstance;
	}
	
	public LoggingObject getLoggingObject() {
		return new LoggingObject();
	}
	
	public LoggingObject getLoggingObject(long timestamp, IDataContainer data, int type) {
		return new LoggingObject(timestamp, data, type);
	}
	
	public class LoggingObject {
		
		public final static int TYPE_NULL = 0;
		public final static int TYPE_DATA = 1;
		public final static int TYPE_PAUSE = 2;
		
		private long timestamp;
		private IDataContainer container;
		private int type;
		
		public LoggingObject() {
			
		}
		
		public LoggingObject(long timestamp, IDataContainer data, int type) {
			if(type < 0 || type > 2) {
				type = TYPE_NULL;
			}
			
			if (data == null && type == TYPE_DATA) {
				type = TYPE_NULL;
			}
			
			this.timestamp = timestamp;
			this.container = data;
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
	
		public long getTimestamp() {
			return timestamp;
		}
	
		public void setDataContainer(IDataContainer data) {
			this.container = data;
			if(data == null)
				this.type = TYPE_NULL;
		}
	
		public IDataContainer getDataContainer() {
			return container;
		}
		
		public byte[] serialize() {
			StringBuilder sb = new StringBuilder();
			sb.append(timestamp).append(objectSeparator).append(type).append(objectSeparator);
			
			if(type != TYPE_DATA) {
				sb.delete(sb.length()-(objectSeparator.length()), sb.length());
				sb.append(System.getProperty("line.separator"));
				return sb.toString().getBytes();
			}
			for (IData i : container.getData()) {
				sb.append("s").append(valueSeparator).append(i.getClass().getName()).append(valueSeparator);
				for(Object o : i.serialize() ) {
					String type = "u"; //unsupported type
					if(o.getClass().equals(String.class)) {
						type = "s";
					}
					else if(o.getClass().equals(Integer.class)) {
						type = "i";
					}
					else if(o.getClass().equals(Float.class)) {
						type = "f";
					}
					else if(o.getClass().equals(Long.class)) {
						type = "l";
					}
					else if(o.getClass().equals(Double.class)) {
						type = "d";
					}
					else if(o.getClass().equals(Boolean.class)) {
						type = "b";
					}
					sb.append(type).append(valueSeparator);
					sb.append(o.toString()).append(valueSeparator);
				}
				sb.delete(sb.length()-valueSeparator.length(), sb.length());
				sb.append(objectSeparator);
			}
			sb.delete(sb.length()-(objectSeparator.length()), sb.length());
			sb.append(System.getProperty("line.separator"));
			return sb.toString().getBytes();
		}
		
		public void deserialize(String logline) {
			this.container = null;
			
			String[] dataPackets = logline.split(objectSeparator);
			this.timestamp = (long) Long.parseLong(dataPackets[0]);
			
//			int startIdx = dataPackets.length - 1;
			this.type = (int) Integer.parseInt(dataPackets[1]);
			if(type == TYPE_NULL || type == TYPE_PAUSE) {
				//Nothing to do for pause or null objects, container = null
				return;
			}
			
			this.container = new DefaultDataContainer();
			List<IData> dataList = new ArrayList<IData>(1);
			
			for (int i=2; i < dataPackets.length; i++) {
				String[] data = dataPackets[i].split(valueSeparator);
				Vector<Object> serial = new Vector<Object>();
				String type = "";
				for(int j = 0; j < data.length; j++) {
					if(type == "") {
						type = new String(data[j]);
					}
					else {
						Object o = null;
						if(type.equals("s")) {
							o = new String(data[j]);
						}
						else if (type.equals("i")) {
							o = Integer.parseInt(data[j]);
						}
						else if (type.equals("f")) {
							o = Float.parseFloat(data[j]);
						}
						else if (type.equals("l")) {
							o = Long.parseLong(data[j]);
						}
						else if (type.equals("d")) {
							o = Double.parseDouble(data[j]);
						}
						else if (type.equals("b")) {
							o = Boolean.parseBoolean(data[j]);
						}
						if(!type.equals("u")) {
							serial.add(o);
						}
						type = "";
					}
				}
				Object[] arguments = serial.toArray();
				IData idata = ReflectionUtil.createInstance((String) arguments[0]);
				Object[] rawData = new Object[arguments.length - 1];
				System.arraycopy(arguments, 1, rawData, 0, rawData.length);
				idata.deserialize(rawData);
				dataList.add(idata);
			}
			container.setData(dataList.toArray(new IData[dataList.size()]));
		}
	}
}
