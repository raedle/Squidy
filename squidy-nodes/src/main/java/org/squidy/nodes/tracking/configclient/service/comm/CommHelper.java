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

package org.squidy.nodes.tracking.configclient.service.comm;

import java.util.ArrayList;
import java.util.List;

import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.config.xml.Configuration;
import org.squidy.nodes.tracking.config.xml.Display;
import org.squidy.nodes.tracking.config.xml.Property;
import org.squidy.nodes.tracking.config.xml.PropertyContainer;


public class CommHelper {
	public static void clearMessages(Configuration config) {
		List<PropertyContainer> messages = getMessages(config);
		for (PropertyContainer container : messages) {
			container.setMsg(null);
			clearMessages(container.getProperties());
		}
	}
	
	public static void clearMessages(PropertyContainer p) {
		p.setMsg(null);
		clearMessages(p.getProperty());
	}
	
	public static void clearMessages(List<Property> properties) {
		for (Property property : properties) {
			property.setMsg(null);
		}
	}
	
	/**
	 * Collects all messages contained in the given configuration (currently considering
	 * camera and display level messages only).
	 * @param config
	 * @return A list of objects that implement the message interface
	 * (use getClass() to inspect those more closely).
	 */
	public static List<PropertyContainer> getMessages(Configuration config) {
		ArrayList<PropertyContainer> messages = new ArrayList<PropertyContainer>();
		List<Camera> cameras = config.getCamera();
		for (Camera camera : cameras) {
			if(camera.getMsg() != null) {
				messages.add(camera);
			}
		}
		List<Display> displays = config.getDisplay();
		for (Display display : displays) {
			if(display.getMsg() != null) {
				messages.add(display);
			}
		}
		return messages;
	}
	
	/**
	 * Collects all messages contained in the given property container.
	 * @param propertyContainer
	 * @return
	 */
	public static List<Property> getMessages(PropertyContainer propertyContainer) {
		ArrayList<Property> messages =  new ArrayList<Property>();
		for (Property p : propertyContainer.getProperty()) {
			if(p.getMsg() != null) messages.add(p);
		}
		return messages;
	}
	
	/**
	 * Clones ps using copy constructor of property.
	 * @param ps
	 * @return
	 */
	public static List<Property> cloneProperties(List<Property> ps) {
		List<Property> properties = new ArrayList<Property>();
		for (Property p : ps) {
			properties.add(new Property(p));
		}
		return properties;
	}

	public static void writePropertyToConfig(Property p, PropertyContainer container) {
		p.setMsg(Protocol.UPDATE);
		Property property =  container.getPropertyHashtable().get(p.getName());
		if(property != null) property.update(p);
		else {
			container.getProperty().add(p);
			container.updatePropertyHashtable();
		}
	}
	
	public static Property readPropertyFromConfig(String name, PropertyContainer container) {
		Property property =  container.getPropertyHashtable().get(name);
		return property;
	}
	
	public static Property createEmptyPropertyInConfig(String name, String type, PropertyContainer container) {
		Property property =  container.getPropertyHashtable().get(name);
		if(property == null) {
			property = new Property(name,"");
			property.setType(type);
		}
		return property;
	}

	/**
	 * @deprecated
	 */
	public static Property readcreatePropertyFromConfig(String name, String type, PropertyContainer container) {
		Property property =  container.getPropertyHashtable().get(name);
		if(property == null) {
			property = new Property(name,"");
			property.setType(type);
		}
		return property;
	}
	


	
}
